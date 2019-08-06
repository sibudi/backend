package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by wanghuaizhou on 2018/8/17.
 */
@Data
@Table("usrHouseWifeDetail")
public class UsrHouseWifeDetail extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -8230659283385505016L;

    private String userUuid;

    private String email;
    private String academic;
    private Integer maritalStatus;
    private String birthday;
    private String religion;
    private String motherName;
    private Integer childrenAmount;
    private String borrowUse;//

    // 家庭月收入
    private String homeMouthIncome;
    // 主要家庭收入方式
    private String incomeType;
    // 主要家庭收入来源者
    private String incomtSource;

    // 主要家庭收入来源者相关信息
    // 姓名
    private String sourceName;
    // 收入
    private String sourceTel;
    // 月收入
    private String mouthIncome;
    private String workType;


    // 是否是公司员工  0 是 1 不是
    private Integer isCompanyUser;
    private String companyName;
    private String companyPhone;
    // 如果不是公司员工
    // 收入的主要方式
    private String incomeWithNoCom;

}
