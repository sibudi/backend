package com.yqg.ojk.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.ojk.entity.OjkDataTotal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Mapper
public interface OjkDataTotalDao extends BaseMapper<OjkDataTotal> {

    @Select("select max(idCode) from ojkDataTotal where disabled = 0;")
    Integer getMaxCodeValue();

    @Update("update ojkDataTotal set updateTime = now(), ojkValue = #{value} where disabled = 0 and ojkKey = #{key} and type = #{type} and idCode = #{code};")
    int updateOjkData(@Param("key") String key, @Param("value") String value,
                      @Param("code") int code, @Param("type") String type);

    @Select("select * from ojkDataTotal where type = #{type} and disabled=0 order by idCode desc;")
    List<OjkDataTotal> listData(@Param("type") String type);

    @Update("update ojkDataTotal set disabled = 1,updateTime = now() where idCode=#{idCode} and disabled = 0;")
    int deleteOjkData(@Param("idCode") Integer idCode);
}
