package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by wanghuaizhou on 2018/12/7.
 */
@Data
@Table("usrIziVerifyResult")
public class UsrIziVerifyResult extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -8619088953077314760L;

    private String userUuid;
    private String orderNo;
    private String iziVerifyType;  // Izi验证类型（1在网时长 2 手机号码实名认证）
    private String iziVerifyResult; // Izi验证结果
    private String iziVerifyResponse; // Izi验证返回response






}
