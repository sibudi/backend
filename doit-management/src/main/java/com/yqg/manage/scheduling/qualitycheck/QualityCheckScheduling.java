package com.yqg.manage.scheduling.qualitycheck;

import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.collection.QualityCheckService;
import com.yqg.common.utils.ServiceJudgeUtils;
import com.yqg.service.third.twilio.config.TwilioConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tonggen
 */
@RestController
@RequestMapping("/manage")
public class QualityCheckScheduling {
    private final Logger logger = LoggerFactory.getLogger(QualityCheckScheduling.class);

    @Autowired
    private QualityCheckService qualityCheckService;

    @Autowired
    private TwilioConfig config;

    /**
     * "本人电话,WA必须要同时都有记录
     若不满足，则增加一条备注质检记录，质检标签为：D0催收联系情况全部进行标亮；质检人员为：系统质检"
     */
    @RequestMapping(value = "/qualityCheckD0/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> qualityCheckD0() {
        logger.info("<=============>    qualityCheckD0 start     <=================>");
        qualityCheckService.qualityCheckD0();
        logger.info("<=============>    qualityCheckD0 end <=================> ");
        return ResponseEntitySpecBuilder.success();
    }


//    @Scheduled(cron = "0 0 17 * * ?")
//    public void qualityCheckD0() throws Exception{
//        logger.info("<=============>    qualityCheckD0 start     <=================>");
//        if (ServiceJudgeUtils.judgeRunOrNot(config.getAnOtherServerAddress())) {
//            qualityCheckService.qualityCheckD0();
//        }
//        logger.info("<=============>    qualityCheckD0 end <=================> ");
//    }

    /**
     *"12：00前是否有添加过新记录；
     若不满足，则增加一条备注质检记录，质检标签为：D0案件及新增逾期案件12点之前未跟进问题；质检人员为：系统质检"
     */
    @RequestMapping(value = "/qualityCheckBefore12/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> qualityCheckBefore12() {
        logger.info("<=============>    qualityCheckBefore12 start     <=================>");
        qualityCheckService.qualityCheckBefore12();
        logger.info("<=============>    qualityCheckBefore12 end <=================> ");
        return ResponseEntitySpecBuilder.success();
    }
//    @Scheduled(cron = "0 0 12 * * ?")
//    public void qualityCheckBefore12() throws Exception{
//        logger.info("<=============>    qualityCheckBefore12 start     <=================>");
//        if (ServiceJudgeUtils.judgeRunOrNot(config.getAnOtherServerAddress())) {
//            qualityCheckService.qualityCheckBefore12();
//        }
//        logger.info("<=============>    qualityCheckBefore12 end <=================> ");
//    }
    /**
     *"17：00前是否有添加过新记录；
     若不满足，则增加一条备注质检记录，质检标签为：一天以上未跟进订单 ；质检人员为：系统质检"
     */
    @RequestMapping(value = "/qualityCheckBefore17/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> qualityCheckBefore17() {
        logger.info("<=============>    qualityCheckBefore17 start     <=================>");
        qualityCheckService.qualityCheckBefore17();
        logger.info("<=============>    qualityCheckBefore17 end <=================> ");
        return ResponseEntitySpecBuilder.success();
    }
//    @Scheduled(cron = "0 0 17 * * ?")
//    public void qualityCheckBefore17() throws Exception{
//        logger.info("<=============>    qualityCheckBefore17 start     <=================>");
//        if (ServiceJudgeUtils.judgeRunOrNot(config.getAnOtherServerAddress())) {
//            qualityCheckService.qualityCheckBefore17();
//        }
//        logger.info("<=============>    qualityCheckBefore17 end <=================> ");
//    }

    /**
     * "承诺还款时间距当前时间在30分钟以上，1个小时以下
     若不满足，则增加一条备注质检记录，质检标签为：承诺还款时间半小时之前/之后未跟进；质检人员为：系统质检"
     */
    @RequestMapping(value = "/qualityCheckPromiseTime/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> qualityCheckPromiseTime() {
        logger.info("<=============>    qualityCheckPromiseTime start     <=================>");
        qualityCheckService.qualityCheckPromiseTime();
        logger.info("<=============>    qualityCheckPromiseTime end <=================> ");
        return ResponseEntitySpecBuilder.success();
    }
//    @Scheduled(cron = "0 35 9 * * ?")
//    public void qualityCheckPromiseTime1() throws Exception{
//        logger.info("<=============>    qualityCheckPromiseTime9 start     <=================>");
//        if (ServiceJudgeUtils.judgeRunOrNot(config.getAnOtherServerAddress())) {
//            qualityCheckService.qualityCheckPromiseTime();
//        }
//        logger.info("<=============>    qualityCheckPromiseTime9 end <=================> ");
//    }

//    /**
//     * "承诺还款时间距当前时间在30分钟以上，1个小时以下
//     若不满足，则增加一条备注质检记录，质检标签为：承诺还款时间半小时之前/之后未跟进；质检人员为：系统质检"
//     */
//    @Scheduled(cron = "0 35 10 * * ?")
//    public void qualityCheckPromiseTime2() throws Exception{
//        logger.info("<=============>    qualityCheckPromiseTime10 start     <=================>");
//        if (ServiceJudgeUtils.judgeRunOrNot(config.getAnOtherServerAddress())) {
//            qualityCheckService.qualityCheckPromiseTime();
//        }
//        logger.info("<=============>    qualityCheckPromiseTime10 end <=================> ");
//    }
//
//    /**
//     * "承诺还款时间距当前时间在30分钟以上，1个小时以下
//     若不满足，则增加一条备注质检记录，质检标签为：承诺还款时间半小时之前/之后未跟进；质检人员为：系统质检"
//     */
//    @Scheduled(cron = "0 35 11 * * ?")
//    public void qualityCheckPromiseTime3() throws Exception{
//        logger.info("<=============>    qualityCheckPromiseTime11 start     <=================>");
//        if (ServiceJudgeUtils.judgeRunOrNot(config.getAnOtherServerAddress())) {
//            qualityCheckService.qualityCheckPromiseTime();
//        }
//        logger.info("<=============>    qualityCheckPromiseTime11 end <=================> ");
//    }
//
//    /**
//     * "承诺还款时间距当前时间在30分钟以上，1个小时以下
//     若不满足，则增加一条备注质检记录，质检标签为：承诺还款时间半小时之前/之后未跟进；质检人员为：系统质检"
//     */
//    @Scheduled(cron = "0 35 13 * * ?")
//    public void qualityCheckPromiseTime4() throws Exception{
//        logger.info("<=============>    qualityCheckPromiseTime13 start     <=================>");
//        if (ServiceJudgeUtils.judgeRunOrNot(config.getAnOtherServerAddress())) {
//            qualityCheckService.qualityCheckPromiseTime();
//        }
//        logger.info("<=============>    qualityCheckPromiseTime13 end <=================> ");
//    }
//
//    /**
//     * "承诺还款时间距当前时间在30分钟以上，1个小时以下
//     若不满足，则增加一条备注质检记录，质检标签为：承诺还款时间半小时之前/之后未跟进；质检人员为：系统质检"
//     */
//    @Scheduled(cron = "0 35 14 * * ?")
//    public void qualityCheckPromiseTime5() throws Exception{
//        logger.info("<=============>    qualityCheckPromiseTime14 start     <=================>");
//        if (ServiceJudgeUtils.judgeRunOrNot(config.getAnOtherServerAddress())) {
//            qualityCheckService.qualityCheckPromiseTime();
//        }
//        logger.info("<=============>    qualityCheckPromiseTime14 end <=================> ");
//    }
//
//    /**
//     * "承诺还款时间距当前时间在30分钟以上，1个小时以下
//     若不满足，则增加一条备注质检记录，质检标签为：承诺还款时间半小时之前/之后未跟进；质检人员为：系统质检"
//     */
//    @Scheduled(cron = "0 35 15 * * ?")
//    public void qualityCheckPromiseTime6() throws Exception{
//        logger.info("<=============>    qualityCheckPromiseTime15 start     <=================>");
//        if (ServiceJudgeUtils.judgeRunOrNot(config.getAnOtherServerAddress())) {
//            qualityCheckService.qualityCheckPromiseTime();
//        }
//        logger.info("<=============>    qualityCheckPromiseTime15 end <=================> ");
//    }
//
//    /**
//     * "承诺还款时间距当前时间在30分钟以上，1个小时以下
//     若不满足，则增加一条备注质检记录，质检标签为：承诺还款时间半小时之前/之后未跟进；质检人员为：系统质检"
//     */
//    @Scheduled(cron = "0 35 16 * * ?")
//    public void qualityCheckPromiseTime7() throws Exception{
//        logger.info("<=============>    qualityCheckPromiseTime16 start     <=================>");
//        if (ServiceJudgeUtils.judgeRunOrNot(config.getAnOtherServerAddress())) {
//            qualityCheckService.qualityCheckPromiseTime();
//        }
//        logger.info("<=============>    qualityCheckPromiseTime16 end <=================> ");
//    }
//
//    /**
//     * "承诺还款时间距当前时间在30分钟以上，1个小时以下
//     若不满足，则增加一条备注质检记录，质检标签为：承诺还款时间半小时之前/之后未跟进；质检人员为：系统质检"
//     */
//    @Scheduled(cron = "0 35 17 * * ?")
//    public void qualityCheckPromiseTime8() throws Exception{
//        logger.info("<=============>    qualityCheckPromiseTime17 start     <=================>");
//        if (ServiceJudgeUtils.judgeRunOrNot(config.getAnOtherServerAddress())) {
//            qualityCheckService.qualityCheckPromiseTime();
//        }
//        logger.info("<=============>    qualityCheckPromiseTime17 end <=================> ");
//    }


}
