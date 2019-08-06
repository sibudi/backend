package com.yqg.manage.controller.user;

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
public class UserUserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static String requestStr;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        requestStr = "";
    }

    @Test
    public void testUserMobileByUuid() throws Exception {
        requestStr = "{\"userUuid\": \"33547896631\"}";
        ResultActions resultActions = mockMvc.perform(post("/manage/userMobileByUuid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUserFeedBackList() throws Exception {
        requestStr = "{\"userUuid\": \"33547896631\"}";
        ResultActions resultActions = mockMvc.perform(post("/manage/userFeedBackList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUserFeedBackExcel() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/userFeedBackExcel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUserFeedBackExcel1() throws Exception {
        requestStr = "{\n" +
                "    \"uuid\": \"12456988774\",\n" +
                "    \"operatorName\": \"android\",\n" +
                "    \"userId\": \"1223664\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/remark2UserFeedBack")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetUserListByMobile() throws Exception {
        requestStr = "{\n" +
                "    \"mobile\": \"\",\n" +
                "    \"realName\": \"\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/getUserListByMobile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testManualOperationRepayOrder() throws Exception {
        requestStr = "{\n" +
                "    \"operatorId\": \"123456\",\n" +
                "    \"actualRepayTime\": \"\",\n" +
                "    \"orderNo\": \"\",\n" +
                "    \"userUuid\": \"\",\n" +
                "    \"actualRepayAmout\": \"\",\n" +
                "    \"promiseDate\": \"\",\n" +
                "    \"promiseTime\": \"\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/manualOperationRepayOrder")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSetUserDisabled() throws Exception {
        requestStr = "{\n" +
                "    \"operatorId\": \"123456\",\n" +
                "    \"actualRepayTime\": \"\",\n" +
                "    \"orderNo\": \"\",\n" +
                "    \"userUuid\": \"\",\n" +
                "    \"actualRepayAmout\": \"\",\n" +
                "    \"promiseDate\": \"\",\n" +
                "    \"promiseTime\": \"\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/setUserDisabled")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetBaseMobile() throws Exception {
        requestStr = "{\n" +
                "    \"operatorId\": \"123456\",\n" +
                "    \"actualRepayTime\": \"\",\n" +
                "    \"orderNo\": \"\",\n" +
                "    \"userUuid\": \"\",\n" +
                "    \"actualRepayAmout\": \"\",\n" +
                "    \"promiseDate\": \"\",\n" +
                "    \"promiseTime\": \"\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/getBaseMobile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testListBaseMobileOnOtherContract() throws Exception {
        requestStr = "{\n" +
                "    \"operatorId\": \"123456\",\n" +
                "    \"actualRepayTime\": \"\",\n" +
                "    \"orderNo\": \"\",\n" +
                "    \"userUuid\": \"\",\n" +
                "    \"actualRepayAmout\": \"\",\n" +
                "    \"promiseDate\": \"\",\n" +
                "    \"promiseTime\": \"\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/listBaseMobileOnOtherContract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testListUserMobileContract() throws Exception {
        requestStr = "{\n" +
                "    \"operatorId\": \"123456\",\n" +
                "    \"actualRepayTime\": \"\",\n" +
                "    \"orderNo\": \"\",\n" +
                "    \"userUuid\": \"\",\n" +
                "    \"actualRepayAmout\": \"\",\n" +
                "    \"promiseDate\": \"\",\n" +
                "    \"promiseTime\": \"\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/listUserMobileContract")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testInsertManCollectionRemark() throws Exception {
        requestStr = "{\n" +
                "    \"userUuid\": \"33854795\",\n" +
                "    \"orderNo\": \"115456654\",\n" +
                "    \"contactType\": \"dsadsad\",\n" +
                "    \"contactMobile\": \"2124456468\",\n" +
                "    \"createUser\":\"\",\n" +
                "    \"pdateUser\":\"\"   \n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/listpayDeposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testListpayDeposit() throws Exception {
        requestStr = "{\n" +
                "    \"operatorId\": \"123456\",\n" +
                "    \"actualRepayTime\": \"\",\n" +
                "    \"orderNo\": \"\",\n" +
                "    \"userUuid\": \"\",\n" +
                "    \"actualRepayAmout\": \"\",\n" +
                "    \"promiseDate\": \"\",\n" +
                "    \"promiseTime\": \"\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/listpayDeposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}