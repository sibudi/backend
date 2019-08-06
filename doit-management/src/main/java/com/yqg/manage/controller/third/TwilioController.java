package com.yqg.manage.controller.third;

import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Play;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.common.utils.StringUtils;
import com.yqg.manage.service.third.ThirdTwilioService;
import com.yqg.manage.service.third.request.ThirdTwilioRequest;
import com.yqg.manage.service.third.request.TwilioWhatsAppRequest;
import com.yqg.service.third.twilio.TwilioService;
import com.yqg.service.third.twilio.request.TwilioWhatsAppRecordRequest;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Author: tonggen
 * Date: 2018/10/14
 * time: 下午5:06
 */
@RestController
public class TwilioController {

    private Logger logger = LoggerFactory.getLogger(TwilioController.class);

    @Autowired
    private TwilioService twilioService1;

    @Autowired
    private ThirdTwilioService twilioService;

    @ApiOperation("获得mp3文件流，Author: tonggen")
    @RequestMapping(value = "/manage/twilioMP3", method = RequestMethod.GET)
    public void twilioMP3(@RequestParam("type") String type, HttpServletRequest request, HttpServletResponse response) {

        if (StringUtils.isEmpty(type)) {
            type = "D0" ;
        }
        try {
            OutputStream os = response.getOutputStream();
            response.setContentType("audio/mp3");
            InputStream stream = new FileInputStream("/MyUpload/twilio/" + type + ".mp3");
            os.write(IOUtils.toByteArray(stream));
        } catch (Exception e) {
            logger.error("twilioMP3 is error, type is {}", type);
        }
    }

    @ApiOperation("生成twilio语音的提示语，通过不同type区分，Author: tonggen")
    @RequestMapping(value = "/manage/{type}/twilioXml", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML)
    public String twilioXml(@PathVariable("type") String type) {
        Play play = new Play.Builder("http://cn.control.do-it.id/manage/twilioMP3?type=" + type)
                .loop(1).build();
        VoiceResponse response = new VoiceResponse.Builder().play(play).build();
        return response.toXml();
    }


    @ApiOperation("查询twilio语音记录列表")
    @RequestMapping(value = "/manage/listTwilioCallResult", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<PageData> listTwilioCallResult(@RequestBody ThirdTwilioRequest request) {

        return ResponseEntitySpecBuilder.success(twilioService.listTwilioCallResult(request));
    }

    @ApiOperation("根据批次号，查询批次详情")
    @RequestMapping(value = "/manage/getTwilioCallResultDetail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<PageData> getTwilioCallResultDetail(@RequestBody ThirdTwilioRequest request) throws ServiceExceptionSpec {

        return ResponseEntitySpecBuilder.success(twilioService.getTwilioCallResultDetail(request));
    }


    @ApiOperation("WA回调方法，获得用户回复")
    @RequestMapping(value = "/manage/getTwilioWaResponses", method = RequestMethod.POST, produces = MediaType.APPLICATION_FORM_URLENCODED)
    public ResponseEntitySpec<Object> getTwilioWaResponses(TwilioWhatsAppRequest request)
            throws ServiceExceptionSpec {

        twilioService.getTwilioWaResponses(request);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("twilio WA回复（状态变化时，twilio会进行回调)，Author: tonggen")
    @RequestMapping(value = "/manage/twilioWhatsAppResXML", method = RequestMethod.POST, produces = MediaType.APPLICATION_XML)
    public String twilioWhatsAppResXML(TwilioWhatsAppRequest request) throws ServiceExceptionSpec {

        return twilioService.twilioWhatsAppResXML(request);
    }

    @ApiOperation("test，Author: tonggen")
    @RequestMapping(value = "/manage/testTwilio", method = RequestMethod.GET)
    public ResponseEntitySpec<Object> testTwilio() throws ServiceExceptionSpec {

        TwilioWhatsAppRecordRequest request = new TwilioWhatsAppRecordRequest();
        request.setBatchNo(String.valueOf(System.currentTimeMillis() + "D-1"));
        request.setDays(-1);
        request.setReplyContent("this just is a test!");
        twilioService1.startWhatsAppTwilio(request);
        return ResponseEntitySpecBuilder.success();
    }


    @ApiOperation("twilio whatsapp 列表查询，Author: tonggen")
    @RequestMapping(value = "/manage/listTwilioWaRecord", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<PageData> listTwilioWaRecord(@RequestBody TwilioWhatsAppRequest request)
            throws ServiceExceptionSpec {

        return ResponseEntitySpecBuilder.success(twilioService.listTwilioWaRecord(request));
    }

    @ApiOperation("twilio whatsapp 新增或者修改解决方法，Author: tonggen")
    @RequestMapping(value = "/manage/insertOrUpdateTwilioWaRecord", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Boolean> insertOrUpdateTwilioWaRecord(@RequestBody TwilioWhatsAppRequest request)
            throws ServiceExceptionSpec {

        return ResponseEntitySpecBuilder.success(twilioService.insertOrUpdateTwilioWaRecord(request));
    }

    @ApiOperation("Author: tonggen")
    @RequestMapping(value = "/manage/tempUpdateOrderRole", method = RequestMethod.GET)
    public ResponseEntitySpec<Integer> tempUpdateOrderRole(@RequestParam String orderNo)
            throws ServiceExceptionSpec {

        return ResponseEntitySpecBuilder.success(twilioService.tempUpdateOrderRole(orderNo));
    }
}
