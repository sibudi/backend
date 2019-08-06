package com.yqg.order.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRiskRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/12/18.
 */
@Mapper
public interface OrdRiskRecordDao extends BaseMapper<OrdRiskRecord> {
//
//    // 获取上一次贷款APP个数 / 比率
//    @Select("select * from ordRiskRecord where ruleDetailType like '%${ruleType}' " +
//            "and userUuid = #{userUuid} and disabled = 0 order by createTime" )
//    public List<OrdRiskRecord> getLastAppNumOrRate(@Param("ruleType") String ruleType,@Param("userUuid") String userUuid);
//
//    // 获取面电核规则
////    @Select("select ruleDetailType , ruleRealValue from ordRiskRecord where ruleDetailType in" +
////            " ('COMB_2_FEMALE_FREE_PHONE_CHECK','COMB_3_SEX_PHONE_LANGUAGE','COMB_RECENT30_MISCALL_FEMALE_MARRIAGE_EDUCATION','COMB_AGE_FEMALE_EDUCATION') " +
////            " and userUuid = #{userUuid} and orderNo = #{orderNo} and disabled = 0 order by createTime" )
////    List<OrdRiskRecord> getOrdRiskByType (@Param("userUuid") String userUuid, @Param("orderNo") String orderNo);
//
//    @Select("select ruleDetailType , ruleRealValue from ordRiskRecord where ruleDetailType in" +
//            " (select u.ruleDetailType  from sysAutoReviewRule u where u.ruleType = 21 and u.disabled=0) " +
//            " and userUuid = #{userUuid} and orderNo = #{orderNo} and disabled = 0 order by createTime" )
//    List<OrdRiskRecord> getOrdRiskByType (@Param("userUuid") String userUuid, @Param("orderNo") String orderNo);
//
//
//    // 获取面电核规则
//    @Select("select * from ordRiskRecord where orderNo = #{orderNo}" )
//    List<OrdRiskRecord> getByOrderNo (@Param("orderNo") String orderNo);
//
//    @Select("select * from ordRiskRecord where orderNo=#{orderNo} and disabled =0 and ruleDetailType in ('CASH2_FACE_PLUS_PLUS_SCORE','YITU_SCORE_LIMIT')")
//    List<OrdRiskRecord> getFaceVerifyScoreRuleResult(@Param("orderNo") String orderNo);

//    /***
//     * 相同信名，相同的税卡号，非本人之前成功借款且实名成功过
//     * @param realName
//     * @param taxNumber
//     * @param userUuid
//     * @return
//     */
//
//    @Select("select count(1) from usrUser u where u.realName=#{realName} and u.uuid !=#{userUuid} and u.disabled=0\n" +
//            "  and exists(\n" +
//            "      -- 有过成功借款\n" +
//            "      select 1 from ordOrder o where o.userUuid = u.uuid and o.status not in (1,2,12,15) and o.disabled=0\n" +
//            "      and exists (\n" +
//            "         select 1 from ordRiskRecord b where b.orderNo = o.uuid and b.ruleDetailType = 'ADVANCE_VERIFY_RULE'\n" +
//            "         and b.ruleRealValue in ('用户之前实名认证已经认证成功','聚信立实名认证已经认证成功','姓名完全匹配成功','姓名模糊匹配成功',\n" +
//            "         '税卡姓名完全匹配','税卡姓名模糊匹配成功')\n" +
//            "      )\n" +
//            "      \n" +
//            "  ) and exists(\n" +
//            "     select 1 from usrCertificationInfo s where s.userUuid = u.uuid and s.certificationType =12 and s.certificationData = " +
//            "#{taxNumber}\n" +
//            "  );")
//    Integer countOfSameNameAndTaxNumber(@Param("realName") String realName, @Param("taxNumber") String taxNumber,@Param("userUuid") String userUuid);

//
//    @Select("select * from ordRiskRecord where orderNo=#{orderNo} and disabled=0 and ruleDetailType = #{ruleDetailType} limit 1")
//    OrdRiskRecord getRuleResultByRuleName(@Param("ruleDetailType") String ruleDetailType,@Param("orderNo") String orderNo);

//    @Select("select distinct orderNo from ordRiskRecord where id>=#{startId} and id<#{endId} and disabled=0")
//    List<String> getReviewedOrders(@Param("startId") Integer startId,@Param("endId")Integer endId);
//
//    @Select("select * from ordRiskRecord where orderNo=#{orderNo} and disabled=0")
//    List<OrdRiskRecord> getOrderRiskRecordByOrderNo(@Param("orderNo") String orderNo);
}
