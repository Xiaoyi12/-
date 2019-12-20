package com.briup.environment.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Properties;

public class BackupImpl implements Backup{
	private String filePath ;
	private Configuration conf;

	@Override
	public void init(Properties properties) {
		filePath = properties.getProperty("filePath");
	}

	@Override
	public void setConfiguration(Configuration configuration) {
		conf = configuration;
	}

	@Override
	public void backup(String fileName, Object data) throws Exception {
		File file = new File(filePath, fileName);
		OutputStream out = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(data);
		if (oos != null) {
			oos.close();
		}
		if (out != null) {
			out.close();
		}
	}

	@Override
	public Object load(String fileName) throws Exception {
		File file = new File(filePath + fileName);
		Log log = conf.getLogger();
		if (!file.exists()) {
			log.warn("文件不存在");
			return null;
		}
		log.info("加载备份.........");
		InputStream is = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(is);
		Object object = ois.readObject();
		if (ois != null) {
			ois.close();
		}
		if (is != null) {
			is.close();
		}
		return object;
	}

	@Override
	public void deleteBackup(String fileName) {
		Log log;
		try {
			log = conf.getLogger();
			File file = new File(filePath + fileName);
			if (!file.exists()) {
				log.error("文件不存在");
			}
			file.delete();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
