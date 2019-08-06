package com.yqg.service.system.service;

import com.yqg.common.constants.SysParamContants;
import com.yqg.system.dao.SysParamDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wanghuaizhou on 2019/4/16.
 */
@Service
@Slf4j
public class RepayRateService {

    @Autowired
    private SysParamDao sysParamDao;
    @Autowired
    private SysParamService sysParamService;

    public void updateRepayRate(){
        String repayRate = sysParamDao.getRepayRate();
        this.sysParamService.setSysParamValue(SysParamContants.SYSTEM_REPAY_RATE,repayRate);
    }

    public String getRepayRate(){
        return this.sysParamService.getSysParamValue(SysParamContants.SYSTEM_REPAY_RATE);
    }

}
