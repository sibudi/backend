package com.yqg.signcontract.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Table(value = "usrSignContractStep")
public class UsrSignContractStep extends BaseEntity implements Serializable {
    private String userUuid;
    private String orderNo;
    private Integer signStep;
    private Integer stepResult;

    //ojk step of ekyc and digiSign
    @Getter
    public enum SignStepEnum {
//        EKYC_REAL_NAME_VERIFICATION(0),
//        EKYC_SELFIE_SCORE(1),
        EKYC_PLUS_VERIFICATION(1), //asli ekyc验证
        DIGI_SIGN_REGISTER(2), //digi sign注册
        DIGI_SIGN_ACTIVATION_API(3),//系统调用api激活
        DIGI_SIGN_ACTIVATION_CONFIRMED(4);//用户页面激活确认

        SignStepEnum(int code) {
            this.code = code;
        }

        private Integer code;
    }

    @Getter
    public enum StepResultEnum {
        INIT(0),
        SUCCESS(1),
        FAILED(2);

        StepResultEnum(int code) {
            this.code = code;
        }

        private Integer code;
    }

    public static boolean  isStepSuccess(UsrSignContractStep step){
       return step!=null && step.getStepResult() == StepResultEnum.SUCCESS.getCode();
    }
}
