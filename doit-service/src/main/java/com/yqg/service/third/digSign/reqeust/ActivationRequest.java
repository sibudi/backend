package com.yqg.service.third.digSign.reqeust;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivationRequest {

    @JsonProperty(value = "userid")
    private String userid;
    @JsonProperty(value = "email_user")
    private String emailUser;
}
