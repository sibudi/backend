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
public class CheetahBaseInfoControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void orderInfo()throws Exception {
        String str = "{\"data\": \n\t{\n\t\n\t\t\"partner_key\":\"123\",\n\t\t\"en_data\":\"123\"\n\n\t}\n}";

        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/cash-loan/v1/orders/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }
    @Test
    public void infoEdit()throws Exception {
        String str = "{\n    \"orderInfo\":{\n    \t\"id\":\"123\"\n   \n    },\n    \"userInfo\": {\n    \t\"fullName\":\"test\",\n    \t\"idNumber\":\"123\",\n    \t\"idFrontPhoto\":\"123\",\n    \t\"handHeldPhoto\":\"123\"\n    \t\n    },\n    \"bankInfo\":{\n    \t\"accountNumber\":\"123\",\n    \t\"bankCode\":\"13\"\n    \t\n    }\n    \n}";
        String enStr = RSAUtils.encryptString(str);
        ResultActions resultActions = mockMvc.perform(post("/cash-loan/v1/orders/info-edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(str));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andDo(print());
    }
}