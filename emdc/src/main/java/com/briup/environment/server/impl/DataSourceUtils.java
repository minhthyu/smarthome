package com.briup.environment.server.impl;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataSourceUtils {
    private static ComboPooledDataSource cpds = new ComboPooledDataSource();
    private static Logger logger = Logger.getLogger(DataSourceUtils.class);

    //获取连接对象，从连接池中返回一个连接对象
    public static Connection getConnection() throws SQLException {
        return cpds.getConnection();

    }

    //释放连接回连接池
    public static void close(Connection conn, PreparedStatement pst, ResultSet rs){
        if(rs!=null){
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("Exception in DataSourceUtils!数据库连接关闭出错!", e);
//                throw new MyError("数据库连接关闭出错!", e);
            }
        }
        if(pst!=null){
            try {
                pst.close();
            } catch (SQLException e) {
                logger.error("Exception in DataSourceUtils!数据库连接关闭出错!", e);
//                throw new MyError("数据库连接关闭出错!", e);
            }
        }

        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("Exception in DataSourceUtils!数据库连接关闭出错!", e);
//                throw new MyError("数据库连接关闭出错!", e);
            }
        }
    }

    //获取连接池对象
    public static DataSource getDataSource() {
        return cpds;
    }
//    // 因为 LinkedList 是用链表实现的,对于增删实现起来比较容易
//    LinkedList<Connection> dataSources = new LinkedList<Connection>();
//
//    // 初始化连接数量
//    public MyDataSource() {
//        // 问题：每次new MyDataSource 都会建立 10 个链接，可使用单例设计模式解决此类问题
//        for(int i = 0; i < 10; i++) {
//            try {
//                // 1、装载 sqlserver 驱动对象
//                DriverManager.registerDriver(new SQLServerDriver());
//                // 2、通过 JDBC 建立数据库连接
//                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/s_bm?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=UTC", "briup123", "briup123");
//                // 3、将连接加入连接池中
//                dataSources.add(con);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public Connection getConnection() throws SQLException {
//        // 取出连接池中一个连接
//        final Connection conn = dataSources.removeFirst(); // 删除第一个连接返回
//        return conn;
//    }
//
//    // 将连接放回连接池
//    public void releaseConnection(Connection conn) {
//        dataSources.add(conn);
//    }
}
