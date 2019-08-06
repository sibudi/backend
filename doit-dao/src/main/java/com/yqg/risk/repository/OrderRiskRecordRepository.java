package com.yqg.risk.repository;

import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.mongo.dao.MongoPageQueryDal;
import com.yqg.mongo.dao.OrderRiskRecordDal;
import com.yqg.mongo.entity.MongoPageEntity;
import com.yqg.mongo.entity.OrderRiskRecordMongo;
import com.yqg.order.dao.OrdRiskRecordDao;
import com.yqg.order.entity.OrdRiskRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderRiskRecordRepository {
    @Autowired
    private OrderRiskRecordDal orderRiskRecordDal;

    @Autowired
    private MongoPageQueryDal mongoPageQueryDal;

//    @Autowired
//    private OrdRiskRecordDao ordRiskRecordDao;

    public OrdRiskRecord getRuleResultByRuleName(String ruleDetailType, String orderNo) {
        //先从mongodb查询：
        List<OrdRiskRecord> records = getOrderRiskRecordList(orderNo);
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        List<OrdRiskRecord> ruleRecords =
                records.stream().filter(elem -> elem.getRuleDetailType().equalsIgnoreCase(ruleDetailType)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(ruleRecords)){
            return null;
        }

        Optional<OrdRiskRecord> recordOptional = ruleRecords.stream().max(Comparator.comparing(OrdRiskRecord::getCreateTime));
        if (recordOptional.isPresent()) {
            return recordOptional.get();
        }
        return null;
    }

    public List<OrdRiskRecord> getFaceVerifyScoreRuleResult(@Param("orderNo") String orderNo) {
        //先从mongodb查询：
        List<OrdRiskRecord> records = getOrderRiskRecordList(orderNo);

        records = records.stream().filter(elem -> elem.getRuleDetailType().equalsIgnoreCase(BlackListTypeEnum.CASH2_FACE_PLUS_PLUS_SCORE.getMessage())
                || elem.getRuleDetailType().equalsIgnoreCase(BlackListTypeEnum.YITU_SCORE_LIMIT.getMessage())).collect(Collectors.toList());
        return records;

    }

    public List<OrdRiskRecord> getOrderRiskRecordList(String orderNo){
        List<OrdRiskRecord> records = getRecordsFromMongo(orderNo);
        if(records !=null){
            return records;
        }
        return new ArrayList<>();
//        OrdRiskRecord searchDB = new OrdRiskRecord();
//        searchDB.setOrderNo(orderNo);
//        searchDB.setDisabled(0);
//        return ordRiskRecordDao.scan(searchDB);
    }

    public List<OrdRiskRecord> getRecordsFromMongo(String orderNo) {
        OrderRiskRecordMongo searchParam = new OrderRiskRecordMongo();
        searchParam.setOrderNo(orderNo);
        searchParam.setDisabled(0);
        List<OrderRiskRecordMongo> mongoList = orderRiskRecordDal.find(searchParam);
        if (!CollectionUtils.isEmpty(mongoList)) {
            List<OrdRiskRecord> records = mongoList.get(0).getRuleResult();
            records = records.stream().filter(elem -> elem.getDisabled() == null || elem.getDisabled() == 0).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(records)) {
                return new ArrayList<>();
            }
            return records;
        }
        //null标识mongo无数据
        return null;
    }

    public void addRiskRecordList(List<OrdRiskRecord> records){
        if(CollectionUtils.isEmpty(records)){
            return;
        }
        OrderRiskRecordMongo searchParam = new OrderRiskRecordMongo();
        searchParam.setDisabled(0);
        searchParam.setOrderNo(records.get(0).getOrderNo());
        List<OrderRiskRecordMongo> dbList = orderRiskRecordDal.find(searchParam);
        if(CollectionUtils.isEmpty(dbList)){
            //无数据insert
            OrderRiskRecordMongo insertParam = new OrderRiskRecordMongo();
            insertParam.setDisabled(0);
            insertParam.setOrderNo(records.get(0).getOrderNo());
            insertParam.setUserUuid(records.get(0).getUserUuid());
            insertParam.setUpdateTime(new Date());
            insertParam.setCreateTime(new Date());
            if(records.size()>2000){
                log.info("the order: {} records exceed 2000",insertParam.getOrderNo());
                return;
            }
            insertParam.setRuleResult(records);
            orderRiskRecordDal.insertObject(insertParam);
        }else{
            //有数据update
            OrderRiskRecordMongo dbRecord = dbList.get(0);
            if(CollectionUtils.isEmpty(dbRecord.getRuleResult())){
                dbRecord.setRuleResult(records);
            }else{
                List<String> newRules = records.stream().map(elem->elem.getRuleDetailType()).collect(Collectors.toList());
                dbRecord.getRuleResult().forEach(elem->{
                    if(newRules.contains(elem.getRuleDetailType())){
                        elem.setDisabled(1);
                    }
                });
                dbRecord.getRuleResult().addAll(records);

            }
            dbRecord.setUpdateTime(new Date());
            orderRiskRecordDal.updateById(dbRecord);
        }


    }

    public List<OrdRiskRecord> selectRuleResultByRuleNames(String orderNo, List<String> ruleList) {
        List<OrdRiskRecord> records = getOrderRiskRecordList(orderNo);
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        return records.stream().filter(elem -> ruleList.contains(elem.getRuleDetailType())).collect(Collectors.toList());
    }

    public List<OrderRiskRecordMongo> getOrderRiskRecordByPage(Integer pageSize, Date beginTime, Date endTime, String lastMaxObjectId) {
        MongoPageEntity entity = new MongoPageEntity<OrderRiskRecordMongo>();
        entity.setPageSize(pageSize);
        entity.setCreateStartTime(beginTime);
        entity.setCreateEndTime(endTime);
        entity.setEntityClass(OrderRiskRecordMongo.class);
        entity.setLastPageMaxObjectId(lastMaxObjectId);
        List<OrderRiskRecordMongo> resultList = mongoPageQueryDal.findResultByPage(entity);
        return resultList;
    }

}
