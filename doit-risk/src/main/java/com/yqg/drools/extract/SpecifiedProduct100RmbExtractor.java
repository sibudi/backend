package com.yqg.drools.extract;

import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.SpecifiedProduct100RMBModel;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.UserService;
import com.yqg.order.entity.OrdOrder;
import com.yqg.user.entity.UsrAddressDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class SpecifiedProduct100RmbExtractor implements BaseExtractor<SpecifiedProduct100RMBModel> {
    @Autowired
    private UserService userService;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.SPECIFIED_PRODUCT_100RMB.equals(ruleSet);
    }

    @Override
    public Optional<SpecifiedProduct100RMBModel> extractModel(OrdOrder order, KeyConstant keyConstant) throws Exception {
        SpecifiedProduct100RMBModel model = new SpecifiedProduct100RMBModel();
        //100rmb 产品规则参数
        UsrAddressDetail companyAddress = userService
                .getUserAddressDetailByType(order.getUserUuid(),
                        UsrAddressEnum.COMPANY);
        if (companyAddress != null && StringUtils.isNotEmpty(companyAddress.getCity())) {
            model.setCompanyAddressNotBelongToJarkat(
                    !isAddressMatch(keyConstant.getJarkatAddressWordsFor100Rmb(), companyAddress.getCity()));
        }
        return Optional.of(model);
    }



    private static boolean isAddressMatch(String originAddress, String compareAddress) {
        if (StringUtils.isEmpty(originAddress)) {
            return false;
        }
        String[] addressArray = originAddress.split("#");
        boolean matched = Arrays.asList(addressArray).stream().filter(elem -> StringUtils.replaceBlank(elem)
                .equalsIgnoreCase(StringUtils.replaceBlank(compareAddress))).count() > 0;

        return matched;
    }
}
