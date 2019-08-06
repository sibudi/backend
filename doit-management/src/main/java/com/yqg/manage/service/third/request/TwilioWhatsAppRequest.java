package com.yqg.manage.service.third.request;

import lombok.Data;

/**
 * Author: tonggen
 * Date: 2018/12/4
 * time: 11:35 AM
 */

@Data
public class TwilioWhatsAppRequest {

    private String from;

    private String to;

    private String body;

    private String messageSid;
    private String messageStatus;

    //设置结果查询列表的请求参数
    private String orderNo;

    private String userName;

    private String phoneNumber;

    private Integer overDueDay;

    private String promiseTimeStart;// yyyy-MM-dd HH:mm

    private String promiseTimeEnd;// yyyy-MM-dd HH:mm

    private Integer solveType; //解决情况 1 已解决，2 待跟进，3 暂时无解，4 无须跟进

    private String replyContent;

    private Integer pageNo = 1;

    private Integer pageSize = 10;

    private String remark;

    private Integer id;

    @Override
    public String toString() {
        return "TwilioWhatsAppRequest{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", body='" + body + '\'' +
                ", messageSid='" + messageSid + '\'' +
                ", messageStatus='" + messageStatus + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", userName='" + userName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", overDueDay=" + overDueDay +
                ", promiseTimeStart='" + promiseTimeStart + '\'' +
                ", promiseTimeEnd='" + promiseTimeEnd + '\'' +
                ", solveType=" + solveType +
                ", replyContent='" + replyContent + '\'' +
                ", pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", remark='" + remark + '\'' +
                ", id=" + id +
                '}';
    }
}
