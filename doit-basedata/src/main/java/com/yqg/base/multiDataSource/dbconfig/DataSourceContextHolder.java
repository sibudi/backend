package com.yqg.base.multiDataSource.dbconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ???????????
 *
 */
public class DataSourceContextHolder {

	private static Logger log = LoggerFactory.getLogger(DataSourceContextHolder.class);
	
	//??????
	private static final ThreadLocal<String> local = new ThreadLocal<String>();

    public static ThreadLocal<String> getLocal() {
        return local;
    }

    /**
     * ??
     */
    public static void setRead() {
        local.set(DataSourceType.read.getType());
//        log.info("read db set...");
    }

    /**
     * ??
     */
    public static void setWrite() {
        local.set(DataSourceType.write.getType());
//        log.info("write db set ...");
    }

    public static void setRiskBackupDB() {
        local.set(DataSourceType.risk_backup.getType());
        log.info("risk_backup db set ...");
    }

    public static String getReadOrWrite() {
        return local.get();
    }
    
    public static void clear(){
    	local.remove();
    }
}
