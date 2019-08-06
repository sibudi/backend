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
public class UsrBaseInfoControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void rolesChoose() throws Exception {
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
                "  \"role\" : \"2\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"sign\" : \"IwYjIwN2JhZmRiOWFhM2Q2NjVlY2EyOTdhMGY2Y2\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/rolesChoose")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getIdentityInfo() throws Exception {
    }

    /**
     *
     * 实名认证
     * */
    @Test
    public void advanceVerify() throws Exception {
        String str = "{\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"idCardNo\" : \"1231231231231231\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"timestamp\" : \"1553590479\",\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"name\" : \"Tristan\",\n" +
                "  \"sign\" : \"lhMGE5OWQyNGFhZjc4MjRiZDM5ZTcxNzkxYjk3OT\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/advanceVerify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     *
     * 提交用户身份信息
     * */
    @Test
    public void getAndUpdateUser() throws Exception {
        String str = "{\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"idCardNo\" : \"1231231231231231\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"timestamp\" : \"1553590479\",\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"name\" : \"Tristan\",\n" +
                "  \"sex\" : \"1\",\n" +
                "  \"idCardPhoto\" : \"/MyUpload/ID_CARD/V/G/a0a2cf550c383617c827d3c57c44fa3e.jpg\",\n" +
                "  \"handIdCardPhoto\" : \"/MyUpload/ID_CARD/V/G/a0a2cf550c383617c827d3c57c44fa3e.jpg\",\n" +
                "  \"sign\" : \"lhMGE5OWQyNGFhZjc4MjRiZDM5ZTcxNzkxYjk3OT\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/getAndUpdateUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void saveUserPhoto() throws Exception {
        String str = "{\n" +
                "  \"photoType\":\"0\",\n" +
                "  \"photoUrl\":\"/MyUpload/ID_CARD/V/G/a0a2cf550c383617c827d3c57c44fa3e.jpg\",\n" +
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
                "  \"sessionId\":\"45af773f13aa4379af85c85ef7428bd4\",\n" +
                "  \"sign\":\"FjZjE2NGZlYWQ0NjAzNGRiMWY0N2E3NjA0MmFiZW\",\n" +
                "  \"system_version\":\"8.1.0\",\n" +
                "  \"timestamp\":\"1553590827\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/saveUserPhoto")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     *
     * 反显已工作人的基本信息
     * */
    @Test
    public void getWorkBaseInfo() throws Exception {
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
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/getWorkBaseInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     *
     * 添加已工作人的基本信息
     * */
    @Test
    public void addWorkBaseInfo() throws Exception {

        String str = "{\n" +
                "  \"religion\" : \"Kristen Katolik\",\n" +
                "  \"birthday\" : \"2017-01-15\",\n" +
                "  \"province\" : \"Banten\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"smallDirect\" : \"Kadu\",\n" +
                "  \"detailed\" : \"Qweqwewqeqwe\",\n" +
                "  \"lbsY\" : \"\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"city\" : \"Kabupaten Tangerang\",\n" +
                "  \"bigDirect\" : \"Curug\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"childrenAmount\" : \"4\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"borrowUse\" : \"Liburan\",\n" +
                "  \"sign\" : \"kwYWZhYjRlODQ0OWQ1ZjhhN2YyM2U0MTdjYmY4OG\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"addressType\" : \"0\",\n" +
                "  \"timestamp\" : \"1553591664\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"motherName\" : \"Sofia\",\n" +
                "  \"email\" : \"861009122@qq.com\",\n" +
                "  \"kkCardPhoto\" : \"\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"lbsX\" : \"\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"academic\" : \"Sekolah Menengah Atas\",\n" +
                "  \"insuranceCardPhoto\" : \"(null)\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/addWorkBaseInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     *
     * 反显已工作人的工作信息
     * */
    @Test
    public void getUsrWorkInfo() throws Exception {
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
                "  \"sign\" : \"FmYWYwMGRmMTg3MzUyM2VmYzI2YjQxNmJmZDAyOW\",\n" +
                "  \"timestamp\" : \"1553593525\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/getUsrWorkInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     *
     * 添加已工作人的工作信息
     * */
    @Test
    public void addUsrWorkInfo() throws Exception {
        String str = "{\n" +
                "  \"province\" : \"DKI JAKARTA\",\n" +
                "  \"employeeNumber\" : \"\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"smallDirect\" : \"Kebayoran Lama Utara\",\n" +
                "  \"detailed\" : \"Asdascadada\",\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"lbsY\" : \"\",\n" +
                "  \"monthlyIncome\" : \"123123\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"city\" : \"Kota Jakarta Selatan\",\n" +
                "  \"bigDirect\" : \"Kebayoran Lama\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"dependentBusiness\" : \"karyawan non-perusahaan\",\n" +
                "  \"sign\" : \"JhNmQwNjdlOWU3OGY0MzA5MjY0MGM4MmMwNTFhZT\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"addressType\" : \"1\",\n" +
                "  \"timestamp\" : \"1553593387\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"positionName\" : \"Petani\",\n" +
                "  \"companyName\" : \"Yajiada\",\n" +
                "  \"companyPhone\" : \"0512-12312312\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"lbsX\" : \"\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"extensionNumber\" : \"\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/addUsrWorkInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getHousewifekBaseInfo() throws Exception {
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
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/getHousewifekBaseInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void addHousewifeBaseInfo() throws Exception {
        String str = "{\n" +
                "  \"religion\" : \"Kristen Katolik\",\n" +
                "  \"birthday\" : \"2017-01-15\",\n" +
                "  \"province\" : \"Banten\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"smallDirect\" : \"Kadu\",\n" +
                "  \"detailed\" : \"Qweqwewqeqwe\",\n" +
                "  \"lbsY\" : \"\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"city\" : \"Kabupaten Tangerang\",\n" +
                "  \"bigDirect\" : \"Curug\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"childrenAmount\" : \"4\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"borrowUse\" : \"Liburan\",\n" +
                "  \"sign\" : \"kwYWZhYjRlODQ0OWQ1ZjhhN2YyM2U0MTdjYmY4OG\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"addressType\" : \"0\",\n" +
                "  \"timestamp\" : \"1553591664\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"motherName\" : \"Sofia\",\n" +
                "  \"email\" : \"861009122@qq.com\",\n" +
                "  \"kkCardPhoto\" : \"\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"lbsX\" : \"\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"academic\" : \"Sekolah Menengah Atas\",\n" +
                "  \"insuranceCardPhoto\" : \"(null)\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/addHousewifeBaseInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getUsrHousewifeInfo() throws Exception {

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
                "  \"sign\" : \"Q2MWU5MGM0M2RjM2QwZmZjZTY1YTBhM2JjYjlkMm\",\n" +
                "  \"timestamp\" : \"1553594991\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/getUsrHousewifeInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void addUsrHousewifeInfo() throws Exception {

        String str = "{\n" +
                "  \"incomeType\" : \"pengasuh bayi\",\n" +
                "  \"workType\" : \"karyawan non-perusahaan\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"sourceTel\" : \"81312331233\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"sourceName\" : \"Niulajsdkl\",\n" +
                "  \"incomeWithNoCom\" : \"Qweqweqweqwe\",\n" +
                "  \"homeMouthIncome\" : \"12312332\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"ZmMDc4OWQ5MjliOTNlNDZlM2QxNzA4ZmUxZGYyZD\",\n" +
                "  \"mouthIncome\" : \"12312\",\n" +
                "  \"incomtSource\" : \"Ibu\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"timestamp\" : \"1553594865\",\n" +
                "  \"isCompanyUser\" : 2,\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/addUsrHousewifeInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getLinkManInfo() throws Exception {
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
                "  \"sign\" : \"Q2MWU5MGM0M2RjM2QwZmZjZTY1YTBhM2JjYjlkMm\",\n" +
                "  \"timestamp\" : \"1553594991\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/getLinkManInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void addLinkManInfo() throws Exception {
        String str = "{\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"contactsName1\" : \"LiHai\",\n" +
                "  \"alternatePhoneNo\" : \"\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"contactsName2\" : \"LinLong\",\n" +
                "  \"timestamp\" : \"1553595726\",\n" +
                "  \"relation2\" : \"Saudara \\/ saudari\",\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"contactsMobile1\" : \"81380808080\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"contactsMobile2\" : \"81390909090\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"relation1\" : \"Pasangan\",\n" +
                "  \"sign\" : \"U3ODc1YjI3NmYxMDM5NzljOTg4ZDE1NDlmMTExZW\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/addLinkManInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getStudentBaseInfo() throws Exception {

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
                "  \"sign\" : \"RjZjk3MGRiMjlkZmVmY2EwM2E3MTdjMTg2NzRlY2\",\n" +
                "  \"timestamp\" : \"1553597692\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/getStudentBaseInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void addOrUpdateStudentBaseInfo() throws Exception {
        String str = "{\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"lbsY\" : \"\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"fatherPosition\" : \"Pekerja\",\n" +
                "  \"motherPosition\" : \"Pegawai negeri\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"classmateName\" : \"23423\",\n" +
                "  \"familyAnnualIncome\" : \"123123\",\n" +
                "  \"academic\" : \"Sekolah Menengah Pertama\",\n" +
                "  \"partTimeName\" : \"Tidak\",\n" +
                "  \"detailed\" : \"Qweqwewqeqwe\",\n" +
                "  \"familyMemberAmount\" : \"12\",\n" +
                "  \"addressType\" : \"0\",\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"email\" : \"861009122@qq.com\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"birthday\" : \"2013-02-26\",\n" +
                "  \"mouthCostSource\" : \"diberikan oleh orang tua\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"mouthCost\" : \"343234\",\n" +
                "  \"fatherMobile\" : \"81350505050\",\n" +
                "  \"dwellingCondition\" : \"0\",\n" +
                "  \"sign\" : \"NlMTE3YjVmNWQ5ZWVlYzFhN2MzYWRiZjg2NWFkOG\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"kkCardPhoto\" : \"http:\\/\\/image.uanguang.co.id<null>\",\n" +
                "  \"isPartTime\" : 2,\n" +
                "  \"fatherName\" : \"123\",\n" +
                "  \"lbsX\" : \"\",\n" +
                "  \"motherName\" : \"Add\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\",\n" +
                "  \"classmateTele\" : \"81990909090\",\n" +
                "  \"motherMobile\" : \"81360606060\",\n" +
                "  \"timestamp\" : \"1553596503\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/addOrUpdateStudentBaseInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getStudentSchoolInfo() throws Exception {

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
                "  \"sign\" : \"E3ZDJlNDY2N2ViNzU2ZGJkMDhkMjIyZGRlZjY1NT\",\n" +
                "  \"timestamp\" : \"1553599061\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/getStudentSchoolInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void addOrUpdateStudentSchoolInfo() throws Exception {

        String str = "{\n" +
                "  \"startSchoolDate\" : \"2016-02-26\",\n" +
                "  \"province\" : \"JAWA BARAT\",\n" +
                "  \"system_version\" : \"12.100000\",\n" +
                "  \"deviceId\" : \"63BC7C64-5935-442B-95BE-6187E1239216\",\n" +
                "  \"smallDirect\" : \"Cipetir\",\n" +
                "  \"detailed\" : \"123123123123\",\n" +
                "  \"lbsY\" : \"\",\n" +
                "  \"schoolName\" : \"Sekolah Tinggi Teknologi Pahlawan 12 Sungailiat, Sungailiat, Kabupaten Bangka\",\n" +
                "  \"major\" : \"12312312\",\n" +
                "  \"channel_name\" : \"APPStore\",\n" +
                "  \"city\" : \"Kabupaten Cianjur\",\n" +
                "  \"bigDirect\" : \"Cibeber\",\n" +
                "  \"studentNo\" : \"3123123123\",\n" +
                "  \"sessionId\" : \"82e793e83f7d440e8dc44ce2f8221532\",\n" +
                "  \"net_type\" : \"wifi\",\n" +
                "  \"sign\" : \"c3M2Y3NzAzMzAwZTZkYzQ3YzdhY2Q5MzFlOTY4MD\",\n" +
                "  \"client_version\" : \"1.0.5\",\n" +
                "  \"addressType\" : \"2\",\n" +
                "  \"timestamp\" : \"1553598972\",\n" +
                "  \"orderNo\" : \"011903261156483111\",\n" +
                "  \"studentCardUrl\" : \"\\/MyUpload\\/DOIT\\/Q\\/Q\\/febadf42bd5d7772fb6e33742b68c67c.png\",\n" +
                "  \"client_type\" : \"iOS\",\n" +
                "  \"lbsX\" : \"\",\n" +
                "  \"channel_sn\" : \"APPStore\",\n" +
                "  \"IPAdress\" : \"192.168.0.40\"\n" +
                "}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/userBaseInfo/addOrUpdateStudentSchoolInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(enStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     *
     * 此接口，暂时不用了
     * */
    @Test
    public void getCertificationInfo() throws Exception {

    }

    /**
     *
     * APP没调用此接口
     * */
    @Test
    public void getOrderSave() throws Exception {
    }

    @Test
    public void getSuccessCertification() throws Exception {
    }

    @Test
    public void addLinkManInfoStepOne() throws Exception {
    }

    @Test
    public void addLinkManInfStep2() throws Exception {
    }

    @Test
    public void confirmBackupLinkman() throws Exception {
    }

    @Test
    public void checkLinkman() throws Exception {
    }
}