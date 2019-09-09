package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdOrder;
import com.yqg.system.entity.UserResponse;
import com.yqg.user.entity.UserMobileNumberInfo;
import com.yqg.user.entity.UsrUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Mapper
public interface UsrDao extends BaseMapper<UsrUser> {

    @Select("select * from usrUser where disabled = 0 and uuid = #{userUuid}")
    UsrUser getUserInfoById(@Param("userUuid")String userUuid);

    @Select("select * from usrUser where  uuid = #{userUuid}")
    UsrUser getUserInfoByIdIgnoreDisable(@Param("userUuid")String userUuid);

    // 5天内借款成功的用户
    @Select("select * from usrUser where uuid in (SELECT userUuid FROM doit.ordOrder where status = 7 and datediff(curdate(),updateTime)<5);")
    List<UsrUser> getUserWithLoanSuccessFiveDay();


    @Select("<script>"
        + "select * from usrUser where disabled = 0 and status = 1 and mobileNumberDES in "
        + "<foreach collection='phones' item='item' separator=',' open='(' close=')'>"
        + " #{item}"
        + "</foreach>"
        + "</script>")
    List<UsrUser> getUsersByDesMobileList(@Param("phones") List<String> phones);


    // 沉默用户（注册未申请）
    @Select("select * from usrUser where disabled =0 and uuid not in (select userUuid from ordOrder where disabled = 0 )")
    List<UsrUser> getSilenceUser();

    // 沉默用户（注册未申请）
    @Select("select * from usrUser where disabled =0 and uuid not in (select userUuid from ordOrder where disabled = 0 ) and dateDiff(now(), createTime) <= 14")
    List<UsrUser> getSilenceUserWithTwoWeek();


    // 沉默用户（申请未提交）
    @Select("select * from usrUser where disabled =0 and uuid  in (select userUuid from ordOrder where status = 1 and disabled = 0) \n")
    List<UsrUser> getSilenceUser2();

    // 沉默用户（一周内申请未提交）
    @Select("select * from usrUser where disabled =0 and uuid  in (select userUuid from ordOrder where status = 1 and disabled = 0 and dateDiff(now(), createTime) <= 7 ) ;")
    List<UsrUser> getSilenceUser2WithOneWeek();

    // 沉默用户（二周内申请未提交）
    @Select("select * from usrUser where disabled =0 and uuid  in (select userUuid from ordOrder where status = 1 and disabled = 0 and dateDiff(now(), createTime) <= 14 ) ;")
    List<UsrUser> getSilenceUser2WithTwoWeek();

    // 沉默用户（还款成功后不再复借用户）
    @Select("#沉默用户\n" +
            "select '2' as orderNo, " +
            "orda.useruuid as uuid,\n" +
            "mobileNumberDES\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "max(actualRefundTime) as repayDay,\n" +
            "useruuid\n" +
            "from \n" +
            "ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (10,11) \n" +
            "group by useruuid\n" +
            ") orda\n" +
            "left join \n" +
            "(\n" +
            "select\n" +
            "useruuid\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (2,17,3,18,4,5,6,7,8,19,20)\n" +
            "group by useruuid\n" +
            ") ordb on ordb.useruuid = orda.useruuid\n" +
            "left join  \n" +
            "(\n" +
            "select \n" +
            "useruuid,\n" +
            "max(datediff(actualRefundTime,refundTime)) as dayType\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (10,11) \n" +
            "group by useruuid\n" +
            "having dayType > 7\n" +
            ") ordc on ordc.useruuid = orda.useruuid \n" +
            "left join \n" +
            "(\n" +
            "select \n" +
            "userUuid\n" +
            "from usrBlackList\n" +
            "where disabled = 0\n" +
            "group by userUuid\n" +
            ") black on black.userUuid = orda.useruuid\n" +
            "inner join \n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "realName,\n" +
            "mobileNumberDES, remark \n" +
            "from usrUser\n" +
            "where disabled = 0\n" +
            "and mobileNumberDES is not null and remark != 'operate sms and voice call not send!'\n" +
            ") usr on usr.uuid = orda.userUuid \n" +
            "where ordb.userUuid is null and black.userUuid is null and ordc.useruuid is null\n" +
            "and timestampdiff(hour,repayDay,now()) between 24 and 3*24 #调整时间\n" +
            "order by repayDay;")
    List<UserResponse> getSilenceUser3();

    // 待审核用户
    @Select("select * from ordOrder where status in(3,4) and disabled = 0;")
    List<UsrUser> getUserWithWaitingForReview();


    // 复审流程bug导致的信息不全用户召回
    @Select(" select * from usrUser where uuid in (select userUuid from ordOrder where remark = '复贷流程问题，用户重新下单' and disabled = 1)")
    List<UsrUser> getCancleUser();


    // 处于待绑卡的用户
    @Select("select * from usrUser where uuid in(SELECT userUuid FROM doit.ordOrder where status = 1 and orderStep = 6 and disabled = 0) and disabled = 0;")
    List<UsrUser> getUserWithBindCardFaild();

    // 处于待放款的用户
    @Select("select * from usrUser where uuid in(SELECT userUuid FROM doit.ordOrder where status = 5 and disabled = 0) and disabled = 0;")
    List<UsrUser> getUserWithNotLoan();

    // 处于代还款的用户
    @Select("select count(*) from usrUser where uuid in(SELECT userUuid FROM doit.ordOrder where status in(7,8) and disabled = 0);")
    List<UsrUser> getUserWithLoaning();

    // 借过款的用户
    @Select("select * from usrUser where uuid in(select userUuid from ordOrder where status in(7,10) and disabled = 0) and disabled = 0 limit 30000;")
    List<UsrUser> getUserWithHadLoan();

    // 没有复借的用户
    @Select("SELECT \n" +
            "  mobileNumberDES\n" +
            "FROM\n" +
            "    (SELECT \n" +
            "        MAX(actualRefundTime) AS actualDay, useruuid\n" +
            "    FROM\n" +
            "        ordOrder\n" +
            "    WHERE\n" +
            "        disabled = 0 AND status IN (10 , 11)\n" +
            "    GROUP BY useruuid) a\n" +
            "        LEFT JOIN\n" +
            "    (SELECT \n" +
            "        useruuid\n" +
            "    FROM\n" +
            "        ordOrder\n" +
            "    WHERE\n" +
            "        disabled = 0 AND status IN (7 , 8)\n" +
            "    GROUP BY useruuid) b ON b.useruuid = a.useruuid\n" +
            "        LEFT JOIN\n" +
            "    (SELECT \n" +
            "        userUuid\n" +
            "    FROM\n" +
            "        usrBlackList\n" +
            "    WHERE\n" +
            "        disabled = 0\n" +
            "    GROUP BY userUuid) c ON c.userUuid = a.useruuid\n" +
            "        LEFT JOIN\n" +
            "    (SELECT \n" +
            "        useruuid,\n" +
            "            MAX(DATEDIFF(actualRefundTime, refundTime)) AS dayType\n" +
            "    FROM\n" +
            "        ordOrder\n" +
            "    WHERE\n" +
            "        disabled = 0 AND status IN (10 , 11)\n" +
            "    GROUP BY useruuid\n" +
            "    HAVING dayType > 7) d ON d.useruuid = a.useruuid\n" +
            "        INNER JOIN\n" +
            "    (SELECT \n" +
            "        uuid, realName, mobileNumberDES\n" +
            "    FROM\n" +
            "        usrUser\n" +
            "    WHERE\n" +
            "        disabled = 0\n" +
            "            AND mobileNumberDES IS NOT NULL) e ON e.uuid = a.useruuid\n" +
            "WHERE\n" +
            "    b.userUuid IS NULL\n" +
            "        AND c.userUuid IS NULL\n" +
            "        AND d.useruuid IS NULL\n" +
            "        AND TIMESTAMPDIFF(HOUR, actualDay, NOW()) > 24 * 3\n" +
            "ORDER BY actualDay;")
    List<String> getUserMobileWithNotRebrow();


    // 已借款的用户
    @Select("select * from usrUser where uuid in(select DISTINCT(userUuid) from ordOrder where status in (7,8,10,11) and disabled = 0 order by userUuid) and disabled = 0")
    List<String> getUserMobileWithInviteFriend();

    // 联系人填写被误拒 的用户
    @Select("select * from usrUser where uuid in(\n" +
            "select userUuid from ordBlack t where ruleHitNo ='25-AUTO_CALL_REJECT_BACKUP_LINKMAN_VALID_COUNT' and remark = '没有备选联系人召回充填') and disabled=0;")
    List<UsrUser> getUserMobileWithAutoCallFaild();

    //相同姓名、税卡实名通过的其他用户
    @Select("select uuid from usrUser u where u.realName=#{realName} and u.uuid !=#{userUuid} and u.disabled=0 " +
            " and exists (select 1 from usrCertificationInfo s where s.userUuid = u.uuid and s.certificationType =12 and s.certificationData = #{taxNumber})" +
            " and exists (select 1 from usrCertificationInfo s where s.userUuid = u.uuid and s.certificationType in (1,12) and s.certificationResult = 1)")
    List<String> userWithSameRealNameAndTaxNumber(@Param("realName") String realName, @Param("taxNumber") String taxNumber,
                                                  @Param("userUuid") String userUuid);


    @Select("<script>" +
            " select count(1) from ordOrder o where o.disabled=0 and o.status not in (1,2,12,15) and o.userUuid in "
            + "<foreach collection='userList' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>"+
            "</script>")
    Integer existsSuccessOrder(@Param("userList") List<String> userList);


    // 税卡被拒绝用户
    @Select("select * from usrUser where uuid in(SELECT userUuid FROM  doit.usrVerifyResult where verifyResult =2 and verifyType = 3 and createTime > '2018-10-01 00:00:00' and createTime < '2018-10-17 00:00:00' and disabled =0);")
    List<UsrUser> getUserMobileWithVervifyFaild();

    // 提醒alfmart还款
    @Select("Select * from doit.usrUser where uuid in (select userUuid from doit.ordOrder where disabled = 0  and dateDiff(now(), refundTime) >= -7 and DATEDIFF(now(),refundTime) <= 7 and status in (7,8)) and disabled = 0 and status = 1;")
    List<UsrUser> sendSmsToUseAlfamart();

    // 风控规则拒绝用户召回
    @Select("Select * from doit.usrUser where uuid in (select userUuid from doit.ordOrder where disabled = 0  and dateDiff(now(), refundTime) >= -7 and DATEDIFF(now(),refundTime) <= 7 and status in (7,8)) and disabled = 0 and status = 1;")
    List<UsrUser> sendSmsToUseWithRefuse();

    // 风控规则拒绝用户召回
    @Select("select * from usrUser where uuid in(select userUuid from ordBlack b where b.ruleHitNo = '9-GOJEK_EMAIL_NOT_SAME_AND_MOBILE_NOT_SAME'\n" +
            "and b.remark='规则误拒，短信召回用户') and disabled = 0;\n" +
            "\n" +
            "\n" +
            "select * from usrUser where uuid in(select userUuid from ordBlack b where b.ruleHitNo in ('7-MOBILE_LANGUAGE','5-SHORT_MESSAGE_KEY_INfO_EMPTY','4-USER_CALL_RECORDS_KEY_INfO_EMPTY') and\n" +
            "                               disabled =0\n" +
            "and exists(\n" +
            "    select  1 from ordDeviceInfo d where d.deviceType='iOS' and d.orderNo = b.orderNo\n" +
            "                                   ) ) and disabled = 0;\n" +
            "\n")
    List<UsrUser> sendSmsToUseWithRefuse2();


    // 风控规则拒绝用户召回
    @Select("Select * from doit.usrUser where uuid in (select distinct userUuid from ordBlack b where b.remark='规则阈值取错发短信召回1116') and disabled = 0 and status = 1;")
    List<UsrUser> sendSmsToUseWithRefuse3();

    @Select("\n" +
            "#申请未提交\n" +
            "select \n" +
            "'1' as orderNo,\n" +
            "orda.userUuid as uuid,\n" +
            "mobileNumberDES\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "userUuid\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and date(createTime) between date_sub(curdate(),interval 3 day) and date_sub(curdate(),interval 1 day)#调整时间\n" +
            "group by userUuid\n" +
            ") orda\n" +
            "left join \n" +
            "(\n" +
            "select \n" +
            "userUuid\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status >= 2\n" +
            "group by userUuid\n" +
            ") ordb on ordb.userUuid = orda.userUuid \n" +
            "inner join \n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "realName,\n" +
            "mobileNumberDES,remark \n" +
            "from usrUser\n" +
            "where disabled = 0\n" +
            "and mobileNumberDES is not null\n" +
            ") usr on usr.uuid = orda.userUuid \n" +
            "where ordb.userUuid is null and usr.remark != 'operate sms and voice call not send!';")
    List<UserResponse> sendSmsToUseWithNotVerifyOrder();

    @Select("#降额未确认放款\n" +
            "select\n" +
            "'3' as orderNo,\n" +
            "ord.useruuid as uuid,\n" +
            "mobileNumberDES\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "useruuid\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (19) \n" +
            "and date(updateTime) between date_sub(curdate(),interval 3 day) and date_sub(curdate(),interval 1 day)#调整时间\n" +
            "group by useruuid\n" +
            ") ord\n" +
            "inner join \n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "realName,\n" +
            "mobileNumberDES, remark \n" +
            "from usrUser\n" +
            "where disabled = 0\n" +
            "and mobileNumberDES is not null\n" +
            ") usr on usr.uuid = ord.useruuid and usr.remark != 'operate sms and voice call not send!';")
    List<UserResponse> sendReduceSms();

    // 30w已还款用户 没有再复借
    @Select("select * from usrUser where uuid in(select userUuid from ordOrder o where o.amountApply=300000.00 and disabled=0 and status in (10,11) and orderType = 0\n" +
            "   and not exists(\n" +
            "   select 1 from ordOrder oo where oo.userUuid=o.userUuid and oo.id>o.id\n" +
            "   ) );")
    List<UsrUser> sendSmsToUseWithNotReLoanAfter20W();


    // 风控规则拒绝用户召回
    @Select("select * from usrUser where uuid in(select distinct userUuid from doit.ordBlack\n" +
            "where disabled = 0 and responseMessage = '公司电话不是大雅加达和爪哇岛和巴厘岛区号开头' \n" +
            ") and disabled = 0;")
    List<UsrUser> sendSmsToUseWithRefuse20181210();

    @Select("select * from usrUser where uuid in(select distinct userUuid from doit.ordBlack\n" +
            "where disabled = 0 and responseMessage = '工作地址不属于雅加达大区和爪哇岛和巴厘岛' \n" +
            " and date(createTime)<'2018-11-09'\n" +
            " ) and disabled = 0;")
    List<UsrUser> sendSmsToUseWithRefuse2018121101();

    @Select("select * from usrUser where uuid in( select distinct b.userUuid from doit.ordBlack b\n" +
            "where b.disabled = 0 and b. responseMessage = '手机通话记录为空' \n" +
            " and date(b.createTime)<'2018-11-09'\n" +
            "and b.userUuid not in (select distinct uuid from doit.usrUser where disabled = 0 and userSource = 28)\n" +
            ") and disabled = 0;")
    List<UsrUser> sendSmsToUseWithRefuse2018121102();

    @Select("select * from usrUser where uuid in( select distinct b.userUuid from doit.ordBlack b\n" +
            "where b.disabled = 0 and b. responseMessage = '去重通讯录个数' \n" +
            " and date(b.createTime)<'2018-11-09'\n" +
            "and b.userUuid not in (select distinct uuid from doit.usrUser where disabled = 0 and userSource = 28)\n" +
            ") and disabled = 0;")
    List<UsrUser> sendSmsToUseWithRefuse2018121103();

    @Select("select * from usrUser where uuid in(select distinct userUuid,orderNo from doit.ordBlack\n" +
            "where disabled = 0 and responseMessage = '手机使用时长（单位：天）' \n" +
            " and date(createTime)<'2018-11-09'\n" +
            ") and disabled = 0;")
    List<UsrUser> sendSmsToUseWithRefuse2018121104();


    @Select("<script>" +
            "\n" +
            "select count(1) from usrUser u  where u.mobileNumberDES in " +
            "<foreach collection='mobiles' item='item' separator=',' open='(' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "and exists(\n" +
            "select 1 from ordOrder o where o.userUuid = u.uuid and o.borrowingCount = 1 and o.amountApply=1200000 and o.disabled=0 \n" +
            "and o.status in (11,10)\n" +
            " <![CDATA[ "+
            " and datediff(o.actualRefundTime,o.refundTime)<" +
            "]]> #{days}" +
            ");" +
            "</script>")
    Integer countOfOverdueLessThan5UsersByMobiles(@Param("mobiles") List<String> mobiles, @Param("days") Integer days);

    @Select("select count(*) from teleCallResult t where t.callType = 3\n" +
            "and t.disabled=0 \n" +
            "and t.userUuid in (\n" +
            "select userUuid from usrProductRecord where disabled = 0\n" +
            ")\n" +
            "and exists(\n" +
            "select 1 from ordOrder o where o.userUuid = t.userUuid and o.borrowingCount = 1 and o.amountApply=1200000 and o.status = 10\n" +
            ")\n" +
            "and t.tellNumber = #{mobile}")
    Integer countemergeInIQorGood600(@Param("mobile") String mobile);

    @Select("select count(1) from usrUser a where a.disabled=0 and a.mobileNumberDES=#{mobileDesc} and productLevel>0")
    Integer mobileExistInUpLoanLimitInfo(@Param("mobileDesc") String mobileDesc);

    @Select("select count(1) from usrUser a where a.disabled=0 and a.mobileNumberDES=#{mobileDesc} and exists(select 1 from ordOrder o where o" +
            ".userUuid = a.uuid and" +
            " o.amountApply = 1200000.00 and o.disabled=0)")
    Integer mobileExistInFirstBorrow600NotOverdueUser(@Param("mobileDesc") String mobileDesc);


    @Select("<script>" +
            "\n" +
            "select count(1) from usrUser u  where u.uuid in " +
            "<foreach collection='userList' item='item' separator=',' open='(' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "and exists(\n" +
            "select 1 from ordOrder o where o.userUuid = u.uuid and o.borrowingCount = 1 and o.amountApply=1200000 and o.disabled=0 \n" +
            "and o.status in (11,10)\n" +
            " <![CDATA[ "+
            " and datediff(o.actualRefundTime,o.refundTime)<" +
            "]]> #{days}" +
            ");" +
            "</script>")
    Integer countOfOverdueLessThanNUsersByUserIds(@Param("userList") List<String> userList, @Param("days") Integer days);


    @Select("select count(*) from usrUser where idCardNo != ''and realName != '' and disabled = 0;")
    List<UsrUser> getAllRegistUser();

    @Select("select * from usrUser limit 1;")
    List<UsrUser> getAllUser();
}
