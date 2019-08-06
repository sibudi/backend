package com.yqg.common.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @author by alan on 17-5-9.
 */
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageData<T> {
    @ApiModelProperty(value = "??", required = true)
    @JsonProperty
    @NotNull
    private int pageNo;

    @ApiModelProperty(value = "????", required = true)
    @JsonProperty
    @NotNull
    private int pageSize;

    @ApiModelProperty(value = "????", required = true)
    @JsonProperty
    @NotNull
    private int recordsTotal;

    @ApiModelProperty(value = "????", required = true)
    @JsonProperty
    @NotNull
    private T data;

    public int getPageNo() {
        return pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getRecordsTotal() {
        return recordsTotal;
    }

    public T getData() {
        return data;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setRecordsTotal(int recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public void setData(T data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return com.google.common.base.MoreObjects.toStringHelper(this)
                .add("pageNo", this.getPageNo())
                .add("pageSize", this.getPageSize())
                .add("recordsTotal", this.getRecordsTotal())
                .add("data", this.getData());
    }
}
