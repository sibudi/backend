package com.yqg.manage.service.collection;

import com.yqg.manage.dal.collection.CollectionOrderDetailDao;
import com.yqg.manage.dal.collection.ManQualityCheckRecordDao;
import com.yqg.manage.dal.user.CollectorInfoDao;
import com.yqg.manage.entity.collection.CollectionOrderDetail;
import com.yqg.manage.entity.collection.ManQualityCheckRecord;
import com.yqg.manage.entity.user.CollectorInfo;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.scheduling.qualitycheck.response.QualityCheckResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: tonggen
 * Date: 2019/3/7
 * time: 3:07 PM
 */
@Component
public class QualityCheckService {
    private Logger logger = LoggerFactory.getLogger(QualityCheckService.class);

    @Autowired
    private ManQualityCheckRecordDao manQualityCheckRecordDao;

    @Autowired
    private CollectionOrderDetailDao collectionOrderDetailDao;

    /**
     * 获得所有需要添加标记的订单
     */
    public void qualityCheckD0() {
        List<QualityCheckResponse> responseList =
                manQualityCheckRecordDao.listQualityCheckD0();
        if (CollectionUtils.isEmpty(responseList)) {
            logger.info("no qualityCheckD0 need solve");
            return ;
        }
        //添加记录到record
        for (QualityCheckResponse response : responseList) {
            addManQualityCheck(response.getOrderNo(), response.getUserUuid(), 3);
        }
    }

    /**
     */
    public void qualityCheckPromiseTime() {

        List<QualityCheckResponse> responseList =
                manQualityCheckRecordDao.qualityCheckPromiseTime();
        if (CollectionUtils.isEmpty(responseList)) {
            logger.info("no qualityCheckPromiseTime need solve");
            return ;
        }
        //过滤掉一个小时之前没有记录的数据
        responseList.stream().filter(elem-> {
            return hitOrNot(elem.getOrderNo(), 1);
        }).forEach(e -> {
            addManQualityCheck(e.getOrderNo(), e.getUserUuid(), 4);
        });
    }

    @Autowired
    private CollectionAutoAssignmentService service;
    /**
     *"12：00前是否有添加过新记录；
     若不满足，则增加一条备注质检记录，质检标签为：D0案件及新增逾期案件12点之前未跟进问题；质检人员为：系统质检"
     */
    public void qualityCheckBefore12() {

        String stages = service.getOrderStage();
        List<QualityCheckResponse> responseList =
                manQualityCheckRecordDao.qualityCheckBefore12(stages);
        if (CollectionUtils.isEmpty(responseList)) {
            logger.info("no qualityCheckBefore12 need solve");
            return ;
        }
        //过滤掉12点之前没有记录的数据
        responseList.stream().filter(elem-> {
            return hitOrNot(elem.getOrderNo(), 12);
        }).forEach(e -> {
            addManQualityCheck(e.getOrderNo(), e.getUserUuid(), 5);
        });
    }

    /**
     *"17：00前是否有添加过新记录；
     若不满足，则增加一条备注质检记录，质检标签为：一天以上未跟进订单 ；质检人员为：系统质检"
     */
    public void qualityCheckBefore17() {

        List<QualityCheckResponse> responseList =
                manQualityCheckRecordDao.qualityCheckBefore17();
        if (CollectionUtils.isEmpty(responseList)) {
            logger.info("no qualityCheckBefore17 need solve");
            return ;
        }
        //过滤掉17点之前没有记录的数据
        responseList.stream().filter(elem-> {
            return hitOrNot(elem.getOrderNo(), 17);
        }).forEach(e -> {
            addManQualityCheck(e.getOrderNo(), e.getUserUuid(), 6);
        });
    }

    private boolean hitOrNot(String orderNo, Integer hours) {

        Integer count = manQualityCheckRecordDao.hitOrNot(orderNo, hours);

        if (count > 0) {
            return false;
        }
        return true;
    }

    @Autowired
    private CollectorInfoDao collectorInfoDao;
    /**
     *
     根据订单号码插入数据到manQualityRecord
     */
    public void addManQualityCheck(String orderNo, String userUuid, Integer orderTag) {

        //先查询到催收人员
        CollectionOrderDetail collectionOrderDetail = new CollectionOrderDetail();
        collectionOrderDetail.setDisabled(0);
        collectionOrderDetail.setSourceType(0);
        collectionOrderDetail.setOrderUUID(orderNo);
        List<CollectionOrderDetail> lists = collectionOrderDetailDao.scan(collectionOrderDetail);
        if (CollectionUtils.isEmpty(lists)) {
            logger.info("not find collector!");
            return ;
        }
        //判断催收人员是否休息
        CollectorInfo collectorInfo = new CollectorInfo();
        collectorInfo.setDisabled(0);
        collectorInfo.setRest(1);
        collectorInfo.setUserId(lists.get(0).getOutsourceId());
        if (!CollectionUtils.isEmpty(collectorInfoDao.scan(collectorInfo))) {
            logger.info("the collector is resting!");
            return ;
        }
        ManQualityCheckRecord record = new ManQualityCheckRecord();
        record.setOrderNo(orderNo);
        record.setUserUuid(userUuid);
        record.setCollectorId(lists.get(0).getOutsourceId());
        record.setCheckTag(orderTag);
        record.setRemark("system automatic quality check.");
        record.setType(0);
        manQualityCheckRecordDao.insert(record);

    }

}
