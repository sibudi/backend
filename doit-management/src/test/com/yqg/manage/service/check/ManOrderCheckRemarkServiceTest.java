package com.yqg.manage.service.check;

import com.yqg.ManageApplication;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.manage.service.check.request.CheckRequest;
import com.yqg.manage.service.order.request.ManOrderListSearchResquest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ManageApplication.class)
@WebAppConfiguration
@Slf4j

public class ManOrderCheckRemarkServiceTest {

    @Autowired
    private ManOrderCheckRemarkService ManOrderCheckRemarkService;

    @Test
    public void getCheckRemarkListByOrderNo() throws ServiceExceptionSpec {
        ManOrderListSearchResquest request = new ManOrderListSearchResquest();
        request.setUuid("1");
        request.setRealName("lala");
        System.out.println(ManOrderCheckRemarkService.getCheckRemarkListByOrderNo(request));
    }

    @Test
    public void orderCheckRemarkListByOrderNo() {
        try {
            System.out.println(ManOrderCheckRemarkService.orderCheckRemarkListByOrderNo("1"));
        } catch (ServiceExceptionSpec serviceExceptionSpec) {
            serviceExceptionSpec.printStackTrace();
        }
    }

    @Test
    public void firstCheck() {

        CheckRequest request = new CheckRequest();
        request.setRemark("1");
        request.setOrderNo("lala");
        request.setSessionId("1");
        request.setOrderNo("1");
        request.setBurningTime("1");
        request.setUnPass(true);
        try {
            ManOrderCheckRemarkService.firstCheck(request);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void secondCheck() {
        CheckRequest request = new CheckRequest();
        request.setRemark("1");
        request.setOrderNo("1");
        request.setSessionId("1");
        request.setPass(1);
        request.setType(1);
        request.setBurningTime("1");
        request.setUnPass(true);
        try {
            ManOrderCheckRemarkService.firstCheck(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void saveOrderInfoToMongo() {

        //ManOrderCheckRemarkService.sendReviewPassMsg("1");
    }

    @Test
    public void saveOrderInfoToMongoByHand() {
    }

    @Test
    public void addOrderCheckRemark() {
    }

    @Test
    public void getSysAutoReviewRule() {
    }
}