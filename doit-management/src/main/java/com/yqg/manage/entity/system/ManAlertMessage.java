package com.yqg.manage.entity.system;


import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseCondition;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author alan
 */
@Table("manAlertMessage")
@Data
public class ManAlertMessage extends BaseCondition implements Serializable {
    private static final long serialVersionUID = -2283877144674010417L;

    private Integer userId;

    private String message;

    private String url;

    private Date alertTime;

    private Integer disabled;

}
