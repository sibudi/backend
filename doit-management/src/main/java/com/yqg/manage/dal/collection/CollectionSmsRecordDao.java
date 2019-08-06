package com.yqg.manage.dal.collection;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.collection.CollectionSmsRecordEntity;
import com.yqg.manage.service.collection.response.CollectionSmsRecordResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: caomiaoke
 * Date: 23/03/2018
 * Time: 3:19 PM
 */

@Mapper
public interface CollectionSmsRecordDao extends BaseMapper<CollectionSmsRecordEntity> {

    @Select("SELECT id, sender,receiverType as receiveType,sendTime,smsTitle from collectionSmsRecord where orderNo = #{orderUuid} and disabled = 0 ")
    List<CollectionSmsRecordResponse> getCollectionSmsRecordListByUserUuid(@Param("orderUuid") String orderUuid);
}
