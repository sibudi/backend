package com.yqg.service.third.operators.request;

import com.yqg.common.models.BaseRequest;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/1/16.
 */
@Data
public class TokoPedaiAuthRequest extends BaseRequest{

    private String pwd;

    private String email;

    private String orderNo;

    private String website;

    private String name;
}
