package com.yqg.service.third.asli;

import com.yqg.common.enums.system.ThirdDataTypeEnum;
import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.order.dao.AsliIdentityAuthResultDao;
import com.yqg.order.entity.AsliIdentityAuthResult;
import com.yqg.service.order.OrdThirdDataService;
import com.yqg.service.third.asli.config.AsliConfig;
import com.yqg.service.third.asli.request.AsliPlusVerificationRequest;
import com.yqg.service.third.asli.response.AsliPlusVerificationResponse;
import com.yqg.service.util.ImageUtil;
import com.yqg.user.dao.UsrFaceVerifyResultDao;
import com.yqg.user.entity.UsrFaceVerifyResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.ImagingConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffConstants;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


/**
 * Created by wanghuaizhou on 2019/3/25.
 */
@Component
@Slf4j
public class AsliService {

    @Autowired
    private AsliConfig asliConfig;

    @Autowired
    private AsliIdentityAuthResultDao asliIdentityAuthResultDao;

    @Autowired
    private UsrFaceVerifyResultDao usrFaceVerifyResultDao;

    @Autowired
    private OrdThirdDataService ordThirdDataService;
    @Autowired
    private OkHttpClient httpClient;



    /**
     * Professional Plus Verification  实名+分数
     */
    public Optional<AsliPlusVerificationResponse> plusVerification(AsliPlusVerificationRequest param) {
        Long startTime = System.currentTimeMillis();
        try {

            String requestJson = JsonUtils.serialize(param);
            String url = asliConfig.getHost() + asliConfig.getName() + asliConfig.getVerifyProfessionalPlus();
            log.info("asli request url: {} with nik: {}, name: {}, userUuid: {}", url, param.getNik(),param.getName(),param.getUserUuid());
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestJson);

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .header("token", asliConfig.getToken())
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                // 响应
                log.info("asli plus verification response :{}", responseStr);
                AsliPlusVerificationResponse authResponse = JsonUtils.deserialize(responseStr, AsliPlusVerificationResponse.class);

                AsliIdentityAuthResult result = new AsliIdentityAuthResult();
                result.setOrderNo(param.getOrderNo());
                result.setUserUuid(param.getUserUuid());
                //result.setMessage(authResponse.getMessage());
                result.setError(authResponse != null && authResponse.getError() != null ? JsonUtils.serialize(authResponse.getError()) : null);
                result.setStatus(authResponse.getStatus());
                AsliPlusVerificationResponse.DataDetail dataDetail = authResponse.getData();
                if (dataDetail != null) {
                    result.setName(dataDetail.getName() != null ? dataDetail.getName().toString() : null);
                    result.setBirthdate(dataDetail.getBirthdate() != null ? dataDetail.getBirthdate().toString() : null);
                    result.setBirthplace(dataDetail.getBirthplace() != null ? dataDetail.getBirthplace().toString() : null);
                    result.setAddress(dataDetail.getAddress());
                }
                this.asliIdentityAuthResultDao.insert(result);
                //保存分数
                UsrFaceVerifyResult faceVerifyResult = new UsrFaceVerifyResult();
                faceVerifyResult.setUserUuid(param.getUserUuid());
                faceVerifyResult.setOrderNo(param.getOrderNo());
                faceVerifyResult.setChannel("ASLI");
                faceVerifyResult.setScore(dataDetail != null ? dataDetail.getSelfiePhoto() : null);
                this.usrFaceVerifyResultDao.insert(faceVerifyResult);
                // 保存原始数据到mongo
                this.ordThirdDataService.add(responseStr, param.getOrderNo(), param.getUserUuid(), ThirdDataTypeEnum.ASLI_PLUS_VERIFICATION, 1);
                return Optional.of(authResponse);
            } else {
                log.error("asli实名验证 请求失败,失败原因:{},错误码:{}", response.message(), response.code());
            }
        } catch (Exception e) {
            log.error("check ekyc identity error with id: " + param.getNik(), e);
        }finally {
            log.info("asli api invoke cost: {} ms",(System.currentTimeMillis()-startTime));
        }
        return Optional.empty();
    }




//
//    /**
//     * 实名认证
//     */
//    public Optional<AsliIdentityAuthResponse> identityAuth(AsliEkycIdentRequest param) {
//        try {
//            Map<String, String> contents = new HashMap<>();
//            contents.put("nik", param.getNik()); // Identifier number of individual
//            contents.put("name", param.getName()); // Individual name
//            contents.put("birthplace", param.getBirthPlace()); // Individual birthplace
//            contents.put("birthdate", param.getBirthDate()); // Individual birthdate. DD-MM-YYYY (e.g. 01-11- 1975)
//            contents.put("address", param.getAddress()); // Individual address
//            /**
//             *   TODO: 切换到正式环境的时候  不忽略验证https证书
//             * */
//            OkHttpClient httpClient = new OkHttpClient.Builder()
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(10, TimeUnit.SECONDS)
//                    .sslSocketFactory(new SSLSocketClient().getSSLSocketFactory())//配置
//                    .hostnameVerifier(new SSLSocketClient().getHostnameVerifier())//配置    //忽略验证证书
//                    .build();
//
//            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), JSONObject.toJSON(contents).toString());
//
//            Request request = new Request.Builder()
//                    .url(asliConfig.getHost() + asliConfig.getName() + asliConfig.getIdentityUrl())
//                    .post(requestBody)
//                    .header("token", asliConfig.getToken())
//                    .addHeader("Content-Type", "application/json")
//                    .build();
//
//            Response response = httpClient.newCall(request).execute();
//            if (response.isSuccessful()) {
//                String responseStr = response.body().string();
//                // 响应
//                log.info("asli实名验证 请求后返回:{}", JsonUtils.serialize(responseStr));
//                AsliIdentityAuthResponse authResponse = JsonUtils.deserialize(responseStr, AsliIdentityAuthResponse.class);
//
//                AsliIdentityAuthResult result = new AsliIdentityAuthResult();
//                result.setOrderNo(param.getOrderNo());
//                result.setUserUuid(param.getUserUuid());
//                result.setMessage(authResponse.getMessage());
//                result.setError(authResponse.getError());
//                result.setStatus(authResponse.getStatus());
//                if (authResponse.getData() != null) {
//                    result.setName(authResponse.getData().getName());
//                    result.setBirthdate(authResponse.getData().getBirthdate());
//                    result.setBirthplace(authResponse.getData().getBirthplace());
//                    result.setAddress(authResponse.getData().getAddress());
//                }
//                this.asliIdentityAuthResultDao.insert(result);
//
//                // 保存原始数据到mongo
//                this.ordThirdDataService.add(responseStr, param.getOrderNo(), param.getUserUuid(), ThirdDataTypeEnum.ASLI_IDENTITY_CHECK, 1);
//                return Optional.of(authResponse);
//            } else {
//                log.error("asli实名验证 请求失败,失败原因:{},错误码:{}", response.message(), response.code());
//            }
//        } catch (Exception e) {
//            log.error("check ekyc identity error with param: " + JsonUtils.serialize(param), e);
//        }
//        return Optional.empty();
//    }
//
//    /**
//     * 自拍照认证
//     */
//    public Optional<AsliSelfieScoreResposne> selfieAuth(AsliEkycSelfieScoreRequest selfieRequest) {
//
//        try {
//
//            Map<String, String> contents = new HashMap<String, String>();
//            contents.put("nik", selfieRequest.getNik()); // Identifier number of individual
//            contents.put("selfie_photo", selfieRequest.getSelfiePhoto()); // Face image. Available formats: Base64 encoded string converted from jpeg, png, wsq
//
//            OkHttpClient httpClient = new OkHttpClient.Builder()
//                    .connectTimeout(30, TimeUnit.SECONDS)
//                    .readTimeout(10, TimeUnit.SECONDS)
//                    .sslSocketFactory(new SSLSocketClient().getSSLSocketFactory())//配置
//                    .hostnameVerifier(new SSLSocketClient().getHostnameVerifier())//配置    //忽略验证证书
//                    .build();
//
//            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), JSONObject.toJSON(contents).toString());
//
//            Request request = new Request.Builder()
//                    .url(asliConfig.getHost() + asliConfig.getName() + asliConfig.getSelfieUrl())
//                    .post(requestBody)
//                    .header("token", asliConfig.getToken())
//                    .addHeader("Content-Type", "application/json")
//                    .build();
//
//            Response response = httpClient.newCall(request).execute();
//            if (response.isSuccessful()) {
//                String responseStr = response.body().string();
//                // 响应
//                log.info("asli自拍照验证 请求后返回:{}", JsonUtils.serialize(responseStr));
//
//                AsliSelfieScoreResposne scoreResposne = JsonUtils.deserialize(responseStr, AsliSelfieScoreResposne.class);
//                UsrFaceVerifyResult result = new UsrFaceVerifyResult();
//                result.setUserUuid(selfieRequest.getUserUuid());
//                result.setOrderNo(selfieRequest.getOrderNo());
//                result.setRemark(scoreResposne.getMessage());
//                result.setChannel("ASLI");
//                if (scoreResposne.getData() != null) {
//                    result.setScore(scoreResposne.getData().getSelfieSocre());
//                }
//                this.usrFaceVerifyResultDao.insert(result);
//                // 保存原始数据到mongo
//                this.ordThirdDataService.add(responseStr, selfieRequest.getOrderNo(), selfieRequest.getUserUuid(), ThirdDataTypeEnum.ASLI_SELFIE_CHECK, 1);
//                return Optional.of(scoreResposne);
//            } else {
//                log.error("asli自拍照验证 请求失败,失败原因:{},错误码:{}", response.message(), response.code());
//            }
//        } catch (Exception e) {
//            log.error("check ekyc selfie score error, param: " + JsonUtils.serialize(selfieRequest), e);
//        }
//        return Optional.empty();
//    }




    public static void main(String[] args) throws Exception{


        AsliPlusVerificationRequest param = new AsliPlusVerificationRequest();
        param.setNik("3301160301950001");
        param.setName("ANDRIYANA");
        param.setBirthdate("03-05-1995");
        param.setBirthplace("CILACAP");
        param.setAddress("DUSUN BUNISEURI");
        //param.setIdentityPhoto("");
        File f = new File("C:\\Users\\zxc20\\Desktop\\线上测试2.jpg");

        try (BufferedInputStream inStream = new BufferedInputStream(new FileInputStream(f))) {

            byte[] bb= IOUtils.toByteArray(inStream);
            BufferedImage img = Imaging.getBufferedImage(bb);
            BufferedImage newImg = ImageUtil.rotateImage(img,-90);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(newImg,"jpg",byteArrayOutputStream);
            bb = byteArrayOutputStream.toByteArray();

            param.setSelfiePhoto(Base64Utils.encode(bb));
        } catch (Exception e) {
            log.error("get file stream error, path: " + f.getPath(),e);
        }
        param.setPhone("6285883662578");
        param.setMotherName("cicih arsih");
        param.setUserUuid("7597B34FA2284CDFA2DEAAA53A5788C5");
        param.setOrderNo("011905081457264510");

        String url = "https://api.asliri.id:8443/glotech/verify_profesional_plus";
        log.info("asli request url: {} with nik: {}, userUuid: {}", url, param.getNik(),param.getUserUuid());
        String requestJson = JsonUtils.serialize(param);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), requestJson);

        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .sslSocketFactory(new SSLSocketClient().getSSLSocketFactory())//配置
                .hostnameVerifier(new SSLSocketClient().getHostnameVerifier())//配置    //忽略验证证书
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("token", "92395c97-3bf0-48d7-8097-5b9920d6e0fa")
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = httpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseStr = response.body().string();
            // 响应
            log.info("asli plus verification response :{}", responseStr);
            System.err.println(responseStr);
            AsliPlusVerificationResponse authResponse = JsonUtils.deserialize(responseStr, AsliPlusVerificationResponse.class);
        }else{
            log.info("resp: {}",response);
        }

    }

}
