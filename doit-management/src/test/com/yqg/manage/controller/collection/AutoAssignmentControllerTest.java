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
public class AutoAssignmentControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void autoAssignCollectionOrderForD0() throws Exception {

        String str = "{\n" +
                "  \"collectorAssignmentRequest\":\"\",\n" +
                "  \"section\":\"1\",\n" +
                "  \"amountApply\":\"13311\",\n" +
                "  \"otherAmount\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection//D0-auto-order-assignment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void autoAssignCollectionOrderForOverdue() throws Exception {

        String str = "{\n" +
                "  \"collectorAssignmentRequest\":\"\",\n" +
                "  \"section\":\"1\",\n" +
                "  \"amountApply\":\"13311\",\n" +
                "  \"otherAmount\":\"1\",\n" +
                "  \"postId\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/overdue-auto-order-assignment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getD0AssignmentStatistics() throws Exception {

        String str = "{\n" +
                "  \"amountApply\":\"\",\n" +
                "  \"section\":\"1\",\n" +
                "  \"otherAmount\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/D0-assignment-statistics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getOverdueAssignmentStatistics() throws Exception {

        String str = "{\n" +
                "  \"postId\":\"1\",\n" +
                "  \"section\":\"1\",\n" +
                "  \"amountApply\":\"13311\",\n" +
                "  \"otherAmount\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/overdue-assignment-statistics")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void resetCollectOrderByHand() throws Exception {

        String str = "{\n" +
                "  \"outSourceId\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/overdue-assignment-statistics-outSource")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

}