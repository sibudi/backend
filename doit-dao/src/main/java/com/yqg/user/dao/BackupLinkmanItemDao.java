package com.yqg.user.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.user.entity.BackupLinkmanItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BackupLinkmanItemDao extends BaseMapper<BackupLinkmanItem> {

    @Update("update backupLinkmanItem set isConfirmed = #{confirmed},updateTime=now() where userUuid=#{userUuid} and id= #{id}")
    Integer confirmBackupItem(@Param("userUuid") String userUuid,@Param("confirmed") Integer confirmed, @Param("id") Integer id);

    @Update("delete from backupLinkmanItem  where userUuid=#{userUuid} and orderNo=#{orderNo}")
    Integer deleteHistoryItem(@Param("orderNo") String orderNo,@Param("userUuid") String userUuid);
}
