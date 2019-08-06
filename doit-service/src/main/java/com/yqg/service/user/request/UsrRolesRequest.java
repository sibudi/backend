package com.yqg.service.user.request;

import com.yqg.common.models.BaseRequest;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/29.
 */
@Data
public class UsrRolesRequest extends BaseRequest implements Serializable {
    private static final long serialVersionUID = 2909619105682657712L;
    private Integer role;
    private String orderNo;
}
