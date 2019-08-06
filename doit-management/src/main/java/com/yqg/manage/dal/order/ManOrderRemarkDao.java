package com.yqg.manage.dal.order;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.OrderRemarkSqlProvider;
import com.yqg.manage.entity.check.ManTeleReviewQuestion;
import com.yqg.manage.entity.system.ManOrderRemark;
import com.yqg.manage.scheduling.check.request.TeleReviewRequest;
import com.yqg.manage.service.order.request.ManOrderRemarkRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @Author Jacob
 */
@Mapper
public interface ManOrderRemarkDao extends BaseMapper<ManOrderRemark> {

    @Select("select * from manOrderRemark where type = #{type} and orderNo = #{orderNo} and disabled = 0 order by updateTime ASC ")
    List<ManOrderRemark> getManOrderRemarkByOrderNo(@Param("type") Integer type, @Param("orderNo") String orderNo);

    /**
     * 获得状态为1和3的电核
     * @param type
     * @param orderNo
     * @return
     */
    @Select("select * from manOrderRemark where type in (1,3) and orderNo = #{orderNo} and description != '' and disabled = 0 order by updateTime ASC ")
    List<ManOrderRemark> getManOrderRemark(@Param("orderNo") String orderNo);
}
