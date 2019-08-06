package com.yqg.management.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.management.entity.QualityCheckingVoice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @program: microservice
 * @description:
 * @author: 许金泉
 * @create: 2019-04-12 15:27
 **/
@Mapper
public interface QualityCheckingVoiceDao extends BaseMapper<QualityCheckingVoice> {

    @Select("select * from qualityCheckingVoice where disabled = 0 and callState = 0 and timestampdiff(HOUR, createTime, now()) <= 48 and mod(id,3)=#{num};")
    List<QualityCheckingVoice> needUpdateList(@Param("num") Integer num);

    @Update("update qualityCheckingVoice attachmentSavePath=#{serverFileName} where disabled = 0 and callState = 0 and downUrl=#{downUrl}")
    int updateQualityCheckingVoiceServerFileName(String downUrl, String serverFileName);

//    @Select("select count(*) from qualityCheckingVoice where disabled = 0 and downUrl=#{downUrl};")
//    int getRecordByFileUrl(@Param("downUrl") String downUrl);


}
