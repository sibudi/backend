package com.yqg.manage.controller.system;


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
public class ManAppVersionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private static String requestStr;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        requestStr = "{\n" +
                "    \"title\": \"\",\n" +
                "    \"memo\": \"\",\n" +
                "    \"versionNo\": \"\",\n" +
                "    \"isForce\": false,\n" +
                "    \"leftButton\": \"\",\n" +
                "    \"rightButton\": \"\",\n" +
                "    \"appType\": \"\",\n" +
                "    \"sysType\": \"\",\n" +
                "    \"downloadAddress\": \"\",\n" +
                "    \"status\": true\n" +
                "}";
    }

    @Test
    public void testAddAppVersion() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/appVersionAdd")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testAppVersionList() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/appVersionList")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testEditVersion() throws Exception {
        ResultActions resultActions = mockMvc.perform(post("/manage/appVersionEdit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestStr));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}