package com.yqg.controller.user;

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
public class UsrBankControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void checkBankCardBin() throws Exception {
        String str = "{\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"timestamp\" : \"1553572619\",\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"bankCode\" : \"101\",\n" +
                "  \"bankNumberNo\" : \"123123\",\n" +
                "  \"bankCardName\" : \"fra\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"sign\" : \"IwYjIwN2JhZmRiOWFhM2Q2NjVlY2EyOTdhMGY2Y2\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBank/checkBankCardBin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getUserBankList() throws Exception {
        String str = "{\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"timestamp\" : \"1553572619\",\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"sign\" : \"IwYjIwN2JhZmRiOWFhM2Q2NjVlY2EyOTdhMGY2Y2\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBank/getUserBankList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void changeOrderBankCard() throws Exception {

        String str = "{\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"timestamp\" : \"1553572619\",\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"bankCode\" : \"101\",\n" +
                "  \"bankNumberNo\" : \"123123\",\n" +
                "  \"bankCardName\" : \"fra\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"sign\" : \"IwYjIwN2JhZmRiOWFhM2Q2NjVlY2EyOTdhMGY2Y2\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBank/checkBankCardBin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}