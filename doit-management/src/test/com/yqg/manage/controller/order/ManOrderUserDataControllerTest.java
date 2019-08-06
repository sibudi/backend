package com.yqg.manage.controller.order;


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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ManageApplication.class)
@WebIntegrationTest
public class ManOrderUserDataControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static String requestStr;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        requestStr = "{\n" +
                "    \"userUuid\": 3388668774664,\n" +
                "    \"type\": 2,\n" +
                "    \"langue\": 1\n" +
                "}";
    }

    @Test
    public void testGetOrderUserDataMongo() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/orderUserDataMongo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetOrderUserDataSql() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/orderUserDataSql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetPairVerifySimilarity() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/getPairVerifySimilarity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}