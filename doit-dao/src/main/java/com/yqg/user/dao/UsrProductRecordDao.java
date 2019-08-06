package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.UsrProductRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by wanghuaizhou on 2018/9/3.
 */
@Mapper
public interface UsrProductRecordDao extends BaseMapper<UsrProductRecord> {

    @Select("select * from usrProductRecord where userUuid = #{userUuid} and orderNo = #{orderNo} and currentProductLevel = #{currentProductLevel} and disabled =0 order by createTime limit 1;")
    List<UsrProductRecord> getUpRecord(@Param("userUuid") String userUuid,@Param("orderNo") String orderNo,@Param("currentProductLevel") Integer currentProductLevel);
}
