package com.yqg.common.models;

import lombok.Data;

/**
 * Created by Didit Dwianto on 2017/10/23.
 */
@Data
public class ResultMsg {
    private Boolean success = true;//????
    private Integer errCode = 1;//????
    private String message = "success";//??success
    private Object result;

}
