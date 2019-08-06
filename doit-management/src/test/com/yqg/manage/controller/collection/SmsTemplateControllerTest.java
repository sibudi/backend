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
public class SmsTemplateControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void insertOrUpdateSmsTemp() throws Exception {

        String str = "{\n" +
                "  \"smsTitle\n\":\"1\",\n" +
                "  \"smsContent\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/smsTemplate/insertOrUpdateSmsTemp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteSmsTemp() throws Exception {

        String str = "{\n" +
                "  \"id\n\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/smsTemplate/deleteSmsTemp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void smsTemplateList() throws Exception {

        String str = "{\n" +
                "  \"pageNo\n\":\"1\",\n" +
                "  \"pageSize\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/smsTemplate/smsTemplateList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void sendSmsToLoanUser() throws Exception {

        String str = "{\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/smsTemplate/listCollectionSmsSwitch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void listCollectionSmsSwitch() throws Exception {

        String str = "{\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/smsTemplate/listContactSwitch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void listContactSwitch() throws Exception {

        String str = "{\n" +
                "  \"sysKey\n\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/smsTemplate/getCollectionSmsSwitch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getCollectionSmsSwitch() throws Exception {

        String str = "{\n" +
                "  \"sysKey\n\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/smsTemplate/getContactSwitch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getContactSwitch() throws Exception {

        String str = "{\n" +
                "  \"id\n\":\"1\",\n" +
                "  \"sysValue\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/smsTemplate/updateCollectionSmsSwitch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void updateCollectionSmsSwitch() throws Exception {

        String str = "{\n" +
                "  \"sysKey\n\":\"1\",\n" +
                "  \"sysValue\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/smsTemplate/updateContactSwitch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

}