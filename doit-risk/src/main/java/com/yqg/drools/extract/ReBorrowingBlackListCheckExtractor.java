package com.yqg.drools.extract;

import java.util.Optional;

import com.yqg.drools.model.BlackListUserCheckModel;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.DeviceService;
import com.yqg.drools.service.UserService;
import com.yqg.drools.service.UsrBlackListService;
import com.yqg.order.entity.OrdDeviceInfo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.user.entity.UsrBlackList;
import com.yqg.user.entity.UsrUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReBorrowingBlackListCheckExtractor implements BaseExtractor<BlackListUserCheckModel> {

    @Autowired
    private UsrBlackListService usrBlackListService;

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceService deviceService;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.RE_BORROWING_BLACK_LIST_USER.equals(ruleSet);
    }

    @Override
    public Optional<BlackListUserCheckModel> extractModel(OrdOrder order, KeyConstant keyConstant) throws Exception {
        BlackListUserCheckModel model = new BlackListUserCheckModel();
        UsrUser user = userService.getUserInfo(order.getUserUuid());
        model.setMobileInOverdue15BlackList(usrBlackListService.isMobileInBlackUserCategoryN(user.getMobileNumberDES(),
                UsrBlackList.BlackUserCategory.OVERDUE15));
        model.setContactInOverdue15Count(0);
        model.setEmergenctContactInOverdue15Count(0);

        OrdDeviceInfo ordDeviceInfo = deviceService.getOrderDeviceInfo(order.getUuid());

        model.setImeiInFraudUser(ordDeviceInfo == null ? false : usrBlackListService.isImeiInFraudUser(ordDeviceInfo.getIMEI()));

        model.setHitFraudUserInfo(usrBlackListService.hitFraudUserInfo(user, order));

        model.setSmsContactOverdue15DaysCount(0);

        model.setHitSensitiveUserInfo(usrBlackListService.hitIdCardOrMobileForCategoryN(user,UsrBlackList.BlackUserCategory.SENSITIVE));

        model.setHitCollectorBlackUserInfo(usrBlackListService.hitIdCardOrMobileForCategoryN(user,UsrBlackList.BlackUserCategory.COLLECTOR_BLACK_LIST));

        model.setHitComplaintUserInfo(usrBlackListService.hitIdCardOrMobileForCategoryN(user,UsrBlackList.BlackUserCategory.COMPLAINT));

        return Optional.of(model);
    }
}
