package com.yqg.manage.service.loan.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author alan
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class FindOrderForCompanyRequest {
    @ApiModelProperty(value = "关联id")
    @JsonProperty
    private Integer outsourceId;
    @ApiModelProperty(value = "通讯录手机号")
    @JsonProperty
    private String contactMobile;
    @ApiModelProperty(value = "sessionId")
    @JsonProperty
    private String sessionId;
    @ApiModelProperty(value = "页数")
    @JsonProperty
    private Integer pageNo = 1;
    @ApiModelProperty(value = "分页大小")
    @JsonProperty
    private Integer pageSize = 10;

    public Integer getOutsourceId() {
        return outsourceId;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setOutsourceId(Integer outsourceId) {
        this.outsourceId = outsourceId;
    }

    public void setContactMobile(String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
