package com.yqg.manage.service.user;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.DESUtils;
import com.yqg.manage.dal.user.ManUsrQuestionnaireDao;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.util.PageDataUtils;
import com.yqg.service.third.upload.UploadService;
import com.yqg.service.third.upload.response.UploadResultInfo;
import com.yqg.service.user.request.Questionnaire;
import com.yqg.service.user.request.UsrQuestionnaireListRequest;
import com.yqg.service.user.response.UserQuestionnaireDetailResponse;
import com.yqg.service.user.response.UsrQuestionnaireBaseResponse;
import com.yqg.service.util.ImageUtil;
import com.yqg.service.util.LoginSysUserInfoHolder;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.dao.UsrQuestionnaireAttactDao;
import com.yqg.user.entity.UsrQuestionnaire;
import com.yqg.user.entity.UsrQuestionnaireAttach;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Author: tonggen
 * Date: 2019/6/20
 * time: 11:06 AM
 */
@Service
@Slf4j
public class ManUsrQuestionnaireService {

    @Autowired
    private ManUsrQuestionnaireDao manUsrQuestionnaireDao;

    @Autowired
    private UsrQuestionnaireAttactDao usrQuestionnaireAttactDao;

    @Value("${upload.imagePath}")
    private String imagePath;

    @Autowired
    private ManUserService manUserService;

    @Autowired
    private UsrDao usrDao;

    @Autowired
    private UploadService uploadService;

    /**
     * get questionnaire list.
     * @return
     */
    public PageData<List<UsrQuestionnaireBaseResponse>> listQuestionnaire(UsrQuestionnaireListRequest request) {

        PageHelper.startPage(request.getPageNo(), request.getPageSize());
        List<UsrQuestionnaireBaseResponse> result = manUsrQuestionnaireDao.listQuestionnaire(request);
        if (!CollectionUtils.isEmpty(result)) {
            result.stream().forEach(elem -> {
                if (elem.getChecker() != null && elem.getChecker() != 0) {
                    Optional<ManUser> optional = manUserService.getManUserById(elem.getChecker());
                    elem.setCheckerName(optional.isPresent() ? optional.get().getUsername() : "");
                }
                if (!StringUtils.isEmpty(elem.getUserUuid())) {
                    UsrUser usrUser = usrDao.getUserInfoById(elem.getUserUuid());
                    if (usrUser != null) {
                        elem.setUserName(usrUser.getRealName());
                    }
                }
            });
        }
        PageInfo page = new PageInfo(result);
        return PageDataUtils.mapPageInfoToPageData(page);
    }

    public UserQuestionnaireDetailResponse getManQuestionnaireDetail(String userUuid) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(userUuid)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }

        UserQuestionnaireDetailResponse result = manUsrQuestionnaireDao.getManQuestionnaireDetail(userUuid);

        if (result.getChecker() != null && result.getChecker() != 0) {
            Optional<ManUser> optional = manUserService.getManUserById(result.getChecker());
            result.setCheckerName(optional.isPresent() ? optional.get().getUsername() : "");
        }
        if (!StringUtils.isEmpty(result.getUserUuid())) {

            UsrUser usrUser = usrDao.getUserInfoById(result.getUserUuid());
            if (usrUser != null) {
                String phone = DESUtils.decrypt(usrUser.getMobileNumberDES());
                result.setMobile(phone);
                phone = phone.substring(0,3) + "****" + phone.substring(7);
                result.setUsrPhone(phone);
                result.setUserName(usrUser.getRealName());
            }
        }

        //get usr questionnarie attach
        UsrQuestionnaireAttach attach = new UsrQuestionnaireAttach();
        attach.setDisabled(0);
        attach.setUserUuid(userUuid);
        attach.setSourceType(0);
        List<UsrQuestionnaireAttach> lists = usrQuestionnaireAttactDao.scan(attach);
        if (CollectionUtils.isEmpty(lists)) {
            return result;
        }

        List<Questionnaire> questionnaires = new ArrayList<>();
        String attachTypes = lists.get(0).getAttachmentType();
        lists.stream().forEach(elem ->{
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setComment(elem.getComment());
            questionnaire.setFileUrl(imagePath + ImageUtil.encryptUrl(elem.getAttachmentSavePath()) + "&sessionId="
                    + LoginSysUserInfoHolder.getUsrSessionId());
            questionnaires.add(questionnaire);
        });
        if (!StringUtils.isEmpty(attachTypes)) {
            result.setAttachTypes(Arrays.asList(attachTypes.split(",")));
        }
        result.setAttachs(questionnaires);
        return result;
    }


    public Integer checkManQuestionnaire(UsrQuestionnaireListRequest request) throws ServiceExceptionSpec {

        if (request.getState() == null || StringUtils.isEmpty(request.getUserUuid())) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }

       return manUsrQuestionnaireDao.updateChecker(request.getUserUuid(), request.getState(),
               LoginSysUserInfoHolder.getLoginSysUserId());

    }

    public Integer uploadQuestionnaireAttach(MultipartFile file, String userUuid, String sessionId)
            throws ServiceExceptionSpec {

        if (file == null || StringUtils.isEmpty(userUuid)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        String suffixIndex = file.getOriginalFilename().substring(
                file.getOriginalFilename().lastIndexOf("."),
                file.getOriginalFilename().length());
        String attachType = ".mp3".equals(suffixIndex) ? "1" : "2";
        String fileName = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()) + suffixIndex;
        log.info("uploadQuestionnaireAttach fileName is {}", fileName);
        UploadResultInfo uploadResultInfo = null;
        try {
            uploadResultInfo = uploadService.uploadFile(sessionId,
                    file.getInputStream(), fileName);
        } catch (IOException e) {
            log.error("file upload is error", e);
        }
        String result = uploadResultInfo.getData();
        log.info("uploadQuestionnaireAttach result is {} ", result);
        if (StringUtils.isEmpty(result)) {
            return 0;
        }

        //将结果保存至数据库
        UsrQuestionnaireAttach usrQuestionnaireAttach = new UsrQuestionnaireAttach();
        usrQuestionnaireAttach.setDisabled(0);
        usrQuestionnaireAttach.setUserUuid(userUuid);
        usrQuestionnaireAttach.setAttachmentSavePath(result);
        usrQuestionnaireAttach.setSourceType(1);
        usrQuestionnaireAttach.setAttachmentType(attachType);
        usrQuestionnaireAttach.setAttachmentUrl(imagePath + result);
        return usrQuestionnaireAttactDao.insert(usrQuestionnaireAttach);

    }

    public List<Questionnaire> getSupplementData(String userUuid) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(userUuid)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }

        List<Questionnaire> result = new ArrayList<>();

        //get usr questionnarie attach
        UsrQuestionnaireAttach attach = new UsrQuestionnaireAttach();
        attach.setDisabled(0);
        attach.setUserUuid(userUuid);
        attach.setSourceType(1);
        List<UsrQuestionnaireAttach> lists = usrQuestionnaireAttactDao.scan(attach);
        if (CollectionUtils.isEmpty(lists)) {
            return result;
        }
        lists.stream().filter(e -> (!StringUtils.isEmpty(e.getAttachmentType()))).forEach(elem -> {
            Questionnaire questionnaire = new Questionnaire();
            questionnaire.setFileUrl(elem.getAttachmentUrl());
            // 1、语音 2、图片
            questionnaire.setAttachType(elem.getAttachmentType());
            result.add(questionnaire);
        });

        return result;
    }

}
