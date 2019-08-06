package com.yqg.service.user.service;

import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.user.dao.UsrAddressDetailDao;
import com.yqg.user.entity.UsrAddressDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class UserAddressDetailService {
    @Autowired
    private UsrAddressDetailDao usrAddressDetailDao;

    public UsrAddressDetail getUserAddressDetailByType(String userId, UsrAddressEnum addressEnum) {
        UsrAddressDetail addressDetail = new UsrAddressDetail();
        addressDetail.setDisabled(0);
        addressDetail.setAddressType(addressEnum.getType());
        List<UsrAddressDetail> addressList = usrAddressDetailDao.scan(addressDetail);
        if (CollectionUtils.isEmpty(addressList)) {
            return null;
        }
        return addressList.get(0);
    }
}
