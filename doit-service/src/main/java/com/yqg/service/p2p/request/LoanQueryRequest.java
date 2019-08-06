package com.yqg.service.p2p.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanQueryRequest extends P2PInvokeBaseParam {
    private String mobileNumber;
}
