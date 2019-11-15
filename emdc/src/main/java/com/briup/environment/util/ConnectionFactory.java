package com.briup.environment.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnectionFactory {
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	private static Properties properties = new Properties();
	private static Connection connection;
	static {
		File file = new File("emdc/src/main/java/jdbc.properties");
		try {
			InputStream is = new FileInputStream(file);
			//将需要读取的文件加载到properties对象中
			properties.load(is);
			driver = properties.getProperty("driver");
			url = properties.getProperty("url");
			username = properties.getProperty("username");
			password = properties.getProperty("password");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static Connection getConnection() {
		try {
			//注册驱动
			Class.forName(driver);
			//建立连接
			connection = DriverManager.getConnection
					(url,username,password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
}



