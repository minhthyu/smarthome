package com.briup.environment.bean.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DBRowMapper {
    /**
     * 将数据库结果集封装成对应的对象
     * @param rs 结果集
     * @return
     * @throws SQLException
     */
    Object mapperObject(ResultSet rs) throws SQLException;

}
