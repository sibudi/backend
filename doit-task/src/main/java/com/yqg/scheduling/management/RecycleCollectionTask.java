package com.yqg.scheduling.management;


import com.alibaba.fastjson.JSONObject;
import com.yqg.common.utils.HttpTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RecycleCollectionTask {

    @Value("${manager.path}")
    private String url;


    /**
     * 自动回收催收未还款订单(用户为id 为207，cuishouheimingdan的账号不回收）
     */
    //@Scheduled(cron = "0 12 0 * * ?")
    public void recycleCollectionOrder() {
        String managerUrl = url;
        managerUrl += "recycleCollectionOrder/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    //自动回收质检订单(用户为id 为207，cuishouheimingdan的账号不回收）
    //@Scheduled(cron = "0 10 0 * * ?")
    public void recycleQualityOrder() {
        String managerUrl = url;
        managerUrl += "recycleQualityOrder/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    //@Scheduled(cron = "0 10 0 * * ?")
    public void schedulingReceiverRestAndWork() {
        String managerUrl = url;
        managerUrl += "schedulingReceiverRestAndWork/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

//    @Scheduled(cron = "0 0 3 * * ?")
    public void systemAutoAssignment() {
        String managerUrl = url;
        managerUrl += "systemAutoAssignment/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

}
