package com.yqg.manage.dal.third;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.TwilioSqlProvider;
import com.yqg.manage.service.third.request.ThirdTwilioRequest;
import com.yqg.manage.service.third.request.TwilioWhatsAppRequest;
import com.yqg.manage.service.third.response.ThirdTwilioResponse;
import com.yqg.manage.service.third.response.TwilioWhatsAppRecordResponse;
import com.yqg.system.entity.TwilioCallResult;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Author: tonggen
 * Date: 2018/10/19
 * time: 下午3:16
 */
@Mapper
public interface ThirdTwilioDao extends BaseMapper<TwilioCallResult> {

    @SelectProvider(type = TwilioSqlProvider.class, method = "listTwilioCallResult")
    List<ThirdTwilioResponse> listTwilioCallResult(@Param("request") ThirdTwilioRequest request);

    @Select("select count(*) from twilioCallResult where disabled = 0 and batchNo = #{batchNo} and callResultType = 1")
    Integer getNoResponseCount(@Param("batchNo") String batchNo);

    @Select("select batchNo, startTime, phoneNumber, callResultType as passOrNot from twilioCallResult where disabled = 0 and batchNo = #{batchNo}")
    List<ThirdTwilioResponse> getTwilioCallResultDetail(@Param("batchNo") String batchNo);

    @SelectProvider(type = TwilioSqlProvider.class, method = "listTwilioWaRecord")
    List<TwilioWhatsAppRecordResponse> listTwilioWaRecord(@Param("request") TwilioWhatsAppRequest request);

}
