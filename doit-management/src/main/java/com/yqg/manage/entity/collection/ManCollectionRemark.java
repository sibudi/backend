package com.yqg.manage.entity.collection;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Author: tonggen
 * Date: 2018/8/28
 * time: 上午10:20
 */
@Setter
@Getter
@Table("manCollectionRemark")
public class ManCollectionRemark extends BaseEntity implements Serializable{

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 用户ID
     *
     */
    private String userUuid;
    /**
     * 联系人类型
     */
    private Integer contactType;
    /**
     * 联系电话
     */
    private String contactMobile;
    /**
     * 联系方式
     */
    private Integer contactMode;
    /**
     * 联系结果
     */
    private Integer contactResult;
    /**
     * 联系标签
     */
    private Integer orderTag;
    /**
     * 提醒时间
     */
    private Date alertTime;
    /**
     * 承诺还款时间
     */
    private Date promiseRepaymentTime;
}
