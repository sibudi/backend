package com.yqg.service.user.response;

import com.yqg.service.user.request.Questionnaire;
import lombok.Data;

import java.util.List;

/**
 * Author: tonggen
 * Date: 2019/6/20
 * time: 10:35 AM
 */
@Data
public class UserQuestionnaireDetailResponse extends UsrQuestionnaireBaseResponse{

    private String usrPhone;

    private String onlineOrNot;

    private String isEnough;

    private String postName;

    private String sales;

    private String loanAmout;

    private String loanTerm;

    private String otherProduceName;

    private String workInterestingOrNot;

    private String produceInterestingOrNot;

    private String callBackOrNot;

    private List<String> attachTypes;

    private List<Questionnaire> attachs;

}
