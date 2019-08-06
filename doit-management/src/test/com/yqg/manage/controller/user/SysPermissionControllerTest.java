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
public class SysPermissionControllerTest {

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
    public void testPermissionItemAdd() throws Exception {
        requestStr = "{\n" +
                "    \"permissionCode\": \"\",\n" +
                "    \"permissionName\": \"\",\n" +
                "    \"permissionUrl\": \"/\",\n" +
                "    \"parentId\": \"0\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/permissionItemAdd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testPermissionItemEdit() throws Exception {
        requestStr = "{\n" +
                "    \"permissionCode\": \"\",\n" +
                "    \"permissionName\": \"\",\n" +
                "    \"permissionUrl\": \"/\",\n" +
                "    \"parentId\": \"0\"\n" +
                "}";
        ResultActions resultActions = mockMvc.perform(post("/manage/permissionItemEdit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testPermissionList() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/permissionList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testPermissionTreeByUserId() throws Exception {
        requestStr = "{id: \"1232\"}";
        ResultActions resultActions = mockMvc.perform(post("/manage/permissionTreeByUserId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testPermissionTreeInnByUserId() throws Exception {
        requestStr = "{id: \"1232\"}";
        ResultActions resultActions = mockMvc.perform(post("/manage/permissionTreeInnByUserId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}