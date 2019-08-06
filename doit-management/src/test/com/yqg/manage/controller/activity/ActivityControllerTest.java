package com.yqg.manage.controller.activity;

import com.yqg.ManageApplication;
import com.yqg.common.utils.RSAUtils;
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
public class ActivityControllerTest {


    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void withdrawList() throws Exception {
        String str = "{\n" +
                "  \"beginTime\":\"2019-4-10 15:22:23\",\n" +
                "  \"endTime\":\"2019-4-12 15:22:32\",\n" +
                "  \"pageNo\":\"1\",\n" +
                "  \"pageSize\":\"20\",\n" +
                "  \"sessionId\":\"2B255FD4D8CA41049AD56F66C47CBB89\",\n" +
                "  \"type\":\"4\",\n" +
                "  \"amount\":\"\",\n" +
                "  \"caseoutAccount\":\"13551\",\n" +
                "  \"caseoutAccountName\":\"lhihaf\",\n" +
                "  \"channel\":\"2\",\n" +
                "  \"isAssigned\":\"\",\n" +
                "  \"isRepeatBorrowing\":\"\",\n" +
                "  \"uuid\":\"131421515\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/activity/withdrawList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void withdrawRecord() throws Exception {

        String str = "{\n" +
                "  \"beginTime\":\"2019-4-10 15:22:23\",\n" +
                "  \"caseoutAccount\":\"\",\n" +
                "  \"caseoutAccountName\":\"\",\n" +
                "  \"endTime\":\"2019-4-12 16:2:47\",\n" +
                "  \"isAssigned\":\"\",\n" +
                "  \"isRepeatBorrowing\":\"\",\n" +
                "  \"mobile\":\"123456789\",\n" +
                "  \"pageNo\":\"1\",\n" +
                "  \"pageSize\":\"20\",\n" +
                "  \"sessionId\":\"2B255FD4D8CA41049AD56F66C47CBB89\",\n" +
                "  \"type\":\"4\",\n" +
                "  \"isRepeatBorrowing\":\"\",\n" +
                "  \"uuid\":\"\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/activity/withdrawRecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void withdrawSuccess() throws Exception {

        String str = "{\n" +
                "  \"sessionId\":\"623A950B25BE4AC29100475675C56E79\",\n" +
                "  \"uuid\":\"021903311327198961\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/activity/withdrawSuccess")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void withdrawFail() throws Exception {

        String str = "{\n" +
                "  \"sessionId\":\"623A950B25BE4AC29100475675C56E79\",\n" +
                "  \"uuid\":\"021903311327198961\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/activity/withdrawFail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}