package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Table("usrVerifyResult")
public class UsrVerifyResult extends BaseEntity implements Serializable {
    private String userUuid;
    private String orderNo;
    private Integer verifyType;
    private Integer verifyResult;
    private String response;
    private String remark;

    @Getter
    public enum VerifyTypeEnum{
        KTP(1),
        ADVANCE(2),
        TAX_NUMBER(3),
        XENDIT(4),
        IZI_PHONE(5),
        IZI_REAL_NAME(6);

        VerifyTypeEnum(Integer code){
            this.code = code;
        }
        private Integer code;
    }

    @Getter
    public enum VerifyResultEnum{
        INIT(0),
        SUCCESS(1),
        FAILED(2);
        VerifyResultEnum(Integer code){
            this.code = code;
        }
        private Integer code;
    }
}
