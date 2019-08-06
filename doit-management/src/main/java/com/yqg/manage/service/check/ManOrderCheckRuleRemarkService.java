package com.yqg.manage.service.check;


import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.manage.dal.order.ManOrderCheckRuleConfigDao;
import com.yqg.manage.dal.order.ManOrderCheckRuleRemarkDao;
import com.yqg.manage.dal.order.ManOrderOrderDao;
import com.yqg.manage.entity.check.ManOrderCheckRule;
import com.yqg.manage.entity.check.ManOrderCheckRuleConfig;
import com.yqg.manage.entity.check.ManOrderCheckRuleRemark;
import com.yqg.manage.enums.ManOrderCheckRuleEnum;
import com.yqg.manage.service.check.request.OrderCheckBase;
import com.yqg.manage.service.check.request.OrderCheckRemark;
import com.yqg.manage.service.check.request.OrderCheckRule;
import com.yqg.manage.service.user.ManUserService;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.third.Inforbip.Enum.CallReusltEnum;
import com.yqg.system.dao.TeleCallResultDao;
import com.yqg.system.entity.TeleCallResult;
import com.yqg.user.dao.UsrAttachmentInfoDao;
import com.yqg.user.dao.UsrCertificationInfoDao;
import com.yqg.user.entity.UsrAttachmentInfo;
import com.yqg.user.entity.UsrCertificationInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alan
 */
@Component
public class ManOrderCheckRuleRemarkService {
    @Autowired
    private ManOrderCheckRuleRemarkDao manOrderCheckRuleRemarkDao;

    @Autowired
    private ManUserService manUserService;

    @Autowired
    private ManOrderCheckRuleService manOrderCheckRuleService;

    @Autowired
    private UsrCertificationInfoDao usrCertificationInfoDao;

    @Autowired
    private ManOrderOrderDao manOrderOrderDao;

    @Autowired
    private UsrAttachmentInfoDao usrAttachmentInfoDao;

    @Autowired
    private TeleCallResultDao teleCallResultDao;

    @Autowired
    private ManOrderCheckRuleConfigDao manOrderCheckRuleConfigDao;

    private static Logger logger = LoggerFactory.getLogger(ManOrderCheckRuleRemarkService.class);

    /**
     * 通过订单编号添加修改审核规则历史记录表*/
    @Transactional(rollbackFor = Exception.class)
    public void editManOrderCheckRule(OrderCheckBase request) throws ServiceExceptionSpec {

        String orderNo = request.getOrderNo();
        String sessionId = request.getSessionId();
        if(StringUtils.isEmpty(orderNo) || StringUtils.isEmpty(sessionId)){
            logger.info("editManOrderCheckRule 的参数 {}",request);
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }

        Integer userId = this.manUserService.getUserIdBySession(sessionId);

        //审核规则数组
        List<OrderCheckRule> checkRules = request.getCheckRuleList();
        //审核规则备注
        OrderCheckRemark checkRemark = request.getCheckRuleRemark();
        //审核信息模块类型
        Integer infoType = request.getInfoType();

        //校验(类型为8和9时，规则审核数组可以为空)
        if(userId == null || (CollectionUtils.isEmpty(checkRules)
                && (infoType != ManOrderCheckRuleEnum.ADDINFO_COMMON.getCode() &&
                infoType != ManOrderCheckRuleEnum.ADDINFO_STUDENT.getCode()))
                || infoType == null){
            logger.info("editManOrderCheckRule 的userId:{}, checkRules:{}, infoType:{}",
                    userId, checkRules, infoType);
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }

        //备注不为空则修改订单审核规则备注
        if(checkRemark != null){
            //查询是否有审核规则备注
            ManOrderCheckRuleRemark checkRemarkResult = this.orderCheckRuleRemark(orderNo,infoType);
            //没有审核备注则添加，有则修改
            if(checkRemarkResult == null){
                this.addOrderCheckRuleRemark(checkRemark,orderNo,userId,infoType);
            }else{
                this.editOrderCheckRuleRemark(checkRemarkResult,checkRemark.getRemark(),userId);
            }
        }

        if (CollectionUtils.isEmpty(checkRules)) {
            return;
        }
        //通过orderNo和infoType查询审核规则历史记录
        List<ManOrderCheckRule> checkRuleList = this.manOrderCheckRuleService.manOrderCheckRuleList(orderNo,infoType);

        //没有审核规则历史则添加，有则对比修改
        if(checkRuleList == null){
            for(OrderCheckRule item:checkRules){
                //如果没有选中，且填写备注，则类型变成D类
                if (checkRemark != null && StringUtils.isNotBlank(checkRemark.getRemark())
                        && item.getRuleResult() == false) {
                    item.setRuleLevel(4);
                }
                this.manOrderCheckRuleService.addOrderCheckRule(item,orderNo,userId,infoType, item.getDescription(),
                        item.getDescriptionInn());

            }
        }else {
            for(OrderCheckRule item:checkRules){
                boolean flag = false;
                ManOrderCheckRule tempData = null;
                for (ManOrderCheckRule cell:checkRuleList){
                    if(cell.getRuleCount() == item.getRuleCount()){
                        flag = true;
                        tempData = cell;
                    }
                }
                //如果没有选中，且填写备注，则类型变成D类
                if (flag) {
                    if (checkRemark != null && StringUtils.isNotBlank(checkRemark.getRemark())
                            && item.getRuleResult() == false) {
                        this.manOrderCheckRuleService.editOrderCheckRule(tempData,item.getRuleResult()
                                ,userId, item.getDescription(), item.getDescriptionInn(), 4);
                    } else {
                        this.manOrderCheckRuleService.editOrderCheckRule(tempData,item.getRuleResult(),
                                userId, item.getDescription(), item.getDescriptionInn(), item.getRuleLevel());
                    }

                } else {
                    if (checkRemark != null && StringUtils.isNotBlank(checkRemark.getRemark())
                            && item.getRuleResult() == false) {
                        item.setRuleLevel(4);
                    }
                    this.manOrderCheckRuleService.addOrderCheckRule(item,orderNo,userId,infoType,
                            item.getDescription(), item.getDescriptionInn());
                }
            }
        }
    }

    /**
     * 通过订单编号查询审核规则历史记录信息 (先去查询记录表 若没有则从配置表中查询）
     *
     * */
    public OrderCheckBase manOrderCheckRuleInfo(OrderCheckBase request) throws ServiceExceptionSpec {

        OrderCheckBase response = new OrderCheckBase();
        String orderNo = request.getOrderNo();
        Integer infoType = request.getInfoType();
        if(StringUtils.isEmpty(orderNo) || infoType == null){
            logger.info("manOrderCheckRuleInfo 的 orderNo:{}, infoType:{}",
                    orderNo, infoType);
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }

        ManOrderCheckRuleRemark checkRemarkResult = this.orderCheckRuleRemark(orderNo,infoType);   //查询审核规则备注
        OrderCheckRemark checkRemark = new OrderCheckRemark();

        if (checkRemarkResult == null) {
            checkRemark.setRemark("");
        } else {
            checkRemark.setRemark(checkRemarkResult.getRemark());
        }

        //通过orderNo和infoType查询审核规则历史记录
        List<ManOrderCheckRule> checkRuleList = this.manOrderCheckRuleService.manOrderCheckRuleList(orderNo,infoType);
        //如果没有查询到记录 从配置表获取
        if (CollectionUtils.isEmpty(checkRuleList)) {
            List<ManOrderCheckRuleConfig> lists = manOrderCheckRuleConfigDao.listRuleConfig(infoType);
            if (CollectionUtils.isEmpty(lists)) {
//                throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
                return null;
            }
            checkRuleList = new ArrayList<>();
            for (ManOrderCheckRuleConfig elem : lists) {
                ManOrderCheckRule manOrderCheckRule = new ManOrderCheckRule();
                manOrderCheckRule.setRuleCount(elem.getRuleCount());
                manOrderCheckRule.setRuleLevel(elem.getRuleLevel());
                manOrderCheckRule.setRuleResult(0);
                manOrderCheckRule.setInfoType(elem.getInfoType());
                manOrderCheckRule.setType(elem.getType());
                manOrderCheckRule.setDescriptionInn(elem.getDescriptionInn());
                manOrderCheckRule.setDescription(elem.getDescription());
                checkRuleList.add(manOrderCheckRule);
            }
        }

        List<OrderCheckRule> checkRules = new ArrayList<>();
        if(!CollectionUtils.isEmpty(checkRuleList)){
            for(ManOrderCheckRule item:checkRuleList){
                OrderCheckRule cell = new OrderCheckRule();
                cell.setRuleCount(item.getRuleCount());
                cell.setRuleLevel(item.getRuleLevel());
                cell.setDescription(item.getDescription());
                cell.setDescriptionInn(item.getDescriptionInn());
                cell.setType(item.getType());
                if(item.getRuleResult() == 1){  //true为1命中审核规则，false为0
                    cell.setRuleResult(true);
                }else {
                    cell.setRuleResult(false);
                }
                checkRules.add(cell);
            }
        }

        response.setCheckRuleList(checkRules);
        response.setCheckRuleRemark(checkRemark);
        response.setInfoType(infoType);
        response.setOrderNo(orderNo);
        return response;
    }

    /**
     * 查询审核规则备注*/
    public ManOrderCheckRuleRemark orderCheckRuleRemark(String orderNo,Integer infoType) throws ServiceExceptionSpec {
        ManOrderCheckRuleRemark search = new ManOrderCheckRuleRemark();
        search.setOrderNo(orderNo);
        search.setInfoType(infoType);
        search.setDisabled(0);
        List<ManOrderCheckRuleRemark> remarkList = this.manOrderCheckRuleRemarkDao.scan(search);
        if(!CollectionUtils.isEmpty(remarkList)){
            return remarkList.get(0);
        }
        return null;
    }

    /**
     * 添加审核规则备注*/
    public void addOrderCheckRuleRemark(OrderCheckRemark checkRemark,String orderNo,Integer updateUser,Integer infoType) throws ServiceExceptionSpec {
        ManOrderCheckRuleRemark addInfo = new ManOrderCheckRuleRemark();
        addInfo.setInfoType(infoType);
        addInfo.setOrderNo(orderNo);
        addInfo.setUuid(UUIDGenerateUtil.uuid());
        addInfo.setUpdateUser(updateUser);
        addInfo.setRemark(checkRemark.getRemark());
        this.manOrderCheckRuleRemarkDao.insert(addInfo);
    }

    /**
     * 修改审核规则备注*/
    public void editOrderCheckRuleRemark(ManOrderCheckRuleRemark checkRemark,String remark,Integer updateUser) throws ServiceExceptionSpec {
        ManOrderCheckRuleRemark editInfo = new ManOrderCheckRuleRemark();
        editInfo.setUuid(checkRemark.getUuid());
        editInfo.setRemark(remark);
        editInfo.setUpdateUser(updateUser);
        this.manOrderCheckRuleRemarkDao.update(editInfo);
    }

    /**
     * 通过订单编号判断是否命中有保险卡规则
     * @param orderNo
     */
    public Boolean onlyRealNameVerifyFailedWithInsuranceCard(String orderNo) {

        if (StringUtils.isEmpty(orderNo)) {
            return false;
        }
        //通过订单号查询用户useruuid
        String userUuid = getUserUuid(orderNo);
        if (StringUtils.isEmpty(userUuid)) {
            return false;
        }

        //查询是否实名认证通过
        Boolean flag = judgeTrueName(userUuid);
        if (flag) {
            return false;
        }
        //判断是否含有附件图片
        return judgeAttachPict(userUuid, UsrAttachmentEnum.INSURANCE_CARD.getType());
    }

    /**
     * 通过订单编号判断是否命中有家庭卡规则
     * @param orderNo
     */
    public Boolean onlyRealNameVerifyFailedWithFamilyCard(String orderNo) {

        if (StringUtils.isEmpty(orderNo)) {
            return false;
        }
        //通过订单号查询用户useruuid
        String userUuid = getUserUuid(orderNo);
        if (StringUtils.isEmpty(userUuid)) {
            return false;
        }

        //查询是否实名认证通过
        Boolean flag = judgeTrueName(userUuid);
        if (flag) {
            return false;
        }
        //判断是否含有附件图片
        return judgeAttachPict(userUuid, UsrAttachmentEnum.KK.getType());
    }

    /**
     * 通过订单编号判断是否需要goole电话
     * @param orderNo
     */
    public Boolean isUserHitGooglePhone(String orderNo) {

        if (StringUtils.isEmpty(orderNo)) {
            return false;
        }
        //通过订单号查询公司外呼记录
        TeleCallResult teleCallResult = new TeleCallResult();
        teleCallResult.setDisabled(0);
        teleCallResult.setCallType(TeleCallResult.CallTypeEnum.COMPANY.getCode());
        teleCallResult.setOrderNo(orderNo);
        teleCallResult.set_orderBy("createTime desc");
        List<TeleCallResult> lists = teleCallResultDao.scan(teleCallResult);
        if (CollectionUtils.isEmpty(lists)) {
            return false;
        }
        Integer callResult = lists.get(0).getCallResult();
        if (callResult != null && (callResult.equals(CallReusltEnum.NUMBER_NOT_EXIST.getCode())
                || callResult.equals(CallReusltEnum.INVALID_NUMBER.getCode()))) {
            return true;
        }
        return false;
    }

    private String getUserUuid (String orderNo) {

        OrdOrder ordOrder = new OrdOrder();
        ordOrder.setDisabled(0);
        ordOrder.setUuid(orderNo);
        List<OrdOrder> ordOrders = manOrderOrderDao.scan(ordOrder);

        if (CollectionUtils.isEmpty(ordOrders)) {
            return null;
        }
        return ordOrders.get(0).getUserUuid();
    }

    /**
     * 判断实名认证是否通过
     */
    private Boolean judgeTrueName(String userUuid) {

        List<UsrCertificationInfo> usrCertificationInfos = usrCertificationInfoDao.listCertificationInfo(userUuid);
        if (!CollectionUtils.isEmpty(usrCertificationInfos)) {
            if (usrCertificationInfos.get(0).getCertificationResult() == 1
                    || (usrCertificationInfos.size() > 1 && usrCertificationInfos.get(1).getCertificationResult() == 1)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是否有附件图片
     */
    private Boolean judgeAttachPict(String userUuid, Integer type) {
        UsrAttachmentInfo usrAttachmentInfo = new UsrAttachmentInfo();
        usrAttachmentInfo.setDisabled(0);
        usrAttachmentInfo.setAttachmentType(type);
        usrAttachmentInfo.setUserUuid(userUuid);
        List<UsrAttachmentInfo> lists = usrAttachmentInfoDao.scan(usrAttachmentInfo);
        if (CollectionUtils.isEmpty(lists)) {
            return false;
        }
        return true;
    }
}
