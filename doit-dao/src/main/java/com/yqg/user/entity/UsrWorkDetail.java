package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import com.yqg.common.utils.StringUtils;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("usrWorkDetail")
public class UsrWorkDetail extends BaseEntity implements Serializable{

    private static final long serialVersionUID = 5778886069444593000L;
    private String userUuid;
    private String email;
    private String academic;
    private Integer maritalStatus;
    private String birthday;
    private String religion;
    private String motherName;
    private Integer childrenAmount;
    private String companyName;
    private String positionName;
    private String monthlyIncome;
    private String companyPhone;
    private String borrowUse;//
    private String dependentBusiness;//


    public String getReplaceMonthlyIncome() {
        if (StringUtils.isEmpty(monthlyIncome)) {
            return "";
        }
        return monthlyIncome.replaceAll("\\.","");
    }

    // 员工身份号码
    private String employeeNumber;
    // 分机号码
    private String extensionNumber;
}