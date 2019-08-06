package com.yqg.manage.entity.check;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author alan
 */
@Data
@Table("manOrderCheckRule")
public class ManOrderCheckRule extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -837019474006254772L;

    private Integer infoType;

    private Integer ruleCount;

    private Integer ruleLevel;

    private Integer ruleResult;

    private String orderNo;

    private String description;

    private String descriptionInn;

    /**
     * 1、直接将用户加入黑名单 2、订单回退：身份证重传 3、订单回退：公司地址重传 4、订单回退：手持身份证重传 5、订单回退：活体重传
     * 6、订单回退：视频重传 7、订单回退：驾照重传 8、订单回退：护照重传 9、订单回退：保险卡重传 10、订单回退：家庭卡重传
     */
    private Integer type;
}
