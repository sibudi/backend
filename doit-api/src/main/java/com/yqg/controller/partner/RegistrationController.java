package com.yqg.controller.partner;

import com.yqg.common.annotations.CompatibleRSA;
import com.yqg.common.annotations.H5Request;
import com.yqg.common.constants.MessageConstants;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.GetIpAddressUtil;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.user.request.*;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.entity.UsrLinkManInfo;
import com.yqg.service.user.service.UsrBankService;
import com.yqg.service.user.service.UsrBaseInfoService;
import com.yqg.service.user.service.UserLinkManService;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.request.OrdRequest;
import com.yqg.service.order.response.OrderOrderResponse;
import com.yqg.service.partner.request.AddressRequest;
import com.yqg.service.partner.request.AttachmentRequest;
import com.yqg.service.partner.request.BankInfoRequest;
import com.yqg.service.partner.request.CustomerInfoRequest;
import com.yqg.service.partner.request.DeviceInfoRequest;
import com.yqg.service.partner.request.OtherContactRequest;
import com.yqg.service.partner.request.RegistrationRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghuaizhou on 2017/11/24.
 */
@H5Request
@RestController
@Slf4j
public class RegistrationController {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UsrService usrService;
    @Autowired
    private OrdService ordService;
    @Autowired
    private UsrBaseInfoService usrBaseInfoService;
    @Autowired
    private UserLinkManService userLinkManService;
    @Autowired
    private UsrBankService usrBankService;


    @ApiOperation("/v1/partner/registration")
    @RequestMapping(value = "/v1/partner/registration", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> registration(HttpServletRequest httpRequest, @RequestBody RegistrationRequest reqRequest) throws Exception {
        log.info("/v1/partner/registration - request body {}", JsonUtils.serialize(reqRequest));
        
        //sign up
        UsrRequst usrRequst = ConvertToUsrRequest(reqRequest);
        this.usrService.signupV3(usrRequst);
        
        //order step 1
        OrdRequest orderRequest = ConvertToOrdRequest(reqRequest, usrRequst.getUserUuid());
        OrderOrderResponse orderResponse = this.ordService.toOrder(orderRequest, redisClient);

        //order step 2
        SaveUserPhotosRequest(reqRequest, usrRequst.getUserUuid());
        
        //work info 
        UsrWorkBaseInfoRequest workBaseInfoRequest = ConvertToUsrWorkBaseInfoRequest(reqRequest, orderResponse.getOrderNo(), usrRequst.getUserUuid());
        usrBaseInfoService.addWorkBaseInfo(workBaseInfoRequest);

        // //emergency contact
        LinkManRequest request = ConvertToLinkManRequest(reqRequest, orderResponse.getOrderNo(), usrRequst.getUserUuid());
        userLinkManService.addEmergencyLinkmans(request, true);

        // //bind card
        UsrBankRequest userBankRequest = ConvertToUsrBankRequest(reqRequest, orderResponse.getOrderNo(), usrRequst.getUserUuid());
        usrBankService.bindBankCard(userBankRequest, redisClient);

        return ResponseEntityBuilder.success(
            MessageFormat.format(MessageConstants.USER_SUCCESS_REGISTRATION, usrRequst.getEmail())
        );
    }

    @ApiOperation("/v1/partner/bank/check")
    @RequestMapping(value = "/v1/partner/bank/check", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntity<Object> registration(String bankCode, String bankNumberNo, String bankCardName, String userUuid) throws Exception {
        
        usrBankService.cheakBankCardBinForPartner(bankCode, bankNumberNo, bankCardName, userUuid);
        return ResponseEntityBuilder.success("success");
    }

    private UsrRequst ConvertToUsrRequest(RegistrationRequest reqRequest){
        UsrRequst request = new UsrRequst();
        DeviceInfoRequest deviceInfo = reqRequest.getCustomerInfo().getDeviceInfo();
        
        request.setAndroidId("");
        request.setClient_type("android");
        request.setClient_version("partner-api");
        request.setDeviceId("");
        request.setDeviceSysModel("");
        request.setEmail(reqRequest.getCustomerInfo().getEmailAddress());
        request.setFcmToken(deviceInfo.getFcmToken());
        request.setIPAdress("");
        request.setLbsX(deviceInfo.getLongitude());
        request.setLbsY(deviceInfo.getLatitude());
        request.setMac("");
        request.setMobileNumber(reqRequest.getCustomerInfo().getMobileNo());
        request.setNet_type("");
        request.setRealName(reqRequest.getCustomerInfo().getFullName());
        request.setResolution("");
        request.setChannel_name("partner-api");
        return request;
    }

    private OrdRequest ConvertToOrdRequest(RegistrationRequest reqRequest, String userUuid){
        DeviceInfoRequest deviceInfo = reqRequest.getCustomerInfo().getDeviceInfo();
        AddressRequest address = reqRequest.getCustomerInfo().getAddress();

        OrdRequest orderRequest = new OrdRequest();
        orderRequest.setUserUuid(userUuid);
        orderRequest.setProductUuid(reqRequest.getProductType());
        orderRequest.setDetailed("");
        orderRequest.setDeviceType(deviceInfo.getDeviceType());
        orderRequest.setDeviceName("");
        orderRequest.setPhoneBrand("");
        orderRequest.setTotalMemory("");
        orderRequest.setRemainMemory("");
        orderRequest.setTotalSpace("");
        orderRequest.setRemainSpace("");
        orderRequest.setClient_type("android");
        orderRequest.setClient_version("partner-api");
        orderRequest.setChannel_name("partner-api");
        orderRequest.setIMEI("");
        orderRequest.setIMSI("");
        orderRequest.setSimNumber("");
        orderRequest.setCpuType("");
        orderRequest.setLastPowerOnTime("");
        orderRequest.setDnsStr("");
        orderRequest.setIsRoot("");
        orderRequest.setMemoryCardCapacity("");
        orderRequest.setWifiList("");
        orderRequest.setMobileLanguage("");
        orderRequest.setIsSimulator("");
        orderRequest.setBattery("");
        orderRequest.setPictureNumber("");
        orderRequest.setProvince(address.getProvince());
        orderRequest.setCity(address.getRegion());
        orderRequest.setBigDirect(address.getSubdistrict());
        orderRequest.setSmallDirect(address.getVillage());
        orderRequest.setOrderType(0);
        
        return orderRequest;
    }

    private void SaveUserPhotosRequest(RegistrationRequest reqRequest, String userUuid){
        
        SaveUserPhotoRequest photoRequest = null;
        for(AttachmentRequest attachment : reqRequest.getCustomerInfo().getAttachments()){

            photoRequest = new SaveUserPhotoRequest();
            photoRequest.setPhotoType(attachment.getAttachmentType());
            photoRequest.setPhotoUrl(attachment.getAttachmentUrl());
            photoRequest.setUserUuid(userUuid);

            usrBaseInfoService.saveUserPhoto(photoRequest);
        }
    }

    private UsrWorkBaseInfoRequest ConvertToUsrWorkBaseInfoRequest(RegistrationRequest reqRequest, String orderNo, String userUuid){

        CustomerInfoRequest cust = reqRequest.getCustomerInfo();
        DeviceInfoRequest deviceInfo = reqRequest.getCustomerInfo().getDeviceInfo();

        UsrWorkBaseInfoRequest workBaseInfoRequest = new UsrWorkBaseInfoRequest();
        workBaseInfoRequest.setOrderNo(orderNo);
        workBaseInfoRequest.setEmail(cust.getEmailAddress());
        workBaseInfoRequest.setAcademic(cust.getLastEducation());
        workBaseInfoRequest.setBirthday(DateUtils.DateToString(cust.getDateOfBirth()));
        workBaseInfoRequest.setReligion(cust.getReligion());
        workBaseInfoRequest.setMotherName(cust.getMotherMaidenName());
        workBaseInfoRequest.setChildrenAmount(Integer.parseInt(cust.getNoOfChildren()));
        workBaseInfoRequest.setMaritalStatus(Integer.parseInt(cust.getMaritalStatus()));
        workBaseInfoRequest.setProvince(cust.getWorkInfo().getCompanyProvince());
        workBaseInfoRequest.setCity(cust.getWorkInfo().getCompanyRegion());
        workBaseInfoRequest.setBigDirect(cust.getWorkInfo().getCompanySubdistrict());
        workBaseInfoRequest.setSmallDirect(cust.getWorkInfo().getCompanyVillage());
        workBaseInfoRequest.setDetailed(cust.getWorkInfo().getCompanySpecificAddress());
        workBaseInfoRequest.setLbsY(deviceInfo.getLongitude());
        workBaseInfoRequest.setLbsX(deviceInfo.getLatitude());
        workBaseInfoRequest.setAddressType(UsrAddressEnum.COMPANY.getType());
        workBaseInfoRequest.setBorrowUse(cust.getLoanPurpose());
        workBaseInfoRequest.setBirthProvince(cust.getPlaceOfBirth());
        // workBaseInfoRequest.setBirthCity();
        // workBaseInfoRequest.setBirthBigDirect();
        // workBaseInfoRequest.setBirthSmallDirect();
    
        workBaseInfoRequest.setMonthlyIncome(cust.getWorkInfo().getMonthlyIncome());
    
        workBaseInfoRequest.setNpwp(cust.getTaxNumber());
        workBaseInfoRequest.setUserUuid(userUuid);
        // workBaseInfoRequest.setInsuranceCardPhoto();
        // workBaseInfoRequest.setWhatsappAccount();
        // workBaseInfoRequest.setKkCardPhoto();

        return workBaseInfoRequest;
    }

    private LinkManRequest ConvertToLinkManRequest(RegistrationRequest reqRequest, String orderNo, String userUuid){
        LinkManRequest request = new LinkManRequest();
        List<UsrLinkManInfo> linkmanList = new ArrayList<UsrLinkManInfo>();
        
        List<OtherContactRequest> contacts = reqRequest.getCustomerInfo().getOtherContacts();
        UsrLinkManInfo linkManInfo = null;
        int i = 0;
        for (OtherContactRequest contact : contacts){

            linkManInfo = new UsrLinkManInfo();
            linkManInfo.setUserUuid(userUuid);
            linkManInfo.setContactsName(contact.getOtherContactName());
            linkManInfo.setRelation(contact.getOtherRelationship());
            linkManInfo.setContactsMobile(contact.getOtherMobileNo());
            linkManInfo.setSequence(i++);
            linkManInfo.setWaOrLine(contact.getOtherWhatsappNo());
            linkManInfo.setFormatMobile(contact.getOtherMobileNo());

            linkmanList.add(linkManInfo);
        }
        request.setLinkmanList(linkmanList);
        request.setOrderNo(orderNo);
        request.setUserUuid(userUuid);
        
        return request;
    }
    
    private UsrBankRequest ConvertToUsrBankRequest(RegistrationRequest reqRequest, String orderNo, String userUuid){
        List<BankInfoRequest> banksInfo = reqRequest.getCustomerInfo().getBanksInfo();
        UsrBankRequest request = new UsrBankRequest();

        if(banksInfo != null && banksInfo.size() > 0){
            request.setBankCode(banksInfo.get(0).getBankCode());//
            request.setBankNumberNo(banksInfo.get(0).getAccountNo());//
            request.setBankCardName(banksInfo.get(0).getCardName());//
            request.setOrderNo(orderNo);
        }

        request.setUserUuid(userUuid);
        
        return request;
    }
    
    
}
