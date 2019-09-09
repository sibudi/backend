package com.yqg.manage.scheduling.twilioCall;

import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.service.third.Inforbip.InforbipCollectionService;
import com.yqg.service.third.twilio.TwilioService;
import com.yqg.service.third.twilio.config.TwilioConfig;
import com.yqg.service.third.twilio.request.TwilioCallResultRequest;
import com.yqg.system.entity.TwilioCallResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutorService;

/**
 * Author: tonggen
 * Date: 2018/10/16
 * time: 下午12:27
 */
@RestController
@RequestMapping("/manage")
public class TwilioCallScheduling {

    private Logger logger = LoggerFactory.getLogger(TwilioCallScheduling.class);

    @Autowired
    private TwilioService twilioService;
    @Autowired
    private InforbipCollectionService inforbipCollectionService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private TwilioConfig config;

    @RequestMapping(value = "/call8D_1/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> call8D_1() {
        logger.info("<=============>    早上8点发送D-1全部 start     <=================>");
        //放入线程池中处理(处理时间较长，避免用户页面一直等待)
        executorService
                .submit(() -> {
                    TwilioCallResultRequest request = new TwilioCallResultRequest();
                    request.setCallPhase("D-1");
                    request.setCallPhaseType(TwilioCallResult.CallPhaseTypeEnum.CALL_PHASE_ALL.getCode());
//                    request.setCallUrl(config.getUrl() + "D-1/twilioXml");
                    request.setCallUrl("http://h5.do-it.id/twilio/D-1.mp3");
                    request.setDays(-1);
                    request.setBatchNo(String.valueOf(System.currentTimeMillis() + "D-1"));
//                    twilioService.startCallTwilio(request);
                    try {
                        inforbipCollectionService.startCallInfobip(request);
                    } catch (Exception e) {
                        logger.error("send voice is error, {}", e);
                    }
                    logger.info("<=============>    早上8点发送D-1全部 end<=================> ");
                });
        return ResponseEntitySpecBuilder.success();
    }


    @RequestMapping(value = "/call8D_2/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> call8D_2() {
        logger.info("<=============>    早上8点发送D-2全部 start     <=================>");
        executorService.submit(() -> {
            TwilioCallResultRequest request = new TwilioCallResultRequest();
            request.setCallPhase("D-2");
            request.setCallPhaseType(TwilioCallResult.CallPhaseTypeEnum.CALL_PHASE_ALL.getCode());
//            request.setCallUrl(config.getUrl() + "D-2/twilioXml");
            request.setCallUrl("http://h5.do-it.id/twilio/D-2.mp3");
            request.setDays(-2);
            request.setBatchNo(String.valueOf(System.currentTimeMillis() + "D-2"));
//            twilioService.startCallTwilio(request);
            try {
                inforbipCollectionService.startCallInfobip(request);
            } catch (Exception e) {
                logger.error("send voice is error, {}", e);
            }
            logger.info("<=============>    早上8点发送D-2全部 end<=================> ");
        });
        return ResponseEntitySpecBuilder.success();
    }


    @RequestMapping(value = "/call11D_1/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> call11D_1() {
        logger.info("<=============>    11点发送D-1全部  start     <=================>");
        executorService.submit(() -> {
            TwilioCallResultRequest request = new TwilioCallResultRequest();
            request.setCallPhase("D-1");
            request.setCallPhaseType(TwilioCallResult.CallPhaseTypeEnum.CALL_PHASE_ALL.getCode());
//            request.setCallUrl(config.getUrl() + "D-1/twilioXml");
            request.setCallUrl("http://h5.do-it.id/twilio/D-1.mp3");
            request.setDays(-1);
            request.setBatchNo(String.valueOf(System.currentTimeMillis() + "D-1"));
//            twilioService.startCallTwilio(request);
            try {
                inforbipCollectionService.startCallInfobip(request);
            } catch (Exception e) {
                logger.error("send voice is error, {}", e);
            }
            logger.info("<=============>    11点发送D-1全部 end<=================> ");
        });
        return ResponseEntitySpecBuilder.success();
    }


    @RequestMapping(value = "/call11D_2/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> call11D_2(){
        logger.info("<=============>    11点发送D-2部分  start     <=================>");
        executorService.submit(() -> {
            TwilioCallResultRequest request = new TwilioCallResultRequest();
            request.setCallPhase("D-2");
            request.setCallPhaseType(TwilioCallResult.CallPhaseTypeEnum.CALL_PHASE_NOT_RESPONSE.getCode());
//            request.setCallUrl(config.getUrl() + "D-2/twilioXml");
            request.setCallUrl("http://h5.do-it.id/twilio/D-2.mp3");
            request.setDays(-2);
            request.setBatchNo(String.valueOf(System.currentTimeMillis() + "D-2"));
//            twilioService.startCallTwilio(request);
            try {
                inforbipCollectionService.startCallInfobip(request);
            } catch (Exception e) {
                logger.error("send voice is error, {}", e);
            }
            logger.info("<=============>    11点发送D-2部分 end<=================> ");
        });
        return ResponseEntitySpecBuilder.success();
    }


    @RequestMapping(value = "/call11D_3/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> call11D_3(){
        logger.info("<=============>    11点发送D-3全部  start     <=================>");
        executorService.submit(() -> {
            TwilioCallResultRequest request = new TwilioCallResultRequest();
            request.setCallPhase("D-3");
            request.setCallPhaseType(TwilioCallResult.CallPhaseTypeEnum.CALL_PHASE_ALL.getCode());
//            request.setCallUrl(config.getUrl() + "D-3/twilioXml");
            request.setCallUrl("http://h5.do-it.id/twilio/D-3.mp3");
            request.setDays(-3);
            request.setBatchNo(String.valueOf(System.currentTimeMillis() + "D-3"));
//            twilioService.startCallTwilio(request);
            try {
                inforbipCollectionService.startCallInfobip(request);
            } catch (Exception e) {
                logger.error("send voice is error, {}", e);
            }
            logger.info("<=============>    11点发送D-3全部 end<=================> ");
        });
        return ResponseEntitySpecBuilder.success();
    }


    @RequestMapping(value = "/call15D_1/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> call15D_1(){
        logger.info("<=============>    15点发送D-1部分  start     <=================>");
        executorService.submit(() -> {
            TwilioCallResultRequest request = new TwilioCallResultRequest();
            request.setCallPhase("D-1");
            request.setCallPhaseType(TwilioCallResult.CallPhaseTypeEnum.CALL_PHASE_NOT_RESPONSE.getCode());
//            request.setCallUrl(config.getUrl() + "D-1/twilioXml");
            request.setCallUrl("http://h5.do-it.id/twilio/D-1.mp3");
            request.setDays(-1);
            request.setBatchNo(String.valueOf(System.currentTimeMillis() + "D-1"));
//            twilioService.startCallTwilio(request);
            try {
                inforbipCollectionService.startCallInfobip(request);
            } catch (Exception e) {
                logger.error("send voice is error, {}", e);
            }
            logger.info("<=============>    15点发送D-1部分 end<=================> ");
        });
        return ResponseEntitySpecBuilder.success();
    }


    @RequestMapping(value = "/call15D_2/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> call15D_2(){
        logger.info("<=============>    15点发送D-2部分  start     <=================>");
        executorService.submit(() -> {

            TwilioCallResultRequest request = new TwilioCallResultRequest();
            request.setCallPhase("D-2");
            request.setCallPhaseType(TwilioCallResult.CallPhaseTypeEnum.CALL_PHASE_NOT_RESPONSE.getCode());
//            request.setCallUrl(config.getUrl() + "D-2/twilioXml");
            request.setCallUrl("http://h5.do-it.id/twilio/D-2.mp3");
            request.setDays(-2);
            request.setBatchNo(String.valueOf(System.currentTimeMillis() + "D-2"));
//            twilioService.startCallTwilio(request);
            try {
                inforbipCollectionService.startCallInfobip(request);
            } catch (Exception e) {
                logger.error("send voice is error, {}", e);
            }
            logger.info("<=============>    15点发送D-2部分 end<=================> ");
        });
        return ResponseEntitySpecBuilder.success();
    }

    @RequestMapping(value = "/startUpdateCallResult/managerTask", method = RequestMethod.GET)
    public ResponseEntitySpec<Object> startUpdateCallResult(@RequestParam("num") Integer num){
        logger.info("<=============>    updater twilio start  start     <=================>");
        executorService.submit(() -> {

            twilioService.startUpdateCallResult(num);
            logger.info("<=============>    updater twilio start end<=================> ");
        });
        return ResponseEntitySpecBuilder.success();
    }

    @RequestMapping(value = "/getInfobipCollectionReport/managerTask", method = RequestMethod.GET)
    public ResponseEntitySpec<Object> getInfobipCollectionReport(@RequestParam("num") Integer num){
        logger.info("<=============>    getInfobipCollectionReport start       <=================>");
        executorService.submit(() -> {

            try {
                inforbipCollectionService.getReport(num);
            } catch (Exception e) {
                logger.error("get infobip collection is error.", e);
            }
            logger.info("<=============>   getInfobipCollectionReport end<=================> ");
        });
        return ResponseEntitySpecBuilder.success();
    }


//===========以下为沉默和提额为申请的外呼========

    @RequestMapping(value = "/infobipSilient/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> infobipSilient(){
        logger.info("<=============>   start infobipSilient    <=================>");
        executorService.submit(() -> {

            TwilioCallResultRequest request = new TwilioCallResultRequest();
            request.setCallPhase("infobipSilient");
            request.setCallPhaseType(3);
            request.setCallUrl("http://h5.do-it.id/twilio/silentAndNotSubmit.mp3");
            request.setBatchNo(String.valueOf(System.currentTimeMillis() + "infobipSilient"));
//            twilioService.startCallTwilio(request);
            try {
                inforbipCollectionService.startCallInfobip(request);
            } catch (Exception e) {
                logger.error("send voice is error, {}", e);
            }
            logger.info("<=============>    end silSedFri <=================> ");
        });
        return ResponseEntitySpecBuilder.success();
    }
//
    @RequestMapping(value = "/infobipNotSumbit/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> infobipNotSumbit(){
        logger.info("<=============>   start infobipNotSumbit    <=================>");
        executorService.submit(() -> {

            TwilioCallResultRequest request = new TwilioCallResultRequest();
            request.setCallPhase("infobipNotSumbit");
            request.setCallPhaseType(4);
            request.setCallUrl("http://h5.do-it.id/twilio/silentAndNotSubmit.mp3");
            request.setBatchNo(String.valueOf(System.currentTimeMillis() + "infobipNotSumbit"));
            try {
                inforbipCollectionService.startCallInfobip(request);
            } catch (Exception e) {
                logger.error("send infobipNotSumbit voice is error, {}", e);
            }
            logger.info("<=============>    end infobipNotSumbit <=================> ");
        });
        return ResponseEntitySpecBuilder.success();
    }

    @RequestMapping(value = "/infobipReduce/managerTask", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> infobipReduce(){
        logger.info("<=============>   start infobipReduce    <=================>");
        executorService.submit(() -> {

            TwilioCallResultRequest request = new TwilioCallResultRequest();
            request.setCallPhase("infobipReduce");
            request.setCallPhaseType(5);
            request.setCallUrl("http://h5.do-it.id/twilio/reduce.mp3");
            request.setBatchNo(String.valueOf(System.currentTimeMillis() + "infobipReduce"));
            try {
                inforbipCollectionService.startCallInfobip(request);
            } catch (Exception e) {
                logger.error("send infobipReduce voice is error, {}", e);
            }
            logger.info("<=============>    end infobipReduce <=================> ");
        });
        return ResponseEntitySpecBuilder.success();
    }

}
