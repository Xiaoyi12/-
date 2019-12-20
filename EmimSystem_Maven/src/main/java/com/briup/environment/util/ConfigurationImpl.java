package com.briup.environment.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.briup.environment.client.Client;
import com.briup.environment.client.Gather;
import com.briup.environment.server.DBStore;
import com.briup.environment.server.Server;

public class ConfigurationImpl implements Configuration {
	private static Map<String, Object> map = new HashMap<String, Object>();
	@SuppressWarnings("unused")
	private Properties properties = new Properties();

	public ConfigurationImpl() {
		map = Dom4j.dom4J("src\\main\\resources\\config.xml");
		properties = Dom4j.getProperties("src\\main\\resources\\config.xml");
		for (String str : map.keySet()) {
			try {
				/**
				 * 对配置中包含的配置信息依赖注入
				 */
				Object obj = map.get(str);
				if (obj instanceof WossModule) {
					WossModule wm = (WossModule) obj;
					wm.init(properties);
				}
				/**
				 * 对配置中包含的对象进行依赖注入
				 */
				if (obj instanceof ConfigurationAWare) {
					ConfigurationAWare ca = (ConfigurationAWare) obj;
					ca.setConfiguration(this);
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	@Override
	public Log getLogger() throws Exception {
		return (Log) map.get("logger");
	}

	@Override
	public Server getServer() throws Exception {

		return (Server) map.get("server");
	}

	@Override
	public Client getClient() throws Exception {

		return (Client) map.get("client");
	}

	@Override
	public DBStore getDbStore() throws Exception {

		return (DBStore) map.get("dbstore");
	}

	@Override
	public Gather getGather() throws Exception {

		return (Gather) map.get("gather");
	}

	@Override
	public Backup getBackup() throws Exception {

		return (Backup) map.get("backup");
	}

}
