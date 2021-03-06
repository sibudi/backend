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

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ManageApplication.class)
@WebIntegrationTest
public class SysRoleControllerTest {


    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void sysRoleAdd() throws Exception {

        String str = "{\n" +
                "  \"roleName\n\":\"123\",\n" +
                "  \"remark\":\"1\",\n" +
                "  \"permissionIds\n\":\"1\",\n" +
                "  \"status\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/sysRoleAdd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void sysRoleList() throws Exception {

        String str = "{\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/sysRoleList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void rolePermissionCheckList() throws Exception {

        String str = "{\n" +
                "  \"id\n\":\"123\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/rolePermissionCheckList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void sysRoleEdit() throws Exception {

        String str = "{\n" +
                "  \"id\n\":\"123\",\n" +
                "  \"roleName\":\"1\",\n" +
                "  \"remark\n\":\"1\",\n" +
                "  \"permissionIds\":\"1\",\n" +
                "  \"status\":\"1\",\n" +
                "}";


        ResultActions resultActions = mockMvc.perform(post("/manage/sysRoleEdit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

}