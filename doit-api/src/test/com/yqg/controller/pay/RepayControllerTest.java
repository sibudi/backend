package com.yqg.controller.pay;

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
public class RepayControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void repayment() throws Exception {
        String str = "{\n" +
                "    \"orderNo\":\"011903221706409271\",\n" +
                "    \"repaymentChannel\":\"9\",\n" +
                "    \"repaymentCode\":\"\",\n" +
                "    \"principal\":\"1500000\",\n" +
                "    \"repaymentType\":\"4\",\n" +
                "    \"transactionId\":\"\",\n" +
                "    \"type\":\"1\",\n" +
                "    \"androidId\":\"1f2a15fa166806bf\",\n" +
                "    \"channel_sn\":\"10002\",\n" +
                "    \"channel_name\":\"playStore\",\n" +
                "    \"client_type\":\"android\",\n" +
                "    \"client_version\":\"1.6.1\",\n" +
                "    \"deviceId\":\"\",\n" +
                "    \"deviceSysBrand\":\"OPPO\",\n" +
                "    \"deviceSysId\":\"OPM1.171019.026\",\n" +
                "    \"deviceSysIncremental\":\"1541248040\",\n" +
                "    \"deviceSysManufacturer\":\"OPPO\",\n" +
                "    \"deviceSysModel\":\"CPH1803\",\n" +
                "    \"deviceSysProduct\":\"CPH1803\",\n" +
                "    \"deviceSysSdkVersion\":27,\n" +
                "    \"deviceSysType\":\"user\",\n" +
                "    \"IPAdress\":\"192.168.0.87\",\n" +
                "    \"net_type\":\"wifi\",\n" +
                "    \"resolution\":\"1424*720\",\n" +
                "    \"sessionId\":\"29881d15f5e44fcbb7ffbd3d9ebcae2e\",\n" +
                "    \"sign\":\"EzY2RkNTJjY2Y2MDgzZWNmNzRmYzVlOGRhYWZjY2\",\n" +
                "    \"system_version\":\"8.1.0\",\n" +
                "    \"timestamp\":\"1553568253\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/repay/repayment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}