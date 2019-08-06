package com.yqg.manage.controller.collection;

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
public class CollectionSmsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getAllSmsTemplate() throws Exception {
        String str = "{\n" +
                "  \"collectionSmsType\":\"1\",\n" +
                "  \"smsTitle\":\"1\",\n" +
                "  \"smsTemplateId\":\"1\",\n" +
                "  \"orderNo\":\"1123123\",\n" +
                "  \" userUuid\":\"1123\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/getAllSmsTemplate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void sendCollectionSms() throws Exception {
        String str = "{\n" +
                "  \"orderNo,\n\":\"1\",\n" +
                "  \"collectionLevel\":\"1\",\n" +
                "  \"collectionSmsType\":\"1\",\n" +
                "  \"smsTitle\":\"1123123\",\n" +
                "  \"userUuid\":\"1123\",\n" +
                "  \"smsTemplateId\":\"1123\",\n" +
                "}";



        ResultActions resultActions = mockMvc.perform(post("/manage/collection/sendCollectionSms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void collectSmsTemplateList() throws Exception {
        String str = "{\n" +
                "  \"smsTitle\n\":\"1\",\n" +
                "  \"realName\":\"1\",\n" +
                "  \"minSendTime\":\"1\",\n" +
                "  \"maxSendTime\":\"1123123\",\n" +
                "  \"sendUser\":\"1123\",\n" +
                "  \"uuid\":\"1123\",\n" +
                "  \"isArrived,\n\":\"1\",\n" +
                "  \"status\":\"1\",\n" +
                "  \"pageNo\":\"1\",\n" +
                "  \"pageSize\":\"1123123\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/collectSmsTemplateList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getCollectionSmsRecordList() throws Exception {
        String str = "{\n" +
                "  \"orderNo,\n\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/getCollectSmsRecordList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}