package com.yqg.manage.service.mongo;

import com.vdurmont.emoji.EmojiParser;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.manage.dal.user.UserUserDao;
import com.yqg.manage.service.mongo.request.OrderMongoRequest;
import com.yqg.manage.service.mongo.response.FrequentOrderUserCallRecordResponse;
import com.yqg.manage.service.mongo.response.HouseWifiInfoResponse;
import com.yqg.manage.service.mongo.response.OrderUserCallRecordResponse;
import com.yqg.manage.service.mongo.response.OrderUserContractResponse;
import com.yqg.manage.service.order.ManOrderOrderService;
import com.yqg.mongo.dao.UserCallRecordsDal;
import com.yqg.mongo.dao.UserContactsDal;
import com.yqg.mongo.entity.UserCallRecordsMongo;
import com.yqg.mongo.entity.UserContactsMongo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.system.dao.TeleCallResultDao;
import com.yqg.system.entity.TeleCallResult;
import com.yqg.user.dao.UsrHouseWifeDetailDao;
import com.yqg.user.dao.UsrLinkManDao;
import com.yqg.user.entity.UsrHouseWifeDetail;
import com.yqg.user.entity.UsrLinkManInfo;
import com.yqg.user.entity.UsrUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author alan
 */
@Component
public class OrderUserContactMongoService {

    private static Logger logger = LoggerFactory.getLogger(OrderUserContactMongoService.class);
    /**
     * 过滤emoji表情正则
     */
    private static final String EMOJI_REGEX = "(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)";

    @Autowired
    private UserContactsDal orderUserContactDal;

    @Autowired
    private UserCallRecordsDal userCallRecordsDal;

    @Autowired
    private ManOrderOrderService manOrderOrderService;

    @Autowired
    private UsrLinkManDao usrLinkManDao;

    @Autowired
    private UserUserDao usrUserDao;

    @Autowired
    private UsrHouseWifeDetailDao usrHouseWifeDetailDao;

    @Autowired
    private TeleCallResultDao teleCallResultDao;

    public List<OrderUserContractResponse> getOrderUserContactByOrderNo(OrderMongoRequest request)
            throws Exception {

        //因为前段催收通讯录地方已下架 暂时注释掉。
//        if (StringUtils.isEmpty(request.getOrderNo())) {
//            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
//        }
//        UserContactsMongo search = new UserContactsMongo();
//        search.setOrderNo(request.getOrderNo());
//        List<UserContactsMongo> result = this.orderUserContactDal.find(search);
//        if (result.size() <= 0) {
//            return null;
//        }
//        List<OrderUserContractResponse> response = new ArrayList<>();
//
//        UserContactsMongo dataResult = result.get(0);
//
//        String temp = dataResult.getData();
//        if (!StringUtils.isEmpty(temp)) {
//            List<LinkedHashMap> list = JsonUtils.deserialize(temp, List.class);
//            //姓名和电话去重
//            if (CollectionUtils.isEmpty(list)) {
//                logger.info("联系人通讯录有误");
//                return response;
//            }
//            List<String> nameAndPhoneList = new ArrayList<>();
//            for (LinkedHashMap mapContact : list) {
//                OrderUserContractResponse cell = new OrderUserContractResponse();
//                if (!StringUtils.isEmpty((mapContact.get("phone")))) {
//                    String tempName = "" + mapContact.get("name");
//                    if (!StringUtils.isEmpty(mapContact.get("name"))) {
//                        tempName = tempName.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
//                    }
//                    String name = URLDecoder.decode(tempName , "UTF-8");
//                    String phone = ("" + mapContact.get("phone")).replaceAll("-", "").replaceAll(" ", "");
//                    String nameAndPhone = name + phone;
//                    if (!nameAndPhoneList.contains(nameAndPhone)) {
//                        cell.setName(name);
//                        cell.setMobile(phone);
//                        response.add(cell);
//                        nameAndPhoneList.add(nameAndPhone);
//                    }
//
//                }
//            }
//        }
//        return response;
        return new ArrayList<>();
    }

    public List<OrderUserCallRecordResponse> orderUserCallRecordMongoByUuid(OrderMongoRequest request)
            throws Exception {

        if (StringUtils.isEmpty(request.getOrderNo())) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        //复借订单查询第一笔订单号
        List<OrdOrder> orderNos = manOrderOrderService.orderListByOrderNo(request.getOrderNo());
        if (CollectionUtils.isEmpty(orderNos)) {
            return null;
        }
        String orderNo = orderNos.get(0).getUuid();
        UserCallRecordsMongo search = new UserCallRecordsMongo();
        search.setOrderNo(orderNo);
        search.setDisabled(0);
        List<UserCallRecordsMongo> resultMongo = this.userCallRecordsDal.find(search);
        if (CollectionUtils.isEmpty(resultMongo)) {
            logger.info("没有查出其通话记录！");
            return null;
        }
        //全部数据集合
        List<OrderUserCallRecordResponse> response = new ArrayList<>();
        UserCallRecordsMongo dataResult = resultMongo.get(0);
        //获得电话记录
        String callRecordStr = dataResult.getData();
        if (StringUtils.isEmpty(callRecordStr)) {
            return response;
        }
        List<LinkedHashMap> callRecordList = JsonUtils.deserialize(callRecordStr, List.class);
        //封装数据到list中
        for (LinkedHashMap callRecord : callRecordList) {
            OrderUserCallRecordResponse orderUser = new OrderUserCallRecordResponse();
            if (StringUtils.isEmpty(String.valueOf(callRecord.get("number")))) {
                continue;
            }
            String tempType = String.valueOf(callRecord.get("type"));
            //兼容老数据（使用中文的情况)
            String type = "";
            if ("0".equals(tempType) || "未接".equals(tempType)) {
                type = "0";
            } else if ("1".equals(tempType) || "打入".equals(tempType)) {
                type = "1";
            } else {
                type = "2";
            }
            String duration = (StringUtils.isEmpty(String.valueOf(callRecord.get("duration"))) ||
                    "null".equals(String.valueOf(callRecord.get("duration"))))
                    ? "0" : String.valueOf(callRecord.get("duration"));

            String tempMobile = String.valueOf(callRecord.get("number"));

            if(tempMobile.startsWith("08")){
                tempMobile = tempMobile.replaceFirst("0","62");
            } else if (tempMobile.startsWith("+62")) {
                tempMobile = tempMobile.replaceFirst("\\+","");
            }
            orderUser.setMobile(tempMobile);
            orderUser.setRealName("未备注联系人".equals(String.valueOf(callRecord.get("name")))
                    ? "" : String.valueOf(callRecord.get("name")));
            orderUser.setCallTime(String.valueOf(callRecord.get("date")));
            orderUser.setType(Integer.parseInt(type));
            orderUser.setDuration(Integer.parseInt(duration));
            response.add(orderUser);
        }
        //将电话号码相同的时长相加，并且其他字段取第一条
        List<OrderUserCallRecordResponse> result = new ArrayList<>();
        //用于去重判断
        List<String> phones = new ArrayList<>();
        for (OrderUserCallRecordResponse callRecord : response) {
            if (phones.contains(callRecord.getMobile())) {
                continue;
            }
            OrderUserCallRecordResponse recordResponse = new OrderUserCallRecordResponse();
            //这里复制最新的数据
            BeanUtils.copyProperties(callRecord, recordResponse);
            //判断重复加上当前手机号
            phones.add(callRecord.getMobile());
            Integer count = 0;
            int index = 0;
            for (OrderUserCallRecordResponse record : response) {
                if (callRecord.getMobile().equals(record.getMobile())) {
                    count += record.getDuration();
                    index++;
                }
            }
            recordResponse.setDuration(count);
            recordResponse.setCallCount(index);
            result.add(recordResponse);
        }
        //先按照通话次数，在按照通话时长排序
        this.sortOrderUserCallRecord(result);
        return result;
    }


    /**
     * Do filter of call records;
     * @param orderUserCallRecordResponseList
     * @Author Milo
     * @return
     */
    public List<OrderUserCallRecordResponse> orderUserCallRecordWithFilter(List<OrderUserCallRecordResponse> orderUserCallRecordResponseList) throws Exception {

        if(orderUserCallRecordResponseList == null || orderUserCallRecordResponseList.isEmpty()){
            logger.info("The call record is none, no need to send message.");
            throw new ServiceExceptionSpec(ExceptionEnum.CALL_RECORD_IS_NONE);
//            return new ArrayList<>();
        }

        return  orderUserCallRecordResponseList.stream()
                .filter(item -> item.getMobile().length() >= 8)
                .filter(item ->  item.getMobile().startsWith("62"))
                .collect(Collectors.toList());
    }

    private void sortOrderUserCallRecord(List<OrderUserCallRecordResponse> result) {
        if (CollectionUtils.isEmpty(result)) {
            return;
        }
        Collections.sort(result, new Comparator<OrderUserCallRecordResponse>() {
            @Override
            public int compare(OrderUserCallRecordResponse ord1, OrderUserCallRecordResponse ord2) {
                if (ord1.getCallCount().compareTo(ord2.getCallCount()) == 0) {
                    return 0 - ord1.getDuration().compareTo(ord2.getDuration());
                } else {
                    return 0 - ord1.getCallCount().compareTo(ord2.getCallCount());
                }
            }
        });
    }


    private void sortOrderUserCallRecordByDuration(List<OrderUserCallRecordResponse> result) {
        if (CollectionUtils.isEmpty(result)) {
            return;
        }
        Collections.sort(result, new Comparator<OrderUserCallRecordResponse>() {
            @Override
            public int compare(OrderUserCallRecordResponse ord1, OrderUserCallRecordResponse ord2) {
                return 0 - ord1.getDuration().compareTo(ord2.getDuration());
            }
        });
    }


    /**
     * 获得经常使用的两个联系人，若小于两个使用紧急联系人
     *
     * @param request
     * @return
     */
    public List<FrequentOrderUserCallRecordResponse> frequentOrderUserCallRecordMongo(OrderMongoRequest request) throws ServiceExceptionSpec {
        if (StringUtils.isEmpty(request.getOrderNo())) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        //复借订单查询第一笔订单号
        List<OrdOrder> orderNos = manOrderOrderService.orderListByOrderNo(request.getOrderNo());
        if (CollectionUtils.isEmpty(orderNos)) {
            return null;
        }
        String userUuid = orderNos.get(0).getUserUuid();
        //全部数据集合
        List<FrequentOrderUserCallRecordResponse> response = new ArrayList<>();
        //使用紧急联系人
        List<FrequentOrderUserCallRecordResponse> lists = getUsrLinkManInfo(userUuid);
        if (CollectionUtils.isEmpty(lists)) {
            return response;
        }

        TeleCallResult teleCallResult = new TeleCallResult();
        teleCallResult.setDisabled(0);
        teleCallResult.setUserUuid(userUuid);
        teleCallResult.setOrderNo(request.getOrderNo());
        List<TeleCallResult> teleCallResults = teleCallResultDao.scan(teleCallResult);
        if (CollectionUtils.isEmpty(teleCallResults)) {
            return new ArrayList<>();
        }
        //取出外呼中有效并且为紧急联系人的数据
        teleCallResults = teleCallResults.stream().filter(elem -> elem.getCallType().equals(3)
                && elem.getCallResultType().equals(1)).collect(Collectors.toList());

        //如果外呼系统出现异常
        if (CollectionUtils.isEmpty(teleCallResults)) {
            logger.info("外呼系统出现异常，没有取得联系人！订单号：" + request.getOrderNo());
            response.add(lists.get(0));
            response.add(lists.get(1));
            return response;
        }
        for (FrequentOrderUserCallRecordResponse result : lists) {
            String tempPhone = getFormatPhone(result.getMobile());
            for (TeleCallResult tele : teleCallResults) {
                if (!StringUtils.isEmpty(tempPhone) && tempPhone.equals(tele.getTellNumber())) {
                    if (response.size() == 2) {
                        break;
                    }
                    //外呼结果中可能有重复
                    if (response.size() == 1) {
                        String tempMobile = getFormatPhone(response.get(0).getMobile());
                        if (tempPhone.equals(tempMobile)) {
                            continue;
                        }
                    }
                        response.add(result);
                }
            }
        }
        if (response.size() == 1) {
            logger.info("orderNo:" + request.getOrderNo() + "只取得一个电核联系人");
            String mobile = response.get(0).getMobile();
            if (mobile.equals(lists.get(1).getMobile())) {
                response.add(lists.get(0));
            } else {
                response.add(lists.get(1));
            }
        }
        if (response.size() == 0) {
            response.add(lists.get(0));
            response.add(lists.get(1));
        }

        for (FrequentOrderUserCallRecordResponse res : response) {
            String mobile = res.getMobile();
            if (!StringUtils.isEmpty(mobile)) {
                if (mobile.startsWith("62")) {
                    res.setMobile(mobile.replaceFirst("62", "0").replaceAll(" ", ""));
                } else if (mobile.startsWith("+62")) {
                    res.setMobile(mobile.replaceFirst("\\+62", "0").replaceAll(" ", ""));
                }
            }
        }
        //如果是家庭主妇角色判断家庭收入来源者号码
        if (getUserRole(userUuid).equals(3)) {
            UsrHouseWifeDetail usrHouseWifeDetail = new UsrHouseWifeDetail();
            usrHouseWifeDetail.setDisabled(0);
            usrHouseWifeDetail.setUserUuid(userUuid);
            List<UsrHouseWifeDetail> usrHouseWifeDetails =
                    usrHouseWifeDetailDao.scan(usrHouseWifeDetail);
            String houseWifiPhone = CollectionUtils.isEmpty(usrHouseWifeDetails) ? ""
                    : usrHouseWifeDetails.get(0).getSourceTel();
            if (!"".equals(houseWifiPhone)) {
                if (houseWifiPhone.startsWith("62")) {
                    houseWifiPhone = houseWifiPhone.replaceFirst("62", "0");
                } else if (houseWifiPhone.startsWith("+62")) {
                    houseWifiPhone = houseWifiPhone.replaceFirst("\\+62", "0");
                }

                if (response.get(0).getMobile().equals(houseWifiPhone)) {
                    response.get(0).setRealName(usrHouseWifeDetails.get(0).getSourceName());
                } else {
                    final String tempName = response.get(0).getRealName();
                    final String tempMobile = response.get(0).getMobile();
                    response.get(0).setRealName(usrHouseWifeDetails.get(0).getSourceName());
                    response.get(0).setMobile(houseWifiPhone);
                    response.get(1).setMobile(tempMobile);
                    response.get(1).setRealName(tempName);
                }
            }
        }
        return response;
    }

    private String getFormatPhone(String phone) {
        if (!StringUtils.isEmpty(phone)) {
            String tempPhone = CheakTeleUtils.telephoneNumberValid2(phone);
            return "62" + tempPhone;
        }
        return "";
    }

    /**
     * 通过用户ID，获得用户角色
     * @param uuid
     * @return
     */
    private Integer getUserRole(String uuid) {

        UsrUser usrUser = new UsrUser();
        usrUser.setDisabled(0);
        usrUser.setUuid(uuid);
        List<UsrUser> lists = usrUserDao.scan(usrUser);
        return CollectionUtils.isEmpty(lists) ? 0 : lists.get(0).getUserRole();
    }

    /**
     * 过滤表情
     */
    private String filterEmoji(String text) {
        if (!StringUtils.isEmpty(text)) {
            return EmojiParser.removeAllEmojis(text);
        }
        return "";
    }

    /**
     * 判断两个电话号码，是否相同 08，628，+628
     * @param mobile1
     * @param mobile2
     * @return
     */
    private boolean judgePhoneNum(String mobile1, String mobile2) {

        mobile1 = mobile1.replaceAll(" ", "").replaceAll("-", "");
        mobile2 = mobile2.replaceAll(" ", "").replaceAll("-", "");
        if (mobile1.startsWith("08")) {
            mobile1 = mobile1.substring(2);
        }
        if (mobile1.startsWith("628")) {
            mobile1 = mobile1.substring(3);
        }
        if (mobile1.startsWith("+628")) {
            mobile1 = mobile1.substring(4);
        }

        if (mobile2.startsWith("08")) {
            mobile2 = mobile2.substring(2);
        }
        if (mobile2.startsWith("628")) {
            mobile2 = mobile2.substring(3);
        }
        if (mobile2.startsWith("+628")) {
            mobile2 = mobile2.substring(4);
        }

        return mobile1.equals(mobile2);
    }

    private List<FrequentOrderUserCallRecordResponse> getUsrLinkManInfo(String userUuid) {
        if (StringUtils.isEmpty(userUuid)) {
            return new ArrayList<>();
        }
        UsrLinkManInfo usrLinkManInfo = new UsrLinkManInfo();
        usrLinkManInfo.setDisabled(0);
        usrLinkManInfo.setUserUuid(userUuid);
        usrLinkManInfo.set_orderBy("sequence ");
        List<UsrLinkManInfo> lists = usrLinkManDao.scan(usrLinkManInfo);
        if (CollectionUtils.isEmpty(lists)) {
            return new ArrayList<>();
        }
        lists = lists.stream().filter(e ->e.getSequence() != 3).collect(Collectors.toList());

        return lists.stream().map(elem -> new FrequentOrderUserCallRecordResponse
                (elem.getContactsMobile(), filterEmoji(elem.getContactsName()))).collect(Collectors.toList());

    }

    /**
     * 判断日期是否超过30天
     *
     * @param date
     * @return
     */
//    private boolean judgeOver30Days(String date) {
//        if (StringUtils.isEmpty(date)) {
//            return false;
//        }
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.add(Calendar.MONTH, -1);
//
//        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            return calendar.getTime().compareTo(sf.parse(date)) > 0 ?
//                    true : false;
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    /**
     * 获得家庭主妇收入来源
     * @param request
     */
    public HouseWifiInfoResponse getHouseWifiInfo(OrderMongoRequest request) {
        if (request.getUserUuid() == null) {
            return new HouseWifiInfoResponse();
        }
        UsrHouseWifeDetail usrHouseWifeDetail = new UsrHouseWifeDetail();
        usrHouseWifeDetail.setDisabled(0);
        usrHouseWifeDetail.setUserUuid(request.getUserUuid());
        List<UsrHouseWifeDetail> lists = usrHouseWifeDetailDao.scan(usrHouseWifeDetail);
        if (CollectionUtils.isEmpty(lists)) {
            return new HouseWifiInfoResponse();
        }
        UsrHouseWifeDetail temp = lists.get(0);
        return new HouseWifiInfoResponse(temp.getCompanyName(),temp.getCompanyPhone(),
                temp.getSourceName(), temp.getIncomeType());
    }

}
