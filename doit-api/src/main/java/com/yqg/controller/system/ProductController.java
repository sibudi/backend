package com.yqg.controller.system;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.service.system.request.AddUserWhiteListRequest;
import com.yqg.service.system.request.PromoteUserProductLevelRequest;
import com.yqg.service.system.service.PromoteService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutorService;

/**
 * Created by wanghuaizhou on 2018/9/4.
 */
@H5Request
@RestController
@Slf4j
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private PromoteService promoteService;
    @Autowired
    private ExecutorService executorService;

    @ApiOperation("额度提升")
    @RequestMapping(value = "/promoteUserProductLevel",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntitySpec<Object> promoteUserProductLevel(HttpServletRequest request, @RequestBody PromoteUserProductLevelRequest promoteRequest) throws Exception {

        executorService
                .submit(() -> {
                    log.info("<=============>    开始额度提升   <=================> ");
                    this.promoteService.promoteUser(promoteRequest);

                });
        return ResponseEntitySpecBuilder.success();
    }


    @ApiOperation("添加分期产品白名单")
    @RequestMapping(value = "/addUserWhiteList",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntitySpec<Object> addUserWhiteList(HttpServletRequest request, @RequestBody AddUserWhiteListRequest addUserWhiteListRequest) throws Exception {

        this.promoteService.addUserToWhiteList(addUserWhiteListRequest);
        return ResponseEntitySpecBuilder.success();
    }
}
