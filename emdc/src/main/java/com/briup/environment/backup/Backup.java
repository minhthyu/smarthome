package com.briup.environment.backup;

import com.briup.environment.util.ConfigurationAWare;
import com.briup.environment.util.WossModuleInit;

import java.util.Properties;

/**
 * 备份类
 * @author Administrator
 */
public interface Backup extends WossModuleInit, ConfigurationAWare {
	/**
	 * 备份数据
	 * @param fileName 备份文件
	 * @param data  备份数据
	 * @param append 备份模式 是否是追加
	 * @throws Exception 
	 */
	void backup(String fileName,Object data, boolean append) throws Exception;
	
	/**
	 * 加载备份
	 * @param fileName 备份文件
	 * @return  备份数据
	 * @throws Exception  
	 */
	Object load(String fileName) throws Exception;
	
	/**
	 * 删除备份
	 * @param fileName
	 */
	void deleteBackup(String fileName);
		
}
