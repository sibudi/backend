package com.yqg.mongo.dao;

import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;
import com.yqg.mongo.entity.MongoPageEntity;
import com.yqg.mongo.entity.OrderRiskRecordMongo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MongoPageQueryDal {

    @Autowired
    private MongoTemplate mongoTemplate;

    public <T> List<T> findResultByPage(MongoPageEntity<T> pageEntity){
        //每次查询pageSize个订单的数据
        Query query = new Query();
        Criteria criteria = new Criteria("updateTime");
        criteria.gte(pageEntity.getCreateStartTime()).lt(pageEntity.getCreateEndTime());
        query.addCriteria(criteria);
        if(!StringUtils.isEmpty(pageEntity.getLastPageMaxObjectId())){
            Criteria idQuery = new Criteria("_id");
            idQuery.gt(new ObjectId(pageEntity.getLastPageMaxObjectId()));
            query.addCriteria(idQuery);
        }



        query.limit(pageEntity.getPageSize()).with(new Sort(Sort.Direction.ASC,"_id"));

        String tableName = "";
        Table table = pageEntity.getEntityClass().getAnnotation(Table.class);
        if (table != null) {
            tableName = table.value();
        } else {
            tableName =  pageEntity.getEntityClass().getSimpleName();
        }
        log.info("page query param: "+query);
        List<?> resultList = mongoTemplate.find(query, pageEntity.getEntityClass(),tableName);
        if(CollectionUtils.isEmpty(resultList)){
            return new ArrayList<>();
        }
        return (List<T>) resultList;
    }
}
