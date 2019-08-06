package com.yqg.manage.scheduling.check;

import com.yqg.manage.entity.system.ManAlertMessage;
import com.yqg.manage.scheduling.check.response.WiselyResponse;
import com.yqg.manage.service.system.ManAlertMessageService;
import com.yqg.manage.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author alan
 */
@RestController
public class ManageMessageSend {
    @Autowired
    private ManAlertMessageService manAlertMessageService;

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * 提醒催收和电核
     * type 1 :电核，2 催收
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public List<WiselyResponse> sendCollectionAlertMessage(Integer type) throws Exception {
        Calendar time = Calendar.getInstance();
        String startTime = "";
        String endTime = "";
        //催收一个小时，复审一分钟跑一次（所以先将催收的注释掉）

        List<ManAlertMessage> result = null;
//        if (type == ManOrderRemarkTypeEnum.COLLECTION_ORDERTAG.getType()) {
//            startTime = DateUtils.getCurrentTimeToHour()+":00:00";
//            endTime  = com.yqg.common.utils.DateUtils.dateToDay()+ " " + (time.get(Calendar.HOUR_OF_DAY) + 1) +":00:00";
//            result = this.manAlertMessageService.getCollectionMessageListByTime(startTime,endTime);
//        } else if (type == ManOrderRemarkTypeEnum.TELE_REVIEW.getType()) {
        startTime = DateUtils.getCurrentTimeToMinute() +":00";
        result = manAlertMessageService.getCollectionMessageListByTime(startTime);
//        }

        if(result.size() <= 0){
            return null;
        }

        List<WiselyResponse> message = new ArrayList<>();
        for(ManAlertMessage item:result){
            WiselyResponse temp = new WiselyResponse();
            temp.setMessage(item.getMessage());
            temp.setUrl(item.getUrl());
            temp.setUserId(item.getUserId());
            message.add(temp);
        }

        template.convertAndSend("/topic/collectionMessage",message);
        return message;

    }

}
