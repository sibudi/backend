package com.yqg.service.third.gojek.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yqg.common.models.BaseRequest;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/12/20.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class GojekRequest extends BaseRequest {

    private String mobile;
    private String id_type="1";
    private String id_num;
    private String name="Do-It";

    private String report_task_token;
    private String auth_token;
    private String captcha;

}
