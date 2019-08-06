package com.yqg.manage.service.order.response;

import lombok.Data;

import java.util.Date;

/**
 *  @author Jacob
 */
@Data
public class ManAllOrdListResponse extends OrderBaseResponse{

    private Date createTime;

    private Date updateTime;//更新日期

    private Integer isRepeatBorrowing;     //是否复借订单

}
