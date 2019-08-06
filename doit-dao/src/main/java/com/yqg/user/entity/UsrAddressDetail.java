package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("usrAddressDetail")
public class UsrAddressDetail  extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5799354450348940653L;

    private String userUuid;//
    private Integer addressType;//?????0?????1?????2?????3????
    private String province;//
    private String city;//
    private String bigDirect;//
    private String smallDirect;//
    private String detailed;//????
    private Integer status;//
    private String lbsX;//??x
    private String lbsY;//??y
    private String region;// ??
//    private String birthProvince;// ???:?
//    private String birthCity;// ???:?
//    private String birthBigDirect;// ???:??
//    private String birthSmallDirect;// ???:??
}