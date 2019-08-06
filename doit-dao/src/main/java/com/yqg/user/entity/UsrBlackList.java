package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Table("usrBlackList")
@Getter
@Setter
public class UsrBlackList   extends BaseEntity implements Serializable {
    private String mobileDes;
    private String userUuid;
    private String deviceId;
    private String idCardNo;
    private String imei;
    private String linkManContactNumber1;
    private String linkManContactNumber2;
    private String bankCardNumber;
    private Integer type;

    private String whatsapp; //whatsapp账号
    private String md5ForCompanyAddress; //md5(upper(省份市大区小区详细地址))
    private String md5ForHomeAddress; //md5(upper(省份市大区小区详细地址))
    private String companyTel; //公司电话

    @Getter
    public enum UsrBlackTypeEnum{

        UANG2_OVERDUE_30(1),//uang2逾期用户[30,)
        UANG2_OVERDUE_15(2),//uang2逾期[15,30)
        DOIT_OVERDUE_30(3),//doit逾期[30,)
        DOIT_OVERDUE_15(4),//doit逾期[15,30)
        FRAUDULENT_USER(5),//欺诈用户[手工添加]
        UANG2_OVERDUE_7(6),//uang2逾期[7,15)
        DOIT_OVERDUE_7(7),//doit逾期[7,15)
        COLLECTOR_BLACK_LIST_USER(8),//催收黑名单手机号
        SENSITIVE_USER(9),//敏感用户-催收或者审核人员
        COMPLAINT(10);//ojk投诉用户
        UsrBlackTypeEnum(int code){
            this.code=code;
        }
        private int code;
        public static UsrBlackTypeEnum enumFromCode(int code){
            UsrBlackTypeEnum[] arrays = UsrBlackTypeEnum.values();
            Optional<UsrBlackTypeEnum> relatedEnum = Arrays.asList(arrays).stream().filter(elem->elem.getCode() == code).findFirst();
            if(relatedEnum.isPresent()){
                return relatedEnum.get();
            }
            return null;
        }
    }

    public enum BlackUserCategory{
        OVERDUE7,
        OVERDUE15,
        OVERDUE30,
        FRAUD,
        SENSITIVE,
        COLLECTOR_BLACK_LIST,
        COMPLAINT
    }

    public static List<Integer>  getBlackTypesByCategory(BlackUserCategory category) {
        List<Integer> types = new ArrayList<>();
        switch (category) {
            case FRAUD:
                return Arrays.asList(UsrBlackTypeEnum.FRAUDULENT_USER.getCode());
            case SENSITIVE:
                return Arrays.asList(UsrBlackTypeEnum.SENSITIVE_USER.getCode());
            case COLLECTOR_BLACK_LIST:
                return Arrays.asList(UsrBlackTypeEnum.COLLECTOR_BLACK_LIST_USER.getCode());
            case COMPLAINT:
                return Arrays.asList(UsrBlackTypeEnum.COMPLAINT.getCode());
            case OVERDUE7:
                return Arrays.asList(UsrBlackTypeEnum.FRAUDULENT_USER.getCode(),
                        UsrBlackTypeEnum.DOIT_OVERDUE_7.getCode(),
                        UsrBlackTypeEnum.UANG2_OVERDUE_7.getCode(),
                        UsrBlackTypeEnum.DOIT_OVERDUE_15.getCode(),
                        UsrBlackTypeEnum.DOIT_OVERDUE_30.getCode(),
                        UsrBlackTypeEnum.UANG2_OVERDUE_15.getCode(),
                        UsrBlackTypeEnum.UANG2_OVERDUE_30.getCode());
            case OVERDUE15:
                return Arrays.asList(UsrBlackTypeEnum.FRAUDULENT_USER.getCode(),
                        UsrBlackTypeEnum.DOIT_OVERDUE_15.getCode(),
                        UsrBlackTypeEnum.DOIT_OVERDUE_30.getCode(),
                        UsrBlackTypeEnum.UANG2_OVERDUE_15.getCode(),
                        UsrBlackTypeEnum.UANG2_OVERDUE_30.getCode());
            case OVERDUE30:
                return Arrays.asList(UsrBlackTypeEnum.FRAUDULENT_USER.getCode(),
                        UsrBlackTypeEnum.DOIT_OVERDUE_30.getCode(),
                        UsrBlackTypeEnum.UANG2_OVERDUE_30.getCode());
        }
        return types;
    }

}
