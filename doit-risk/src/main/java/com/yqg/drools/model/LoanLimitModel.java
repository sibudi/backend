package com.yqg.drools.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanLimitModel {
    private Boolean isProduct50;
    private Boolean isProduct100;
    private Integer sex;
    private Long lastLoanOverdueDays;
    private Boolean mobileIsEmergencyTelForMoreThan3TimesUser;
    private Boolean emergencyTelIsUpLimitUser; //紧急联系人是提额用户
    private Boolean userInHighQualityIndustry;
    private Boolean workCityInHighQualityCity;
    private Integer companyTelResultInFirstBorrow;
    private Integer appCountForBank;
    private Integer appCount;
    private Integer appCountForCreditCard;
    private Integer appCountForEcommerce;
    private Boolean gojekVerifed;
    private Integer iziPhoneAge;
    private Long diffMinutesForCreateAndSubmitTime;

    private Boolean sameForWhatsappAndMobile;
    private Integer age;
    private Boolean hasSalaryPic;
    private Boolean hasDriverLicense;
    private RUserInfo.IziPhoneVerifyResult iziPhoneVerifyResult;

    private BigDecimal totalSpace;

    private Boolean emergencyTelIsFirstBorrowNotOverdueUser;
    private String academic;
    private Integer userRole;

    //50
    private Long diffMinutesForRegisterAddOrderCreate;
    private String firstLinkmanName;
    private String iziWhatsappOpenResult;
    private Boolean userInHighQualityLiveCity;
    private Integer validBankCount;

    private Boolean userInHighQualityIndustryFor50;
    private Boolean userInHighQualityPostFor50;
    private Boolean inFirstBorrowingNotOverdueUserList;




    public static String highQualityIndustryForFemalePRD100 = "Alat Elektronik, Perlengkapan dan Perabotan Rumah Tangga / Kantor#Retail & " +
            "Distribusi#Penginapan, Perjalanan & Pariwisata#Asuransi#Pendidikan & Pelatihan#Lembaga Pemerintah, Non Profit, NGO & Facilitas Publik#Real Estate & Properti";

    public static String highQualityIndustryForMalePRD100 = "Manufaktur Lainnya#Retail & Distribusi#Transportasi & Logistik#Real Estate & Properti#Penginapan, Perjalanan & Pariwisata#Pendidikan & Pelatihan#Alat Berat & Peralatan Industri#Business Process Outsourcing (BPO)#Lembaga Pemerintah, Non Profit, NGO & Facilitas Publik#Asuransi#Konsultan, Legal & Riset#Percetakan & Penerbitan";

    public static String highQualityWorkCityForFemalePRD100 = "kota jakarta timur#kabupaten bekasi#kota bogor#kota bekasi#kabupaten sumedang#kabupaten bandung barat#kota banjarmasin#kota tangerang#kabupaten gresik#kabupaten tegal#kabupaten malang#kota jakarta utara#kabupaten sidoarjo#kabupaten subang#kabupaten tangerang#kabupaten sleman#kota medan#kota palembang";
    public static String highQualityWorkCityForMalePRD100 = "kota tangerang#kabupaten bandung#kota bekasi#kota jakarta timur#kota surabaya#kabupaten karawang#kabupaten bekasi#kota cirebon#kabupaten banyuwangi#kota sukabumi#kabupaten bogor#kota batam#kota medan#kota palembang#kota jakarta utara#kota jakarta selatan";

    public static String highQualityIndustryPRD50 = "Jasa Lainnya#Retail & Distribusi#Lembaga Pemerintah, Non Profit, NGO & Facilitas Publik#Pendidikan & Pelatihan#Kecantikan & Kesehatan#IT & Telekomunikasi#Pertanian, Perikanan & Perkebunan#Konsultan, Legal & Riset#Lingkungan, Jasa Perawatan & Perbaikan Fasilitas#Pertambangan & Migas#Media, Hiburan & Periklanan#Real Estate & Properti";
    public static String highQualityLiveCityPRD50 = "Kota Medan#Kota Jakarta Timur#Kabupaten Bogor#Kota Surabaya";
    public static String highQualityPositionNamePRD50 = "Lainnya#Pegawai negeri#Guru#Koki#Vendor individu#Pelayan#Pembersih#Petani";
}
