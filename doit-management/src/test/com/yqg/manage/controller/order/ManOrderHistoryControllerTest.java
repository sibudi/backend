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
public class ManOrderHistoryControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static String requestStr;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        requestStr = "{\n" +
                "    \"amountApply\": \"\",\n" +
                "    \"applyTime\": \"\",\n" +
                "    \"borrowingTerm\": \"10\",\n" +
                "    \"channel\": \"5\",\n" +
                "    \"createBeginTime\": \"2019-02-11\",\n" +
                "    \"createEndTime\": \"2019-04-12\",\n" +
                "    \"isAgain\": \"\",\n" +
                "    \"isRepeatBorrowing\": \"1\",\n" +
                "    \"mobile\": \"1412351151\",\n" +
                "    \"pageNo\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"realName\": \"123\",\n" +
                "    \"status\": \"SECOND_CHECK\",\n" +
                "    \"updateBeginTime\": \"2019-04-11\",\n" +
                "    \"updateEndTime\": \"2019-04-12\",\n" +
                "    \"userRole\": \"WORKING_STAFF\",\n" +
                "    \"uuid\": \"dingdanbianhao\"\n" +
                "}";
    }

    @Test
    public void testOrderSchedule() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/orderSchedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}