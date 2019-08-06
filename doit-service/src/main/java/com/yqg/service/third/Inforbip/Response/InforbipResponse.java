package com.yqg.service.third.Inforbip.Response;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


/**
 * Created by wanghuaizhou on 2018/8/28.
 */
@Getter
@Setter
public class InforbipResponse {

    /**
     *
     {
     "bulkId": "f3b35d4a-e3ed-427c-bb3e-bd486cac22dc",
     "messages": [
     {
     "to": "6287713310463",
     "status": {
     "groupId": 1,
     "groupName": "PENDING",
     "id": 26,
     "name": "PENDING_ACCEPTED",
     "description": "Message accepted, pending for delivery."
     },
     "messageId": "f234122f-ee0a-47d1-957c-bb3e730befce"
     }
     ]
     }
     * */
    private String bulkId;  // bulkId

    private List<ReportMessage> messages;  // 相关信息




}
