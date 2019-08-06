package com.yqg.service.user.request;

import com.yqg.common.models.BaseRequest;
import lombok.Data;

/**
 * Author: tonggen
 * Date: 2019/6/20
 * time: 6:12 PM
 */
@Data
public class UsrQuestionnaireSaveRequet extends BaseRequest{

    private String postName;

    private String onlineOrNot;

    private String isEnough;

    private String sales;

    private String loanAmout;

    private String loanTerm;

    private String hasOtherProduce;

    private String otherProduceName;

    private String workInterestingOrNot;

    private String produceInterestingOrNot;

    private String callBackOrNot;

    private Integer type; // 1 bussiness, 2 not bussiness.

}
