package com.yqg.service.externalChannel.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.service.externalChannel.request.Cash2BaseInfo.OrderInfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/9
 *
 ****/

@Getter
@Setter
public class Cash2AdditionalInfoParam extends Cash2BaseRequest {

    @JsonProperty(value = "order_info")
    private OrderInfo orderInfo;

    @JsonProperty(value = "apply_detail")
    private ApplyDetail applyDetail;



    @Getter
    @Setter
    public static class ApplyDetail {

        @JsonProperty(value = "bio_assay_image")
        private List<String> imgList;//活体检查图片

        @JsonProperty(value = "confidence")
        private BigDecimal confidence;//活体对比阀值

        @JsonProperty(value = "is_bio_assay")
        private String result; //0否，1是

        private String birthday; //出生日期YYYY-MM-DD

        @JsonProperty("user_mail")
        private String userEmail;

        @JsonProperty(value = "address_province")
        private String addressProvince;

        @JsonProperty(value = "address_city")
        private String addressCity;

        @JsonProperty(value = "address_large_district")
        private String addressLargeDistrict;

        @JsonProperty(value = "address_small_district")
        private String addressSmallDistrict;

        @JsonProperty(value = "address_street")
        private String addressStreet;


        @JsonProperty(value = "company_name")
        private String companyName;

        @JsonProperty(value = "job_position")
        private int jobPosition;


        @JsonProperty(value = "commany_mobile")
        private String companyMobile;

        @JsonProperty(value = "mother_name")
        private String motherName;

        @JsonProperty(value = "commany_address_province")
        private String companyAddressProvince;

        @JsonProperty(value = "commany_address_city")
        private String companyAddressCity;

        @JsonProperty(value = "commany_address_large_district")
        private String companyAddressLargeDistrict;

        @JsonProperty(value = "commany_address_small_district")
        private String companyAddressSmallDistrict;

        @JsonProperty(value = "commany_address_street")
        private String companyAddressDetail;

        @JsonProperty(value = "payroll")
        private String payroll;//工资单图片

        @JsonProperty(value = "bank_wage_water")
        private String bankStatementImg;//银行流水照片


        @JsonProperty(value = "wage_proof")
        private String workProofImg;//工作证明图片

        @JsonProperty(value = "tax_card_number")
        private String taxCardNumber;//税卡卡号

        @JsonProperty(value = "family_card")
        private String familyCard;//家庭卡url

        @JsonProperty(value = "insurance_card")
        private String insuranceCard;//保险卡url



//        1公司宿舍
//        2父母住房
//        3租房
//        4自有住房
//        5其他
        @JsonProperty(value = "live_type")
        private String liveType; //居住类型

//        1（0-5个月）
//        2（6-11个月）
//        3（1-3年）
//        4（4-7年）
//        5（7年以上）
        @JsonProperty(value = "live_time")
        private String liveTime; //居住时长

        @JsonProperty(value = "driver_license_image")
        private String driverLicenseImage;//驾驶证照片

        @JsonProperty(value = "emergency_contact_name")
        private String emergencyContactName;

        @JsonProperty(value = "emergency_contact_mobile")
        private String emergencyContactMobile;

        @JsonProperty(value = "emergency_contact_relation")
        private int emergencyContactRelation;

        @JsonProperty(value = "emergency_contact_name_2")
        private String emergencyContactName2;

        @JsonProperty(value = "emergency_contact_mobile_2")
        private String emergencyContactMobile2;

        @JsonProperty(value = "emergency_contact_relation_2")
        private int emergencyContactRelation2;

        @JsonProperty(value = "emergency_contact_name_3")
        private String emergencyContactName3;

        @JsonProperty(value = "emergency_contact_mobile_3")
        private String emergencyContactMobile3;

        @JsonProperty(value = "emergency_contact_relation_3")
        private int emergencyContactRelation3;
        @JsonProperty(value = "emergency_contact_name_4")
        private String emergencyContactName4;

        @JsonProperty(value = "emergency_contact_mobile_4")
        private String emergencyContactMobile4;

        @JsonProperty(value = "emergency_contact_relation_4")
        private int emergencyContactRelation4;

        @JsonProperty(value = "reserve_mobile")
        private String reserveMobile;


        @JsonProperty(value = "school_name")
        private String schoolName;

        @JsonProperty(value = "school_specialty")
        private String major;

        @JsonProperty(value = "school_enrolment_time")
        private String schoolEnrolmentTime; //"YYYY-MM-DD"

        @JsonProperty(value = "student_id")
        private String studentId;

        @JsonProperty(value = "school_address_province")
        private String schoolAddressProvince;

        @JsonProperty(value = "school_address_city")
        private String schoolAddressCity;

        @JsonProperty(value = ".school_address_large_district")
        private String schoolAddressLargeDistrict;

        @JsonProperty(value = "school_address_small_district")
        private String schoolAddressSmallDistrict;

        @JsonProperty(value = "school_address_street")
        private String schoolAddressDetail;

        @JsonProperty(value = "family_number")
        private Integer familyNumber;//家庭成员数量

        @JsonProperty(value = "family_income")
        private Integer familyMonthlyIncome;//家庭年收入

        @JsonProperty(value = "father_name")
        private String fatherName;//父亲姓名

        @JsonProperty(value = "father_mobile")
        private String fatherMobile;//父亲电话

        @JsonProperty(value = "father_position")
        private int fatherPosition;//父亲职位

        @JsonProperty(value = "mother_mobile")
        private String motherMobile;//母亲电话

        @JsonProperty(value = "mother_position")
        private int motherPosition;//母亲职位

        @JsonProperty(value = "living_condition")
        private int livingCondition; //0在校，1在家跟父母，2在外居住

        @JsonProperty(value = "student_idcard")
        private String studentIdCardImage; //学生证照片

        @JsonProperty(value = "english_certificate_image")
        private String englishCertificateImage;//英语证书照片

        @JsonProperty(value = "scholarship_certificate_image")
        private String scholarshipCertificateImage;//奖学金证书照片

        @JsonProperty(value = "campus_card_image")
        private String campusCardImage;//校园卡照片

        @JsonProperty(value = "other_certificate_image")
        private String otherCertificateImage;//其他大赛照片

        @JsonProperty(value = "whats_app")
        private String whatsApp;// 用户whatsapp账号
    }




    @Getter
    public enum EmergencyContactRelationEnum {

        PARENT(1, "Orang tua"),
        SPOUSE(2, "Pasangan"),
        BROTHER_OR_SISTER(3, "Saudara / saudari"),
        RELATIVE(4, "Keluarga dekat"),
        FRIEND(5, "Teman"),
        COLLEAGUE(6, "Rekan kerja"),;

        EmergencyContactRelationEnum(int code, String name) {
            this.code = code;
            this.name = name;
        }

        private int code;
        private String name;

        public static EmergencyContactRelationEnum getByCode(int code) {
            return Arrays.asList(EmergencyContactRelationEnum.values()).stream()
                .filter(elem -> elem.getCode() == code).findFirst().get();
        }
    }


    @Getter
    public enum JobPositionEnum {

        CIVIL_SERVANT(1, "Pegawai negeri"),//公务员
        Worker(2, "Pekerja"),//工人
        Farmer(3, "Petani"),//农民
        RELATIVE(4, "Pengemudi"),//司机
        FRIEND(5, "Pembersih"),//清洁工
        Staff(6, "Staf perusahaan"),//公司职员
        House_Keeper(7, "Pembantu rumah tangga"),//保姆
        Cooker(8, "Koki"),//厨师
        Waiter(9, "Pelayan"),//服务员
        Doctor(10, "Staf medis"),//医护人员
        Teacher(11, "Guru"),//教师
        Courier(12, "Kurir"),//快递员
        Seller(13, "Staf Penjualan"),//销售
        Security_Guard(14, "Keamanan"),//保安
        Engineer(15, "Insinyur"),//工程师
        Individual_Business(16, "Vendor individu"),//个体商贩
        HouseWife(17, "Ibu rumah tangga"),//家庭主妇
        Other(18, "Lainnya"),//其它
        ;

        JobPositionEnum(int code, String name) {
            this.code = code;
            this.name = name;
        }

        private int code;
        private String name;

        public static JobPositionEnum getByCode(int code) {
            return Arrays.asList(JobPositionEnum.values()).stream()
                .filter(elem -> elem.getCode() == code).findFirst().get();
        }
    }


}
