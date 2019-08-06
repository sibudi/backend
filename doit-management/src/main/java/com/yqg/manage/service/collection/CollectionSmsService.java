package com.yqg.manage.service.collection;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.manage.dal.collection.CollectionSmsRecordDao;
import com.yqg.manage.dal.collection.SmsOneStepStatisticsDao;
import com.yqg.manage.dal.collection.SmsTemplateDao;
import com.yqg.manage.dal.user.ManUserDao;
import com.yqg.manage.entity.collection.CollectionSmsRecordEntity;
import com.yqg.manage.entity.collection.SmsOneStepStatisticsEntity;
import com.yqg.manage.entity.collection.SmsTemplateEntity;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.enums.CollectionSmsType;
import com.yqg.manage.enums.ContactModeEnum;
import com.yqg.manage.service.collection.request.ManCollectionRemarkRequest;
import com.yqg.manage.service.collection.request.SmsTemplateRequest;
import com.yqg.manage.service.collection.response.CollectSmsTemplateResponse;
import com.yqg.manage.service.collection.response.CollectionSmsRecordResponse;
import com.yqg.manage.service.mongo.OrderUserContactMongoService;
import com.yqg.manage.service.mongo.request.CollectSmsRequest;
import com.yqg.manage.service.mongo.request.OrderMongoRequest;
import com.yqg.manage.service.mongo.response.OrderUserCallRecordResponse;
import com.yqg.manage.util.PageDataUtils;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.system.request.DictionaryRequest;
import com.yqg.service.system.response.SysDicItemModel;
import com.yqg.service.system.service.SysDicService;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: caomiaoke
 * Date: 23/03/2018
 * Time: 1:46 PM
 */
@Service
@Slf4j
public class CollectionSmsService {


    private Logger logger = LoggerFactory.getLogger(CollectionSmsService.class);

    @Value("${HttpUrl.smsUrl}")
    private String smsUrl;

    @Value("${SmsChannelType.collection}")
    private String smsChannel;

    @Autowired
    private OrderUserContactMongoService orderUserContactMongoService;

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private SmsOneStepStatisticsDao smsOneStepStatisticsDao;

    @Autowired
    private SmsTemplateDao smsTemplateDao;

    @Autowired
    private OrdDao ordDao;

    @Autowired
    private UsrDao usrDao;

    @Autowired
    private ManUserDao manUserDao;

    @Autowired
    private CollectionSmsRecordDao collectionSmsRecordDao;

    @Autowired
    private SysDicService sysDicService;

    @Autowired
    private CollectionRemarkService collectionRemarkService;


    @Transactional
    public Boolean sendTopnCollectSms(CollectSmsRequest collectSmsRequest) throws Exception {

        CollectionSmsType collectionSmsType = collectSmsRequest.getCollectionSmsType();
        //判断当天发送短信是否达到上线
        if (!judgeSmsLimit(collectionSmsType, collectSmsRequest.getOrderNo(),
                collectSmsRequest.getCollectionLevel())) {
            throw new ServiceExceptionSpec(ExceptionEnum.SMS_COUNT_LIMIT);
        }
        OrdOrder ordOrder = getOrdOrder(collectSmsRequest);
        UsrUser usrUser = getUsrUser(collectSmsRequest);
        ManUser manUser = getManUser(collectSmsRequest);
        SmsTemplateEntity smsTemplateEntity = getSmsTemplate(collectSmsRequest);

        OrderMongoRequest orderMongoRequest = new OrderMongoRequest();
        orderMongoRequest.setOrderNo(collectSmsRequest.getOrderNo());
        orderMongoRequest.setUserUuid(collectSmsRequest.getUserUuid());

        if(ordOrder == null || usrUser == null || manUser == null || smsTemplateEntity == null || collectionSmsType == null ){
            return false;
        }


        List<OrderUserCallRecordResponse> orderUserCallRecordResponseAll = orderUserContactMongoService.orderUserCallRecordMongoByUuid(orderMongoRequest);
        List<OrderUserCallRecordResponse> orderUserCallRecordResponseList = orderUserContactMongoService.orderUserCallRecordWithFilter(orderUserCallRecordResponseAll);
//        List<OrderUserCallRecordResponse> orderUserCallRecordResponseList = orderUserCallRecordResponseAll;

//        todo insert table
        CollectionSmsRecordEntity collectionSmsRecordEntity = new CollectionSmsRecordEntity();
        collectionSmsRecordEntity.setOrderNo(ordOrder.getUuid());
        collectionSmsRecordEntity.setReceiverType(collectionSmsType);
        collectionSmsRecordEntity.setSender(manUser.getUsername());
        collectionSmsRecordEntity.setSendTime(new Date());
        collectionSmsRecordEntity.setSmsTitle(smsTemplateEntity.getSmsTitle());
        collectionSmsRecordEntity.setSmsContent(smsTemplateEntity.getSmsContent());
        collectionSmsRecordEntity.setUserUuid(usrUser.getUuid());

        collectionSmsRecordDao.insert(collectionSmsRecordEntity);


        //自动生成一条记录到manCollectionRemark表
//        ManCollectionRemarkRequest manCollectionRemarkRequest = new ManCollectionRemarkRequest();
//        manCollectionRemarkRequest.setUserUuid(usrUser.getUuid());
//        manCollectionRemarkRequest.setContactMode(ContactModeEnum.CONTACT_MODE_SMS.getType());
//        manCollectionRemarkRequest.setOrderNo(ordOrder.getUuid());
//        manCollectionRemarkRequest.setRemark("system send the message!");
//        collectionRemarkService.insertManCollRemark(manCollectionRemarkRequest);

        orderUserCallRecordResponseList.stream().limit(collectionSmsType.getNum()).forEach(item -> {

            SmsOneStepStatisticsEntity smsOneStepStatisticsEntity = getSmsOneStepStatisticsEntity(ordOrder,usrUser,manUser,smsTemplateEntity,item,collectionSmsType);
//            if(item.getMobile().startsWith("08")){
//                item.setMobile(item.getMobile().replaceFirst("0","62"));
//            }
//
//            if(item.getMobile().startsWith("+62")){
//                item.setMobile(item.getMobile().replaceFirst("\\+",""));
//                log.info("the number: {}",item.getMobile());
//            }
            Request request = buildRequest(smsChannel, item.getMobile(),smsTemplateEntity.getSmsContent());

            try {
                Response response = sendSms(request);
                if(response.isSuccessful()){
                    smsOneStepStatisticsEntity.setIsArrived(1);
                    log.info("Send sms succeed", smsOneStepStatisticsEntity);
                }else {
                    smsOneStepStatisticsEntity.setIsArrived(2);
                    log.info("Send sms failed,{}", smsOneStepStatisticsEntity);
                }
            } catch (IOException e) {
                smsOneStepStatisticsEntity.setIsArrived(2);
                log.info("Send sms failed,{}",smsOneStepStatisticsEntity);
//                e.printStackTrace();
            }
            smsOneStepStatisticsDao.insert(smsOneStepStatisticsEntity);
        });
        return true;

    }

    /**
     * 判断当天发送短信是否达到上限（D0只能发送10次）
     * @param collectionSmsType
     * @param orderNo
     */
    private Boolean judgeSmsLimit(CollectionSmsType collectionSmsType, String orderNo
            , String collectionLevel) throws Exception{
        if (collectionSmsType == null || StringUtils.isEmpty(orderNo)) {
            return false;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String nowTime = df.format(new Date());
        Integer count = smsOneStepStatisticsDao.collectSmsTempListCount(nowTime, orderNo);
        Integer maxCount = 0;
        //先判断是否是D0
        if ("D0".equals(collectionLevel)) {
            maxCount = 10;
        } else {
            DictionaryRequest dictionaryRequest = new DictionaryRequest();
            dictionaryRequest.setDicCode("MAX_SMS_COUNT");
            try {
                List<SysDicItemModel> dicItems = sysDicService.dicItemListByDicCode(dictionaryRequest);
                if (!CollectionUtils.isEmpty(dicItems)) {
                    maxCount = Integer.valueOf(dicItems.get(0).getDicItemValue());
                }
            } catch (Exception e) {
                throw new ServiceExceptionSpec(ExceptionEnum.ORDER_PRODUCT_CONFIG_IS_NULL);
            }
        }

        if (count + collectionSmsType.getNum() > maxCount) {

            logger.info("maxCount:" + maxCount + ", nowCount : " + (count + collectionSmsType.getNum()));
            return false;
        }
        return true;
    }


    public List<CollectionSmsRecordResponse> getCollectionSmsRecord(CollectSmsRequest request){
        List<CollectionSmsRecordResponse> getCollectionSmsRecord = collectionSmsRecordDao.getCollectionSmsRecordListByUserUuid(request.getOrderNo());
        return getCollectionSmsRecord;
    }



    private SmsOneStepStatisticsEntity getSmsOneStepStatisticsEntity(OrdOrder ordOrder,
                                                                     UsrUser usrUser,
                                                                     ManUser manUser,
                                                                     SmsTemplateEntity smsTemplateEntity,
                                                                     OrderUserCallRecordResponse orderUserCallRecordResponse,
                                                                     CollectionSmsType collectionSmsType){
        SmsOneStepStatisticsEntity smsOneStepStatisticsEntity = new SmsOneStepStatisticsEntity();
        smsOneStepStatisticsEntity.setOrderNo(ordOrder.getUuid());
        smsOneStepStatisticsEntity.setUserUuid(ordOrder.getUserUuid());
        smsOneStepStatisticsEntity.setSender(manUser.getUsername());
        smsOneStepStatisticsEntity.setSendTime(new Date());
        smsOneStepStatisticsEntity.setLoaner(usrUser.getRealName());
        smsOneStepStatisticsEntity.setReceiverType(collectionSmsType);
        smsOneStepStatisticsEntity.setReceiver(orderUserCallRecordResponse.getMobile());
        smsOneStepStatisticsEntity.setSmsTitle(smsTemplateEntity.getSmsTitle());
        smsOneStepStatisticsEntity.setSmsContent(smsTemplateEntity.getSmsContent());

        return smsOneStepStatisticsEntity;
    }


    private Request buildRequest(String smsChannel, String sendTo, String content){
        RequestBody requestBody = new FormBody.Builder()
                .add("smsChannel",smsChannel)
                .add("productType","PAYDAYLOAN")
                .add("sendFrom","Do-it")
                .add("sendTo",sendTo)
                .add("smsTrigger","Collection")
                .add("content",content)
                .build();

        Request request = new Request.Builder()
                .url(smsUrl)
                .addHeader("X-AUTH-TOKEN","Do-it")
                .post(requestBody)
                .build();
        return request;
    }

    private Response sendSms(Request request) throws IOException {

        return  okHttpClient.newCall(request).execute();
    }



    private OrdOrder getOrdOrder(CollectSmsRequest collectSmsRequest){

        OrdOrder order = new OrdOrder();
        order.setDisabled(0);
        order.setUuid(collectSmsRequest.getOrderNo());
//        order.setUserUuid(collectSmsRequest.getUserUuid());
        List<OrdOrder> scanList = this.ordDao.scan(order);

        if (CollectionUtils.isEmpty(scanList)){
            log.info("Do not find the order when send the collection sms, orderNo: {}", collectSmsRequest.getOrderNo());
            return null;
        }
        return scanList.get(0);
    }

    private UsrUser getUsrUser(CollectSmsRequest collectSmsRequest){
        UsrUser usrUser = new UsrUser();
        usrUser.setDisabled(0);
        usrUser.setUuid(collectSmsRequest.getUserUuid());
        List<UsrUser> scanList = this.usrDao.scan(usrUser);

        if (CollectionUtils.isEmpty(scanList)){
            log.info("Do not find the user when send the collection sms, orderNo: {}", collectSmsRequest.getOrderNo());
            return null;
        }
        return scanList.get(0);
    }


    private ManUser getManUser(CollectSmsRequest collectSmsRequest){
        ManUser manUser = new ManUser();
        manUser.setDisabled(0);
        manUser.setUuid(collectSmsRequest.getManUuid());
        List<ManUser> scanList = this.manUserDao.scan(manUser);

        if (CollectionUtils.isEmpty(scanList)){
            log.info("Do not find the reviewer when send the collection sms, orderNo: {}", collectSmsRequest.getOrderNo());
            return null;
        }

        return scanList.get(0);
    }

    private SmsTemplateEntity getSmsTemplate(CollectSmsRequest collectSmsRequest){
        SmsTemplateEntity smsTemplateEntity = new SmsTemplateEntity();
        smsTemplateEntity.setDisabled(0);
        smsTemplateEntity.setSmsTemplateId(collectSmsRequest.getSmsTemplateId());
        List<SmsTemplateEntity> scanList = this.smsTemplateDao.scan(smsTemplateEntity);
        if(CollectionUtils.isEmpty(scanList)){
            log.info("Do not find the templates when send the collection sms, orderNo: {}", collectSmsRequest.getOrderNo());
            return null;
        }

        return scanList.get(0);
    }


    /**
     * 分页获得短信列表
     * @param param
     * @return
     * @throws Exception
     */
    public PageData<List<CollectSmsTemplateResponse>> collectSmsTemplateList(SmsTemplateRequest param)
            throws Exception {

        PageHelper.startPage(param.getPageNo(), param.getPageSize());

        List<CollectSmsTemplateResponse> rList = smsOneStepStatisticsDao.collectSmsTempList(param);

        PageInfo pageInfo = new PageInfo(rList);

        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }

}
