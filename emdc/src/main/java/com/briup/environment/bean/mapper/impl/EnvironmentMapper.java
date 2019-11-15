package com.briup.environment.bean.mapper.impl;

import com.briup.environment.bean.Environment;
import com.briup.environment.bean.mapper.FileRowMapper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class EnvironmentMapper implements FileRowMapper {
    private static Environment environment;
    private List<Environment> environments;
    static {
        environment = new Environment();
    }
    public EnvironmentMapper() {
        environments = new ArrayList<>();
    }

    @Override
    public Object mapperObject(String row) {
        return null;
    }
}
