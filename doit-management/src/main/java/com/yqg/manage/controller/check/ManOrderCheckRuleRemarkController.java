package com.yqg.manage.controller.check;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.check.ManOrderCheckRuleRemarkService;
import com.yqg.manage.service.check.request.OrderCheckBase;
import com.yqg.service.user.service.UserRiskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.MediaType;

/**
 * @author alan
 */
@RestController
@H5Request
@Api(tags = "订单审核规则管理")
public class ManOrderCheckRuleRemarkController {

    @Autowired
    private ManOrderCheckRuleRemarkService manOrderCheckRuleRemarkService;

    @Autowired
    private UserRiskService userRiskService;

    @ApiOperation("通过订单编号查询审核规则历史记录信息")
    @RequestMapping(value = "/manage/manOrderCheckRuleInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<OrderCheckBase> manOrderCheckRuleInfo(@RequestBody OrderCheckBase orderListSearchResquest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manOrderCheckRuleRemarkService.manOrderCheckRuleInfo(orderListSearchResquest));
    }

    @ApiOperation("通过订单编号添加修改审核规则历史记录表")
    @RequestMapping(value = "/manage/manOrderCheckRuleEdit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> editManOrderCheckRule(@RequestBody OrderCheckBase orderListSearchResquest)
            throws Exception {
        this.manOrderCheckRuleRemarkService.editManOrderCheckRule(orderListSearchResquest);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("通过订单编号判断是否命中人脸识别")
    @RequestMapping(value = "/manage/isUserHitRuleForVerifyScore", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> isUserHitRuleForVerifyScore(@RequestBody OrderCheckBase request)
            throws Exception {

        return ResponseEntitySpecBuilder.success(userRiskService.isUserHitRuleForVerifyScore(request.getOrderNo()));
    }

    @ApiOperation("通过订单编号判断是否命中有保险卡规则")
    @RequestMapping(value = "/manage/isUserHitRuleForInsuranceCard", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> isUserHitRuleForInsuranceCard(@RequestBody OrderCheckBase request)
            throws Exception {

        return ResponseEntitySpecBuilder.success(manOrderCheckRuleRemarkService.onlyRealNameVerifyFailedWithInsuranceCard(request.getOrderNo()));
    }

    @ApiOperation("通过订单编号判断是否命中有家庭卡规则")
    @RequestMapping(value = "/manage/isUserHitRuleForHomeCard", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> isUserHitRuleForHomeCard(@RequestBody OrderCheckBase request)
            throws Exception {

        return ResponseEntitySpecBuilder.success(manOrderCheckRuleRemarkService.onlyRealNameVerifyFailedWithFamilyCard(request.getOrderNo()));
    }

    @ApiOperation("通过订单编号判断是否需要google电话")
    @RequestMapping(value = "/manage/isUserHitGooglePhone", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> isUserHitGooglePhone(@RequestBody OrderCheckBase request)
            throws Exception {

        return ResponseEntitySpecBuilder.success(manOrderCheckRuleRemarkService.isUserHitGooglePhone(request.getOrderNo()));
    }
}
