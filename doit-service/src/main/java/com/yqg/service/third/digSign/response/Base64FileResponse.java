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

    public enum resultType{
        //ref: Digisign API documentation
        SUCCESS("00", "Success"),
        DATA_NOT_FOUND("05", "Data not found");

        private String code;
        private String message;
        
        private resultType(String code, String message){
            this.code = code;
            this.message = message;
        }
        
        public String getCode() {
            return this.code;
        }
        public String getMessage() {
            return this.message;
        }
    }
}
