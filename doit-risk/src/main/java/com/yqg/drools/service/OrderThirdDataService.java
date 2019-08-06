package com.yqg.drools.service;

import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.drools.beans.JuXinLiData;
import com.yqg.drools.beans.JuXinLiData.DataDetail;
import com.yqg.drools.beans.RideData;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.mongo.dao.OrderThirdDataDal;
import com.yqg.mongo.entity.OrderThirdDataMongo;
import com.yqg.order.entity.OrdOrder;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/31
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Service
@Slf4j
public class OrderThirdDataService {

    @Autowired
    private OrderThirdDataDal orderThirdDataDal;

    public DataDetail getThirdData(OrdOrder order, CertificationEnum certificationType) {
        OrderThirdDataMongo search = new OrderThirdDataMongo();
        search.setUserUuid(order.getUserUuid());
        search.setOrderNo(order.getUuid());
        search.setThirdType(certificationType.getType());
        List<OrderThirdDataMongo> orderThirdDataLists = orderThirdDataDal.find(search);
        if (CollectionUtils.isEmpty(orderThirdDataLists)) {
            log.error("search mongodb data is empty, certificationType : {} , orderNo: {}",
                certificationType, order.getUuid());
            return null;
        }

        OrderThirdDataMongo orderThirdDataMongo = orderThirdDataLists.get(0);
        String orderThirdData = orderThirdDataMongo.getData();

        if (StringUtils.isEmpty(orderThirdData)) {
            log.error("mongodb data field is empty, certificationType : {} , orderNo: {}",
                certificationType, order.getUuid());
            return null;
        }
        JuXinLiData data = JsonUtil.toObject(orderThirdData, JuXinLiData.class);
        if (data == null || data.getData() == null) {
            log.error("parsed juxinli data is empty, certificationType : {} , orderNo: {}",
                certificationType, order.getUuid());
            return null;
        }

        return data.getData();
    }

}
