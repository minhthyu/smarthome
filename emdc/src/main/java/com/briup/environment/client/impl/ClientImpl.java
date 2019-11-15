package com.briup.environment.client.impl;

import com.briup.environment.backup.Backup;
import com.briup.environment.bean.Environment;
import com.briup.environment.client.Client;
import com.briup.environment.conf.Configuration;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

public class ClientImpl implements Client {
    private Configuration configuration;  //配置对象
    private Logger logger;                //日志对象
    private Backup backup;                //备份对象
    private String ip;                    //客户端连接ip
    private int port;                     //客户端连接断开
    private String backupFilePath;        //客户端备份文件路径
    @Override
    public void send(Collection<Environment> coll) throws Exception {
//        String ip = "127.0.0.1";
//        int port = 9999;
        Socket socket  = new Socket(this.ip, this.port);
        /**
         * 读取备份文件，如果不为空，则将文件内容加入此次发送队列中
         */
        Object load = backup.load(backupFilePath);
        if (load != null){
            if (load instanceof Collection){
                for (Environment environment : ((Collection<Environment>) load)) {
                    coll.add(environment);
                }
            }
        }
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(coll);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String flag = bufferedReader.readLine();
        if ("error".equals(flag)) {
            backup.backup(backupFilePath, coll, false);
            logger.error("客户端传输数据到服务器端失败");
        }
//        objectOutputStream.close();
//        socket.close();
    }

    @Override
    public void init(Properties properties) throws Exception {
        this.ip = properties.getProperty("ip");
        this.port = Integer.parseInt(properties.getProperty("port"));
        this.backupFilePath = properties.getProperty("backupFile");
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        try {
            this.logger = configuration.getLogger().getLoggers(ClientImpl.class);
            this.backup = configuration.getBackup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
