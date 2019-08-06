package com.yqg.service.order;

import com.github.pagehelper.StringUtil;
import com.yqg.common.utils.*;
import com.yqg.order.dao.OrdDeviceInfoDao;
import com.yqg.order.entity.OrdDeviceInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.swing.text.html.Option;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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


    public void updateCustomDeviceFingerprint(String orderNo, String userUuid, String appList) {
        try {
            if (StringUtil.isEmpty(appList)) {
                return;
            }
            if (StringUtil.isEmpty(orderNo) && StringUtil.isEmpty(userUuid)) {
                return;
            }
            List<InstalledAppData> appDataList = JsonUtils.toList(appList, InstalledAppData.class);
            if (CollectionUtils.isEmpty(appDataList)) {
                return;
            }
            Date currentDate = new Date();
            Date threeYearAgo = DateUtils.addDate(currentDate, -360 * 3);
            appDataList =
                    appDataList.stream().filter(elem -> elem.getInstalledDate() != null && elem.getInstalledDate().compareTo(threeYearAgo) >= 0).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(appDataList)) {
                return;
            }
            appDataList.sort(Comparator.comparing(InstalledAppData::getInstalledDate));

            //取前10的数据组合
            int appListSize = appDataList.size();
            String resultData = "";
            for (int i = 0; i < appListSize && i < 10; i++) {
                resultData = resultData + appDataList.get(i).getAppName() + appDataList.get(i).getAppInstallTime();
            }

            //更新到设备信息
            if (StringUtil.isEmpty(resultData)) {
                return;
            }
            OrdDeviceInfo ordDeviceInfo = new OrdDeviceInfo();
            ordDeviceInfo.setDisabled(0);
            ordDeviceInfo.setUserUuid(userUuid);
            ordDeviceInfo.setOrderNo(orderNo);
            List<OrdDeviceInfo> dbList = mobileDeviceInfoDao.scan(ordDeviceInfo);
            if (CollectionUtils.isEmpty(dbList)) {
                return;
            }
            dbList.get(0).setAppFingerprint(MD5Util.md5LowerCase(resultData));
            mobileDeviceInfoDao.update(dbList.get(0));
        } catch (Exception e) {
            log.error("update device fingerprint error, orderNo=" + orderNo, e);
        }
    }


    @Getter
    @Setter
    public static class InstalledAppData {
        private String appName;
        private String appInstallTime;

        public Date getInstalledDate() {
            if (StringUtils.isEmpty(appInstallTime)) {
                return null;
            }
            return DateUtils.stringToDate(appInstallTime, DateUtils.FMT_YYYY_MM_DD_HH_mm_ss);
        }

    }

    public List<OrdDeviceInfo> getListById(Integer startId, Integer endId) {
        return mobileDeviceInfoDao.getListById(startId, endId);
    }
}
