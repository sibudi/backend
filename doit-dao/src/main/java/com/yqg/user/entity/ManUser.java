package com.yqg.user.entity;


import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Table("manUser")
public class ManUser extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1423267946472768743L;

    private String username;

    private String password;

    private String realname;

    private String mobile;

    private String email;

    private String ipAddress;

    private Date lastLoginTime;

    private Integer status;

    private Integer third;

    private Integer country;

    private Integer thirdPlatform;

    private Integer parentId;

    /**
     * 催收人员电话
     */
    private String collectionPhone;

    /**
     * 催收人员WA
     */
    private String collectionWa;

    /**
     * 员工号码
     */
    private String employeeNumber;
}
