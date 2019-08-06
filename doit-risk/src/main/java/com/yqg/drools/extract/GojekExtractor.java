package com.yqg.drools.extract;

import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.drools.beans.JuXinLiData.DataDetail;
import com.yqg.drools.beans.RideData;
import com.yqg.drools.beans.RideData.PaymentMethodTypeEnum;
import com.yqg.drools.beans.RideData.RideAddress;
import com.yqg.drools.beans.RideData.RideHistoryData;
import com.yqg.drools.model.GojekModel;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.OrderThirdDataService;
import com.yqg.drools.service.UserService;
import com.yqg.drools.utils.DateUtil;
import com.yqg.order.entity.OrdOrder;
import com.yqg.user.entity.UsrAddressDetail;
import com.yqg.user.entity.UsrUser;
import com.yqg.user.entity.UsrWorkDetail;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 ****/

@Service
@Slf4j
public class GojekExtractor implements BaseExtractor<GojekModel> {

    public static final List<String> SPECIAL_TAXI_TYPES = Arrays
        .asList("blue-bird", "golden", "silver");


    @Autowired
    private TokopediaExtractor tokopediaExtractor;


    @Autowired
    private OrderThirdDataService orderThirdDataService;

    @Autowired
    private UserService userService;


    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.GOJEK.equals(ruleSet);
    }

    @Override
    public Optional<GojekModel> extractModel(OrdOrder order, KeyConstant keyConstant) throws ParseException {

        DataDetail dataDetail = orderThirdDataService
            .getThirdData(order, CertificationEnum.GOJECK_IDENTITY);
        if (dataDetail == null) {
            return Optional.empty();
        }

        //骑行历史
        RideData rideData = dataDetail.getReportData().getRides();
        if (CollectionUtils.isEmpty(rideData.getRidesHistory())) {
            log.error("rides history is empty empty, orderNo: {}", order.getUuid());
            return Optional.empty();
        }

        List<RideHistoryData> historyDataList = rideData.getRidesHistory().stream()
            .filter(elem -> elem.getSuccess()).collect(
                Collectors.toList());
        if (CollectionUtils.isEmpty(historyDataList)) {
            log.error("the success rides history is empty empty, orderNo: {}", order.getUuid());
            return Optional.empty();
        }

        Date recent180Day = DateUtils.getDiffTime(order.getUpdateTime(), -180);
        Date recent30Day = DateUtils.getDiffTime(order.getUpdateTime(), -30);

        //近30/180天的数据
        List<RideHistoryData> recent180RideHistory = historyDataList.stream()
            .filter(elem -> DateUtil
                .filterLimitDate(recent180Day, elem.getRideDate(), order.getUpdateTime())).collect(
                Collectors.toList());

        List<RideHistoryData> recent30RideHistory = historyDataList.stream()
            .filter(elem -> DateUtil
                .filterLimitDate(recent30Day, elem.getRideDate(), order.getUpdateTime())).collect(
                Collectors.toList());

        GojekModel gojekModel = new GojekModel();

        //近180天累计乘车距离
        gojekModel.setTotalDistanceFor180(
            recent180RideHistory.stream().map(RideHistoryData::getRideDistance)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        //近180天累计乘车费用
        gojekModel.setTotalFareFor180(
            recent180RideHistory.stream().map(RideHistoryData::getFare)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        //近180天累计乘车次数
        gojekModel.setTotalCountFor180(recent180RideHistory.stream().count());
        //近180天累计上车地点个数
        gojekModel.setTotalPickUpAddressCountFor180(
            recent180RideHistory.stream().map(RideHistoryData::getPickUp).distinct().count());
        //近180天累计乘车类型
        gojekModel.setTotalTaxiTypeCountFor180(
            recent180RideHistory.stream().filter(elem -> !StringUtils.isEmpty(elem.getTaxiType()))
                .map(RideHistoryData::getTaxiType).distinct().count());
        //近180天累计乘坐golden/silver/blue bird的类型数量
        gojekModel.setTotalSpecialTaxiTypeCountFor180(
            recent180RideHistory.stream().filter(
                elem -> !StringUtils.isEmpty(elem.getTaxiType()) && SPECIAL_TAXI_TYPES
                    .contains(elem.getTaxiType()))
                .map(elem -> elem.getTaxiType()).distinct().count());

        //近30天累计乘车距离
        gojekModel.setTotalDistanceFor30(
            recent30RideHistory.stream().map(RideHistoryData::getRideDistance)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        //近30天累计乘车费用
        gojekModel.setTotalFareFor30(
            recent30RideHistory.stream().map(RideHistoryData::getFare)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        //近30天累计乘车次数
        gojekModel.setTotalCountFor30(recent30RideHistory.stream().count());
        //近30天累计上车地点个数
        gojekModel.setTotalPickUpAddressCountFor30(
            recent30RideHistory.stream().map(RideHistoryData::getPickUp).distinct().count());
        //近30天累计乘车类型
        gojekModel.setTotalTaxiTypeCountFor30(
            recent30RideHistory.stream().filter(elem -> !StringUtils.isEmpty(elem.getTaxiType()))
                .map(RideHistoryData::getTaxiType).distinct().count());
        //近30天累计乘坐golden/silver/blue bird的类型数量
        gojekModel.setTotalSpecialTaxiTypeCountFor30(recent30RideHistory.stream().filter(
            elem -> !StringUtils.isEmpty(elem.getTaxiType()) && SPECIAL_TAXI_TYPES
                .contains(elem.getTaxiType()))
            .map(elem -> elem.getTaxiType()).distinct().count());

        //首笔订单乘车距提交申请的天数
        Optional<RideHistoryData> firstRide = historyDataList.stream()
            .filter(elem -> elem.getRideDate() != null)
            .min(Comparator.comparing(elem -> elem.getRideDate()));

        if (firstRide.isPresent()) {
            gojekModel.setDiffDaysForFirstRideAndApplyTime(DateUtil
                .getDiffDaysIgnoreHours(firstRide.get().getRideDate(), order.getApplyTime()));
        }

        //尾笔订单乘车距提交申请的天数
        Optional<RideHistoryData> lastRide = historyDataList.stream()
            .filter(elem -> elem.getRideDate() != null)
            .max(Comparator.comparing(elem -> elem.getRideDate()));

        if (lastRide.isPresent()) {
            gojekModel.setDiffDaysForLastRideAndApplyTime(DateUtil
                .getDiffDaysIgnoreHours(lastRide.get().getRideDate(), order.getApplyTime()));
        }

        //月平均乘车费用/平均乘车次数
        BigDecimal totalFare = historyDataList.stream()
            .map(RideHistoryData::getFare)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (firstRide.isPresent() && lastRide.isPresent()) {

            //首次和最后一次相差的月数
            Long diffDays = DateUtil
                .getDiffDays(firstRide.get().getRideDate(), lastRide.get().getRideDate());

            BigDecimal diffMonth = BigDecimal.valueOf(diffDays)
                .divide(new BigDecimal(DateUtil.DAYS_PER_MONTH), 4, BigDecimal.ROUND_HALF_UP)
                .setScale(0, BigDecimal.ROUND_UP);

            if (diffMonth.compareTo(BigDecimal.ONE) < 0) {
                //非0个月(对首次和最后一次乘车在一个月内月时间差为1)
                diffMonth = BigDecimal.ONE;
            }
            gojekModel.setAverageFarePerMonth(
                totalFare.divide(diffMonth, 2, BigDecimal.ROUND_HALF_UP));
            gojekModel.setAverageRideCountPerMonth(
                BigDecimal.valueOf(historyDataList.size())
                    .divide(diffMonth, 2, BigDecimal.ROUND_HALF_UP));
        }

        //平均乘车费用
        gojekModel.setAverageFare(
            totalFare.divide(BigDecimal.valueOf(historyDataList.size()), 2,
                BigDecimal.ROUND_HALF_UP));

        //平均乘车距离
        BigDecimal totalDistance = historyDataList.stream()
            .map(RideHistoryData::getRideDistance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        gojekModel.setAverageDistance(
            totalDistance.divide(BigDecimal.valueOf(historyDataList.size()), 4,
                BigDecimal.ROUND_HALF_UP));

        //单次乘车的最大费用
        gojekModel.setMaxFare(historyDataList.stream()
            .map(RideHistoryData::getFare).max(BigDecimal::compareTo)
            .get());
        //单次乘车的最长距离
        gojekModel.setMaxDistance(historyDataList.stream()
            .map(RideHistoryData::getRideDistance).max(BigDecimal::compareTo)
            .get());

        //支付类型数
        gojekModel.setPaymentMethodCount(historyDataList.stream()
            .filter(elem -> !StringUtils.isEmpty(elem.getPaymentMethod()))
            .map(elem -> elem.getPaymentMethod()).distinct().count());
        //现金支付次数
        gojekModel.setCashPayCount(historyDataList.stream()
            .filter(elem -> PaymentMethodTypeEnum.Cash.name().equals(elem.getPaymentMethod())).count());

        // 拿到用户地址信息
        UsrUser userInfo = userService.getUserInfo(order.getUserUuid());
        UsrAddressDetail userHomeAddress = userService
            .getUserAddressDetailByType(order.getUserUuid(), UsrAddressEnum.HOME);// 居住地址
        // 学校地址
        UsrAddressDetail userSchoolAddress =
            (userInfo != null && userInfo.getUserRole() == 1) ? userService
                .getUserAddressDetailByType(order.getUserUuid(), UsrAddressEnum.SCHOOL) : null;
        // 公司地址
        UsrAddressDetail userCompanyAddress =
            (userInfo != null && userInfo.getUserRole() == 2) ? userService
                .getUserAddressDetailByType(order.getUserUuid(), UsrAddressEnum.COMPANY)
                : null;

        /***老的地址匹配****/
        //乘车地(出发)/家庭地址匹配度
//        gojekModel.setCoherenceForPickUpAndHomeAddress(
//            calculateAvgPickUpCoherence(userHomeAddress, historyDataList));
//
//        //乘车地(下车)/家庭地址匹配度
//        gojekModel.setCoherenceForDropOffAndHomeAddress(
//            calculateAvgDropOffCoherence(userHomeAddress, historyDataList));
//
//        //乘车地（出发地）/学校地址（学生人群）
//        gojekModel.setCoherenceForPickUpAndSchoolAddress(
//            calculateAvgPickUpCoherence(userSchoolAddress, historyDataList));
//
//        //乘车地（下车地）/学校地址（学生人群）
//        gojekModel.setCoherenceForDropOffAndSchoolAddress(
//            calculateAvgDropOffCoherence(userSchoolAddress, historyDataList));
//
//        //乘车地（出发地）/公司地址（工作人群）
//        gojekModel.setCoherenceForPickUpAndCompanyAddress(
//            calculateAvgPickUpCoherence(userCompanyAddress, historyDataList));
//
//        //乘车地（目的地）/公司地址（工作人群）
//        gojekModel.setCoherenceForDropOffAndCompanyAddress(
//            calculateAvgDropOffCoherence(userCompanyAddress, historyDataList));
//
//        //最近一次包含居住地的打车订单距提交订单的天数
//        gojekModel.setDiffDaysForLatestRideContainHomeAndApplyTime(
//            calculateMinDiffDaysContainAddress(userHomeAddress, order,
//                historyDataList));
//
//        //最近一次包含学校地的打车订单距提交订单的天数
//        gojekModel.setDiffDaysForLatestRideContainSchoolAndApplyTime(
//            calculateMinDiffDaysContainAddress(userSchoolAddress, order,
//                historyDataList));
//
//        //最近一次包含公司地的打车订单距提交订单的天数
//        gojekModel.setDiffDaysForLatestRideContainCompanyAndApplyTime(
//            calculateMinDiffDaysContainAddress(userCompanyAddress, order,
//                historyDataList));
//
//        //首次包含居住地的打车订单距提交订单的天数
//        gojekModel.setDiffDaysForFirstRideContainHomeAndApplyTime(
//            calculateMaxDiffDaysContainAddress(userHomeAddress, order,
//                historyDataList));
//
//        //首次包含学校地的打车订单距提交订单的天数
//
//        gojekModel.setDiffDaysForFirstRideContainSchoolAndApplyTime(
//            calculateMaxDiffDaysContainAddress(userSchoolAddress, order,
//                historyDataList));
//
//        //首次包含公司地的打车订单距提交订单的天数
//        gojekModel.setDiffDaysForFirstRideContainCompanyAndApplyTime(
//            calculateMaxDiffDaysContainAddress(userCompanyAddress, order,
//                historyDataList));
        /***老的地址匹配****/

        // 乘车地（出发地/目的地）匹配现居地中大区、小区、详细地址未同时出现 homeAddrBoolean
        gojekModel.setHomeAddrBoolean(boxAddrBoolean(userHomeAddress, historyDataList));
        // 乘车地（出发地/目的地）匹配公司地址（工作人群）中大区、小区、详细地址未同时出现 companyAddrBoolean
        gojekModel.setCompanyAddrBoolean(boxAddrBoolean(userCompanyAddress, historyDataList));
        // 乘车地（出发地/目的地）匹配学校地址（学生人群）中大区、小区、详细地址未同时出现  schoolAddrBoolean
        gojekModel.setSchoolAddrBoolean(boxAddrBoolean(userSchoolAddress, historyDataList));
        // 最近一次包含居住地的打车订单距提交订单的天数 diffDaysForLatestRideContainHomeAndApplyTime
        gojekModel.setDiffDaysForLatestRideContainHomeAndApplyTime(boxRecentAddrDiffOrderDay(userHomeAddress, order,historyDataList));
        // 最近一次包含工作地的打车订单距提交订单的天数 diffDaysForLatestRideContainCompanyAndApplyTime
        gojekModel.setDiffDaysForLatestRideContainCompanyAndApplyTime(boxRecentAddrDiffOrderDay(userCompanyAddress, order,historyDataList));
        // 最近一次包含学校地的打车订单距提交订单的天数 diffDaysForLatestRideContainSchoolAndApplyTime
        gojekModel.setDiffDaysForLatestRideContainSchoolAndApplyTime(boxRecentAddrDiffOrderDay(userSchoolAddress, order,historyDataList));
        // 首次包含居住地的打车订单距提交订单的天数 diffDaysForFirstRideContainHomeAndApplyTime
        gojekModel.setDiffDaysForFirstRideContainHomeAndApplyTime(boxFirstAddrDiffOrderDay(userHomeAddress, order,historyDataList));
        // 首次包含工作地的打车订单距提交订单的天数 diffDaysForFirstRideContainCompanyAndApplyTime
        gojekModel.setDiffDaysForFirstRideContainCompanyAndApplyTime(boxFirstAddrDiffOrderDay(userCompanyAddress, order,historyDataList));
        // 首次包含学校地的打车订单距提交订单的天数 diffDaysForFirstRideContainSchoolAndApplyTime
        gojekModel.setDiffDaysForFirstRideContainSchoolAndApplyTime(boxFirstAddrDiffOrderDay(userSchoolAddress, order,historyDataList));


        Map<String, String> userInfoMap = userService.getOrderUserInfo(order.getUserUuid());
        if (!CollectionUtils.isEmpty(rideData.getPersonBasicInfo()) && userInfoMap != null) {
            gojekModel.setPersonBaseInfo(rideData.getPersonBasicInfo().get(0));
            //gojek手机号与贷款申请注册号码是否匹配
            String userMobile = userInfoMap.get("mobileNumber");
            String gojekPhone = gojekModel.getPersonBaseInfo().getPhone();
            if (!StringUtils.isEmpty(userMobile) && !StringUtils.isEmpty(gojekPhone)) {
                String mobileNumber = CheakTeleUtils
                    .telephoneNumberValid2(DESUtils.decrypt(userMobile));
                String phone = CheakTeleUtils
                    .telephoneNumberValid2(gojekPhone);
                gojekModel.setMobilePhoneNotSame(!mobileNumber.equals(phone));
            }

            String userEmail = userInfoMap.get("email");
            String gojekEmail = gojekModel.getPersonBaseInfo().getEmail();
            if (!StringUtils.isEmpty(userEmail) && !StringUtils.isEmpty(gojekEmail)) {
                //gojek邮箱与贷款申请邮箱是否匹配
                gojekModel.setEmailNotSame(!userEmail.equals(gojekEmail));
            }
        }
        // 出发地或目的地匹配到公司名称的个数
        UsrWorkDetail workDetail = userService.getUserWorkDetail(order.getUserUuid());

        String userCompanyAddressDetailed = workDetail==null?"":workDetail.getCompanyName();

        Long matchCompanyNum = historyDataList.stream().filter(
            elem -> (!StringUtils.isEmpty(elem.getPickUp()) && elem.getPickUp().toUpperCase()
                .contains(userCompanyAddressDetailed)) ||
                (!StringUtils.isEmpty(elem.getDropOff()) && elem.getDropOff().toUpperCase()
                    .contains(userCompanyAddressDetailed))).count();

        gojekModel.setMatchCompanyNum(matchCompanyNum);


        return Optional.of(gojekModel);

    }

    private Long boxFirstAddrDiffOrderDay(UsrAddressDetail userHomeAddress, OrdOrder order, List<RideHistoryData> historyDataList) {
        Date today = new Date();
        Date minDay = today;
        for (RideHistoryData item : historyDataList) {
            Integer bigCount = 0;
            Integer smallCount = 0;
            Integer detailCount = 0;
            if (userHomeAddress != null && item.getPickUp() != null && item.getDropOff() != null && userHomeAddress.getBigDirect() != null
                    && userHomeAddress.getSmallDirect() != null && userHomeAddress.getDetailed() != null) {
                if (item.getPickUp().toUpperCase().contains(userHomeAddress.getBigDirect().toUpperCase()) ||
                        item.getDropOff().toUpperCase().contains(userHomeAddress.getBigDirect().toUpperCase())) {
                    bigCount++;
                }
                if(item.getPickUp().toUpperCase().contains(userHomeAddress.getSmallDirect().toUpperCase()) ||
                        item.getDropOff().toUpperCase().contains(userHomeAddress.getSmallDirect().toUpperCase())){
                    smallCount++;
                }
                Integer pickUpDetailCount = matchDetailedCountFun(0,userHomeAddress.getDetailed().toUpperCase(),item.getPickUp().toUpperCase());
                Integer dropOffDetailCount = matchDetailedCountFun(0,userHomeAddress.getDetailed().toUpperCase(),item.getDropOff().toUpperCase());
                if (pickUpDetailCount > 0 || dropOffDetailCount > 0) {
                    detailCount++;
                }
                if (bigCount > 0 && smallCount > 0 && detailCount > 0) {
                    // item < minDay 找到最小的值
                    if(item.getRideDate().before(minDay)){
                        minDay = item.getRideDate();
                    }
                }
            }
        }
        Long diff = 0l;
        if (minDay == today) {
            return diff;
        }else {
            return (order.getApplyTime().getTime() - minDay.getTime())/(24*60*60*1000);
        }
    }

    private Long boxRecentAddrDiffOrderDay(UsrAddressDetail userHomeAddress, OrdOrder order, List<RideHistoryData> historyDataList) throws ParseException {
        Date maxDay = DateUtils.stringToDate("1901-11-11 00:00:00");
        Date minDay = new Date();
        for (RideHistoryData item : historyDataList) {
            Integer bigCount = 0;
            Integer smallCount = 0;
            Integer detailCount = 0;
            if (userHomeAddress != null && item.getPickUp() != null && item.getDropOff() != null && userHomeAddress.getBigDirect() != null
                    && userHomeAddress.getSmallDirect() != null && userHomeAddress.getDetailed() != null) {
                if (item.getPickUp().toUpperCase().contains(userHomeAddress.getBigDirect().toUpperCase()) ||
                        item.getDropOff().toUpperCase().contains(userHomeAddress.getBigDirect().toUpperCase())) {
                    bigCount++;
                }
                if(item.getPickUp().toUpperCase().contains(userHomeAddress.getSmallDirect().toUpperCase()) ||
                        item.getDropOff().toUpperCase().contains(userHomeAddress.getSmallDirect().toUpperCase())){
                    smallCount++;
                }
                Integer pickUpDetailCount = matchDetailedCountFun(0,userHomeAddress.getDetailed().toUpperCase(),item.getPickUp().toUpperCase());
                Integer dropOffDetailCount = matchDetailedCountFun(0,userHomeAddress.getDetailed().toUpperCase(),item.getDropOff().toUpperCase());
                if (pickUpDetailCount > 0 || dropOffDetailCount > 0) {
                    detailCount++;
                }
                if (bigCount > 0 && smallCount > 0 && detailCount > 0) {
                    // item > maxDay 找到最大的值
                    if(item.getRideDate().after(maxDay)){
                        maxDay = item.getRideDate();
                    }
                }
            }
        }

        if (DateUtils.formDate(maxDay,"yyyy-MM-dd HH:mm:ss").equals("1901-11-11 00:00:00")) {
            return null;
        }else {
            return (order.getApplyTime().getTime() - maxDay.getTime())/(24*60*60*1000);
        }




    }


    /**
     * 乘车地（出发地/目的地）匹配现居地中大区、小区、详细地址未同时出现
     *
     * @param userHomeAddress
     * @param historyDataList
     */
    public Boolean boxAddrBoolean(UsrAddressDetail userHomeAddress, List<RideHistoryData> historyDataList) {
        Boolean flag = true;
        for (RideHistoryData item : historyDataList) {
            Integer bigCount = 0;
            Integer smallCount = 0;
            Integer detailCount = 0;
            if (userHomeAddress != null && item.getPickUp() != null && item.getDropOff() != null && userHomeAddress.getBigDirect() != null
                    && userHomeAddress.getSmallDirect() != null && userHomeAddress.getDetailed() != null) {
                if (item.getPickUp().toUpperCase().contains(userHomeAddress.getBigDirect().toUpperCase()) ||
                        item.getDropOff().toUpperCase().contains(userHomeAddress.getBigDirect().toUpperCase())) {
                    bigCount++;
                }
                if(item.getPickUp().toUpperCase().contains(userHomeAddress.getSmallDirect().toUpperCase()) ||
                        item.getDropOff().toUpperCase().contains(userHomeAddress.getSmallDirect().toUpperCase())){
                    smallCount++;
                }
                Integer pickUpDetailCount = matchDetailedCountFun(0,userHomeAddress.getDetailed().toUpperCase(),item.getPickUp().toUpperCase());
                Integer dropOffDetailCount = matchDetailedCountFun(0,userHomeAddress.getDetailed().toUpperCase(),item.getDropOff().toUpperCase());
                if (pickUpDetailCount > 0 || dropOffDetailCount > 0) {
                    detailCount++;
                }
                if (bigCount > 0 && smallCount > 0 && detailCount > 0) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }




    /**
     * 详细地址单词匹配到的次数
     *
     * @param count
     * @param userDetailed
     * @param gojekAddr
     * @return
     */
    public static Integer matchDetailedCountFun(Integer count,String userDetailed, String gojekAddr) {
        if(com.yqg.common.utils.StringUtils.isNotEmpty(userDetailed)){
            userDetailed = userDetailed.toUpperCase().replaceAll("JL","")
                    .replaceAll("JALAN","");
        }
        // 将用户地址按照逗号和空格分割
        String[] userDetailedArr = userDetailed.split("\\s|,");
        String gojekAddrStr = "";// gojek的详细地址片段
        if(com.yqg.common.utils.StringUtils.isNotEmpty(gojekAddr)){
            if (gojekAddr.contains(",")) {
                String[] gojekAddrArr = gojekAddr.split(",");
                gojekAddrStr = gojekAddrArr[0].trim();
                if (gojekAddrArr.length > 1) {
                    gojekAddrStr = gojekAddrStr + gojekAddrArr[1].trim();
                }
            } else {
                gojekAddrStr = gojekAddr;
            }
        }
        for (String item : userDetailedArr) {
            if (com.yqg.common.utils.StringUtils.isNotEmpty(item) && gojekAddrStr.toUpperCase().contains(item)) {
                count++;
            }
        }
        return count;
    }





    /***
     * 自定义的计算骑行地址和用户地址相似度分数
     * @param userAddress
     * @param rideAddress
     * @return
     */
    private BigDecimal calculateAddressCoherence(UsrAddressDetail userAddress,
        RideAddress rideAddress) {
        if (userAddress == null || rideAddress == null) {
            return BigDecimal.ZERO;
        }
        if (StringUtils.isEmpty(userAddress.getProvince()) ||
            StringUtils.isEmpty(userAddress.getCity()) ||
            StringUtils.isEmpty(userAddress.getBigDirect()) ||
            StringUtils.isEmpty(userAddress.getSmallDirect())) {
            return BigDecimal.ZERO;
        }

        if (userAddress.getProvince().equalsIgnoreCase(rideAddress.getProvince()) && userAddress
            .getCity().equalsIgnoreCase(rideAddress.getCity()) && userAddress.getBigDirect()
            .equalsIgnoreCase(rideAddress.getBigDirect()) &&
            userAddress.getSmallDirect().equalsIgnoreCase(rideAddress.getSmallDirect())) {

            String userAddressDetail = userAddress.getDetailed();
            String rideAddressDetail = rideAddress.getDetail();
            String blank = "\\s";
            List<String> address1Arr = Arrays.asList(userAddressDetail.split(blank));
            List<String> address2Arr = Arrays.asList(rideAddressDetail.split(blank));
            //列表交集
            Long count = address1Arr.stream().filter(elem -> address2Arr.contains(elem)).count();

            BigDecimal score1 = BigDecimal.valueOf(count)
                .divide(BigDecimal.valueOf(address1Arr.size()), 6, BigDecimal.ROUND_HALF_UP);
            BigDecimal score2 = BigDecimal.valueOf(count)
                .divide(BigDecimal.valueOf(address2Arr.size()), 6, BigDecimal.ROUND_HALF_UP);

            BigDecimal score = score1.add(score2)
                .divide(new BigDecimal("2"), 6, BigDecimal.ROUND_HALF_UP);
            return score;
        } else {
            return BigDecimal.ZERO;
        }
    }

    /***
     * 计算rideList 中上车或下车地址和address相似的骑行记录中尾次骑行和订单申请时间的间隔天数
     * @param address
     * @param order
     * @param rideList
     * @return
     */
    private Long calculateMinDiffDaysContainAddress(UsrAddressDetail address, OrdOrder order,
        List<RideHistoryData> rideList) {
        if (address == null || CollectionUtils.isEmpty(rideList)) {
            return null;
        }
        Optional<RideHistoryData> latestRideContainAddress = rideList.stream()
            .filter(
                elem -> calculateAddressCoherence(address, elem.getPickUpAddress())
                    .compareTo(BigDecimal.ZERO) > 0 ||
                    calculateAddressCoherence(address, elem.getDropOffAddress())
                        .compareTo(BigDecimal.ZERO) > 0)
            .max(Comparator.comparing(RideHistoryData::getRideDate));
        if (latestRideContainAddress.isPresent()) {
            return DateUtil
                .getDiffDays(latestRideContainAddress.get().getRideDate(), order.getApplyTime());
        }
        return null;
    }

    /***
     * 计算rideList 中上车或下车地址和address相似的骑行记录中首次骑行和订单申请时间的间隔天数
     * @param address
     * @param order
     * @param rideList
     * @return
     */
    private Long calculateMaxDiffDaysContainAddress(UsrAddressDetail address, OrdOrder order,
        List<RideHistoryData> rideList) {
        if (address == null || CollectionUtils.isEmpty(rideList)) {
            return null;
        }
        Optional<RideHistoryData> firstRideContainAddress = rideList.stream()
            .filter(
                elem -> calculateAddressCoherence(address, elem.getPickUpAddress())
                    .compareTo(BigDecimal.ZERO) > 0 ||
                    calculateAddressCoherence(address, elem.getDropOffAddress())
                        .compareTo(BigDecimal.ZERO) > 0)
            .min(Comparator.comparing(RideHistoryData::getRideDate));
        if (firstRideContainAddress.isPresent()) {
            return DateUtil
                .getDiffDays(firstRideContainAddress.get().getRideDate(), order.getApplyTime());
        }
        return null;
    }


    /***
     * 计算rideList中上车地址和address的平均相似度
     * @param address
     * @param rideList
     * @return
     */
    private BigDecimal calculateAvgPickUpCoherence(UsrAddressDetail address,
        List<RideHistoryData> rideList) {
        List<BigDecimal> homePickUpAddressScoreList = rideList.stream()
            .map(elem -> calculateAddressCoherence(address, elem.getPickUpAddress()))
            .collect(
                Collectors.toList());
        return homePickUpAddressScoreList.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(homePickUpAddressScoreList.size()), 6,
                BigDecimal.ROUND_HALF_UP);
    }


    /***
     * 计算rideList中下车地址和address的平均相似度
     * @param address
     * @param rideList
     * @return
     */
    private BigDecimal calculateAvgDropOffCoherence(UsrAddressDetail address,
        List<RideHistoryData> rideList) {
        List<BigDecimal> homePickUpAddressScoreList = rideList.stream()
            .map(elem -> calculateAddressCoherence(address, elem.getDropOffAddress()))
            .collect(
                Collectors.toList());
        return homePickUpAddressScoreList.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(homePickUpAddressScoreList.size()), 6,
                BigDecimal.ROUND_HALF_UP);
    }
}