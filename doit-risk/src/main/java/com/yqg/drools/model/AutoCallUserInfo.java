package com.yqg.drools.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AutoCallUserInfo {
    private Integer age;
    private Boolean gojekVerified;

    private Boolean existsSameEmail;
}
