package com.yqg.service.scheduling;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.redis.RedisClient;
import com.yqg.service.order.request.SaveOrderUserUuidRequest;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.user.service.UsrBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Didit Dwianto on 2017/11/28.
 */
@Component
@Slf4j
public class OrderUserMangoScheduling {
    @Autowired
    private UsrBaseInfoService baseInfoService;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private SysParamService sysParamService;

    public void saveOrderUserDataToMango() throws Exception{
        //count可以在数据库配置
        String number = this.sysParamService.getSysParamValue(SysParamContants.NUMBER_OF_ORDERS);


        for (int count = Integer.parseInt(number) ;count > 0;count --){
            //----------------------------???Redis?-----------------------------------------
//            SaveOrderUserUuidRequest saveOrderUserUuidRequest = new SaveOrderUserUuidRequest();
//            saveOrderUserUuidRequest.setOrderNo("AFB35980BB0B48BDA53D7249CAEFB38C");
//            saveOrderUserUuidRequest.setUserUuid("2231E4711F914C4FBDAE864CB8412DAA");
//            redisClient.listAdd(RedisContants.SAVE_MANGO_ORDER_LIST,saveOrderUserUuidRequest);
//            saveOrderUserUuidRequest.setOrderNo("CED5A46C6BA048D2BE9389A439BB1E81");
//            saveOrderUserUuidRequest.setUserUuid("DAFC2FE51B464976A4D30DF36484C5A3");
//            redisClient.listAdd(RedisContants.SAVE_MANGO_ORDER_LIST,saveOrderUserUuidRequest);
            //----------------------------???Redis?-----------------------------------------
            SaveOrderUserUuidRequest orderInfo = redisClient.listGetTail(RedisContants.SAVE_MANGO_ORDER_LIST,SaveOrderUserUuidRequest.class);
            try {
                if (orderInfo != null){
                    log.info("归档的order-----"+orderInfo);
                    String userUuid = orderInfo.getUserUuid();
                    String orderNo = orderInfo.getOrderNo();
                    this.baseInfoService.savaOrderInfoToMango(userUuid,orderNo);
                }else {
                    log.info("订单归档完毕,本次归档订单数{}",Integer.parseInt(number)-count);
                    break;
                }
            }catch (Exception e){

                log.error(e.getMessage(),e);
                this.redisClient.listAdd(RedisContants.SAVE_MANGO_ORDER_LIST ,orderInfo);
                log.info("***************获取数据订单归档异常************************");
            }
        }
    }
}