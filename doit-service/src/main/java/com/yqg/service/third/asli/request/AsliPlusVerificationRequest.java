package com.yqg.service.third.asli.request;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AsliPlusVerificationRequest {
    private String nik;
    private String name;
    @JsonProperty(value = "birthdate")
    private String birthdate;  //DD-MM-YYYY
    @JsonProperty(value = "birthplace")
    private String birthplace;
    private String address;//
    @JsonProperty(value = "identity_photo")
    private String identityPhoto;// base64 photo
    @JsonProperty(value = "selfie_photo")
    private String selfiePhoto;
    private String phone;
    @JsonProperty(value = "mother_name")
    private String motherName;

    private String orderNo;
    private String userUuid;
}
