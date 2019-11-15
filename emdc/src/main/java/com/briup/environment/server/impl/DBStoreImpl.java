package com.briup.environment.server.impl;

import com.briup.environment.backup.Backup;
import com.briup.environment.bean.Environment;
import com.briup.environment.conf.Configuration;
import com.briup.environment.server.DBStore;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class DBStoreImpl implements DBStore {
    private Configuration configuration;           //配置对象
    private Logger logger;                         //日志对象
    private Backup backup;                         //备份对象
    private Collection<Environment> environments;  //需要备份的对象集合
    private Connection connection;                 //数据库连接对象
    private PreparedStatement preparedStatement;   //预处理对象
    private String backupFilePath;                 //备份文件路径
    private String sql;

//    static {
//        Properties properties = new Properties();
//        try {
//            properties.load(new FileReader("src/main/resources/jdbc.properties"));
//            DRIVERNAME = String.valueOf(properties.getProperty("driver"));
//            URL = String.valueOf(properties.getProperty("url"));
//            USERNAME = String.valueOf(properties.getProperty("username"));
//            PASSWORD = String.valueOf(properties.getProperty("password"));
//            Class.forName(DRIVERNAME);
//            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
//
//        } catch (IOException e) {
////            e.printStackTrace();
//            logger.error("properties文件路径有问题");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    public DBStoreImpl() throws SQLException {
        connection = DataSourceUtils.getConnection();
    }

    @Override
    public void saveDb(Collection<Environment> coll) throws Exception {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        int commitIndex = 0;
        int batchNum = 50000;
        sql = "insert into e_detail_? values(?,?,?,?,?,?,?,?,?)";
        /**
         * 读取备份文件，如果不为空，则将文件内容加入此次入库集合中
         */
        Object load = backup.load(backupFilePath);
        if (load != null){
            if (load instanceof Collection){
                for (Environment environment : ((Collection<Environment>) load)) {
                    coll.add(environment);
                }
            }
        }
        try {
            connection.setAutoCommit(false);
//        long l1 = System.currentTimeMillis();
            preparedStatement = connection.prepareStatement(sql);
            for (Environment environment : coll) {
                preparedStatement.setInt(1, environment.getGather_date().getDate());
                preparedStatement.setString(2, environment.getName());
                preparedStatement.setString(3, environment.getSrcId());
                preparedStatement.setString(4, environment.getDstId());
                preparedStatement.setString(5, environment.getSersorAddress());
                preparedStatement.setInt(6, environment.getCount());
                preparedStatement.setString(7, environment.getCmd());
                preparedStatement.setInt(8, environment.getStatus());
                preparedStatement.setFloat(9, environment.getData());
                preparedStatement.setDate(10, new Date(environment.getGather_date().getTime()));
                preparedStatement.addBatch();
                i++;
                if (i%batchNum == 0) {
                    preparedStatement.executeBatch();
                    connection.commit();
                    commitIndex+=batchNum;
                    preparedStatement.clearBatch();
                }
            }
            if (i%batchNum != 0) {
                preparedStatement.executeBatch();
                connection.commit();
                commitIndex+=i%batchNum;
                preparedStatement.clearBatch();
            }
        }catch (Exception e){
            /**
             * 入库失败是进行失败部分数据的备份，以备下次入库时使用
             */
            this.environments = new ArrayList<>();
            for (int index = commitIndex; index < coll.size(); index++){
                this.environments.add(((ArrayList<Environment>)coll).get(index));
                backup.backup(backupFilePath, this.environments, false);
            }
        }finally {
            // 关闭资源
            closeResource(preparedStatement, connection);
        }
        logger.info("总共存储的数据条数:" + (commitIndex));
        DataSourceUtils.close(connection, preparedStatement, null);
    }

    @Override
    public void init(Properties properties) throws Exception {
        this.backupFilePath = properties.getProperty("backupFile");
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        try {
            this.logger = configuration.getLogger().getLoggers(DBStoreImpl.class);
            this.backup = configuration.getBackup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭所有资源
     */
    private void closeResource(PreparedStatement preparedStatement, Connection connection){

        try {
            if (preparedStatement != null)
                preparedStatement.close();
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
