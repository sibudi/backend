package com.yqg.manage.service.order;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.BaseRequest;
import com.yqg.manage.dal.order.ManOrderOrderDao;
import com.yqg.manage.dal.user.ManUserBankDao;
import com.yqg.manage.enums.ManCheckOperatorEnum;
import com.yqg.manage.enums.ManCollectorOperatorEnum;
import com.yqg.manage.enums.ManOperatorEnum;
import com.yqg.manage.service.order.request.ManOrderUserRequest;
import com.yqg.manage.service.order.response.OrderBankResponse;
import com.yqg.manage.service.system.ManAuthManagerService;
import com.yqg.manage.service.user.UserUserService;
import com.yqg.mongo.dao.OrderThirdDataDal;
import com.yqg.mongo.dao.OrderUserDataDal;
import com.yqg.mongo.entity.OrderThirdDataMongo;
import com.yqg.mongo.entity.OrderUserDataMongo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.user.model.*;
import com.yqg.service.user.response.UsrAttachmentResponse;
import com.yqg.service.user.service.UsrBaseInfoService;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.dao.UsrAddressDetailDao;
import com.yqg.user.dao.UsrAttachmentInfoDao;
import com.yqg.user.dao.UsrFaceVerifyResultDao;
import com.yqg.user.dao.UsrHouseWifeDetailDao;
import com.yqg.user.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author alan
 */
@Slf4j
@Component
public class ManOrderUserDataService {
    @Autowired
    private OrderUserDataDal orderUserDataDal;

    @Autowired
    private OrderThirdDataDal orderThirdDataDal;

    @Autowired
    private UsrBaseInfoService usrBaseInfoService;

    @Autowired
    private UserUserService userUserService;

    @Autowired
    private UsrService usrService;

    @Autowired
    private UsrAttachmentInfoDao usrAttachmentInfoDao;

    @Autowired
    private ManUserBankDao usrBankDao;

    @Autowired
    private ManOrderOrderDao manOrderOrderDao;

    @Autowired
    private UsrFaceVerifyResultDao usrFaceVerifyResultDao;

    @Autowired
    private UsrHouseWifeDetailDao usrHouseWifeDetailDao;

    @Autowired
    private UsrAddressDetailDao usrAddressDetailDao;

    @Autowired
    private ManAuthManagerService manAuthManagerService;

    /**
     * 通过订单编号和类型mongo查询订单用户信息*/
    public Object getOrderUserDataMongo(ManOrderUserRequest request) throws Exception {
        String orderNo = request.getOrderNo();
        Integer type = request.getType();
        if(StringUtils.isEmpty(orderNo) || type == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        OrdOrder ordOrder = new OrdOrder();
        ordOrder.setUuid(orderNo);
        ordOrder.setDisabled(0);
        List<OrdOrder> ordOrders = manOrderOrderDao.scan(ordOrder);
        if (CollectionUtils.isEmpty(ordOrders)) {
            return new ArrayList<>();
        }
        String userUuid = ordOrders.get(0).getUserUuid();

        //基本信息
        if (type == 3) {
            UsrUser result = this.userUserService.userInfoByUuid(userUuid);
            if (result.getUserRole() == 3){
                HouseWifeInfoResponse response = this.usrBaseInfoService.getHouseWifeDetail(userUuid);
                setOrderDataResponse(response);
                return response == null ? new UsrWorkBaseInfoModel() : response;
            }
        }

        //工作或学校信息
        if (type == 4) {
            UsrUser result = this.userUserService.userInfoByUuid(userUuid);
            if(result.getUserRole() == 3) {
                UsrHouseWifeDetail usrHouseWifeDetail = new UsrHouseWifeDetail();
                usrHouseWifeDetail.setDisabled(0);
                usrHouseWifeDetail.setUserUuid(userUuid);
                List<UsrHouseWifeDetail> response = this.usrHouseWifeDetailDao.scan(usrHouseWifeDetail);
                if (CollectionUtils.isEmpty(response)) {
                    return new HouseWifeInfoResponse();
                }
                HouseWifeInfoResponse houseWifeInfoResponse = new HouseWifeInfoResponse();
                BeanUtils.copyProperties(response.get(0), houseWifeInfoResponse);
                //封装工作地址
                UsrAddressDetail usrAddressDetail = new UsrAddressDetail();
                usrAddressDetail.setUserUuid(userUuid);
                usrAddressDetail.setDisabled(0);
                usrAddressDetail.setAddressType(UsrAddressEnum.FAMILY.getType());
                List<UsrAddressDetail> usrAddressDetailList = this.usrAddressDetailDao.scan(usrAddressDetail);
                if (!usrAddressDetailList.isEmpty()){
                    usrAddressDetail = usrAddressDetailList.get(0);
                    String address = usrAddressDetail.getProvince() + " " + usrAddressDetail.getCity() + " " +
                            usrAddressDetail.getBigDirect() + " " + usrAddressDetail.getSmallDirect() + " " +
                            usrAddressDetail.getDetailed();
                    houseWifeInfoResponse.setWorkAddressDetail(address);
                }
                setWorkInfoResponse(houseWifeInfoResponse);
                return houseWifeInfoResponse;
            }
        }

        //获得银行卡信息
        if (type == 7) {
            //取得订单中的usrBank
            UsrBank usrBank = new UsrBank();
            usrBank.setUuid(ordOrders.get(0).getUserBankUuid());
            List<UsrBank> usrBanks = usrBankDao.scan(usrBank);

            if (CollectionUtils.isEmpty(usrBanks)) {
                return new OrderBankResponse();
            }
            UsrBank bank = usrBanks.get(0);
            OrderBankResponse response = new OrderBankResponse();
            response.setBankCardName(bank.getBankCardName());
            response.setBankCode(bank.getBankCode());
            response.setBankNumberNo(bank.getBankNumberNo());
            response.setStatus(bank.getStatus());
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
                String number = response.getBankNumberNo();
                if (StringUtils.isNotBlank(number)) {
                    response.setBankNumberNo("*******" + number.substring(number.length() - 4));
                }
            }
            return response;

        }
        //补充信息
        if (type == 8) {

            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
                return new ArrayList<UsrAttachmentResponse>();
            }
            BaseRequest baseRequest = new BaseRequest();
            baseRequest.setUserUuid(ordOrders.get(0).getUserUuid());
            List<UsrAttachmentResponse> attachmentResponses = this.usrService.initSupplementInfo(baseRequest);
            if (attachmentResponses == null) {
                return new ArrayList<UsrAttachmentResponse>();
            }
            if (request.getLangue() != null && request.getLangue() == 2) {
                for (UsrAttachmentResponse response : attachmentResponses) {
                    response.setAttachmentName(getAttachmenNameWithType(response.getAttachmentType()));
                }
            }
            return attachmentResponses;
        }

        //身份认证
        if (type == 2) {
            UsrIdentityModel result = this.usrBaseInfoService.getIdentityInfo(ordOrders.get(0).getUserUuid(), request.getCheckQualityOrNot());
            return result == null ? new UsrIdentityModel() : result;
        }

        OrderUserDataMongo search = new OrderUserDataMongo();
        search.setOrderNo(orderNo);
        search.setInfoType(type);
        search.setStatus(1);
        List<OrderUserDataMongo> result = this.orderUserDataDal.find(search);
        if(CollectionUtils.isEmpty(result)){
            return new ArrayList<>();
        }

        return result.get(0).getData();

    }

    /**
     * 判断权限后封装返回结果
     * @param object
     */
    private void setOrderDataResponse(Object object) {
        if (object instanceof UsrStudentBaseInfoModel) {
            UsrStudentBaseInfoModel result = (UsrStudentBaseInfoModel) object;
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
                result.setBirthday("******");
                result.setMotherName("******");
            }
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnumNoCheck())) {
                result.setFatherMobile("******");
                result.setMotherMobile("******");
                result.setEmail("******");
            }
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnumNoCollect())) {
                result.setEmail("******");
                result.setAcademic("******");
                result.setFamilyMemberAmount(0);
                result.setDetailed("******");
                result.setBigDirect("**");
                result.setSmallDirect("**");
                result.setCity("**");
                result.setProvince("**");
            }
        } else if (object instanceof UsrWorkBaseInfoModel) {
            UsrWorkBaseInfoModel result = (UsrWorkBaseInfoModel)object;
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
                result.setBirthday("******");
                result.setMotherName("******");
                result.setMaritalStatus(-1);

            }
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnumNoCheck())) {
                result.setEmail("******");
            }
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnumNoCollect())) {
                result.setEmail("******");
                result.setAcademic("******");
                result.setChildrenAmount(0);
                result.setDetailed("******");
                result.setBigDirect("**");
                result.setSmallDirect("**");
                result.setCity("**");
                result.setProvince("**");
            }
        } else if (object instanceof HouseWifeInfoResponse) {
            HouseWifeInfoResponse result = (HouseWifeInfoResponse)object;
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
                result.setBirthday("******");
                result.setMotherName("******");
                result.setMaritalStatus(-1);
            }
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnumNoCheck())) {
                result.setEmail("******");
            }
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnumNoCollect())) {
                result.setEmail("******");
                result.setAcademic("******");
                result.setChildrenAmount(0);
                result.setDetailed("******");
                result.setBigDirect("**");
                result.setSmallDirect("**");
                result.setCity("**");
                result.setProvince("**");
            }
        }
    }
    /**
     * 判断权限后封装工作信息返回结果
     * @param object
     */
    private void setWorkInfoResponse(Object object) {
        if (object instanceof UsrSchoolInfoModel) {
            UsrSchoolInfoModel result = (UsrSchoolInfoModel) object;
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
                result.setComputerUrl("*******");
                result.setEnglishUrl("*******");
                result.setScholarshipUrl("*******");
                result.setStudentCardUrl("*******");
                result.setMajor("*******");
                result.setOtherCertificateUrl("*******");
                result.setSchoolName("*******");
                result.setStudentNo("*******");
            }
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnumNoCollect())) {
                result.setCity("*******");
                result.setDetailed("*******");
                result.setSmallDirect("*******");
                result.setBigDirect("*******");
                result.setProvince("*******");
            }
        } else if (object instanceof UsrWorkInfoModel) {
            UsrWorkInfoModel result = (UsrWorkInfoModel)object;
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
                result.setCompanyName("******");
                result.setDependentBusiness("******");
                result.setDetailed("******");
                result.setEmployeeNumber("******");
                result.setExtensionNumber("******");
                result.setMonthlyIncome("******");

            }
            if (!manAuthManagerService.hasAuthorityByRoleName(ManCheckOperatorEnum.listCheckOperatorEnumAddSC())) {
                result.setExtensionNumber("******");
                result.setCompanyPhone("******");
            }
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnumNoCollect())) {
                result.setCity("*******");
                result.setDetailed("*******");
                result.setSmallDirect("*******");
                result.setBigDirect("*******");
                result.setProvince("*******");
                result.setPositionName("******");
                result.setCompanyPhone("******");
            }
        } else if (object instanceof HouseWifeInfoResponse) {
            HouseWifeInfoResponse result = (HouseWifeInfoResponse)object;
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
                result.setCompanyName("******");
                result.setSourceTel("******");
            }
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnumNoCollect())) {
                result.setEmail("******");
                result.setAcademic("******");
                result.setChildrenAmount(0);
                result.setCity("*******");
                result.setDetailed("*******");
                result.setSmallDirect("*******");
                result.setBigDirect("*******");
                result.setProvince("*******");
                result.setCompanyPhone("******");
            }
        }
    }
    /**
     * 通过订单编号和类型mysql查询订单用户信息*/
    public Object getOrderUserDataSql(ManOrderUserRequest request) throws Exception {
        String userUuid = request.getUserUuid();
        Integer type = request.getType();
        String orderNo = request.getOrderNo();
        if(StringUtils.isEmpty(userUuid) || type == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        //身份认证
        if (type == 2) {
            UsrIdentityModel result = this.usrBaseInfoService.getIdentityInfo(userUuid, request.getCheckQualityOrNot());
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnumNoCollect())) {
                String idCardNo = result.getIdCardNo();
                if (StringUtils.isNotBlank(idCardNo)) {
                    result.setIdCardNo(idCardNo.substring(0,4) + "********" + idCardNo.substring(idCardNo.length() - 4));
                }
            }
            return result == null ? new UsrIdentityModel() : result;
        }
        //基本信息
        if (type == 3) {
            UsrUser result = this.userUserService.userInfoByUuid(userUuid);
            if(result.getUserRole() == 1){
                UsrStudentBaseInfoModel response = this.usrBaseInfoService.getStudentBaseInfo(userUuid);
                setOrderDataResponse(response);
                return response == null ? new UsrStudentBaseInfoModel() : response;
            }else if (result.getUserRole() == 2){
                UsrWorkBaseInfoModel response = this.usrBaseInfoService.getWorkBaseInfo(userUuid);
                setOrderDataResponse(response);
                return response == null ? new UsrWorkBaseInfoModel() : response;
            } else if (result.getUserRole() == 3){
                HouseWifeInfoResponse response = this.usrBaseInfoService.getHouseWifeDetail(userUuid);
                setOrderDataResponse(response);
                return response == null ? new UsrWorkBaseInfoModel() : response;
            }
        }
        //工作或学校信息
        if (type == 4) {
            UsrUser result = this.userUserService.userInfoByUuid(userUuid);
            if(result.getUserRole() == 1){
                UsrSchoolInfoModel response = this.usrBaseInfoService.getStudentSchoolInfo(userUuid);
                setWorkInfoResponse(response);
                return response == null ? new UsrSchoolInfoModel() : response;
            }else if (result.getUserRole() == 2){
                UsrWorkInfoModel response = this.usrBaseInfoService.getUsrWorkInfo(userUuid);
                setWorkInfoResponse(response);
                return response == null ? new UsrWorkInfoModel() : response;
            } else {
                UsrHouseWifeDetail usrHouseWifeDetail = new UsrHouseWifeDetail();
                usrHouseWifeDetail.setDisabled(0);
                usrHouseWifeDetail.setUserUuid(userUuid);
                List<UsrHouseWifeDetail> response = this.usrHouseWifeDetailDao.scan(usrHouseWifeDetail);
                if (CollectionUtils.isEmpty(response)) {
                    return new HouseWifeInfoResponse();
                }
                HouseWifeInfoResponse houseWifeInfoResponse = new HouseWifeInfoResponse();
                BeanUtils.copyProperties(response.get(0), houseWifeInfoResponse);
                //封装工作地址
                UsrAddressDetail usrAddressDetail = new UsrAddressDetail();
                usrAddressDetail.setUserUuid(userUuid);
                usrAddressDetail.setDisabled(0);
                usrAddressDetail.setAddressType(UsrAddressEnum.FAMILY.getType());
                List<UsrAddressDetail> usrAddressDetailList = this.usrAddressDetailDao.scan(usrAddressDetail);
                if (!usrAddressDetailList.isEmpty()){
                    usrAddressDetail = usrAddressDetailList.get(0);
                    String address = usrAddressDetail.getProvince() + " " + usrAddressDetail.getCity() + " " +
                            usrAddressDetail.getBigDirect() + " " + usrAddressDetail.getSmallDirect() + " " +
                            usrAddressDetail.getDetailed();
                    houseWifeInfoResponse.setWorkAddressDetail(address);
                }
                setWorkInfoResponse(response);
                return houseWifeInfoResponse;
            }
        }
        //联系人信息
        if (type == 5) {
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
                return new UsrLinkManModel();
            }
            UsrLinkManModel usrLinkManModel = this.usrBaseInfoService.getLinkManInfo(userUuid);
            if (!manAuthManagerService.hasAuthorityByRoleName(ManCheckOperatorEnum.listCheckOperatorEnumAddSC())) {
                usrLinkManModel.setContactsMobile1("******");
                usrLinkManModel.setContactsMobile2("******");
                usrLinkManModel.setAlternatePhoneNo("******");
            }
            return usrLinkManModel == null ? new UsrLinkManModel() : usrLinkManModel;
        }
        //验证信息
        if (type == 6) {
            Object checkData = this.usrAttachmentInfoDao.getCheckAttachmenInfoByUserUuid(userUuid);
            if (checkData == null) {
                return new ArrayList<UsrAttachmentInfo>();
            }
            return checkData;
        }
        //补充信息
        if (type == 8) {
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
                return new ArrayList<UsrAttachmentResponse>();
            }
            BaseRequest search = new BaseRequest();
            search.setUserUuid(userUuid);
            List<UsrAttachmentResponse> result = this.usrService.initSupplementInfo(search);
            if (result == null) {
                return new ArrayList<UsrAttachmentResponse>();
            }
            if (request.getLangue() != null && request.getLangue() == 2) {
                for (UsrAttachmentResponse response : result) {
                    response.setAttachmentName(getAttachmenNameWithType(response.getAttachmentType()));
                }
            }
            return result;
        }
        //获得银行卡信息
        if (type == 7) {

            //催收角色直接返回
            if (!manAuthManagerService.hasAuthorityByRoleName(ManCollectorOperatorEnum.listCollectorOperatorEnum())) {
                log.info("collector bank is empty show.");
                return new OrderBankResponse();
            }

            //取得订单中的usrBank
            OrdOrder ordOrder = new OrdOrder();
            ordOrder.setUuid(orderNo);
            ordOrder.setDisabled(0);
            List<OrdOrder> ordOrders = manOrderOrderDao.scan(ordOrder);
            if (CollectionUtils.isEmpty(ordOrders)) {
                return new OrderBankResponse();
            }
            UsrBank usrBank = new UsrBank();
            usrBank.setUuid(ordOrders.get(0).getUserBankUuid());
            List<UsrBank> usrBanks = usrBankDao.scan(usrBank);

            if (CollectionUtils.isEmpty(usrBanks)) {
                return new OrderBankResponse();
            }
            UsrBank bank = usrBanks.get(0);
            OrderBankResponse response = new OrderBankResponse();
            response.setBankCardName(bank.getBankCardName());
            response.setBankCode(bank.getBankCode());
            response.setBankNumberNo(bank.getBankNumberNo());
            response.setStatus(bank.getStatus());
            /**
             * 如果是客服
             */
            if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
                String number = response.getBankNumberNo();
                if (StringUtils.isNotBlank(number)) {
                    response.setBankNumberNo("*******" + number.substring(number.length() - 4));
                }
            }
            return response;
        }
        return null;
    }

    public String getAttachmenNameWithType(int attachmentType) {
        String attachmentName = "";
        switch(attachmentType) {
            case 4:
                //信用卡
                attachmentName = "Kartu Kredit";
                break;
            case 5:
                //驾驶证
                attachmentName = "SIM";
                break;
            case 6:
                //护照
                attachmentName = "identitas paspor anda";
                break;
            case 7:
                //家庭卡
                attachmentName = "KK";
                break;
            case 8:
                //工资单
                attachmentName = "Slip Gaji";
                break;
            case 9:
                //银行卡流水
                attachmentName = "Mutasi Bank/bukti transaksi";
                break;
//            case 17:
//                //保险卡
//                attachmentName = "Foto BPJS";
//                break;
            default:
        }

        return attachmentName;
    }

    /**
     * 通过订单号和用户uuid获得活体识别分
     * @param orderListSearchResquest
     * @return
     */
    public String getPairVerifySimilarity(ManOrderUserRequest orderListSearchResquest) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(orderListSearchResquest.getOrderNo())
                || StringUtils.isEmpty(orderListSearchResquest.getUserUuid())) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        OrderThirdDataMongo orderThirdDataMongo = new OrderThirdDataMongo();
        orderThirdDataMongo.setOrderNo(orderListSearchResquest.getOrderNo());
        orderThirdDataMongo.setUserUuid(orderListSearchResquest.getUserUuid());
        //活体识别分
        orderThirdDataMongo.setThirdType(1);
        orderThirdDataMongo.setStatus(1);
        List<OrderThirdDataMongo> orderThirdDataMongos = orderThirdDataDal.find(orderThirdDataMongo);
        String result = "";
        if (!CollectionUtils.isEmpty(orderThirdDataMongos)) {
            String tempStr = orderThirdDataMongos.get(0).getData();
            int start = tempStr.indexOf("pair_verify_similarity");
            int end = tempStr.indexOf("query_image_package_result");
            if (start != -1 && end != -1) {
                result = tempStr.substring(start + 24, end -2);
                //保留一个小数
                result = result.substring(0, result.indexOf(".")) +
                        result.substring(result.indexOf("."), result.indexOf(".") + 2);
                return result;
            }
        }
        //若没有结果查询face++结果
        UsrFaceVerifyResult usrFaceVerifyResult = new UsrFaceVerifyResult();
        usrFaceVerifyResult.setDisabled(0);
        usrFaceVerifyResult.setUserUuid(orderListSearchResquest.getUserUuid());
        usrFaceVerifyResult.set_orderBy("updateTime desc");
        List<UsrFaceVerifyResult> lists = usrFaceVerifyResultDao.scan(usrFaceVerifyResult);
        if (!CollectionUtils.isEmpty(lists)) {
            result = lists.get(0).getScore() == null ? "" : lists.get(0).getScore().toString();

            result = result.substring(0, result.indexOf(".")) +
                    result.substring(result.indexOf("."), result.indexOf(".") + 2);
            result = "face++: " + result;
        }
        return result;
    }
}
