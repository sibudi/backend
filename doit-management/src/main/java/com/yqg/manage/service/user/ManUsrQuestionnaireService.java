package com.yqg.manage.service.user;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.utils.DESUtils;
import com.yqg.manage.dal.user.ManUsrQuestionnaireDao;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.util.PageDataUtils;
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
                phone = phone.substring(0,3) + "****" + phone.substring(7);
                result.setUsrPhone(phone);
            }
        }

        //get usr questionnarie attach
        UsrQuestionnaireAttach attach = new UsrQuestionnaireAttach();
        attach.setDisabled(0);
        attach.setUserUuid(userUuid);
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
}
