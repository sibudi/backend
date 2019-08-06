package com.yqg.service.third.digSign;

import com.yqg.common.enums.system.ThirdDataTypeEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.mongo.entity.OrderThirdDataMongo;
import com.yqg.mongo.repository.ThirdDataRepository;
import com.yqg.mongo.repository.ThirdDataRepository.EKYCReturn.DataDetail;
import com.yqg.service.externalChannel.transform.ImagePathService;
import com.yqg.service.signcontract.UsrSignContractStepService;
import com.yqg.service.third.asli.request.AsliPlusVerificationRequest;
import com.yqg.service.third.asli.response.AsliPlusVerificationResponse;
import com.yqg.service.third.digSign.reqeust.RegisterRequest;
import com.yqg.service.third.izi.IziService.IziRealNameVerifyResult;
import com.yqg.service.third.izi.IziService.IziRealNameVerifyResult.MessageDetail;
import com.yqg.service.third.jxl.response.JXLBaseResponse;
import com.yqg.service.user.service.*;
import com.yqg.service.user.service.UserDetailService.UserDetailInfo;
import com.yqg.service.user.service.UserVerifyResultService.JXLRealNameVerifyResult;
import com.yqg.service.util.ImageUtil;
import com.yqg.signcontract.entity.UsrSignContractStep;
import com.yqg.user.dao.UsrFaceVerifyResultDao;
import com.yqg.user.entity.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DigSignParamService {
    @Autowired
    private DigiSignConfig digiSignConfig;
    @Autowired
    private UserVerifyResultService userVerifyResultService;
    @Autowired
    private UserAddressDetailService userAddressDetailService;
    @Autowired
    private UsrService usrService;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private ThirdDataRepository thirdDataRepository;
    @Autowired
    private UsrFaceVerifyResultDao usrFaceVerifyResultDao;
    @Autowired
    private UsrSignContractStepService usrSignContractStepService;
    @Autowired
    private UserAttachmentInfoService userAttachmentInfoService;


    public Optional<AsliPlusVerificationRequest> getEkycIdentityValidationData(String userUuid) {
        UsrUser user = usrService.getUserByUuid(userUuid);
        UserDetailInfo userDetailInfo = userDetailService.getUserDetailInfo(user);
        Optional<IdentityCardInfo> identityCardInfo = getIdentityCardInfo(user, userDetailInfo);
        if (!identityCardInfo.isPresent()) {
            return Optional.empty();
        }
        AsliPlusVerificationRequest request = new AsliPlusVerificationRequest();
        request.setAddress(identityCardInfo.get().getAddress());
        request.setBirthdate(identityCardInfo.get().getDateOfBirth());
        request.setBirthplace(identityCardInfo.get().getPlaceOfBirth());
        request.setName(identityCardInfo.get().getName());
        request.setNik(identityCardInfo.get().getIdCardNo());
        request.setMotherName(userDetailInfo.getMotherName());
        request.setPhone("62"+DESUtils.decrypt(user.getMobileNumberDES()));

        List<UsrAttachmentInfo> attachmentInfoList = userAttachmentInfoService.getAttachmentListByUserId(userUuid);
        Optional<UsrAttachmentInfo> selfieInfo =
                attachmentInfoList.stream().filter(elem -> UsrAttachmentEnum.SELFIE.getType() == elem.getAttachmentType()).findFirst();
        if (!selfieInfo.isPresent()) {
            log.error("no selfie attachment file, userId: {}", userUuid);
            return Optional.empty();
        }

        Optional<UsrAttachmentInfo> idcardInfo =
                attachmentInfoList.stream().filter(elem -> UsrAttachmentEnum.ID_CARD.getType() == elem.getAttachmentType()).findFirst();
        if (!idcardInfo.isPresent()) {
            log.error("no idcard attachment file, userId: {}", userUuid);
            return Optional.empty();
        }

        try {
            byte [] selfieInfoBytes = userAttachmentInfoService.getAttachmentStream(selfieInfo.get());
            if(selfieInfoBytes==null){
                return Optional.empty();
            }
            byte [] idCardInfoBytes = userAttachmentInfoService.getAttachmentStream(idcardInfo.get());
            if(idCardInfoBytes==null){
                return Optional.empty();
            }
            //因android展示的时候图片有选择，需要处理成正常的角度
            request.setSelfiePhoto(Base64.encodeBase64String(getRotatedImg(selfieInfoBytes)));
            request.setIdentityPhoto(Base64.encodeBase64String(getRotatedImg(idCardInfoBytes)));
        } catch (Exception e) {
            log.error("create base64 data for attachment file error userUuid: " + userUuid, e);
            return Optional.empty();
        }


        return Optional.of(request);
    }

    private byte[] getRotatedImg(byte[] bytes) throws Exception {
        BufferedImage img = Imaging.getBufferedImage(bytes);
        if (img.getWidth() < img.getHeight()) {
            //宽小于高度一般是正常的图片
            return bytes;
        }
        BufferedImage newImg = ImageUtil.rotateImage(img, -90);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(newImg, "jpg", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    public Optional<RegisterRequest> getRegisterData(String userUuid) {

        UsrUser user = usrService.getUserByUuid(userUuid);
        UserDetailInfo userDetailInfo = userDetailService.getUserDetailInfo(user);
        RegisterRequest result = new RegisterRequest();
        result.setUserId(digiSignConfig.getDoitAdminEmail());

        Optional<IdentityCardInfo> identityCardInfo = getIdentityCardInfo(user, userDetailInfo);
        if (!identityCardInfo.isPresent()) {
            return Optional.empty();
        }
        result.setIdCardAddressDetail(identityCardInfo.get().getAddress());
        result.setGender(identityCardInfo.get().getGender());
        result.setSmallDirect(identityCardInfo.get().getSmallDirect());
        result.setBigDirect(identityCardInfo.get().getBigDirect());
        result.setCity(identityCardInfo.get().getCity());
        result.setName(identityCardInfo.get().getName());
        result.setDateOfBirth(identityCardInfo.get().getDateOfBirth());
        result.setPlaceOfBirth(identityCardInfo.get().getPlaceOfBirth());
        result.setProvince(identityCardInfo.get().getProvince());
        result.setNikKtp(identityCardInfo.get().getIdCardNo());
        //TODO 设置一个默认值
        result.setPostalCode("40115");
        result.setMobileNumber("62" + DESUtils.decrypt(user.getMobileNumberDES()));

        result.setEmail(userDetailInfo.getEmail());

        result.setUserIdInDoit(user.getId().toString());

        Optional<OrderThirdDataMongo> ekycRealNameResult = thirdDataRepository.getLatestDataByType(userUuid, null,
                ThirdDataTypeEnum.ASLI_PLUS_VERIFICATION);
        //ekyc返回的数据
        if (!ekycRealNameResult.isPresent()) {
            log.error("the ekyc realName result is empty of userUuid: {}", userUuid);
            return Optional.empty();
        }
        //ekyc 数据，新版本的api和原来的不一致需要处理为一致的数据
//
//        UsrSignContractStep stepResult =usrSignContractStepService.getSignContractStepResult(userUuid,
//                UsrSignContractStep.SignStepEnum.EKYC_PLUS_VERIFICATION);

        AsliPlusVerificationResponse authResponse = JsonUtils.deserialize(ekycRealNameResult.get().getData(), AsliPlusVerificationResponse.class);
//        AsliPlusVerificationResponse authResponse = JsonUtils.deserialize(stepResult.getRemark(), AsliPlusVerificationResponse.class);

        DataDetail ekycDataDetail = new DataDetail();
        ekycDataDetail.setName(getBooleanValueWithDefault(authResponse.getData().getName(), false));

        ekycDataDetail.setBirthdate(getBooleanValueWithDefault(authResponse.getData().getBirthdate(), false));
        ekycDataDetail.setBirthplace(getBooleanValueWithDefault(authResponse.getData().getBirthplace(), false));
        ekycDataDetail.setAddress(authResponse.getData().getAddress());
        result.setReferenceIdFromEkyc(authResponse.getTimestamp());
        result.setDataFromEkyc(JsonUtils.serialize(ekycDataDetail));


        result.setIdCardValid("1");
        result.setNameValid("1");
        result.setDateOfBirthValid("1");
        result.setPlaceOfBirthValid("1");
        UsrFaceVerifyResult ekycSelfieScore = usrFaceVerifyResultDao.getLatestResultByUserIdAndChannel(userUuid, "ASLI");
        if (ekycSelfieScore == null || ekycSelfieScore.getScore() == null) {
            log.error("the ekyc selfie score result is empty of userUuid: {}", userUuid);
            return Optional.empty();
        }
        String score = ekycSelfieScore.getScore().toPlainString();
        if (score != null && score.length() > 5) {
            score = score.substring(0, 5);
        }
        result.setSelfieScore(score);

        return Optional.of(result);

    }

    private Boolean getBooleanValueWithDefault(Boolean realValue, Boolean defaultValue){
        if(realValue==null){
            return defaultValue;
        }
        return realValue;
    }

    private Optional<IdentityCardInfo> getIdentityCardInfo(UsrUser user, UserDetailInfo userDetailInfo) {
        IdentityCardInfo identityCardInfo = new IdentityCardInfo();
        //izi中数据
        UsrVerifyResult iziVerifyResult = userVerifyResultService.getLatestVerifyResultByUserUuid(user.getUuid(),
                UsrVerifyResult.VerifyTypeEnum.IZI_REAL_NAME);
        if (iziVerifyResult != null && iziVerifyResult.getVerifyResult() == UsrVerifyResult.VerifyResultEnum.SUCCESS.getCode()) {
            //验证成功
            IziRealNameVerifyResult iziReturn = JsonUtils.deserialize(iziVerifyResult.getRemark(), IziRealNameVerifyResult.class);
            MessageDetail msgDetail = JsonUtils.deserialize(JsonUtils.serialize(iziReturn.getMessage()), MessageDetail.class);
            identityCardInfo.setAddress(msgDetail.getAddress());
            identityCardInfo.setGender(msgDetail.getGender());
            identityCardInfo.setSmallDirect(msgDetail.getVillage());
            identityCardInfo.setBigDirect(msgDetail.getDistrict());
            identityCardInfo.setCity(msgDetail.getCity());
            identityCardInfo.setName(msgDetail.getName());
            identityCardInfo.setDateOfBirth(msgDetail.getDateOfBirth());
            identityCardInfo.setPlaceOfBirth(msgDetail.getPlaceOfBirth());
            identityCardInfo.setProvince(msgDetail.getProvince());
            identityCardInfo.setIdCardNo(msgDetail.getId());
        } else {
            //查聚信立ktp的
            UsrVerifyResult jxlResult = userVerifyResultService.getLatestVerifyResultByUserUuid(user.getUuid(),
                    UsrVerifyResult.VerifyTypeEnum.KTP);
            UsrAddressDetail birthAddress = userAddressDetailService.getUserAddressDetailByType(user.getUuid(), UsrAddressEnum.BIRTH);
            if (jxlResult != null && jxlResult.getVerifyResult() == UsrVerifyResult.VerifyResultEnum.SUCCESS.getCode() && jxlResult.getRemark().contains("place_of_birth")) {
                if(jxlResult.getRemark().startsWith("{\"code\":\"20000\",\"message\":\"Invoke api successful\",")){
                    //新版
                    JXLBaseResponse jxlRealNameVerifyResult = JsonUtils.deserialize(jxlResult.getRemark(), JXLBaseResponse.class);
                    if(jxlRealNameVerifyResult.getData()!=null){
                        JXLBaseResponse.IdentityVerifyData jxlResultDetail = JsonUtils.deserialize(JsonUtils.serialize(jxlRealNameVerifyResult.getData()),
                        JXLBaseResponse.IdentityVerifyData.class);
                        identityCardInfo.setGender("male".equalsIgnoreCase(jxlResultDetail.getGender()) ? "LAKI-LAKI" : "PEREMPUAN");
                        identityCardInfo.setSmallDirect(jxlResultDetail.getDivision());
                        identityCardInfo.setBigDirect(jxlResultDetail.getDistrict());
                        identityCardInfo.setCity(jxlResultDetail.getCity());
                        identityCardInfo.setName(jxlResultDetail.getName());
                        identityCardInfo.setDateOfBirth(jxlResultDetail.getBirthday().replace("/", "-"));
                        identityCardInfo.setPlaceOfBirth(jxlResultDetail.getPlaceOfBirth());
                        identityCardInfo.setProvince(jxlResultDetail.getProvince());
                        identityCardInfo.setIdCardNo(jxlResultDetail.getIdCardNo());
                    }

                }else{
                    //老版本
                    JXLRealNameVerifyResult jxlRealNameVerifyResult = JsonUtils.deserialize(jxlResult.getRemark(), JXLRealNameVerifyResult.class);
                    identityCardInfo.setGender("male".equalsIgnoreCase(jxlRealNameVerifyResult.getGender()) ? "LAKI-LAKI" : "PEREMPUAN");
                    identityCardInfo.setSmallDirect(jxlRealNameVerifyResult.getDivision());
                    identityCardInfo.setBigDirect(jxlRealNameVerifyResult.getDistrict());
                    identityCardInfo.setCity(jxlRealNameVerifyResult.getCity());
                    identityCardInfo.setName(jxlRealNameVerifyResult.getName());
                    identityCardInfo.setDateOfBirth(jxlRealNameVerifyResult.getBirthday().replace("/", "-"));
                    identityCardInfo.setPlaceOfBirth(jxlRealNameVerifyResult.getPlaceOfBirth());
                    identityCardInfo.setProvince(jxlRealNameVerifyResult.getProvince());
                    identityCardInfo.setIdCardNo(jxlRealNameVerifyResult.getIdCard());
                }


            } else {
                //查询用户录入的
                if (birthAddress != null) {
                    identityCardInfo.setSmallDirect(birthAddress.getSmallDirect());
                    identityCardInfo.setBigDirect(birthAddress.getBigDirect());
                    identityCardInfo.setCity(birthAddress.getCity());
                    identityCardInfo.setPlaceOfBirth(birthAddress.getCity());
                    identityCardInfo.setProvince(birthAddress.getProvince());
                } else {
                    log.error("the birth address is empty of userUuid: {}", user.getUuid());
                    return Optional.empty();
                }
                identityCardInfo.setGender(user.getSex() == 1 ? "LAKI-LAKI" : "PEREMPUAN");

                identityCardInfo.setName(user.getRealName());
                //日期格式化DD-MM-YYYY
                identityCardInfo.setDateOfBirth(birthdayFormat(userDetailInfo.getBirthday()));
                identityCardInfo.setIdCardNo(user.getIdCardNo());
            }
            identityCardInfo.setAddress(birthAddress != null ? birthAddress.getDetailed() : null);
        }

        return Optional.of(identityCardInfo);
    }

    private String birthdayFormat(String birthday) {
        if(StringUtils.isEmpty(birthday)){
            return "";
        }
        String resultStr = "";
        String split[] = birthday.split("-");
        if (split[0].length() == 1) {
            resultStr += "0" + split[0];
        } else {
            resultStr += split[0];
        }
        resultStr += "-";
        if (split[1].length() == 1) {
            resultStr += "0" + split[1];
        } else {
            resultStr += split[1];
        }
        resultStr += "-";
        if (split[2].length() == 2) {
            resultStr += "19" + split[1];
        } else {
            resultStr += split[2];
        }
        return resultStr;
    }

    @Getter
    @Setter
    public static class IdentityCardInfo {
        private String address;
        private String gender;
        private String smallDirect;
        private String bigDirect;
        private String city;
        private String province;
        private String name;
        private String idCardNo;
        private String dateOfBirth;
        private String placeOfBirth;
    }


}
