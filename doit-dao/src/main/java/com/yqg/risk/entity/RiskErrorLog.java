package com.yqg.risk.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseCondition;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * Created at 2018/2/7
 *
 *
 ****/

@Table("riskErrorLog")
@Getter
@Setter
public class RiskErrorLog extends BaseCondition {

    private Integer id;
    private String orderNo;
    private Date createTime;
    private Date updateTime;
    private Integer times;
    private Integer disabled;
    private String uuid;
    private String remark;
    private Integer errorType;

    @Getter
    public enum RiskErrorTypeEnum{
        SYSTEM_ERROR(0),
        TAX_VERIFY_NEED_RETRY(1);

        RiskErrorTypeEnum(int code){
            this.code = code;
        }
        private int code;

    }

}
