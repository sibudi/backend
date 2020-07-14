package com.yqg.service.third.digSign.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DocumentStatusResponse {
    @JsonProperty(value = "JSONFile")
    private DocumentStatusDetail JSONFile;

    @Getter
    @Setter
    public static class DocumentStatusDetail{
        @JsonProperty(value = "expired aktifasi")
        private String expireTime;
        private String result;
        private String notif;
        private String info;
        private String link;

        private List<DocumentUser> waiting;
        private List<DocumentUser> signed;
        private String status; //Complete , Waiting
    }
    @Getter
    @Setter
    public static class DocumentUser{
        private String name;
        private String email;
    }
}
