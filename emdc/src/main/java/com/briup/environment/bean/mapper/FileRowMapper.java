package com.briup.environment.bean.mapper;

import java.util.Collection;

public interface FileRowMapper {
    /**
     * 将文件中的一行数据封装成一个对应的对象
     * @param row
     * @return
     */
    Object mapperObject(String row);
}
