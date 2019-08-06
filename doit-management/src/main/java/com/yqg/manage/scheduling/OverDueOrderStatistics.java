package com.yqg.manage.scheduling;


import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.TimeUtil;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.datasource.TargetDataSource;
import com.yqg.manage.service.loan.ManOrderCountService;
import com.yqg.manage.service.loan.request.FindOrderForCompanyRequest;
import com.yqg.mongo.dao.ManOverDueOrderAnalysisDal;
import com.yqg.mongo.entity.ManOverDueOrderAnalysisMongo;
import com.yqg.service.order.OrdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author alan
 */
@Component
public class OverDueOrderStatistics {
    @Autowired
    private ManOverDueOrderAnalysisDal manOverDueOrderAnalysisDal;

    @Autowired
    private ManOrderCountService manOrderCountService;

    @Autowired
    private OrdService orderOrderService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 初始化数据*/
    public void initData() throws Exception {
        Integer dayCount = DateUtils.daysBetween("2018-03-01",DateUtils.dateToDay());
        this.addOverDueData(dayCount);
    }

    /**
     * 数据app逾期数据统计更新*/
    public void updateOverDueDailyData() throws Exception {
        this.addOverDueData(0);
        Integer dayCount = DateUtils.daysBetween("2018-03-01",DateUtils.dateToDay());
        for(int count = dayCount;count > 0;count--){
            /*到期日*/
            String day = TimeUtil.getPastDate(count+1);
            logger.info("day = {}",day);

            ManOverDueOrderAnalysisMongo search = new ManOverDueOrderAnalysisMongo();
            search.setDueDay(day);
            List<ManOverDueOrderAnalysisMongo> result = this.manOverDueOrderAnalysisDal.find(search);

            if(result.size() <= 0){
                continue;
            }

            ManOverDueOrderAnalysisMongo cellData = result.get(0);
            /*到期单数*/
            String dueOrderCount = result.get(0).getDueOrderCount();
            /*剩余未还单数*/
            Integer overdueOrderCount = this.orderOrderService.orderCountByRefundTimeStatus("8",day);
            /*应还逾期率*/
            BigDecimal overdueRate = new BigDecimal("0.00");
            if(overdueOrderCount == 0){
            }else{
                overdueRate = new BigDecimal(overdueOrderCount).divide(new BigDecimal(dueOrderCount),10,BigDecimal.ROUND_HALF_UP);
            }
            cellData.setOverDueRate(overdueRate.toString());
            cellData.setNotPaidCount(String.valueOf(overdueOrderCount));
            this.refreshOverDueOrder(cellData);
        }
    }

    /**
     * 数据app逾期订单还款数据统计更新*/
    @TargetDataSource(name="read")
    public void updateRepayDailyData() throws Exception {
        Integer dayCount = DateUtils.daysBetween("2018-03-01",DateUtils.dateToDay());
        for(int count = dayCount;count >= 0;count--) {
            String day = TimeUtil.getPastDate(count);
            logger.info("day = {}",day);

            ManOverDueOrderAnalysisMongo search = new ManOverDueOrderAnalysisMongo();
            search.setDueDay(day);
            List<ManOverDueOrderAnalysisMongo> result = this.manOverDueOrderAnalysisDal.find(search);

            if(result.size() <= 0){
                continue;
            }

            ManOverDueOrderAnalysisMongo cellData = result.get(0);
            Integer todayRepayCount = this.manOrderCountService.todayRepayOrderCountByRefundTime(day);
            cellData.setUpdateTime(new Date());
            cellData.setTodayRepay(String.valueOf(todayRepayCount));

            this.refreshOverDueOrder(cellData);
        }
    }

    /**
     * 添加初始化的数据*/
    public void addOverDueData(int dayCount) throws Exception {
        for(int count = dayCount;count >= 0;count--){
            /*到期日*/
            String day = TimeUtil.getPastDate(count);
            /*到期单数*/
            Integer dueOrderCount = this.manOrderCountService.countOrderExpireShouldPayDaily(day);
            /*剩余未还单数*/
            Integer overdueOrderCount = this.orderOrderService.orderCountByRefundTimeStatus("8",day);
            /*应还逾期率*/
            BigDecimal overdueRate = new BigDecimal("0.00");
            if(overdueOrderCount == 0){
            }else{
                overdueRate = new BigDecimal(overdueOrderCount).divide(new BigDecimal(dueOrderCount),10,BigDecimal.ROUND_HALF_UP);
            }

            /*催收阶段*/
            String stage = this.getOverDueStage(day);
            logger.info("day = {}",day);
            ManOverDueOrderAnalysisMongo addInfo = new ManOverDueOrderAnalysisMongo();
            addInfo.setDueDay(day);
            addInfo.setNotPaidCount(String.valueOf(overdueOrderCount));
            addInfo.setOverDueRate(overdueRate.toString());
            addInfo.setDueOrderCount(String.valueOf(dueOrderCount));
            addInfo.setUuid(UUIDGenerateUtil.uuid());
            addInfo.setCollectionStage(stage);
            this.insertOverDueOrderData(addInfo);
        }
    }

    /**
     * 分页查询逾期统计数据*/
    public List<ManOverDueOrderAnalysisMongo> getOverDueDataByPage(FindOrderForCompanyRequest request) throws Exception {
        Sort sort = new Sort(Sort.Direction.DESC,"_id");
        Integer pageStart = (request.getPageNo() - 1)*request.getPageSize();
        List<ManOverDueOrderAnalysisMongo> result = this.manOverDueOrderAnalysisDal.customFindByPage(new ManOverDueOrderAnalysisMongo(),
                sort,pageStart,request.getPageSize());
        return result;
    }

    /**
     * 修改一条记录*/
    public void refreshOverDueOrder(ManOverDueOrderAnalysisMongo data) throws Exception {
        this.manOverDueOrderAnalysisDal.updateById(data);
    }

    /**
     * 新增一条记录*/
    public void insertOverDueOrderData(ManOverDueOrderAnalysisMongo data) throws Exception {
        this.manOverDueOrderAnalysisDal.insert(data);
    }



    /**
     * 计算订单所处催收阶段*/
    public String getOverDueStage(String time) throws Exception {
        String stage = "";
        Integer day = DateUtils.daysBetween(time,DateUtils.dateToDay());
        if(day == 0){
            stage = "D0";
        }
        if(day <= 7 && day > 0){
            stage = "D1~D7";
        }
        if(day <= 15 && day >= 8){
            stage = "D8~D15";
        }
        if(day <= 30 && day >= 16){
            stage = "D16~D30";
        }
        if(day > 30){
            stage = "D30+";
        }
        return stage;
    }
}
