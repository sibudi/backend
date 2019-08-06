package com.yqg.service.scheduling;

import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.mongo.entity.OrderRiskRecordMongo;
import com.yqg.mongo.entity.UserIziVerifyResultMongo;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.FraudRule;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRiskRecord;
import com.yqg.risk.dao.SyncDataDao;
import com.yqg.risk.entity.RiskSyncDataConfig;
import com.yqg.risk.entity.RiskSyncDataIds;
import com.yqg.risk.repository.OrderRiskRecordRepository;
import com.yqg.user.entity.UsrIziVerifyResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class RiskDataSynService {
    @Autowired
    private SyncDataDao syncDataDao;

    @Autowired
    private OrdDao ordDao;

    private int PAGE_SIZE = 40;

    public static String RISK_RECORD_SYNC = "orderRiskRecord_sync";

    public static String IZI_SYNC = "izi_sync";



    @Autowired
    private OrderRiskRecordRepository orderRiskRecordRepository;

    public void syncMongo2MySQL() {
        log.info("syncMongo2MySQL start...");
        long startTime = System.currentTimeMillis();
        RiskSyncDataConfig config = syncDataDao.getRiskDataSyncConfig();
        if (config == null || config.getStartTime() == null || config.getEndTime() == null) {
            log.info("the sync config is empty");
            return;
        }
        log.info("syncMongo2MySQL config: {}", JsonUtils.serialize(config));
        List<OrderRiskRecordMongo> result = orderRiskRecordRepository.getOrderRiskRecordByPage(PAGE_SIZE, config.getStartTime(), config.getEndTime(), null);
        //有数据
        while (!CollectionUtils.isEmpty(result)) {
            //取最后一笔记录的objectId
            String lastObjectId = result.get(result.size() - 1).getId();
            //是否有后续页数据
            boolean needToQuery = result.size() >= PAGE_SIZE;
            //将结果批量插入到到风控库
            insert2MySQL(result);
            if (needToQuery) {
                result = orderRiskRecordRepository.getOrderRiskRecordByPage(PAGE_SIZE, config.getStartTime(), config.getEndTime(), lastObjectId);
            }else{
                break;
            }
        }
        //更新同步时间
        syncDataDao.updateRiskDataSyncConfig();
        log.info("syncMongo2MySQL finished, cost: {} ms",(System.currentTimeMillis()-startTime));

    }

    private void insert2MySQL(List<OrderRiskRecordMongo> dataList) {
        for (OrderRiskRecordMongo elem : dataList) {
            try {
                syncDataDao.disabledRecord(elem.getOrderNo());
                elem.getRuleResult().stream().forEach(e1->{
                    if(StringUtils.isEmpty(e1.getRemark())){
                        e1.setRemark("");
                    }
                });
                syncDataDao.addRiskRecordList(elem.getRuleResult());
            } catch (Exception e) {
                log.info("error for insert2MySQL, orderNo: " + elem.getOrderNo(), e);
            }
        }
    }


    /***
     * 风控明细数据最新1.5个月的数据放到ordRiskRecord_last,最新3个月到1.5个月前的放到 ordRiskRecord_01
     * 每天凌晨处理,将ordRiskRecord_last前一天的数据转移到ordRiskRecord_01，同时删除ordRiskRecord_01中90天前的数据
     */
    public void  splitTable() {
        log.info("start split table");
        //将ordRiskRecord_last最早一天的数据同步到ordRiskRecord_01，
        //取最早一天id最大值和最小值
        Date beginDate = syncDataDao.getMinDateFromOrderRiskRecordLast();
        beginDate = DateUtils.dateFormat(beginDate, DateUtils.FMT_YYYY_MM_DD);
        Date endDate  = DateUtils.addDate(beginDate, 1);
        RiskSyncDataIds syncIdPeriod = syncDataDao.getMaxMinIdsByDate(endDate, beginDate);

        if (syncIdPeriod.getMaxId().equals(syncIdPeriod.getMinId())) {
            return;
        }
        //每次取5k进行insert
        Long perTimeCount = 5000L;
        Long startPeriod = syncIdPeriod.getMinId();
        Long endPeriod = syncIdPeriod.getMaxId() + 1;
        while (startPeriod < endPeriod) {
            Long tmpEndPeriod = startPeriod + perTimeCount;
            try {
                tmpEndPeriod = tmpEndPeriod > endPeriod ? endPeriod : tmpEndPeriod;
                log.info("batch insert period, start: {} ,end : {}",startPeriod,tmpEndPeriod);
                syncDataDao.batchInsertByIdPeriod(startPeriod, tmpEndPeriod,beginDate,endDate);
                //删除 ordRiskRecord_latest 前一天的数据根据id
                log.info("batch delete period, start: {} ,end : {}",startPeriod,tmpEndPeriod);
                syncDataDao.batchDeleteByIdPeriod(startPeriod, tmpEndPeriod,beginDate,endDate);
                log.info("end delete period, start: {} ,end : {}",startPeriod,tmpEndPeriod);
            } catch (Exception e) {
                log.error("split table error", e);
            } finally {
                startPeriod = tmpEndPeriod;
            }
        }

        log.info("end trans data from ordRiskRecord_latest to ordRiskRecord_01 ");
        //删除ordRiskRecord_01中保留天前的数据,并将其转移到ordRiskRecord_02表
        Date currentDate = DateUtils.dateFormat(beginDate, DateUtils.FMT_YYYY_MM_DD);
        Date dayBefore45 = DateUtils.addDate(currentDate,-45);

        //统计90天前数据最大最小id值
        RiskSyncDataIds idsForTable01 = syncDataDao.getMaxMinIdsForTable01(dayBefore45);
        if(idsForTable01==null){
            log.info("the idsForTable01 is empty");
            return;
        }
        //每次5w
        startPeriod = idsForTable01.getMinId();
        endPeriod = idsForTable01.getMaxId() + 1;
        while (startPeriod < endPeriod) {
            Long tmpEndPeriod = startPeriod + perTimeCount;
            try {
                tmpEndPeriod = tmpEndPeriod > endPeriod ? endPeriod : tmpEndPeriod;
                log.info("batch insert period table02, start: {} ,end : {}",startPeriod,tmpEndPeriod);
                syncDataDao.insertBatch02(startPeriod, tmpEndPeriod,dayBefore45);
                //删除 ordRiskRecord_01
                log.info("batch delete period table01, start: {} ,end : {}",startPeriod,tmpEndPeriod);
                syncDataDao.deleteBatch01(startPeriod, tmpEndPeriod,dayBefore45);
                log.info("end delete period table01, start: {} ,end : {}",startPeriod,tmpEndPeriod);
            } catch (Exception e) {
                log.error("split table error table01", e);
            } finally {
                startPeriod = tmpEndPeriod;
            }
        }

        log.info("end split table");

    }


    /***
     * 从库读取riskRecord表 获取 欺诈规则命中率 相关
     */
    public List<FraudRule> getFraudRuleData() {

        List<FraudRule> fraudRuleList = ordDao
                .getFraudRuleData();
        return fraudRuleList;
    }


    public List<OrdRiskRecord> getFixData(){
        return syncDataDao.getFixData();
    }
    public void addFixData(List<OrdRiskRecord> insertList){
        syncDataDao.addRiskRecordList(insertList);
    }




    public RiskSyncDataIds getMinMaxIdsForTransfer(Date startDate,Date endDate){
        return syncDataDao.getMaxMinIdsByDate(endDate,startDate);
    }


    public RiskSyncDataIds getMinMaxIdsForOrder(Date startDate,Date endDate){
        return syncDataDao.getMinMaxIdsForOrder(startDate,endDate);
    }


    public List<OrdOrder> getOrdersByIdLimit(Long startId,  Long endId){
        return syncDataDao.getOrdersByIdLimit(startId,endId);
    }


    public int addRiskRecordListForAppClean(@Param("riskRecords") List<OrdRiskRecord> riskRecords){
        return syncDataDao.addRiskRecordListForAppClean(riskRecords);
    }

    public void addIziDataClean(UsrIziVerifyResult iziVerifyResult){
        syncDataDao.addIziCleanData(iziVerifyResult);
    }

    public void addIziWhatsApp(UserIziVerifyResultMongo record) {
        //如果存在则delete 然后insert
        if (hasWhatsappResult(record.getWhatsAppNumber(), record.getOrderNo())) {
            //delete
            syncDataDao.deleteWhatsappByOrderAndSequence(record.getWhatsAppNumberType(), record.getOrderNo());
        }
        syncDataDao.addIziWhatsApp(record);
    }

    public boolean hasWhatsappResult(String number, String orderNo) {
        Integer affectRow = syncDataDao.hasWhatsAppResult(number, orderNo);
        return affectRow != null && affectRow >= 1;
    }

    public boolean hasSuccessWhatsappResult(String number, String orderNo) {
        Integer affectRow = syncDataDao.hasSuccessWhatsappResult(number, orderNo);
        return affectRow != null && affectRow >= 1;
    }


    public List<OrdOrder> getReRunAdvanceData(){
        return syncDataDao.getReRunAdvanceData();
    }




}
