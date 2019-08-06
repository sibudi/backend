package com.yqg.manage.dal.system;


import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.system.ManAlertMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author alan
 */
@Mapper
public interface ManAlertMessageDao extends BaseMapper<ManAlertMessage> {
    @Select("select userId,message,url from manAlertMessage where alertTime >= ' ${startTime} ' and alertTime <= ' ${endTime} ' and disabled = 0 ")
    List<ManAlertMessage> getCollectionMessageListByTime(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @Select("select userId,message,url from manAlertMessage where alertTime = ' ${alertTime} ' and disabled = 0 ")
    List<ManAlertMessage> getCollectionMessageListByMinute(@Param("alertTime") String alertTime);
}

