package com.yqg.service.user.service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.yqg.collection.dao.CollectionRemarkDao;
import com.yqg.common.enums.order.BlackListTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.order.dao.OrdBlackDao;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdBlack;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdDeviceExtendInfoService;
import com.yqg.service.risk.service.AutoCallSendService;
import com.yqg.service.user.request.LinkManCheckRequest;
import com.yqg.service.user.request.LinkManRequest;
import com.yqg.service.user.response.BackupLinkman;
import com.yqg.service.user.response.BackupLinkmanResponse;
import com.yqg.service.user.response.LinkmanCheckResponse;
import com.yqg.service.user.response.LinkmanCheckResponse.LinkmanDetail;
import com.yqg.service.util.CustomEmojiUtils;
import com.yqg.system.entity.CallResult;
import com.yqg.system.entity.TeleCallResult;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.dao.UsrLinkManDao;
import com.yqg.user.entity.BackupLinkmanItem;
import com.yqg.user.entity.UsrLinkManInfo;
import com.yqg.user.entity.UsrUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserLinkManService {


    @Autowired
    private UsrLinkManDao usrLinkManDao;

    @Autowired
    private UsrDao usrDao;

    @Autowired
    private UserBackupLinkmanService userBackupLinkmanService;


    @Autowired
    private CollectionRemarkDao collectionRemarkDao;

    @Autowired
    private OrdDao ordDao;

    @Autowired
    private OrdBlackDao ordBlackDao;
    @Autowired
    private AutoCallSendService autoCallSendService;
    @Autowired
    private OrdDeviceExtendInfoService ordDeviceExtendInfoService;


    /***
     * 添加紧急联系人
     * @param request
     * @param needBackupLinkman
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public BackupLinkmanResponse addEmergencyLinkmans(LinkManRequest request, boolean needBackupLinkman) throws Exception{
        if(CollectionUtils.isEmpty(request.getLinkmanList())){
            //数据为空
            log.warn("the linkman is empty");
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        request.getLinkmanList().stream().forEach(elem -> {
            elem.setUserUuid(request.getUserUuid());
            elem.setContactsName(CustomEmojiUtils.removeEmoji(elem.getContactsName()));
        });
        for(UsrLinkManInfo item: request.getLinkmanList()){
            addLinkman(item);
        }
        ordDeviceExtendInfoService.saveExtendInfo(request.getLinkmanList());
        //if(!needBackupLinkman){
            return new BackupLinkmanResponse();
        //}
    }



    public LinkmanCheckResponse getLinkmanCheckResult(LinkManCheckRequest request){
        LinkmanCheckResponse result = new LinkmanCheckResponse();
        result.setNeedFill(true);
        result.setLinkmanStep(LinkmanCheckResponse.LinkmanFillStep.ONE);
        //查询联系人
        List<UsrLinkManInfo> linkmanList = userBackupLinkmanService.getLinkManInfo(request.getUserUuid());
        //查询备用号码
        Optional<UsrLinkManInfo> backupNumber = getBackupNumber(request.getUserUuid());
        if(backupNumber.isPresent()){
            UsrLinkManInfo elem = backupNumber.get();
            result.getLinkmanList().add(new LinkmanDetail(elem.getContactsName(),elem.getRelation(),
                    elem.getContactsMobile(),elem.getSequence(),elem.getWaOrLine(),true, LinkmanCheckResponse.TelNumberValidationEnum.NOT_SURE));
        }

        if(CollectionUtils.isEmpty(linkmanList)){
          return result;
        }
        List<LinkmanDetail> checkList = linkmanList.stream().map(elem->new LinkmanDetail(elem.getContactsName(),elem.getRelation(),
                elem.getContactsMobile(),elem.getSequence(),elem.getWaOrLine(),true,LinkmanCheckResponse.TelNumberValidationEnum.NOT_SURE)).collect(Collectors.toList());

        //历史单联系人不够，重新填写
        if(linkmanList.size()<4){
            checkList.forEach(elem->{
                elem.setIsValid(false);
                elem.setValidationStatus(LinkmanCheckResponse.TelNumberValidationEnum.NOT_SURE);
            });
            result.getLinkmanList().addAll(checkList);
            return result;
        }
        OrdOrder searchParam = new OrdOrder();
        searchParam.setUuid(request.getOrderNo());
        searchParam.setDisabled(0);
        OrdOrder currentOrder = ordDao.scan(searchParam).get(0);
        if(currentOrder.getBorrowingCount()>1){
            //复借检查[上一笔订单是外呼拒绝的，需要重新填写]
            //上一笔订单
            List<OrdOrder> historyOrderList = ordDao.getOrder(request.getUserUuid());
            if(!CollectionUtils.isEmpty(historyOrderList) && historyOrderList.size()>1){
                OrdOrder lastOrder = historyOrderList.get(historyOrderList.size()-2);
                //上笔订单是否外呼拒绝
                OrdBlack ordBlackSearch = new OrdBlack();
                ordBlackSearch.setDisabled(0);
                ordBlackSearch.setOrderNo(lastOrder.getUuid());
                List<OrdBlack> dbData = ordBlackDao.scan(ordBlackSearch);
                boolean autoCallReject = false;
                if(!CollectionUtils.isEmpty(dbData) && (dbData.get(0).getRuleHitNo().contains(BlackListTypeEnum.MULTI_AUTO_CALL_REJECT_BACKUP_LINKMAN_VALID_COUNT.getMessage())
                        ||dbData.get(0).getRuleHitNo().contains(BlackListTypeEnum.MULTI_AUTO_CALL_REJECT_LINKMAN_VALID_COUNT.getMessage())
                )){
                    autoCallReject = true;
                }
                //上一笔拒绝的需要重新填写
                if(autoCallReject){
                    result.setNeedFill(true);
                    result.setLinkmanStep(LinkmanCheckResponse.LinkmanFillStep.ONE);
                    checkList.forEach(elem ->
                            {
                                elem.setIsValid(true);
                                elem.setValidationStatus(LinkmanCheckResponse.TelNumberValidationEnum.NOT_SURE);
                            }
                    );
                    result.getLinkmanList().addAll(checkList);
                    return result;
                }
            }
            //上一笔成功订单订单号【上一笔订单催收说相应的号码无效的需要重新填写】
            List<OrdOrder> successOrders = ordDao.getLastSuccessOrder(request.getUserUuid());
            String lastSuccessOrderNo = successOrders.get(0).getUuid();
            for (LinkmanDetail item : checkList) {
                Integer count = collectionRemarkDao.countOfInvalidCollectionCallResult(lastSuccessOrderNo, item.getContactsMobile());
                boolean isValid = (count == 0);
                item.setIsValid(isValid);
                item.setValidationStatus(isValid == false ? LinkmanCheckResponse.TelNumberValidationEnum.INVALID :
                        LinkmanCheckResponse.TelNumberValidationEnum.NOT_SURE);
            }
        }

        //判断是否进行上次审核而且审核外呼无效【最后一次外呼的紧急联系人的4个号码】
        List<CallResult> callResultList = autoCallSendService.getTelCallList(request.getOrderNo(), null);
        if (!CollectionUtils.isEmpty(callResultList)) {
            //当前已经填写的紧急联系人外呼情况
            Map<String, CallResult> callResultMap =
                    callResultList.stream().filter(elem -> elem.getCallType().equals(TeleCallResult.CallTypeEnum.EMERGENCY_LINKMAN.getCode()))
                            .collect(Collectors.toMap(CallResult::getTellNumber, Function.identity()));
            checkList.forEach(elem -> {
                String callFormatMobile = "62" + CheakTeleUtils.telephoneNumberValid2(elem.getContactsMobile());
                if (callResultMap == null || callResultMap.isEmpty()) {
                    //无相应号码的外呼情况，设置为
                    elem.setIsValid(true);
                    elem.setValidationStatus(getNewTransStatus(elem,LinkmanCheckResponse.TelNumberValidationEnum.NOT_SURE));
                } else {
                    CallResult resultForMobile = callResultMap.get(callFormatMobile);
                    if (resultForMobile == null) {
                        //无
                        elem.setIsValid(true);
                        elem.setValidationStatus(getNewTransStatus(elem,LinkmanCheckResponse.TelNumberValidationEnum.NOT_SURE));
                    } else if (resultForMobile.isCallValid()) {
                        //有效
                        elem.setIsValid(true);
                        elem.setValidationStatus(getNewTransStatus(elem,LinkmanCheckResponse.TelNumberValidationEnum.VALID));
                    } else {
                        //无效
                        elem.setIsValid(true);
                        elem.setValidationStatus(getNewTransStatus(elem,LinkmanCheckResponse.TelNumberValidationEnum.INVALID));
                    }
                }

            });
        }
        result.getLinkmanList().addAll(checkList);


       //检查第一联系人是否无效：
        Optional<LinkmanDetail> firstLinkman =
                checkList.stream().filter(elem->elem.getSequence()==UsrLinkManInfo.SequenceEnum.FIRST.getCode()).findFirst();

        if(!firstLinkman.isPresent()){
            //无第一联系人,返回从第一个页面开始
            return result;
        }

        if(!firstLinkman.get().getIsValid()){
            //第一联系人无效，返回从第一各页面开始
            return result;
        }

        //其他联系人是否无效
        List<LinkmanDetail> otherLinkmanList =
                checkList.stream().filter(elem->elem.getSequence()!=UsrLinkManInfo.SequenceEnum.FIRST.getCode()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(otherLinkmanList)){
            //无其他联系人，从第二也开始填写
            result.setLinkmanStep(LinkmanCheckResponse.LinkmanFillStep.TWO);
            return result;
        }

        Long invalidCount = otherLinkmanList.stream().filter(elem->!elem.getIsValid()).count();
        if(invalidCount>0){
            //其他联系人无效，从第二页开始
            result.setLinkmanStep(LinkmanCheckResponse.LinkmanFillStep.TWO);
            return result;
        }else{
            result.setNeedFill(false); //无无效的联系人，不需要重新填写
            return result;
        }
    }

    /***
     * 将detail的状态转为toStatus
     * @param detail
     * @param toStatus
     * @return
     */
    private LinkmanCheckResponse.TelNumberValidationEnum getNewTransStatus(LinkmanDetail detail, LinkmanCheckResponse.TelNumberValidationEnum toStatus) {
        if (toStatus.equals(LinkmanCheckResponse.TelNumberValidationEnum.INVALID)) {
            return toStatus;
        }
        if (toStatus.equals(LinkmanCheckResponse.TelNumberValidationEnum.NOT_SURE)) {
            if (detail.getValidationStatus().equals(LinkmanCheckResponse.TelNumberValidationEnum.INVALID)) {
                return LinkmanCheckResponse.TelNumberValidationEnum.INVALID;
            }
            return LinkmanCheckResponse.TelNumberValidationEnum.NOT_SURE;
        }
        if(toStatus.equals(LinkmanCheckResponse.TelNumberValidationEnum.VALID)) {
            if (detail.getValidationStatus().equals(LinkmanCheckResponse.TelNumberValidationEnum.INVALID)) {
                return LinkmanCheckResponse.TelNumberValidationEnum.INVALID;
            }
            return LinkmanCheckResponse.TelNumberValidationEnum.VALID;
        }
        return toStatus;
    }

    private Optional<UsrLinkManInfo> getBackupNumber(String userUuid){
        UsrLinkManInfo search = new UsrLinkManInfo();
        search.setUserUuid(userUuid);
        search.setDisabled(0);
        search.setSequence(UsrLinkManInfo.SequenceEnum.OWNER_BACKUP.getCode());
        List<UsrLinkManInfo> searchList = usrLinkManDao.scan(search);
        if(CollectionUtils.isEmpty(searchList)){
            return Optional.empty();
        }
        return Optional.of(searchList.get(0));
    }

    private void addLinkman(UsrLinkManInfo linkmanInfo) throws Exception{
        if (StringUtils.isEmpty(linkmanInfo.getContactsMobile())) {
            log.info("the input contact mobile is empty");
            return;
        }
        linkmanInfo.setFormatMobile(CheakTeleUtils.telephoneNumberValid2(linkmanInfo.getContactsMobile()));
//        log.info("--log ,start check: {}",JsonUtils.serialize(linkmanInfo));
        if(linkmanInfo.getSequence()==UsrLinkManInfo.SequenceEnum.OWNER_BACKUP.getCode()&& StringUtils.isNotEmpty(linkmanInfo.getContactsMobile())){
            // 判断备选联系人是否是已经注册的用户
            String formatPhone = CheakTeleUtils.telephoneNumberValid2(linkmanInfo.getContactsMobile());
            UsrUser usr = new UsrUser();
            log.info("加密手机号:"+ DESUtils.encrypt(formatPhone));
            usr.setMobileNumberDES(DESUtils.encrypt(formatPhone));
            usr.setDisabled(0);
            List<UsrUser> scanList = this.usrDao.scan(usr);
            if (!CollectionUtils.isEmpty(scanList)){
                log.error("备选联系人已经是Do-It注册用户");
                throw new ServiceException(ExceptionEnum.USER_ALTERNET_PHONENO_IS_EXIT);
            }
        }


        UsrLinkManInfo searchInfo = new UsrLinkManInfo();
        searchInfo.setUserUuid(linkmanInfo.getUserUuid());
        searchInfo.setSequence(linkmanInfo.getSequence());
        searchInfo.setDisabled(0);
        List<UsrLinkManInfo> resultList = usrLinkManDao.scan(searchInfo);
        if(CollectionUtils.isEmpty(resultList)){
            //add
            usrLinkManDao.insert(linkmanInfo);
        }else{

            UsrLinkManInfo dbResult = resultList.get(0);
            dbResult.setContactsMobile(linkmanInfo.getContactsMobile());
            dbResult.setContactsName(linkmanInfo.getContactsName());
            dbResult.setRelation(linkmanInfo.getRelation());
            dbResult.setWaOrLine(linkmanInfo.getWaOrLine());
            dbResult.setFormatMobile(linkmanInfo.getFormatMobile());
            usrLinkManDao.update(dbResult);
        }
    }



    public List<UsrLinkManInfo> getLinkmanByFormatMobile(String mobile){
        return usrLinkManDao.getLinkmanByFormatMobile(mobile);
    }


}
