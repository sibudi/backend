package com.yqg.manage.service.order;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.utils.StringUtils;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.manage.service.user.SysPermissionService;
import com.yqg.manage.service.user.request.ManButtonPermissionRequest;
import com.yqg.order.dao.OrdBlackDao;
import com.yqg.order.entity.OrdBlack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Author Jacob
 */
@Component
public class ManOrderBlackService {

    private Logger logger = LoggerFactory.getLogger(ManOrderBlackService.class);

    @Autowired
    private OrdBlackDao ordeBlackDao;

    @Autowired
    private SysPermissionService sysPermissionService;
    /**
     * 初审复审加入黑名单
     * @param orderNo
     * @param responseMessage
     * @param ruleHitNo
     * @param ruleRealValue
     * @param userId
     * @param ruleValue
     * @param ruleRejectDay
     */
    public void addBackList(String orderNo, String responseMessage,
                            String ruleHitNo,String ruleRealValue,
                            String userId ,String ruleValue,
                            Integer ruleRejectDay) {
        try{
            OrdBlack ordBlack = new OrdBlack();
            ordBlack.setOrderNo(orderNo);
            ordBlack.setUserUuid(userId);
            ordBlack.setResponseMessage(responseMessage);
            ordBlack.setUuid(UUIDGenerateUtil.uuid());
            ordBlack.setRuleHitNo(ruleHitNo);
            ordBlack.setRuleRealValue(ruleRealValue);
            ordBlack.setRuleValue(ruleValue);
            ordBlack.setRuleRejectDay(ruleRejectDay);
            this.ordeBlackDao.insert(ordBlack);
        } catch (Exception e) {
            logger.info("审核异常订单号:"+orderNo+"异常信息:"+e);
        }
    }

    /**
     * 添加个disabled的备注
     */
    public void addBackListRemark(String orderNo, String remark,
                            String userId) {
        try{
            OrdBlack ordBlack = new OrdBlack();
            ordBlack.setOrderNo(orderNo);
            ordBlack.setUserUuid(userId);
            ordBlack.setRemark(remark);
            ordBlack.setUuid(UUIDGenerateUtil.uuid());
            ordBlack.setDisabled(1);
            this.ordeBlackDao.insert(ordBlack);
        } catch (Exception e) {
            logger.info("审核异常订单号:"+orderNo+"异常信息:"+e);
        }
    }
    /**
     * 查询是否拒绝原因（先查用户是否具有权限）
     * @param request
     * @return
     */
    public String searchRejectReason(ManButtonPermissionRequest request) throws Exception {

        if (request.getUserId() == null ||
                StringUtils.isEmpty(request.getOrderNo()) ||
                StringUtils.isEmpty(request.getPermissionCode()) ||
                StringUtils.isEmpty(request.getUserUuid())) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_PERMISSIONTREE_ERROR);
        }
        //没有权限直接返回空
        if (!sysPermissionService.hasButtonPermission(request)) {
            return "";
        }

        //查出拒绝原因
        OrdBlack ordBlack = new OrdBlack();
        ordBlack.setOrderNo(request.getOrderNo());
        ordBlack.setUserUuid(request.getUserUuid());
        ordBlack.setDisabled(0);
        List<OrdBlack> ordBlacks = ordeBlackDao.scan(ordBlack);
        if (CollectionUtils.isEmpty(ordBlacks)) {
            return "";
        }
        return ordBlacks.get(0).getResponseMessage();
    }
}
