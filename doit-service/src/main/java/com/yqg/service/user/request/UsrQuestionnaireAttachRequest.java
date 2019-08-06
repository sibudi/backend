package com.yqg.service.user.request;

import com.yqg.common.models.BaseRequest;
import lombok.Data;

import java.util.List;

/**
 * Author: tonggen
 * Date: 2019/6/18
 * time: 6:16 PM
 */
@Data
public class UsrQuestionnaireAttachRequest extends BaseRequest{


    private String comment;

    private List<Questionnaire> usrQuestionnaires;


}
