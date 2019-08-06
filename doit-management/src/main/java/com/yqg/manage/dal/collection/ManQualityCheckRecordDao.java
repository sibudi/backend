package com.yqg.manage.dal.collection;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.collection.ManQualityCheckRecord;
import com.yqg.manage.scheduling.qualitycheck.response.QualityCheckResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Mapper
public interface ManQualityCheckRecordDao extends BaseMapper<ManQualityCheckRecord> {

    @Select("select o.uuid as orderNo , o.userUuid as userUuid from ordOrder o where o.disabled = 0 and " +
            "(o.status = 7 or o.status = 8) and datediff(now(), o.refundTime) = 0 and (not exists (select 1 from doit.manCollectionRemark where contactType = 1 and orderNo = o.uuid)" +
            "or not exists (select 1 from doit.manCollectionRemark where contactType = 2 and orderNo = o.uuid)); ")
    List<QualityCheckResponse> listQualityCheckD0();

    @Select("select o.uuid as orderNo , o.userUuid as userUuid from ordOrder o where o.disabled = 0 and (o.status = 7 or o.status = 8) and " +
            "exists (select 1 from manCollectionRemark where orderNo = o.uuid and promiseRepaymentTime >= date_sub(now(), INTERVAL 1 HOUR) " +
            "and promiseRepaymentTime < date_sub(now(), INTERVAL 30 MINUTE)); ")
    List<QualityCheckResponse> qualityCheckPromiseTime();

    @Select("select o.uuid as orderNo , o.userUuid as userUuid from ordOrder o where o.disabled = 0 and (o.status = 7 or o.status = 8) and \n" +
            "datediff(now(), o.refundTime) in (${stages})")
    List<QualityCheckResponse> qualityCheckBefore12(@Param("stages") String stages);

    @Select("select count(*) from manCollectionRemark where disabled = 0 and orderNo=#{orderNo} and createTime >= date_sub(now(), INTERVAL #{hours} HOUR);")
    Integer hitOrNot (@Param("orderNo") String orderNo, @Param("hours") Integer hours);

    @Select("select o.uuid as orderNo , o.userUuid as userUuid from ordOrder o where o.disabled = 0 and (o.status = 7 or o.status = 8) and \n" +
            "(datediff(now(), o.refundTime) >=1 and datediff(now(), o.refundTime) < 8);")
    List<QualityCheckResponse> qualityCheckBefore17();
}
