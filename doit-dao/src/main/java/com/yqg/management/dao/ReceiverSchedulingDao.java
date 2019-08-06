package com.yqg.management.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.management.entity.QualityCheckingVoice;
import com.yqg.management.entity.ReceiverScheduling;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @program: microservice
 * @description:
 * @author: 许金泉
 * @create: 2019-04-12 15:27
 **/
@Mapper
public interface ReceiverSchedulingDao extends BaseMapper<ReceiverScheduling> {


}
