package com.yqg.manage.controller.third;


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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ManageApplication.class)
@WebIntegrationTest
public class TwilioControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static String requestStr;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        requestStr = "{\n" +
                "    \"batchNo\": \"\",\n" +
                "    \"callPhase\": \"\",\n" +
                "    \"sendStartTime\": \"\",\n" +
                "    \"sendEndTime\": \"\",\n" +
                "    \"channel\": \"\",\n" +
                "    \"pageNo\": 1,\n" +
                "    \"pageSize\": 10\n" +
                "}";
    }

    @Test
    public void testListTwilioCallResult() throws Exception {

        ResultActions resultActions = mockMvc.perform(post("/manage/listTwilioCallResult")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetTwilioCallResultDetail() throws Exception {

        ResultActions resultActions = mockMvc.perform(post("/manage/getTwilioCallResultDetail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testListTwilioWaRecord() throws Exception {
        requestStr = "{\n" +
                "    \"replyContent\": 3354897412,\n" +
                "    \"pageNo\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"orderNo\": \"\",\n" +
                "    \"userName\": \"\",\n" +
                "    \"phoneNumber\": \"\",\n" +
                "    \"overDueDay\": \"\",\n" +
                "    \"promiseTimeStart\": \"\",\n" +
                "    \"promiseTimeEnd\": \"\",\n" +
                "    \"isRepeatBorrowing\": \"\",\n" +
                "    \"solveType\": \"\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/listTwilioWaRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testInsertOrUpdateTwilioWaRecord() throws Exception {
        requestStr = "{\n" +
                "    \"replyContent\": 3354897412,\n" +
                "    \"pageNo\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"orderNo\": \"\",\n" +
                "    \"userName\": \"\",\n" +
                "    \"phoneNumber\": \"\",\n" +
                "    \"overDueDay\": \"\",\n" +
                "    \"promiseTimeStart\": \"\",\n" +
                "    \"promiseTimeEnd\": \"\",\n" +
                "    \"isRepeatBorrowing\": \"\",\n" +
                "    \"solveType\": \"\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/insertOrUpdateTwilioWaRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testTempUpdateOrderRole() throws Exception {
        requestStr = "";
        ResultActions resultActions = mockMvc.perform(post("/manage/insertOrUpdateTwilioWaRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}