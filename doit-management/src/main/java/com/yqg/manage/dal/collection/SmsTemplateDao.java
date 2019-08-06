package com.yqg.manage.dal.collection;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.entity.collection.SmsTemplateEntity;
import com.yqg.manage.service.collection.response.SmsTemplateResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: caomiaoke
 * Date: 23/03/2018
 * Time: 3:08 PM
 */
@Mapper
public interface SmsTemplateDao extends BaseMapper<SmsTemplateEntity> {

    @Select("select max(id) from smsTemplate")
    Integer getMaxId();

    @Select("SELECT smsTemplateId, smsTitle, smsContent from smsTemplate where disabled = '0'")
    List<SmsTemplateResponse> getAllSmsTemplate();

}
