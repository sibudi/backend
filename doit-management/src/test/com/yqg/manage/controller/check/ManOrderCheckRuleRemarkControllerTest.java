package com.yqg.manage.controller.check;

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

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ManageApplication.class)
@WebIntegrationTest
public class ManOrderCheckRuleRemarkControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void manOrderCheckRuleInfo() throws Exception {

        String str = "{\n" +
                "  \"infoType\":\"1\",\n" +
                "  \"orderNo\":\"1351513\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/manOrderCheckRuleInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void editManOrderCheckRule() throws Exception {

        String str = "{\n" +
                "  \"checkRuleRemark\":\"13311\",\n" +
                "  \"orderNo\":\"1351513\",\n" +
                "  \"checkRuleList\":\"13311\",\n" +
                "  \"infoType\":\"1\",\n" +
                "  \"sessionId\":\"13311\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/manOrderCheckRuleEdit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void isUserHitRuleForVerifyScore() throws Exception {

        String str = "{\n" +
                "  \"orderNo\":\"1351513\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/isUserHitRuleForVerifyScore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void isUserHitRuleForInsuranceCard() throws Exception {

        String str = "{\n" +
                "  \"orderNo\":\"1351513\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/isUserHitRuleForInsuranceCard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void isUserHitRuleForHomeCard() throws Exception {

        String str = "{\n" +
                "  \"orderNo\":\"1351513\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/isUserHitRuleForHomeCard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void isUserHitGooglePhone() throws Exception {

        String str = "{\n" +
                "  \"infoType\":\"1\",\n" +
                "  \"orderNo\":\"1351513\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/isUserHitGooglePhone")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}