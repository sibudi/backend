package com.yqg.manage.dal.collection;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.QualityCheckingVoiceSqlProvider;
import com.yqg.manage.service.check.request.QualityCheckingVoiceRequest;
import com.yqg.management.entity.QualityCheckingVoice;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QualityCheckingVoiceDaoProvider extends BaseMapper<QualityCheckingVoice> {


    @SelectProvider(type = QualityCheckingVoiceSqlProvider.class, method = "getQualityCheckingVoiceList")
    @Options(useGeneratedKeys = true)
    List<QualityCheckingVoice> getQualityCheckingVoiceList(@Param("searchRequest") QualityCheckingVoiceRequest searchRequest);

    @SelectProvider(type = QualityCheckingVoiceSqlProvider.class, method = "getQualityCheckingVoiceCount")
    @Options(useGeneratedKeys = true)
    Integer getQualityCheckingVoiceCount(@Param("searchRequest") QualityCheckingVoiceRequest searchRequest);


    @SelectProvider(type = QualityCheckingVoiceSqlProvider.class, method = "getAttachmentSavePathByUuidList")
    List<String> getAttachmentSavePathByUuidList(@Param("uuidList") List<String> uuidList);

}
