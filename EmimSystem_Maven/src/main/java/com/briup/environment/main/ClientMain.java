package com.briup.environment.main;

import java.util.Collection;
import com.briup.environment.bean.Environment;
import com.briup.environment.client.Client;
import com.briup.environment.client.Gather;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.ConfigurationImpl;
import com.briup.environment.util.Log;

/**
 * 
 * @ClassName: ClientMain
 * @Description: 客户端运行主程序
 * @author: yu1556100622@163.com
 * @date: 2019年10月29日 下午7:20:55
 * 
 * @Copyright: 2019 www.briup.com All rights reserved.
 */
public class ClientMain {
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new ConfigurationImpl();
		Log log = conf.getLogger();
		log.info("客户端运行中......");
		Client client = conf.getClient();
		Gather gather = conf.getGather();
		log.info("采集数据中");
		Collection<Environment> collection = gather.gather();
		log.info("采集到数据：" + collection.size());
		log.info("发送数据中");
		client.send(collection);
		log.info("发送数据成功");

	}

	
}
