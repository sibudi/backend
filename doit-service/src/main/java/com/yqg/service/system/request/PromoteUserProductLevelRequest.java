package com.yqg.service.system.request;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/9/3.
 */
@Data
public class PromoteUserProductLevelRequest {

    private String batchId;

    private String content;

    private Integer productLevel;
}
