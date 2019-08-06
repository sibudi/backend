package com.yqg.manage.dal.user;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.check.ManSecondCheckRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * Author: tonggen
 * Date: 2018/6/13
 * time: 下午2:52
 */
@Mapper
public interface ManSecondCheckRecordDao extends BaseMapper<ManSecondCheckRecord> {

//    /**
//     * 通过订单号，查询是否有
//     * @param uuid
//     * @return
//     */
//    @Select("select max(updateTime) from manOrderRemark where orderNo = #{orderNo} and disabled = 0 and (type = 1 or type = 3)")
//    String getManRemarkCount(@Param("orderNo") String uuid);
}
