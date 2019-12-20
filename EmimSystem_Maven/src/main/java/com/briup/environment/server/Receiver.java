package com.briup.environment.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

import com.briup.environment.bean.Environment;

/**
 * 
 * @ClassName: Receiver
 * @Description: TODO
 * @author: yu1556100622@163.com
 * @date: 2019年10月30日 下午3:45:11
 * 
 * @Copyright: 2019 www.briup.com All rights reserved.
 */
public class Receiver extends Thread {
	private Socket socket = null;
	private Collection<Environment> collection = null;
	private InputStream is = null;
	private ObjectInputStream ois = null;

	public Receiver() {
	}

	public Receiver(Socket socket) {
		this.socket = socket;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		collection = new ArrayList<Environment>();
		try {

			is = socket.getInputStream();
			ois = new ObjectInputStream(is);
			collection = (Collection<Environment>) ois.readObject();
			DBStore dbStore = new DBStoreImpl();
			dbStore.saveDb(collection);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (ois == null) {
				try {
					ois.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			if (is == null) {
				try {
					ois.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			
		}
		

	}

}
