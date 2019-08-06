package com.yqg.manage.service.check;

import com.yqg.ManageApplication;
import com.yqg.common.models.PageData;
import com.yqg.manage.service.check.request.ReceiverSchedulingRequest;
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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ManageApplication.class)
@WebAppConfiguration
@Slf4j
public class ManReceiverSchedulingServiceTest {

    @Autowired
    private ManReceiverSchedulingService manReceiverSchedulingService;

    @Test
    public void getReceiverSchedulingList() {
        ReceiverSchedulingRequest request = new ReceiverSchedulingRequest();
        request.setStartTime(new Date());
        request.setEndTime(new Date());
        PageData data = manReceiverSchedulingService.getReceiverSchedulingList(request);
        Assert.assertEquals(data.getRecordsTotal(), 0);
        log.info("Data:" + data);
    }

    @Test
    public void deleteReceiverScheduling() {
        int integer = manReceiverSchedulingService.deleteReceiverScheduling(Arrays.asList("asdf", "ad11"));
        Assert.assertEquals(integer, 0);
    }


    @Test
    public void addOrUpdateReceiverScheduling() {
        ReceiverSchedulingRequest request = new ReceiverSchedulingRequest();
        request.setEndTime(new Date());
        request.setStartTime(new Date());
        request.setUserName("测试");
        request.setId(10000000);
        request.setWork(0);
        int updateResult = manReceiverSchedulingService.addOrUpdateReceiverScheduling(request);
        Assert.assertEquals(updateResult, 0);
        request.setId(null);
        int addResult = manReceiverSchedulingService.addOrUpdateReceiverScheduling(request);
        Assert.assertEquals(addResult, 1);
    }

    @Test
    public void loadReceiverSchedulingListByExcel() throws IOException {
//        List<ReceiverScheduling> receiverSchedulings = manReceiverSchedulingService.loadReceiverSchedulingListByExcel("D:\\Downloads\\ChromeDownload\\12312 (1).xls");
//        log.info(JsonUtils.serialize(receiverSchedulings));
    }
    @Test
    public void schedulingReceiverRestAndWork()  {
        manReceiverSchedulingService.schedulingReceiverRestAndWork();
    }





}