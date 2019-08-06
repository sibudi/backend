package com.yqg.service.third.operators.request;

import com.yqg.common.models.BaseRequest;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/12/21.
 */
@Data
public class IM3Request extends BaseRequest {

    private String phoneNo;

    private String pwd;
}
