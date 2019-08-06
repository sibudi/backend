package com.yqg.risk.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.risk.entity.RiskErrorLog;
import com.yqg.order.entity.OrdOrder;
import com.yqg.system.entity.SysThirdLogs;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/*****
 * @Author Jeremy Lawrence
 * Created at 2018/2/7
 *
 *
 ****/

@Mapper
public interface RiskErrorLogDao extends BaseMapper<RiskErrorLog> {

    @Select("select count(1) from riskErrorLog where orderNo=#{orderNo} and disabled = 0")
    Integer errorCount(@Param("orderNo") String orderNo);

    @Select("select * from sysThirdLogs s where s.response like '%\"name\":\"\"%'\n" +
            "and s.response <>'{\"code\":\"30000\",\"name\":\"\",\"message\":\"account_number invalid\"}'\n" +
            "and s.createTime>='2018-08-22'\n" +
            "and exists(\n" +
            "select 1 from ordOrder o where o.uuid = s.orderNo and o.status=12\n" +
            ");")
    List<SysThirdLogs> getErrorData();


    /**
     * 每次处理两单
     *
     * @param errorType
     * @return
     */
    @Select("\n" +
            " select * from riskErrorLog ss where ss.disabled=0\n" +
            "  and ss.errorType = #{errorType} \n" +
            "  order by times,createTime  limit 2 ;")
    List<RiskErrorLog> getTaxNumberNeedRetryOrders(@Param("errorType") Integer errorType);

    @Update("update riskErrorLog set times = #{times} where id = #{id}")
    Integer updateTimes(@Param("times") Integer times, @Param("id") Integer id);

    @Update("update riskErrorLog set disabled=1 where id = #{id}")
    Integer disabledErrorLog(@Param("id") Integer id);

    @Update("update riskErrorLog set disabled=1,remark=#{remark} where id = #{id}")
    Integer disabledErrorLogWithRemark(@Param("id") Integer id, @Param("remark") String remark);


    @Select("select * from ordOrder oo where oo.uuid in (\n" +
            "select orderNo from ordBlack b where b.disabled =0\n" +
            "                                      and b.ruleHitNo in ('15-COMB_MOBILE_CAP_FIRSTLINKEMANRECENT180CALLCOUNT_MALE',\n" +
            "                                                                 '15-COMB_MOBILE_CAP_MARITAL_EDUCATION')\n" +
            "and exists(\n" +
            "select 1 from usrCertificationInfo ss where ss.certificationType in (1,12) and ss.certificationResult = 1\n" +
            "));")
    List<OrdOrder> getOrdersWithErrorRules();


    @Insert("insert into zxc_test(orderNo,test_remark) values(#{orderNo},#{remark})")
    Integer addReRunResult(@Param("orderNo") String orderNo, @Param("remark") String remark);


    @Select("select createTime from ordBlack b where b.orderNo=#{orderNo} and disabled=0 limit 1")
    Date getMachineCheckDate(@Param("orderNo") String orderNo);

    @Select("select count(1) from riskErrorLog where orderNo=#{orderNo} and errorType = 1")
    Integer existsTaxReRun(@Param("orderNo") String orderNo);

}
