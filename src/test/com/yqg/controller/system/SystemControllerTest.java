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
public class SystemControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void isUpdate() throws Exception {
        String str = "{\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"timestamp\" : \"1553666697\",\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"appType\" : \"1\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"IPAdress\" : \"192.168.0.44\",\n" +
                "  \"sessionId\" : \"cb05a703a11e432297435f47ce90bf47\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"sign\" : \"dlMzc4OWJlYjMxNzUzYmE5MzM3MDYxNzJkYWMwYT\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/system/isUpdate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getUrlList() throws Exception {

        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.44\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"NkODIwMmYzODc1ZGFlOGJkOGZjOGE2YTM2OGY1Yj\",\n" +
                "  \"timestamp\" : \"1553667581\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/system/appH5UrlValueList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getBankInfo() throws Exception {

        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"hhNGI5MzJmZDRlYmUxMTg2MGNhMTFiZTBmMmJiOW\",\n" +
                "  \"timestamp\" : \"1553591778\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/system/getBankInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getPaymentChannelList() throws Exception {

        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"sign\" : \"hhNGI5MzJmZDRlYmUxMTg2MGNhMTFiZTBmMmJiOW\",\n" +
                "  \"timestamp\" : \"1553591778\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/system/getBankInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void isUploadUserApps() throws Exception {

        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.44\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"NkODIwMmYzODc1ZGFlOGJkOGZjOGE2YTM2OGY1Yj\",\n" +
                "  \"timestamp\" : \"1553667581\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/system/isUploadUserApps")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void dicItemListByDicCode() throws Exception {

        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"dicCode\" : \"INCOME_PERSON\",\n" +
                "  \"sign\" : \"hhNGI5MzJmZDRlYmUxMTg2MGNhMTFiZTBmMmJiOW\",\n" +
                "  \"timestamp\" : \"1553591778\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/system/getDicItemListByDicCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getShareData() throws Exception {

        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"hhNGI5MzJmZDRlYmUxMTg2MGNhMTFiZTBmMmJiOW\",\n" +
                "  \"timestamp\" : \"1553591778\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/system/getShareData")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getShareQRCode() throws Exception {

        String str = "{\n" +
                "  \"userUuid\":\"68FB35EFD32C40EA8C6ABB3620BE27E4\",\n" +
                "  \"androidId\":\"1f2a15fa166806bf\",\n" +
                "  \"channel_sn\":\"10002\",\n" +
                "  \"channel_name\":\"playStore\",\n" +
                "  \"client_type\":\"android\",\n" +
                "  \"client_version\":\"1.6.2\",\n" +
                "  \"deviceId\":\"\",\n" +
                "  \"deviceSysBrand\":\"OPPO\",\n" +
                "  \"deviceSysId\":\"OPM1.171019.026\",\n" +
                "  \"deviceSysIncremental\":\"1541248040\",\n" +
                "  \"deviceSysManufacturer\":\"OPPO\",\n" +
                "  \"deviceSysModel\":\"CPH1803\",\n" +
                "  \"deviceSysProduct\":\"CPH1803\",\n" +
                "  \"deviceSysSdkVersion\":27,\n" +
                "  \"deviceSysType\":\"user\",\n" +
                "  \"IPAdress\":\"192.168.0.87\",\n" +
                "  \"net_type\":\"wifi\",\n" +
                "  \"resolution\":\"1424*720\",\n" +
                "  \"sessionId\":\"9f2a24420d46447bb83a135a3d789f1d\",\n" +
                "  \"sign\":\"I3Y2MzYzE4MDc0MDVmZTVhNDgwYjA1NTZmMjA3MW\",\n" +
                "  \"system_version\":\"8.1.0\",\n" +
                "  \"timestamp\":\"1553669767\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/system/getShareQRCode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getSchoolList() throws Exception {

        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"nameStr\" : \"ab\",\n" +
                "  \"sign\" : \"hhNGI5MzJmZDRlYmUxMTg2MGNhMTFiZTBmMmJiOW\",\n" +
                "  \"timestamp\" : \"1553591778\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/system/getSchoolList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}