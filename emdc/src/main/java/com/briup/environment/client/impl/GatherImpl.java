package com.briup.environment.client.impl;

import com.briup.environment.backup.Backup;
import com.briup.environment.bean.Environment;
import com.briup.environment.client.Gather;
import com.briup.environment.conf.Configuration;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class GatherImpl implements Gather {
    private static Logger logger;       //日志对象
    private Configuration configuration;//配置对象
    private Backup backup;              //备份对象
    private String sourceFilePath;      //数据源文件路径
    private String bakFilePath;         //备份文件路径
    private int temperatureNum = 0;     //温度数据数目
    private int humidityNum = 0;        //湿度数据数目
    private int lightIntensityNum = 0;  //光照强度数据数目
    private int carbonDioxideNum = 0;   //二氧化碳数据数目
    private int lineNum = 0;            //读取的行数
    private int charNum = 0;            //读取的字节数
    private int preCharNum = 0;         //上一次读取的字节数

    public GatherImpl() throws Exception {
    }

    /**
     * 1.读取radwtmp
     * 2.一行数据封装成Envrionment
     * 3.存储到容器中(集合自选)
     * @return
     * @throws Exception
     */
    public Collection<Environment> gather() throws Exception {
        temperatureNum = 0;
        humidityNum = 0;
        lightIntensityNum = 0;
        carbonDioxideNum = 0;
        lineNum = 0;
        charNum = 0;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        Collection<Environment> environments = null;
        try {
            fileReader = new FileReader(sourceFilePath);
            bufferedReader = new BufferedReader(fileReader);
            String line = null;
            Environment environment = null;
            Environment newEnvironment = null;
            environments = new ArrayList<>();
            //获取上次读取数据文件的字节数
            if (backup.load(bakFilePath) != null){
                preCharNum = (Integer)(backup.load(bakFilePath));
                logger.debug("加载采集模块 " + bakFilePath + " 文件");
                //跳过上次读取的字节数，防止重复读取数据
                bufferedReader.skip(preCharNum);
                charNum += preCharNum;
            }
            while ((line = bufferedReader.readLine()) != null) {
                if (new File(sourceFilePath).length() == preCharNum) {
                    break;
                }
                charNum += line.getBytes().length + 2;
//                if (new File(sourceFilePath).length() == preCharNum)
                environment = new Environment();
                String[] splits = line.split("[|]");
                if (splits.length != 9) {
                    logger.error("采集的原始数据错误");
                    // 异常数据
                    return null;
                }
                environment.setSrcId(splits[0]);
                environment.setDstId(splits[1]);
                environment.setDevId(splits[2]);
                environment.setSersorAddress(splits[3]);
                environment.setCount(Integer.parseInt(splits[4]));
                environment.setCmd(splits[5]);
                environment.setStatus(Integer.parseInt(splits[7]));
                environment.setGather_date(new Timestamp(Long.parseLong(splits[8])));
                if (splits[3] != null && "16".equals(splits[3])) {
                    float temperature = (float) ((Integer.parseInt(splits[6].substring(0, 4), 16)) * 0.00268127 - 46.85);  //温度
                    float humidity = (float) ((Integer.parseInt(splits[6].substring(4, 8), 16)) * 0.00190735 - 6);          //湿度
                    environment.setName("温度");
                    environment.setData(temperature);
                    environments.add(environment);
                    temperatureNum++;
                    newEnvironment = deepCopy(environment);
                    newEnvironment.setName("湿度");
                    newEnvironment.setData(humidity);
                    environments.add(newEnvironment);
                    humidityNum++;
                } else {
                    int data = Integer.parseInt(splits[6].substring(0, 4), 16);
                    environment.setData(data);
                    environments.add(environment);
                    if ("256".equals(splits[3])) {
                        environment.setName("光照强度");
                        lightIntensityNum++;
                    } else if ("1280".equals(splits[3])) {
                        environment.setName("二氧化碳");
                        carbonDioxideNum++;
                    } else {
                        logger.error("环境参数出错");
                    }
                }
            }
            logger.info("数据采集完成");
        } catch (Exception e) {
            logger.error("数据采集发生错误");
        }finally {
            if (fileReader != null)
                fileReader.close();
            if (bufferedReader != null)
                bufferedReader.close();
        }
        //已读取的字符数的备份
        backup.backup(bakFilePath, charNum, false);
        logger.debug("以备份采集模块数据位置到 " + bakFilePath + " 备份文件");
        logger.info("湿度一共有:" + temperatureNum);
        logger.info("温度一共有:" + humidityNum);
        logger.info("光照强度一共有:" + lightIntensityNum);
        logger.info("二氧化碳一共有:" + carbonDioxideNum);
//        environments.forEach(info -> {
//            System.out.println(info + " ==== " + info.getGather_date().toLocalDateTime().getDayOfMonth());
//        });
        return environments;
    }

    /**
     * 对Environment对象做深拷贝
     * @param environment 原Environment对象
     * @return 新的Environment对象
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private Environment deepCopy(Environment environment) throws IOException, ClassNotFoundException {
        //将对象写到流里
        ByteArrayOutputStream byteOut=new ByteArrayOutputStream();
        ObjectOutputStream objOut=new ObjectOutputStream(byteOut);
        objOut.writeObject(environment);
        //从流里读出来
        ByteArrayInputStream byteIn=new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream objInput=new ObjectInputStream(byteIn);
        return (Environment)objInput.readObject();
    }

    public void init(Properties properties) throws Exception {
        sourceFilePath = properties.getProperty("src-file");
        bakFilePath = properties.getProperty("charNum");
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        try {
            logger = configuration.getLogger().getLoggers(GatherImpl.class);
            backup = configuration.getBackup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
