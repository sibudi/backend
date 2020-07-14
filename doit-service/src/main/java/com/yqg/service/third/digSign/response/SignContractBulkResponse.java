package com.yqg.service.third.digSign.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignContractBulkResponse {
    @JsonProperty(value = "JSONFile")
    private SignContractBulkDetail JSONFile;

    @Getter
    @Setter
    public static class SignContractBulkDetail {

        private String result;
        private String link;
    }
}