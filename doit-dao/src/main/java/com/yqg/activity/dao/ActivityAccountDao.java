package com.yqg.activity.dao;

import com.yqg.activity.entity.ActivityAccount;
import com.yqg.base.data.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Features:
 * Created by huwei on 18.8.15.
 */
@Mapper
public interface ActivityAccountDao extends BaseMapper<ActivityAccount> {


    @Select("SELECT (SELECT realName FROM `usrUser` WHERE UUID=userUuid)AS username,(SELECT mobileNumberDes FROM `usrUser` WHERE UUID=userUuid) AS mobileNumber,count(1) as friendNum,SUM(amount) AS amount FROM activityAccountRecord " +
            "WHERE disabled = 0 AND TYPE IN (1,2) GROUP BY userUuid ORDER BY amount DESC LIMIT 0,10")
    List<Map> getAccountTop10();

}
