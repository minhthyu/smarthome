package com.briup.environment.log.impl;

import com.briup.environment.conf.Configuration;
import com.briup.environment.log.Log;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.impl.Log4jLoggerFactory;

import java.util.Properties;

public class LogImpl implements Log {
    public static Logger rootLogger = Logger.getLogger(LogImpl.class);
    private Configuration configuration;

    public LogImpl() {
    }

    @Override
    public void debug(String message) {
        this.rootLogger.debug(message);
    }

    @Override
    public void info(String message) {
        this.rootLogger.info(message);
    }

    @Override
    public void warn(String message) {
        this.rootLogger.warn(message);
    }

    @Override
    public void error(String message) {
        this.rootLogger.error(message);
    }

    @Override
    public void fatal(String message) {
        this.rootLogger.fatal(message);
    }

    public Logger getLoggers(Class aClass) {
        return Logger.getLogger(aClass);
    }

    @Override
    public void init(Properties properties) throws Exception {
        // 获取Logger对象
        // 1.读取log4j.properties
        PropertyConfigurator.configure(properties.getProperty("log-properties"));
        //2.获取Logger对象 RootLogger
//        this.rootLogger = Logger.getRootLogger();
//        rootLogger.error(rootLogger.getLoggerRepository().getLogger("server").getName());
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
