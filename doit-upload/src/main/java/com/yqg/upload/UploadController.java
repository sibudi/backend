package com.yqg.upload;

import com.alibaba.fastjson.JSON;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.UserSessionUtil;
import com.yqg.upload.common.Base64Info;
import com.yqg.upload.common.Constants;
import com.yqg.upload.common.ResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Jacob on 2017/11/24.
 */
@Controller
@Slf4j
public class UploadController {

    public Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UploadService uploadService;
    @Autowired
    private RedisClient redisClient;

    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo upload(@RequestParam("file") MultipartFile file, String sessionId) throws ServiceException {
        ResultInfo resultInfo = new ResultInfo();
        logger.info("===用户session为,sessionId:{}", sessionId);
        Long startTime = System.currentTimeMillis();
        String loginSessionStr = UserSessionUtil.getLoginSessionStr(redisClient, sessionId);
        if (StringUtils.isEmpty(loginSessionStr)) {
            logger.error("===???");
            throw new ServiceException(ExceptionEnum.SESSION_UN_LOGIN);
        } else {
            try {
                if (!file.isEmpty()) {
                    resultInfo = uploadService.fileStored(file);
                } else {
                    resultInfo.setCode(Constants.FILE_UPLOAD_FAIL);
                    resultInfo.setMessage("file is empty");
                }
            } catch (Exception ex) {
                logger.error("===??????:{}", ex);
                resultInfo.setCode(Constants.FILE_UPLOAD_FAIL);
                resultInfo.setMessage(ex.getMessage());
            }
        }
        logger.info("===图片上传结果为:{},耗时:{}", JSON.toJSONString(resultInfo), System.currentTimeMillis() - startTime);
        return resultInfo;
    }

    @RequestMapping(value = "/uploadFile/uploadManageFile", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo uploadManageFile(@RequestParam("file") MultipartFile file, String sessionId) throws ServiceException {
        ResultInfo resultInfo = new ResultInfo();
        logger.info("===后台文件上传,sessionId:{}", sessionId);
        Long startTime = System.currentTimeMillis();
        String loginSessionStr = redisClient.get(RedisContants.MANAGE_SESSION_PREFIX + sessionId);
        if (StringUtils.isEmpty(loginSessionStr)) {
            logger.error("====后台用户没有登录");
            throw new ServiceException(ExceptionEnum.SESSION_UN_LOGIN);
        } else {
            try {
                if (!file.isEmpty()) {
                    resultInfo = uploadService.fileStored(file);
                } else {
                    resultInfo.setCode(Constants.FILE_UPLOAD_FAIL);
                    resultInfo.setMessage("file is empty");
                }
            } catch (Exception ex) {
                logger.error("===上传出错:{}", ex);
                resultInfo.setCode(Constants.FILE_UPLOAD_FAIL);
                resultInfo.setMessage(ex.getMessage());
            }
        }
        logger.info("===result ", JSON.toJSONString(resultInfo), System.currentTimeMillis() - startTime);
        return resultInfo;
    }

    /**
    * @Description: 不需要sessionId上传文件 （质检语音定时任务文件上传）
    * @Param: [file]
    * @return: com.yqg.upload.common.ResultInfo
    * @Author: 许金泉
    * @Date: 2019/4/9 14:31
    */
    @RequestMapping(value = "/uploadFile/uploadManageFileUnverified", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo uploadManageFileUnverified(@RequestParam("file") MultipartFile file) throws ServiceException {
        ResultInfo resultInfo = new ResultInfo();
        Long startTime = System.currentTimeMillis();
        try {
            if (!file.isEmpty()) {
                resultInfo = uploadService.fileStored(file);
            } else {
                resultInfo.setCode(Constants.FILE_UPLOAD_FAIL);
                resultInfo.setMessage("file is empty");
            }
        } catch (Exception ex) {
            logger.error("===上传出错:{}", ex);
            resultInfo.setCode(Constants.FILE_UPLOAD_FAIL);
            resultInfo.setMessage(ex.getMessage());
        }
        logger.info("===result ", JSON.toJSONString(resultInfo), System.currentTimeMillis() - startTime);
        return resultInfo;
    }

    @RequestMapping(value = "/uploadBase64", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo uploadBase64(String fileData, String fileName, String fileType, String sessionId) {
        ResultInfo resultInfo = new ResultInfo();
        logger.info("===base64????[{}]", sessionId);
        try {
            Base64Info info = new Base64Info();
            info.setFileName(fileName);
            info.setFileType(fileType);
            info.setBase64Str(fileData);
            resultInfo = uploadService.fileStored("", info);
            logger.info("===base64??????:{}", JSON.toJSONString(resultInfo));
        } catch (Exception ex) {
            logger.error("===??????:{}", ex);
            resultInfo.setCode(Constants.FILE_UPLOAD_FAIL);
            resultInfo.setMessage(ex.getMessage());
        }
        return resultInfo;
    }


}
