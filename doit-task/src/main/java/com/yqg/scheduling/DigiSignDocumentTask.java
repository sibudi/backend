package com.yqg.scheduling;

import java.util.Map;

import com.yqg.service.notification.request.NotificationRequest;
import com.yqg.service.notification.service.SlackNotificationService;
import com.yqg.service.signcontract.ContractSignService;
import lombok.extern.slf4j.Slf4j;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DigiSignDocumentTask {
    @Autowired
    private ContractSignService contractSignService;
    @Autowired
    private SlackNotificationService slackNotificationService;

    /***
     * 下载已经签约的文档
     * (每天凌晨开始下载减少系统负载)
     */
    @Scheduled(cron = "30 0/1 * * * ?")
    public void downloadSignPDF(){
        Long startTime = System.currentTimeMillis();
        try{
            log.info("downloadSignPDF start");
            Map<String,String> errorMap = contractSignService.downloadContract();
            if (!errorMap.isEmpty()) {
                this.sendSlackErrorNotification("DIGISIGN_DOWNLOAD_ERROR", new JSONObject(errorMap).toString());
            }
        }catch (Exception e){
            log.error("downloadSignPDF error",e);
            this.sendSlackErrorNotification("DIGISIGN_DOWNLOAD_ERROR", e.toString());
        }finally {
            log.info("downloadSignPDF end: {} ms",(System.currentTimeMillis()-startTime));
        }
    }

    /***
     * 检查用户签约状态，防止回调数据失败数据一直没有到后续的状态
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void checkOrderDigiSignStatus(){

        Long startTime = System.currentTimeMillis();
        try{
            log.info("checkOrderDigiSignStatus start");
            Map<String,String> errorMap = contractSignService.checkSignContractStatus();
            if (!errorMap.isEmpty()) {
                this.sendSlackErrorNotification("DIGISIGN_CHECK_STATUS_ERROR", new JSONObject(errorMap).toString());
            }
        }catch (Exception e){
            log.error("checkOrderDigiSignStatus error",e);
            this.sendSlackErrorNotification("DIGISIGN_CHECK_STATUS_ERROR", e.toString());
        }finally {
            log.info("checkOrderDigiSignStatus end: {} ms",(System.currentTimeMillis()-startTime));
        }
    }

    private void sendSlackErrorNotification (String subject, String errorMessage){
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setMessage(String.format("*%s* - %s", subject, errorMessage));  
        slackNotificationService.SendNotification(notificationRequest);
    }
}
