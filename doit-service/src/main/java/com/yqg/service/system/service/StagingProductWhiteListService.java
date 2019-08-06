package com.yqg.service.system.service;

import com.yqg.system.dao.StagingProductWhiteListDao;
import com.yqg.system.entity.StagingProductWhiteList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by wanghuaizhou on 2019/4/18.
 */
@Service
@Slf4j
public class StagingProductWhiteListService {

    @Autowired
    private StagingProductWhiteListDao stagingProductWhiteListDao;

    public StagingProductWhiteList getProductListByUserUuid(String userUuid){

        StagingProductWhiteList scan = new StagingProductWhiteList();
        scan.setUserUuid(userUuid);
        scan.setDisabled(0);
        List<StagingProductWhiteList> list = this.stagingProductWhiteListDao.scan(scan);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);

    }

    public void updateWhiteList(StagingProductWhiteList update){
        this.stagingProductWhiteListDao.update(update);
    }
}
