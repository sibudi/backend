package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseCondition;
import com.yqg.base.data.condition.BaseEntity;
import com.yqg.common.models.BaseRequest;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.*;

/**
 * Created by Janhsen on 2019/11/22.
 */
@Data
@Table("externalAnalytic")
public class ExternalAnalytic extends BaseEntity implements Serializable {

    private String eventName;
    private String eventAttribute;
    private String eventValue;
    private String source; //Android, IOS, etc
    private String remark;
    private String partnerName;

    @Getter
    public enum AnalyticSourceEnum {
        ANDROID("Android"),
        IOS("IOS"),
        WEBSITE("Website"),
        OTHER("Other"),
        ;

        AnalyticSourceEnum(String sourceEnum) {
            this.source = sourceEnum;
        }
        private String source;

        public static AnalyticSourceEnum getEnumFromValue(String code) {
            AnalyticSourceEnum allEnums[] = AnalyticSourceEnum.values();
            for (AnalyticSourceEnum enumItem : allEnums) {
                if (enumItem.getSource().equals(code)) {
                    return enumItem;
                }
            }
            return null;
        }
    }
}
