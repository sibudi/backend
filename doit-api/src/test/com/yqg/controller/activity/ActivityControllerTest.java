package com.yqg.controller.activity;

import com.yqg.ApiApplication;
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
@SpringApplicationConfiguration(classes = ApiApplication.class)
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
    public void scanUserInviteList() throws Exception {
        String str = "{\n" +
                "  \"pageNo\" : \"1\",\n" +
                "  \"pageSize\" : \"30\",\n" +
                "  \"sessionId\" : \"4d42393d0b2d474696a06a9c4e8f72c3\"\n"+
                "}";

        ResultActions resultActions = mockMvc.perform(post("/activity/scanUserInviteList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getAccountRecords() throws Exception {
        String str = "{\n" +
                "  \"pageNo\" : \"1\",\n" +
                "  \"pageSize\" : \"30\",\n" +
                "  \"sessionId\" : \"4d42393d0b2d474696a06a9c4e8f72c3\"\n"+
                "}";

        ResultActions resultActions = mockMvc.perform(post("/activity/getAccountRecords")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

}