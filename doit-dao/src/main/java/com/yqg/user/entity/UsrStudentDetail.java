package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("usrStudentDetail")
public class UsrStudentDetail extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5018772178083165032L;
    private String userUuid;
    private String email;
    private String academic;
    private String birthday;
    private Integer familyMemberAmount;
    private String familyAnnualIncome;
    private String fatherName;
    private String fatherMobile;
    private String fatherPosition;
    private String motherName;
    private String motherMobile;
    private String motherPosition;
    private Integer dwellingCondition;
    private String schoolName;
    private String major;
    private String startSchoolDate;
    private String studentNo;

    private String borrowUse;

    // 家庭主妇需求 新增
    // 月生活费
    private String mouthCost;
    // 月生活费来源
    private String mouthCostSource;
    // 是否在做兼职   1 是 2不是
    private String isPartTime;

    // 兼职名称
    private String partTimeName;

    // 如果有兼职
    // 兼职类型
    private String partTimeType;
    // 兼职收入
    private String partTimeIncome;
    // 兼职证明人姓名
    private String partTimeProveName;
    // 兼职证明人手机号
    private String partTimeProveTele;

    // 同班同学姓名
    private String classmateName;
    // 同班同学手机号
    private String classmateTele;
}