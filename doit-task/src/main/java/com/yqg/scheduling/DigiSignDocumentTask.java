package com.yqg.scheduling;

import com.yqg.service.signcontract.ContractSignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DigiSignDocumentTask {
    @Autowired
    private ContractSignService contractSignService;
    /***
     * 下载已经签约的文档
     * (每天凌晨开始下载减少系统负载)
     */
    @Scheduled(cron = "13 0/1 0-7 * * ?")
    public void downloadSignPDF(){
        Long startTime = System.currentTimeMillis();
        try{
            contractSignService.downloadContract();
        }catch (Exception e){
            log.error("download file task error",e);
        }finally {
            log.info("cost of download task is: {} ms",(System.currentTimeMillis()-startTime));
        }
    }

    /***
     * 检查用户签约状态，防止回调数据失败数据一直没有到后续的状态
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void checkOrderDigiSignStatus(){

        Long startTime = System.currentTimeMillis();
        try{
            contractSignService.checkSignContractStatus();
        }catch (Exception e){
            log.error("check sign status task error",e);
        }finally {
            log.info("cost of check sign status task is: {} ms",(System.currentTimeMillis()-startTime));
        }
    }
}
