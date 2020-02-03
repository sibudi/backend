package com.yqg.service.activity;

import com.alibaba.fastjson.JSONObject;
import com.yqg.activity.dao.ActivityAccountRecordDao;
import com.yqg.activity.dao.UsrActivityBankDao;
import com.yqg.activity.entity.ActivityAccountRecord;
import com.yqg.activity.entity.UsrActivityBank;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.user.UsrBankCardBinEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdPaymentCodeDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdPaymentCode;
import com.yqg.service.pay.RepayService;
import com.yqg.service.pay.request.RepayRequest;
import com.yqg.service.third.kaBinCheck.KaBinCheckService;
import com.yqg.service.user.request.InsertOrderPaymentCodeRequest;
import com.yqg.service.user.request.UsrBankRequest;
import com.yqg.service.user.service.UsrBankService;
import com.yqg.system.dao.SysBankDao;
import com.yqg.system.entity.SysBankBasicInfo;
import com.yqg.user.dao.UsrBankDao;
import com.yqg.user.entity.UsrBank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Features:
 * Created by huwei on 18.8.17.
 */
@Service
@Slf4j
public class UsrActivityBankService {

    @Autowired
    private UsrActivityBankDao usrActivityBankDao;
    @Autowired
    private UsrBankDao usrBankDao;
    @Autowired
    private KaBinCheckService kaBinCheckService;
    @Autowired
    private UsrBankService usrBankService;
    @Autowired
    private SysBankDao sysBankDao;
    @Autowired
    private ActivityAccountRecordDao activityAccountRecordDao;

    @Autowired
    private OrdPaymentCodeDao ordPaymentCodeDao;
    @Autowired
    private OrdDao ordDao;
    @Autowired
    private RepayService repayService;

    public UsrActivityBank getUsrActivityBank(String userUuid) {
        //1.查询用户的活动银行卡列表
        List<UsrActivityBank> usrActivityBank = this.usrActivityBankDao.scanUsrActivityBank(userUuid);
        if (!CollectionUtils.isEmpty(usrActivityBank)){
            return usrActivityBank.get(0);
        }
        //2.没有活动银行卡 查询用户打款银行卡  copy至活动银行卡列表
        UsrBank usrBank = new UsrBank();
        usrBank.setStatus(2);
        usrBank.setUserUuid(userUuid);
        usrBank.setIsRecent(1);
        usrBank.setDisabled(0);
        List<UsrBank> scan = this.usrBankDao.scan(usrBank);
        if (scan.isEmpty()){
            log.info("用户活动银行卡列表为空 {}", userUuid);
            return null;
        }else{
            UsrBank usrBank1 = scan.get(0);
            UsrActivityBank bank = new UsrActivityBank();
            bank.setStatus(usrBank1.getStatus());
            bank.setUserUuid(usrBank1.getUserUuid());
            bank.setBankCardName(usrBank1.getBankCardName());
            bank.setBankCode(usrBank1.getBankCode());
            bank.setBankId(usrBank1.getBankId());
            bank.setBankNumberNo(usrBank1.getBankNumberNo());
            bank.setDisabled(0);
            bank.setCreateTime(new Date());
            bank.setUpdateTime(new Date());
            this.usrActivityBankDao.insert(bank);
            return getUsrActivityBank(userUuid);
        }
    }


    // 添加或修改用户银行卡
    public void addOrChangeBankCard(UsrBankRequest userBankRequest) throws Exception{

        //查询用户的活动银行卡列表
        List<UsrActivityBank> usrActivityBank = this.usrActivityBankDao.scanUsrActivityBank(userBankRequest.getUserUuid());
        if (!CollectionUtils.isEmpty(usrActivityBank)){
            log.info("用户修改银行卡");

            // 需要查询旧卡有没有提现的操作
             List<ActivityAccountRecord> recordList =  this.activityAccountRecordDao.getLoaningWithCard(usrActivityBank.get(0).getBankNumberNo());
             if (!CollectionUtils.isEmpty(recordList)){
                 log.error("有未完成的提现操作，不能换绑卡");
                 throw new ServiceException(ExceptionEnum.CAN_NOT_REBIND_CARD);
             }
          // 更新银行卡
            UsrActivityBank dbBank = usrActivityBank.get(0);
              if (dbBank.getBankNumberNo().equals(userBankRequest.getBankNumberNo()) && dbBank.getBankCode().equals(userBankRequest.getBankCode())){
                  log.error("该卡已经存在,请更换其他银行卡!");
                  throw new ServiceException(ExceptionEnum.SYSTEM_USER_BANK_IS_EXIST);
              }else {
                  // 对新卡进行校验 然后把旧卡disabled掉
                  cheakBankCardBin(userBankRequest,true,usrActivityBank.get(0));
              }
        }else {
            log.info("用户添加银行卡");
            // 添加银行卡
            cheakBankCardBin(userBankRequest,false,null);
        }
    }


    /**
     * 发送http请求实体类
     * @param userBankRequest
     * @return
     * @throws ServiceException
     */
    public void cheakBankCardBin (UsrBankRequest userBankRequest,Boolean isUpdate,UsrActivityBank oldBank) throws ServiceException {

        //1.查询是否支持改银行
        SysBankBasicInfo bankInfo = sysBankDao.getBankInfoByBankCode(userBankRequest.getBankCode());
        String uuid = "";
        if(bankInfo != null){
            uuid = bankInfo.getUuid();
        }else {
            log.error("活动绑卡不是支持的银行");
            throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_FAILED);
        }

        //2.发送绑卡请求
        JSONObject obj = kaBinCheckService.sendCardBinHttpPost(userBankRequest);
        // 卡bin认证状态
        Integer logType = UsrBankCardBinEnum.valueOf(obj.get("bankCardVerifyStatus").toString()).getType();

        if(logType== UsrBankCardBinEnum.FAILED.getType()) {
            log.error("卡bin返回为FAILED，绑卡失败! ");
            throw new ServiceException(ExceptionEnum.USER_KABIN_RESPONSE_FAILED);
        }else if(logType== UsrBankCardBinEnum.SUCCESS.getType()){

            // 卡bin接口返回成功：匹配姓名
            Map<String,String> nameMap = this.usrBankService.judgeNamesFun(obj.get("bankHolderName").toString(),userBankRequest.getBankCardName());// 比较姓名
            if(!nameMap.get("bankHolderName").equals(nameMap.get("bankCardName"))) {// 不匹配
                log.error("卡bin返回的姓名和传过来的姓名不匹配!");
                throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_NAME);
            }else {

                addOrUpdateUserBankCard(userBankRequest,userBankRequest.getBankCardName(),logType,uuid,isUpdate,oldBank);
            }
        }else if(logType== UsrBankCardBinEnum.PENDING.getType()){// padding，响应成功。算绑卡成功

            addOrUpdateUserBankCard(userBankRequest,userBankRequest.getBankCardName(),logType,uuid,isUpdate,oldBank);
        }else {
            log.error("绑卡异常!");
            throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_FAILED);
        }
    }

    // 添加或修改用户银行卡
    @Transactional
    public void addOrUpdateUserBankCard(UsrBankRequest usrBankRequest,String bankHolderName,Integer bankStatus,String sysBankid,Boolean isUpdate,UsrActivityBank oldBank){
       if (isUpdate){
           oldBank.setDisabled(1);
           oldBank.setRemark("用户换绑卡");
           this.usrActivityBankDao.update(oldBank);
       }

        UsrActivityBank userBank = new UsrActivityBank();
        String userBankUuid = UUIDGenerateUtil.uuid();
        userBank.setUserUuid(usrBankRequest.getUserUuid());
        userBank.setUuid(userBankUuid);
        userBank.setBankId(sysBankid);// 银行信息表 银行的的 uuid
        userBank.setBankCode(usrBankRequest.getBankCode());
        userBank.setBankNumberNo(usrBankRequest.getBankNumberNo());
        userBank.setBankCardName(bankHolderName);
        userBank.setStatus(bankStatus);
        userBank.setDisabled(0);
        this.usrActivityBankDao.insert(userBank);
    }

    /**
     *  手动添加还款码
     * */
    public void insertOrderPaymentCode(InsertOrderPaymentCodeRequest request){

        OrdOrder scan = new OrdOrder();
        scan.setDisabled(0);
        scan.setUuid(request.getOrderNo());
        List<OrdOrder> orderList = this.ordDao.scan(scan);
        if(!CollectionUtils.isEmpty(orderList)){
            OrdOrder order = orderList.get(0);
            if (order.getStatus() == OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode()||
                    order.getStatus() == OrdStateEnum.RESOLVING_OVERDUE.getCode()){
                recordOrderPaymentCode("4",request.getPaymentCode(),order,request.getPaymentAmout());
            }
        }

    }

    public void recordOrderPaymentCode( String codeType, String paymentCode, OrdOrder order, String payAmout){

        OrdPaymentCode entity = new OrdPaymentCode();
        entity.setUserUuid(order.getUserUuid());
        entity.setOrderNo(order.getUuid());
        entity.setPaymentCode(paymentCode);
        entity.setCodeType(codeType);
        entity.setActualRepayAmout(payAmout);
        entity.setInterest(order.getInterest()+"");
        //Overdue service fee regardless overdue date
        entity.setOverDueFee(this.repayService.calculateOverDueFee(order));
        //Actual overdue fee based on overdue date, without limit??? todo:ahalim
        entity.setPenaltyFee(this.repayService.calculatePenaltyFee(order));
        entity.setAmountApply(order.getAmountApply().toString());
        List<OrdPaymentCode> scanList = this.ordPaymentCodeDao.scan(entity);
        if (CollectionUtils.isEmpty(scanList)){
            this.ordPaymentCodeDao.insert(entity);
        }
    }

}
