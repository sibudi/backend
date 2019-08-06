package com.yqg.scheduling;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.externalChannel.service.Cash2OrderService;
import com.yqg.service.externalChannel.service.CheetahOrderService;
import com.yqg.service.externalChannel.utils.Cash2OrdStatusEnum;
import com.yqg.service.externalChannel.utils.CheetahOrdStatusEnum;
import com.yqg.service.order.OrdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
    public class OrderStatusCheckTask {
    @Autowired
    private OrdService ordService;

    @Autowired
    private Cash2OrderService cash2OrderService;
    @Autowired
    private CheetahOrderService cheetahOrderService;

    //检查待用户确认的订单(超过7天未确认则取消)

    @Scheduled(cron = "11 7 0 * * ?")
    public void checkNeedConfirmationLoan() {
        try {

            List<OrdOrder> checkList = ordService.getNeedConfirmationOrders();
            if (CollectionUtils.isEmpty(checkList)) {
                return;
            }
            for (OrdOrder order : checkList) {
                log.info("cancel order: {} with status: {}",order.getUuid(),order.getStatus());
                ordService.changeOrderStatus(order, OrdStateEnum.CANCEL);

                // 如果是cashcash的订单 反馈更新订单状态
                if (order.getThirdType() == 1) {
                    this.cash2OrderService.ordStatusFeedback(order, Cash2OrdStatusEnum.CANCLE);
                } else if (order.getThirdType() == 2) {
                    this.cheetahOrderService.ordStatusFeedback(order, CheetahOrdStatusEnum.CANCLE);
                }

            }
        } catch (Exception e) {
            log.error("cancel order error", e);
        }
    }
}
