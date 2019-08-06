package com.yqg.manage.dal.provider;

import com.yqg.manage.service.user.request.ManUserUserRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author alan
 */
public class UserFeedBackSqlProvider {
    private Logger logger = LoggerFactory.getLogger(UserFeedBackSqlProvider.class);

    public String userFeedBackList(ManUserUserRequest dataRequest){
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select id,uuid,createTime,remark,feedBackContent,userUuid,userMobile,questionType,stageType,operatorName,updateTime,feedBackImages,collectionName ");
        selectSql.append("from usrFeedBack where ");

        StringBuilder conditionSql = this.generateCondition(dataRequest);

        if (dataRequest.getPageNo() != null && dataRequest.getPageSize() != null) {
            Integer num = (dataRequest.getPageNo() - 1) * dataRequest.getPageSize();
            conditionSql.append(" limit ").append(num.toString()).append(",")
                    .append(dataRequest.getPageSize().toString());
        }

        selectSql.append(conditionSql);
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String userFeedBackListCountNum(ManUserUserRequest dataRequest) {
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select count(1) from usrFeedBack where");

        StringBuilder conditionSql = this.generateCondition(dataRequest);
        selectSql.append(conditionSql);


//        this.logger.info(selectSql.toString());
        return selectSql.toString();
    }

    private StringBuilder generateCondition(ManUserUserRequest userUser) {
        StringBuilder conditionSql = new StringBuilder();
        if (StringUtils.isNotBlank(userUser.getMobile())) {
            conditionSql.append(" userMobile = #{ManUserUserRequest.mobile} and ");
        }
        if(StringUtils.isNotEmpty(userUser.getStartTime())){
            conditionSql.append(" createTime >= '"+ userUser.getStartTime() + " 00:00:00' and ");
        }
        if(StringUtils.isNotEmpty(userUser.getEndTime())){
            conditionSql.append(" createTime <= '" + userUser.getEndTime() + " 23:59:59' and ");
        }
        if(userUser.getQuestionType() != null){
            conditionSql.append(" questionType = #{ManUserUserRequest.questionType} and ");
        }
        if(userUser.getStageType() != null){
            conditionSql.append(" stageType = #{ManUserUserRequest.stageType} and ");
        }
        if (userUser.getSourceType() != null) {
            conditionSql.append(" sourceType = #{ManUserUserRequest.sourceType} and ");
        }
        if (StringUtils.isNotBlank(userUser.getCollectionName())) {
            conditionSql.append(" collectionName = #{ManUserUserRequest.collectionName} and ");
        }

        conditionSql.append(" disabled = 0 order by id desc");

        return conditionSql;
    }

    public String userFeedBackListByPage(ManUserUserRequest dataRequest) {
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select * ");
        selectSql.append("from usrFeedBack where ");

        StringBuilder conditionSql = this.generateCondition(dataRequest);

        Integer num = (dataRequest.getPageNo() - 1) * dataRequest.getPageSize();
        conditionSql.append(" limit ").append(num.toString()).append(",")
                .append(dataRequest.getPageSize().toString());

        selectSql.append(conditionSql);
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }
}
