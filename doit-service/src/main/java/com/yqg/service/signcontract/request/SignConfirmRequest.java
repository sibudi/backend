package com.yqg.service.signcontract.request;

import com.yqg.common.models.BaseRequest;
import lombok.Getter;
import lombok.Setter;
import org.apache.xpath.operations.Bool;

import java.io.Serializable;

@Setter
@Getter
public class SignConfirmRequest extends BaseRequest implements Serializable {
    private String orderNo;
    private String response;
}
