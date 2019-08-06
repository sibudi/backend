package com.yqg.manage.dal.collection;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.ReceiverSchedulingSqlProvider;
import com.yqg.manage.service.check.request.ReceiverSchedulingRequest;
import com.yqg.management.entity.ReceiverScheduling;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

/**
 * @program: microservice
 * @description:
 * @author: 许金泉
 * @create: 2019-04-16 11:42
 **/
@Mapper
public interface ManReceiverSchedulingDao extends BaseMapper<ReceiverScheduling> {

    @SelectProvider(type = ReceiverSchedulingSqlProvider.class, method = "getReceiverSchedulingList")
    @Options(useGeneratedKeys = true)
    List<ReceiverScheduling> getReceiverSchedulingList(@Param("searchRequest") ReceiverSchedulingRequest searchRequest);

    @SelectProvider(type = ReceiverSchedulingSqlProvider.class, method = "getReceiverSchedulingCount")
    @Options(useGeneratedKeys = true)
    Integer getReceiverSchedulingCount(@Param("searchRequest") ReceiverSchedulingRequest searchRequest);

    @Select("SELECT MAX(workTime) FROM  receiverScheduling")
    Date getLastWorkTime();


    @Update("<script>" +
            "update receiverScheduling set disabled=1  where disabled = 0 and uuid in "
            + "<foreach collection='uuidList' item='item' separator=',' open='(' close=')'>"
            + " #{item}"
            + "</foreach>"
            + "</script>")
    Integer deleteReceiverScheduling(@Param("uuidList") List<String> uuidList);

}
