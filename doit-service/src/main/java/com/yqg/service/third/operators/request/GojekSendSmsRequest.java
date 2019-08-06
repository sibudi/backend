package com.yqg.service.third.operators.request;

import com.yqg.common.models.BaseRequest;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/12/20.
 */
@Data
public class GojekSendSmsRequest extends BaseRequest {

    private String mobile;

    // gojek 和 golife
    private String website;

    private String name;
}
