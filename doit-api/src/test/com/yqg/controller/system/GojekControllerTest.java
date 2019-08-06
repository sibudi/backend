package com.yqg.controller.system;

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
public class GojekControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void begin() throws Exception {
        String str = "{\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"timestamp\" : \"1553484118\",\n" +
                "  \"deviceId\" : \"CEF94B4F-824A-482B-BD27-454D3662DAFA\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"IPAdress\" : \"192.168.0.39\",\n" +
                "  \"sessionId\" : \"d8c5dbec232e4b35994506c2ac1042a9\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"mobile\" : \"81350505050\",\n" +
                "  \"id_num\" : \"89\",\n" +
                "  \"captcha\" : \"1234\",\n" +
                "  \"report_task_token\" : \"asdf\",\n" +
                "  \"auth_token\" : \"ghjkl\",\n" +
                "  \"userUuid\" : \"11311\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"sign\" : \"E5ZjE3NDVmMGNhMGE4ZDc1OWRkZTY2NDFhZGE0Mj\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/gojek/begin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}