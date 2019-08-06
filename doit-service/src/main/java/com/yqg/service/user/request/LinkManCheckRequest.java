package com.yqg.service.user.request;

import com.yqg.common.models.BaseRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkManCheckRequest extends BaseRequest {
    private String orderNo;
}
