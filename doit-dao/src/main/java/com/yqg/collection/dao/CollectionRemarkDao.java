package com.yqg.collection.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

@Mapper
public interface CollectionRemarkDao {
    @Select("select count(1) from manCollectionRemark a where a.orderNo=#{orderNo} and a.contactMode=2 " +
            " and a.contactMobile=#{mobile} and a.contactResult in (3,7) and id = (select max(b.id) from manCollectionRemark b " +
            " where b.orderNo=#{orderNo} and b.contactMode=2 and b.contactMobile=#{mobile} and b.contactResult in (3,7))")
    Integer countOfInvalidCollectionCallResult(@Param("orderNo") String orderNo, @Param("mobile") String mobile );

    @Select("select min(createTime) from manCollectionRemark where orderNo  =#{orderNo} and orderTag=0 and disabled=0")
    Date getFirstCollectionTime(@Param("orderNo") String orderNo);

    @Select("select count(1) from manCollectionRemark where orderNo  =#{orderNo} and disabled=0")
    Integer getCollectionCount(@Param("orderNo") String orderNo);
}
