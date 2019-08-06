package com.yqg.base.multiDataSource.dbconfig;

import com.github.pagehelper.PageHelper;
import com.yqg.base.multiDataSource.util.SpringContextUtil;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@AutoConfigureAfter(DataSourceConfiguration.class)
public class MybatisConfiguration {
    private static Logger log = LoggerFactory.getLogger(MybatisConfiguration.class);

    @Value("${mysql.datasource.readSize}")
    private String readDataSourceSize;

//	//XxxMapper.xml??????
//	  @Value("${mysql.datasource.mapperLocations}")
//      private String mapperLocations;
//
//     //  ?????????
//      @Value("${mysql.datasource.configLocation}")
//      private String configLocation;

    @Autowired
	@Qualifier("writeDataSource")
    private DataSource writeDataSource;
    @Autowired
	@Qualifier("readDataSource01")
    private DataSource readDataSource01;
    @Autowired
	@Qualifier("readDataSource02")
    private DataSource readDataSource02;

    @Autowired
	@Qualifier("riskBackupDatasource")
    private DataSource riskBackupDatasource;


    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactorys() throws Exception {
//        log.info("--------------------  sqlSessionFactory init ---------------------");
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(roundRobinDataSourceProxy());

        //?????????sql??
        Interceptor[] plugins = new Interceptor[]{pageHelper(), new SqlPrintInterceptor()};
        sessionFactoryBean.setPlugins(plugins);

        return sessionFactoryBean.getObject();
    }

    /**
     * ????
     *
     * @return
     */
    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties p = new Properties();
        p.setProperty("offsetAsPageNum", "true");
        p.setProperty("rowBoundsWithCount", "true");
        p.setProperty("reasonable", "true");
        p.setProperty("returnPageInfo", "check");
        p.setProperty("params", "count=countSql");
        pageHelper.setProperties(p);
        return pageHelper;
    }

    /**
     * ????????????
     *
     * @return
     */
    @Bean(name = "roundRobinDataSourceProxy")
    public AbstractRoutingDataSource roundRobinDataSourceProxy() {

        Map<Object, Object> targetDataSources = new HashMap<Object, Object>();

        targetDataSources.put(DataSourceType.write.getType(), writeDataSource);
        targetDataSources.put(DataSourceType.read.getType() + "1", readDataSource01);
        targetDataSources.put(DataSourceType.read.getType() + "2", readDataSource02);
        //风控库
        targetDataSources.put(DataSourceType.risk_backup.getType(), riskBackupDatasource);

        final int readSize = Integer.parseInt(readDataSourceSize);
        //     MyAbstractRoutingDataSource proxy = new MyAbstractRoutingDataSource(readSize);

        //????????????
        AbstractRoutingDataSource proxy = new AbstractRoutingDataSource() {
            private AtomicInteger count = new AtomicInteger(0);

            /**
             * ??AbstractRoutingDataSource??????????
             * ???????????????dataSource?key??????key??
             * targetDataSources????????DataSource??????????????????
             */
            @Override
            protected Object determineCurrentLookupKey() {
                String typeKey = DataSourceContextHolder.getReadOrWrite();

                if (typeKey == null) {
//                    log.info("???????write.............");
                    return DataSourceType.write.getType();
//        			throw new NullPointerException("???????????????????????...");
                }

                if (typeKey.equals(DataSourceType.write.getType())) {
//                    log.info("?????write.............");
                    return DataSourceType.write.getType();
                }

                if(typeKey.equals(DataSourceType.risk_backup.getType())){
                    return DataSourceType.risk_backup.getType();
                }

                //??? ??????
                int number = count.getAndAdd(1);
                int lookupKey = number % readSize;
//                log.info("?????read-" + (lookupKey + 1));
                return DataSourceType.read.getType() + (lookupKey + 1);
            }
        };

        proxy.setDefaultTargetDataSource(writeDataSource);//???
        proxy.setTargetDataSources(targetDataSources);
        return proxy;
    }


    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    @Bean(name="transactionManager")
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager((DataSource) SpringContextUtil.getBean("roundRobinDataSourceProxy"));
    }
}
