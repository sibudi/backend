package com.yqg.system.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CallResult extends TeleCallResult {

    private CallResultEnum callChannel; //twilio ,inforbip

    public enum CallResultEnum{
        TWILIO,
        INFORBIP
    }
}
