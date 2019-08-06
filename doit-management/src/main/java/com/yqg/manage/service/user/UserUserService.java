package com.yqg.manage.service.user;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yqg.base.multiDataSource.annotation.ReadDataSource;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.manage.dal.collection.ManCollectionRemarkDao;
import com.yqg.manage.dal.user.UserUserDao;
import com.yqg.manage.entity.collection.ManCollectionRemark;
import com.yqg.manage.enums.ContactModeEnum;
import com.yqg.manage.enums.ManCheckOperatorEnum;
import com.yqg.manage.enums.ManOperatorEnum;
import com.yqg.manage.service.collection.CollectionRemarkService;
import com.yqg.manage.service.collection.request.ManCollectionRemarkRequest;
import com.yqg.manage.service.collection.response.CollectionOrderResponse;
import com.yqg.manage.service.order.request.ManualRepayOrderRequest;
import com.yqg.manage.service.system.ManAuthManagerService;
import com.yqg.manage.service.user.request.ManUserUserRequest;
import com.yqg.manage.service.user.response.BaseUserMobileResponse;
import com.yqg.manage.service.user.response.PayDepositResponse;
import com.yqg.manage.service.user.response.UserMobileResponse;
import com.yqg.service.util.LoginSysUserInfoHolder;
import com.yqg.manage.util.PageDataUtils;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.third.Inforbip.Enum.CallReusltEnum;
import com.yqg.system.dao.TeleCallResultDao;
import com.yqg.system.entity.TeleCallResult;
import com.yqg.user.dao.BackupLinkmanItemDao;
import com.yqg.user.dao.UsrLinkManDao;
import com.yqg.user.dao.UsrWorkDetailDao;
import com.yqg.user.entity.BackupLinkmanItem;
import com.yqg.user.entity.UsrLinkManInfo;
import com.yqg.user.entity.UsrUser;
import com.yqg.user.entity.UsrWorkDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author alan
 */
@Slf4j
@Component
public class UserUserService {
    @Autowired
    private UserUserDao userUserDao;

    @Autowired
    private UsrLinkManDao usrLinkManDao;

    @Autowired
    private TeleCallResultDao teleCallResultDao;

    @Autowired
    private UsrWorkDetailDao usrWorkDetailDao;

    @Autowired
    private ManCollectionRemarkDao manCollectionRemarkDao;

    @Autowired
    private BackupLinkmanItemDao backupLinkmanItemDao;

    @Autowired
    private CollectionRemarkService collectionRemarkService;

    @Autowired
    private ManAuthManagerService manAuthManagerService;


    public Integer insertManCollectionRemark(ManCollectionRemarkRequest request) throws ServiceExceptionSpec {

        try {
            collectionRemarkService.insertCollectionRemark(request);
        } catch (Exception e) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_ITEM_ERROR);
        }
        return 1;

    }


    /**
     * 通过用户的用户名或手机号查询用户*/
    public String getUuidByRealNameOrMobile(ManUserUserRequest userSearch) throws ServiceExceptionSpec {
        String resultStr = "";
        if (StringUtils.isNotBlank(userSearch.getMobile()) || StringUtils.isNotBlank(userSearch.getRealName())) {

            List<UsrUser> userResult = this.uuidByRealNameOrMobile(userSearch);

            StringBuilder uuidStr = new StringBuilder();
            for (UsrUser Obj : userResult) {
                if (StringUtils.isNotBlank(Obj.getUuid()))
                    uuidStr.append("'" + Obj.getUuid() + "',");
            }

            resultStr = StringUtils.removeEnd(uuidStr.toString(), ",");
        }
        return resultStr;
    }

    /**
     * 通过用户的姓名手机号查寻用户uuid
     */
    public List<UsrUser> uuidByRealNameOrMobile(ManUserUserRequest userRequest)
            throws ServiceExceptionSpec {
        List<UsrUser> result = this.userUserDao.getUuidByRealNameOrMobile(userRequest);
        return result;
    }

    /**
     * 通过uuid集合查询用户基本数据集合
     */
    public List<UsrUser> getUserInfoByUuids(String uuids) {
        List<UsrUser> result = this.userUserDao.getInfoByUuids(uuids);
        return result;
    }

    /**
     * 通过用户的uuid查询用户基本信息*/
    public UsrUser userInfoByUuid(String uuid) throws ServiceExceptionSpec {
        UsrUser search = new UsrUser();
        search.setUuid(uuid);
//        search.setDisabled(0);
        List<UsrUser> result = this.userUserDao.scan(search);
        if(CollectionUtils.isEmpty(result)){
            return null;
        }
        return result.get(0);
    }

    /**
     * 通过用户uuid查询掩码手机号和未掩码手机号*/
    public UsrUser userMobileByUuid(ManUserUserRequest userRequest) throws ServiceExceptionSpec {
        String userUuid = userRequest.getUserUuid();

        if(StringUtils.isEmpty(userUuid)){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        UsrUser search = this.userInfoByUuid(userUuid);
        if(!StringUtils.isEmpty(search.getMobileNumberDES())){
            search.setMobileNumberDES(DESUtils.decrypt(search.getMobileNumberDES()));
//            search.setMobileNumberDES("6289676059775");
        }else {
            search.setMobileNumberDES("");
        }

//        if (!manAuthManagerService.hasAuthorityByRoleName(ManCheckOperatorEnum.listCheckOperatorEnum())) {
//            search.setMobileNumberDES("******");
//        }
        if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnumNoCollect())) {
            String idCardNo = search.getIdCardNo();
            if (StringUtils.isNotBlank(idCardNo)) {
                search.setIdCardNo(idCardNo.substring(0,4) + "********" + idCardNo.substring(idCardNo.length() - 4));
            }
        }
        return search;
    }


    /**
     * 通过用户id将用户置为无效
     * @param uuid
     * @param remark 备注
     * @return
     */
    public int updateUsrStatusInvalid(String uuid, String remark) {

        if (StringUtils.isEmpty(uuid)) {
            return 0;
        }

        UsrUser usrUser = new UsrUser();
        usrUser.setUuid(uuid);
        usrUser.setRemark(remark);
        usrUser.setStatus(0);

        return userUserDao.update(usrUser);
    }
    /**
     *通过手机号修改客户姓名
     *
     * @param userUserRequest mobile手机号, realName真实姓名
     */
    public Integer getUserListByMobile(ManUserUserRequest userUserRequest) throws Exception {

        if (StringUtils.isBlank(userUserRequest.getMobile())) {
            return 0;
        }
        UsrUser seach = new UsrUser();
        seach.setMobileNumberDES(DESUtils.encrypt(userUserRequest.getMobile()));
        seach.setDisabled(0);
        //通过手机号和订单状态查询当前用户订单状态
        List<UsrUser> data = this.userUserDao.scan(seach);
        if (CollectionUtils.isEmpty(data)) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }
        UsrUser tempUser = data.get(0);
        seach.setUuid(tempUser.getUuid());
        seach.setRealName(userUserRequest.getRealName());
        seach.setUpdateUser(LoginSysUserInfoHolder.getLoginSysUserId());
        return  this.userUserDao.update(seach);
    }

    /**
     * 根据用户手机号码删除用户（置为黑名单）
     * @param request
     */
    public Integer setUserDisabled(ManualRepayOrderRequest request) {

        if (StringUtils.isEmpty(request.getMobile())
                || request.getAddBlackReason() == null) {
            return 0;
        }
        UsrUser usrUser = new UsrUser();
        usrUser.setDisabled(0);
        usrUser.setMobileNumberDES(DESUtils.encrypt(request.getMobile()));
        List<UsrUser> usrUsers = userUserDao.scan(usrUser);
        if (CollectionUtils.isEmpty(usrUsers)) {
            return 0;
        }
        usrUser = usrUsers.get(0);
        //置为无效
        usrUser.setDisabled(1);
        if (StringUtils.isNotBlank(request.getAddBlackRemark())) {
            usrUser.setRemark(usrUser.getAddBlackRemark() + request.getAddBlackRemark());
        }
        usrUser.setAddBlackReason(request.getAddBlackReason());
        return userUserDao.update(usrUser);
    }
    /**
     *查询联系人信息
     * @param request
     * @return
     */
    public List<UserMobileResponse> listUserMobileContract(ManualRepayOrderRequest request) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(request.getOrderNo())
                || StringUtils.isEmpty(request.getUserUuid())) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
            return new ArrayList<>();
        }
        UsrLinkManInfo usrLinkManInfo = new UsrLinkManInfo();
        usrLinkManInfo.setDisabled(0);
        usrLinkManInfo.setUserUuid(request.getUserUuid());
        List<UsrLinkManInfo> lists = usrLinkManDao.scan(usrLinkManInfo);

        if (CollectionUtils.isEmpty(lists)) {
            return new ArrayList<>();
        }
        List<UserMobileResponse> responses = new ArrayList<>();
        //封装外呼和催收结果
        for (UsrLinkManInfo item : lists) {

            if (item.getSequence() == UsrLinkManInfo.SequenceEnum.OWNER_BACKUP.getCode()) {
                continue;
            }

            UserMobileResponse response = new UserMobileResponse();
            response.setMobile(item.getContactsMobile());
            response.setRealName(item.getContactsName());
            response.setWaOrLine(item.getWaOrLine());
            response.setRelation(item.getRelation());
            response.setSequence(item.getSequence());

            //保存外呼结果
            response.setCallResult(getCallResultEnum(getTeleCallByMobile(request.getUserUuid(),
                    request.getOrderNo(), response.getMobile())));

            //保存催收结果
            setManCollectResult(response, request.getUserUuid(), request.getOrderNo(), response.getMobile());
            responses.add(response);
        }

        return responses;
    }
    /**
     *查询备用类型人信息
     * @param request
     * @return
     */
    public List<BaseUserMobileResponse> listBaseMobileOnOtherContract(ManualRepayOrderRequest request) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(request.getOrderNo())
                || StringUtils.isEmpty(request.getUserUuid())) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        BackupLinkmanItem backupLinkmanItem = new BackupLinkmanItem();
        backupLinkmanItem.setDisabled(0);
        backupLinkmanItem.setUserUuid(request.getUserUuid());
        backupLinkmanItem.setOrderNo(request.getOrderNo());
        List<BackupLinkmanItem> lists = backupLinkmanItemDao.scan(backupLinkmanItem);

        if (CollectionUtils.isEmpty(lists)) {
            return new ArrayList<>();
        }
        List<BaseUserMobileResponse> responses = new ArrayList<>();
        //封装外呼和催收结果
        for (BackupLinkmanItem item : lists) {

            BaseUserMobileResponse response = new BaseUserMobileResponse();
            response.setRealName(item.getLinkmanName());
            response.setMobile(item.getLinkmanNumber());

            //保存外呼结果
            response.setCallResult(getCallResultEnum(getTeleCallByMobile(request.getUserUuid(),
                    request.getOrderNo(), response.getMobile())));

            //保存催收结果
            setManCollectResult(response, request.getUserUuid(), request.getOrderNo(), response.getMobile());
            responses.add(response);
        }

        return responses;
    }

    /**
     *查询本人，备用和公司电话
     * @param request
     * @return
     */
    public BaseUserMobileResponse getBaseMobile(ManualRepayOrderRequest request) throws ServiceExceptionSpec {

        if (request.getType() == null || StringUtils.isEmpty(request.getOrderNo())
                || StringUtils.isEmpty(request.getUserUuid())) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        //客服角色的用户直接返回
        if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
            return new BaseUserMobileResponse();
        }
        BaseUserMobileResponse response = new BaseUserMobileResponse();
        if (request.getType().equals(1)) {
            ManUserUserRequest userRequest = new ManUserUserRequest();
            userRequest.setUserUuid(request.getUserUuid());
            UsrUser usrUser = this.userMobileByUuid(userRequest);
            response.setMobile(usrUser.getMobileNumberDES());
        } else if (request.getType().equals(2)) {
            UsrLinkManInfo usrLinkManInfo = new UsrLinkManInfo();
            usrLinkManInfo.setDisabled(0);
            usrLinkManInfo.setUserUuid(request.getUserUuid());
            usrLinkManInfo.setSequence(UsrLinkManInfo.SequenceEnum.OWNER_BACKUP.getCode());
            List<UsrLinkManInfo> lists = usrLinkManDao.scan(usrLinkManInfo);
            if (CollectionUtils.isEmpty(lists)) {
                return response;
            }
            response.setMobile(lists.get(0).getContactsMobile());
        } else if (request.getType().equals(3)) {
            UsrWorkDetail usrWorkDetail = new UsrWorkDetail();
            usrWorkDetail.setDisabled(0);
            usrWorkDetail.setUserUuid(request.getUserUuid());
            List<UsrWorkDetail> lists = usrWorkDetailDao.scan(usrWorkDetail);
            if (CollectionUtils.isEmpty(lists)) {
                return response;
            }
            response.setMobile(lists.get(0).getCompanyPhone());
        }
        //保存外呼结果
        response.setCallResult(getCallResultEnum(getTeleCallByMobile(request.getUserUuid(),
                request.getOrderNo(), response.getMobile())));

        //保存催收结果
        setManCollectResult(response, request.getUserUuid(), request.getOrderNo(), response.getMobile());

        return response;
    }


    public TeleCallResult getTeleCallByMobile(String userUuid, String orderNo, String mobile) {

        if (StringUtils.isEmpty(mobile)) {
            return null;
        }
        mobile = CheakTeleUtils.telephoneNumberValid2(mobile);
        if (StringUtils.isEmpty(mobile)) {
            return null;
        }
        mobile = "62" + mobile;

        TeleCallResult search = new TeleCallResult();
        search.setDisabled(0);
        search.setTellNumber(mobile);
        search.setUserUuid(userUuid);
        search.setOrderNo(orderNo);
        search.set_orderBy("updateTime desc");
        List<TeleCallResult> lists = teleCallResultDao.scan(search);
        if (CollectionUtils.isEmpty(lists)) {
            return null;
        }
        return lists.get(0);
    }

    public String getCallResultEnum(TeleCallResult teleCallResult) {

        if (teleCallResult == null) {
            return "";
        }
        Integer callResult = teleCallResult.getCallResult();
        if (callResult == 0) {
            return "";
        }
        if (callResult.equals(CallReusltEnum.CONNECT.getCode()) ||
                callResult.equals(CallReusltEnum.BUSY.getCode()) ||
                callResult.equals(CallReusltEnum.NO_ANSWER.getCode()) ||
                callResult.equals(CallReusltEnum.USER_REFUSE.getCode())) {
//            return "可接通";
            return "1";
        } else if (callResult.equals(CallReusltEnum.ARREARS.getCode()) ||
                callResult.equals(CallReusltEnum.NUMBER_NOT_EXIST.getCode()) ||
                callResult.equals(CallReusltEnum.DOWN_TINE.getCode()) ||
                callResult.equals(CallReusltEnum.INVALID_NUMBER.getCode()) ||
                callResult.equals(CallReusltEnum.NOT_ON_SERVER.getCode())) {
//            return "不可接通";
            return "2";
        } else {
//            return "不确定";
            return "3";
        }

    }

    /**
     * 查询该笔订单的电话号码的，催收结果情况
     * @param response
     * @param userUuid
     * @param orderNo
     * @param contractMoble
     */
    public void setManCollectResult (BaseUserMobileResponse response, String userUuid,
                                     String orderNo, String contractMoble) {

//        contractMoble = CheakTeleUtils.telephoneNumberValid2(contractMoble);
//        if (StringUtils.isEmpty(contractMoble)) {
//            return;
//        }
//        contractMoble = "62" + contractMoble;

        ManCollectionRemark manCollectionRemark = new ManCollectionRemark();
        manCollectionRemark.setDisabled(0);
        manCollectionRemark.setOrderNo(orderNo);
        manCollectionRemark.setUserUuid(userUuid);
        manCollectionRemark.setContactMobile(contractMoble);
        manCollectionRemark.set_orderBy("updateTime ASC");
        List<ManCollectionRemark> lists = manCollectionRemarkDao.scan(manCollectionRemark);

        if (CollectionUtils.isEmpty(lists)) {
            return ;
        }
        for (ManCollectionRemark collectionRemark : lists) {
            if (collectionRemark.getContactMode() != null &&
                    ContactModeEnum.CONTACT_MODE_PHONE.getType() == collectionRemark.getContactMode()) {
                response.setContactResultPhone(collectionRemark.getContactResult());
            } else if (collectionRemark.getContactMode() != null &&
                    ContactModeEnum.CONTACT_MODE_WA.getType() == collectionRemark.getContactMode()) {
                response.setContactResultWA(collectionRemark.getContactResult());
            } else if (collectionRemark.getContactMode() != null &&
                    ContactModeEnum.CONTACT_MODE_SMS.getType() == collectionRemark.getContactMode()) {
                response.setContactResultSms(collectionRemark.getContactResult());
            }
        }

    }

    /**
     * 根据 还款码 和  订单号 查询还款状态
     * @param request
     */
    public PageData<List<PayDepositResponse>> listpayDeposit(ManualRepayOrderRequest request) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(request.getOrderNo())
                && StringUtils.isEmpty(request.getPaymentCode())) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        PageHelper.startPage(request.getPageNo(), request.getPageSize());


        List<PayDepositResponse> payDepositResponses =
                userUserDao.listpayDeposit(request.getOrderNo(), request.getPaymentCode());

        PageInfo pageInfo = new PageInfo(payDepositResponses);

        return PageDataUtils.mapPageInfoToPageData(pageInfo);

    }
}
