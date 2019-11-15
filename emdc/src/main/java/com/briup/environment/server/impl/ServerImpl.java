package com.briup.environment.server.impl;

import com.briup.environment.bean.Environment;
import com.briup.environment.conf.Configuration;
import com.briup.environment.server.DBStore;
import com.briup.environment.server.Server;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Properties;

public class ServerImpl implements Server {
    private Configuration configuration;          //配置对象
    private Logger logger;                        //日志对象
    private int port;                             //服务端口号
    private static boolean isStop = false;        //服务器shutdown标识
    private DBStore dbStore;                      //入库对象

    @Override
    public void reciver() throws Exception {
        ServerSocket socketServer = new ServerSocket(port);
        logger.info("服务器已启动");
        while (!isStop){
            Socket socket = socketServer.accept();
            new Thread(new ServerRunnable(socket)).start();
//            socket.close();
        }
        socketServer.close();
    }

    @Override
    public void shutdown() {
        isStop = true;
    }

    @Override
    public void init(Properties properties) throws Exception {
        this.port = Integer.parseInt(properties.getProperty("port"));
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        try {
            this.logger = configuration.getLogger().getLoggers(ServerImpl.class);
            this.dbStore = configuration.getDbStore();
        } catch (Exception e) {
        }
    }


    /***
     * 内部线程类
     */
    class ServerRunnable implements Runnable {
        private Socket socket;
        private ObjectInputStream objectInputStream = null;
        private Collection<Environment> environments;
        private PrintWriter printWriter = null;
        public ServerRunnable(Socket socket) throws SQLException, IOException {
            this.socket = socket;
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream());
//            dbStore = new DBStoreImpl();
        }

        public Collection<Environment> getEnvironments() {
            return environments;
        }

        @Override
        public void run() {
            try {
                environments = (Collection<Environment>) objectInputStream.readObject();
                printWriter.println("success");
                try {
                    dbStore.saveDb(environments);
                }catch (Exception e){
                }
            } catch (Exception e) {
//                e.printStackTrace();
                printWriter.println("error");
            } finally {
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

/*
class ServerRunnable implements Runnable {
        private Socket socket;
        private ObjectInputStream objectInputStream = null;
        private Collection<Environment> environments;
        private DBStore dbStore;
        public ServerRunnable(Socket socket) throws SQLException, IOException {
            this.socket = socket;
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            dbStore = new DBStoreImpl();
        }

        public Collection<Environment> getEnvironments() {
            return environments;
        }

        @Override
        public void run() {
            try {
                environments = (Collection<Environment>) objectInputStream.readObject();
                dbStore.saveDb(environments);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
 */