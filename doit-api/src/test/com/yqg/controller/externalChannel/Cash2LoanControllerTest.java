package com.yqg.controller.externalChannel;

import com.yqg.ApiApplication;
import com.yqg.common.annotations.H5Request;
import com.yqg.common.utils.RSAUtils;
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
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@H5Request
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest
public class Cash2LoanControllerTest {


    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void checkUserLoanable() throws Exception {
        String str = "{\"data\": \n\t{\n\t\n\t\t\"partner_key\":\"123\",\n\t\t\"en_data\":\"123\"\n\n\t}\n}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/external/cash2/loanable")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }

    @Test
    public void confirmLoanApplication() throws Exception {
        String str = "{\"data\": \n\t{\n\t\n\t\t\"partner_key\":\"123\",\n\t\t\"en_data\":\"123\"\n\n\t}\n}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/external/cash2/loanable")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }
    @Test
    public void getProductConfig() throws Exception {
        String str = "{\"data\": \n\t{\n\t\n\t\t\"partner_key\":\"123\",\n\t\t\"en_data\":\"123\"\n\n\t}\n}";
        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/external/cash2//application-confirmation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }

    @Test
    public void getDetailProductInfo() throws Exception {
        String str = "{\"data\": \n\t{\n\t\n\t\t\"partner_key\":\"123\",\n\t\t\"en_data\":\"123\"\n\n\t}\n}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/external/cash2/getDetailProductInfo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }
}