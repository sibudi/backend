package com.yqg.manage.dal.provider;

import com.yqg.manage.service.order.request.ManOrderListSearchResquest;
import com.yqg.manage.service.user.request.ManSysUserListRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManUserSqlProvider {
    private Logger logger = LoggerFactory.getLogger(ManUserSqlProvider.class);

    public String sysUserList(ManSysUserListRequest resquest){
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select id,uuid,username,realname,mobile,status,createTime,remark,collectionPhone,collectionWa, employeeNumber, voicePhone  ");
        selectSql.append("from manUser where disabled = 0 ");

        StringBuilder conditionSql = this.generateConditionToUserList(resquest).append(" order by id desc ");
        selectSql.append(conditionSql);

        if (resquest.getOnlineOrNot() == null) {
            selectSql.append(this.byPageSql(resquest));
        }

        logger.debug(selectSql.toString());
        return selectSql.toString();
    }

    public StringBuilder generateConditionToUserList(ManSysUserListRequest resquest) {

        StringBuilder sql = new StringBuilder();
        if (StringUtils.isNotBlank(resquest.getUsername())) {
            sql.append(" and username like '%").append(resquest.getUsername()).append("%'");
        }
        if (StringUtils.isNotBlank(resquest.getMobile())) {
            sql.append(" and mobile = #{request.mobile} ");
        }
        if (resquest.getStatus() != null) {
            sql.append(" and status = #{request.status} ");
        }

        return sql;

    }

    public String sysUserListCount(ManSysUserListRequest resquest){
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select count(1) ");
        selectSql.append("from manUser where disabled = 0 ");

        StringBuilder conditionSql = this.generateConditionToUserList(resquest);
        selectSql.append(conditionSql);

        logger.debug(selectSql.toString());
        return selectSql.toString();
    }

//    public String sysUserListCondition(ManOrderListSearchResquest resquest){
//        StringBuilder selectSql = new StringBuilder();
//        selectSql.append("select su.id,su.uuid,su.username,su.realname,su.status,su.createTime,su.updateTime,su.remark,third ");
//        selectSql.append("from manUser as su inner join manSysUserRole as sr on su.id = sr.userId where su.disabled = 0 and sr.disabled = 0 ");
//
//        StringBuilder conditionSql = this.generateCondition(resquest).append(" order by id desc ");
//        conditionSql.append(this.byPageSql(resquest));
//        selectSql.append(conditionSql);
//
//        logger.debug(selectSql.toString());
//        return selectSql.toString();
//    }

    public String sysUserListConditionCount(ManOrderListSearchResquest resquest){
        StringBuilder selectSql = new StringBuilder();
        selectSql.append("select count(1) ");
        selectSql.append("from manUser as su inner join manSysUserRole as sr on su.id = sr.userId where su.disabled = 0 and sr.disabled = 0 ");

        StringBuilder conditionSql = this.generateCondition(resquest);
        selectSql.append(conditionSql);

        logger.debug(selectSql.toString());
        return selectSql.toString();
    }

    public StringBuilder generateCondition(ManOrderListSearchResquest searchResquest) {
        StringBuilder conditionSql = new StringBuilder();

        if(StringUtils.isNotEmpty(searchResquest.getRoleId())){
            conditionSql.append(" and roleId = #{request.roleId} ");
        }

        return conditionSql;
    }

    /*产生分页sql*/
    private StringBuilder byPageSql(ManSysUserListRequest searchResquest){
        StringBuilder conditionSql = new StringBuilder();
        if(searchResquest.getPageNo() != null && searchResquest.getPageSize() != null ){
            Integer num = (searchResquest.getPageNo() - 1) * searchResquest.getPageSize();
            conditionSql.append(" limit ").append(num.toString()).append(",")
                    .append(searchResquest.getPageSize().toString());
        }
        return conditionSql;
    }
}
