package com.yqg.task;


import com.yqg.service.RiskErrorLogService;
import com.yqg.service.RiskService;
import com.yqg.service.RuleExecuteSourceThreadLocal;
import com.yqg.risk.entity.RiskErrorLog;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdService;
import com.yqg.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;


@Service
@Slf4j
public class TaxNumberScheduler {

    public static final int TAX_NUMBER_MAX_RE_RUN_TIMES = 2;
    @Autowired
    private RiskErrorLogService riskErrorLogService;

    @Autowired
    private RiskService riskService;

    @Autowired
    private OrdService ordService;


    //税卡异常重跑
    @Scheduled(cron = "3 13/5 0-7 * * ?")
    public void reRun() {

        List<RiskErrorLog> errorLogs = riskErrorLogService.getTaxNumberNeedRetryOrders();
        if (CollectionUtils.isEmpty(errorLogs)) {
            log.warn("tax number need retry list is empty");
        }
        for (RiskErrorLog errorLog : errorLogs) {
            try {
                LogUtils.addMDCRequestId(errorLog.getOrderNo());
                RuleExecuteSourceThreadLocal.setSource(RuleExecuteSourceThreadLocal.ExecuteSourceEnum.TAX_RERUN);

                log.info("start reRun taxNumber  verify order... ,orderNo: {}", errorLog.getOrderNo());
                //检查是否超过最大重试次数
                if (errorLog.getTimes() != null && errorLog.getTimes() >= TAX_NUMBER_MAX_RE_RUN_TIMES) {
                    log.info("order: {} exceed max retry times", errorLog.getOrderNo());
                    riskErrorLogService.disabledErrorLogWithRemark(errorLog.getId(), "exceed max retry times: " + errorLog.getTimes());
                    continue;
                }

                //查询订单：
                OrdOrder order = ordService.getOrderByOrderNo(errorLog.getOrderNo());
                if (order == null) {
                    log.info("the order is empty, orderNo: {}", errorLog.getOrderNo());
                    riskErrorLogService.disabledErrorLog(errorLog.getId());
                    continue;
                }
                if (order.getStatus() != 2) {
                    log.info("the order status is not correct, orderNo: {}", order.getUuid());
                    riskErrorLogService.disabledErrorLog(errorLog.getId());
                    continue;
                }
                if (order.getOrderStep() != 7 && order.getOrderStep() != 8) {
                    log.info("the order step is not correct, orderNo: {}, step: {}", order.getUuid(), order.getOrderStep());
                    riskErrorLogService.disabledErrorLog(errorLog.getId());
                    continue;
                }
                //更新订单为disabled
                riskErrorLogService.disabledErrorLog(errorLog.getId());
                //重跑
                riskService.caculate(Arrays.asList(order));
            }catch (Exception e){
                log.info("re run tax error", e);
            } finally {
                RuleExecuteSourceThreadLocal.remove();
                LogUtils.removeMDCRequestId();
            }
        }

    }

}
