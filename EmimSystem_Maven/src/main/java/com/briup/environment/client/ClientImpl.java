package com.briup.environment.client;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

import com.briup.environment.bean.Environment;

/**
 * 
 * @ClassName: ClientImpl
 * @Description: 使用socket编程，将客户端的信息传输到服务器端
 * @author: yu1556100622@163.com
 * @date: 2019年10月29日 下午3:29:27
 * 
 * @Copyright: 2019 www.briup.com All rights reserved.
 */
public class ClientImpl implements Client {
	/**
	 * IP地址serverAddress 本地IP地址 端口号port 6666 写出流的定义
	 */
	private Socket socket = null;
	private String serverAddress ;
	private int port ;

	@Override
	public void init(Properties properties) throws Exception {
		serverAddress = properties.getProperty("client-ip");
		port = Integer.valueOf(properties.getProperty("client-port"));
	}

	/**
	 * 
	 * <p>
	 * Title: send
	 * </p>
	 * <p>
	 * Description: 将collection集合中的environment对象读取出来， 并获取得到每个对象的具体内容
	 * 通过socket的写出流对象将具体内容写出
	 * </p>
	 *
	 * @param coll
	 * @throws Exception
	 */
	@Override
	public void send(Collection<Environment> coll) throws Exception {
		/**
		 * 变量声明
		 */
		socket = new Socket(serverAddress, port);
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		out.writeObject(coll);
		out.flush();
		
		if (out != null) {
			out.close();
		}
		if (socket != null) {
			socket.close();
		}

	}

}
