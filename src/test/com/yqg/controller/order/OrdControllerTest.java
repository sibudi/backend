package com.yqg.controller.order;

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
public class OrdControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void toOrder() throws Exception {

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
        ResultActions resultActions = mockMvc.perform(post("/order/toOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getOrderList() throws Exception {

        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.44\",\n" +
                "  \"sessionId\" : \"cb05a703a11e432297435f47ce90bf47\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"k1MTk4YTgwMTQ4MWJiYjhhNmViMGVhMTUzMzkzNG\",\n" +
                "  \"timestamp\" : \"1553671239\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/order/getOrderList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getOrderRepayAmout() throws Exception {

        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.44\",\n" +
                "  \"sessionId\" : \"cb05a703a11e432297435f47ce90bf47\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"type\" : \"1\",\n" +
                "  \"sign\" : \"k1MTk4YTgwMTQ4MWJiYjhhNmViMGVhMTUzMzkzNG\",\n" +
                "  \"timestamp\" : \"1553671239\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/order/getOrderRepayAmout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getDelayOrderConfig() throws Exception {

        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.44\",\n" +
                "  \"sessionId\" : \"cb05a703a11e432297435f47ce90bf47\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"sign\" : \"k1MTk4YTgwMTQ4MWJiYjhhNmViMGVhMTUzMzkzNG\",\n" +
                "  \"timestamp\" : \"1553671239\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/order/getDelayOrderConfig")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void repayDelayOrder() throws Exception {

        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.44\",\n" +
                "  \"sessionId\" : \"cb05a703a11e432297435f47ce90bf47\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"repayNum\" : \"1000000\",\n" +
                "  \"delayDay\" : \"7\",\n" +
                "  \"sign\" : \"k1MTk4YTgwMTQ4MWJiYjhhNmViMGVhMTUzMzkzNG\",\n" +
                "  \"timestamp\" : \"1553671239\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/order/repayDelayOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getPaymentProof() throws Exception {

        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.44\",\n" +
                "  \"sessionId\" : \"cb05a703a11e432297435f47ce90bf47\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"sign\" : \"k1MTk4YTgwMTQ4MWJiYjhhNmViMGVhMTUzMzkzNG\",\n" +
                "  \"timestamp\" : \"1553671239\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/order/getPaymentProof")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void confirmLoan() throws Exception {

        String str = "{\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.44\",\n" +
                "  \"sessionId\" : \"cb05a703a11e432297435f47ce90bf47\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"sign\" : \"k1MTk4YTgwMTQ4MWJiYjhhNmViMGVhMTUzMzkzNG\",\n" +
                "  \"timestamp\" : \"1553671239\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/order/confirmation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void updateDeviceInfo() throws Exception {

        String str = "{\n" +
                "  \"bigDirect\":\"\",\n" +
                "  \"city\":\"\",\n" +
                "  \"detailed\":\"\",\n" +
                "  \"lbsY\":\"31.208913\",\n" +
                "  \"lbsX\":\"121.472219\",\n" +
                "  \"orderNo\":\"011903221453421140\",\n" +
                "  \"province\":\"\",\n" +
                "  \"smallDirect\":\"\",\n" +
                "  \"IPAdress\":\"192.168.0.87\",\n" +
                "  \"androidId\":\"1f2a15fa166806bf\",\n" +
                "  \"channel_name\":\"playStore\",\n" +
                "  \"channel_sn\":\"10002\",\n" +
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
                "  \"net_type\":\"wifi\",\n" +
                "  \"resolution\":\"1424*720\",\n" +
                "  \"sessionId\":\"7ec8997a69564652818f7ab2ca0c8b28\",\n" +
                "  \"sign\":\"FhNjM3ZTNmZTM4OTkzM2EwYTFlZmM4M2E1ZWM4ZT\",\n" +
                "  \"system_version\":\"8.1.0\",\n" +
                "  \"timestamp\":\"1553675156\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/order/order-address")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}