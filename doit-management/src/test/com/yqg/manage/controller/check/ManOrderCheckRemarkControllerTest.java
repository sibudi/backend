package com.yqg.manage.controller.check;

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
public class ManOrderCheckRemarkControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getCheckRemarkListByOrderNo() throws Exception {

        String str = "{\n" +
                "  \"langue\":\"1\",\n" +
                "  \"uuid\":\"021903311327198961\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/CheckRemarkListByOrderNo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void firstCheck() throws Exception {

        String str = "{\n" +
                "  \"remark\":\"asd\",\n" +
                "  \"sessionId\":\"021903311327198961\",\n" +
                "  \"unPass\":\"type==2\",\n" +
                "  \"orderNo\":\"1351513\",\n" +
                "  \"type\":\"1\",\n" +
                "  \"burningTime\":\"2019-4-10 15:22:23\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/firstCheck")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void secondCheck() throws Exception {

        String str = "{\n" +
                "  \"remark\":\"this.reviewRemark1\",\n" +
                "  \"sessionId\":\"021903311327198961\",\n" +
                "  \"pass\":\"1\",\n" +
                "  \"orderNo\":\"1351513\",\n" +
                "  \"burningTime\":\"2019-4-10 15:22:23\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/secondCheck")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getTeleReviewQuestion() throws Exception {

        String str = "{\n" +
                "  \"teleReviewType\":\"1\",\n" +
                "  \"langue\":\"1\",\n" +
                "  \"type\":\"1\",\n" +
                "  \"orderNo\":\"1351513\",\n" +
                "  \"uuid\":\"123\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/getTeleReviewQuestion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getTeleReviewObj() throws Exception {

        String str = "{\n" +
                "  \"teleReviewType\":\"1\",\n" +
                "  \"type\":\"1\",\n" +
                "  \"orderNo\":\"1351513\",\n" +
                "  \"uuid\":\"123\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/getTeleReviewObj")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void getTeleReviewRecords() throws Exception {

        String str = "{\n" +
                "  \"teleReviewType\":\"1\",\n" +
                "  \"langue\":\"1\",\n" +
                "  \"orderNo\":\"1351513\",\n" +
                "  \"uuid\":\"123\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/getTeleReviewRecords")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void searchRejectReason() throws Exception {

        String str = "{\n" +
                "  \"permissionCode\":\"REJECT_REASON_PERMISSION\",\n" +
                "  \"uuid\":\"123\",\n" +
                "  \"orderNo\":\"1351513\",\n" +
                "  \"userId\":\"123\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/searchRejectReason")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void saveOrderMogo() throws Exception {

        String str = "{\n" +
                "  \"uuid\":\"021903311327198961\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/getOperatorType")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void saveOperatorType() throws Exception {

        String str = "{\n" +
                "  \"createUser\":\"1351561361\",\n" +
                "  \"orderNo\":\"021903311327198961\",\n" +
                "  \"uuid\":\"021903311327198961\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/saveOperatorType")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void saveOrderInfoToMongo() throws Exception {

        String str = "{\n" +
                "  \"uuid\":\"021903311327198961\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/saveOrderInfoToMongo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteOrderDataMongo() throws Exception {

        String str = "{\n" +
                "  \"uuid\":\"021903311327198961\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/deleteOrderDataMongo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}