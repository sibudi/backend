package com.yqg.controller.h5;

import com.yqg.ApiApplication;
import com.yqg.common.annotations.H5Request;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class UserH5ControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void randomImage() throws Exception {
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
        ResultActions resultActions = mockMvc.perform(post("/web/users/randomImage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }

    @Test
    public void sendH5SmsCode() throws Exception{
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
                "  \"mobile\" : \"812381361242\",\n" +
                "  \"imgCode\" : \"1234\",\n" +
                "  \"imgKey\" : \"4AAQSkZJRgABAgAAAQABAAD\"\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/web/users/sendH5SmsCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }

    @Test
    public void register() throws Exception {
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
                "  \"mobile\" : \"812381361242\",\n" +
                "  \"code\" : \"1234\",\n" +
                "  \"codeKey\" : \"1234\",\n" +
                "  \"channel\" : \"1\"\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/web/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }

    @Test
    public void inviteSignup() throws Exception{
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
                "  \"mobileNumber\" : \"812381361242\",\n" +
                "  \"smsCode\" : \"1234\",\n" +
                "  \"userUuid\" : \"1234\",\n" +
                "  \"role\" : 1,\n" +
                "  \"smsKey\" : \"1234\",\n" +
                "  \"realName\" : \"hghghgh\",\n" +
                "  \"userSource\" : \"1311\",\n" +
                "  \"invite\" : \"1\"\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/web/users/inviteSignup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }

    @Test
    public void getRepayRate() throws Exception{
        String str = "";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(get("/web/users/getRepayRate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());

    }
}