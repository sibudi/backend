package com.yqg.drools.model;

import com.yqg.common.utils.JsonUtils;
import com.yqg.service.third.izi.response.IziResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Date;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 * 用户信息
 ****/

@Getter
@Setter
public class RUserInfo {

    private String idCard;
    private Integer age;
    private String homeAddress;
    private Integer userRole;
    private String schoolAddress;
    private String companyAddress;
    private String orderAddress;


    private Boolean isCompanyTelphoneNotInJarkat;//公司电话是否属于Jarkat

    private Boolean homeAddressNotBelongToJarkat;
    private Boolean schoolAddressNotBelongToJarkat;
    private Boolean companyAddressNotBelongToJarkat;
    private Boolean orderAddressNotBelongToJarkat;

    private Boolean companyAddressNotBelongToJarkatNormal; //新的公司地址不属于开放城市

    private Boolean companyAddressNotBelongToJarkatIOS; //公司地址不属于开放城市-iOS

    private BigDecimal yituScore;//yitu分数

    private BigDecimal facePlusPlusScore;// face++分数

    private IdentityVerifyResult advanceVerifyResult;//实名验证结果

    private int sex; //1:男 2:女

    private boolean hasDriverLicense;

    private String companyAddrProvice;// 公司地址：省
    private String companyAddrCity;// 公司地址：市
    private String homeAddrProvice;// 居住地址：省
    private String homeAddrCity;// 居住地址：市


    private Integer childrenAmount = 0;// 孩子数量

    private String academic;//学历
    private Integer maritalStatus;//婚姻状况

    private Integer thirdType;//1:cashcash

    private Integer mobileAsEmergencyTelCount;//借款人手机号码已作为紧急联系人出现次数

    private Boolean hasInsuranceCard;//有保险卡

    private Boolean hasFamilyCard;// 有家庭卡

    private Boolean hasTaxNumber;//有税卡

    private Boolean hasPayroll; //有工资单

    private String positionName;//职业

    private Boolean hitOverDuePositionMan;//男性高逾期职业

    private Boolean hitOverDuePositionFeMen; //女性高逾期职业

    private Boolean hitHomeProviceMan;//男性高风险地址（省）

    private Boolean hitHomeProviceFeMen; //女性高风险地址（省）

    private Boolean hitHomeProviceMan150;//男性高风险地址（省150）

    private Boolean hitHomeProviceFeMen150; //女性高风险地址（省 150）

    private Boolean hitHomeProviceMan80;//男性高风险地址（省80）

    private String dependentBusiness;//行业

    private String religionName;//宗教

    private BigDecimal monthlyIncome;//月收入

    private Boolean firstLinkmanExists;  //第一联系人已经存在
    private Boolean firstLinkmanExistsRelatedUserSettled;//首个第一紧急联系人相同的用户的订单是否settle
    private Boolean emailExists; //邮件已经存在
    private Boolean firstEmailExistsRelatedUserSettled;//首个email相同的用户的订单是否settle

    private Boolean linkmanExists;//联系人是否已经存在

    private Boolean existsLinkmanHasSettledOrder;//首个相同联系人对应的用户有完成的订单
    private Boolean existsLinkmanWithoutSuccessOrder;//首个相同联系人对应的用户没有成功的订单

    private Boolean workAddressExists; //工作地址相同
    private Boolean orderAddressExists; //订单地址相同
    private Boolean homeAddressExists;  //居住地址相同

    private IziPhoneAgeResult iziPhoneAgeResult; //izi在网时长结果

    private IziPhoneVerifyResult iziPhoneVerifyResult; //izi手机实名结果

    private String companyTel;
    private String companyName;

    private String whatsappAccount;

    private String whatsappAccountStr;//直接返回没有处理的whatsApp值

    private String mobileNumber;

//    private Boolean idCardBirthdayNotSameWithUserDetail;
//
//    private Boolean idCardSexNotSameWithUserDetail;

    private Integer idCardSex;

    private String idCardBirthday;
    private String birthday;

    private Integer countOfOverdueLessThan5UsersByEmergencyTel; //联系人是逾期小于5天的用户

    private Boolean hasCreditCard;

    private Boolean gojekVerified;

    private String bankCode;

    private String whatsAppCheckResult;//yes no checking

    private String whatsAppYesRadio;//所有紧急联系人通过占比

    private Boolean linkmanInMultiBankcardUser;//联系人在相同卡号用户列表中

    private Boolean mobileInMultiBankcardUserEmergencyTel; //手机号在相同卡号用户的紧急联系人列表中

    private Integer  sameBankcardNumberWithOthersCount;//和本人相同银行卡的用户数量

    private Boolean orderSmallDirectIsNull;//下单的小区为空

    private Boolean mobileInOverdueLessThan5UserEmergencyTel; //号码是逾期小于5天的用户的紧急联系人

    private Long linkmanWhatsAppCheckWithNoCount;//联系人whatsapp检查结果返回No的个数


    private IziWhatsAppDetail ownerWhatsAppDetail;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IziPhoneAgeResult{
        private PhoneAgeStatusEnum status;

        private Integer age;

        public enum PhoneAgeStatusEnum{
            OK,
            INVALID_PHONE_NUMBER,
            PHONENUMBER_NOT_FOUND,
            RETRY_LATER,
            UN_KNOWN
        }
        public static PhoneAgeStatusEnum valueOfStatus(String status) {
            try {
                return PhoneAgeStatusEnum.valueOf(status);
            } catch (Exception e) {
                return PhoneAgeStatusEnum.UN_KNOWN;
            }
        }

        public static IziPhoneAgeResult parseResultFromResponse(IziResponse iziResponse) {
            if (iziResponse != null) {
                PhoneAgeStatusEnum status = IziPhoneAgeResult.valueOfStatus(iziResponse.getStatus());
                Integer age = null;
                if (status.equals(PhoneAgeStatusEnum.OK) && iziResponse.getMessage() != null) {
                    Map<String, Object> msg = JsonUtils.toMap(JsonUtils.serialize(iziResponse.getMessage()));
                    if (msg != null && msg.get("age") != null) {
                        age = Integer.valueOf(msg.get("age").toString());
                    }
                }
                return new IziPhoneAgeResult(status, age);
            }
            return null;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IziPhoneVerifyResult{

        private PhoneVerifyStatusEnum status;

        private String message;

        public enum PhoneVerifyStatusEnum{
            OK,
            NOT_FOUND,
            INVALID_PHONE_NUMBER,
            INVALID_ID_NUMBER,
            UN_KNOWN
        }

        public static PhoneVerifyStatusEnum valueOfStatus(String status) {
            try {
                return PhoneVerifyStatusEnum.valueOf(status);
            } catch (Exception e) {
                return PhoneVerifyStatusEnum.UN_KNOWN;
            }
        }

        public static IziPhoneVerifyResult parseResultFromResponse(IziResponse iziResponse) {
            if (iziResponse != null) {
                String message = iziResponse.getMessage() == null ? null : iziResponse.getMessage().toString();
                return new IziPhoneVerifyResult(IziPhoneVerifyResult.valueOfStatus(iziResponse.getStatus()), message);
            }
            return null;
        }
    }



    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static  class IziWhatsAppDetail{
        private String whatsAppOpenStatus;
        private String statusUpdateTime;
        private String avatar;
    }


    @Getter
    public enum SexEnum{
        MALE(1),
        FEMALE(2);
        SexEnum(int code){
            this.code = code;
        }
        private int code;
    }

    @Getter
    public enum EducationEnum{
        PrimarySchool("Sekolah dasar"),//小学
        JuniorMiddleSchool("Sekolah Menengah Pertama"),//初中
        HighSchool("Sekolah Menengah Atas"),//高中
        Specialty("Diploma"), //专科
        Undergraduate("Sarjana"),//大学
        GraduateStudent("Pascasarjana"),//研究生
        Doctor("Doktor"),//博士生
        ;
        EducationEnum(String code){
            this.code = code;
        }
        private String code;
    }

    //0 单身(single)、1已婚(menikah)、2离异(cerai hidup)、3丧偶(Janda/duda)
    @Getter
    public enum MarriageEnum{
        Single(0),
        MARRIED(1),
        DIVORCED(2),
        WIDOWED(3)
        ;
        MarriageEnum(int code){
            this.code = code;
        }
        private int code;
    }



    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IdentityVerifyResult{
        private IdentityVerifyResultType advanceVerifyResultType;
        private String desc;

        public boolean isXenditFailed(){
            switch (advanceVerifyResultType){
                case XENDIT_REALNAME_VERIFY_DISMATCH:
                case XENDIT_REALNAME_VERIFY_RESPONSE_EMPTY:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isAdvanceFailed() {
            switch (advanceVerifyResultType) {
                case EXCEPTION:
                case INTERFACE_FAILED:
                case ID_NUMBER_EMPTY:
                case NAME_EMPTY:
                case NAME_FUZZY_MATCH_FAILED:
                    return true;
                default:
                    return false;
            }
        }

    }

    @Getter
    public enum IdentityVerifyResultType {
        EXCEPTION(),//异常
        INTERFACE_FAILED,//接口返回异常
        ID_NUMBER_EMPTY,//身份证号为空
        NAME_EMPTY,//姓名为空
        NAME_FULL_MATCH_SUCCESS,//姓名完全匹配成功
        NAME_FUZZY_MATCH_SUCCESS,//姓名模糊匹配成功
        NAME_FUZZY_MATCH_FAILED,//姓名模糊匹配失败
        FORWARD_MATCH_SUCCESS,//以前实名成功
        JUXINLI_MATCH_SUCCESS,//聚信立实名成功
        TAX_VERIFY_FULL_MATCH,//根据税卡姓名完全匹配
        TAX_VERIFY_FUZZY_MATCH_SUCCESS,//根据税卡模糊匹配成功
        TAX_VERIFY_FUZZY_MATCH_FAILED,//根据税卡模糊匹配失败
        TAX_VERIFY_ERROR,//校验接口错误
        TAX_VERIFY_RESPONSE_EMPTY,//校验返回信息为空
        TAX_NUMBER_VERIFY_ALREADY_SUCCESS,// 税卡已经实名验证通过
        REAL_NAME_TAX_NUMBER_BLACKLIST,//首借用户实名税卡相同黑名单
        TAX_VERIFY_NEED_RETRY,//税卡实名需要重试【后续重跑】
        XENDIT_REALNAME_VERIFY_RESPONSE_EMPTY,//xendit实名返回数据为空
        XENDIT_REALNAME_VERIFY_MATCH,//xendit实名匹配
        XENDIT_REALNAME_VERIFY_DISMATCH,//xendit实名不匹配
        XENDIT_REALNAME_VERIFY_ALREADY_SUCCESS,//xendit已经实名验证通过
        IZI_PHONE_IDCARD_VERIFY_SUCCESS,//izi 手机身份证号实名通过
        IZI_PHONE_IDCARD_VERIFY_FAILED,//izi 手机身份证号实名失败
        IZI_PHONE_IDCARD_VERIFY_ALREADY_SUCCESS,//izi实名验证已经通过

        IZI_REAL_NAME_VERIFY_ALREADY_SUCCESS,//izi已经实名通过

    }

}
