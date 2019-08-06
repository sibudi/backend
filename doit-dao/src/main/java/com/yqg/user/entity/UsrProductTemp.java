package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseCondition;
import lombok.Data;

import java.io.Serializable;

/**
 * 待提额用户表
 * Created by wanghuaizhou on 2018/9/3.
 */
@Data
@Table("usrProductTemp")
public class UsrProductTemp extends BaseCondition implements Serializable {

    private static final long serialVersionUID = -446668231941538878L;

    private Integer id;
    private Integer disabled;
    private String remark;

    private String userUuid;

    private String orderNo;

    private String beachId;

    private String ruleName;
}
