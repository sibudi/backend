package com.yqg.service.third.jxl.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JXLBaseResponse {
    private String code;
    private String message;
    private Object data;


    public boolean isResponseSuccess() {
        if (StringUtils.isNotEmpty(code) && "20000".equalsIgnoreCase(code)) {
            return true;
        }
        return false;
    }

    @Getter
    @Setter
    public static class CreateReportTaskData {
        @JsonProperty(value = "report_task_token")
        private String reportTaskToken;
    }

    @Getter
    @Setter
    public static class IdentityVerifyData {
        private String birthday;
        private String city;
        private String district;
        private String division;
        private String gender;
        @JsonProperty(value = "id_card_no")
        private String idCardNo;
        private String name;
        @JsonProperty(value = "place_of_birth")
        private String placeOfBirth;
        private String province;
        private String remark;

        @JsonProperty(value = "verify_result")
        private String verifyResult;
    }
}
