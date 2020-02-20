package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("usrUser")
public class UsrUser extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 8381134355320645178L;

    private String mobileNumber;
    private String mobileNumberDES;
    private String realName;
    private Integer status;// ?????1=???0=?????????
    private String idCardNo;
    private Integer sex;
    private String loginPwd;
    private String payPwd;
    private Integer userSource;  // 1 ?? 2 iOS 3h5  28CashCash
    private Integer productLevel;
    private Integer userRole;  // 身份（角色)（1=学生，2=已工作 3 =家庭主妇)
    private Integer userType;  // 1 个人 2 公司
    private String companyBusinessCode;  // 公司营业执照号 （userType为2的时候才有）
    private String companyType;  // 公司类型 （userType为2的时候才有）
    private String registeredLegalName;  // 注册法人名称 （userType为2的时候才有）
    private Integer age;  // 年龄
    private Integer isInvited;

    /**
     *   第三方撞库命中 1 CashCash命中Do-It黑名单
     *                2 CashCash命中Do-It有订单未结束的用户
     *                3 CashCash命中Do-It在机审拒绝期间内的用户
     *                4 Cheetah中Do-It黑名单
     *                5 Cheetah命中Do-It有订单未结束的用户
     *                6 Cheetah命中Do-It在机审拒绝期间内的用户
     * */
    private Integer thirdHit;

    private Integer payDay; //发薪日 （催收人员催收时生成)

    /**
     * 用户拉黑的原因 1.客户本人原因 2.亲属代替申请  3.高风险客户 4.被盗用信息
     */
    private Integer addBlackReason;

    /**
     * 用户拉黑时填写的备注
     */
    private String addBlackRemark;

    private String emailAddress;

    private Integer isMobileValidated;

    public enum AddBlackReasonEnum {

        SELF_RESAON(1,"客户本人原因"),
        OTHER_APPLAY(2,"亲属代替申请"),
        HIGH_RISK_USER(3,"高风险客户"),
        CHEAT_PERSON(4,"被盗用信息");

        private Integer code;
        private String name;

        AddBlackReasonEnum(Integer code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}
