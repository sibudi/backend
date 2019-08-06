package com.yqg.controller.activity;

import com.yqg.ApiApplication;
import com.yqg.common.utils.RSAUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
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
public class Activity4AppControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void checkMyAccount() throws Exception {
//        String str = "{\"net_type\":null,\"system_version\":null,\"client_type\":null,\"channel_sn\":null,\"channel_name\":null,\"deviceId\":null,\"client_version\":null,\"resolution\":null,\"IPAdress\":null,\"sign\":null,\"timestamp\":null,\"sessionId\":null,\"userUuid\":null,\"mac\":null,\"wifimac\":null,\"lbsX\":null,\"lbsY\":null,\"deviceSysModel\":null,\"androidId\":null,\"ipadress\":null}\n";
        String str = "{\n" +
                "  \"deviceSysIncremental\" : \"1541248040\",\n" +
                "  \"IPAdress\" : \"192.168.0.87\",\n" +
                "  \"\" : \"\",\n" +
                "  \"deviceId\" : \"\",\n" +
                "  \"system_version\" : \"8.1.0\",\n" +
                "  \"channel_name\" : \"playStore\",\n" +
                "  \"deviceSysBrand\" : \"OPPO\",\n" +
                "  \"resolution\" : \"1424*720\",\n" +
                "  \"sessionId\" : \"0c5a59f8b8dd400a9f57e225b1c1cb22\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"FjYzRhODMxOGQ0ODNlNmMwMmM1N2JlZDEyNzAyMW\",\n" +
                "  \"client_version\" : \"1.6.1\",\n" +
                "  \"deviceSysProduct\" : \"CPH1803\",\n" +
                "  \"deviceSysModel\" : \"CPH1803\",\n" +
                "  \"timestamp\" : \"1553249947\",\n" +
                "  \"userUuid\" : \"68FB35EFD32C40EA8C6ABB3620BE27E4\",\n" +
                "  \"androidId\" : \"1f2a15fa166806bf\",\n" +
                "  \"client_type\" : \"android\",\n" +
                "  \"deviceSysManufacturer\" : \"OPPO\",\n" +
                "  \"channel_sn\" : \"10002\",\n" +
                "  \"deviceSysSdkVersion\" : \"27\",\n" +
                "  \"deviceSysId\" : \"OPM1.171019.026\",\n" +
                "  \"deviceSysType\" : \"user\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/activity/getAccountTop10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }

    @Test
    public void checkCaseout() throws Exception {
//        String str = "{\"net_type\":null,\"system_version\":null,\"client_type\":null,\"channel_sn\":null,\"channel_name\":null,\"deviceId\":null,\"client_version\":null,\"resolution\":null,\"IPAdress\":null,\"sign\":null,\"timestamp\":null,\"sessionId\":null,\"userUuid\":null,\"mac\":null,\"wifimac\":null,\"lbsX\":null,\"lbsY\":null,\"deviceSysModel\":null,\"androidId\":null,\"ipadress\":null}\n";
        String str = "{\n" +
                "  \"amount\":\"100\",\n" +
                "  \"channel\":\"2\",\n" +
                "  \"androidId\":\"1f2a15fa166806bf\",\n" +
                "  \"channel_sn\":\"10002\",\n" +
                "  \"channel_name\":\"playStore\",\n" +
                "  \"client_type\":\"android\",\n" +
                "  \"client_version\":\"1.6.1\",\n" +
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
                "  \"sessionId\":\"79466c3472bf496fa983cb783dc9db6b\",\n" +
                "  \"sign\":\"IwM2M4NGNmM2YyY2Q4YmU2NmIwMzk2YWVmZTcxZj\",\n" +
                "  \"system_version\":\"8.1.0\",\n" +
                "  \"timestamp\":\"1553503530\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/activity/caseout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }

    @Test
    public void checkGetMyAccount() throws Exception {
//        String str = "{\"net_type\":null,\"system_version\":null,\"client_type\":null,\"channel_sn\":null,\"channel_name\":null,\"deviceId\":null,\"client_version\":null,\"resolution\":null,\"IPAdress\":null,\"sign\":null,\"timestamp\":null,\"sessionId\":null,\"userUuid\":null,\"mac\":null,\"wifimac\":null,\"lbsX\":null,\"lbsY\":null,\"deviceSysModel\":null,\"androidId\":null,\"ipadress\":null}\n";
        String str = "{\n" +
                "  \"userUuid\":\"68FB35EFD32C40EA8C6ABB3620BE27E4\",\n" +
                "  \"androidId\":\"1f2a15fa166806bf\",\n" +
                "  \"channel_sn\":\"10002\",\n" +
                "  \"channel_name\":\"playStore\",\n" +
                "  \"client_type\":\"android\",\n" +
                "  \"client_version\":\"1.6.1\",\n" +
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
                "  \"sessionId\":\"79466c3472bf496fa983cb783dc9db6b\",\n" +
                "  \"sign\":\"c1NDk3NWY4MTkwY2QzNjIzMTU0MjNhNmNjYjQzMj\",\n" +
                "  \"system_version\":\"8.1.0\",\n" +
                "  \"timestamp\":\"1553503532\"" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/activity/getMyAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }

    @Test
    public void checkGetUsrActivityAccountList() throws Exception {
        String str = "{\n" +
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
                "    \"sessionId\":\"79466c3472bf496fa983cb783dc9db6b\",\n" +
                "    \"sign\":\"EwNzA1MmIzMjBhZTQzNzQwMTVkZTg4YmFlNzFjOD\",\n" +
                "    \"system_version\":\"8.1.0\",\n" +
                "    \"timestamp\":\"1553503845\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/activity/getUsrActivityAccountList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }

    @Test
    public void checkAddOrChangeBankCard() throws Exception {
//        String str = "{\"net_type\":null,\"system_version\":null,\"client_type\":null,\"channel_sn\":null,\"channel_name\":null,\"deviceId\":null,\"client_version\":null,\"resolution\":null,\"IPAdress\":null,\"sign\":null,\"timestamp\":null,\"sessionId\":null,\"userUuid\":null,\"mac\":null,\"wifimac\":null,\"lbsX\":null,\"lbsY\":null,\"deviceSysModel\":null,\"androidId\":null,\"ipadress\":null}\n";
        String str = "{\n" +
                "    \"bankCardName\":\"user\",\n" +
                "    \"bankCode\":\"BCA\",\n" +
                "    \"bankNumberNo\":\"585885595959595\",\n" +
                "    \"orderNo\":\"\",\n" +
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
                "    \"sessionId\":\"79466c3472bf496fa983cb783dc9db6b\",\n" +
                "    \"sign\":\"M1OWY5OWUyM2M3YmMxZTM5Y2I4NjNjYjRiZmY0OD\",\n" +
                "    \"system_version\":\"8.1.0\",\n" +
                "    \"timestamp\":\"1553503923\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/activity/addOrChangeBankCard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }
}