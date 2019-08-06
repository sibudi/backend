package com.yqg.controller.externalChannel;

import com.yqg.ApiApplication;
import com.yqg.common.annotations.H5Request;
import com.yqg.common.utils.RSAUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@H5Request
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApiApplication.class)
@WebIntegrationTest

public class CheetahUploadControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private InputStream is;
    private MockMvc mockMvc;



    @Test
    public void confirmLoanApplication() throws Exception {
//        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "excel.jpeg", "application/x-www-form-urlencoded", is);
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/cash-loan/v1/images").file(mockMultipartFile).contentType(MediaType.IMAGE_JPEG_VALUE))
//                .andExpect(MockMvcResultMatchers.status().is(200)).andReturn();
    }
}