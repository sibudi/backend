package com.yqg.manage.service.system;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.utils.StringUtils;
import com.yqg.manage.dal.system.ManCtrlcRecordDao;
import com.yqg.manage.entity.system.ManCtrlcRecord;
import com.yqg.manage.service.system.request.ManCtrlcRecordRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author: tonggen
 * Date: 2019/5/14
 * time: 11:00 AM
 */
@Service
public class ManCtrlcRecordService {

    @Autowired
    private ManCtrlcRecordDao manCtrlcRecordDao;
    /**
     * 新增一条记录
     * @param request
     * @return
     */
    public Integer insertManCtrlcRecord(ManCtrlcRecordRequest request) throws ServiceExceptionSpec{

        if (request.getOperator() == null || StringUtils.isEmpty(request.getContent())
                || StringUtils.isEmpty(request.getOrderNo()) || StringUtils.isEmpty(request.getUserUuid())) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        ManCtrlcRecord record = new ManCtrlcRecord();
        BeanUtils.copyProperties(request, record);

        return manCtrlcRecordDao.insert(record);
    }
}
