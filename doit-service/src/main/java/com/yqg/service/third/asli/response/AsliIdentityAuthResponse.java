package com.yqg.service.third.asli.response;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/3/26.
 */
@Data
public class AsliIdentityAuthResponse {
    private String timestamp;
    private String status;
    private String error;
    private String message;
    private AuthData data;

    @Data
    public static class AuthData{
        private String name;
        private String birthplace;
        private String birthdate;
        private String address;
    }

    public boolean isIdentityAutoSuccess() {
        if (!"200".equals(status)) {
            return false;
        }
        if (!"Success".equals(message)) {
            return false;
        }
        if (data == null) {
            return false;
        }
        boolean detailOk = "true".equals(data.getName()) && "true".equals(data.getBirthdate()) && "true".equals(data.getBirthplace());
        return detailOk;
    }
}
