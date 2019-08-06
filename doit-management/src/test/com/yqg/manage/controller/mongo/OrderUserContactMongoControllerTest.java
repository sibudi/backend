package com.yqg.manage.controller.mongo;

import com.yqg.ManageApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ManageApplication.class)
@WebIntegrationTest
public class OrderUserContactMongoControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void orderUserContactMongoByOrderNo() throws Exception {

        String str = "{\n" +
                "  \"orderNo\n\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/orderUserContactMongoByOrderNo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void orderUserCallRecordMongoByUuid() throws Exception {

        String str = "{\n" +
                "  \"uuid\n\":\"123\",\n" +
                "  \"dataType\":\"1\",\n" +
                "  \"orderNo\n\":\"1\",\n" +
                "  \"channel\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/orderUserCallRecordMongoByUuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void frequentOrderUserCallRecordMongo() throws Exception {

        String str = "{\n" +
                "  \"orderNo\n\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/frequentOrderUserCallRecordMongo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getHouseWifiInfo() throws Exception {

        String str = "{\n" +
                "  \"userUuid\n\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/getHouseWifiInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}