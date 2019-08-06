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

public class ReceiverSchedulingControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getReceiverSchedulingList() throws Exception {

        String str = "{\n" +
                "  \"pageNo\n\":\"1\",\n" +
                "  \"pageSize\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/ReceiverSchedulingList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteReceiverScheduling() throws Exception {
        String str="";
        ResultActions resultActions = mockMvc.perform(post("/manage/DeleteReceiverScheduling")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void addReceiverScheduling() throws Exception {
        String str = "{\n" +
                "  \"endtime\n\":\"1\",\n" +
                "  \"pageNo\":\"1\",\n" +
                "  \"pageSize\":\"1\",\n" +
                "  \"startTime\":\"1\",\n" +
                "  \"userName\":\"1\",\n" +
                "  \"work\":\"1\",\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/AddReceiverScheduling")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void updateReceiverScheduling() {
    }

    @Test
    public void downloadReceiverSchedulingTemplateExcel() {
    }

    @Test
    public void importReceiverSchedulingDataByExcel() throws Exception {
        String str = "";
        ResultActions resultActions = mockMvc.perform(post("/manage/importReceiverSchedulingDataByExcel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}