package com.yqg.service.system.service;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/***
 * 注册下单流程自动化
 */
@Service
@Slf4j
public class MainProcessAutoTestStarter {
    @Autowired
    private MainProcessAutoTestService testService;

    public void test(){
        try {
            testService.loanForWorker();
            log.info("order finished...");
        }catch (Exception e){
            if(e instanceof ServiceException){
                ServiceException e1 = (ServiceException) e;
                //忽略银行卡
                if(e1.getErrorCode() == ExceptionEnum.USER_KABIN_CHECK_NAME.getCode()){
                    log.info("process finished with bank card invalid");
                    return;
                }
            }else{
                log.error("finished with error", e);

            }

            log.info("process finished with rollback...");
        }
    }
}
