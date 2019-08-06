package com.yqg.service.scheduling.task;

import com.yqg.common.utils.StringUtils;
import com.yqg.service.user.service.UsrService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IziWhatsAppTask implements Runnable {

    private UsrService usrService;
    private String userUuid;

    public IziWhatsAppTask(UsrService usrService,String userUuid){
        this.usrService = usrService;
        this.userUuid = userUuid;
    }
    @Override
    public void run() {
        if(StringUtils.isEmpty(userUuid)){
            log.info("the userUuid isEmpty");
            return ;
        }
        usrService.checkIziWhatsappOpen(userUuid);
    }
}
