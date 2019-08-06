package com.yqg.manage.service.system;

import com.yqg.manage.dal.system.ManAlertMessageDao;
import com.yqg.manage.entity.system.ManAlertMessage;
import com.yqg.manage.service.order.request.ManAlertMessageRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Jacob
 */
@Component
public class ManAlertMessageService {

    private Logger logger = LoggerFactory.getLogger(ManAlertMessageService.class);
    @Autowired
    private ManAlertMessageDao manAlertMessageDao;

    public List<ManAlertMessage> getCollectionMessageListByTime(String startTime, String endTime) {
        return this.manAlertMessageDao.getCollectionMessageListByTime(startTime,endTime);
    }

    /**
     * 查出当前分钟的提醒
     * @return
     */
    public List<ManAlertMessage> getCollectionMessageListByTime(String alertTime) {
        return this.manAlertMessageDao.getCollectionMessageListByMinute(alertTime);
    }

    /**
     * 添加一条电话审核提醒消息*/
    public void addTeleReviewAlertMessage(ManAlertMessageRequest manAlertMessageRequest) {
        ManAlertMessage addInfo = new ManAlertMessage();
        if(manAlertMessageRequest.getOperateId() == null
                || manAlertMessageRequest.getAlertTime() == null
                || StringUtils.isEmpty(manAlertMessageRequest.getRealName())
                || StringUtils.isEmpty(manAlertMessageRequest.getMobileNumber())){
            logger.error("添加电话提醒失败! realName:{}, mobileNumber:{}", manAlertMessageRequest.getRealName()
                    , manAlertMessageRequest.getMobileNumber());
            return;
        }
        addInfo.setUserId(manAlertMessageRequest.getOperateId());
        addInfo.setAlertTime(manAlertMessageRequest.getAlertTime());
        addInfo.setDisabled(0);

        //电话号码中间四位掩码
        String mobile = manAlertMessageRequest.getMobileNumber();
        int length = mobile.length();
        StringBuffer sf = new StringBuffer();
        for (int index = 0; index < length -7; index++) {
            sf = sf.append("*");
        }
        if (StringUtils.isNotBlank(mobile)) {
            mobile = mobile.substring(0,3) + sf.toString() + mobile.substring(length-4);
        }
        String message = "";
        String url = "";
        if (manAlertMessageRequest.getLangue() == 1) {
            message = "提醒:用户 "+ manAlertMessageRequest.getRealName() + " "+ mobile +"的电话审核时间已到";
            url = "#/orderSecondReview?seen=true&userUuid="+manAlertMessageRequest.getUserUuid()+"&uuid="+manAlertMessageRequest.getOrderNo();
        } else if (manAlertMessageRequest.getLangue() == 2) {
            message = "Waktu verifikasi via telepon akun " + manAlertMessageRequest.getRealName() + " " + mobile + " telah tiba";
            url = "#/orderSecondReviewInn?seen=true&userUuid="+manAlertMessageRequest.getUserUuid()+"&uuid="+manAlertMessageRequest.getOrderNo();
        }
        addInfo.setMessage(message);
        addInfo.setUrl(url);
        this.manAlertMessageDao.insert(addInfo);
    }

    /**
     * 添加一条催收打标签提醒消息
     * */
    public void addCollectionAlertMessage(ManAlertMessageRequest manAlertMessageRequest) {
        ManAlertMessage addInfo = new ManAlertMessage();
        if(manAlertMessageRequest.getOperateId() == null
                || manAlertMessageRequest.getAlertTime() == null
                || StringUtils.isEmpty(manAlertMessageRequest.getRealName())
                || StringUtils.isEmpty(manAlertMessageRequest.getMobileNumber())
                || manAlertMessageRequest.getOrderTag() == null){
            logger.error("添加催收提醒失败! realName:{}, mobileNumber:{}", manAlertMessageRequest.getRealName()
                    , manAlertMessageRequest.getMobileNumber());
            return;
        }
        addInfo.setUserId(manAlertMessageRequest.getOperateId());
        addInfo.setAlertTime(manAlertMessageRequest.getAlertTime());
        addInfo.setDisabled(0);

        //电话号码中间四位掩码
        String mobile = manAlertMessageRequest.getMobileNumber();
        int length = mobile.length();
        StringBuffer sf = new StringBuffer();
        for (int index = 0; index < length -7; index++) {
            sf = sf.append("*");
        }
        mobile = mobile.substring(0,3) + sf.toString() + mobile.substring(length-4);
        String message = "";
        String url = "";
        if (manAlertMessageRequest.getLangue() == 1) {
            message = "提醒:用户 "+ manAlertMessageRequest.getRealName() + " "+ mobile +"的催收提醒时间已到";
            url = "#/CollectionOrderDetails?seen=true&userUuid="+manAlertMessageRequest.getUserUuid()+"&uuid="+manAlertMessageRequest.getOrderNo();
        } else if (manAlertMessageRequest.getLangue() == 2) {
            message = "Waktu penagihan untuk akun " + manAlertMessageRequest.getRealName() + mobile + " telah tiba.";
            url = "#/CollectionOrderDetailsInn?seen=true&userUuid="+manAlertMessageRequest.getUserUuid()+"&uuid="+manAlertMessageRequest.getOrderNo();
        }
        addInfo.setMessage(message);
        addInfo.setUrl(url);
        this.manAlertMessageDao.insert(addInfo);
    }
}
