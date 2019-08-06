package com.yqg.service.order;

import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.mongo.dao.UserAppsDal;
import com.yqg.mongo.dao.UserCallRecordsDal;
import com.yqg.mongo.dao.UserContactsDal;
import com.yqg.mongo.dao.UserMessagesDal;
import com.yqg.mongo.entity.UserAppsMongo;
import com.yqg.mongo.entity.UserCallRecordsMongo;
import com.yqg.mongo.entity.UserContactsMongo;
import com.yqg.mongo.entity.UserMessagesMongo;
import com.yqg.service.order.request.UploadAppsRequest;
import com.yqg.service.order.request.UploadCallRecordsRequest;
import com.yqg.service.order.request.UploadContactRequest;
import com.yqg.service.order.request.UploadMsgsRequest;
import com.yqg.service.user.service.UsrContactService;
import com.yqg.user.dao.UsrContractInfoDao;
import com.yqg.user.entity.UsrContractInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Component
@Slf4j
public class UploadInfoService {


    @Autowired //
    private UserContactsDal userContactsDal;

    @Autowired
    private UserMessagesDal userMessagesDal;

    @Autowired
    private UserAppsDal userAppsDal;

    @Autowired
    private UserCallRecordsDal userCallRecordsDal;

    @Autowired
    private UsrContactService usrContactService;
    @Autowired
    private OrdDeviceInfoService ordDeviceInfoService;

    public void uploadContacts(UploadContactRequest uploadContactRequest) throws Exception{

        UserContactsMongo orderUserContactEntity=new UserContactsMongo();
        orderUserContactEntity.setUserUuid(uploadContactRequest.getUserUuid());
        orderUserContactEntity.setOrderNo(uploadContactRequest.getOrderNo());
        List<UserContactsMongo> orderUserContactes = this.userContactsDal.find(orderUserContactEntity);

        if(CollectionUtils.isEmpty(orderUserContactes)){
            UserContactsMongo contactsMongo = new UserContactsMongo();
            contactsMongo.setOrderNo(uploadContactRequest.getOrderNo());
            this.insertContactMongo(contactsMongo,uploadContactRequest.getUserUuid(),uploadContactRequest.getContactStr());
        }else {

            UserContactsMongo contactsMongo = orderUserContactes.get(0);
            contactsMongo.setUpdateTime(new Date());
            contactsMongo.setData(uploadContactRequest.getContactStr());
            this.userContactsDal.updateById(contactsMongo);
        }

//        UsrContractInfo search = new UsrContractInfo();
//        search.setOrderNo(uploadContactRequest.getOrderNo());
//        List<UsrContractInfo> scanList =  this.usrContractInfoDao.scan(search);
//        if (CollectionUtils.isEmpty(scanList)){
//            usrContactService.dealWithContacts(uploadContactRequest.getContactStr(),uploadContactRequest.getOrderNo());
//        }
    }


    public  void  insertContactMongo(UserContactsMongo contactsMongo,String userUuid,String contactStr){

        contactsMongo.setCreateTime(new Date());
        contactsMongo.setDisabled(0);
        contactsMongo.setUpdateTime(new Date());
        contactsMongo.setUuid(UUIDGenerateUtil.uuid());
        contactsMongo.setUserUuid(userUuid);
        contactsMongo.setData(contactStr);
        this.userContactsDal.insert(contactsMongo);
    }



    //   ??????
    public void uploadMsgs(UploadMsgsRequest uploadMsgsRequest){

        UserMessagesMongo message = new UserMessagesMongo();
        message.setUserUuid(uploadMsgsRequest.getUserUuid());
        message.setDisabled(0);
        message.setOrderNo(uploadMsgsRequest.getOrderNo());
        // ??
        List<UserMessagesMongo> scanList = userMessagesDal.find(message);
        if (CollectionUtils.isEmpty(scanList)){
            message.setData(uploadMsgsRequest.getMessageListStr());
            message.setUpdateTime(new Date());
            message.setCreateTime(new Date());
            message.setUuid(UUIDGenerateUtil.uuid());
            userMessagesDal.insert(message);
        }else {
            if(StringUtils.isEmpty(uploadMsgsRequest.getMessageListStr())
                    ||"[{\"type\":\"-1000\"}]".equals(uploadMsgsRequest.getMessageListStr())){
                return;
            }
            UserMessagesMongo messageMongo = scanList.get(0);
            messageMongo.setUpdateTime(new Date());
            messageMongo.setData(uploadMsgsRequest.getMessageListStr());
            userMessagesDal.updateById(messageMongo);
        }
    }

    //
    public void uploadApps(UploadAppsRequest uploadAppsRequest){

        UserAppsMongo app = new UserAppsMongo();
        app.setUserUuid(uploadAppsRequest.getUserUuid());
        app.setClientType(uploadAppsRequest.getClient_type());
        app.setDisabled(0);
        app.setOrderNo(uploadAppsRequest.getOrderNo());

        List<UserAppsMongo> scanAppList = this.userAppsDal.find(app);
        if (CollectionUtils.isEmpty(scanAppList)) {
            app.setUpdateTime(new Date());
            app.setCreateTime(new Date());
            app.setData(uploadAppsRequest.getAppsListStr());
            app.setUuid(UUIDGenerateUtil.uuid());
            app.setCreateTime(new Date());
            // ??
            this.userAppsDal.insert(app);
        }else {

            // ???mongo
            UserAppsMongo appMongo = scanAppList.get(0);
            appMongo.setUpdateTime(new Date());
            appMongo.setData(uploadAppsRequest.getAppsListStr());
            this.userAppsDal.updateById(appMongo);
        }
        //更新自定义设备指纹
        ordDeviceInfoService.updateCustomDeviceFingerprint(uploadAppsRequest.getOrderNo(), uploadAppsRequest.getUserUuid(),
                uploadAppsRequest.getAppsListStr());
    }

    //  ??????
    public void uploadCallRecords(UploadCallRecordsRequest uploadCallRecordsRequest){

        UserCallRecordsMongo calls = new UserCallRecordsMongo();
        calls.setUserUuid(uploadCallRecordsRequest.getUserUuid());
        calls.setDisabled(0);
        calls.setOrderNo(uploadCallRecordsRequest.getOrderNo());

        List<UserCallRecordsMongo> callsList = this.userCallRecordsDal.find(calls);


        if (CollectionUtils.isEmpty(callsList)) {
            calls.setUpdateTime(new Date());
            calls.setCreateTime(new Date());
            calls.setData(uploadCallRecordsRequest.getCallRecordsStr());
            calls.setUuid(UUIDGenerateUtil.uuid());
            calls.setCreateTime(new Date());
            // ??
            this.userCallRecordsDal.insert(calls);
        }else {
            if (StringUtils.isEmpty(uploadCallRecordsRequest.getCallRecordsStr())
                    || "[{\"type\":\"-1000\"}]".equals(uploadCallRecordsRequest.getCallRecordsStr())) {
                return;
            }
            UserCallRecordsMongo callsMongo = callsList.get(0);
            callsMongo.setUpdateTime(new Date());
            callsMongo.setData(uploadCallRecordsRequest.getCallRecordsStr());
            this.userCallRecordsDal.updateById(callsMongo);
        }
    }
}
