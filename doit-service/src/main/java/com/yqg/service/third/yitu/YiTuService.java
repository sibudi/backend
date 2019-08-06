package com.yqg.service.third.yitu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.system.SysThirdLogsEnum;
import com.yqg.common.enums.system.ThirdDataTypeEnum;
import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.JsonUtils;
import com.yqg.service.order.OrdThirdDataService;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.system.service.SysThirdLogsService;
import com.yqg.service.third.upload.UploadService;
import com.yqg.service.third.upload.response.UploadResultInfo;
import com.yqg.service.user.service.UsrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by wanghuaizhou on 2017/11/25.
 */
@Component
@Slf4j
public class YiTuService {


    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private YiTuConfig yituConfig;

    @Autowired
    private SysParamService sysParamService;

    @Autowired
    private SysThirdLogsService sysThirdLogsService;

    @Autowired
    private OrdThirdDataService ordThirdDataService;

    @Autowired
    private UsrService usrService;

    @Autowired
    private UploadService uploadService;

    /**
     * 依图人脸比对
     * databaseImageContent ： 身份证正面照
     * queryImageContent ： 自拍照
     */
    public void verifyFacePackage(String databaseImageContent, String queryImageContent,String orderNo,String userUuid,String sessionId)
           {
         try {
             Map<String, Object> map = new HashMap<>();
             map.put("database_image_content", databaseImageContent);
             map.put("database_image_type", 2);
             map.put("query_image_content", queryImageContent);
             map.put("query_image_type", 301);
             map.put("true_negative_rate", "99.99");
             map.put("auto_rotate_for_query",true);
             map.put("auto_rotate_for_database",true);

             StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(
                     Charset.forName("UTF-8"));
             this.restTemplate.getMessageConverters().set(1, stringHttpMessageConverter);

             LinkedMultiValueMap<String, String> entityMap = new LinkedMultiValueMap<>();
             entityMap.add("X-Auth-Token", this.yituConfig.getToken());
             HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(
                     map, entityMap);

             // 保存请求依图的数据
             this.sysThirdLogsService.addSysThirdLogs(orderNo,userUuid, SysThirdLogsEnum.YITU.getCode(),0,"Yitu request","");

             ResponseEntity<String> response = this.restTemplate.exchange(
                     this.yituConfig.getVerifyUrl(), HttpMethod.POST, requestEntity, String.class);
             if (HttpStatus.OK.value() != response.getStatusCode().value()) {
                 log.info("依图人脸比对出错, statusCode = {}", response.getStatusCode().value());
                 throw new ServiceException(ExceptionEnum.SYSTEM_YITU_CALL_SCORE_LIMT_ERROR);
             }
             Map<String, Object> result = JsonUtils.deserialize(response.getBody(),
                     new TypeReference<Map<String, Object>>() {
                     });


             int status = "0".equals(result.get("rtn").toString()) ? 1 : 2;

             if(status==2){
                 log.info("请求依图返回失败订单号:{},结果:{}", orderNo, JsonUtils.serialize(result));
             }else{
                 log.info("请求依图成功后订单号:{},结果:{}", orderNo,JsonUtils.serialize(result));
             }

             // TODO: 依图第三方数据保存
             log.info("mongo保存活体数据开始,thirdDataType为1,订单号:{}", orderNo);
             this.ordThirdDataService
                     .add(JsonUtils.serialize(result), orderNo, userUuid, ThirdDataTypeEnum.YITU_DATA,
                             status);
             log.info("mongo保存活体数据结束,thirdDataType为1,订单号:" + orderNo);

         }catch (Exception e){
             log.error("请求依图异常",e);
         }

        /**
         *  不使用sdk的话  获取不到活体截图
         * */
//        //无论是否成功都保存图片
//        if (result.get("query_image_package_result") == null) {
//            log.info("依图返回信息为空,orderNo: {}", orderNo);
//            throw new ServiceException(ExceptionEnum.SYSTEM_YITU_CALL_SCORE_LIMT_ERROR);
//        }
//        Map<String, Object> queryImagePackageResult = JsonUtils.deserialize(
//            JsonUtils.serialize(result.get("query_image_package_result")),
//            new TypeReference<Map<String, Object>>() {
//            });
//
//        if (!CollectionUtils.isEmpty(queryImagePackageResult)
//            && queryImagePackageResult.get("query_image_contents") != null) {
//            List<String> queryImageContents = JsonUtils.deserialize(
//                JsonUtils
//                    .serialize(queryImagePackageResult.get("query_image_contents")),
//                new TypeReference<List<String>>() {
//                });
//            if (!CollectionUtils.isEmpty(queryImageContents)) {
//
//                String queryImage = queryImageContents.get(0);
//                //  TODO: 上传到服务器
//                UploadResultInfo uploadResultInfo = this.uploadService
//                    .uploadBase64Img(sessionId, queryImage, "FACE.jpeg", "FACE");
//
//                String path;
//                if (uploadResultInfo.getCode() == 1) {
//                    path = uploadResultInfo.getData();
//                } else {
//                    log.info("活体匹配图片失败，请重新认证");
//                    throw new ServiceException(ExceptionEnum.SYSTEM_YITU_UPLOAD_IMAGE_FAILD);
//                }
//
//                // 保存人脸识别的附件
//                usrService.insertAttachment(userUuid, path,
//                    String.valueOf(UsrAttachmentEnum.FACE.getType()));
//            }
//        }

    }

}
