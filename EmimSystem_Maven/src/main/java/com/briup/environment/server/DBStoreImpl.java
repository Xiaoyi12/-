package com.briup.environment.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.Backup;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.Log;

/**
 * 
 * @ClassName: DBStoreImpl
 * @Description: TODO
 * @author: yu1556100622@163.com
 * @date: 2019年10月31日 上午10:22:03
 * 
 * @Copyright: 2019 www.briup.com All rights reserved.
 */
public class DBStoreImpl implements DBStore {

	private String driver = null;
	private String url = null;
	private String user = null;
	private String password = null;
	private Connection connection = null;
	private PreparedStatement prepareStatement = null;
	private static int preday = -1;
	private String backupFile ;
	private Collection<Environment> collection;
	private static int sum = 0;
	private Configuration conf;
	@Override
	public void init(Properties properties) throws Exception {
		backupFile = properties.getProperty("dbstore-backupFile");
		driver = properties.getProperty("driver");
		url = properties.getProperty("url");
		user = properties.getProperty("user");
		password = properties.getProperty("password");
	}

	@Override
	public void setConfiguration(Configuration configuration) {
		conf = configuration;
	}
	/**
	 * 
	 * <p>
	 * Title: saveDb
	 * </p>
	 * <p>
	 * Description:获取数据库连接对象，将collection集合中的environment对象存储到数据库中
	 * </p>
	 *
	 * @param coll
	 * @throws Exception
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void saveDb(Collection<Environment> coll) {
		try {
			
			/**
			 * 加载配置文件、注册驱动
			 */
			Log log = conf.getLogger();
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
			connection.setAutoCommit(false);
			Backup backup = conf.getBackup();
			/**
			 * 找db.bak是否存在， 如果存在，先提取db.bak中集合数据 coll<size-sum> 然后delete
			 * 
			 * coll<new> 两个合并成一个coll<size-sum,new>， 下面代码不用动
			 */
			if (backup.load(backupFile) != null) {
				collection = new ArrayList<Environment>();
				collection = (Collection<Environment>) backup.load(backupFile);
				backup.deleteBackup(backupFile);
				coll.addAll(collection);
			}
			log.info("开始入库..............");
			Calendar calendar = Calendar.getInstance();
			int count = 0;
			for (Environment environment : coll) {
				count++;
				calendar.setTimeInMillis(environment.getGather_date().getTime());
				int day = calendar.get(Calendar.DATE);
				String sql = "INSERT INTO E_DETAIL_" + day + " VALUES(?,?,?,?,?,?,?,?,?,?)";
				if (day != preday) {
					if (preday != -1) {
						prepareStatement.executeBatch();
						sum = count;

						/**
						 * 模拟出现异常
						 */
						/*
						 * if (count == 2) { int num = 10 / 0; System.out.println(num); }
						 */

						log.debug("入库" + count + "条");
						connection.commit();
						count = 0;
					}
					prepareStatement = connection.prepareStatement(sql);
				}
				prepareStatement.setString(1, environment.getName());
				prepareStatement.setString(2, environment.getSrcId());
				prepareStatement.setString(3, environment.getDstId());
				prepareStatement.setString(4, environment.getDevId());
				prepareStatement.setString(5, environment.getSersorAddress());
				prepareStatement.setInt(6, environment.getCount());
				prepareStatement.setString(7, environment.getCmd());
				prepareStatement.setInt(8, environment.getStatus());
				prepareStatement.setFloat(9, environment.getData());
				prepareStatement.setTimestamp(10, environment.getGather_date());
				prepareStatement.addBatch();
				preday = day;
				if (count % 5 == 0) {
					prepareStatement.executeBatch();
					sum = count;
					log.debug("入库" + count + "条");
					connection.commit();
					count = 0;
				}
			}
			prepareStatement.executeBatch();
			sum = count;
			connection.commit();
			log.debug("入库" + count + "条");

		} catch (Exception e) {
			if (connection != null) {
				try {
					/**
					 * 入库时出现异常，事务回滚， 回滚到何处？ 上一次commit的地方
					 * 
					 * 定义计数器 记录 真正入库的数据条数 sum
					 * 
					 * 不论在何处出现异常，回滚代码块中 提取尚未入库的数据 coll<Enrironment>(size-sum) backUp("db.bak",coll);
					 */
					Backup backup = conf.getBackup();
					collection = new ArrayList<Environment>();
					/**
					 * 也可以借助于List的subList()方法 ArrayList<Environment> list =
					 * (ArrayList<Environment>)coll; list.subList(0, sum).clear();
					 * backup.backup(backUpFile, list);
					 */

					for (int i = sum; i < coll.size(); i++) {
						Iterator<Environment> iter = coll.iterator();
						while (iter.hasNext()) {
							Environment environment = iter.next();
							collection.add(environment);
						}
					}
					backup.backup(backupFile, collection);
					connection.rollback();
				} catch (Exception e1) {

					e1.printStackTrace();
				}
			}
			e.printStackTrace();
		}finally {
			if(prepareStatement != null) {
				try {
					prepareStatement.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
			}
			if(connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
			}
		}
	}

	
}
