package com.yqg.manage.dal.collection;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.SmsTemplateProvider;
import com.yqg.manage.entity.collection.SmsOneStepStatisticsEntity;
import com.yqg.manage.service.collection.request.SmsTemplateRequest;
import com.yqg.manage.service.collection.response.CollectSmsTemplateResponse;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: caomiaoke
 * Date: 23/03/2018
 * Time: 3:19 PM
 */

@Mapper
public interface SmsOneStepStatisticsDao extends BaseMapper<SmsOneStepStatisticsEntity> {

    /**
     * 获得模板列表
     * @param searchResquest
     * @return
     */
    @SelectProvider(type = SmsTemplateProvider.class, method = "collectSmsTempList")
    @Options(useGeneratedKeys = true)
    List<CollectSmsTemplateResponse> collectSmsTempList(@Param("SmsTemplateRequest") SmsTemplateRequest searchResquest);

    /**
     * 获得当天已发短信条数
     * @return
     */
    @SelectProvider(type = SmsTemplateProvider.class, method = "collectSmsTempListCount")
    Integer collectSmsTempListCount(@Param("nowTime") String nowTime, @Param("orderNo") String orderNo);
}
