package com.yqg.risk.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Slf4j
@Table("fraudUserOrderInfo")
public class FraudUserInfo extends BaseEntity {

    private String realName;
    private String orderAddressDetail;//下单位置，取detail字段
    private String motherName; //母亲姓名
    private String emergencyTel;//第一紧急联系人电话
    private String emergencyTel2;//第二紧急联系人电话
//    private String emergencyName;//第一紧急联系人姓名
//    private String emergencyName2;//第二紧急联系人姓名


    private Integer age;
    private String companyName;
    private String companyAddress; //公司地址：省市+大区+小区
    private String companyTel;//公司电话
    private String firstCommonContactTel ;// 第一常用联系人,,通话记录中查找
    private String secondCommonContactTel;// 第一常用联系人,,通话记录中查找
//    private String firstCommonContactName;//第一常用联系人姓名
//    private String secondCommonContactName;//第二常用联系人姓名;
    private String ipAddress;//ip地址
    private String pictureCount;//图片数量
    private String liveAddress; //居住地址：省市+大区+小区


    //关联的订单
    private String orderNo;
    private String userUuid;

    private static List<String> ignoreEmergencyNames = Arrays.asList(" Mom", " My Mom", " Usen 1 2", " P3", " Mamah", "Ppi 44", "Mama", "My Love",
            "Mamah 1",
            "Mother", "Chacha 1", "My Husband", "Bapa", "Mama AL", " Mamat M3", "Ita M3", " P4p4ku C4y41", "Mama M3", "A", "Papa", "Mamak11",
            "mamak", "ari as " +
                    "new", "P6", "Papa Clara", "Pk", "Dita M3", "Bule", "MAMA FAJRI", "Ia", "Mama Ast", "ompong2", "Papah");


    //满足A类数据[将欺诈用户的数据分为AB组，看是否满足A组中任意一个属性]
    public boolean matchAttributeGroupA(FraudUserInfo matchingUserInfo) {
        int hitItemCount = 0;
        if (attributeMatch(realName, matchingUserInfo.getRealName())) {
            hitItemCount++;
        }
        if (attributeMatch(orderAddressDetail, matchingUserInfo.getOrderAddressDetail())) {
            hitItemCount++;
        }
        if (attributeMatch(motherName, matchingUserInfo.getMotherName())) {
            hitItemCount++;
        }
        if (telMatch(emergencyTel, matchingUserInfo.getEmergencyTel())) {
            hitItemCount++;
        }
        if (telMatch(emergencyTel2, matchingUserInfo.getEmergencyTel2())) {
            hitItemCount++;
        }
        return hitItemCount>=2;

//        if (StringUtils.isNotEmpty(matchingUserInfo.getEmergencyName()) && !ignoreEmergencyNames.contains(matchingUserInfo.getEmergencyName())) {
//            //不在排除的名称里面
//            if (attributeMatch(emergencyName, matchingUserInfo.getEmergencyName())) {
//                return true;
//            }
//        }
//        if (StringUtils.isNotEmpty(matchingUserInfo.getEmergencyName2()) && !ignoreEmergencyNames.contains(matchingUserInfo.getEmergencyName2())) {
//            //不在排除的名称里面
//            if (attributeMatch(emergencyName2, matchingUserInfo.getEmergencyName2())) {
//                return true;
//            }
//        }
//
//
//        return false;
    }


    //满足B类数据[将欺诈用户的数据分为AB组，看是否满足B组中任意一个属性]
    public boolean matchAttributeGroupB(FraudUserInfo matchingUserInfo){
        if(attributeMatch(age,matchingUserInfo.getAge())){
            return true;
        }
        if(attributeMatch(companyName,matchingUserInfo.getCompanyName())){
            return true;
        }
        if(attributeMatch(companyAddress,matchingUserInfo.getCompanyAddress())){
            return true;
        }
        if(attributeMatch(companyTel,matchingUserInfo.getCompanyTel())){
            return true;
        }
        if(telMatch(firstCommonContactTel,matchingUserInfo.getFirstCommonContactTel())){
            return true;
        }
        if(telMatch(secondCommonContactTel,matchingUserInfo.getSecondCommonContactTel())){
            return true;
        }
//        if(attributeMatch(firstCommonContactName,matchingUserInfo.getFirstCommonContactName())){
//            return true;
//        }
//        if(attributeMatch(secondCommonContactName,matchingUserInfo.getSecondCommonContactName())){
//            return true;
//        }
        if(attributeMatch(ipAddress,matchingUserInfo.getIpAddress())){
            return true;
        }
        if(attributeMatch(pictureCount,matchingUserInfo.getPictureCount())){
            return true;
        }
        if(attributeMatch(liveAddress,matchingUserInfo.getLiveAddress())){
            return true;
        }

        return false;
    }

    /****
     * 比较compareData 和targetData值是否相同【暂考虑一些常用类型】
     * @param compareData
     * @param targetData
     * @return
     */
    public boolean attributeMatch(Object compareData, Object targetData) {
        if (compareData == null || targetData == null) {
            return false;
        }
        if (compareData instanceof String) {
            String str1 = (String) compareData;
            String str2 = (String) targetData;
            return StringUtils.isNotEmpty(str1) && StringUtils.isNotEmpty(str2) && str1.equalsIgnoreCase(str2);
        }
        if (compareData instanceof Integer) {
            Integer int1 = (Integer) compareData;
            Integer int2 = (Integer) targetData;
            return int1.equals(int2);
        }
        if (compareData instanceof Long) {
            Long long1 = (Long) compareData;
            Long long2 = (Long) targetData;
            return long1.equals(long2);
        }
        if (compareData instanceof BigDecimal) {
            BigDecimal temp1 = (BigDecimal) compareData;
            BigDecimal temp2 = (BigDecimal) targetData;
            return temp1.compareTo(temp2) == 0;
        }
        log.warn("the data type is not correct,{}" + JsonUtils.serialize(this));
        return false;
    }

    /***
     * 手机号比对
     * @param compareTel
     * @param targetTel
     * @return
     */
    public static boolean telMatch(String compareTel, String targetTel) {
        if (StringUtils.isEmpty(compareTel) || StringUtils.isEmpty(targetTel)) {
            return false;
        }
        if (targetTel.equalsIgnoreCase(compareTel)) {
            return true;
        }
        String formatTargetTel = CheakTeleUtils.telephoneNumberValid2(targetTel);
        String formatCompareTel = CheakTeleUtils.telephoneNumberValid2(compareTel);
        if (StringUtils.isNotEmpty(formatTargetTel) && formatTargetTel.equalsIgnoreCase(formatCompareTel)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {

        System.err.println(ignoreEmergencyNames.contains("P6"));
        //+62 877-4137-8545	0858-7101-6603
        String s1 = "+62 877-4137-8545";
        String s2 ="877-4137-8545";
        System.err.println(telMatch(s1,s2));

    }
//        FraudUserInfo thisFrauld = new FraudUserInfo();
//        thisFrauld.setAge(12);
//        thisFrauld.setCompanyAddress("asdfjlaskfjdalsfjdk");
//        thisFrauld.setCompanyTel("12121212");
//        thisFrauld.setFirstCommonContactTel("slafkjdsaleijw");
//        thisFrauld.setEmergencyTel2("wujqoriewqeroj");
//
//        FraudUserInfo target = new FraudUserInfo();
//        target.setAge(15);
//        target.setCompanyAddress("sda");
//        target.setCompanyTel("1");
//        target.setFirstCommonContactTel("ssd");
//        target.setEmergencyTel2("ds");
//
//        Long start1 = System.currentTimeMillis();
//        for(long i =0 ;i<1000000L;i++){
//            thisFrauld.matchAttributeGroupA(target);
//            thisFrauld.matchAttributeGroupB(target);
//        }
//        Long end1=System.currentTimeMillis();
//        System.err.println("first cal: "+(end1-start1)+" ms");
//
//        Long start2 = System.currentTimeMillis();
//        for(long i =0 ;i<1000000L;i++){
//            thisFrauld.groupMathch(thisFrauld,target,groupAAttributes);
//            thisFrauld.groupMathch(thisFrauld,target,groupBAttributes);
//        }
//        Long end2=System.currentTimeMillis();
//        System.err.println("first cal: "+(end2-start2)+" ms");
//    }

}
