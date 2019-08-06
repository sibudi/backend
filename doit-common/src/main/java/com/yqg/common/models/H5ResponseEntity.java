package com.yqg.common.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.yqg.common.annotations.H5Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Created by Didit Dwianto on 2017/11/16.
 */
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
@H5Request
public class H5ResponseEntity<T>{
    @ApiModelProperty(value = "return code defined by api protocol", required = true)
    @JsonProperty
    @NotNull
    private int code;

    @ApiModelProperty(value = "readable message corresponding to the return code", required = true)
    @JsonProperty
    @NotNull
    private String message;

    @ApiModelProperty(value = "data returned to client, in JSON format", required = false)
    @JsonProperty
    @Valid
    private T data;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResponseEntity<T> other = (ResponseEntity<T>) o;

        return Objects.equals(this.getCode(), other.getCode())
                && Objects.equals(this.getMessage(), other.getMessage())
                && Objects.equals(this.getData(), other.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.getCode(),
                this.getData(),
                this.getMessage());
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("code", this.getCode())
                .add("message", this.getMessage())
                .add("data", this.getData());
    }
}
