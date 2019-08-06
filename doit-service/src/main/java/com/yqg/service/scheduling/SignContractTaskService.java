package com.yqg.service.scheduling;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class SignContractTaskService {
    @Autowired
    private OrdDao ordDao;

    public void checkNeedToSignContractOrder(){
        //查询待检查去签约订单
        List<OrdOrder> orders = ordDao.getOrdersByStatus(OrdStateEnum.WAITING_SIGN_CONTRACT.getCode());
        if(CollectionUtils.isEmpty(orders)){
            log.info("the orders need to sign contract is empty");
            return;
        }
        for(OrdOrder order: orders){
            MDC.put("X-Request-Id", order.getUuid());
            try{
                //检查是否ekyc成功

            }catch (Exception e){
                log.error("check need to sign contract error with orderNo: "+order.getUuid(),e);
            }finally {
                MDC.remove("X-Request-Id");
            }

        }
    }
}
