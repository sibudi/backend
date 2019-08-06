package com.yqg.service.third.asli.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AsliSelfieScoreResposne {
    private Long timestamp;
    private Integer status;
    private String error;
    private String message;
    private SelfieData data;

    @Getter
    @Setter
    public static class SelfieData {
        @JsonProperty(value = "selfie_photo")
        private BigDecimal selfieSocre;
    }
}
