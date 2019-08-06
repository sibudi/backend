package com.yqg.service.externalChannel.service;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.order.OrdStepTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.user.UserSourceEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.externalChannel.entity.ExternalOrderRelation;
import com.yqg.order.dao.OrdStepDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdStep;
import com.yqg.service.externalChannel.request.Cash2LoanConfirmationParam;
import com.yqg.service.externalChannel.request.Cash2LoanableCheckParam;
import com.yqg.service.externalChannel.response.Cash2Response;
import com.yqg.service.externalChannel.utils.Cash2OrdStatusEnum;
import com.yqg.service.externalChannel.utils.Cash2ResponseBuiler;
import com.yqg.service.externalChannel.utils.Cash2ResponseCode;
import com.yqg.service.order.OrdService;
import com.yqg.service.user.service.UsrBaseInfoService;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.dao.UsrBankDao;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.entity.UsrBank;
import com.yqg.user.entity.UsrUser;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/7
 * @Email zengxiangcai@yishufu.com
 *
 ****/
@Service
@Slf4j
public class Cash2LoanService {

    @Autowired
    private UsrService usrService;

    @Autowired
    private OrdService ordService;

    @Autowired
    private ExternalChannelDataService externalChannelDataService;

    @Autowired
    private UsrBaseInfoService usrBaseInfoService;

    @Autowired
    private UsrBankDao usrBankDao;

    @Autowired
    private Cash2OrderService cash2OrderService;

    @Autowired
    private UsrDao usrDao;

    @Autowired
    private OrdStepDao ordStepDao;

    /***
     * 检查用户是否可贷款，如果可贷款检查是复借还是初借
     * @param param
     * @return
     */
    public Cash2Response checkLoanable(Cash2LoanableCheckParam param) {

        // 当用户首次借款时，无法获取用户身份号码
        if ( StringUtils.isEmpty(param.getMobile())) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.PARAM_EMPTY_1001);
        }
        String mobile = CheakTeleUtils.telephoneNumberValid2(param.getMobile());
        if (StringUtils.isEmpty(mobile)) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.PARAM_TYPE_ERROR_1002).withMessage("手机号格式不对");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("is_reloan", 0);
        try {
            Cash2CheckResult cash2CheckResult = checkCash2User(mobile, param.getIdcard());

            switch (cash2CheckResult.type) {
                case MOBILE_ALREADY_EXIT_WITH_CASH2:
                case MOBILE_ALREADY_EXIT:
                    UsrUser usrUser = cash2CheckResult.getUser();
                    if (cash2CheckResult.getUser().getDisabled() == 1){
                        usrUser.setThirdHit(1);
                        //this.usrService.updateUser(usrUser);
                        return Cash2ResponseBuiler.buildResponse(
                                Cash2ResponseCode.CAN_NOT_BORROW_FOR_USER_IN_BLACKLIST_11004);
                    }
                    //老用户检查是否有订单
                    //是否有在途贷款
                    try {
                        ordService.checkUserLoanable(cash2CheckResult.getUser().getUuid());
                    } catch (ServiceException e) {
                        if (e.getErrorCode() == ExceptionEnum.ORDER_UN_FINISH.getCode()) {
                            //未完成订单
                            usrUser.setThirdHit(2);
                           // this.usrService.updateUser(usrUser);
                            return Cash2ResponseBuiler.buildResponse(
                                Cash2ResponseCode.CAN_NOT_BORROW_FOR_EXIST_LOAN_11000);
                        } else if (e.getErrorCode() == ExceptionEnum.NOT_ARRVIE_DAY.getCode()) {
                            //被拒
                            usrUser.setThirdHit(3);
                           // this.usrService.updateUser(usrUser);
                            return Cash2ResponseBuiler.buildResponse(
                                Cash2ResponseCode.CAN_NOT_BORROW_FOR_REJECT_11002);
                        }
                        log.error("check user loanable error", e);
                    }
                    //检查是否复借
                    OrdOrder lastSettledLoan = ordService
                        .getLastSettledLoan(cash2CheckResult.getUser().getUuid());
                    data.put("is_reloan", lastSettledLoan == null ? 0 : 1);
                    if (lastSettledLoan != null) {
                        return Cash2ResponseBuiler.buildResponse(
                            Cash2ResponseCode.CODE_OK_1).withData(data);
                    }

                    break;
//                case MOBILE_ALREADY_EXIT:
//                    return Cash2ResponseBuiler.buildResponse(
//                        Cash2ResponseCode.CAN_NOT_BORROW_FOR_USER_INFO_MISMATCH_11003);

                case NEW_USER:
                    return Cash2ResponseBuiler.buildResponse(
                        Cash2ResponseCode.CODE_OK_1).withData(data);

            }

        } catch (Exception e) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.SERVER_INTERNAL_ERROR_9001);
        }
        return Cash2ResponseBuiler.buildResponse(
            Cash2ResponseCode.CODE_OK_1).withData(data);

    }


    @Getter
    @Setter
    @AllArgsConstructor
    public static class Cash2CheckResult {

        private Cash2UserCheckResultType type;
        private UsrUser user;
    }

    public enum Cash2UserCheckResultType {
        NEW_USER,//新用戶
//        EXIST_MOBILE_NAME_IDCARD_MISMATCH,//手机号存在，用户名身份证号不匹配
//        EXIST_IDCARD_MOBILE_MISMATCH,//身份证号存在，但是手机号不匹配
//        EXIST_USER_MATCH,//手机号身份证号都匹配
//        EXIST_USER_NO_IDCARD;//有手机号，但是无身份证姓名信息
        MOBILE_ALREADY_EXIT,
        MOBILE_ALREADY_EXIT_WITH_CASH2;//手机号已存在
    }


    private Cash2CheckResult checkCash2User(String mobile, String idCard) {
        //1、按照手机号查询
        UsrUser searchInfo = new UsrUser();
        searchInfo.setMobileNumberDES(DESUtils.encrypt(mobile));
        List<UsrUser> userList = usrService.getUserInfo(searchInfo);
        if (!CollectionUtils.isEmpty(userList)) {
            //   用户已存在
            UsrUser user = userList.get(0);
            if (user.getUserSource() == Integer.valueOf(UserSourceEnum.CashCash.getCode())){
                // 是cashcash那边的用户
                return new Cash2CheckResult(Cash2UserCheckResultType.MOBILE_ALREADY_EXIT_WITH_CASH2, user);
            }
            return new Cash2CheckResult(Cash2UserCheckResultType.MOBILE_ALREADY_EXIT, user);
        } else {
            //新用户
            return new Cash2CheckResult(Cash2UserCheckResultType.NEW_USER, null);
        }

    }


    @Transactional(rollbackFor = Exception.class)
    public Cash2Response confirmApplication(Cash2LoanConfirmationParam confirmationParam)
        throws Exception {
        if (StringUtils.isEmpty(confirmationParam.getOrderNo())) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.ORDER_NO_EMPTY);
        }
        ExternalOrderRelation relation = externalChannelDataService
            .getExternalOrderRelationByExternalOrderNo(confirmationParam.getOrderNo());
        if (relation == null || StringUtils.isEmpty(relation.getOrderNo())) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.ORDER_NO_ERROR);
        }

        OrdOrder ordOrder = ordService.getOrderByOrderNo(relation.getOrderNo());
        if (ordOrder == null) {
            return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.ORDER_NO_ERROR).withData("the order cannot find");
        }

        //防止信息推送不完整就调用贷款确认了，增加一个控制
        List<OrdStep> ordSteps = ordStepDao.getOrderSteps(ordOrder.getUuid(), 5);
        if(CollectionUtils.isEmpty(ordSteps)){
            log.info("no linkman step , externalOrderNo: {}",confirmationParam.getOrderNo());
            //没有联系人步骤
            return Cash2ResponseBuiler.buildResponse(
                    Cash2ResponseCode.DATA_VERIFY_ERROR).withMessage(ExceptionEnum.ORDER_STATES_ERROR.getMessage());

        }

        //金额是否有变化：
        if (ordOrder.getAmountApply().compareTo(confirmationParam.getApplicationAmount()) == 0
            && ordOrder.getBorrowingTerm() == confirmationParam.getApplicationTerm()) {
            //无变化
        } else {
            //更新订单金额
            ordService.updateOrderAmountAndTerm(ordOrder.getUuid(),
                confirmationParam.getApplicationAmount(), confirmationParam.getApplicationTerm());

        }

        // 改变订单表：状态变为待机审 2
        //取当前绑定的银行卡
        UsrBank bankInfoSearch = new UsrBank();
        bankInfoSearch.setUserUuid(ordOrder.getUserUuid());
        bankInfoSearch.setIsRecent(1);
        bankInfoSearch.setDisabled(0);
        //bankInfoSearch.setThirdType(1);
        List<UsrBank> bankcardList = usrBankDao.scan(bankInfoSearch);
        if (CollectionUtils.isEmpty(bankcardList)) {
            throw new IllegalArgumentException("cannot get recent valid bank card info");
        }
        UsrBank usrBank = new UsrBank();
        usrBank.setUuid(bankcardList.get(0).getUuid());
        usrBaseInfoService.updateOrderState(ordOrder.getUuid(), ordOrder.getUserUuid(),
            OrdStateEnum.MACHINE_CHECKING.getCode(), usrBank);

        // 改变订单表：步骤变为银行卡信息 7
        usrBaseInfoService.updateOrderStep(ordOrder.getUuid(), ordOrder.getUserUuid(),
            OrdStepTypeEnum.BANK_INFO.getType());

        //反馈更新订单状态
//        this.cash2OrderService.ordStatusFeedback(ordOrder, Cash2OrdStatusEnum.IN_CHECKING);

        return Cash2ResponseBuiler.buildResponse(Cash2ResponseCode.CODE_OK_1);

    }
}
