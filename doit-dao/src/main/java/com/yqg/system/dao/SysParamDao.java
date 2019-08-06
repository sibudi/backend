package com.yqg.system.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.SysParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/25.
 */
@Mapper
public interface SysParamDao extends BaseMapper<SysParam>{

    @Update("update sysParam set sysValue = #{sysValue},updateTime = now() where sysKey = #{sysKey} ")
    public void updateSysParam(@Param("sysValue") String sysValue,
                               @Param("sysKey") String sysKey);

    @Select("select * from sysParam where sysKey regexp '^sms:collections:' ")
    List<SysParam> listCollectionSmsSwitch();

    @Select("select * from sysParam where sysKey regexp '^users:contact:' ")
    List<SysParam> listContactSwitch();

    @Select("select sysValue from sysParam where sysKey = #{sysKey} ")
    List<String> getCollectionSmsSwitch(String sysKey);

    @Select("select \n" +
            "concat(round(100 - overAmount/dueAmount*100,2),'%') as repayRate\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "sum(amountApply) as overAmount\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (7,8)\n" +
            "and ordertype in (0,1,2)\n" +
            "and date(refundTime) between '2018-06-01' and date_sub(curdate(),interval 91 day)\n" +
            ") over\n" +
            "cross join \n" +
            "(\n" +
            "select\n" +
            "sum(amountApply) as dueAmount\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (7,8)\n" +
            "and ordertype in (0,1,2)\n" +
            "and date(lendingTime) between '2018-06-01' and date_sub(curdate(),interval 1 day)\n" +
            ") due")
    String getRepayRate();
}
