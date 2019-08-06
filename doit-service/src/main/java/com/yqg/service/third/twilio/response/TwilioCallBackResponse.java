package com.yqg.service.third.twilio.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Author: tonggen
 * Date: 2018/10/15
 * time: 上午10:50
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwilioCallBackResponse {


    private String CallSid;
    private String AccountSid;
    private String From;
    private String To;
    private String CallStatus;
    private String ApiVersion;
    private String Direction;
    private String ForwardedFrom;
    private String CallerName;
    private String ParentCallSid;

    @Override
    public String toString() {
        return "TwilioCallBackResponse{" +
                "CallSid='" + CallSid + '\'' +
                ", AccountSid='" + AccountSid + '\'' +
                ", From='" + From + '\'' +
                ", To='" + To + '\'' +
                ", CallStatus='" + CallStatus + '\'' +
                ", ApiVersion='" + ApiVersion + '\'' +
                ", Direction='" + Direction + '\'' +
                ", ForwardedFrom='" + ForwardedFrom + '\'' +
                ", CallerName='" + CallerName + '\'' +
                ", ParentCallSid='" + ParentCallSid + '\'' +
                '}';
    }
}
