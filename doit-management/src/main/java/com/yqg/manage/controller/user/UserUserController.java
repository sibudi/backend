package com.yqg.manage.controller.user;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.PageData;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.manage.service.collection.request.ManCollectionRemarkRequest;
import com.yqg.manage.service.order.ChangeOrderService;
import com.yqg.manage.service.order.request.ManualRepayOrderRequest;
import com.yqg.manage.service.user.UserFeedBackService;
import com.yqg.manage.service.user.UserUserService;
import com.yqg.manage.service.user.request.ManUserUserRequest;
import com.yqg.manage.service.user.request.ManUsrFeedBackRemarkRequest;
import com.yqg.user.entity.UsrFeedBack;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author alan
 */
@RestController
@H5Request
@Api(tags = "?????")
public class UserUserController {
    @Autowired
    private UserUserService userUserService;

    @Autowired
    private UserFeedBackService userFeedBackService;

    @Autowired
    private ChangeOrderService changeOrderService;

    @ApiOperation("通过用户uuid查询掩码手机号和未掩码手机号")
    @RequestMapping(value = "/manage/userMobileByUuid", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> userMobileByUuid(@RequestBody ManUserUserRequest roleRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.userUserService.userMobileByUuid(roleRequest));
    }

    @ApiOperation("查询用户反馈列表")
    @RequestMapping(value = "/manage/userFeedBackList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<PageData<List<UsrFeedBack>>> userFeedBackList(HttpServletRequest request, @RequestBody ManUserUserRequest dataRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.userFeedBackService.getUserFeedBackList(dataRequest));
    }

    @ApiOperation("导出用户反馈列表Excel")
    @RequestMapping(value = "/manage/userFeedBackExcel")
    @ResponseBody
    public ResponseEntitySpec<Object> userFeedBackExcel(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        this.userFeedBackService.getUserFeedBackListExcel(request,response);
        return ResponseEntitySpecBuilder.success();
    }
    @ApiOperation("用户反馈列表添加备注")
    @RequestMapping(value = "/manage/remark2UserFeedBack")
    @ResponseBody
    public ResponseEntitySpec<Object> userFeedBackExcel(@RequestBody ManUsrFeedBackRemarkRequest remarkRequest)
            throws Exception {
        this.userFeedBackService.updateRemark2UserFeedBack(remarkRequest);
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("通过手机号修改客户姓名")
    @RequestMapping(value = "/manage/getUserListByMobile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public  ResponseEntitySpec<Object>  getUserListByMobile(@RequestBody ManUserUserRequest userUserRequest)
            throws Exception{
        return ResponseEntitySpecBuilder.success(this.userUserService.getUserListByMobile(userUserRequest));
    }

    @ApiOperation("线下还款")
    @RequestMapping(value = "/manage/manualOperationRepayOrder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> manualOperationRepayOrder(@RequestBody ManualRepayOrderRequest repayOrderRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.changeOrderService.manualOperationRepayOrder(repayOrderRequest));
    }

    @ApiOperation("用户加入黑名单")
    @RequestMapping(value = "/manage/setUserDisabled", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> setUserDisabled(@RequestBody ManualRepayOrderRequest repayOrderRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.userUserService.setUserDisabled(repayOrderRequest));
    }


    @ApiOperation("本人备用和公司联系人")
    @RequestMapping(value = "/manage/getBaseMobile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> getBaseMobile(@RequestBody ManualRepayOrderRequest repayOrderRequest)
        throws  Exception {
        return ResponseEntitySpecBuilder.success(this.userUserService.getBaseMobile(repayOrderRequest));
    }

    @ApiOperation("获得备用联系人信息")
    @RequestMapping(value = "/manage/listBaseMobileOnOtherContract", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> listBaseMobileOnOtherContract(@RequestBody ManualRepayOrderRequest repayOrderRequest)
            throws  Exception {
        return ResponseEntitySpecBuilder.success(this.userUserService.listBaseMobileOnOtherContract(repayOrderRequest));
    }

    @ApiOperation("获得联系人信息")
    @RequestMapping(value = "/manage/listUserMobileContract", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> listUserMobileContract(@RequestBody ManualRepayOrderRequest repayOrderRequest)
            throws  Exception {
        return ResponseEntitySpecBuilder.success(this.userUserService.listUserMobileContract(repayOrderRequest));
    }

    @ApiOperation("新增联系人催收")
    @RequestMapping(value = "/manage/insertManCollectionRemark", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> insertManCollectionRemark(@RequestBody ManCollectionRemarkRequest request)
            throws  Exception {
        return ResponseEntitySpecBuilder.success(this.userUserService.insertManCollectionRemark(request));
    }

    @ApiOperation("根据 还款码 和  订单号 查询还款状态")
    @RequestMapping(value = "/manage/listpayDeposit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> listpayDeposit(@RequestBody ManualRepayOrderRequest repayOrderRequest)
            throws  Exception {
        return ResponseEntitySpecBuilder.success(this.userUserService.listpayDeposit(repayOrderRequest));
    }

}
