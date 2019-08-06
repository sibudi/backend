package com.yqg.manage.scheduling.collection;

import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.check.ManReceiverSchedulingService;
import com.yqg.manage.service.collection.CollectionAutoAssignmentService;
import com.yqg.common.utils.ServiceJudgeUtils;
import com.yqg.service.third.twilio.config.TwilioConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Jacob
 * ??????
 */
@RestController
@RequestMapping("/manage")
public class CollectionManageScheduling {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CollectionAutoAssignmentService collectionAutoAssignmentService;

    @Autowired
    private ManReceiverSchedulingService manReceiverSchedulingService;



    @Autowired
    private TwilioConfig config;

    //自动回收催收未还款订单(用户为id 为207，cuishouheimingdan的账号不回收）
//    @Scheduled(cron = "0 10 0 * * ?")
//    public void recycleCollectionOrder() throws Exception {
//
//        if (ServiceJudgeUtils.judgeRunOrNot(config.getAnOtherServerAddress())) {
//            logger.info("======自动回收催收未还款订单======");
//            collectionAutoAssignmentService.recycleCollectionOrder();
//            logger.info("自动回收催收未还款订单 end");
//        } else {
//            logger.info("recycleColletionOrder's ip is error.");
//        }
//    }
//
    @RequestMapping(value = "/recycleCollectionOrder/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> recycleCollectionOrder() {
        logger.info("======recycleCollectionOrder start======");
        collectionAutoAssignmentService.recycleCollectionOrder();
        logger.info("recycleCollectionOrder end");
        return ResponseEntitySpecBuilder.success();
    }

    @RequestMapping(value = "/recycleQualityOrder/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> recycleQualityOrder() {
        logger.info("recycleQualityOrder start============");
        collectionAutoAssignmentService.recycleQualityOrder();
        logger.info(" ===== recycleQualityOrder end=== ");
        return ResponseEntitySpecBuilder.success();
    }

    @RequestMapping(value = "/schedulingReceiverRestAndWork/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> schedulingReceiverRestAndWork() {
        logger.info("schedulingReceiverRestAndWork start============");
        manReceiverSchedulingService.schedulingReceiverRestAndWork();
        logger.info(" ===== schedulingReceiverRestAndWork end=== ");
        return ResponseEntitySpecBuilder.success();
    }

    @RequestMapping(value = "/systemAutoAssignment/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> systemAutoAssignment() {
        logger.info("systemAutoAssignment start============");
        collectionAutoAssignmentService.systemAutoAssignment();
        logger.info(" ===== systemAutoAssignment end=== ");
        return ResponseEntitySpecBuilder.success();
    }





    //自动回收质检订单(用户为id 为207，cuishouheimingdan的账号不回收）
//    @Scheduled(cron = "0 10 0 * * ?")
//    public void recycleQualityOrder() throws Exception {
//
//        if (ServiceJudgeUtils.judgeRunOrNot(config.getServerAddress())) {
//            logger.info("============");
//            collectionAutoAssignmentService.recycleQualityOrder();
//            logger.info(" end");
//        } else {
//            logger.info("recycleQualityOrder's ip is error.");
//        }
//    }

}
