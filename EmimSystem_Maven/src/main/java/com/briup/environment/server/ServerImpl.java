package com.briup.environment.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.Log;

/**
 * 
 * @ClassName: ServerImpl
 * @Description: 服务器端的实现类
 * @author: yu1556100622@163.com
 * @date: 2019年10月29日 下午7:33:38
 * 
 * @Copyright: 2019 www.briup.com All rights reserved.
 */
public class ServerImpl implements Server {
	/**
	 * 
	 * 变量定义
	 */
	private int port ;
	private Socket socket = null;
	private ServerSocket serverSocket = null;
	private ServerSocket shutdownServerSocket = null;
	private static boolean flag = true;
//	private Receiver receiver = null;
	private InputStream is = null;
	private ObjectInputStream ois = null;
	private Collection<Environment> collection = null;
	private Configuration conf;

	@Override
	public void init(Properties properties) throws Exception {
		port = Integer.valueOf(properties.getProperty("server-port"));
	}

	@Override
	public void setConfiguration(Configuration configuration) {
		conf = configuration;
	}

	/**
	 * 
	 * <p>
	 * Title: receiver
	 * </p>
	 * <p>
	 * Description: 读取客户端传输过来的内容，并进行封装，将其封装到environment对象中，并将该对象存储到collection集合中返回
	 * 
	 * </p>
	 *
	 * @return collection
	 * @throws Exception
	 */
	public ServerImpl() {
	}

	public ServerImpl(Socket socket) {
		this.socket = socket;
	}

	public void receiver() throws Exception {
		/**
		 * 变量声明
		 */
		
		serverSocket = new ServerSocket(port);
		Log log = conf.getLogger();
		new Thread() {
			public void run() {
				try {
					shutdownServerSocket = new ServerSocket(7777);
					log.info("启动监听服务器子线程，监听端口号：7777");
					shutdownServerSocket.accept();
					log.info("关闭服务器线程");
					flag = false;
					log.info("关闭服务器线程");
				} catch (IOException e) {

					e.printStackTrace();
				} finally {
					if (shutdownServerSocket == null) {
						try {
							log.info("关闭监听子线程");
							shutdownServerSocket.close();
						} catch (IOException e) {

							e.printStackTrace();
						}
					}
				}

			}
		}.start();
		/*
		 * while (flag) { socket = serverSocket.accept(); receiver = new
		 * Receiver(socket); receiver.start(); }
		 */
		ExecutorService pool = Executors.newFixedThreadPool(10);
		while (flag) {
			socket = serverSocket.accept();
			log.info("接收客户端连接........." + "ip:" + socket.getLocalAddress() + "port:" + socket.getPort());
			pool.submit(new Runnable() {
				@SuppressWarnings("unchecked")
				@Override
				public void run() {
					try {
						is = socket.getInputStream();
						ois = new ObjectInputStream(is);
						collection = (Collection<Environment>) ois.readObject();
						if (collection != null && collection.size() > 0) {
							DBStore dbStore = conf.getDbStore();
							dbStore.saveDb(collection);
						}
					} catch (Exception e) {

						e.printStackTrace();
					} finally {
						if (ois != null) {
							try {
								ois.close();
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
						if (socket != null) {
							try {
								ois.close();
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
					}

				}
			});
		}
		log.info("关闭线程");
		pool.shutdown();
	}

	
}
