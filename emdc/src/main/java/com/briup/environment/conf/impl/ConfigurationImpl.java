package com.briup.environment.conf.impl;

import com.briup.environment.backup.Backup;
import com.briup.environment.client.Client;
import com.briup.environment.client.Gather;
import com.briup.environment.conf.Configuration;
import com.briup.environment.log.Log;
import com.briup.environment.server.DBStore;
import com.briup.environment.server.Server;
import com.briup.environment.util.WossModuleInit;
import com.briup.environment.util.XMLParse;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class ConfigurationImpl implements Configuration {
    private static ConfigurationImpl configuration;
    private Map<String, WossModuleInit> objectMap;
    private ConfigurationImpl() {
        try {
            objectMap = new HashMap<>();
            Map<String, Map<String, String>> parse = XMLParse.parse();

            Class<?> lClass = Class.forName(parse.get("logger").get("class"));
            WossModuleInit log = (WossModuleInit) objectInstantiate(lClass);


            Class<?> sClass = Class.forName(parse.get("server").get("class"));
            WossModuleInit server = (WossModuleInit) objectInstantiate(sClass);


            Class<?> cClass = Class.forName(parse.get("client").get("class"));
            WossModuleInit client = (WossModuleInit) objectInstantiate(cClass);


            Class<?> dClass = Class.forName(parse.get("dbstore").get("class"));
            WossModuleInit dbStore = (WossModuleInit) objectInstantiate(dClass);


            Class<?> gClass = Class.forName(parse.get("gather").get("class"));
            WossModuleInit gather = (WossModuleInit) objectInstantiate(gClass);

            Class<?> bClass = Class.forName(parse.get("backup").get("class"));
            WossModuleInit backup = (WossModuleInit) objectInstantiate(bClass);


            // init 传入配置信息
//            Properties logProperties = getProperties(parse, "logger");
            Properties logProperties = new Properties();
            for (Map.Entry<String, String> stringStringEntry : parse.get("logger").entrySet()) {
                logProperties.setProperty(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
            log.init(logProperties);


//            Properties serverProperties = getProperties(parse, "server");
            Properties serverProperties = new Properties();
            for (Map.Entry<String, String> stringStringEntry : parse.get("server").entrySet()) {
                serverProperties.setProperty(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
            server.init(serverProperties);


//            Properties clientProperties = getProperties(parse, "client");
            Properties clientProperties = new Properties();
            for (Map.Entry<String, String> stringStringEntry : parse.get("client").entrySet()) {
                clientProperties.setProperty(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
            client.init(clientProperties);


//            Properties dbStoreProperties = getProperties(parse, "dbStore");
            Properties dbStoreProperties = new Properties();
            for (Map.Entry<String, String> stringStringEntry : parse.get("dbstore").entrySet()) {
                dbStoreProperties.setProperty(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
            dbStore.init(dbStoreProperties);


//            Properties gatherProperties = getProperties(parse, "gather");
            Properties gatherProperties = new Properties();
            for (Map.Entry<String, String> stringStringEntry : parse.get("gather").entrySet()) {
                gatherProperties.setProperty(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
            gather.init(gatherProperties);


//            Properties backupProperties = getProperties(parse, "backup");
            Properties backupProperties = new Properties();
            for (Map.Entry<String, String> stringStringEntry : parse.get("backup").entrySet()) {
                backupProperties.setProperty(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
            backup.init(backupProperties);

            objectMap.put("log", log);
            objectMap.put("server", server);
            objectMap.put("client", client);
            objectMap.put("dbStore", dbStore);
            objectMap.put("gather", gather);
            objectMap.put("backup", backup);
        } catch (DocumentException e) {
            e.printStackTrace();
//            System.out.println("配置文件解析错误");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static ConfigurationImpl getInstance(){
        if (configuration==null)
            configuration = new ConfigurationImpl();
        return configuration;
    }
    @Override
    public Log getLogger() throws Exception {
        Log log = (Log) objectMap.get("log");
        log.setConfiguration(this);
        return log;
    }

    @Override
    public Server getServer() throws Exception {
        Server server = (Server) objectMap.get("server");
        server.setConfiguration(this);
        return server;
    }

    @Override
    public Client getClient() throws Exception {
        Client client = (Client) objectMap.get("client");
        client.setConfiguration(this);
        return client;
    }

    @Override
    public DBStore getDbStore() throws Exception {
        DBStore dbStore = (DBStore) objectMap.get("dbStore");
        dbStore.setConfiguration(this);
        return dbStore;
    }

    @Override
    public Gather getGather() throws Exception {
        Gather gather = (Gather) objectMap.get("gather");
        gather.setConfiguration(this);
        return gather;
    }

    @Override
    public Backup getBackup() throws Exception {
        Backup backup = (Backup) objectMap.get("backup");
        backup.setConfiguration(this);
        return backup;
    }

    /**
     * 通过反射实例化对象
     * @param aClass
     * @return
     * @throws Exception
     */
    private Object objectInstantiate(Class<?> aClass) throws Exception{
        Constructor<?> constructor = aClass.getConstructor();
        return constructor.newInstance();
    }

    /**
     * 获得对应对的配置文件(properties)对象
     * @param propertiesFileName 配置文件的文件名
     * @return properties 对象
     * @throws IOException
     */
    private Properties getProperties(String propertiesFileName) throws IOException {
        if (isPropertiesFileExist(propertiesFileName)){
            Properties properties = new Properties();
            properties.load(new FileReader(propertiesFileName));
            return properties;
        }
        return null;
    }

    /**
     * 判断配置文件是否存在
     * @return true - 存在  false - 不存在
     */
    private boolean isPropertiesFileExist(String propertiesFileName){
        return new File(propertiesFileName).exists();
    }

//    /**
//     * 获取各个模块对应的properties对象
//     * @param parse 解析后的XML数据的Map集合
//     * @param name 模块的名称
//     * @return
//     */
//    private Properties getProperties(Map<String, Map<String, String>> parse, String name) {
//        Properties properties = new Properties();
//        Map<String, String> stringStringMap = parse.get(name);
//        for (Map.Entry<String, String> stringStringEntry : stringStringMap.entrySet()) {
//            properties.setProperty(stringStringEntry.getKey(), stringStringEntry.getValue());
//        }
//        return properties;
//    }





//    public static void main(String[] args) throws Exception {
//        Configuration instance = ConfigurationImpl.getInstance();
//    }
}
