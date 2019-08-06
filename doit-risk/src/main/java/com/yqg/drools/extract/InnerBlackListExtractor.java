package com.yqg.drools.extract;

import com.yqg.drools.model.InnerBlackList;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.DeviceService;
import com.yqg.order.entity.OrdDeviceInfo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.dao.RiskDeviceIdBlackListDao;
import com.yqg.risk.entity.RiskDeviceIdBlackList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/19
 * @Email zengxiangcai@yishufu.com
 *  摇钱罐黑名单【内部黑名单】
 ****/

@Service
public class InnerBlackListExtractor implements BaseExtractor<InnerBlackList> {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private RiskDeviceIdBlackListDao riskDeviceIdBlackListDao;


    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.YQG_BLACK_LIST.equals(ruleSet);
    }

    @Override
    public Optional<InnerBlackList> extractModel(OrdOrder order, KeyConstant keyConstant) {

        OrdDeviceInfo orderDeviceInfo = deviceService.getOrderDeviceInfo(order.getUuid());

//        if (orderDeviceInfo == null || StringUtils.isEmpty(orderDeviceInfo.getDeviceId())) {
//           return Optional.empty();
//        }
        InnerBlackList innerBlackList = new InnerBlackList();
        if(orderDeviceInfo != null && !StringUtils.isEmpty(orderDeviceInfo.getDeviceId())){
            RiskDeviceIdBlackList riskDeviceEntity = new RiskDeviceIdBlackList();
            riskDeviceEntity.setDeviceId(orderDeviceInfo.getDeviceId());
            riskDeviceEntity.setDisabled(0);
            riskDeviceEntity.setType("0");
            List<RiskDeviceIdBlackList> scanList = riskDeviceIdBlackListDao.scan(riskDeviceEntity);
            innerBlackList.setIsInYQGBlackListSize(CollectionUtils.isEmpty(scanList)?0:scanList.size());
        }

        //通过imei查询
        if (orderDeviceInfo != null && !StringUtils.isEmpty(orderDeviceInfo.getIMEI())) {
            RiskDeviceIdBlackList riskDeviceEntity = new RiskDeviceIdBlackList();
            riskDeviceEntity.setDeviceId(orderDeviceInfo.getIMEI());
            riskDeviceEntity.setDisabled(0);
            riskDeviceEntity.setType("1");
            List<RiskDeviceIdBlackList> scanList = riskDeviceIdBlackListDao.scan(riskDeviceEntity);
            innerBlackList.setIsIMEIInYQGBlackList(scanList.size() > 0 ? true : false);
        }


        return Optional.of(innerBlackList);
    }
}
