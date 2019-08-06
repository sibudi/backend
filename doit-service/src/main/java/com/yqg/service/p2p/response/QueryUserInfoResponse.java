package com.yqg.service.p2p.response;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/9/6.
 */
@Data
public class QueryUserInfoResponse {

    private String userUuid;  // 借款人ID
    private String teleNumber;  // 手机号
    private String realName;  // 真实姓名
    private String idCardNumber; // 身份证号
    private String birthday; //出生日期
    private Integer age;  //年龄
    private Integer sex;  //性别 0：未知，1：男，2：女
    private String academic;  // 学历
    private Integer userRole;  // 借款人身份 0未知 1学生 2已工作 3家庭主妇
    private Integer maritalStatus;//婚姻状况  0 单身(single)、1已婚(menikah)、2离异(cerai hidup)、3丧偶(Janda/duda)
    private String email; // 邮箱
    private String religion; // 宗教
    private String liveAddress; // 居住地址
    private String hasIdentity;  // 是否认证身份信息
    private String hasBank; // 是否认证银行卡信息
    private String hasContact; // 是否认证联系人信息
    private String hasInsuranceCard = "0"; // 是否认证保险卡信息
    private String hasFamilyCard = "0"; // 是否认证家庭卡信息

}
