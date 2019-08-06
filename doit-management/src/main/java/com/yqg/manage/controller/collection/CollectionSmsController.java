package com.yqg.manage.controller.collection;

import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.dal.collection.SmsTemplateDao;
import com.yqg.manage.enums.CollectionSmsType;
import com.yqg.manage.service.collection.CollectionSmsService;
import com.yqg.manage.service.collection.request.SmsTemplateRequest;
import com.yqg.manage.service.mongo.request.CollectSmsRequest;
import com.yqg.manage.service.mongo.request.OrderMongoRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;


/**
 * Created with IntelliJ IDEA.
 * User: caomiaoke
 * Date: 26/03/2018
 * Time: 11:07 AM
 */

@RestController
@RequestMapping("/manage/collection")
@Api(tags = "催收短信")
public class CollectionSmsController {

    @Autowired
    private CollectionSmsService collectionSmsService;

    @Autowired
    private SmsTemplateDao smsTemplateDao;

    @ApiOperation("获取短信模版")
    @RequestMapping(value = "/getAllSmsTemplate", method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> getAllSmsTemplate(@RequestBody OrderMongoRequest request){
        return ResponseEntitySpecBuilder.success(this.smsTemplateDao.getAllSmsTemplate());
    }


    @ApiOperation("获取接收短信人类型")
    @RequestMapping(value = "/getCollectionType", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> getCollectionSmsType(@RequestBody OrderMongoRequest request){
        return ResponseEntitySpecBuilder.success(CollectionSmsType.values());
    }

    @ApiOperation("发送短信")
    @RequestMapping(value = "/sendCollectionSms", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> sendCollectionSms(@RequestBody CollectSmsRequest collectSmsRequest) throws Exception {

        return ResponseEntitySpecBuilder.success(collectionSmsService.sendTopnCollectSms(collectSmsRequest));
    }

    @ApiOperation("分页获得短信列表")
    @RequestMapping(value = "/collectSmsTemplateList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> collectSmsTemplateList(@RequestBody SmsTemplateRequest request) throws Exception {
        return ResponseEntitySpecBuilder
                .success(collectionSmsService.collectSmsTemplateList(request));
    }

    @ApiOperation("获得发送记录")
    @RequestMapping(value = "/getCollectSmsRecordList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public ResponseEntitySpec<Object> getCollectionSmsRecordList(@RequestBody CollectSmsRequest request){
        return ResponseEntitySpecBuilder.success(collectionSmsService.getCollectionSmsRecord(request));
    }
}
