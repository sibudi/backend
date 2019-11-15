package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdOrder;
import com.yqg.user.entity.UsrBlackList;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UsrBlackListDao extends BaseMapper<UsrBlackList> {

    @Select("<script>"
            + "select count(1) from usrBlackList u where u.disabled=0 and u.type in "
            + "<foreach collection='types' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            " and (u.linkManContactNumber1 in "
            + "<foreach collection='mobileNumbers' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            " or u.linkManContactNumber2 in "
            + "<foreach collection='mobileNumbers' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>)"
            + "</script>")
    Integer mobileIsEmergencyTelForBlackListUser(@Param("mobileNumbers") List<String> mobileNumbers, @Param("types") List<Integer> types);

    @Select("<script>" +
            " select count(1) from ("
            + "select distinct mobileDes from usrBlackList where disabled=0 and type in  "
            + "<foreach collection='types' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            " and mobileDes in "
            + "<foreach collection='mobileNumbers' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            ") aa "
            + "</script>")
    Integer countOfMobilesInBlackList(@Param("mobileNumbers") List<String> mobileNumbers, @Param("types") List<Integer> types);

    @Select("<script>" +
            " select count(1) from ("
            + "select distinct whatsapp from usrBlackList where disabled=0 and type in  "
            + "<foreach collection='types' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            " and whatsapp in "
            + "<foreach collection='whatsappList' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            ") aa "
            + "</script>")
    Integer countOfWhatsappInBlackList(@Param("whatsappList") List<String> whatsappList, @Param("types") List<Integer> types);



    @Select("<script> " +
            "select count(1) from usrBlackList where disabled=0  and type in "
            + "<foreach collection='types' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            " and deviceId=#{deviceId} and deviceId!='' and deviceId is not null " +
            " </script>")
    Integer deviceIdInBlackList(@Param("deviceId") String deviceId, @Param("types") List<Integer> types);

    @Select("<script> " +
            "select count(1) from usrBlackList where disabled=0  and type in "
            + "<foreach collection='types' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            " and companyTel=#{companyTel} and companyTel!='' and companyTel is not null " +
            " </script>")
    Integer companyTelInBlackList(@Param("companyTel") String companyTel, @Param("types") List<Integer> types);

    @Select("<script> " +
            "select count(1) from usrBlackList where disabled=0  and type in "
            + "<foreach collection='types' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            " and md5ForCompanyAddress=#{companyAddress} and md5ForCompanyAddress!='' and md5ForCompanyAddress is not null " +
            " </script>")
    Integer companyAddressInBlackList(@Param("companyAddress") String companyAddress, @Param("types") List<Integer> types);

    @Select("<script> " +
            "select count(1) from usrBlackList where disabled=0  and type in "
            + "<foreach collection='types' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            " and md5ForHomeAddress=#{homeAddress} and md5ForHomeAddress!='' and md5ForHomeAddress is not null " +
            " </script>")
    Integer homeAddressInBlackList(@Param("homeAddress") String homeAddress, @Param("types") List<Integer> types);

    @Select("<script> " +
            "select count(1) from usrBlackList where disabled=0  and type in "
            + "<foreach collection='types' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            " and imei=#{imei} and imei!=''  and imei is not null   " +
            " </script>")
    Integer imeiInBlackList(@Param("imei") String imei, @Param("types") List<Integer> types);

    @Select("<script> " +
            "select count(1) from usrBlackList where disabled=0  and type in  "
            + "<foreach collection='types' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            " and idCardNo=#{idCardNo} and idCardNo!=''   and idCardNo is not null   " +
            " </script>")
    Integer idCardNoInBlackList(@Param("idCardNo") String idCardNo, @Param("types") List<Integer> types);

    @Select("<script> " +
            "select count(1) from usrBlackList where disabled=0  and type in "
            + "<foreach collection='types' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            " and bankCardNumber=#{bankCardNumber} and bankCardNumber!=''  and bankCardNumber is not null   " +
            " </script>")
    Integer bankcardNumberInBlackList(@Param("bankCardNumber") String bankCardNumber, @Param("types") List<Integer> types);

    @Select("select userUuid from usrBlackList where disabled =0 and type = 5 and userUuid!='审核人员or催收人员' " +
            "  and userUuid !='' and userUuid is not null")
    List<String> getFraudUsersId();

    @Select("<script>" +
            "  select * from usrBlackList where disabled=0 and userUuid!='审核人员or催收人员'  and userUuid !='' and userUuid is not null and type in "
            + "<foreach collection='types' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            "</script>")
    List<UsrBlackList> getUsrBlackListByTypes(@Param("types") List<Integer> types);

    @Insert("insert into usrBlackList(uuid,userUuid,deviceId,imei,idCardNo,mobileDes,linkManContactNumber1,linkManContactNumber2,bankCardNumber,createTime,\n" +
            "                         updateTime,type)\n" +
            "SELECT\n" +
            "  REPLACE(UUID(), '-', ''),\n" +
            "  u.uuid userUuid,\n" +
            "  d.deviceId,\n" +
            "  d.IMEI,\n" +
            "  u.idCardNo,\n" +
            "  u.mobileNumberDES,\n" +
            "  replace(replace(link.linkman1,'-',''),' ',''),\n" +
            "  replace(replace(link.linkman2,'-',''),' ',''),\n" +
            "  ub.bankNumberNo,\n" +
            "  now() createTime,\n" +
            "  now() updateTime,\n" +
            "  7 type " +
            "FROM\n" +
            "  usrUser u\n" +
            "  LEFT JOIN\n" +
            "  -- 设备信息\n" +
            "  (SELECT DISTINCT\n" +
            "     dd.userUuid,\n" +
            "     dd.deviceId,\n" +
            "     dd.IMEI,\n" +
            "     count(1)\n" +
            "   FROM ordDeviceInfo dd\n" +
            "   WHERE disabled = 0\n" +
            "   GROUP BY dd.userUuid, dd.deviceId, dd.IMEI\n" +
            "  ) d ON u.uuid = d.userUuid\n" +
            "  LEFT JOIN\n" +
            "  -- 联系人信息\n" +
            "  (\n" +
            "    SELECT\n" +
            "      u1.userUuid,\n" +
            "      u1.contactsMobile linkman1,\n" +
            "      u2.contactsMobile linkman2\n" +
            "    FROM usrLinkManInfo u1\n" +
            "      JOIN usrLinkManInfo u2 ON u1.userUuid = u2.userUuid\n" +
            "    WHERE u1.sequence = 1 AND u1.disabled = 0\n" +
            "          AND u2.sequence = 2 AND u2.disabled = 0\n" +
            "  ) link ON u.uuid = link.userUuid\n" +
            "  -- 银行卡信息\n" +
            "  LEFT JOIN\n" +
            "  usrBank ub ON u.uuid = ub.userUuid AND ub.isRecent = 1 AND ub.disabled = 0\n" +
            "WHERE\n" +
            "  u.disabled = 0\n" +
            "  AND u.uuid IN (\n" +
            "    --  逾期30天及以上用户\n" +
            "    SELECT userUuid\n" +
            "    FROM\n" +
            "      ordOrder o\n" +
            "    WHERE\n" +
            "      o.disabled = 0\n" +
            "      AND o.refundTime IS NOT NULL\n" +
            " and (case when status=11 then date(actualRefundTime)=current_date()\n" +
            "            else true\n" +
            "            end)"+
            "      AND DATEDIFF((CASE\n" +
            "                    WHEN o.actualRefundTime IS NULL\n" +
            "                      THEN NOW()\n" +
            "                    ELSE o.actualRefundTime\n" +
            "                    END),\n" +
            "                   o.refundTime) =7);")
    Integer addOverdue7Users();


    @Update("UPDATE  usrBlackList b set b.type = 3,updateTime = now() where b.type=4\n" +
            "and exists(\n" +
            "    select 1 from ordOrder o where o.disabled =0 AND o.refundTime IS NOT NULL and o.userUuid = b.userUuid\n" +
            "                                   AND DATEDIFF((CASE\n" +
            "                                                 WHEN o.actualRefundTime IS NULL\n" +
            "                                                   THEN NOW()\n" +
            "                                                 ELSE o.actualRefundTime\n" +
            "                                                 END),\n" +
            "                                                o.refundTime) >= 30\n" +
            ");")
    Integer transUserFromOverdue15To30();

    @Update("UPDATE  usrBlackList b set b.type = 4,updateTime = now() where b.type=7\n" +
            "and exists(\n" +
            "    select 1 from ordOrder o where o.disabled =0 AND o.refundTime IS NOT NULL and o.userUuid = b.userUuid\n" +
            "                                   AND DATEDIFF((CASE\n" +
            "                                                 WHEN o.actualRefundTime IS NULL\n" +
            "                                                   THEN NOW()\n" +
            "                                                 ELSE o.actualRefundTime\n" +
            "                                                 END),\n" +
            "                                                o.refundTime) >= 15\n" +
            ");")
    Integer transUserFromOverdue7To15();



    @Select("select * from usrBlackList b where b.updateTime>=date(now()) and b.updateTime>b.createTime and b.type=4 and b.disabled=0 "+
            " and exists(\n" +
            "    select 1 from ordOrder o where o.disabled =0 AND o.refundTime IS NOT NULL and o.userUuid = b.userUuid\n" +
            "                                   AND DATEDIFF((CASE\n" +
            "                                                 WHEN o.actualRefundTime IS NULL\n" +
            "                                                   THEN NOW()\n" +
            "                                                 ELSE o.actualRefundTime\n" +
            "                                                 END),\n" +
            "                                                o.refundTime) = 15\n" +
            ");")
    List<UsrBlackList> getCurrentDayTransFrom7to15();

    @Select("select * from usrBlackList b where b.createTime>=date(now()) and  b.type=7 and b.disabled=0 "+
            " and exists(\n" +
            "    select 1 from ordOrder o where o.disabled =0 AND o.refundTime IS NOT NULL and o.userUuid = b.userUuid\n" +
            "                                   AND DATEDIFF((CASE\n" +
            "                                                 WHEN o.actualRefundTime IS NULL\n" +
            "                                                   THEN NOW()\n" +
            "                                                 ELSE o.actualRefundTime\n" +
            "                                                 END),\n" +
            "                                                o.refundTime) = 7\n" +
            ");")
    List<UsrBlackList> getCurrentDayOverdue7();


    @Insert("insert into usrBlackList(uuid,userUuid,deviceId,imei,idCardNo,mobileDes,linkManContactNumber1,linkManContactNumber2," +
            "bankCardNumber,createTime,\n"+
                   "                         updateTime,type)\n"+
                   "SELECT\n"+
                   "  REPLACE(UUID(), '-', ''),\n"+
                   "  u.uuid userUuid,\n"+
                   "  d.deviceId,\n"+
                   "  d.IMEI,\n"+
                   "  u.idCardNo,\n"+
                   "  u.mobileNumberDES,\n"+
                   "  replace(replace(link.linkman1,'-',''),' ',''),\n"+
                   "  replace(replace(link.linkman2,'-',''),' ',''),\n"+
                   "  ub.bankNumberNo,\n"+
                   "  now() createTime,\n"+
                   "  now() upateTime,\n"+
                   "  5\n"+
                   "FROM\n"+
                   "  usrUser u\n"+
                   "  LEFT JOIN\n"+
                   "  -- 设备信息\n"+
                   "  (SELECT DISTINCT\n"+
                   "     dd.userUuid,\n"+
                   "     dd.deviceId,\n"+
                   "     dd.IMEI,\n"+
                   "     count(1)\n"+
                   "   FROM ordDeviceInfo dd\n"+
                   "   WHERE disabled = 0\n"+
                   "   GROUP BY dd.userUuid, dd.deviceId, dd.IMEI\n"+
                   "  ) d ON u.uuid = d.userUuid\n"+
                   "  LEFT JOIN\n"+
                   "  -- 联系人信息\n"+
                   "  (\n"+
                   "    SELECT\n"+
                   "      u1.userUuid,\n"+
                   "      u1.contactsMobile linkman1,\n"+
                   "      u2.contactsMobile linkman2\n"+
                   "    FROM usrLinkManInfo u1\n"+
                   "      JOIN usrLinkManInfo u2 ON u1.userUuid = u2.userUuid\n"+
                   "    WHERE u1.sequence = 1 AND u1.disabled = 0\n"+
                   "          AND u2.sequence = 2 AND u2.disabled = 0\n"+
                   "  ) link ON u.uuid = link.userUuid\n"+
                   "  -- 银行卡信息\n"+
                   "  LEFT JOIN\n"+
                   "  usrBank ub ON u.uuid = ub.userUuid AND ub.isRecent = 1 AND ub.disabled = 0\n"+
                   "WHERE\n"+
                   "  u.disabled = 0\n"+
                   "  AND u.uuid = #{userUuid}")
    Integer addFraudUser(@Param("userUuid") String userUuid);

    @Select("select count(1) from usrBlackList where disabled=0 and userUuid = #{userUuid} and type = 5")
    Integer existsFraudUser(@Param("userUuid") String userUuid);


    /***
     * 查询欺诈用户的所有订单
     * @return
     */
    @Select("select * from ordOrder o where o.userUuid in (\n" +
            "select b.userUuid from usrBlackList b where b.type=5 and userUuid!='审核人员or催收人员' and userUuid !='' and userUuid is not null) ;")
    List<OrdOrder> getFraudOrders();

    @Select("select userUuid from ordOrder where uuid in (select orderNo from zxc_risk_fix ) and disabled=0")
    List<String> getUserIds();

    @Select("<script>" +
            "  select * from usrBlackList where disabled=0  and userUuid!='' and userUuid in "
            + "<foreach collection='userList' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            "</script>")
    List<UsrBlackList> getFixUsrBlackList(@Param("userList") List<String> userIdList);


    @Insert("<script>"
            + " insert into usrBlackList(uuid,userUuid,remark,type,createTime,updateTime,mobileDes)  "
            + " values "
            + "   <foreach collection='mobiles' item='record' separator=',' >"
            + "     (replace(uuid(), '-', ''),"
            + "      '',"
            + "      'auto-add-sensitive', #{type},"
            + "      now(),"
            + "      now()," +
            "         #{record})"
            + "   </foreach>"
            + " </script>")
    Integer addSensitiveUser(@Param("mobiles") List<String> mobiles,@Param("type") Integer type);

    @Select("<script>"+
            "select mobileDes from usrBlackList where disabled=0  and type =#{type} and mobileDes in "
            + "   <foreach collection='mobiles' item='item' separator=',' open='(' close=')' >"
            +" #{item}"
            + "   </foreach>"
            + " </script>")
    List<String> existsUserByMobiles(@Param("mobiles") List<String> mobiles,@Param("type") Integer type);

    @Select("<script>"+
            "select * from ordOrder o where o.uuid in "
            + "   <foreach collection='userIdList' item='item' separator=',' open='(' close=')' >"
            +" #{item}"
            + "   </foreach>"
            + " </script>")
    List<OrdOrder> getFraudUserOrdersByUserId(@Param("userIdList") List<String> userIdList);



    @Insert("insert into usrBlackList(uuid,userUuid,deviceId,imei,idCardNo,mobileDes,linkManContactNumber1,linkManContactNumber2,bankCardNumber,createTime,\n" +
            "                         updateTime,type)\n" +
            "SELECT\n" +
            "  REPLACE(UUID(), '-', ''),\n" +
            "  u.uuid userUuid,\n" +
            "  d.deviceId,\n" +
            "  d.IMEI,\n" +
            "  u.idCardNo,\n" +
            "  u.mobileNumberDES,\n" +
            "  replace(replace(link.linkman1,'-',''),' ',''),\n" +
            "  replace(replace(link.linkman2,'-',''),' ',''),\n" +
            "  ub.bankNumberNo,\n" +
            "  now() createTime,\n" +
            "  now() updateTime,\n" +
            "  4 type FROM\n" +
            "  usrUser u\n" +
            "  LEFT JOIN\n" +
            "  -- 设备信息\n" +
            "  (SELECT DISTINCT\n" +
            "     dd.userUuid,\n" +
            "     dd.deviceId,\n" +
            "     dd.IMEI,\n" +
            "     count(1)\n" +
            "   FROM ordDeviceInfo dd\n" +
            "   WHERE disabled = 0\n" +
            "   GROUP BY dd.userUuid, dd.deviceId, dd.IMEI\n" +
            "  ) d ON u.uuid = d.userUuid\n" +
            "  LEFT JOIN\n" +
            "  -- 联系人信息\n" +
            "  (\n" +
            "    SELECT\n" +
            "      u1.userUuid,\n" +
            "      u1.contactsMobile linkman1,\n" +
            "      u2.contactsMobile linkman2\n" +
            "    FROM usrLinkManInfo u1\n" +
            "      JOIN usrLinkManInfo u2 ON u1.userUuid = u2.userUuid\n" +
            "    WHERE u1.sequence = 1 AND u1.disabled = 0\n" +
            "          AND u2.sequence = 2 AND u2.disabled = 0\n" +
            "  ) link ON u.uuid = link.userUuid\n" +
            "  -- 银行卡信息\n" +
            "  LEFT JOIN\n" +
            "  usrBank ub ON u.uuid = ub.userUuid AND ub.isRecent = 1 AND ub.disabled = 0\n" +
            "WHERE\n" +
            "  u.disabled = 0\n" +
            "  AND u.uuid IN (\n" +
            "    --  逾期30天及以上用户\n" +
            "    SELECT userUuid\n" +
            "    FROM\n" +
            "      ordOrder o\n" +
            "    WHERE\n" +
            "      o.disabled = 0\n" +
            "      AND o.refundTime IS NOT NULL\n" +
            "      AND DATEDIFF((CASE\n" +
            "                    WHEN o.actualRefundTime IS NULL\n" +
            "                      THEN NOW()\n" +
            "                    ELSE o.actualRefundTime\n" +
            "                    END),\n" +
            "                   o.refundTime) >=15\n" +
            "       AND DATEDIFF((CASE\n" +
            "                    WHEN o.actualRefundTime IS NULL\n" +
            "                      THEN NOW()\n" +
            "                    ELSE o.actualRefundTime\n" +
            "                    END),\n" +
            "                   o.refundTime) <30\n" +
            "                   );\n")
    Integer addOverdue15Users();

    @Select("\n" +
            "select * from usrBlackList t where type in (5)")
    List<UsrBlackList> getHistoryBlackList();

    @Select("<script>"+
            "select  count(distinct u.userUuid) from usrLinkManInfo u where u.formatMobile in \n"
            + "<foreach collection='formatMobileList' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            " and exists(\n" +
            "select 1 from ordOrder o where o.userUuid = u.userUuid and o.borrowingCount = #{borrowingCount} and o.status=8\n" +
            "and DATEDIFF(case when o.actualRefundTime is null \n" +
            "                   then now()\n" +
            "\t\t\t  else o.actualRefundTime \n" +
            "              end, o.refundTime)<![CDATA[ >= 7]]>\n" +
            ") ;"+
       "</script>")
    Integer mobileIsLinkmanForUnSettledOverdue7UserWithBorrowingCountN(@Param("formatMobileList") List<String> formatMobileList,
                                                              @Param("borrowingCount") int borrowingCount);

    @Select("<script>"+
            "select  count(distinct u.userUuid) from usrLinkManInfo u where u.formatMobile in \n"
            + "<foreach collection='formatMobileList' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            " and exists(\n" +
            "select 1 from ordOrder o where o.userUuid = u.userUuid and o.borrowingCount = #{borrowingCount} and o.status =11 \n" +
            "and DATEDIFF(case when o.actualRefundTime is null \n" +
            "                   then now()\n" +
            "\t\t\t  else o.actualRefundTime \n" +
            "              end, o.refundTime)<![CDATA[ >= 7]]>\n" +
            ") ;"+
       "</script>")
    Integer mobileIsLinkmanForSettledOverdue7UserWithBorrowingCountN(@Param("formatMobileList") List<String> formatMobileList,
                                                              @Param("borrowingCount") int borrowingCount);

    @Select("<script>"+
            " select count(uuid) from usrUser u where u.mobileNumberDES in \n"
            + "<foreach collection='formatMobileList' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            "              and exists(\n" +
            "               select 1 from ordOrder o where o.userUuid = u.uuid and o.borrowingCount = #{borrowingCount} and o.status in (8,11)\n" +
            "\t\t\t   and DATEDIFF(case when o.actualRefundTime is null \n" +
            "                   then now()\n" +
            "\t\t\t       else o.actualRefundTime \n" +
            "                   end, o.refundTime)<![CDATA[ >= 7]]>\n" +
            "             ) ;"+
            "</script>")
    Integer mobileIsOverdue7UserWithBorrowingCountN(@Param("formatMobileList") List<String> formatMobileList,
                                                    @Param("borrowingCount") int borrowingCount);
    @Select("<script>"+
            " select count(uuid) from usrUser u where u.mobileNumberDES in \n"
            + "<foreach collection='formatMobileList' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            "              and exists(\n" +
            "               select 1 from ordOrder o where o.userUuid = u.uuid and o.borrowingCount = #{borrowingCount} and o.status = 11 \n" +
            "\t\t\t   and DATEDIFF(case when o.actualRefundTime is null \n" +
            "                   then now()\n" +
            "\t\t\t       else o.actualRefundTime \n" +
            "                   end, o.refundTime)<![CDATA[ >= 7]]>\n" +
            "             ) ;"+
            "</script>")
    Integer mobileIsSettledOverdue7UserWithBorrowingCountN(@Param("formatMobileList") List<String> formatMobileList,
                                                    @Param("borrowingCount") int borrowingCount);
    @Select("<script>"+
            " select count(uuid) from usrUser u where u.mobileNumberDES in \n"
            + "<foreach collection='formatMobileList' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>" +
            "              and exists(\n" +
            "               select 1 from ordOrder o where o.userUuid = u.uuid and o.borrowingCount = #{borrowingCount} and o.status = 8 \n" +
            "\t\t\t   and DATEDIFF(case when o.actualRefundTime is null \n" +
            "                   then now()\n" +
            "\t\t\t       else o.actualRefundTime \n" +
            "                   end, o.refundTime)<![CDATA[ >= 7]]>\n" +
            "             ) ;"+
            "</script>")
    Integer mobileIsUnSettledOverdue7UserWithBorrowingCountN(@Param("formatMobileList") List<String> formatMobileList,
                                                    @Param("borrowingCount") int borrowingCount);

    @Select("select * from usrBlackList limit 1;")
    List<UsrBlackList> getAllBlackList();
}
