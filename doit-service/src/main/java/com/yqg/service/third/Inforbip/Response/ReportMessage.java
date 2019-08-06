package com.yqg.service.third.Inforbip.Response;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/8/29.
 */
@Data
public class ReportMessage {

    private String to;
    private String messageId;
    private Status status;  //显示号码查询是否成功,不执行或其他的状态。

    @Data
    public static class Status {

        private Integer groupId;
        private String groupName;
        private Integer id;
        private String name;
        private String description;
    }
}
