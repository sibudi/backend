package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * 产品提额记录表
 * Created by wanghuaizhou on 2018/9/3.
 */
@Data
@Table("usrProductRecord")
public class UsrProductRecord extends BaseEntity implements Serializable {

    private String orderNo;

    private String userUuid;

    private String productUuid;

    private Integer lastProductLevel;

    private Integer currentProductLevel;

    private String ruleName;

}
