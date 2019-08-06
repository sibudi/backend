package com.yqg.controller.user;

import com.yqg.common.enums.order.OrdStepTypeEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.ResponseEntity;
import com.yqg.common.models.builders.ResponseEntityBuilder;
import com.yqg.common.redis.RedisClient;
import com.yqg.service.user.response.BackupLinkmanResponse;
import com.yqg.service.user.response.LinkmanCheckResponse;
import com.yqg.service.user.response.UsrStudentInfoResponse;
import com.yqg.service.user.service.UserBackupLinkmanService;
import com.yqg.service.user.service.UserLinkManService;
import com.yqg.service.user.service.UsrCertificationService;
import com.yqg.service.user.service.UsrBaseInfoService;
import com.yqg.service.user.model.*;
import com.yqg.service.user.request.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by hyy on 2017/11/24.
 */
@RestController
@RequestMapping("/userBaseInfo")
@Slf4j
public class UsrBaseInfoController {

    @Autowired
    private UsrBaseInfoService usrBaseInfoService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private UsrCertificationService usrCertificationService;

    @Autowired
    private UserLinkManService userLinkManService;

    @Autowired
    private UserBackupLinkmanService userBackupLinkmanService;


    @ApiOperation("选择用户角色")
    @RequestMapping(value = "/rolesChoose", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> rolesChoose(HttpServletRequest request, @RequestBody UsrRolesRequest usrRolesRequest) throws ServiceException {
//        UserSessionUtil.filter(request,this.redisClient, usrRolesRequest);
//        log.info("选择用户角色request body {}", JsonUtils.serialize(usrRolesRequest));
        log.info("选择用户角色");
        usrBaseInfoService.rolesChoose(usrRolesRequest);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("反显用户身份信息")
    @RequestMapping(value = "/getIdentityInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UsrIdentityModel> getIdentityInfo(HttpServletRequest request, @RequestBody UsrIdentityInfoRequest identityInfoRequest) throws Exception {
//        UserSessionUtil.filter(request,this.redisClient,identityInfoRequest);
//        log.info("反显用户身份信息request body {}", JsonUtils.serialize(identityInfoRequest));
        log.info("反显用户身份信息");
        return ResponseEntityBuilder.success(usrBaseInfoService.getIdentityInfo(identityInfoRequest.getUserUuid(), false));
    }

    @ApiOperation("实名认证接口")
    @RequestMapping(value = "/advanceVerify", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> advanceVerify(HttpServletRequest request, @RequestBody UsrIdentityInfoRequest identityInfoRequest) throws Exception {
//        UserSessionUtil.filter(request,this.redisClient,identityInfoRequest);
//        log.info("实名认证接口request body {}", JsonUtils.serialize(identityInfoRequest));
        log.info("实名认证接口");
        return ResponseEntityBuilder.success(usrBaseInfoService.advanceVerify(identityInfoRequest));
    }

    @ApiOperation("更新用户身份信息")
    @RequestMapping(value = "/getAndUpdateUser", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> getAndUpdateUser(HttpServletRequest request, @RequestBody UsrIdentityInfoRequest identityInfoRequest) throws Exception {
//        UserSessionUtil.filter(request,this.redisClient,identityInfoRequest);
//        log.info("更新用户身份信息request body {}", JsonUtils.serialize(identityInfoRequest));
        log.info("更新用户身份信息");
        usrBaseInfoService.getAndUpdateUser(identityInfoRequest);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("保存用户照片")
    @RequestMapping(value = "/saveUserPhoto", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> saveUserPhoto(HttpServletRequest request, @RequestBody SaveUserPhotoRequest saveUserPhotoRequest) throws Exception {
//        UserSessionUtil.filter(request,this.redisClient,saveUserPhotoRequest);
//        log.info("保存用户照片 request body {}", JsonUtils.serialize(saveUserPhotoRequest));
        log.info("保存用户照片");
        usrBaseInfoService.saveUserPhoto(saveUserPhotoRequest);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("反显 工作人员 基本信息")
    @RequestMapping(value = "/getWorkBaseInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UsrWorkBaseInfoModel> getWorkBaseInfo(HttpServletRequest request, @RequestBody UsrWorkBaseInfoRequest workBaseInfoRequest) throws Exception {
//        UserSessionUtil.filter(request,this.redisClient,workBaseInfoRequest);
//        log.info("反显 工作人员 基本信息request body {}", JsonUtils.serialize(workBaseInfoRequest));
        log.info("反显 工作人员 基本信息");
        return ResponseEntityBuilder.success(usrBaseInfoService.getWorkBaseInfo(workBaseInfoRequest.getUserUuid()));
    }

    @ApiOperation("添加或更新 工作人员 基本信息")
    @RequestMapping(value = "/addWorkBaseInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> addWorkBaseInfo(HttpServletRequest request, @RequestBody UsrWorkBaseInfoRequest workBaseInfoRequest) throws Exception {
        log.info("添加或更新 工作人员 基本信息");
        usrBaseInfoService.addWorkBaseInfo(workBaseInfoRequest);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("反显 工作人员 工作信息")
    @RequestMapping(value = "/getUsrWorkInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UsrWorkInfoModel> getUsrWorkInfo(HttpServletRequest request, @RequestBody UsrWorkInfoRequest usrWorkInfoRequest) throws Exception {
        log.info("反显 工作人员 工作信息");
        return ResponseEntityBuilder.success(usrBaseInfoService.getUsrWorkInfo(usrWorkInfoRequest.getUserUuid()));
    }

    @ApiOperation("添加或更新 工作人员 工作信息")
    @RequestMapping(value = "/addUsrWorkInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> addUsrWorkInfo(HttpServletRequest request, @RequestBody UsrWorkInfoRequest usrWorkInfoRequest) throws Exception {
        log.info("添加或更新 工作人员 工作信息");
        usrBaseInfoService.addUsrWorkInfo(usrWorkInfoRequest);
        return ResponseEntityBuilder.success();
    }


    @ApiOperation("反显 家庭主妇 基本信息")
    @RequestMapping(value = "/getHousewifekBaseInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UsrWorkBaseInfoModel> getHousewifekBaseInfo(HttpServletRequest request, @RequestBody UsrWorkBaseInfoRequest workBaseInfoRequest) throws Exception {
        log.info("反显 家庭主妇 基本信息");
        return ResponseEntityBuilder.success(usrBaseInfoService.getHousewifekBaseInfo(workBaseInfoRequest.getUserUuid()));
    }

    @ApiOperation("添加或更新 家庭主妇 基本信息")
    @RequestMapping(value = "/addHousewifeBaseInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> addHousewifeBaseInfo(HttpServletRequest request, @RequestBody UsrWorkBaseInfoRequest workBaseInfoRequest) throws Exception {
        log.info("添加或更新 家庭主妇 基本信息");
        usrBaseInfoService.addHousewifeBaseInfo(workBaseInfoRequest);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("反显 家庭主妇 家庭信息)")
    @RequestMapping(value = "/getUsrHousewifeInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UsrHouseWifeInfoModel> getUsrHousewifeInfo(HttpServletRequest request, @RequestBody UsrHousewifeRequest usrHousewifeRequest) throws Exception {
        log.info("反显 家庭主妇 家庭信息");
        return ResponseEntityBuilder.success(usrBaseInfoService.getUsrHousewifeInfo(usrHousewifeRequest.getUserUuid()));
    }

    @ApiOperation("添加或更新 家庭主妇 家庭信息")
    @RequestMapping(value = "/addUsrHousewifeInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> addUsrHousewifeInfo(HttpServletRequest request, @RequestBody UsrHousewifeRequest usrHousewifeRequest) throws Exception {
        log.info("添加或更新 家庭主妇 家庭信息");
        usrBaseInfoService.addUsrHousewifeInfo(usrHousewifeRequest);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("反显 联系人信息")
    @RequestMapping(value = "/getLinkManInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UsrLinkManModel> getLinkManInfo(HttpServletRequest request, @RequestBody UsrContactInfoRequest usrContactInfoRequest) throws Exception {
        log.info("反显 联系人信息");
        return ResponseEntityBuilder.success(usrBaseInfoService.getLinkManInfo(usrContactInfoRequest.getUserUuid()));
    }

    @ApiOperation("添加或修改 联系人信息")
    @RequestMapping(value = "/addLinkManInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> addLinkManInfo(HttpServletRequest request, @RequestBody UsrContactInfoRequest usrContactInfoRequest) throws Exception {
//        UserSessionUtil.filter(request,this.redisClient, usrContactInfoRequest);
//        log.info("添加或修改 联系人信息request body {}", JsonUtils.serialize(usrContactInfoRequest));
        log.info("添加或修改 联系人信息");
        usrBaseInfoService.addLinkManInfo(usrContactInfoRequest);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("反显 学生 基本信息")
    @RequestMapping(value = "/getStudentBaseInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UsrStudentBaseInfoModel> getStudentBaseInfo(HttpServletRequest request, @RequestBody UsrStudentBaseInfoRequest studentBaseInfoRequest) throws Exception {
//        UserSessionUtil.filter(request,this.redisClient,studentBaseInfoRequest);
//        log.info("反显 学生 基本信息request body {}", JsonUtils.serialize(studentBaseInfoRequest));
        log.info("反显 学生 基本信息");
        return ResponseEntityBuilder.success(usrBaseInfoService.getStudentBaseInfo(studentBaseInfoRequest.getUserUuid()));
    }

    @ApiOperation("添加或更新 学生 基本信息")
    @RequestMapping(value = "/addOrUpdateStudentBaseInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UsrStudentInfoResponse> addOrUpdateStudentBaseInfo(HttpServletRequest request, @RequestBody UsrStudentBaseInfoRequest studentBaseInfoRequest) throws Exception {
//        UserSessionUtil.filter(request,this.redisClient,studentBaseInfoRequest);
//        log.info("添加或更新 学生 基本信息request body {}", JsonUtils.serialize(studentBaseInfoRequest));
        log.info("添加或更新 学生 基本信息");
        return ResponseEntityBuilder.success(usrBaseInfoService.addOrUpdateStudentBaseInfo(studentBaseInfoRequest));
    }

    @ApiOperation("反显 学生 学校信息")
    @RequestMapping(value = "/getStudentSchoolInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<UsrSchoolInfoModel> getStudentSchoolInfo(HttpServletRequest request, @RequestBody UsrSchoolInfoRequest usrSchoolInfoRequest) throws Exception {
//        UserSessionUtil.filter(request,this.redisClient, usrSchoolInfoRequest);
//        log.info("反显 学生 学校信息request body {}", JsonUtils.serialize(usrSchoolInfoRequest));
        log.info("反显 学生 学校信息");
        return ResponseEntityBuilder.success(usrBaseInfoService.getStudentSchoolInfo(usrSchoolInfoRequest.getUserUuid()));
    }

    @ApiOperation("添加或修改 学生 学校信息")
    @RequestMapping(value = "/addOrUpdateStudentSchoolInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> addOrUpdateStudentSchoolInfo(HttpServletRequest request, @RequestBody UsrSchoolInfoRequest usrSchoolInfoRequest) throws Exception {
//        UserSessionUtil.filter(request,this.redisClient, usrSchoolInfoRequest);
//        log.info("添加或修改 学生 学校信息request body {}", JsonUtils.serialize(usrSchoolInfoRequest));
        log.info("添加或修改 学生 学校信息");
        usrBaseInfoService.addOrUpdateStudentSchoolInfo(usrSchoolInfoRequest);
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("查询认证状态并判断是否更新订单步骤")
    @RequestMapping(value = "/getCertificationInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> getCertificationInfo(HttpServletRequest request, @RequestBody UsrSubmitCerInfoRequest userSubmitCerInfoRequest) throws Exception {
//        UserSessionUtil.filter(request,this.redisClient,userSubmitCerInfoRequest);
//        log.info("查询认证状态并判断是否更新订单步骤request body {}", JsonUtils.serialize(userSubmitCerInfoRequest));
        log.info("查询认证状态并判断是否更新订单步骤");
        return ResponseEntityBuilder.success(usrBaseInfoService.getCertificationInfo(userSubmitCerInfoRequest));
    }

    @ApiOperation("归档测试")
    @RequestMapping(value = "/getOrderSave", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> getOrderSave(HttpServletRequest request, @RequestBody UsrSubmitCerInfoRequest userSubmitCerInfoRequest) throws Exception {
//        UserSessionUtil.filter(request,this.redisClient,userSubmitCerInfoRequest);
//        log.info("归档测试request body {}", JsonUtils.serialize(userSubmitCerInfoRequest));
        log.info("归档测试");
        usrBaseInfoService.savaOrderInfoToMango(userSubmitCerInfoRequest.getUserUuid(),userSubmitCerInfoRequest.getOrderNo());
        return ResponseEntityBuilder.success();
    }

    @ApiOperation("判断是否认证成功")
    @RequestMapping(value = "/getSuccessCertification", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Object> getSuccessCertification(HttpServletRequest request, @RequestBody UsrSubmitCerInfoRequest userSubmitCerInfoRequest) throws Exception {
//        UserSessionUtil.filter(request,this.redisClient,userSubmitCerInfoRequest);
//        log.info("判断是否认证成功request body {}", JsonUtils.serialize(userSubmitCerInfoRequest));
        log.info("判断是否认证成功");
        usrCertificationService.getSuccessCertificationByUserUuidAndType(userSubmitCerInfoRequest.getUserUuid(),userSubmitCerInfoRequest.getCertificationType());
        return ResponseEntityBuilder.success();
    }

    /***
     * 第一步添加联系人
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/linkman/step1-add", method = RequestMethod.POST)
    public ResponseEntity<Object> addLinkManInfoStepOne(@RequestBody LinkManRequest request) throws Exception {
        userLinkManService.addEmergencyLinkmans(request, false);
        return ResponseEntityBuilder.success();
    }

    /***
     * 第二步添加联系人
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/linkman/step2-add", method = RequestMethod.POST)
    public ResponseEntity<BackupLinkmanResponse> addLinkManInfStep2(@RequestBody LinkManRequest request) throws Exception {
        BackupLinkmanResponse backupResponse = userLinkManService.addEmergencyLinkmans(request, false);
        //更新订单步骤到联系人信息
        usrBaseInfoService.updateOrderStep(request.getOrderNo(), request.getUserUuid(), OrdStepTypeEnum.CONTACT_INFO.getType());
        //userLinkManService.sendAutoCallRequestForLinkman(request.getOrderNo(), request.getUserUuid());
        return ResponseEntityBuilder.success(backupResponse);
    }

    /**
     * 确认备选联系人
     * @param request Belum melebihi jumlah batas hari penolakan!
     * @return
     */
    @RequestMapping(value = "/backupLinkman/confirmation", method = RequestMethod.POST)
    public ResponseEntity<Object> confirmBackupLinkman(@RequestBody BackupLinkmanRequest request){
        userBackupLinkmanService.confirmBackupLinkman(request);
        return ResponseEntityBuilder.success();
    }


    /**
     * 反显联系人
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/linkman/check", method = RequestMethod.POST)
    public ResponseEntity<LinkmanCheckResponse> checkLinkman(@RequestBody LinkManCheckRequest request) {
        return ResponseEntityBuilder.success(userLinkManService.getLinkmanCheckResult(request));
    }


}