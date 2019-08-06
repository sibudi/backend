package com.yqg.service.third.digSign.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Base64FileResponse {

    @JsonProperty(value = "JSONFile")
    private DigiResponseDetail JSONFile;

    @Getter
    @Setter
    public static class DigiResponseDetail{
        private String result;
        private String notif;
        private String info;
        private String file;
    }
}
