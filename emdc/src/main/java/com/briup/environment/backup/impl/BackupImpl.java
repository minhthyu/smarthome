package com.briup.environment.backup.impl;

import com.briup.environment.backup.Backup;
import com.briup.environment.conf.Configuration;

import java.io.*;
import java.util.Properties;

public class BackupImpl implements Backup {
    private Configuration configuration = null;        //配置对象
    private String backBasePath = null;                //基本的备份路径
    @Override
    public void init(Properties properties) {
        this.backBasePath = properties.getProperty("path");
    }

    @Override
    public void backup(String fileName, Object data, boolean append) throws Exception {
        fileName = backBasePath + fileName;
        File file = new File(fileName);
        if (!file.exists()){
            file.createNewFile();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file, append);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(data);
        objectOutputStream.flush();
        objectOutputStream.close();
        fileOutputStream.close();
    }

    @Override
    public Object load(String fileName) throws Exception {
        fileName = backBasePath + fileName;
        File file = new File(fileName);
        if (file.exists()){
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Object o = objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            return o;
        }
        return null;
    }

    @Override
    public void deleteBackup(String fileName) {
        fileName = backBasePath + fileName;
        File file = new File(fileName);
        if (file.exists())
            file.delete();
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        try {
            this.configuration.getLogger().getLoggers(BackupImpl.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
