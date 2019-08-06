package com.yqg.service.user.service;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.utils.Base64Utils;
import com.yqg.service.third.upload.UploadService;
import com.yqg.service.third.upload.response.UploadResultInfo;
import com.yqg.service.user.request.Questionnaire;
import com.yqg.service.user.request.UsrQuestionnaireAttachRequest;
import com.yqg.service.user.request.UsrQuestionnaireSaveRequet;
import com.yqg.user.dao.UsrQuestionnaireAttactDao;
import com.yqg.user.dao.UsrQuestionnaireDao;
import com.yqg.user.entity.UsrQuestionnaire;
import com.yqg.user.entity.UsrQuestionnaireAttach;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Author: tonggen
 * Date: 2019/6/18
 * time: 1:13 PM
 */
@Service
@Slf4j
public class UsrQuestionnaireService {

    @Autowired
    private UsrQuestionnaireDao usrQuestionnaireDao;

    @Autowired
    private UsrQuestionnaireAttactDao usrQuestionnaireAttactDao;

    @Autowired
    private UploadService uploadService;

    @Value("${third.upload.uploadHost}")
    private String urlPath;

    public int insertQuestionnaire(UsrQuestionnaireSaveRequet requet) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(requet.getUserUuid()) || requet.getType() == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        log.info("start insertQuestionnarie, userUuid is {}, type is {}",
                requet.getUserUuid(), requet.getType());

        UsrQuestionnaire usrQuestionnaire = new UsrQuestionnaire();
        BeanUtils.copyProperties(requet, usrQuestionnaire);
        usrQuestionnaire.setState(0);

        usrQuestionnaireDao.deleteUsrQuestionnaire(requet.getUserUuid());
        return usrQuestionnaireDao.insert(usrQuestionnaire);
    }

    public int insertQuestionnaireAttach(UsrQuestionnaireAttachRequest request) throws ServiceExceptionSpec {


        if (request == null || CollectionUtils.isEmpty(request.getUsrQuestionnaires())
                || StringUtils.isEmpty(request.getUserUuid())) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }

        final boolean[] vality = {false};

        List<Questionnaire> lists = request.getUsrQuestionnaires();
        lists.stream().filter(e -> !StringUtils.isEmpty(e.getFileUrl())
                && !"./images/photo.png".equals(e.getFileUrl())).forEach(elem -> {
            try {
                log.info("insertQuesionnaireAttach userUuid is {}, and result is {}, type is {}", request.getUserUuid(),
                        elem.getFileUrl(), elem.getAttachType());
                insertAttachData(elem.getFileUrl(), request.getComment(), elem.getAttachType(), request.getUserUuid());
                vality[0] = true;
            } catch (Exception e) {
                log.error("upload insertQuestionnaireAttach error." , e);
            }
        });
        //更改状态为待审核
        if (vality[0]) {
            usrQuestionnaireDao.updateState(request.getUserUuid());
        }

        return lists.size();
    }

    public String uploadQuestionnaireAttach(MultipartFile file, Integer attachType, String sessionId)
            throws ServiceExceptionSpec {

        if (file == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        Integer type = attachType == null ? 0 : attachType;
        String suffixIndex = file.getOriginalFilename().substring(
                file.getOriginalFilename().lastIndexOf("."),
                file.getOriginalFilename().length());
        String fileName = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + suffixIndex;
        String base64Str = Base64Utils.getBase64ImgFromRemoteFile(file);
        log.info("uploadQuestionnaireAttach fileName is {}", fileName);
        UploadResultInfo uploadResultInfo = uploadService.uploadBase64Img(sessionId,
                base64Str, fileName, valueOfQuestionnaireAttactEnum(type) + suffixIndex);
        String result = uploadResultInfo.getData();
        log.info("uploadQuestionnaireAttach result is {} ", result);
        return result;

    }

    public int insertAttachData(String url, String comment, String type, String userUuid) {

        UsrQuestionnaireAttach result = new UsrQuestionnaireAttach();
        result.setUserUuid(userUuid);
        if (!StringUtils.isEmpty(url)) {
            result.setAttachmentUrl(url);
            if (!url.contains("=")) {
                result.setAttachmentSavePath(url);
            } else {
                result.setAttachmentSavePath(url.substring(url.lastIndexOf("=") + 1));
            }
        }
        result.setComment(comment);
        result.setAttachmentType(type);

        return usrQuestionnaireAttactDao.insert(result);
    }

    private UsrQuestionnaireAttach.AttachmentTypeEnum valueOfQuestionnaireAttactEnum(int code){
        List<UsrQuestionnaireAttach.AttachmentTypeEnum> allValues = Arrays.asList(UsrQuestionnaireAttach.AttachmentTypeEnum.values());
        Optional<UsrQuestionnaireAttach.AttachmentTypeEnum> enumOptional = allValues.stream()
                .filter(elem -> elem.getCode() == code).findFirst();
        if (enumOptional.isPresent()) {
            return enumOptional.get();
        }
        return null;
    }
}
