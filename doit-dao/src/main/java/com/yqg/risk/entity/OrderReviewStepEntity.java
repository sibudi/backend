package com.yqg.risk.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("orderReviewStep")
public class OrderReviewStepEntity   extends BaseEntity {
    private String orderNo;
    private String userUuid;
    private Integer step;

    @Getter
    public enum StepEnum{
        UNKNOWN(0),
        LINKMAN_AND_COMPANY_CALL(1),
        OWNER_CALL(2),
        ;

        StepEnum(int inCode){
            this.code = inCode;
        }
        private int code;


        public static StepEnum getEnumFromCode(int code){
            StepEnum[] stepValues = StepEnum.values();
            for(StepEnum elem: stepValues){
                if(elem.getCode()==code){
                    return elem;
                }
            }
            return UNKNOWN;
        }
    }
}
