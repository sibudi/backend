package com.yqg.manage.controller.system;


import com.yqg.ManageApplication;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ManageApplication.class)
@WebIntegrationTest
public class ManSystemFunctionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static String requestStr;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void testCheakUserMobileNoWithDecryption() throws Exception {
        requestStr = "{mobileNumber：\"426264277\"}";
        ResultActions resultActions = mockMvc.perform(post("/manage/cheakUserMobileNoWithDecryption")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void testEncryptUserMobile() throws Exception {
        requestStr = "{mobileNumber：\"426264277\"}";
        ResultActions resultActions = mockMvc.perform(post("/manage/encryptUserMobile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void testDownloadUserMobile() throws Exception {
        requestStr = "";
        ResultActions resultActions = mockMvc.perform(post("/manage/downloadUserMobile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDownloadUserMobile1() throws Exception {
        requestStr = "";
        ResultActions resultActions = mockMvc.perform(post("/manage/addBlackListByFile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void testSendSmsBatch() throws Exception {
        requestStr = "";
        ResultActions resultActions = mockMvc.perform(post("/manage/sendSmsBatch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSendCodeToEmail() throws Exception {
        requestStr = "";
        ResultActions resultActions = mockMvc.perform(post("/manage/sendCodeToEmail")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    /*@Test
    public void testGetXmlFile() throws Exception {
        requestStr = "http://control2.uanguang.co.id/";
        ResultActions resultActions = mockMvc.perform(get("/manage/getExcelFile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testShowStreamOnBrowser() throws Exception {
        requestStr = "";
        ResultActions resultActions = mockMvc.perform(get("/manage/showStreamOnBrowser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }*/
}