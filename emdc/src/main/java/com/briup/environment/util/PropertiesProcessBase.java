package com.briup.environment.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesProcessBase {
    private static String propertiesFileName;   //配置文件名
    private static Properties properties;       //配置文件解析对象

    /**
     * 获取key对应的value值
     * @param key 键
     * @param path 配置文件的路径
     * @return value值
     */
    public static Object getValue(String path, String key){
        propertiesFileName = path;
        loadPropertiewFile();
        return properties.getProperty(key);
    }

    /**
     * 加载配置文件
     */
    private static void loadPropertiewFile(){
        properties = new Properties();
        if (isPropertiesFileExist()) {
            try {
                properties.load(propertiesFile());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("配置文件读取失败");
            }
        }
    }

    /**
     * 获取配置文件的输入流
     * @return 配置文件的输入流
     */
    private static FileInputStream propertiesFile(){
        try {
            return new FileInputStream(propertiesFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("配置文件不存在");
        }
        return null;
    }

    /**
     * 判断配置文件是否存在
     * @return true - 存在  false - 不存在
     */
    private static boolean isPropertiesFileExist(){
        return new File(propertiesFileName).exists();
    }

}
