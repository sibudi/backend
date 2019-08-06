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
public class ManOrderControllerTest {

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
    public void testAllOrderList() throws Exception {

        ResultActions resultActions = mockMvc.perform(post("/manage/AllOrderList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testOrderInfoByUuid() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/orderInfoByUuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testOrderHistoryListByUserUuid() throws Exception {

        ResultActions resultActions = mockMvc.perform(post("/manage/orderHistoryListByUserUuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetD0OrderList() throws Exception {

        requestStr = "{\n" +
                "borrowingTerm:\"14\"\n" +
                "channel:\"5\"\n" +
                "createBeginTime:\"2019-04-08T16:00:00.000Z\"\n" +
                "createEndTime:\"2019-04-11T16:00:00.000Z\"\n" +
                "endTime:\"\"\n" +
                "isRepeatBorrowing:\"1\"\n" +
                "mobile:\"11415151\"\n" +
                "orderNo:\"\"\n" +
                "orderTag:\"2\"\n" +
                "outsourceId:\"128\"\n" +
                "pageNo:1\n" +
                "pageSize:10\n" +
                "payChannelType:\"\"\n" +
                "realName:\"112\"\n" +
                "startTime:\"\"\n" +
                "status:\"RESOLVED_NOT_OVERDUE\"\n" +
                "type:\"\"\n" +
                "userName:\"\"\n" +
                "userRole:\"WORKING_STAFF\"\n" +
                "uuid:\"312415135\"" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/orders/D0-list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetOverdueOrderList() throws Exception {
        requestStr = "{\n" +
                "    \"borrowingTerm\": \"14\",\n" +
                "    \"channel\": \"5\",\n" +
                "    \"createBeginTime\": \"2019-04-08T16:00:00.000Z\",\n" +
                "    \"createEndTime\": \"2019-04-11T16:00:00.000Z\",\n" +
                "    \"endTime\": \"\",\n" +
                "    \"isRepeatBorrowing\": \"1\",\n" +
                "    \"mobile\": \"11415151\",\n" +
                "    \"orderNo\": \"\",\n" +
                "    \"orderTag\": \"2\",\n" +
                "    \"outsourceId\": \"128\",\n" +
                "    \"pageNo\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"payChannelType\": \"\",\n" +
                "    \"realName\": \"112\",\n" +
                "    \"startTime\": \"\",\n" +
                "    \"status\": \"RESOLVED_NOT_OVERDUE\",\n" +
                "    \"type\": \"\",\n" +
                "    \"userName\": \"\",\n" +
                "    \"userRole\": \"WORKING_STAFF\",\n" +
                "    \"uuid\": \"312415135\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/orders/overdue-list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetPaidOrderList() throws Exception {
        requestStr = "{\n" +
                "    \"amountApply\": \"\",\n" +
                "    \"applyTime\": \"\",\n" +
                "    \"borrowingTerm\": \"10\",\n" +
                "    \"channel\": \"2\",\n" +
                "    \"createBeginTime\": \"2019-03-31T16:00:00.000Z\",\n" +
                "    \"createEndTime\": \"2019-04-10T16:00:00.000Z\",\n" +
                "    \"dueDayEndTime\": \"2019-04-11T16:00:00.000Z\",\n" +
                "    \"dueDayStartTime\": \"2019-04-09T16:00:00.000Z\",\n" +
                "    \"isAgain\": \"\",\n" +
                "    \"isRepeatBorrowing\": \"1\",\n" +
                "    \"isTest\": \"1\",\n" +
                "    \"maxOverdueDays\": \"6\",\n" +
                "    \"minOverdueDays\": \"22\",\n" +
                "    \"mobile\": \"3651515\",\n" +
                "    \"orderTag\": \"\",\n" +
                "    \"overdueDayMax\": \"\",\n" +
                "    \"overdueDayMin\": \"\",\n" +
                "    \"pageNo\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"realName\": \"35235\",\n" +
                "    \"status\": \"3\",\n" +
                "    \"updateBeginTime\": \"\",\n" +
                "    \"updateEndTime\": \"\",\n" +
                "    \"userRole\": \"House_Wife\",\n" +
                "    \"uuid\": \"3516t631\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/orders/paid-list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetIssueFailedOrderList() throws Exception {
        requestStr = "{\n" +
                "    \"pageNo\": 1,\n" +
                "    \"pageSize\": 10\n" +
                "}";

        ResultActions resultActions = mockMvc.perform(post("/manage/orders/issueFailed-list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testInserOrUpdateTeleReviewRemark() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/inserOrUpdateManOrderRemark")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetPaymentProof() throws Exception {
        requestStr = "{\n" +
                "    \"userUuid\": 3386779455657138321,\n" +
                "    \"orderNo\": 20190401258747\n" +
                "}";

        ResultActions resultActions = mockMvc.perform(post("/manage/getPaymentProof")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCancleOrder() throws Exception {
        requestStr = "{\n" +
                "    \"userUuid\": 3386779455657138321,\n" +
                "    \"orderNo\": 20190401258747\n" +
                "}";

        ResultActions resultActions = mockMvc.perform(post("/manage/cancleOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());

    }
}