package com.yqg.service.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.models.BaseRequest;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsrEvaluateScoreRequest extends BaseRequest {


    private String orderNo;

    private String collectionUuid;//催收人员uuid

    private Integer serviceMentality; //int(4) not null default 0 comment '服务意识',
    private Integer communicationBility;// int(4) not null default 0 comment '沟通能力',

}
