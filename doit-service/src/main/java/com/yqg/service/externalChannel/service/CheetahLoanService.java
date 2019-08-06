package com.yqg.service.externalChannel.service;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.order.OrdStepTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.externalChannel.entity.ExternalOrderRelation;
import com.yqg.order.dao.OrdStepDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdStep;
import com.yqg.service.externalChannel.request.CheetahCheckLoanParam;
import com.yqg.service.externalChannel.request.CheetahLoanConfirmationParam;
import com.yqg.service.externalChannel.response.CheetahResponse;
import com.yqg.service.externalChannel.utils.CheetahOrdStatusEnum;
import com.yqg.service.externalChannel.utils.CheetahResponseBuilder;
import com.yqg.service.externalChannel.utils.CheetahResponseCode;
import com.yqg.service.order.OrdService;
import com.yqg.service.user.service.UsrBaseInfoService;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.dao.UsrBankDao;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.entity.UsrBank;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghuaizhou on 2018/12/27.
 */
@Service
@Slf4j
public class CheetahLoanService {

    @Autowired
    private OrdService ordService;

    @Autowired
    private ExternalChannelDataService externalChannelDataService;

    @Autowired
    private OrdStepDao ordStepDao;

    @Autowired
    private UsrBaseInfoService usrBaseInfoService;

    @Autowired
    private UsrBankDao usrBankDao;

    @Autowired
    private UsrService usrService;

    @Autowired
    private CheetahOrderService cheetahOrderService;

    @Autowired
    private UsrDao usrDao;

    public CheetahResponse checkLoanable(CheetahCheckLoanParam param)
            throws Exception {

        if ( StringUtils.isEmpty(param.getMobile())) {
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.NOT_ALLOW_LOAN_ERROR_11000).withMessage("手机号码为空");
        }
        String mobile = CheakTeleUtils.telephoneNumberValid2(param.getMobile());
        if (StringUtils.isEmpty(mobile)) {
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.NOT_ALLOW_LOAN_ERROR_11000).withMessage("手机号格式不对");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("is_reloan", 0);
        try {
            UsrUser searchInfo = new UsrUser();
            searchInfo.setMobileNumberDES(DESUtils.encrypt(mobile));
            List<UsrUser> userList = usrService.getUserInfo(searchInfo);
            if (!CollectionUtils.isEmpty(userList)) {
                //   用户已存在
                UsrUser user = userList.get(0);
                //  自己的用户
                if (user.getDisabled() == 1){
                    return CheetahResponseBuilder.buildResponse(
                            CheetahResponseCode.NOT_ALLOW_LOAN_ERROR_11000).withMessage("用户被拉黑");
                }
                //是否能够贷款
                ordService.checkUserLoanable(user.getUuid());

            } else {
                //新用户
                return CheetahResponseBuilder.buildResponse(
                        CheetahResponseCode.CODE_OK_0);
            }

        }catch (ServiceException e) {
            if (e.getErrorCode() == ExceptionEnum.ORDER_UN_FINISH.getCode()) {
                //未完成订单
                return CheetahResponseBuilder.buildResponse(
                        CheetahResponseCode.NOT_ALLOW_LOAN_ERROR_11000).withMessage("用户有未完成订单");
            } else if (e.getErrorCode() == ExceptionEnum.NOT_ARRVIE_DAY.getCode()) {
                //被拒
                return CheetahResponseBuilder.buildResponse(
                        CheetahResponseCode.NOT_ALLOW_LOAN_ERROR_11000).withMessage("用户仍在拒绝期限内");
            }
            log.error("check user loanable error", e);
        } catch (Exception e) {
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.SERVER_INTERNAL_ERROR_3001);
        }
        return CheetahResponseBuilder.buildResponse(
                CheetahResponseCode.CODE_OK_0);
    }

    @Transactional(rollbackFor = Exception.class)
    public CheetahResponse confirmApplication(CheetahLoanConfirmationParam confirmationParam)
            throws Exception {
        if (StringUtils.isEmpty(confirmationParam.getOrderId())) {
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.PARAM_ERROR_1001);
        }
        ExternalOrderRelation relation = externalChannelDataService
                .getExternalOrderRelationByExternalOrderNo(confirmationParam.getOrderId());
        if (relation == null || StringUtils.isEmpty(relation.getOrderNo())) {
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.ORDER_NOT_EXIT_14001);
        }

        OrdOrder ordOrder = ordService.getOrderByOrderNo(relation.getOrderNo());
        if (ordOrder == null) {
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.ORDER_NOT_EXIT_14001).withData("the order cannot find");
        }

        //防止信息推送不完整就调用贷款确认了，增加一个控制
        List<OrdStep> ordSteps = ordStepDao.getOrderSteps(ordOrder.getUuid(), 5);
        if(CollectionUtils.isEmpty(ordSteps)){
            log.info("no linkman step , externalOrderNo: {}",confirmationParam.getOrderId());
            //没有联系人步骤
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.ORDER_STATES_ERROR_1002).withMessage(ExceptionEnum.ORDER_STATES_ERROR.getMessage());
        }

//        //金额是否有变化：
//        if (ordOrder.getAmountApply().compareTo(confirmationParam.getApplicationAmount()) == 0
//                && ordOrder.getBorrowingTerm() == confirmationParam.getApplicationTerm()) {
//            //无变化
//        } else {
//            //更新订单金额
//            ordService.updateOrderAmountAndTerm(ordOrder.getUuid(),
//                    confirmationParam.getApplicationAmount(), confirmationParam.getApplicationTerm());
//
//        }

        // 改变订单表：状态变为待机审 2
        //取当前绑定的银行卡
        UsrBank bankInfoSearch = new UsrBank();
        bankInfoSearch.setUserUuid(ordOrder.getUserUuid());
        bankInfoSearch.setIsRecent(1);
        bankInfoSearch.setDisabled(0);
        //bankInfoSearch.setThirdType(1);
        List<UsrBank> bankcardList = usrBankDao.scan(bankInfoSearch);
        if (CollectionUtils.isEmpty(bankcardList)) {
            return CheetahResponseBuilder.buildResponse(
                    CheetahResponseCode.ORDER_INFO_NOT_COMPLETE_14002).withData("the bank cannot find");
        }
        UsrBank usrBank = new UsrBank();
        usrBank.setUuid(bankcardList.get(0).getUuid());
        usrBaseInfoService.updateOrderState(ordOrder.getUuid(), ordOrder.getUserUuid(),
                OrdStateEnum.MACHINE_CHECKING.getCode(), usrBank);

        // 改变订单表：步骤变为银行卡信息 7
        usrBaseInfoService.updateOrderStep(ordOrder.getUuid(), ordOrder.getUserUuid(),
                OrdStepTypeEnum.BANK_INFO.getType());

        //反馈更新订单状态
        cheetahOrderService
                .ordStatusFeedback(ordOrder, CheetahOrdStatusEnum.IN_CHECKING);

        return CheetahResponseBuilder.buildResponse(CheetahResponseCode.CODE_OK_0);

    }
}
