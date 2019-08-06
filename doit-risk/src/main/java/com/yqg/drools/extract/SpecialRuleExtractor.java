package com.yqg.drools.extract;

import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.model.KeyConstant;
import com.yqg.drools.model.SpecialModel;
import com.yqg.drools.model.base.RuleSetEnum;
import com.yqg.drools.service.UserService;
import com.yqg.drools.utils.RuleUtils;
import com.yqg.order.entity.OrdOrder;
import com.yqg.user.entity.UsrAddressDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class SpecialRuleExtractor implements BaseExtractor<SpecialModel> {

    @Autowired
    private UserService userService;

    @Override
    public boolean filter(RuleSetEnum ruleSet) {
        return RuleSetEnum.SPECIAL_RULE.equals(ruleSet);
    }

    @Override
    public Optional<SpecialModel> extractModel(OrdOrder order, KeyConstant keyConstant) throws Exception {
        SpecialModel specialModel = new SpecialModel();
        UsrAddressDetail homeAddress = userService.getUserAddressDetailByType(order.getUserUuid(),
                UsrAddressEnum.HOME);
        if(homeAddress==null){
            return Optional.empty();
        }
        if (StringUtils.isNotEmpty(homeAddress.getProvince())) {
            if(StringUtils.isNotEmpty(keyConstant.getEarthquakeArea())){
                Optional<String> foundElem = Arrays.asList(keyConstant.getEarthquakeArea().split("#")).stream().filter(elem->elem.equalsIgnoreCase
                        (homeAddress.getProvince
                        ()))
                        .findFirst();
                specialModel.setProvinceInEarthquakeArea(foundElem.isPresent());
            }else{
                specialModel.setProvinceInEarthquakeArea(false);
            }

        }
        if (StringUtils.isNotEmpty(homeAddress.getCity())) {
            if(StringUtils.isNotEmpty(keyConstant.getEarthquakeCity())){
                Optional<String> foundElem = Arrays.asList(keyConstant.getEarthquakeCity().split("#")).stream().filter(elem->elem.equalsIgnoreCase
                        (homeAddress.getCity
                        ()))
                        .findFirst();
                specialModel.setCityInEarthquakeArea(foundElem.isPresent());
            }else{
                specialModel.setCityInEarthquakeArea(false);
            }

        }

        return Optional.of(specialModel);
    }
}
