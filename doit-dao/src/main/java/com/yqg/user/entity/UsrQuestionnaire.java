package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

/**
 * Author: tonggen
 * Date: 2019/6/18
 * time: 12:42 PM
 */
@Data
@Table("usrQuestionnaire")
public class UsrQuestionnaire extends BaseEntity{

    private String userUuid;

    private String postName;

    private String onlineOrNot;

    private String isEnough;

    private String sales;

    private String loanAmout;

    private String loanTerm;

    private String otherProduceName;

    private String hasOtherProduce;

    private String workInterestingOrNot;

    private String produceInterestingOrNot;

    private String callBackOrNot;

    private Integer type; // 1 bussiness, 2 not bussiness.

    private Integer state;//1.wait check, 2.pass 3, not pass

    private Integer checker;
}
