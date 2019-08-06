package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.LoanUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by wanghuaizhou on 2018/6/8.
 */
@Mapper
public interface LoanUserDao extends BaseMapper<LoanUser> {

    @Select("SELECT * FROM doit.loanUser where isSend = 0 limit #{count} ")
    List<LoanUser> getLoanUserListWithCount(@Param("count") Integer count);

    // 获取未发送的用户
    @Select("SELECT * FROM doit.loanUser where isSend != 1;")
    public List<LoanUser> getLoanUserListWithNotSend();
}
