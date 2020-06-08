package com.yqg.scheduling;

import com.yqg.service.scheduling.CheakThirdDataScheduling;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Didit Dwianto on 2017/12/20.
 */
@Component
@Slf4j
public class CheakThirdDataTask {

    @Autowired
    private CheakThirdDataScheduling cheakThirdDataScheduling;

    /**
     * @throws Exception
     */
    //@Scheduled(cron = "0 0/1 * * * ?")
    public void getThirdDataFromSpider() throws Exception{
        log.info("==============getThirdDataFromSpider begin==================");
        this.cheakThirdDataScheduling.getThirdData();
        log.info("==============getThirdDataFromSpider  end==============");
    }


}
