package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/30.
 */
@Data
@Table("sysAutoReviewRule")
public class SysAutoReviewRule extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1340747066045088908L;

    //  规则大类
    private Integer ruleType;

    //  规则小类
    private String ruleDetailType;

    //  规则描述
    private String ruleDesc;

    //  规则数据
    private String ruleCondition;

    //  规则数据
    private String ruleData;

    //  规则阈值
    private String ruleValue;

    //  规则审核结果 1通过 2 拒绝
    private Integer ruleResult;

    //  规则状态 1有效 2无效 3测试
    private Integer ruleStatus;

    //  规则拒绝天数
    private Integer ruleRejectDay;

    //  规则顺序
    private Integer ruleSequence;

    //  规则版本
    private String ruleVersion;

    //适用对象
    private Integer appliedTo;

    //可排除该规则的产品
    private Integer specifiedProduct;

    @Getter
    public enum AppliedTargetEnum {
        //规则适用类型
        android(1),
        iOS(2),
        both(3),
        android_CashCash(14),//android and cashcash执行
        iOS_CashCash(24),//  ios and cashcash执行
        both_CashCash(34),//只要是cashcash就执行，不区分ios android
        android_NORMAL(15),// android and 自有渠道
        iOS_NORMAL(25),// android and 自有渠道
        both_NORMAL(35), // 自有渠道[和CashCash渠道的分开]
        android_CHEETAH(16),// android and 猎豹
        iOS_CHEETAH(26),// android and 猎豹
        both_CHEETAH(36), // 猎豹
        ;

        AppliedTargetEnum(int code) {
            this.code = code;
        }

        private int code;

        public static AppliedTargetEnum enumOfValue(int inputCode) {
            AppliedTargetEnum[] enums = AppliedTargetEnum.values();
            for (AppliedTargetEnum elem : enums) {
                if (elem.getCode() == inputCode) {
                    return elem;
                }
            }
            return null;
        }
    }

    @Getter
    public enum ExcludedForSpecifiedProduct {
        DEFAULT(0),
        PRODUCT_100RMB(1),
        PRODUCT_50RMB(2),
        PRODUCT_600RMB(3),
        PRODUCT_50_100(12),

        ;

        ExcludedForSpecifiedProduct(int code) {
            this.code = code;
        }

        public static ExcludedForSpecifiedProduct enumFromCode(Integer code) {
            if (code == null) {
                return DEFAULT;
            }
            ExcludedForSpecifiedProduct allEnums[] = ExcludedForSpecifiedProduct.values();
            for (ExcludedForSpecifiedProduct item : allEnums) {
                if (code == item.getCode()) {
                    return item;
                }
            }
            return DEFAULT;
        }

        private int code;
    }


}
