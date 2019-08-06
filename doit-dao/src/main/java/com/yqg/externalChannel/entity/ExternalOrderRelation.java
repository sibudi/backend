package com.yqg.externalChannel.entity;

import com.yqg.base.data.condition.BaseCondition;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/8
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Getter
@Setter
public class ExternalOrderRelation extends BaseCondition {
    private Integer id;
    private String externalOrderNo;
    private String orderNo;
    private String userUuid;
    private String channel;
    private Date createTime;
    private Date updateTime;
    private Integer disabled;
}
