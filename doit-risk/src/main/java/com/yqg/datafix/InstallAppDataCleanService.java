package com.yqg.datafix;

import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.drools.beans.InstalledAppData;
import com.yqg.drools.model.InstalledAppInfo;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.RuleResult;
import com.yqg.drools.service.RuleService;
import com.yqg.drools.utils.DateUtil;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.mongo.dao.UserAppsDal;
import com.yqg.mongo.entity.UserAppsMongo;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdHistoryDao;
import com.yqg.order.entity.OrdBlack;
import com.yqg.order.entity.OrdBlackTemp;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRiskRecord;
import com.yqg.risk.entity.RiskSyncDataIds;
import com.yqg.service.scheduling.RiskDataSynService;
import com.yqg.system.entity.SysAutoReviewRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InstallAppDataCleanService {

    @Autowired
    private UserAppsDal userAppsDal;

    @Autowired
    private OrdHistoryDao ordHistoryDao;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RiskDataSynService riskDataSynService;



    /***
     * 分3个线程跑2018-08-01到2019-01-18的所有数据6个月 [2018-08-01,2019-01-18]
     */


    public void multiThreadRun(){

        Thread t1 = new Thread(()->{
            startTaskForDataTransfer(DateUtil.stringToDate("2018-08-01",DateUtil.FMT_YYYY_MM_DD),DateUtil.stringToDate("2018-10-23",DateUtil.FMT_YYYY_MM_DD));
        });
        Thread t2 = new Thread(()->{
            startTaskForDataTransfer(DateUtil.stringToDate("2018-10-23",DateUtil.FMT_YYYY_MM_DD),DateUtil.stringToDate("2018-12-07",DateUtil.FMT_YYYY_MM_DD));
        });
        t1.start();
        t2.start();

    }

    public static void main(String[] args) {
        new InstallAppDataCleanService().startTask(null,null);
    }

    public void startTaskForDataTransfer(Date startTime,Date endTime){
//        Long costStartTime = System.currentTimeMillis();
//        //取最大最小id
//        RiskSyncDataIds ids = riskDataSynService.getMinMaxIdsForTransfer(startTime,endTime);
//
//        Long startId = ids.getMinId();
//        Long tmpEndId = ids.getMinId();
//        Long endId = ids.getMaxId()+1;
//
//        Date periodStart = DateUtil.stringToDate("2018-10-23",DateUtil.FMT_YYYY_MM_DD);
//        boolean isTmp01 = false;
//        if(periodStart.compareTo(startTime)==0){
//            //period 02;
//            isTmp01 =true;
//        }
//
//        while(startId<endId){
//            try {
//                tmpEndId = startId+50000;
//                if(tmpEndId>endId){
//                    tmpEndId = endId;
//                }
//                log.info("start process startId: {},tmpEndId: {}",startId,tmpEndId);
//                if(isTmp01){
//                    //插入tmp表，
//                    //riskDataSynService.insertBatch01(startId,tmpEndId,startTime,endTime);
//                    // 删除原有表数据
//                    riskDataSynService.deleteBatch01(startId,tmpEndId,startTime,endTime);
//                }else{
//                    //插入tmp表，
//                    //riskDataSynService.insertBatch02(startId,tmpEndId,startTime,endTime);
//                    // 删除原有表数据
//                    riskDataSynService.deleteBatch02(startId,tmpEndId,startTime,endTime);
//                }
//
//
//            } catch (Exception e) {
//                log.error("data error, tmpEndId=" + tmpEndId + ",startId=" + startId, e);
//            }finally {
//                startId = tmpEndId;
//            }
//        }
//        Long costEndTime = System.currentTimeMillis();
//        log.info("finished... cost : {} ms",(costEndTime-costStartTime));
    }


    public void startTask(Date startTime,Date endTime){
        Long costStartTime = System.currentTimeMillis();
        //取最大最小id
        RiskSyncDataIds ids = riskDataSynService.getMinMaxIdsForOrder(startTime,endTime);
        Map<String, SysAutoReviewRule> rules = ruleService.getAllRules();
        Optional<KeyConstant> keyConstant = ruleService.getKeyConstants(rules);
//        RiskSyncDataIds ids = new RiskSyncDataIds();
//        ids.setMaxId(327L);
//        ids.setMinId(1L);
        Long startId = ids.getMinId();
        Long tmpEndId = ids.getMinId();
        Long endId = ids.getMaxId()+1;

        while(startId<endId){
            try {
                tmpEndId = startId+100;
                if(tmpEndId>endId){
                    tmpEndId = endId;
                }
                log.info("start process startId: {},tmpEndId: {}",startId,tmpEndId);
                List<OrdOrder> orderList = riskDataSynService.getOrdersByIdLimit(startId, tmpEndId);
                for(OrdOrder order: orderList){
                    try{
                        List<RuleResult> resultList =  extractInstallApps(order,keyConstant.get());
                        //批量保存
                        if(CollectionUtils.isEmpty(resultList)){
                            continue;
                        }
                        batchRecordRuleResult(order,rules,resultList);
                    }catch (Exception e){
                        log.error("orderId: "+order.getUuid(),e);
                    }
                }

            } catch (Exception e) {
                log.error("data error, tmpEndId=" + tmpEndId + ",startId=" + startId, e);
            }finally {
                startId = tmpEndId;
            }
        }
        Long costEndTime = System.currentTimeMillis();
        log.info("finished... cost : {} ms",(costEndTime-costStartTime));
    }

    public List<RuleResult> extractInstallApps(OrdOrder order, KeyConstant keyConstant){
        UserAppsMongo mongo = new UserAppsMongo();
        mongo.setUserUuid(order.getUserUuid());
        mongo.setOrderNo(order.getUuid());
        List<UserAppsMongo> mongoList = this.userAppsDal.find(mongo);
        if (CollectionUtils.isEmpty(mongoList)) {
            log.warn("mongodb installed app is empty, orderNo: {}", order.getUuid());
            return null;
        }

        String appData = mongoList.get(0).getData();
        if (StringUtils.isEmpty(appData)) {
            log.warn("the installed app is empty, orderNo: {}", order.getUuid());
            return null;
        }
        List<InstalledAppData> appList = JsonUtil.toList(appData, InstalledAppData.class);

        if (CollectionUtils.isEmpty(appList)) {
            log.warn("the installed app is empty, orderNo: {}", order.getUuid());
            return null;
        }

        InstalledAppInfo installedAppInfo = new InstalledAppInfo();
        List<RuleResult> ruleSetResultList = new ArrayList<>();

        List<String> validAppNames = appList.stream()
                .filter(elem -> !StringUtils.isEmpty(elem.getAppName()))
                .map(InstalledAppData::getAppName)
                .collect(Collectors.toList());

        Date year1971 = DateUtil.stringToDate("1971-01-01",DateUtil.FMT_YYYY_MM_DD);//不少系统自带软件更新时间是1970，排除掉
        Optional<InstalledAppData> latestUpdateApp =
                appList.stream().filter(elem -> elem.getLastUpdateTime() != null && elem.getLastUpdateTime().compareTo(year1971) > 0).max(Comparator.comparing(InstalledAppData::getLastUpdateTime));
        Optional<InstalledAppData> earliestUpdateApp =
                appList.stream().filter(elem -> elem.getLastUpdateTime() != null && elem.getLastUpdateTime().compareTo(year1971) > 0).min(Comparator.comparing(InstalledAppData::getLastUpdateTime));

        Date applyDate =  ordHistoryDao.getSubmitDate(order.getUuid());
        if(applyDate == null){
            applyDate = order.getApplyTime();
        }
        if (latestUpdateApp.isPresent()) {
            installedAppInfo.setDiffDaysBetweenLatestUpdateTimeAndCommitTime(DateUtil.getDiffDays(latestUpdateApp.get().getLastUpdateTime(), applyDate));
        }
        if (earliestUpdateApp.isPresent()) {
            installedAppInfo.setDiffDaysBetweenEarliestUpdateTimeAndCommitTime(DateUtil.getDiffDays(earliestUpdateApp.get().getLastUpdateTime(),
                    applyDate));
        }


        if (latestUpdateApp.isPresent() && earliestUpdateApp.isPresent()) {
            installedAppInfo.setDiffDaysBetweenForEarliestAndLatestUpdateTime(DateUtil.getDiffDays(earliestUpdateApp.get().getLastUpdateTime(),
                    latestUpdateApp.get().getLastUpdateTime()));
        }



        RuleUtils.buildUnHitRuleResult(BlackListTypeEnum.INSTALLPED_APP_DIFFDAYS_OF_LASTEST_COMMIT_TIME.getMessage(),
                RuleUtils.valueOfStr(installedAppInfo.getDiffDaysBetweenLatestUpdateTimeAndCommitTime()),
                "APP最后一次更新时间距提交的天数").addToResultList(ruleSetResultList);
        RuleUtils.buildUnHitRuleResult(BlackListTypeEnum.INSTALLPED_APP_DIFFDAYS_OF_EARLIEST_COMMIT_TIME.getMessage(),
                RuleUtils.valueOfStr(installedAppInfo.getDiffDaysBetweenEarliestUpdateTimeAndCommitTime()),
                "APP最早一次更新时间距提交的天数").addToResultList(ruleSetResultList);
        RuleUtils.buildUnHitRuleResult(BlackListTypeEnum.INSTALLPED_APP_DIFFDAYS_OF_EARLIEST_LATEST_TIME.getMessage(),
                RuleUtils.valueOfStr(installedAppInfo.getDiffDaysBetweenForEarliestAndLatestUpdateTime()),
                "APP最后一次更新时间-最早一次更新时间").addToResultList(ruleSetResultList);

        return ruleSetResultList;
    }




    @Transactional(rollbackFor = Exception.class)
    public void batchRecordRuleResult(OrdOrder order, Map<String, SysAutoReviewRule> allRules,
                                      List<RuleResult> ruleResultList) {
        List<OrdRiskRecord> orderRiskRecords = new ArrayList<>();
        List<OrdBlack> ordBlacks = new ArrayList<>();
        List<OrdBlackTemp> ordBlackTemps = new ArrayList<>();

        ruleResultList.stream().forEach(elem -> {
            SysAutoReviewRule rule = allRules.get(elem.getRuleName());
            if (rule == null) {
                log.error("the rule name error: ,ruleName: " + elem.getRuleName());
            }
            String respMessage;
            String realValue;
            if (elem.getRuleName().equalsIgnoreCase(BlackListTypeEnum.DATA_EMPTY.name())) {
                respMessage =
                        StringUtils.isEmpty(elem.getDesc()) ? elem.getRealValue() : elem.getDesc();
                realValue = StringUtils.isEmpty(elem.getRealValue()) ? elem.getDesc()
                        : elem.getRealValue();
            } else {
                respMessage = rule.getRuleDesc();
                realValue = elem.getRealValue();
            }
            //插入ordRiskRecord表
            OrdRiskRecord record = new OrdRiskRecord();
            record.setOrderNo(order.getUuid());
            record.setUserUuid(order.getUserUuid());
            record.setRuleRealValue(realValue);

            record.setRuleType(rule.getRuleType());
            record.setRuleDetailType(rule.getRuleDetailType());
            record.setRuleDesc(rule.getRuleDesc());
            record.setUuid(UUIDGenerateUtil.uuid());
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            orderRiskRecords.add(record);


        });

        if (!CollectionUtils.isEmpty(orderRiskRecords)) {
            riskDataSynService.addRiskRecordListForAppClean(orderRiskRecords);
        }


    }




}
