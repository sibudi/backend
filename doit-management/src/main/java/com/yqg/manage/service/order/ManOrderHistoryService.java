package com.yqg.manage.service.order;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.manage.dal.order.ManOrderHistoryDao;
import com.yqg.manage.service.order.request.ManOrderListSearchResquest;
import com.yqg.order.entity.OrdHistory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author alan
 */
@Component
public class ManOrderHistoryService {
    @Autowired
    private ManOrderHistoryDao manOrderHistoryDao;

    public List<OrdHistory> orderSchedule(ManOrderListSearchResquest request) throws ServiceExceptionSpec {
        String orderNo = request.getUuid();
        if(StringUtils.isEmpty(orderNo)){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        OrdHistory search = new OrdHistory();
        search.setOrderId(orderNo);
        search.setDisabled(0);
        search.set_orderBy(" statusChangeTime ");
        List<OrdHistory> result = this.manOrderHistoryDao.scan(search);

        return result;
    }

    public void addOrderHistory(OrdHistory order) throws ServiceExceptionSpec {
        this.manOrderHistoryDao.insert(order);
    }

    /**
     * 通过订单编号查询订单初审复审时间*/
    public OrdHistory orderCheckTimeByNo(String orderNo, Integer status) throws ServiceExceptionSpec {
        OrdHistory search = new OrdHistory();
        search.setOrderId(orderNo);
        search.setStatus(status);
        search.setDisabled(0);
        List<OrdHistory> ordHistories = this.manOrderHistoryDao.scan(search);
        if(CollectionUtils.isEmpty(ordHistories)){
            return null;
        }
        return ordHistories.get(0);
    }
}
