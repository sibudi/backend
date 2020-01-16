package com.yqg.scheduling.management;


import com.alibaba.fastjson.JSONObject;
import com.yqg.common.utils.HttpTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ManageMessageTask {

    @Value("${manager.path}")
    private String url;

    // 每隔半小时检查复审备注提醒消息,并发送
    // @Scheduled(cron = "0 */1 * * * ?")
    public void sendTeleReviewAlertMessage() {
        String managerUrl = url;
        managerUrl += "sendTeleReviewAlertMessage/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    //将数据App内的数据分析保存到Mysql
    // @Scheduled(cron = "0 45 23 * * ?")
    public void saveLoanData2Mysql() {
        String managerUrl = url;
        managerUrl += "saveLoanData2Mysql/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    // @Scheduled(cron = "0 0/12 * * * ?")
    public void saveAppData2Redis() {
        String managerUrl = url;
        managerUrl += "saveAppData2Redis/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    // @Scheduled(cron = "0 40 1 * * ?")
    public void updateOverDueDailyData() {
        String managerUrl = url;
        managerUrl += "updateOverDueDailyData/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    // @Scheduled(cron = "0 0/60 * * * ?")
    public void updateRepayDailyData() {
        String managerUrl = url;
        managerUrl += "updateRepayDailyData/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    // @Scheduled(cron = "0 45 14 23 7 ?")
    public void dataAPPInit() {
        String managerUrl = url;
        managerUrl += "dataAPPInit/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);

    }
}
