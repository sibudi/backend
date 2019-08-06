package com.yqg.manage.service.third;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.twilio.Twilio;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.manage.dal.collection.ManCollectionRemarkDao;
import com.yqg.manage.dal.order.ManOrderOrderDao;
import com.yqg.manage.dal.third.ThirdTwilioDao;
import com.yqg.manage.entity.collection.ManCollectionRemark;
import com.yqg.manage.service.third.request.ThirdTwilioRequest;
import com.yqg.manage.service.third.request.TwilioWhatsAppRequest;
import com.yqg.manage.service.third.response.ThirdTwilioResponse;
import com.yqg.manage.service.third.response.TwilioWhatsAppRecordResponse;
import com.yqg.service.util.LoginSysUserInfoHolder;
import com.yqg.manage.util.PageDataUtils;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.third.twilio.config.TwilioConfig;
import com.yqg.system.dao.TwilioWhatsAppRecordDao;
import com.yqg.system.entity.TwilioWhatsAppRecord;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.entity.UsrUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Author: tonggen
 * Date: 2018/10/19
 * time: 上午10:52
 */
@Component
public class ThirdTwilioService {

    private Logger logger = LoggerFactory.getLogger(ThirdTwilioService.class);

    @Autowired
    private ThirdTwilioDao thirdTwilioDao;

    @Autowired
    private TwilioWhatsAppRecordDao twilioWhatsAppRecordDao;

    @Autowired
    private TwilioConfig config;

    @Autowired
    private ManCollectionRemarkDao manCollectionRemarkDao;

    @Autowired
    private ManOrderOrderDao manOrderOrderDao;

    @Autowired
    private UsrDao usrDao;
    /**
     * 分页获取twilio外呼列表
     * @param request
     * @return
     */
    public PageData listTwilioCallResult(ThirdTwilioRequest request) {

        PageHelper.startPage(request.getPageNo(), request.getPageSize());

        if (!StringUtils.isEmpty(request.getCallPhase())) {
            request.setCallPhase(request.getCallPhase().replaceAll("预催收人群", "")
                    .replaceAll(" ", "").replaceAll("D-0", "D0"));
        }
        List<ThirdTwilioResponse> lists = thirdTwilioDao.listTwilioCallResult(request);

        PageInfo pageInfo = new PageInfo(lists);

        if (CollectionUtils.isEmpty(lists)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }
        //循环进行二次封装
        lists.stream().forEach(elem -> {
            elem.setCallPhase(elem.getCallPhase() + "预催收人群");
            elem.setChannel("Twilio");
            //通过批次号查询没有接通的次数
            Integer noResponseCount = thirdTwilioDao.getNoResponseCount(elem.getBatchNo());
            elem.setPassRate(this.getRate(noResponseCount,elem.getSendCount()));
        });
        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }

    private String getRate(Integer count1, Integer count2) {

        BigDecimal number1 = new BigDecimal(count1);
        BigDecimal number2 = new BigDecimal(count2);
        return number1.divide(number2,2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).intValue() + "%";
    }

    /**
     * 通过批次号查询批次详情
     * @param request
     */
    public PageData getTwilioCallResultDetail(ThirdTwilioRequest request) throws ServiceExceptionSpec {

        PageHelper.startPage(request.getPageNo(), request.getPageSize());
        if (StringUtils.isEmpty(request.getBatchNo())) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        List<ThirdTwilioResponse> rList = thirdTwilioDao.getTwilioCallResultDetail(request.getBatchNo());
        PageInfo pageInfo = new PageInfo(rList);

        if (CollectionUtils.isEmpty(rList)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }
        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }

    /**
     * 获得用户回复并且保存记录
     * @param request
     */
    public void getTwilioWaResponses(TwilioWhatsAppRequest request) throws ServiceExceptionSpec {
        if (StringUtils.isEmpty(request.getMessageSid()) || StringUtils.isEmpty(request.getFrom())
                || StringUtils.isEmpty(request.getBody())) {
            logger.error("twilio save userResponse , data is error");
            return ;
        }
        //获取批次号
        List<TwilioWhatsAppRecord> lists = twilioWhatsAppRecordDao.getBatchNoByPhone(request.getFrom());
        if (CollectionUtils.isEmpty(lists) || StringUtils.isEmpty(lists.get(0).getBatchNo())) {
            logger.error("userPhone is {}, not find batchNo", request.getFrom());
            return ;
        }
        //查询用户回复两次，设置发送文案
        TwilioWhatsAppRecord twilioWhatsAppRecord = new TwilioWhatsAppRecord();
        twilioWhatsAppRecord.setDisabled(0);
        twilioWhatsAppRecord.setUserPhone(request.getFrom());
        twilioWhatsAppRecord.setDirection(2);
        twilioWhatsAppRecord.setBatchNo(lists.get(0).getBatchNo());
        twilioWhatsAppRecord.set_orderBy(" createTime desc ");
        List<TwilioWhatsAppRecord> records = twilioWhatsAppRecordDao.scan(twilioWhatsAppRecord);
        if (!CollectionUtils.isEmpty(records) && records.size() > 1) {

            String accountSid = config.getAccountSid();
            String authToken = config.getAuthToken();
            Twilio.init(accountSid, authToken);
            //开始发送whatsApp
            com.twilio.rest.api.v2010.account.Message message = com.twilio.rest.api.v2010.account.Message.creator(
                    new com.twilio.type.PhoneNumber(request.getFrom()),
                    new com.twilio.type.PhoneNumber(request.getTo()),
                    "please contact kefu")
                    .create();

            //保存一条外呼记录(先查询是否已经有了，可能回调函数先执行）
            twilioWhatsAppRecord.setSid(message.getSid());
            twilioWhatsAppRecord.setDirection(1);
            twilioWhatsAppRecord.setReplyContent("please contact kefu");
            twilioWhatsAppRecord.setPhoneNum(request.getFrom().replace("whatsapp:", ""));
            twilioWhatsAppRecord.setTwilioPhone(request.getTo());
            twilioWhatsAppRecord.setOrderNo(lists.get(0).getOrderNo());
            twilioWhatsAppRecord.setUserUuid(lists.get(0).getUserUuid());
            List<TwilioWhatsAppRecord> recordList = twilioWhatsAppRecordDao.listRecordBySid(message.getSid());
            if (CollectionUtils.isEmpty(recordList)) {
                try {
                    twilioWhatsAppRecordDao.insert(twilioWhatsAppRecord);
                } catch (Exception e) {
                    logger.info("orderNo is {}, can't insert data", twilioWhatsAppRecord.getOrderNo());
                }
            } else {
                twilioWhatsAppRecord.setId(recordList.get(0).getId());
                twilioWhatsAppRecord.setUuid(recordList.get(0).getUuid());
                twilioWhatsAppRecordDao.update(twilioWhatsAppRecord);
            }
        }
        //保存一条记录，状态肯定都是成功的
        TwilioWhatsAppRecord record = new TwilioWhatsAppRecord();
        record.setBatchNo(lists.get(0).getBatchNo());
        record.setSid(request.getMessageSid());
        record.setPhoneNum(request.getFrom().replace("whatsapp:", ""));
//        record.setStatus("delivered");
        record.setDirection(2);
        record.setUserPhone(request.getFrom());
        record.setTwilioPhone(request.getTo());
        record.setReplyContent(request.getBody());
        record.setOrderNo(lists.get(0).getOrderNo());
        record.setUserUuid(lists.get(0).getUserUuid());
        try {
            twilioWhatsAppRecordDao.insert(record);
        } catch (Exception e) {
            logger.info("save user response is error , the tonumber is " + request.getFrom() + e.getMessage());
        }



    }

    /**
     * twilio WA回复（状态变化时，twilio会进行回调)
     * @param request
     */
    public String twilioWhatsAppResXML(TwilioWhatsAppRequest request) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(request.getMessageSid()) || StringUtils.isEmpty(request.getTo())
                || StringUtils.isEmpty(request.getMessageStatus())) {
            logger.error("twilio update Stauts , data is error");
            return "";
        }

        //获得最近一次的batchNo
//        List<TwilioWhatsAppRecord> lists = twilioWhatsAppRecordDao.getBatchNoByPhone(request.getTo());
        //如果没有查询到batchNo, 则返回
//        if (CollectionUtils.isEmpty(lists) || StringUtils.isEmpty(lists.get(0).getBatchNo())) {
//            logger.info("not find batchNo userPhone : " + request.getTo());
//            return "";
//        }
        //通过sid进行更新，若没有则插入一条
        twilioWhatsAppRecordDao.updateWaStautsBySid(request.getMessageSid(), request.getMessageStatus(),
                request.getTo());
        //没有找到，则进行插入
//        if (updateCount == 0) {
//            TwilioWhatsAppRecord record = new TwilioWhatsAppRecord();
//            record.setSid(request.getMessageSid());
//            record.setStatus(request.getMessageStatus());
//            record.setUserPhone(request.getTo());
//            record.setTwilioPhone(request.getFrom());
//            record.setPhoneNum(request.getTo().replace("whatsapp:", ""));
//            record.setDirection(1);
//            record.setBatchNo(lists.get(0).getBatchNo());
//            twilioWhatsAppRecordDao.insert(record);
//        }

        return request.toString();
    }

    /** twilio whatsapp 列表查询
     *
     */
    public PageData listTwilioWaRecord(TwilioWhatsAppRequest request) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(request.getReplyContent())) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        PageHelper.startPage(request.getPageNo(), request.getPageSize());
        List<TwilioWhatsAppRecordResponse> responses =
                thirdTwilioDao.listTwilioWaRecord(request);

        PageInfo pageInfo = new PageInfo(responses);
        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }

    /** twilio whatsapp 列表查询
     *
     */
    public Integer tempUpdateOrderRole(String orderNo) throws ServiceExceptionSpec {

        OrdOrder ordOrder = new OrdOrder();
        ordOrder.setDisabled(0);
        ordOrder.setUuid(orderNo);
        List<OrdOrder> lists = manOrderOrderDao.scan(ordOrder);
        if (CollectionUtils.isEmpty(lists)) {
            return 0;
        }
        UsrUser usrUser = new UsrUser();
        usrUser.setDisabled(0);
        usrUser.setUuid(lists.get(0).getUserUuid());
        List<UsrUser> usrUsers = usrDao.scan(usrUser);
        if (CollectionUtils.isEmpty(usrUsers)) {
            return 0;
        }
        usrUser = usrUsers.get(0);
        if (usrUser.getUserRole().equals(3)) {
            usrUser.setUserRole(2);
            return usrDao.update(usrUser);
        }
        return 0;
    }

    /**
     * twilio whatsapp 新增或者修改解决方法
     * @param request
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertOrUpdateTwilioWaRecord(TwilioWhatsAppRequest request) throws ServiceExceptionSpec {

        if (request.getId() == null || request.getId().equals(0)) {
            throw  new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        //通过twilio wa的ID更新数据
        TwilioWhatsAppRecord record = new TwilioWhatsAppRecord();
        record.setId(request.getId());
        List<TwilioWhatsAppRecord> records = twilioWhatsAppRecordDao.scan(record);
        if (CollectionUtils.isEmpty(records)) {
            throw  new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_ITEM_ERROR);
        }
        record = records.get(0);

        record.setCreateUser(LoginSysUserInfoHolder.getLoginSysUserId());
        record.setSolveType(request.getSolveType());
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date promiseDate = null;
        try {
            promiseDate = sf.parse(request.getPromiseTimeStart());
            record.setPromiseTime(promiseDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        record.setRemark(request.getRemark());
        twilioWhatsAppRecordDao.update(record);

        //生成一条记录到催收
        ManCollectionRemark remark = new ManCollectionRemark();
        remark.setOrderNo(record.getOrderNo());
        remark.setUserUuid(record.getUserUuid());
        remark.setPromiseRepaymentTime(promiseDate);
        remark.setRemark(request.getRemark());
        manCollectionRemarkDao.insert(remark);

        return true;
    }
}
