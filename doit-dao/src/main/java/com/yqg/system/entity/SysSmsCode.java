package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

/**
 * Created by luhong on 2018/3/2.
 */
@Data
@Table("sysSmsCode")
public class SysSmsCode extends BaseEntity {
    private String smsCode;// 验证码
    private String mobile;// 手机
    private Integer smsType;// 1=登陆 2=注册
}
