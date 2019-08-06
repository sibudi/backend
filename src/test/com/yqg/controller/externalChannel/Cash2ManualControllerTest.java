package com.yqg.controller.externalChannel;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class Cash2ManualControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void ordStatusFeedbackManual() throws Exception {
//        String str = "{\n" +
//                "  \"orderNo\" : \"123\",\n" +
//                "  \"orderStatus\" : \"123\"\n" +
//                "}";
//
//        String enStr = RSAUtils.encryptString(str);
//        ResultActions resultActions = mockMvc.perform(post("/cash2/manual/ordStatusFeedbackManual")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(enStr));
//
//        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
//        resultActions.andDo(print());
    }

    @Test
    public void ordCheckResultFeedbackManual() throws Exception {
//        String str = "{\n" +
//                "  \"orderNo\" : \"123\",\n"+
//                "  \"orderStatus\" : \"123\"\n" +
//                "}";
//
//        String enStr = RSAUtils.encryptString(str);
//        ResultActions resultActions = mockMvc.perform(post("/cash2/manual/ordCheckResultFeedbackManual")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(str));
//
//        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
//        resultActions.andDo(print());
    }
}