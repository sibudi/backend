package com.yqg.management.entity;

import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: microservice
 * @description: 质检语音实体
 * @author: 许金泉
 * @create: 2019-04-02 19:36
 **/
@Data
@com.yqg.base.data.annotations.Table("receiverScheduling")
public class ReceiverScheduling extends BaseEntity implements Serializable {



    /**
     * 催收人员登陆名
     */
    private String userName;

    /**
     * 催收人员是否工作，0 否，1，是
     */
    private Integer work;

    /**
     * 排班时间
     */
    private Date workTime;


    /**
     *  催收人员ID
     */
    private Integer userId;



}
