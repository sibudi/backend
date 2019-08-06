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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class SystemCompatibleRSAControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void isUpdate() throws Exception{
        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.45\",\n" +
                "  \"sessionId\" : \"cb05a703a11e432297435f47ce90bf47\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"U2YjFmZmMxNDBlMzI5OWRhYjA1ZWVhNzUxMDMxMj\",\n" +
                "  \"timestamp\" : \"1553742326\",\n" +
                "  \"appType\" : 1\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/system/isUpdate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }

    @Test
    public void signup() throws Exception {
        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.45\",\n" +
                "  \"sessionId\" : \"cb05a703a11e432297435f47ce90bf47\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"U2YjFmZmMxNDBlMzI5OWRhYjA1ZWVhNzUxMDMxMj\",\n" +
                "  \"timestamp\" : \"1553742326\",\n" +
                "  \"mobileNumber\" : \"81381361242\",\n" +
                "  \"smsCode\" : \"1234\",\n" +
                "  \"userUuid\" : \"1553742326\",\n" +
                "  \"role\" : \"1\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());

    }

    @Test
    public void smsCode() throws Exception {
        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.45\",\n" +
                "  \"sessionId\" : \"cb05a703a11e432297435f47ce90bf47\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"U2YjFmZmMxNDBlMzI5OWRhYjA1ZWVhNzUxMDMxMj\",\n" +
                "  \"timestamp\" : \"1553742326\",\n" +
                "  \"mobileNumber\" : \"81381361242\",\n" +
                "  \"smsType\" : \"REGISTER\",\n" +
                "  \"idCardNo\" : \"1553742326\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/system/smsCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }

    @Test
    public void logout() throws Exception{
        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.45\",\n" +
                "  \"sessionId\" : \"cb05a703a11e432297435f47ce90bf47\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"U2YjFmZmMxNDBlMzI5OWRhYjA1ZWVhNzUxMDMxMj\",\n" +
                "  \"timestamp\" : \"1553742326\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }

    @Test
    public void smsAutoLogin() throws Exception{
        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.45\",\n" +
                "  \"sessionId\" : \"cb05a703a11e432297435f47ce90bf47\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"U2YjFmZmMxNDBlMzI5OWRhYjA1ZWVhNzUxMDMxMj\",\n" +
                "  \"timestamp\" : \"1553742326\",\n" +
                "  \"mobileNumber\" : \"81381361242\",\n" +
                "  \"smsCode\" : \"1234\",\n" +
                "  \"userUuid\" : \"1553742326\",\n" +
                "  \"role\" : \"1\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/users/smsAutoLogin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }
}