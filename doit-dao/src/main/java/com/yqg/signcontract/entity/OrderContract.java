package com.yqg.signcontract.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Table(value = "orderContract")
public class OrderContract extends BaseEntity implements Serializable {

    private String documentId;
    private String downloadedPath;
    private Integer status;
    private String orderNo;
    private String userUuid;

    @Getter
    public enum DocumentStatus{
        SEND_FAILED(10),
        SEND_SUCCESS(11),
        SIGN_FAILED(20),
        SIGN_SUCCESS(21),
        DOWNLOAD_FAILED(30),
        DOWNLOAD_SUCCESS(31),
        ;
        DocumentStatus(int code){
            this.code = code;
        }
        private Integer code;
    }
}
