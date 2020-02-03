package com.yqg.service.system.service;

import com.yqg.system.dao.StagingProductWhiteListDao;
import com.yqg.system.entity.StagingProductWhiteList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

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

    public void insertWhiteList(StagingProductWhiteList productWhiteList){
        this.stagingProductWhiteListDao.insert(productWhiteList);
    }

    public void upsertWhiteList(String userUuid, String productUuid, String beachId, String ruleName){
        StagingProductWhiteList productWhiteList = this.getProductListByUserUuid(userUuid);
        if(productWhiteList == null){
            productWhiteList = new StagingProductWhiteList();
            productWhiteList.setUuid(UUID.randomUUID().toString());
            productWhiteList.setUserUuid(userUuid);
            
            productWhiteList.setProductUuid(productUuid);
            productWhiteList.setBeachId(beachId);
            productWhiteList.setRuleName(ruleName);
            this.insertWhiteList(productWhiteList);
        }
        else{
            productWhiteList.setProductUuid(productUuid);
            this.updateWhiteList(productWhiteList);
        }
    }
}
