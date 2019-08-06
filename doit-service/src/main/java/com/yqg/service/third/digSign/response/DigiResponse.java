package com.yqg.service.third.digSign.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DigiResponse {

    @JsonProperty(value = "JSONFile")
    private DigiResponseDetail JSONFile;

    @Getter
    @Setter
    public static class DigiResponseDetail{
        @JsonProperty(value = "expired aktifasi")
        private String expireTime;
        private String result;
        private String notif;
        private String info;
    }
}
