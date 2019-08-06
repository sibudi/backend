package com.yqg.service.third.asli.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AsliPlusVerificationResponse {
    private String timestamp;
    private String status;

    private DataDetail data;

    private Object error;

    public Boolean isEkycVerifySuccess(BigDecimal scoreLimit) {
        if (!"200".equals(status)) {
            return false;
        }
        if (data == null) {
            return false;
        }
        return data.getBirthdate() != null && data.getBirthdate()
                && data.getBirthplace() != null && data.getBirthplace()
                && data.getName() != null && data.getName()
                && data.getSelfiePhoto() != null && data.getSelfiePhoto().compareTo(scoreLimit) >= 0;


    }


    @Getter
    @Setter
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
    public static class DataDetail {
        private Boolean name;
        private Boolean birthplace;
        private Boolean birthdate;
        private String address;
        @JsonProperty(value = "identity_photo ")
        private String identityPhoto;
        @JsonProperty(value = "selfie_photo")
        private BigDecimal selfiePhoto;
        private Boolean phone;
        private String mother;
        @JsonProperty(value = "mother_name")
        private String motherName;
    }
}
