package com.yqg.manage.controller.review;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.review.AutoReviewConfigService;
import com.yqg.manage.service.review.request.AutoReviewRuleParam;
import com.yqg.manage.service.review.response.AutoReviewRuleResponse;
import com.yqg.manage.service.user.ManUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 ****/

@Api(tags = "auto-review-rules")
@RestController
@RequestMapping("/manage")
@H5Request
public class AuthReviewConfigController {

    @Autowired
    private AutoReviewConfigService autoReviewConfigService;

    @Autowired
    private ManUserService manUserService;

    @ApiOperation(value = "某一类型自动审核规则列表，Author: zengxiangcai", notes = "查询的时候传ruleType即可")
    @RequestMapping(value = "/auto-review-rules", method = RequestMethod.POST)
    public ResponseEntitySpec<List<AutoReviewRuleResponse>> getAutoReviewRuleConfigByType(
            @RequestBody
                    AutoReviewRuleParam param) {
        //close this method.
        return ResponseEntitySpecBuilder
                .success();
//        return ResponseEntitySpecBuilder
//                .success(autoReviewConfigService.getAutoReviewRuleConfigByType(param.getRuleType()));
    }

    @ApiOperation("编辑自动审核规则，Author: zengxiangcai")
    @RequestMapping(value = "/auto-review-rule", method = RequestMethod.POST)
    public ResponseEntitySpec<Object> editAutoReviewRule(
            @RequestParam("sessionId") String sessionId, @RequestBody
            AutoReviewRuleParam param) {
        return ResponseEntitySpecBuilder.success(autoReviewConfigService
                .editAutoReviewRule(manUserService.getUserIdBySession(sessionId), param));
    }

}
