package com.yqg.manage.dal.order;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.CouponRecordSqlProvider;
import com.yqg.manage.service.order.request.CouponRequest;
import com.yqg.manage.service.order.response.CouponResponse;
import com.yqg.manage.service.order.response.TwilioCallCouponResponse;
import com.yqg.order.entity.CouponRecord;
import com.yqg.order.entity.OrdOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Author: tonggen
 * Date: 2019/2/26
 * time: 2:23 PM
 */
@Mapper
public interface ManCouponRecordDao extends BaseMapper<CouponRecord> {

    @Select("select count(*) from couponRecord record join couponConfig config on config.id = record.couponConfigId and " +
            "config.`status` = 0 and config.disabled=0 and record.disabled=0 and record.status = 3 and record.orderNo = #{orderNo};")
    Integer getValidCount(@Param("orderNo") String orderNo);

    @SelectProvider(type = CouponRecordSqlProvider.class, method = "listCouponRecord")
    @Options(useGeneratedKeys = true)
    List<CouponResponse> listCouponRecord(@Param("CouponRequest") CouponRequest searchResquest);


    //sendPerson为0的表示有系统自动分发的。
    @Select("select ord.uuid as orderNo , ord.userUuid as userUuid from ordOrder ord where ord.`status` in (7,8) and ord.disabled = 0 " +
            "and exists (select 1 from couponRecord where disabled = 0 and status = 3 and sendPersion = 0 and datediff(now(), validityStartTime) = 0 and orderNo = ord.uuid and " +
            "couponConfigId in (select id from couponConfig where disabled = 0 and status = 0 and alias = #{alias}));")
    List<TwilioCallCouponResponse> listOrderNeedTwilioCall(@Param("alias") String alias);

    @Update("update couponRecord set status = 2, updateTime = now() where status = 3 and dateDiff(now(), validityEndTime) > 0 and disabled=0;")
    Integer updateCouponStatus();

}
