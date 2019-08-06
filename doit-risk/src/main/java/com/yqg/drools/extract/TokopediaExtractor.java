package com.yqg.drools.extract;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.beans.ECommerceData;
import com.yqg.drools.beans.ECommerceData.OrderInfo;
import com.yqg.drools.beans.ECommerceData.PersonBasicInfo;
import com.yqg.drools.beans.JuXinLiData.DataDetail;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.TokopediaModel;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.OrderThirdDataService;
import com.yqg.drools.service.UserService;
import com.yqg.drools.utils.DateUtil;
import com.yqg.order.entity.OrdOrder;
import com.yqg.user.dao.UsrAddressDetailDao;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.entity.UsrAddressDetail;
import com.yqg.user.entity.UsrUser;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 *
 * tokopedia数据提取
 *
 ****/

@Slf4j
@Service
public class TokopediaExtractor implements BaseExtractor<TokopediaModel> {


    private static final String num1 = "0-60000";
    private static final String num2 = "60000-100000";
    private static final String num3 = "100000-200000";
    private static final String num4 = "200000-600000";
    private static final String num5 = "600000-1000000";
    private static final String num6 = "1000000-2000000";
    private static final String num7 = ">2000000";

    private final static List<String> grandClassifyList = Arrays
        .asList(num1, num2, num3, num4, num5, num6, num7);

    @Autowired
    private OrderThirdDataService orderThirdDataService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private UsrAddressDetailDao usrAddressDetailDao;

    @Autowired
    private UsrDao usrDao;


    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.TOKOPEIDA.equals(ruleSet);
    }

    @Override
    public Optional<TokopediaModel> extractModel(OrdOrder order, KeyConstant keyConstant)
        throws ParseException {

        DataDetail dataDetail = orderThirdDataService
            .getThirdData(order, CertificationEnum.TOKOPEDIA_IDENTITY);
        if (dataDetail == null) {
            return Optional.empty();
        }

        ECommerceData eCommerceData = dataDetail.getReportData().getECommerceData();

        if (eCommerceData == null || CollectionUtils.isEmpty(eCommerceData.getOrder())) {
            log.warn("the tokopedia ecommerceData is empty, orderId: ", order.getUuid());
            return Optional.empty();
        }

        //交易时间有效的所有订单
        List<OrderInfo> validOrderList = eCommerceData.getOrder().stream()
            .filter(
                elem -> elem.getOrderCreateTime() != null && elem.getGrandTotal() != null && elem
                    .getSuccess())
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(validOrderList)) {
            log.warn("the tokopedia order is empty, orderId: ", order.getUuid());
            return Optional.empty();
        }

        Date recent180Day = DateUtils.getDiffTime(order.getUpdateTime(), -180);// 推近180天
        Date recent30Day = DateUtils.getDiffTime(order.getUpdateTime(), -30);// 推近30天

        TokopediaModel model = new TokopediaModel();

        //1、近180天/近30天 累计订单金额  累计订单次数
        List<OrderInfo> recent180OrderList = validOrderList.stream()
            .filter(elem -> DateUtil
                .filterLimitDate(recent180Day, elem.getOrderCreateTime(), order.getUpdateTime()))
            .collect(Collectors.toList());
        List<OrderInfo> recent30OrderList = validOrderList.stream()
            .filter(elem -> DateUtil
                .filterLimitDate(recent30Day, elem.getOrderCreateTime(), order.getUpdateTime()))
            .collect(Collectors.toList());

        //近180天累计金额，累计订单次数
        if (CollectionUtils.isEmpty(recent180OrderList)) {
            model.setRecent180TotalAmount(BigDecimal.ZERO);
            model.setRecent180TotalCount(0L);
        } else {
            model.setRecent180TotalAmount(
                recent180OrderList.stream().map(elem -> elem.getGrandTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            model.setRecent180TotalCount(recent180OrderList.stream().count());
        }

        if (CollectionUtils.isEmpty(recent30OrderList)) {
            model.setRecent30TotalAmount(BigDecimal.ZERO);
            model.setRecent30TotalCount(0L);
        } else {
            model
                .setRecent30TotalAmount(recent30OrderList.stream().map(elem -> elem.getGrandTotal())
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
            model.setRecent30TotalCount(recent30OrderList.stream().count());
        }

        //2、月平均订单次数 月平均订单金额【最早toko订单到贷款申请时间的之间】
        Optional<OrderInfo> earliestTokopeidaOrder = validOrderList.stream()
            .min(Comparator.comparing(OrderInfo::getOrderCreateTime));

        if (earliestTokopeidaOrder.isPresent()) {
            Long diffDays = DateUtil
                .getDiffDays(earliestTokopeidaOrder.get().getOrderCreateTime(),
                    order.getApplyTime());

            //间隔月数【向上取整得到月数】
            BigDecimal monthPeriod = BigDecimal.valueOf(diffDays)
                .divide(new BigDecimal(DateUtil.DAYS_PER_MONTH), 4, BigDecimal.ROUND_HALF_UP)
                .setScale(0, BigDecimal.ROUND_UP);

            if (monthPeriod.compareTo(BigDecimal.ONE) < 0) {
                //不足一个月记录为1个月
                monthPeriod = BigDecimal.ONE;
            }
            BigDecimal averageCountPerMonth = new BigDecimal(validOrderList.stream().count())
                .divide(monthPeriod, 4, BigDecimal.ROUND_HALF_UP);

            BigDecimal averageAmountPerMonth = validOrderList.stream()
                .map(elem -> elem.getGrandTotal()).reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(monthPeriod, 4, BigDecimal.ROUND_HALF_UP);

            model.setAverageAmountPerMonth(averageAmountPerMonth);
            model.setAverageCountPerMonth(averageCountPerMonth);
        }

        Optional<OrderInfo> latestTokopediaOrder = validOrderList.stream()
            .max(Comparator.comparing(OrderInfo::getOrderCreateTime));

        // 3、单笔订单的最低价格 单笔订单的最高价格
        model.setMinAmountPerOrder(
            validOrderList.stream().map(elem -> elem.getGrandTotal())
                .min(Comparator.comparing(Function.identity())).get());

        model.setMaxAmountPerOrder(validOrderList.stream().map(elem -> elem.getGrandTotal())
            .max(Comparator.comparing(Function.identity())).get());

        //4、订单平均交易金额
        BigDecimal averageAmountPerOrder = validOrderList.stream().map(elem -> elem.getGrandTotal())
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(validOrderList.size()), 4, BigDecimal.ROUND_HALF_UP);
        model.setAverageAmountPerOrder(averageAmountPerOrder);

        // 5、求中位数
        model.setMedianAmount(calculateMedianAmount(validOrderList));
        // 6、单笔订单价格的众数【价格分组，然后选择落入某一个分组最大的价格范围】
        Map<String, Long> classifyMap = validOrderList.stream()
            .collect(Collectors.groupingBy(elem -> classifyTokoOrder(elem.getGrandTotal()),
                Collectors.counting()));

        //排序取最大值
        Map.Entry<String, Long> maxEntry = classifyMap.entrySet().stream()
            .max(Comparator.comparing(elem -> elem.getValue())).get();
        model.setModeAmount(maxEntry.getKey());

        // 7、尾笔、首笔订单距离申请时间差
        if (earliestTokopeidaOrder.isPresent()) {
            model.setDiffDaysBetweenFirstTransDayAndApplyDate(
                DateUtil.getDiffDaysIgnoreHours(earliestTokopeidaOrder.get().getOrderCreateTime(),
                    order.getApplyTime()));
        }
        if (latestTokopediaOrder.isPresent()) {
            model.setDiffDaysBetweenLastTransDayAndApplyDate(
                DateUtil.getDiffDaysIgnoreHours(latestTokopediaOrder.get().getOrderCreateTime(),
                    order.getApplyTime()));
        }
        //订单跨越天数
        //订单的平均间隔天数
        if (earliestTokopeidaOrder.isPresent() && latestTokopediaOrder.isPresent()) {
            model.setCrossDays(DateUtil.getDiffDaysIgnoreHours(
                earliestTokopeidaOrder.get().getOrderCreateTime(),
                latestTokopediaOrder.get().getOrderCreateTime()));
            model.setAverageDiffDaysPerOrder(BigDecimal.valueOf(model.getCrossDays())
                .divide(BigDecimal.valueOf(validOrderList.size()), 2, BigDecimal.ROUND_HALF_UP));
        }

        // 8、订单地址个数
        model.setOrderAddressCount(CollectionUtils.isEmpty(eCommerceData.getAddressBook()) ? null
            : eCommerceData.getAddressBook().stream().count());

        // 10、购买店铺的个数[julixin暂无数据]
//        model.setShopCount(
//            validOrderList.stream().map(elem -> elem.getShopUrl()).distinct().count());

        //支出收入占比:（最近一个月的tokopedia消费额+最近一个月的gojek消费金额）/月收入金额
        //取用户的月收入、手机号、email、生日
        Map<String, String> userMap = userService.getOrderUserInfo(order.getUserUuid());

        BigDecimal outAmount = model.getRecent30TotalAmount() == null ? BigDecimal.ZERO
            : model.getRecent30TotalAmount();
        String gojekAmount = redisClient
            .get(RedisContants.RISK_FENGKONG_GOJEK_AMOUNT + order.getUuid());
        redisClient
            .del(RedisContants.RISK_FENGKONG_GOJEK_AMOUNT + order.getUuid());// get到之后，清除redis
        if (StringUtils.isNotEmpty(gojekAmount)) {
            outAmount = outAmount.add(new BigDecimal(gojekAmount));
        }

        String monthlyIncome = userMap.get("monthlyIncome");
        if (StringUtils.isEmpty(monthlyIncome)) {
            //无收入,计入0
            model.setExpenditureIncomeRatio(BigDecimal.valueOf(0));
        } else {
            BigDecimal decimalMonthlyIncome = new BigDecimal(monthlyIncome);
            model.setExpenditureIncomeRatio(decimalMonthlyIncome.equals(BigDecimal.ZERO) ? BigDecimal.ZERO :
                    outAmount.divide(decimalMonthlyIncome, 4, BigDecimal.ROUND_HALF_UP));
        }

        if (CollectionUtils.isEmpty(eCommerceData.getPersonBasicInfo())) {
            log.warn("tokopeida person info is empty,orderId: ", order.getUuid());
            return Optional.of(model);
        }

        PersonBasicInfo personBasicInfo = eCommerceData.getPersonBasicInfo().get(0);

        // model.setIsSameBirthday(userMap.get("birthday").equals(personBasicInfo));
        String userEmail = userMap.get("email");
        if (StringUtils.isNotEmpty(userEmail)) {
            model.setEmailNotSame(!userEmail.equals(personBasicInfo.getEmail()));
        }
        String userMobile = userMap.get("mobileNumber");
        if (StringUtils.isNotEmpty(userMobile)) {

            String mobileNumber = CheakTeleUtils
                .telephoneNumberValid2(DESUtils.decrypt(userMobile));

            String tokopediaPhone = CheakTeleUtils
                .telephoneNumberValid2(personBasicInfo.getPhone());

            model.setMobileNumberNotSame(!mobileNumber.equals(tokopediaPhone));
        }

        //生日不一致（暂无数据）
//        String birthday = userMap.get("birthday");
//        if(StringUtils.isNotEmpty(birthday)){
//
//        }

        /* 2018-01-31 新增需求 */
        // 收货地址不匹配学校地址 SHIPPING_ADDRESS_MATCH_SCHOOL_ADDRESS addressType = 3
        UsrAddressDetail usrAddressDetail = new UsrAddressDetail();
        usrAddressDetail.setUserUuid(order.getUserUuid());
        usrAddressDetail.setDisabled(0);
        usrAddressDetail.setAddressType(UsrAddressEnum.SCHOOL.getType());// 学校地址
        List<UsrAddressDetail> usrAddressDetailList = usrAddressDetailDao.scan(usrAddressDetail);
        if (!CollectionUtils.isEmpty(usrAddressDetailList)) {
            String bigDirect = usrAddressDetailList.get(0).getBigDirect();// 大区
            String smallDirect = usrAddressDetailList.get(0).getSmallDirect();// 小区
            String userDetailed = usrAddressDetailList.get(0).getDetailed();// 详细地址
            List<ECommerceData.AddressBook> addressList = eCommerceData.getAddressBook();
            Integer bigDirectCount = 0;// 大区出现的总次数
            Integer smallDirectCount = 0;// 小区出现的总次数
            Integer detailedCount = 0;// 详细地址的单词匹配到至少1个的次数
            for(ECommerceData.AddressBook item:addressList) {
                bigDirectCount = bigDirectCount + matchBigDirectCountFun(0, bigDirect, item.getAddress());
                smallDirectCount = smallDirectCount + matchBigDirectCountFun(0, smallDirect, item.getAddress());
                if (matchDetailedCountFun(0, userDetailed, item.getAddress()) >= 1) {
                    detailedCount++;
                }
            }
            Boolean addrFlag = false;
            // 同时出现
            if (bigDirectCount > 0 && smallDirectCount > 0 && detailedCount > 0) {
                addrFlag = false;
            }else {
                // 未同时出现
                addrFlag = true;
            }
            model.setBigDirectCount(bigDirectCount);
            model.setSmallDirectCount(smallDirectCount);
            model.setDetailedCount(detailedCount);
            model.setAddrFlag(addrFlag.toString());
        } else {
            // 不是学生
            model.setBigDirectCount(0);
            model.setSmallDirectCount(0);
            model.setDetailedCount(0);
            model.setAddrFlag("");
        }

        UsrUser user = new UsrUser();
        user.setDisabled(0);
        user.setUuid(order.getUserUuid());
        List<UsrUser> users = usrDao.scan(user);
        if (CollectionUtils.isEmpty(users)) {
            return Optional.empty();
        }
        String userPhone = CheakTeleUtils
            .telephoneNumberValid2(DESUtils.decrypt(users.get(0).getMobileNumberDES()));
        String userName = users.get(0).getRealName();
        Set<String> phoneSet = new HashSet<>();
        List<PersonBasicInfo> personBasicInfoList = eCommerceData.getPersonBasicInfo();
        if (CollectionUtils.isEmpty(personBasicInfoList)) {
            return Optional.empty();
        }
        Integer matchPhoneCount = 0;
        Integer toOrderCount = 0;
        Date accountCreateDate = personBasicInfo.getCreateDate();// 账号注册时间
        String tokoName = personBasicInfo.getName();

        String itemPhone = personBasicInfo.getPhone() == null ? null
            : CheakTeleUtils.telephoneNumberValid2(personBasicInfo.getPhone());

        if (StringUtils.isNotEmpty(itemPhone)) {
            phoneSet.add(itemPhone);
        }

        List<ECommerceData.AddressBook> addressBookList = eCommerceData.getAddressBook();// 收货地址
        if (CollectionUtils.isEmpty(addressBookList)) {
            log.warn("the address book is empty");
            return Optional.of(model);
        }
        for (ECommerceData.AddressBook item : addressBookList) {
            itemPhone = CheakTeleUtils.telephoneNumberValid2(item.getPhone());
            if (StringUtils.isNotEmpty(itemPhone)) {
                phoneSet.add(itemPhone);
                item.getShipFreq();// 购物频次
                if (StringUtils.isNotEmpty(userPhone) && userPhone.equals(itemPhone)) {
                    matchPhoneCount++;
                    toOrderCount = toOrderCount + item.getShipFreq();
                }
            }
        }
        // toko的去重手机号码个数(下单地址的手机号+注册手机号)
        model.setDiffPhoneNumCount(phoneSet.size());
        // uu手机号匹配到toko收货地址中手机号的次数
        model.setMatchPhoneCount(matchPhoneCount);
        // uu手机号在toko累计下单次数
        model.setToOrderCount(toOrderCount);
        // toko账号注册距提交订单的天数
        Long registerDiffDays = DateUtil.getDiffDaysIgnoreHours(accountCreateDate,order.getApplyTime());
        model.setRegisterDiffDays(registerDiffDays);
        // toko用户名 tokoName 匹配到uu用户姓名单词的个数 userName
        Integer matchNum = matchNames(tokoName, userName);
        model.setMatchNum(matchNum);
        return Optional.of(model);
    }

    private Integer matchNames(String tokoName, String userName) {
        Integer count = 0;
        if (StringUtils.isEmpty(tokoName) || StringUtils.isEmpty(userName)) {
            return 0;
        }
        String[] tokoNameArr = tokoName.split("\\s");
        String[] userNameArr = userName.split("\\s");
        for (String tokoNameItem : tokoNameArr) {
            for (String userNameItem : userNameArr) {
                if (tokoNameItem.equals(userNameItem)) {
                    count++;
                }
            }
        }
        return count;
    }

    private String classifyTokoOrder(BigDecimal money) {
        Optional<String> classifyResult = grandClassifyList.stream().filter(elem -> {
            if (elem.contains("-")) {
                String limit[] = elem.split("-");
                if (money.compareTo(new BigDecimal(limit[0])) >= 0
                    && money.compareTo(new BigDecimal(limit[1])) < 0) {
                    return true;
                }
            } else if (elem.contains(">")) {
                return money.compareTo(new BigDecimal(elem.replaceAll(">", ""))) >= 0;
            }
            return false;
        }).findFirst();

        if (classifyResult.isPresent()) {
            return classifyResult.get();
        }
        return "";
    }

    private BigDecimal calculateMedianAmount(List<OrderInfo> orderList) {
        List<BigDecimal> sortedList = orderList.stream().map(elem -> elem.getGrandTotal()).sorted()
            .collect(Collectors.toList());

        if (sortedList.size() % 2 == 0) {
            //even[中间两个数的平均值]
            return sortedList.get(sortedList.size() / 2)
                .add(sortedList.get(sortedList.size() / 2 - 1))
                .divide(new BigDecimal("2"));

        } else {
            //odd
            return sortedList.get(sortedList.size() / 2);
        }
    }


    /*** 地址匹配 ***/
    /**
     * 大区、小区、详细地址未同时出现
     *      同时出现记为:false
     *      不同时出现记为:true
     * @param userBig
     * @param userSmall
     * @param userDetailed
     * @param gojekAddr
     * @return
     */
    public static Boolean matchBigSmallDetailedCountFun(String userBig,String userSmall,String userDetailed, String gojekAddr){
        // 其中用户填的详细地址的单词（除了jl和jalan）全都不出现在gojek详细地址以逗号分隔的后两位地址中，表示不在
        Integer countBack = matchDetailedCountFun(0, userDetailed.toUpperCase(), gojekAddr.toUpperCase());
        if (gojekAddr.toUpperCase().contains(userBig.toUpperCase()) && gojekAddr.toUpperCase().contains(userSmall.toUpperCase()) && countBack > 0) {
            return false;
        }else {
            return true;
        }
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
        if(StringUtils.isNotEmpty(userDetailed)){
            userDetailed = userDetailed.toUpperCase().replaceAll("JL","")
                    .replaceAll("JALAN","");
        }
        // 将用户地址按照逗号和空格分割
        String[] userDetailedArr = userDetailed.split("\\s|,");
        String gojekAddrStr = "";// gojek的详细地址片段
        if(StringUtils.isNotEmpty(gojekAddr)){
            if (gojekAddr.contains(",")) {
                String[] gojekAddrArr = gojekAddr.split(",");
                if(gojekAddr.length()>1){
                    gojekAddrStr = gojekAddrArr[0].trim() + gojekAddrArr[1].trim();
                }else{
                    gojekAddrStr = gojekAddr;
                }

            } else {
                gojekAddrStr = gojekAddr;
            }
        }
        for (String item : userDetailedArr) {
            if (StringUtils.isNotEmpty(item) && gojekAddrStr.toUpperCase().contains(item)) {
                count++;
            }
        }
        return count;
    }



    /**
     * 匹配 大区、小区 出现的次数
     * gojek地址contains大区匹配地址
     *
     * @param count
     * @param userDirect
     * @param gojekAddr
     * @return
     */
    private Integer matchBigDirectCountFun(Integer count,String userDirect, String gojekAddr) {
        if (StringUtils.isNotEmpty(userDirect) && StringUtils.isNotEmpty(gojekAddr) &&
                gojekAddr.toUpperCase().contains(userDirect.toUpperCase())) {
            count++;
        }
        return count;
    }


}
