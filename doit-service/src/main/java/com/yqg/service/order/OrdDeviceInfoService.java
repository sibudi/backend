package com.yqg.service.order;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.order.dao.OrdDeviceInfoDao;
import com.yqg.order.entity.OrdDeviceInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Didit Dwianto on 2017/11/29.
 */
@Service
@Slf4j
public class OrdDeviceInfoService {

    @Autowired
    OrdDeviceInfoDao mobileDeviceInfoDao;
    @Autowired
    private OrdDeviceExtendInfoService ordDeviceExtendInfoService;

    public void saveMobileDeviceInfo(OrdDeviceInfo mobileDeviceInfo){

        OrdDeviceInfo searchInfo = new OrdDeviceInfo();
        searchInfo.setUserUuid(mobileDeviceInfo.getUserUuid());
        searchInfo.setOrderNo(mobileDeviceInfo.getOrderNo());
        List<OrdDeviceInfo> scanList = this.mobileDeviceInfoDao.scan(searchInfo);
        if (!CollectionUtils.isEmpty(scanList)){

            OrdDeviceInfo model = scanList.get(0);
            mobileDeviceInfo.setUuid(model.getUuid());

            mobileDeviceInfo.setUpdateTime(new Date());
            this.mobileDeviceInfoDao.update(mobileDeviceInfo);
        }else {
            mobileDeviceInfo.setCreateTime(new Date());
            mobileDeviceInfo.setUpdateTime(new Date());
            mobileDeviceInfo.setUuid(UUIDGenerateUtil.uuid());
            this.mobileDeviceInfoDao.insert(mobileDeviceInfo);
        }
        ordDeviceExtendInfoService.saveExtendInfo(mobileDeviceInfo);
    }

    public Optional<OrdDeviceInfo> getDeviceInfoByOrderNo(String orderNo){
        OrdDeviceInfo searchInfo = new OrdDeviceInfo();
        searchInfo.setOrderNo(orderNo);
        searchInfo.setDisabled(0);
        List<OrdDeviceInfo> dbDeviceList = mobileDeviceInfoDao.scan(searchInfo);
        if(CollectionUtils.isEmpty(dbDeviceList)){
            return Optional.empty();
        }
        return Optional.of(dbDeviceList.get(0));
    }


    public Optional<OrdDeviceInfo> getLatestDeviceInfoByUserUuid(String userUuid){
        OrdDeviceInfo searchInfo = new OrdDeviceInfo();
        searchInfo.setUserUuid(userUuid);
        searchInfo.setDisabled(0);
        List<OrdDeviceInfo> dbDeviceList = mobileDeviceInfoDao.scan(searchInfo);
        if(CollectionUtils.isEmpty(dbDeviceList)){
            return Optional.empty();
        }
        return dbDeviceList.stream().max(Comparator.comparing(OrdDeviceInfo::getCreateTime));
    }


    public String getUserDeviceImei(String userUuid,String orderNo){

        // 查询imei号
        OrdDeviceInfo info = new OrdDeviceInfo();
        info.setDisabled(0);
        info.setUserUuid(userUuid);
        info.setOrderNo(orderNo);
        List<OrdDeviceInfo> scanList = this.mobileDeviceInfoDao.scan(info);
        if (!CollectionUtils.isEmpty(scanList)){
            return scanList.get(0).getIMEI();
        }else {
            return "";
        }
    }

    public String getPhoneBrand(String userUuid,String orderNo){
        OrdDeviceInfo info = new OrdDeviceInfo();
        info.setDisabled(0);
        info.setUserUuid(userUuid);
        info.setOrderNo(orderNo);
        List<OrdDeviceInfo> scanList = this.mobileDeviceInfoDao.scan(info);
        if (!CollectionUtils.isEmpty(scanList)){
            return scanList.get(0).getPhoneBrand();
        }else {
            return "";
        }
    }

    public List<OrdDeviceInfo> getListById(Integer startId, Integer endId) {
        return mobileDeviceInfoDao.getListById(startId, endId);
    }
}
