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
public class ManQualityCheckControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void listQualityCheckConfigs() throws Exception {

        String str = "{\n" +
                "  \"pageNo\n\":\"1\",\n" +
                "  \"pageSize\":\"20\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/qualityCheck/listQualityCheckConfigs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteQualityCheckConfig() throws Exception {

        String str = "{\n" +
                "  \"id\n\":\"123123\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/qualityCheck/deleteQualityCheckConfig")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void insertOrUpdateCheckConfig() throws Exception {

        String str = "{\n" +
                "  \"title\n\":\"asd\",\n" +
                "  \"titleInn\":\"1\",\n" +
                "  \"fineMoney\":\"1000\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/qualityCheck/insertOrUpdateCheckConfig")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void listQualityChecks() throws Exception {

        String str = "{\n" +
                "  \"realName\n\":\"1\",\n" +
                "  \"uuid\":\"1\",\n" +
                "  \"collectiorName\":\"1\",\n" +
                "  \"updateBeginTime\":\"1123123\",\n" +
                "  \"updateEndTime\":\"1123\",\n" +
                "  \"dueDayStartTime\":\"1123\",\n" +
                "  \"dueDayEndTime,\n\":\"1\",\n" +
                "  \"collectionInQualityCheckId\":\"1\",\n" +
                "  \"pageNo\":\"1\",\n" +
                "  \"pageSize\":\"1123123\",\n" +
                "  \"outsourceId\":\"1123123\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/qualityCheck/listQualityChecks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void insertQualityCheckRecord() throws Exception {

        String str = "{\n" +
                "  \"orderNo\n\":\"123456\",\n" +
                "  \"userUuid\":\"123123\",\n" +
                "  \"checkTag\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/qualityCheck/insertQualityCheckRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getCheckReport() throws Exception {

        String str = "{\n" +
                "  \"outsourceId\n\":\"1\",\n" +
                "  \"checkerId\":\"1\",\n" +
                "  \"startTime\":\"1\",\n" +
                "  \"endTime\":\"1123123\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/qualityCheck/getCheckReport")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}