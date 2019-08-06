package com.yqg.risk.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.risk.entity.ErrorData;

import java.util.List;

/**
 * Created by Jeremy Lawrence on 2018/2/12.
 */

@Mapper
public interface ErrorDataDao extends BaseMapper<ErrorData> {

  @Select("select u.userUuid,o.uuid orderNo from usrCertificationInfo u,ordOrder o where u.certificationType=1 "
      + " and u.remark like '20180211%' "
      + " and o.userUuid = u.userUuid and o.disabled=0")
  List<ErrorData> getErrorDataUserOrders();
}
