package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;

import lombok.Data;


@Data
@Table("p2pUser")
public class P2PUser extends BaseEntity{

    private String userName;//
    private String mobileNumber;//
    private String realName;//
    private String idCardNo;//
    private String birthDate;//
    private Integer age;//
    private Integer sex;//
    private String education;//
    private String job;
    private String workField;//
    private String religion;
    private String companyBusinessCode;
    private Integer companyType;//
    private String registeredLegalName;//
    private String organizerCode;//
    private String npwpNo;//
    private String yearSalary;//
    private String workTime;//
    private Integer salaryHomeValue;//
    private String otherSalaryFrom;//

}
