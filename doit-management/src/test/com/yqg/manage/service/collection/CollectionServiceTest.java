package com.yqg.manage.service.collection;



import com.yqg.ManageApplication;
import com.yqg.common.models.PageData;
import com.yqg.common.utils.JsonUtils;
import com.yqg.manage.service.check.request.ReceiverSchedulingRequest;
import com.yqg.manage.service.collection.response.CollectorResponseInfo;
import com.yqg.management.entity.ReceiverScheduling;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ManageApplication.class)
@WebAppConfiguration
@Slf4j
public class CollectionServiceTest {

    @Autowired
    private CollectionService collectionService;

    @Test
    public void getOutSourceInfo() throws Exception {
        List<CollectorResponseInfo> outSourceInfo = collectionService.getOutSourceInfo(16);
        System.out.println("getOutSourceInfo:"+JsonUtils.serialize(outSourceInfo));
    }

    @Test
    public void getAllCollectors() throws Exception {
        List<CollectorResponseInfo> outSourceInfo = collectionService.getAllCollectors(1,0);
        System.out.println("getOutSourceInfo:"+JsonUtils.serialize(outSourceInfo));
    }

}
