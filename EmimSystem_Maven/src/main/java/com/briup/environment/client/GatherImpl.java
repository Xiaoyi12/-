package com.briup.environment.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.Backup;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.Log;

/**
 * 
 * @ClassName: GatherImpl
 * @Description: 实现Gather接口，重写里面的方法
 * @author: yu1556100622@163.com
 * @date: 2019年10月29日 上午11:39:59
 * 
 * @Copyright: 2019 www.briup.com All rights reserved.
 */
public class GatherImpl implements Gather {
	/**
	 * 文件路径声明 变量定义
	 */
	private String filePath ;
	private String backUpFile ;
	private BufferedReader bufferedReader = null;
	String data = null;
	String[] split = null;
	Collection<Environment> collection = new HashSet<Environment>();;
	Environment environment1 = null;
	Environment environment2 = null;
	private FileInputStream fis = null;
	private Configuration conf;
	private Backup backUp;
	
	@Override
	public void init(Properties properties) throws Exception {
		filePath = (String) properties.get("src-file");
		backUpFile = properties.getProperty("gather-backupFile");
	}

	@Override
	public void setConfiguration(Configuration configuration) {
		conf = configuration;
	}
	/**
	 * 
	 * <p>
	 * Title: gather
	 * </p>
	 * <p>
	 * Description: 解析data_tem.txt本地文件， 读取本地文件中的每一行数据 将获取得到的数据根据其数据类型进行不同的处理
	 * 将处理得到的结果封装成一个environment对象 将environment对象存储到一个collection集合中返回
	 * </p>
	 * 
	 * @return collection
	 * @throws Exception
	 */
	@Override
	public Collection<Environment> gather() {
		/**
		 * 采集时的备份文件： 每隔一段时间进行一次采集，所以需要得到每次采集的字节数，并将其存储到length.txt
		 * 文件中，换行占两个字节，在下一次采集的时候，先得到上次的字节数，再进行采集 先读取备份文件 已经采集过的长度值【字节】 88字节
		 * 如果文件不存在，直接往下操作 如果文件存在，获取已经读取过的文件字节数
		 * 
		 * 调整文件偏移量，然后逐行读取文件 88+2
		 * 
		 * 采集完成，将最新读取文件长度写入备份文件 256
		 */
		try {
			Log log =conf.getLogger();
			backUp = conf.getBackup();
			int len = 0;
			if (backUp.load(backUpFile) == null || backUpFile.length() == 0) {
				len = 0;
			} else {
				len = (int) backUp.load(backUpFile);
			}
			fis = new FileInputStream(filePath);
			int len1 = fis.available();
			fis.skip(len + 2);
			bufferedReader = new BufferedReader(new InputStreamReader(fis));
			backUp.backup("length.bak", len1);
			while ((data = bufferedReader.readLine()) != null) {
				split = data.split("[|]");
				if (split.length != 9) {
					log.warn("数据异常....");
					continue;
				}
				Long time = Long.parseLong(split[8]);
				if ("16".equals(split[3])) {
					String value1 = Integer.valueOf(split[6].substring(0, 4), 16).toString();
					String value2 = Integer.valueOf(split[6].substring(4, 8), 16).toString();
					float temperature = (float) (Integer.valueOf(value1) * 0.00268127 - 46.85);
					float humidity = (float) (Integer.valueOf(value2) * 0.00190735 - 6);
					/**
					 * 将温度信息封装成environment1对象
					 */
					environment1 = new Environment();
					environment1.setName("温度");
					environment1.setSrcId(split[0]);
					environment1.setDstId(split[1]);
					environment1.setDevId(split[2]);
					environment1.setCount(Integer.valueOf(split[4]));
					environment1.setCmd(split[5]);
					environment1.setData(temperature);
					environment1.setSersorAddress("16");
					environment1.setStatus(Integer.valueOf(split[7]));
					environment1.setGather_date(new Timestamp(time));
					collection.add(environment1);
					/**
					 * 将湿度信息封装成environment2对象
					 */
					environment2 = new Environment();
					environment2.setName("湿度");
					environment2.setSrcId(split[0]);
					environment2.setDstId(split[1]);
					environment2.setDevId(split[2]);
					environment2.setCount(Integer.valueOf(split[4]));
					environment2.setCmd(split[5]);
					environment2.setData(humidity);
					environment2.setSersorAddress("16");
					environment2.setStatus(Integer.valueOf(split[7]));
					environment2.setGather_date(new Timestamp(time));
					collection.add(environment2);
				} else if ("256".equals(split[3])) {
					/**
					 * 将CO2信息封装成environment2对象
					 */
					environment1 = new Environment();
					String value = Integer.valueOf(split[6].substring(0, 4), 16).toString();
					environment1.setName("CO2");
					environment1.setSrcId(split[0]);
					environment1.setDstId(split[1]);
					environment1.setDevId(split[2]);
					environment1.setCount(Integer.valueOf(split[4]));
					environment1.setCmd(split[5]);
					environment1.setData(Integer.valueOf(value));
					environment1.setSersorAddress("256");
					environment1.setStatus(Integer.valueOf(split[7]));
					environment1.setGather_date(new Timestamp(time));
					collection.add(environment1);
				} else if ("1280".equals(split[3])) {
					/**
					 * 将光照强度信息封装成environment2对象
					 */
					environment1 = new Environment();
					String value = Integer.valueOf(split[6].substring(0, 4), 16).toString();
					environment1.setName("光照强度");
					environment1.setSrcId(split[0]);
					environment1.setDstId(split[1]);
					environment1.setDevId(split[2]);
					environment1.setCount(Integer.valueOf(split[4]));
					environment1.setCmd(split[5]);
					environment1.setData(Integer.valueOf(value));
					environment1.setSersorAddress("1280");
					environment1.setStatus(Integer.valueOf(split[7]));
					environment1.setGather_date(new Timestamp(time));
					collection.add(environment1);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();

		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		return collection;
	}

	

}
