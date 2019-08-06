package com.yqg.manage.service.check.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: microservice
 * @description: 排班信息返回前端的实体
 * @author: 许金泉
 * @create: 2019-04-03 10:25
 **/
@Data
@NoArgsConstructor
public class ManReceiverSchedulingResponse {

    private Integer id;

    private String uuid;

    /**
     * 催收人员
     */
    private String userName;


    /**
     * 录音开始时间
     */
    private Date startTime;

    /**
     * 录音结束时间
     */
    private Date endTime;

    /**
     * 催收人员是否工作，0 否，1，是
     */
    private Integer work;

    /**
     * 催收人员id(对应sysUser表id)
     */
    private Integer userId;

    /**
     * 排班时间
     */
    private Date workTime;
}
