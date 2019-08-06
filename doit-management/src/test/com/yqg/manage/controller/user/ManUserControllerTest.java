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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ManageApplication.class)
@WebIntegrationTest
public class ManUserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static String requestStr;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        requestStr = "{\n" +
                "    \"mobile\": \"\",\n" +
                "    \"username\": \"\",\n" +
                "    \"thirdPlatform\": \"\",\n" +
                "    \"realname\": \"\",\n" +
                "    \"remark\": \"\",\n" +
                "    \"voicePhone\": \"\",\n" +
                "    \"roleIds\": [],\n" +
                "    \"status\": true,\n" +
                "    \"third\": \"\",\n" +
                "    \"collectionPhone\": \"\",\n" +
                "    \"employeeNumber\": \"\",\n" +
                "    \"collectionWa\": \"\"\n" +
                "}";
    }

    @Test
    public void testSysLogin() throws Exception {
        requestStr = "{\n" +
                "    \"username\": \"kuraki\",\n" +
                "    \"password\": \"123456\",\n" +
                "    \"third\": 1\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/sysLogin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Test
    public void testSysLoginOut() throws Exception {
        requestStr = "";
        ResultActions resultActions = mockMvc.perform(post("/manage/sysLoginOut")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSysUserAdd() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/sysUserAdd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSysUserEdit() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/sysUserEdit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSysUserPass() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/sysUserPass")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testResetPassword() throws Exception {

        requestStr = "{\n" +
                "    \"id\": 38447941,\n" +
                "    \"username\": \"kuraki\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/manusr/passwordReset")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSysUserList() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/sysUserList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetSessionIdByName() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/getSessionIdByName")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testManUserListByRemark() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/manUserListByRemark")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testManUserList() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/allManUserList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetReviewers() throws Exception {
        requestStr = "";
        ResultActions resultActions = mockMvc.perform(get("/manage/reviewers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testAddReviewerScheduler() throws Exception {
        requestStr = "{\n" +
                "    \"postEnglishName\": \"kuraki\",\n" +
                "    \"reviewerIds\": \"2255435864\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/reviewers/scheduler")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetReviewersByPostName() throws Exception {
        requestStr = "{postName:url}";
        ResultActions resultActions = mockMvc.perform(post("/manage/all-post-reviewers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetCurrentReviewersByPostName() throws Exception {
        requestStr = "{\n" +
                "    \"reviewerId\": \"\",\n" +
                "    \"uuid\": \"\",\n" +
                "    \"orderTag\": \"\",\n" +
                "    \"outsourceId\": \"\",\n" +
                "    \"thirdDistribute\": \"\",\n" +
                "    \"isAgain\": \"\",\n" +
                "    \"isTestOrder\": \"\",\n" +
                "    \"createBeginTime\": \"\",\n" +
                "    \"createEndTime\": \"\",\n" +
                "    \"amountApply\": \"\",\n" +
                "    \"borrowingTerm\": \"\",\n" +
                "    \"applyTime\": \"\",\n" +
                "    \"channel\": \"\",\n" +
                "    \"mobile\": \"\",\n" +
                "    \"realName\": \"\",\n" +
                "    \"userRole\": \"\",\n" +
                "    \"updateBeginTime\": \"\",\n" +
                "    \"updateEndTime\": \"\",\n" +
                "    \"isAssignment\": \"\",\n" +
                "    \"pageNo\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"status\": \"12133\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/current-reviewers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testJudgeUserName() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/manuser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testForceChangePassword() throws Exception {
        requestStr="{\n" +
                "    \"oldPassword\": \"123456\",\n" +
                "    \"newPassword\": \"854381\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/forceChangePassword.json")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testAddParentId() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/addOrDeleteParentId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testListParentId() throws Exception {
        requestStr = "";
        ResultActions resultActions = mockMvc.perform(post("/manage/listParentId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}