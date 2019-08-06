package com.yqg.service.system.request;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2019/4/22.
 */
@Data
public class AddUserWhiteListRequest {

    private String batchId;

    private String productUuid;
}
