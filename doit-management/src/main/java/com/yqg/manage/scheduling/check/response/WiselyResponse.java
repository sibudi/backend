package com.yqg.manage.scheduling.check.response;

/**
 * @author alan
 */
public class WiselyResponse {
    private Integer userId;

    private String message;

    private String url;

    public Integer getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
