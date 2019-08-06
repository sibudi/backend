package com.yqg.management.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.common.third.InfinityApp;
import com.yqg.management.entity.InfinityExtnumberEntity;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

/*****
 * @Author super
 * Created at 2019/04/04
 * @Email lijianping@yishufu.com
 *
 ****/

@Mapper
public interface InfinityExtNumberDao extends BaseMapper<InfinityExtnumberEntity>{


    @Insert("<script>"
            + " INSERT INTO infinityExtnumber("
            + "    uuid,extnumber,  password,status,createTime,updateTime  "
            + " values "
            + "   <foreach collection='infinityExtnumbers' item='record' separator=',' >"
            + "     (#{record.uuid},#{record.extnumber},#{record.status},now(),now())"
            + "   </foreach>"
            + " </script>")
    int addExtnumberList(@Param("infinityExtnumbers") List<InfinityExtnumberEntity> infinityExtnumbers);

    @Insert(
            "insert into infinityExtnumber(uuid,extnumber,  password,status,createTime,updateTime) "
                    + "   values(#{uuid},#{extnumber},#{password},#{status},now(),now())")
    int insertExtNumber(InfinityExtnumberEntity entity);

    @Select("select * from infinityExtnumber where disabled=0")
    List<InfinityExtnumberEntity> getExtnumberList();

    @Select("select * from infinityExtnumber where uuid = #{uuid}")
    List<InfinityApp> infinityExtnumberById(@Param("uuid") String uuid);

    @Update("update infinityExtnumber set disabled=1 where uuid=#{uuid} and disabled=0")
    Integer disableExtnumberById(@Param("uuid") String uuid);


    @Update("update infinityExtnumber set status=#{status} ,updateTime = now() where extnumber=#{extnumber}")
    Integer updateStatusByExtnumber(@Param("extnumber") String extnumber, @Param("status") String status);


    @Select("\n" +
            "select count(1) from ordOrder o where amountApply = #{amount} and o.disabled=0 and o.status  in (5,6,7,8,10,11)")
    Integer totalIssuedByAmount(@Param("amount") BigDecimal amount);


}

