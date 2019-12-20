package com.briup.environment.main;

import com.briup.environment.server.Server;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.ConfigurationImpl;
import com.briup.environment.util.Log;

/**
 * 
 * @ClassName: ServerMain
 * @Description: 服务器端运行主程序
 * @author: yu1556100622@163.com
 * @date: 2019年10月29日 下午7:20:26
 * 
 * @Copyright: 2019 www.briup.com All rights reserved.
 */
public class ServerMain  {
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new ConfigurationImpl();
		Log log = conf.getLogger();
		log.info("服务器端运行：");
		Server server = conf.getServer();
		server.receiver();
	}

	
}
