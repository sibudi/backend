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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class UsrQuestionnaireH5ControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    public void insertQuestionnaire() throws Exception{
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
                "  \"postName\" : \"hjkl\",\n" +
                "  \"onlineOrNot\" : \"1\",\n" +
                "  \"isEnough\" : \"1\",\n" +
                "  \"sales\" : \"1234\",\n" +
                "  \"loanAmout\" : \"12000\",\n" +
                "  \"loanterm\" : \"12\",\n" +
                "  \"hasOtherProduce\" : \"1\",\n" +
                "  \"otherProduceName\" : \"1234\",\n" +
                "  \"workInterestingOrNot\" : \"1\",\n" +
                "  \"type\" : 1\n" +
                "}";

        //String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/web/questionnaire/insertQuestionnaire")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(status().isOk());
        resultActions.andDo(print());
    }

    @Test
    public void uploadQuestionnaireAttach() throws Exception{
        
    }

    @Test
    public void insertQuestionnaireAttach() throws Exception{
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

        //String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/web/questionnaire/insertQuestionnaireAttach")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(status().isOk());
        resultActions.andDo(print());
    }
}