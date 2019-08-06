package com.yqg.risk.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.risk.entity.OrderScore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderScoreDao extends BaseMapper<OrderScore> {
    @Select("select * from orderScore where orderNo=#{orderNo} and modelName = #{modelName} and disabled=0 order by createTime desc limit 1")
    OrderScore getLatestResult(@Param("orderNo") String orderNo,@Param("modelName") String modelName);
}
