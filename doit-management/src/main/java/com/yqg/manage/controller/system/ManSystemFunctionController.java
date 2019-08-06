package com.yqg.manage.controller.system;

import com.yqg.common.annotations.H5Request;
import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.common.models.builders.ResponseEntitySpecBuilder;
import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.StringUtils;
import com.yqg.manage.service.system.ManSystemFunctionService;
import com.yqg.manage.service.system.request.CheakMobileNoRequest;
import com.yqg.manage.service.system.request.CheckBalanceRequest;
import com.yqg.manage.util.ShowStreamUtils;
import com.yqg.service.risk.service.BlackListManageService;
import com.yqg.service.third.upload.UploadService;
import com.yqg.service.third.upload.response.UploadResultInfo;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.File;

/**
 * Created by Didit Dwianto on 2018/1/7.
 */
@RestController
@H5Request
public class ManSystemFunctionController {

    private Logger logger = LoggerFactory.getLogger(ManSystemFunctionController.class);

    @Autowired
    private ManSystemFunctionService manSystemFunctionService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private BlackListManageService blackListManageService;

    @Value("${downlaod.filePath}")
    private String filePath;

    @ApiOperation("将用户加密手机号脱敏")
    @RequestMapping(value = "/manage/cheakUserMobileNoWithDecryption", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> cheakUserMobileNoWithDecryption(HttpServletRequest request, @RequestBody CheakMobileNoRequest cheakMobileNoRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manSystemFunctionService.cheakUserMobileNunberWithDecryption(cheakMobileNoRequest));
    }

    @ApiOperation("将用户加密手机号加密")
    @RequestMapping(value = "/manage/encryptUserMobile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    @ResponseBody
    public ResponseEntitySpec<Object> encryptUserMobile(HttpServletRequest request, @RequestBody CheakMobileNoRequest cheakMobileNoRequest)
            throws Exception {
        return ResponseEntitySpecBuilder.success(this.manSystemFunctionService.encryptUserMobile(cheakMobileNoRequest));
    }

    @ApiOperation("批量手机号码脱敏")
    @RequestMapping(value = "/manage/downloadUserMobile", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntitySpec<Object> downloadUserMobile(@RequestParam MultipartFile file,
                                                         @RequestParam String type,
                                                         @RequestParam String userId,
                                                      @RequestParam String sessionId) throws Exception {

        File resultFile = new File(filePath + "phoneNums--" + userId + ".csv");
        logger.info("resultFile ====" + resultFile);
        if (!resultFile.exists()) {
            resultFile.createNewFile();
        }
        File fileWriter=new File(manSystemFunctionService.downloadUserMobile(type,file));
        FileUtils.copyFile(fileWriter, resultFile);

        UploadResultInfo uploadResultInfo =
                uploadService.uploadFile(sessionId, filePath + "phoneNums--" + userId + ".csv");
        return ResponseEntitySpecBuilder.success(uploadResultInfo.getData());
    }

    @ApiOperation("文件添加黑名单")
    @RequestMapping(value = "/manage/addBlackListByFile", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntitySpec<Object> downloadUserMobile(@RequestParam MultipartFile file) throws Exception {
        logger.info("start for addBlackListByFile");
        blackListManageService.addFraudAndSensitityUser(file);
        logger.info("end for addBlackListByFile");
        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("只有固定的IP用户能够下载文件")
    @RequestMapping(value = "/manage/getXmlFile", method = RequestMethod.GET)
//    @ResponseBody
    public void getXmlFile(HttpServletRequest request, HttpServletResponse response) throws Exception {

        manSystemFunctionService.getXmlFile(request, response);
//        return ResponseEntitySpecBuilder.success();
    }

    @ApiOperation("批量发送用户短信")
    @RequestMapping(value = "/manage/sendSmsBatch", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntitySpec<Object> sendSmsBatch(@RequestParam MultipartFile file,
                                                       @RequestParam String channel,
                                                       @RequestParam String smsContent,
                                                       @RequestParam String smsType,@RequestParam String code) throws Exception {

        return ResponseEntitySpecBuilder.success(manSystemFunctionService.
                readExcellAndSendSms(smsType,smsContent,channel,code,file));
    }

    @ApiOperation("邮件发送验证码")
    @RequestMapping(value = "/manage/sendCodeToEmail", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntitySpec<Object> sendCodeToEmail() throws Exception {

        return ResponseEntitySpecBuilder.success(manSystemFunctionService.
                sendCodeToEmail(""));
    }


    @ApiOperation("查询放款账户余额")
    @RequestMapping(value = "/manage/checkAccountBalance", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntitySpec<Object> checkAccountBalance(@RequestBody CheckBalanceRequest request ) throws Exception {

        return ResponseEntitySpecBuilder.success(manSystemFunctionService.
                checkAccountBalance(request.getChannel(),request.getAccount()));
    }

    @ApiOperation("通过路径下载文件")
    @RequestMapping(value = "/manage/getExcelFile", method = RequestMethod.GET)
    public void getXmlFile(HttpServletResponse response,
                           @RequestParam("path") String path) throws Exception {

        manSystemFunctionService.getExcelFile(response, path);
    }

    @ApiOperation("直接使用浏览器打开图片等文件")
    @RequestMapping(value = "/manage/showStreamOnBrowser", method = RequestMethod.GET)
    public void showStreamOnBrowser(HttpServletResponse response,
                           @RequestParam("path") String path) throws Exception {

        ShowStreamUtils.showStreamOnBrowser(response, path);
    }

    @ApiOperation("手动添加到黑名单中 ursBlackList表")
    @RequestMapping(value = "/manage/addUserToBlackList", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntitySpec<Object> addUserToBlackList(@RequestParam MultipartFile file,
                                                         @RequestParam String type) throws Exception {

        return ResponseEntitySpecBuilder.success(manSystemFunctionService.
                addUserToBlackList(type, file));
    }

    @ApiOperation("查询现有产品的金额")
    @RequestMapping(value = "/manage/allSysProduct", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntitySpec<Object> allSysProduct() throws Exception {

        return ResponseEntitySpecBuilder.success(manSystemFunctionService.
                allSysProduct());
    }
}
