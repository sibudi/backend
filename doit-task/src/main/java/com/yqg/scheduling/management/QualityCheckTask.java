package com.yqg.scheduling.management;


import com.alibaba.fastjson.JSONObject;
import com.yqg.common.utils.HttpTools;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class QualityCheckTask {

    @Value("${manager.path}")
    private String url;



    /**
     * "本人电话,WA必须要同时都有记录
     若不满足，则增加一条备注质检记录，质检标签为：D0催收联系情况全部进行标亮；质检人员为：系统质检"
     */
    // @Scheduled(cron = "0 0 17 * * ?")
    public void qualityCheckD0() {
        String managerUrl = url;
        managerUrl += "qualityCheckD0/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    /**
     *"12：00前是否有添加过新记录；
     若不满足，则增加一条备注质检记录，质检标签为：D0案件及新增逾期案件12点之前未跟进问题；质检人员为：系统质检"
     */
    // @Scheduled(cron = "0 0 12 * * ?")
    public void qualityCheckBefore12() {
        String managerUrl = url;
        managerUrl += "qualityCheckBefore12/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    /**
     *"17：00前是否有添加过新记录；
     若不满足，则增加一条备注质检记录，质检标签为：一天以上未跟进订单 ；质检人员为：系统质检"
     */
    // @Scheduled(cron = "0 0 17 * * ?")
    public void qualityCheckBefore17() {
        String managerUrl = url;
        managerUrl += "qualityCheckBefore17/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    /**
     * "承诺还款时间距当前时间在30分钟以上，1个小时以下
     若不满足，则增加一条备注质检记录，质检标签为：承诺还款时间半小时之前/之后未跟进；质检人员为：系统质检"
     */
    // @Scheduled(cron = "0 35 9 * * ?")
    public void qualityCheckPromiseTime1() {
        String managerUrl = url;
        managerUrl += "qualityCheckPromiseTime/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    // @Scheduled(cron = "0 35 10 * * ?")
    public void qualityCheckPromiseTime2() {
        String managerUrl = url;
        managerUrl += "qualityCheckPromiseTime/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    // @Scheduled(cron = "0 35 11 * * ?")
    public void qualityCheckPromiseTime3() {
        String managerUrl = url;
        managerUrl += "qualityCheckPromiseTime/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    // @Scheduled(cron = "0 35 13 * * ?")
    public void qualityCheckPromiseTime4() {
        String managerUrl = url;
        managerUrl += "qualityCheckPromiseTime/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }


    // @Scheduled(cron = "0 35 14 * * ?")
    public void qualityCheckPromiseTime5() {
        String managerUrl = url;
        managerUrl += "qualityCheckPromiseTime/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    // @Scheduled(cron = "0 35 15 * * ?")
    public void qualityCheckPromiseTime6() {
        String managerUrl = url;
        managerUrl += "qualityCheckPromiseTime/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    // @Scheduled(cron = "0 35 16 * * ?")
    public void qualityCheckPromiseTime7() {
        String managerUrl = url;
        managerUrl += "qualityCheckPromiseTime/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

    // @Scheduled(cron = "0 35 17 * * ?")
    public void qualityCheckPromiseTime8() {
        String managerUrl = url;
        managerUrl += "qualityCheckPromiseTime/managerTask";
        JSONObject jsonObject = new JSONObject();
        HttpTools.post(managerUrl, null, jsonObject.toString(), 60000, 20000);
    }

}
