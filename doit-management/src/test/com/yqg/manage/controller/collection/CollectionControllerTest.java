package com.yqg.manage.controller.collection;

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
public class CollectionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getAllCollectionsByThirdType() throws Exception {

        String str = "{\n" +
                "  \"isThird\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/collectors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getCurrentCollectors() throws Exception {
        String str = "{\n" +
                "  \"sourceType\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/post-collectors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getRestCurrentCollectors() throws Exception {
        String str = "{\n" +
                "  \"sourceType\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/post-rest-collectors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getCollectorsByPostId() throws Exception {
        String str = "{\n" +
                "  \"isThird\":\"1\",\n" +
                "  \"sourceType\":\"1\",\n" +
                "  \"postId\":\"13311\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/current-collectors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getCollectorPostList() throws Exception {
        String str = "{\n" +
                "  \"dicCode\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/collector-post-list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void scheduleCollectors() throws Exception {
        String str = "{\n" +
                "  \"postId\":\"\",\n" +
                "  \"staff\":\"1\",\n" +
                "  \"sourceType\":\"13311\",\n" +
                "  \"rest\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/collector-scheduling")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getAssignableD0OrderList() throws Exception {
        String str = "{\n" +
                "  \"amountApply\":\"1000\",\n" +
                "  \"isAgain\":\"1\",\n" +
                "  \"isAssigned\":\"13311\",\n" +
                "  \"isRepeatBorrowing\":\"1\",\n" +
                "  \"orderTag\":\"\",\n" +
                "  \"outsourceId\":\"1\",\n" +
                "  \"overdueDayMax\":\"13311\",\n" +
                "  \"overdueDayMin\":\"1\",\n" +
                "  \"pageNo\":\"\",\n" +
                "  \"pageSize\":\"1\",\n" +
                "  \"thirdDistribute\":\"13311\",\n" +
                "  \"uuid\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/orders/assignable-D0-list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getAssignableOverdueOrderList() throws Exception {
        String str = "{\n" +
                "  \"amountApply\":\"1000\",\n" +
                "  \"isAgain\":\"1\",\n" +
                "  \"isAssigned\":\"13311\",\n" +
                "  \"isRepeatBorrowing\":\"1\",\n" +
                "  \"orderTag\":\"\",\n" +
                "  \"outsourceId\":\"1\",\n" +
                "  \"overdueDayMax\":\"13311\",\n" +
                "  \"overdueDayMin\":\"1\",\n" +
                "  \"pageNo\":\"\",\n" +
                "  \"pageSize\":\"1\",\n" +
                "  \"thirdDistribute\":\"13311\",\n" +
                "  \"uuid\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection//orders/assignable-overdue-list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void assignCollectionOrder() throws Exception {
        String str = "{\n" +
                "  \"outsourceId\":\"16\",\n" +
                "  \"sourceType\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/order-assignment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void payDayOrderCount() throws Exception {
        String str = "{\n" +
                "  \"amountApply\":\"\",\n" +
                "  \"borrowingTerm\":\"1\",\n" +
                "  \"collectiorId\":\"13311\",\n" +
                "  \"createBeginTime\":\"1\",\n" +
                "  \"createEndTime\":\"\",\n" +
                "  \"isRepeatBorrowing\":\"1\",\n" +
                "  \"maxOverdueDays\":\"13311\",\n" +
                "  \"minOverdueDays\":\"1\",\n" +
                "  \"mobile\":\"\",\n" +
                "  \"orderTag\":\"1\",\n" +
                "  \"outsourceId\":\"13311\",\n" +
                "  \"pageNo\":\"1\",\n" +
                "  \"pageSize\":\"\",\n" +
                "  \"payDay\":\"1\",\n" +
                "  \"realName\":\"13311\",\n" +
                "  \"uuid\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/outCollectionsByOutSourceIdList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void outCollectionsByOutSourceIdList() throws Exception {
        String str = "{\n" +
                "  \"amountApply\":\"\",\n" +
                "  \"borrowingTerm\":\"1\",\n" +
                "  \"collectiorId\":\"13311\",\n" +
                "  \"createBeginTime\":\"1\",\n" +
                "  \"createEndTime\":\"\",\n" +
                "  \"isRepeatBorrowing\":\"1\",\n" +
                "  \"maxOverdueDays\":\"13311\",\n" +
                "  \"minOverdueDays\":\"1\",\n" +
                "  \"mobile\":\"\",\n" +
                "  \"orderTag\":\"1\",\n" +
                "  \"outsourceId\":\"13311\",\n" +
                "  \"pageNo\":\"1\",\n" +
                "  \"pageSize\":\"\",\n" +
                "  \"payDay\":\"1\",\n" +
                "  \"realName\":\"13311\",\n" +
                "  \"uuid\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/payDayOrderCount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void manOrderRemarkList() throws Exception {
        String str = "{\n" +
                "  \"uuid\":\"123123\",\n" +

                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/manOrderRemarkList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void repaymentOrderList() throws Exception {
        String str = "{\n" +
                "  \"actualPaymentEndDate\":\"\",\n" +
                "  \"actualPaymentStartDate\":\"1\",\n" +
                "  \"amountApply\":\"13311\",\n" +
                "  \"borrowingTerm\":\"1\",\n" +
                "  \"isRepeatBorrowing\":\"\",\n" +
                "  \"maxOverdueDays\":\"1\",\n" +
                "  \"minOverdueDays\":\"13311\",\n" +
                "  \"mobile\":\"1\",\n" +
                "  \"pageNo\":\"\",\n" +
                "  \"pageSize\":\"1\",\n" +
                "  \"realName\":\"13311\",\n" +
                "  \"userRole\":\"1\",\n" +
                "  \"uuid\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/repaymentOrderList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void repaymentOrderByOutSourceIdList() throws Exception {
        String str = "{\n" +
                "  \"postId\":\"123\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/collection/getScoreReport")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}