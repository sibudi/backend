package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Date: 23/03/2018
 * Time: 3:09 PM
 */

@Setter
@Getter
@Table(value = "usrEvaluateScore")
public class UsrEvaluateScore extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -7756652869034667672L;

    private String orderNo;// varchar(32) not null default '' comment '订单号',
    private String userUuid;// varchar(32) not null default '' comment '用户或者催收的userUuid',
    private Integer serviceMentality;// int(4) not null default 0 comment '服务意识',
    private Integer communicationBility;// int(4) not null default 0 comment '沟通能力',
    private Integer repayDesire;// int(4) not null default 0 comment '还款意愿',
    private Integer repayBility;// int(4) not null default 0 comment '还款能力',
    private Integer userDiathesis;// int(4) not null default 0 comment '用户素质',
    private Integer postId;// int(11) not null default 0 comment '催收阶段',
    private Integer type;// int(2) not null default 0 comment '1.用户对催收的评价 2.催收对用户的评价'
}
