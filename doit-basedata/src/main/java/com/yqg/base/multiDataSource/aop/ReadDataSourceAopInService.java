package com.yqg.base.multiDataSource.aop;

import com.yqg.base.multiDataSource.dbconfig.DataSourceContextHolder;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * ?service??????
 * <p>
 * ?????AOP?????????Ordered,order?????????
 * ??????????????????????
 */
@Aspect
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Order(4)
@Component
public class ReadDataSourceAopInService {

    private static Logger log = LoggerFactory.getLogger(ReadDataSourceAopInService.class);


    @Before("execution(* com.yqg.service..*.*(..)) "
            + " && @annotation(com.yqg.base.multiDataSource.annotation.ReadDataSource) " +
            "   && !execution(* com.yqg.service.scheduling.RiskDataSynService.*(..))")
    public void setReadDataSourceType() {
        log.info("****************DataSourceAopInService.setReadDataSourceType***********************");
        DataSourceContextHolder.setRead();
    }

//    @After("execution(* com.yqg.service..*.*(..)) "
//            + " and @annotation(com.yqg.base.multiDataSource.annotation.ReadDataSource) ")
//    public void resetWriteDataSourceType() {
//        log.info("****************DataSourceAopInService.resetWriteDataSourceType***********************");
//        DataSourceContextHolder.setWrite();
//    }



//    @Override
//    public int getOrder() {
//        /**
//         * ?????????
//         * ????????
//         * ????????@EnableTransactionManagement(order = 10)
//         */
//        return 1;
//    }

}
