package com.yqg.manage.dal.provider;

import com.yqg.manage.service.user.request.ManUserUserRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserUserSqlProvider {
    private Logger logger = LoggerFactory.getLogger(UserUserSqlProvider.class);

    /**
     * 通过用户的姓名手机号查寻用户uuid*/
    public String getUuidByRealNameOrMobile(ManUserUserRequest userRequest){
        StringBuilder selectSql = new StringBuilder();

        selectSql.append("select uuid,id,realName,sex,mobileNumber");
        selectSql.append(" from usrUser where ");
        if(StringUtils.isNotBlank(userRequest.getRealName())){
            selectSql.append(" realName = #{ManUserUserRequest.realName} and ");
        }
        if(StringUtils.isNotBlank(userRequest.getMobile())){
            selectSql.append(" mobileNumber = #{ManUserUserRequest.mobile} and ");
        }

        selectSql.append(" disabled = 0 ");
//        logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String getUserInfoByUuids(String uuids){
        StringBuilder selectSql = new StringBuilder();

        selectSql.append("select uuid,id,realName,sex,mobileNumber, userRole");
        selectSql.append(" from usrUser where ");
        if(StringUtils.isNotBlank(uuids)){
            selectSql.append(" uuid in (").append(uuids).append(") and");
        }

        selectSql.append(" disabled = 0 ");
//        this.logger.info(selectSql.toString());
        return selectSql.toString();
    }

    public String listpayDeposit(String orderNo, String paymentCode){
        StringBuilder selectSql = new StringBuilder();

        selectSql.append("SELECT " +
                "EXTERNAL_ID as externalId," +
                "CUSTOMER_NAME as customerName," +
                "TRANSACTION_ID_STATUS as transactionIdStatus," +
                "DEPOSIT_AMOUNT as depositAmount ," +
                "DEPOSIT_STATUS as depositStatus," +
                "DEPOSIT_CHANNEL as depositChannel," +
                "PAYMENT_CODE as paymentCode," +
                "CREATE_TIME as createTime," +
                "UPDATE_TIME as updateTime " +
                " FROM " +
                " uangPay.T_LPAY_DEPOSIT " +
                " WHERE ");
        if(StringUtils.isNotBlank(orderNo)){
            selectSql.append(" EXTERNAL_ID = #{orderNo} and ");
        }
        if(StringUtils.isNotBlank(paymentCode)){
            selectSql.append(" PAYMENT_CODE = #{paymentCode} and ");
        }

        selectSql.append(" 1 = 1 ");
//        this.logger.info(selectSql.toString());
        return selectSql.toString();
    }
}
