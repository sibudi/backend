package com.yqg.service.third.asli.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsliEkycIdentRequest {

    private String nik;
    private String name;
    private String birthPlace;
    private String birthDate;
    private String address;

    private String userUuid;
    private String orderNo;
}
