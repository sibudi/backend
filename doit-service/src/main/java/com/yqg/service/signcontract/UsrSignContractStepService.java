package com.yqg.service.signcontract;

import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.signcontract.dao.UsrSignContractStepDao;
import com.yqg.signcontract.entity.UsrSignContractStep;
import com.yqg.signcontract.entity.UsrSignContractStep.SignStepEnum;
import com.yqg.signcontract.entity.UsrSignContractStep.StepResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class UsrSignContractStepService {
    @Autowired
    private UsrSignContractStepDao usrSignContractStepDao;

    public void saveSignContractStep(String userId, String orderNo, SignStepEnum step, StepResultEnum result, String remark) {
        //如果已经有数据则更新
        UsrSignContractStep dbStep = getSignContractStepResult(userId, step);
        if (dbStep != null) {
            //update
            dbStep.setOrderNo(orderNo);
            dbStep.setUserUuid(userId);
            dbStep.setUpdateTime(new Date());
            dbStep.setSignStep(step.getCode());
            dbStep.setStepResult(result.getCode());
            dbStep.setDisabled(0);
            if (remark != null && remark.length() > 500) {
                remark = remark.substring(0, 500);
            }
            dbStep.setRemark(remark);
            usrSignContractStepDao.update(dbStep);
        } else {
            UsrSignContractStep signStepEntity = new UsrSignContractStep();
            signStepEntity.setOrderNo(orderNo);
            signStepEntity.setUserUuid(userId);
            signStepEntity.setCreateTime(new Date());
            signStepEntity.setUpdateTime(new Date());
            signStepEntity.setUuid(UUIDGenerateUtil.uuid());
            signStepEntity.setSignStep(step.getCode());
            signStepEntity.setStepResult(result.getCode());
            signStepEntity.setDisabled(0);
            if (remark != null && remark.length() > 500) {
                remark = remark.substring(0, 500);
            }
            signStepEntity.setRemark(remark);
            usrSignContractStepDao.insert(signStepEntity);
        }

    }

    public UsrSignContractStep getSignContractStepResult(String userId, SignStepEnum stepEnum) {
        return usrSignContractStepDao.getStepResultByType(userId, stepEnum.getCode());
    }
    public UsrSignContractStep getLatestStep(String userId) {
        return usrSignContractStepDao.getLatestStepResult(userId);
    }
}
