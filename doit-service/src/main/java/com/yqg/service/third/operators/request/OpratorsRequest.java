package com.yqg.service.third.operators.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yqg.common.models.BaseRequest;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/12/19.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OpratorsRequest extends BaseRequest {

    private String phoneNo;

    private String pwd;

    private String email;

    //  xl  telk1 telk2  tokoPedia
    private String type;


    private String batchId;


    private String report_task_token;

    private String orderNo;

    private String website;
}
