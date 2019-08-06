package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.system.entity.CollectionOrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Mapper
public interface ManCollectionOrderDetailDao extends BaseMapper<CollectionOrderDetail> {

    @Update("update collectionOrderDetail set outsourceId = 0 , subOutSourceId = 0, remark = 'recycle bill order' where disabled = 0 and orderUuid=#{orderNo};")
    int recycleCollection(@Param("orderNo") String orderNo);

}
