package com.yqg.service.third.digSign.response;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;


@Getter
public enum DigiSignUserStatusEnum {
    REGISTERED("user sudah mendaftar namun belum melakukan aktivasi."),
    ACTIVATED("aktif"),
    NOT_FOUND("user belum terdaftar."),
    UNKNOWN("error"),
    ;

    DigiSignUserStatusEnum(String notif) {
        this.notif = notif;
    }

    private String notif;

    static DigiSignUserStatusEnum[] statusList = DigiSignUserStatusEnum.values();

    public static DigiSignUserStatusEnum getEnumFromValue(String inValue) {
        Optional<DigiSignUserStatusEnum> result = Arrays.stream(statusList).filter(elem -> elem.getNotif().equalsIgnoreCase(inValue)).findFirst();
        if (result.isPresent()) {
            return result.get();
        } else {
            return UNKNOWN;
        }
    }

}
