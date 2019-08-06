package com.yqg.manage.controller.third;


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
public class PayControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static String requestStr;


    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        requestStr = "{\n" +
                "    \"pageNo\": 1,\n" +
                "    \"pageSize\": 10\n" +
                "}";
    }

    @Test
    public void balance() throws Exception {
        requestStr = "";
        ResultActions resultActions = mockMvc.perform(get("/manage/all/balance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void offline() throws Exception {
        requestStr = "";
        ResultActions resultActions = mockMvc.perform(get("/manage/fund/offline/all")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void action() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/fund/offline/balance/action")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}