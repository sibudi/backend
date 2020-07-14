package com.yqg.manage.service.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vdurmont.emoji.EmojiParser;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.manage.dal.user.UserUserDao;
import com.yqg.manage.service.mongo.request.OrderMongoRequest;
import com.yqg.manage.service.mongo.response.OrderEmergencyContactResponse;
import com.yqg.manage.service.mongo.response.HouseWifiInfoResponse;
import com.yqg.manage.service.order.ManOrderOrderService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author alan
 */
@Component
public class OrderUserContactMongoService {

    private static Logger logger = LoggerFactory.getLogger(OrderUserContactMongoService.class);

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

    /**
     * Get two contacts from telecallresult, if less than two use emergency contacts
     *
     * @param request
     * @return
     */
    public List<OrderEmergencyContactResponse> getOrderEmergencyContact(OrderMongoRequest request) throws ServiceExceptionSpec {
        if (StringUtils.isEmpty(request.getOrderNo())) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        //Check the first order number for re-borrowing orders
        List<OrdOrder> orderNos = manOrderOrderService.orderListByOrderNo(request.getOrderNo());
        if (CollectionUtils.isEmpty(orderNos)) {
            return null;
        }
        String userUuid = orderNos.get(0).getUserUuid();
        List<OrderEmergencyContactResponse> response = new ArrayList<>();
        //Use emergency contacts
        List<OrderEmergencyContactResponse> lists = getUsrLinkManInfo(userUuid);
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
        //Retrieve data from outgoing calls that are valid and emergency contacts
        teleCallResults = teleCallResults.stream().filter(elem -> elem.getCallType().equals(3)
                && elem.getCallResultType().equals(1)).collect(Collectors.toList());

        //If the outbound system is abnormal
        if (CollectionUtils.isEmpty(teleCallResults)) {
            logger.info("There is an exception in the outgoing call system and no contact is obtained! order number：" + request.getOrderNo());
            response.add(lists.get(0));
            response.add(lists.get(1));
            return response;
        }
        for (OrderEmergencyContactResponse result : lists) {
            String tempPhone = getFormatPhone(result.getMobile());
            for (TeleCallResult tele : teleCallResults) {
                if (!StringUtils.isEmpty(tempPhone) && tempPhone.equals(tele.getTellNumber())) {
                    if (response.size() == 2) {
                        break;
                    }
                    //There may be duplicates in the outgoing call results
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
            logger.info("orderNo:" + request.getOrderNo() + "Only one contact person is obtained");
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

        for (OrderEmergencyContactResponse res : response) {
            String mobile = res.getMobile();
            if (!StringUtils.isEmpty(mobile)) {
                if (mobile.startsWith("62")) {
                    res.setMobile(mobile.replaceFirst("62", "0").replaceAll(" ", ""));
                } else if (mobile.startsWith("+62")) {
                    res.setMobile(mobile.replaceFirst("\\+62", "0").replaceAll(" ", ""));
                }
            }
        }
        //If it is the role of a housewife, determine the source of household income
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
     * Obtain user role through user ID
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
     * Filter emoticons
     */
    private String filterEmoji(String text) {
        if (!StringUtils.isEmpty(text)) {
            return EmojiParser.removeAllEmojis(text);
        }
        return "";
    }

    private List<OrderEmergencyContactResponse> getUsrLinkManInfo(String userUuid) {
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

        return lists.stream().map(elem -> new OrderEmergencyContactResponse
                (elem.getContactsMobile(), filterEmoji(elem.getContactsName()))).collect(Collectors.toList());

    }

    /**
     * Get a source of income for housewives
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
