package com.yqg.service.third.Inforbip.Response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by wanghuaizhou on 2018/8/29.
 */
@Data
public class GetReportResponse {


    /**
     *{
     "results":[
     {
     "bulkId":"8c20f086-d82b-48cc-b2b3-3ca5f7aca9fb",
     "messageId":"ff4804ef-6ab6-4abd-984d-ab3b1387e852",
     "to":"385981178",
     "sentAt": "2018-06-25T13:38:14.730+0000",
     "doneAt": "2018-06-25T13:38:28.318+0000",
     "startTime": "2018-06-25T13:38:15.000+0000",
     "endTime": "2018-06-25T13:38:28.316+0000",
     "answerTime": "2018-06-25T13:38:25.000+0000",
     "duration":10,
     "fileDuration": 19.3,
     "mccMnc": "21901",
     "callbackData": "DLR callback data",
     "dtmfCodes":"1",
     "recordedAudioFileUrl":"/tts/3/files/ff4804ef-6ab6-4abd-984d-ab3b1387e852/385981178",
     "price":{
     "pricePerSecond":0.01,
     "currency":"EUR"
     },
     "status":{
     "id":5,
     "groupId":3,
     "groupName":"DELIVERED",
     "name":"DELIVERED_TO_HANDSET",
     "description":"Message delivered to handset"
     },
     "error":{
     "groupId":0,
     "groupName":"OK",
     "id": 5000,
     "name": "VOICE_ANSWERED",
     "description": "Call answered by human",
     "permanent": true
     }
     }
     ]
     }
     * */

    private List<Results> results;
    @Data
    public static class Results {


        private String bulkId;
        private String messageId;
        private String to;
        private String sentAt;
        private String doneAt;
        private String startTime;
        private String endTime;
        private String answerTime;
        private Integer duration;
        private BigDecimal fileDuration;
        private String mccMnc;
        private String callbackData;
        private String dtmfCodes;
        private String recordedAudioFileUrl;

        private Price price;
        private Status status;  //显示号码查询是否成功,不执行或其他的状态。
        private Error error;
    }


    @Data
    public static class Price {

        private String pricePerSecond;
        private String currency;
    }


    @Data
    public static class Status {

        private Integer groupId;
        private String groupName;
        private Integer id;
        private String name;
        private String description;
    }

    @Data
    public static class Error extends Status {

        private Boolean permanent;
    }
}
