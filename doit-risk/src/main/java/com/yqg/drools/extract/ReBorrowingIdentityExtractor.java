package com.yqg.drools.extract;

import com.github.pagehelper.StringUtil;
import com.yqg.common.utils.DESUtils;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.RUserInfo;
import com.yqg.drools.model.RUserInfo.IziPhoneAgeResult;
import com.yqg.drools.model.RUserInfo.IziPhoneVerifyResult;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.UserService;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.third.izi.IziService;
import com.yqg.service.third.izi.response.IziResponse;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ReBorrowingIdentityExtractor  implements BaseExtractor<RUserInfo>{

    @Autowired
    private IziService iziService;
    @Autowired
    private UserService userService;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.RE_BORROWING_USER_IDENTITY.equals(ruleSet);
    }

    @Override
    public Optional<RUserInfo> extractModel(OrdOrder order, KeyConstant keyConstant) throws Exception {
        try {
            UsrUser dbUser = userService.getUserInfo(order.getUserUuid());
            String mobile = DESUtils.decrypt(dbUser.getMobileNumberDES());
            RUserInfo rUserInfo = new RUserInfo();
            rUserInfo.setAge(dbUser.getAge());
            rUserInfo.setSex(dbUser.getSex());
            //取izi结果：
            String strIziPhoneAgeResponse = iziService.getLatestIziResponseByUserUuid(order.getUserUuid(), "1");
            IziResponse phoneAgeResponse = null;
            if (StringUtil.isNotEmpty(strIziPhoneAgeResponse)) {
                phoneAgeResponse = JsonUtil.toObject(strIziPhoneAgeResponse, IziResponse.class);
            } else {
                phoneAgeResponse = iziService.getPhoneAge(mobile, order.getUuid(), order.getUserUuid());
            }
            rUserInfo.setIziPhoneAgeResult(IziPhoneAgeResult.parseResultFromResponse(phoneAgeResponse));

            String strIziPhoneVerifyResponse = iziService.getLatestIziResponseByUserUuid(order.getUserUuid(), "2");
            IziResponse phoneVerifyResponse = null;
            if (StringUtil.isNotEmpty(strIziPhoneVerifyResponse)) {
                phoneVerifyResponse = JsonUtil.toObject(strIziPhoneVerifyResponse, IziResponse.class);
            } else {
                phoneVerifyResponse = iziService.getPhoneVerify(mobile, dbUser.getIdCardNo(), order.getUuid(), order.getUserUuid());
            }
            rUserInfo.setIziPhoneVerifyResult(IziPhoneVerifyResult.parseResultFromResponse(phoneVerifyResponse));

            return Optional.of(rUserInfo);
        } catch (Exception e) {
            log.error("fetch reborrowing data error", e);
        }
        return Optional.empty();
    }
}
