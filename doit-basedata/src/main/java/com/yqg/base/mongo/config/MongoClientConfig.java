package com.yqg.base.mongo.config;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;

@Configuration
public class MongoClientConfig {

    @Autowired
    private MongoConfig mongoConfig;

//    public @Bean
////    Mongo mongo() throws Exception {
////        List<ServerAddress> seeds = new ArrayList<>();
////        String[] ipPorts = this.mongoConfig.getRepleSet().trim().split(",");
////        for (String ipPort : ipPorts) {
////            String[] zones = ipPort.trim().split(":");
////            seeds.add(new ServerAddress(zones[0], Integer.valueOf(zones[1])));
////        }
////        MongoOptions options = new MongoOptions();
////        if (mongoConfig.getConnectionsPerHost() > 10) {//最大连接数,默认10
////            options.setConnectionsPerHost(mongoConfig.getConnectionsPerHost());
////        }
////        if (mongoConfig.getThreadsAllowedToBlockForConnectionMultiplier() > 5) {//连接并发数,默认5
////            options.setThreadsAllowedToBlockForConnectionMultiplier(
////                mongoConfig.getThreadsAllowedToBlockForConnectionMultiplier());
////        }
////        Mongo mongo = new Mongo(seeds, options);
////        if (!StringUtils.isEmpty(mongoConfig.getUsername()) && !StringUtils
////            .isEmpty(mongoConfig.getPassword())) {
////            DB db = mongo.getDB(mongoConfig.getDbName());
////            boolean result = db
////                .authenticate(mongoConfig.getUsername(), mongoConfig.getPassword().toCharArray());
////            if (!result) {
////                throw new Exception();
////            }
////        }
////        return mongo;
////    }


    @Bean
    public MongoClient mongoClient() throws Exception {
        List<ServerAddress> seeds = new ArrayList<>();
        String[] ipPorts = this.mongoConfig.getRepleSet().trim().split(",");
        for (String ipPort : ipPorts) {
            String[] zones = ipPort.trim().split(":");
            seeds.add(new ServerAddress(zones[0], Integer.valueOf(zones[1])));
        }
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();

        if (mongoConfig.getConnectionsPerHost() > 10) {//最大连接数,默认100
            builder.connectionsPerHost(mongoConfig.getConnectionsPerHost());
        }
        if (mongoConfig.getThreadsAllowedToBlockForConnectionMultiplier() > 5) {//连接并发数,默认5
            builder.threadsAllowedToBlockForConnectionMultiplier(
                mongoConfig.getThreadsAllowedToBlockForConnectionMultiplier());
        }
        //设置socket超时时间2min
        builder.socketTimeout(2*60*1000);
        MongoClientOptions options = builder.build();
        MongoCredential credential = MongoCredential
            .createMongoCRCredential(mongoConfig.getUsername(), mongoConfig.getDbName(),
                mongoConfig.getPassword().toCharArray());

        MongoClient mongoClient = new MongoClient(seeds, Arrays.asList(credential),options);

        return mongoClient;
    }


    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(),
            this.mongoConfig.getDbName());
        return mongoTemplate;
    }
}
