package com.yqg.service.risk.service;

import com.yqg.common.utils.StringUtils;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.dao.OrderScoreDao;
import com.yqg.risk.entity.OrderScore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Author: tonggen
 * Date: 2019/4/30
 * time: 11:16 AM
 */
@Service
@Slf4j
public class OrderModelScoreService {

    @Autowired
    private OrderScoreDao orderScoreDao;

    @Autowired
    private OrdDao ordDao;

    /**
     * 通过userUuid 获得模型评分
     * @param userUuid
     * @return
     */
    public BigDecimal getScoreByOrderNo(String userUuid) {

        if (StringUtils.isEmpty(userUuid)) {
            log.error("getScoreByOrderNo's userUuid is null!");
            return BigDecimal.ZERO;
        }
        //查询出需要显示分数的订单 ，没有直接返回0.
        Optional<OrdOrder> order = getFirstBrowingOrd(userUuid);
        OrdOrder ordOrder;
        if (order.isPresent()) {
            ordOrder = order.get();
        } else {
            return BigDecimal.ZERO;
        }

        String orderNo = ordOrder.getUuid();
        //查询顺序 50 -》 100 -》 600 -》order表 -》默认值
        BigDecimal result = getScoreByType(orderNo, "PRODUCT_50");
        if (result == null) {
            result = getScoreByType(orderNo, "PRODUCT_100");
            if (result == null) {
                result = getScoreByType(orderNo, "PRODUCT_600");
            }
        }

        if (result == null) {
            result = ordOrder.getScore();
        }
        //使用平均分默认值
        if (result == null || result.equals(BigDecimal.ZERO)) {
            result = BigDecimal.valueOf(509.24);
        }
        return result;
    }

    /**
     * 通过ModelName查询不同分数
     * @param orderNo
     * @param modelName
     * @return
     */
    private BigDecimal getScoreByType(String orderNo, String modelName) {

        OrderScore orderScore = new OrderScore();
        orderScore.setDisabled(0);
        orderScore.setOrderNo(orderNo);
        orderScore.setModelName(modelName);
        List<OrderScore> lists = orderScoreDao.scan(orderScore);
        if (CollectionUtils.isEmpty(lists)) {
            return null;
        }
        return lists.get(0).getTotalScore();
    }

    /**
     * 通过用户Uuid查询用户首借的订单
     * @param userUuid
     * @return
     */
    public Optional<OrdOrder> getFirstBrowingOrd(String userUuid) {

        OrdOrder ordOrder = new OrdOrder();
        ordOrder.setDisabled(0);
        ordOrder.setUserUuid(userUuid);
        ordOrder.setBorrowingCount(1);
        ordOrder.set_orderBy(" createTime desc ");
        List<OrdOrder> orderList = ordDao.scan(ordOrder);
        if (CollectionUtils.isEmpty(orderList)) {
            return Optional.empty();
        }
        return orderList.stream().findFirst();

    }

    public OrderScore getLatestScoreWithModel(String orderNo, String scoreModelName) {
        OrderScore dbScore = orderScoreDao.getLatestResult(orderNo, scoreModelName);
        return dbScore;
    }
}
