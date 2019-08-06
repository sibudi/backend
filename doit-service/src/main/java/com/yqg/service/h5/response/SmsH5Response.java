package com.yqg.service.h5.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/1/9.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmsH5Response {

    private String smsKey;

}
