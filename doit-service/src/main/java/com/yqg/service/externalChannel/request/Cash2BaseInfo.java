package com.yqg.service.externalChannel.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/6
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Getter
@Setter
public class Cash2BaseInfo extends Cash2BaseRequest {

    @JsonProperty(value = "is_reloan")
    private int isReloan;//是否复借

    @JsonProperty(value = "order_info")
    private OrderInfo orderInfo;

    @JsonProperty(value = "apply_detail")
    private ApplyDetail applyDetail;

    @JsonProperty(value = "add_info")
    private AdditionInfo addInfo;

    @Getter
    @Setter
    public static class AdditionInfo {

        @JsonProperty("device")
        private DeviceInfo deviceInfo;
    }


    @Getter
    @Setter
    public static class OrderInfo {

        @JsonProperty(value = "order_no")
        private String orderNo;

        @JsonProperty(value = "application_amount")
        private BigDecimal applicationAmount;

        @JsonProperty(value = "application_term")
        private int applicationTerm;

        @JsonProperty(value = "term_unit")
        private int termUnit;

        @JsonProperty(value = "order_time")
        private int orderTime;

        private int status;

        @JsonProperty(value = "product_id")
        private int productId;

        @JsonProperty(value = "product_name")
        private String productName;

        @JsonProperty(value = "user_name")
        private String userName;

        @JsonProperty(value = "user_mobile")
        private String userMobile;

        @JsonProperty(value = "user_idcard")
        private String userIdCard;
    }


    @Getter
    @Setter
    public static class ApplyDetail {

        @JsonProperty(value = "user_type")
        private int userType; //1已工作，2学生

        @JsonProperty(value = "idcard_image_front")
        private String idCardFrontImage;

        @JsonProperty(value = "idcard_image_reverse_side")
        private String idCardReverseSizeImage;

        @JsonProperty(value = "idcard_image_hand")
        private String idCardHandImage;//手持身份证照

        private int education;  //1：小学2：初中3：高中4：专科5：本科6：研究生7：博士

        @JsonProperty(value = "loan_remark")
        private int loanRemark; //贷款用途

        @JsonProperty(value = "month_income")
        private String monthlyIncome;

        private int religion; //宗教

        @JsonProperty(value = "marital_status")
        private int maritalStatus; //0 单身(single)、1 已婚(menikah)、2 离异(cerai hidup)、3 丧偶(Janda / duda)

        @JsonProperty(value = "user_sex")
        private int userSex;  //1：男2：女

        private int children;//子女数量
    }

    @Getter
    @Setter
    public static class DeviceInfo {

        @JsonProperty(value = "deviceid")
        private String deviceId;

        @JsonProperty(value = "device_name")
        private String deviceName;
        @JsonProperty(value = "device_model")
        private String deviceModel;
        @JsonProperty(value = "system_version")
        private String systemVersion;

        private BigDecimal longitude;

        private BigDecimal latitude;

        @JsonProperty(value = "order_gps_address_province")
        private String orderProvince;
        @JsonProperty(value = "order_gps_address_city")
        private String orderCity;
        @JsonProperty(value = "order_gps_address_large_district")
        private String orderLargeDistrict;
        @JsonProperty(value = "order_gps_address_small_district")
        private String orderSmallDistrict;
        @JsonProperty(value = "order_gps_address_street")
        private String orderAddressDetail;

        private String ip;
        @JsonProperty(value = "memory_size")
        private String memorySize;
        @JsonProperty(value = "internal_storage_total")
        private String internalStorageTotal; //总内部存储容量
        @JsonProperty(value = "internal_storage_usable")
        private String internalStorageUsable;//可用的内部存储容量
        @JsonProperty(value = "network_type")
        private String networkType;

        @JsonProperty(value = "wifi_data")
        private List<WifiData> wifiDataList;

        private String battery;

        @JsonProperty(value = "root_jailbreak")
        private int rootJailBreak; //是否root或越狱 0不是，1是
        private String dns;
        private int simulator;
        @JsonProperty(value = "android_id")
        private String androidId;

        @JsonProperty(value = "image_num")
        private int imageNum;
        private String uuid;
        private String imei;
        private String mac;
        @JsonProperty(value = "memory_card_size")
        private String memoryCardSize;
        @JsonProperty(value = "memory_card_size_use")
        private String memoryCardSizeUsed; //已使用量

        @JsonProperty(value = "locale_iso_3_language")
        private String language;//语言

        private String imsi;

        @JsonProperty(value = "ram_usable_size")
        private String remainingMemory;

        @JsonProperty(value = "last_boot_time")
        private String lastPowerOnTime;//最后一次开机时间

        @JsonProperty(value = "last_close_time")
        private String lastCloseTIme;//最后一次关机时间（暂无）

        @JsonProperty(value = "brand")
        private String phoneBrand;

        @JsonProperty(value = "cpu_type")
        private String cpuType;


        @JsonProperty(value = "app_data")
        private List<InstalledAppData> installedAppList;

        @JsonProperty(value = "contact_data")
        private List<ContactData> contactList;//联系人列表(通讯录)

        @JsonProperty(value = "tel_data")
        private List<TelData> telList;//通话记录

        @JsonProperty(value = "sms_data")
        private List<SmsData> smsList;

        @JsonProperty(value = "operator_data")
        private OperatorData operatorData;

    }

    @Getter
    @Setter
    public static class WifiData {

        private String name;
        private String mac;
    }

    @Getter
    @Setter
    public static class InstalledAppData {

        @JsonProperty(value = "package")
        private String packageName;//包名
        @JsonProperty(value = "in_time")
        private Date installTime;//安装时间
        @JsonProperty(value = "up_time")
        private Date updateTime;//更新时间

        private String appName;
    }

    @Getter
    @Setter
    public static class ContactData {

        private String name;
        private String number;
        @JsonProperty(value = "up_time")
        private Long updateTime;
    }

    @Getter
    @Setter
    public static class TelData {

        private String number;
        private String type;//1呼出，2呼入
        private String time;//YYYY-MM-DD HH:mm:ss
        @JsonProperty(value = "time_length")
        private Integer duration;

        private String name;
    }

    @Getter
    @Setter
    public static class SmsData {

        private String number;
        private String type;//1呼出，2呼入
        private String content;
        private String time;//YYYY-MM-DD HH:mm:ss
    }

    @Getter
    @Setter
    public static class OperatorData {

        private String name;//运营商名称
    }


    @Getter
    public enum Cash2UserType {

        Working(1),
        Student(2);

        Cash2UserType(int code) {
            this.code = code;
        }

        private int code;
    }

    @Getter
    public enum EducationEnum {
        PRIMARY_SCHOOL(1, "Sekolah dasar"),
        MIDDLE_SCHOOL(2, "Sekolah Menengah Pertama"),
        HIGH_SCHOOL(3, "Sekolah Menengah Atas"),
        JUNIOR_COLLEGE(4, "Diploma"), //专科
        BACHELOR(5, "Sarjana"), //大学
        MASTER(6, "Pascasarjana"),//硕士
        DOCTOR(7, "Doktor"),//博士
        ;

        EducationEnum(int code, String name) {
            this.code = code;
            this.name = name;
        }

        private int code;
        private String name;

        public static EducationEnum getByCode(int code) {
            return Arrays.asList(EducationEnum.values()).stream()
                .filter(elem -> elem.getCode() == code).findFirst().get();
        }
    }

    @Getter
    public enum ReligionEnum {

        Islam(1, "Islam"), //伊斯兰教
        Christianity(2, "Kristen Protestan"),//基督教
        Catholicism(3, "Kristen Katolik"),//天主教
        Hinduism(4, "Hindu"), //印度教
        Buddhism(5, "Buddha"), //佛教
        Confucianism(6, "Konghucu"),//孔教/儒教
        ;

        ReligionEnum(int code, String name) {
            this.code = code;
            this.name = name;
        }

        private int code;
        private String name;

        public static ReligionEnum getByCode(int code) {
            return Arrays.asList(ReligionEnum.values()).stream()
                .filter(elem -> elem.getCode() == code).findFirst().get();
        }
    }


    @Getter
    public enum LoanPurpose {
        EDUCATION(1, "Pendidikan"),//教育
        Decoration(2, "Renovasi"),//装修
        Travel(3, "Liburan"),//旅游
        Marriage(4, "Pernikahan"),//婚庆
        MobileAndDigital(5, "Gadget"),//手机数码
        Renting(6, "Uang sewa"),//租房
        Furniture(7, "Peralatan rumah tangga"),//家具家居
        HealthCare(8, "Pengobatan"),//健康医疗
        Other(9, "Lainnya"),//其它
        ;

        LoanPurpose(int code, String name) {
            this.code = code;
            this.name = name;
        }

        private int code;
        private String name;

        public static LoanPurpose getByCode(int code) {
            return Arrays.asList(LoanPurpose.values()).stream()
                .filter(elem -> elem.getCode() == code).findFirst().get();
        }
    }

}
