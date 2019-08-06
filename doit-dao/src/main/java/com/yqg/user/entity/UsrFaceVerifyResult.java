package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Table("usrFaceVerifyResult")
@Getter
@Setter
public class UsrFaceVerifyResult extends BaseEntity implements Serializable {
    private BigDecimal score;
    private String channel;
    private String userUuid;
    private String orderNo;
}
