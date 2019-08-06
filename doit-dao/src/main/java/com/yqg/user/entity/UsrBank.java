package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * ?????
 * Created by Didit Dwianto on 2017/11/25.
 */
@Data
@Table("usrBank")
public class UsrBank extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -8652690597276721385L;
    private String userUuid;//??uud
    private String bankId;//??id
    private String bankCode;//?????
    private String bankNumberNo;//????
    private String bankCardName;//???????
    private Integer status;//????(0=????1=???,2=??,3=??)
    private Integer bankorder;//??
    private Integer isRecent;//?????????1=??0=?????0?

    private Integer thirdType;

}
