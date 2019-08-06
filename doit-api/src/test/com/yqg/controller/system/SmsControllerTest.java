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
public class SmsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void smsCode() throws Exception {
        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"smsType\" : \"REGISTER\",\n" +
                "  \"mobileNumber\" : \"81398644017\",\n" +
                "  \"sign\" : \"hhNGI5MzJmZDRlYmUxMTg2MGNhMTFiZTBmMmJiOW\",\n" +
                "  \"timestamp\" : \"1553591778\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/upload/uploadContacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}