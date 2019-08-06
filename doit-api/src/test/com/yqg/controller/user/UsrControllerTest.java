package com.yqg.controller.user;

import com.yqg.ApiApplication;
import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.RSAUtils;
import com.yqg.service.third.izi.util.Base64Util;
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

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class UsrControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void userFeedback() throws Exception {
        String str = "{\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"timestamp\" : \"1553484118\",\n" +
                "  \"deviceId\" : \"CEF94B4F-824A-482B-BD27-454D3662DAFA\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"feedBackContent\" : \"123123\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"feedBackImages\" : \"\\/MyUpload\\/DOIT\\/X\\/U\\/5fb1679e1bcda42be1c4390941eb49a6.png\",\n" +
                "  \"IPAdress\" : \"192.168.0.39\",\n" +
                "  \"sessionId\" : \"d8c5dbec232e4b35994506c2ac1042a9\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"sign\" : \"E5ZjE3NDVmMGNhMGE4ZDc1OWRkZTY2NDFhZGE0Mj\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/users/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void signup() throws Exception {
        String str = "{\n" +
                "  \"smsCode\" : \"8888\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"mobileNumber\" : \"81398644017\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"timestamp\" : \"1553500987\",\n" +
                "  \"userType\" : \"LOGIN\",\n" +
                "  \"deviceId\" : \"CEF94B4F-824A-482B-BD27-454D3662DAFA\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"IPAdress\" : \"192.168.0.39\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"sign\" : \"ZiYmI4NDViNTZlNGZkNDAzZTVjNThiNWUzMTZhND\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void smsAutoLogin(){
    }

    @Test
    public void logout() throws Exception{
        String str = "{\n" +
                "  \"deviceId\" : \"CEF94B4F-824A-482B-BD27-454D3662DAFA\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.39\",\n" +
                "  \"sessionId\" : \"d8bab68dcf4c48f383f4221a8bd10a02\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"IyNWY5NTBjNDMyNjZiZjI3YjgzYzUzNGQ4NGZkOD\",\n" +
                "  \"timestamp\" : \"1553501448\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void initCertificationView() {
    }

    @Test
    public void submitCertificationInfo() {
    }

    @Test
    public void submitSupplementInfo() throws Exception{
        String str = "{\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"timestamp\" : \"1553501762\",\n" +
                "  \"deviceId\" : \"CEF94B4F-824A-482B-BD27-454D3662DAFA\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"IPAdress\" : \"192.168.0.39\",\n" +
                "  \"sessionId\" : \"93ef54d2ed9d4b8c88c75fc9787c7846\",\n" +
                "  \"imageUrls\" : \"[\\n\\n]\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"sign\" : \"liMmJhMDI4NDg5YTA5N2VkZGU2YzgwMDE0NjEzMm\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/users/submitSupplementInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void initSupplementInfo() throws Exception{
        String str = "{\n" +
                "  \"deviceId\" : \"CEF94B4F-824A-482B-BD27-454D3662DAFA\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.39\",\n" +
                "  \"sessionId\" : \"93ef54d2ed9d4b8c88c75fc9787c7846\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"ZkZGY3ZTc3MzgzNDdjMDI4MTIwY2VkZGZjNWFiYj\",\n" +
                "  \"timestamp\" : \"1553501727\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/users/initSupplementInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void cheakUserRole() throws Exception{
        String str = "{\n" +
                "  \"deviceId\" : \"CEF94B4F-824A-482B-BD27-454D3662DAFA\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.39\",\n" +
                "  \"sessionId\" : \"93ef54d2ed9d4b8c88c75fc9787c7846\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"Y3N2Y0MjgwZmM2MmEwYzMyMDE1NmQxOWViMWM5OD\",\n" +
                "  \"timestamp\" : \"1553502078\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/users/cheakUserRole")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void insertCollectionScore() throws Exception{
        String str = "{\n" +
                "  \"deviceId\" : \"CEF94B4F-824A-482B-BD27-454D3662DAFA\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.39\",\n" +
                "  \"sessionId\" : \"93ef54d2ed9d4b8c88c75fc9787c7846\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"Y3N2Y0MjgwZmM2MmEwYzMyMDE1NmQxOWViMWM5OD\",\n" +
                "  \"timestamp\" : \"1553502078\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/users/insertCollectionScore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

}