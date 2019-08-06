package com.yqg.service.system.service;

import com.yqg.common.redis.RedisClient;
import com.yqg.service.system.request.LoanSwitchRequest;
import com.yqg.service.system.response.InitLoanSwitchResponse;
import com.yqg.service.system.response.LoanSwitchResponse;
import com.yqg.common.constants.SysParamContants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class LoanSwitchService {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private SysParamService sysParamService;


    public LoanSwitchResponse loanSwitch(LoanSwitchRequest loanSwitchRequest){
        int num = loanSwitchRequest.getNum();
        int loanChannel = loanSwitchRequest.getLoanChannel();
        LoanSwitchResponse loanSwitchResponse = new LoanSwitchResponse();
        if (num != 0 && num != 1) {
            loanSwitchResponse.setCode("-2");
            loanSwitchResponse.setMessage("parameters invalid");
            log.info("please check params");
            return loanSwitchResponse;
        }
        // TODO 总开关的设置

        try{
            // 设置开关
            if (LoanChannelEnum.BCA.getCode()==loanChannel){
                redisClient.set(SysParamContants.LOAN_OFF_NO_BCA,num);
            }else if(LoanChannelEnum.BNI.getCode()==loanChannel){
                redisClient.set(SysParamContants.LOAN_OFF_NO_BNI,num);
            }else if(LoanChannelEnum.TOTAL.getCode()==loanChannel){
                redisClient.set(SysParamContants.LOAN_OFF_NO,num);
            }else if(LoanChannelEnum.CIMB.getCode()==loanChannel){
                redisClient.set(SysParamContants.LOAN_OFF_NO_CIMB,num);
            }
            loanSwitchResponse.setCode("1000");
            loanSwitchResponse.setMessage("success");
            return loanSwitchResponse;

        }catch (Exception e) {
            loanSwitchResponse.setCode("-1");
            loanSwitchResponse.setMessage("redis connect timeout");
            log.error("redis connect timeout");
            return loanSwitchResponse;
        }


    }

    public InitLoanSwitchResponse initLoanSwitch(){
        InitLoanSwitchResponse initLoanSwitchResponse = new InitLoanSwitchResponse();
        try {
            Map<String,String> loanSwitchStatus = new HashMap<>();
            loanSwitchStatus.put("total",redisClient.get(SysParamContants.LOAN_OFF_NO) == null
                    ? sysParamService.getSysParamValue(SysParamContants.LOAN_OFF_NO) : redisClient.get(SysParamContants.LOAN_OFF_NO));
            loanSwitchStatus.put("bca",redisClient.get(SysParamContants.LOAN_OFF_NO_BCA) == null
                    ? sysParamService.getSysParamValue(SysParamContants.LOAN_OFF_NO_BCA) : redisClient.get(SysParamContants.LOAN_OFF_NO_BCA));
            loanSwitchStatus.put("bni",redisClient.get(SysParamContants.LOAN_OFF_NO_BNI) == null
                    ? sysParamService.getSysParamValue(SysParamContants.LOAN_OFF_NO_BNI) : redisClient.get(SysParamContants.LOAN_OFF_NO_BNI));
            loanSwitchStatus.put("cimb",redisClient.get(SysParamContants.LOAN_OFF_NO_CIMB) == null
                    ? sysParamService.getSysParamValue(SysParamContants.LOAN_OFF_NO_CIMB) : redisClient.get(SysParamContants.LOAN_OFF_NO_CIMB));
            initLoanSwitchResponse.setLoanSwitchStatus(loanSwitchStatus);
            initLoanSwitchResponse.setCode("1000");
            initLoanSwitchResponse.setMessage("success");
        }catch (Exception e){
            initLoanSwitchResponse.setCode("-1");
            initLoanSwitchResponse.setMessage("redis connect time out!");
            log.error("redis connect time out");
        }
        return initLoanSwitchResponse;
    }

    static enum LoanChannelEnum {
        TOTAL(1,"总开关"),
        BCA(2,"BCA开关"),
        BNI(3,"BNI开关"),
        CIMB(4,"CIMB开关");

        private int code;
        private String message;

        private LoanChannelEnum(int code,String message){
            this.code=code;
            this.message = message;
        }
        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
