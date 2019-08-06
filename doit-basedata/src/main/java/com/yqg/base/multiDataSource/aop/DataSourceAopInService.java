package com.yqg.base.multiDataSource.aop;

import com.yqg.base.multiDataSource.dbconfig.DataSourceContextHolder;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Aspect
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Order(2)
@Component
public class DataSourceAopInService  {

    private static Logger log = LoggerFactory.getLogger(DataSourceAopInService.class);


    @Before("(execution(* com.yqg.service..*.*(..)) || execution(* com.yqg.drools.service..*.*(..))) && !execution(* com.yqg.service.scheduling.RiskDataSynService.*(..))")
    public void setWriteDataSourceType() {
//        log.info("****************DataSourceAopInService.setWriteDataSourceType***********************");
        DataSourceContextHolder.setWrite();
    }

    @Before("execution(* com.yqg.service.scheduling.RiskDataSynService.*(..))")
    public void setRiskBackupDataSourceType() {
        log.info("risk db set");
        DataSourceContextHolder.setRiskBackupDB();
    }

}
