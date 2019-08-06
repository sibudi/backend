package com.yqg.drools.extract;

import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.utils.CardIdUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.model.AutoCallUserInfo;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.UserService;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.order.entity.OrdOrder;
import com.yqg.user.entity.UsrHouseWifeDetail;
import com.yqg.user.entity.UsrStudentDetail;
import com.yqg.user.entity.UsrUser;
import com.yqg.user.entity.UsrWorkDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class AutoCallUserExtractor implements BaseExtractor<AutoCallUserInfo> {
    @Autowired
    private UserService userService;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.AUTO_CALL_USER.equals(ruleSet);
    }

    @Override
    public Optional<AutoCallUserInfo> extractModel(OrdOrder order, KeyConstant keyConstant) throws Exception {
        UsrUser user = userService.getUserInfo(order.getUserUuid());

        AutoCallUserInfo userInfo = new AutoCallUserInfo();

        if (StringUtils.isNotEmpty(user.getIdCardNo())) {
            userInfo.setAge(CardIdUtils.getAgeByIdCard(user.getIdCardNo().trim()));
        }

        userInfo.setGojekVerified(userService.certificationVerified(order.getUserUuid(), CertificationEnum.GOJECK_IDENTITY));

        userInfo.setExistsSameEmail(userService.isEmailExists(user));
        return Optional.of(userInfo);
    }
}
