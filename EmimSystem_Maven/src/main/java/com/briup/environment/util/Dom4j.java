package com.briup.environment.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.briup.environment.client.Client;
import com.briup.environment.client.Gather;
import com.briup.environment.server.DBStore;
import com.briup.environment.server.Server;

public class Dom4j {
	@SuppressWarnings({ "finally", "unchecked" })
	public static Map<String, Object> dom4J(String path) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(path));
			List<Object> list = getWosses(document);
			map = (Map<String, Object>) list.get(0);

		} catch (DocumentException e) {

			e.printStackTrace();
		} finally {
			return map;
		}

	}

	@SuppressWarnings("finally")
	public static Properties getProperties(String path) {
		SAXReader reader = new SAXReader();
		Document document;
		Properties properties = new Properties();
		try {
			document = reader.read(new File(path));
			List<Object> list = getWosses(document);
			properties = (Properties) list.get(1);
		} catch (DocumentException e) {

			e.printStackTrace();
		} finally {

			return properties;
		}

	}

	public static List<Object> getWosses(Document doc) {
		List<Object> list = new ArrayList<Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		Properties properties = new Properties();
		Element rootElement = doc.getRootElement();
		@SuppressWarnings("unchecked")
		List<Element> allChildElement = rootElement.elements();
		for (Element element : allChildElement) {
			String name = element.getName();
			if (name.equals("server")) {
				try {
					String classes = element.attributeValue("class");
					Element portElement = element.element("server-port");
					String port = portElement.getText();
					Class<?> class1 = Class.forName(classes);
					Server server = (Server) class1.newInstance();
					map.put(name, server);
					properties.setProperty("server-port", port);
				} catch (Exception e) {

					e.printStackTrace();
				}
			} else if (name.equals("client")) {
				try {
					String classes = element.attributeValue("class");
					Element ipElement = element.element("client-ip");
					Element portElement = element.element("client-port");
					String ip = ipElement.getText();
					String port = portElement.getText();
					Class<?> class1;
					class1 = Class.forName(classes);
					Client client = (Client) class1.newInstance();
					map.put(name, client);
					properties.setProperty("client-ip", ip);
					properties.setProperty("client-port", port);
				} catch (Exception e) {

					e.printStackTrace();
				}
			} else if (name.equals("dbstore")) {
				try {
					String classes = element.attributeValue("class");
					Element driverElement = element.element("driver");
					Element urlElement = element.element("url");
					Element userElement = element.element("user");
					Element passwordElement = element.element("password");
					Element backupFileElement = element.element("dbstore-backupFile");
					String driver = driverElement.getText();
					String url = urlElement.getText();
					String user = userElement.getText();
					String password = passwordElement.getText();
					String backupFile = backupFileElement.getText();
					Class<?> class1 = Class.forName(classes);
					DBStore dbStore = (DBStore) class1.newInstance();
					map.put(name, dbStore);
					properties.setProperty("driver", driver);
					properties.setProperty("url", url);
					properties.setProperty("user", user);
					properties.setProperty("password", password);
					properties.setProperty("dbstore-backupFile", backupFile);
				} catch (Exception e) {

					e.printStackTrace();
				}

			} else if (name.equals("gather")) {
				try {
					String classes = element.attributeValue("class");
					Element srcFileElement = element.element("src-file");
					Element backupFileElement = element.element("gather-backupFile");
					String srcFile = srcFileElement.getText();
					String backupFile = backupFileElement.getText();
					Class<?> class1;
					class1 = Class.forName(classes);
					Gather gather = (Gather) class1.newInstance();
					map.put(name, gather);
					properties.setProperty("src-file", srcFile);
					properties.setProperty("gather-backupFile", backupFile);
				} catch (Exception e) {

					e.printStackTrace();
				}

			} else if (name.equals("logger")) {
				try {
					String classes = element.attributeValue("class");
					Element logPropertiesElement = element.element("log-properties");
					String logProperties = logPropertiesElement.getText();
					Class<?> class1;
					class1 = Class.forName(classes);
					Log logger = (Log) class1.newInstance();
					map.put(name, logger);
					properties.setProperty("log-properties", logProperties);
				} catch (Exception e) {

					e.printStackTrace();
				}

			} else if (name.equals("backup")) {
				String classes = element.attributeValue("class");
				Element backupPathElement = element.element("filePath");
				String backupPath = backupPathElement.getText();
				Class<?> class1;
				try {
					class1 = Class.forName(classes);
					Backup backup = (Backup) class1.newInstance();
					map.put(name, backup);
				} catch (Exception e) {

					e.printStackTrace();
				}

				properties.setProperty("filePath", backupPath);
			}

		}
		list.add(map);
		list.add(properties);
		return list;
	}

}
