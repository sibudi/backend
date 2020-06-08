package com.yqg.order.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * Created by Setiya Budi on 2020/03/31.
 */
@Mapper
public interface OrdDigisign_error {
    // delete orderNo from this table if we need to restart digisign process for particular user
    @Insert ("insert into orddigisign_error(orderNo) values(#{orderNo})")
    Integer insertOrddigisign_error(@Param("orderNo") String orderNo);
}
