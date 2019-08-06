package com.yqg.service.user.request;


import com.yqg.common.models.BaseRequest;
import com.yqg.user.entity.UsrLinkManInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LinkManRequest extends BaseRequest {
    private List<UsrLinkManInfo> linkmanList;
    private String orderNo;
}
