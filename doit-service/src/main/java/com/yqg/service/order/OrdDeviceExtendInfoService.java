package com.yqg.service.order;

import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.MD5Util;
import com.yqg.common.utils.StringUtils;
import com.yqg.order.dao.OrdDeviceExtendInfoDao;
import com.yqg.order.entity.OrdDeviceExtendInfo;
import com.yqg.order.entity.OrdDeviceInfo;
import com.yqg.service.user.service.UserBackupLinkmanService;
import com.yqg.user.entity.UsrLinkManInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/***
 * 设备信息扩展
 */
@Service
@Slf4j
public class OrdDeviceExtendInfoService {

    @Autowired
    private OrdDeviceExtendInfoDao ordDeviceExtendInfoDao;
    @Autowired
    private UserBackupLinkmanService userBackupLinkmanService;
    @Autowired
    private OrdDeviceInfoService ordDeviceInfoService;

    /**
     * 订单设备变化后更行设备扩展信息
     *
     * @param deviceInfo
     */
    public void saveExtendInfo(OrdDeviceInfo deviceInfo) {
        //查询联系人信息
        if (deviceInfo == null) {
            log.warn("the device info is empty when add extend info");
            return;
        }
        if (StringUtils.isEmpty(deviceInfo.getUserUuid()) || StringUtils.isEmpty(deviceInfo.getOrderNo())) {
            log.warn("the userUuid, orderNo is empty in deviceInfo");
            return;
        }
        List<UsrLinkManInfo> linkmanList = userBackupLinkmanService.getLinkManInfo(deviceInfo.getUserUuid());
        if (CollectionUtils.isEmpty(linkmanList)) {
            return;
        }
        saveExtendInfo(deviceInfo, linkmanList);

    }

    /***
     * 联系人变化后更新设备扩展信息
     * @param linkmanList
     */
    public void saveExtendInfo(List<UsrLinkManInfo> linkmanList) {
        if (CollectionUtils.isEmpty(linkmanList) || StringUtils.isEmpty(linkmanList.get(0).getUserUuid())) {
            return;
        }
        //查询当前订单设备
        String userUuid = linkmanList.get(0).getUserUuid();
        Optional<OrdDeviceInfo> optionalDeviceInfo = ordDeviceInfoService.getLatestDeviceInfoByUserUuid(userUuid);
        if (!optionalDeviceInfo.isPresent()) {
            return;
        }

        saveExtendInfo(optionalDeviceInfo.get(), linkmanList);

    }


    public Integer hitExtendDeviceUserCount(OrdDeviceInfo deviceInfo) {
        //计算当前用户的扩展设备信息
        if (StringUtils.isEmpty(deviceInfo.getDeviceType()) ||
                StringUtils.isEmpty(deviceInfo.getDeviceName())
                || StringUtils.isEmpty(deviceInfo.getPhoneBrand())
                || StringUtils.isEmpty(deviceInfo.getCpuType())) {
            return null;
        }

        List<UsrLinkManInfo> linkmanList = userBackupLinkmanService.getLinkManInfo(deviceInfo.getUserUuid());
        if (CollectionUtils.isEmpty(linkmanList)) {
            return null;
        }
        List<String> deviceFieldList = buildExtendDeviceField(deviceInfo,linkmanList);
        Set<String> distinctSet = new HashSet<>();
        //查询device1,2,3,4 匹配到的用户
        for(String device: deviceFieldList){
            if(StringUtils.isEmpty(device)){
                continue;
            }
            List<String> deviceHit = ordDeviceExtendInfoDao.getHistUserIdList(device,deviceInfo.getUserUuid());
            if(!CollectionUtils.isEmpty(deviceHit)){
                distinctSet.addAll(deviceHit);
            }
        }
        return distinctSet.size();
    }


    private List<String> buildExtendDeviceField(OrdDeviceInfo deviceInfo, List<UsrLinkManInfo> linkmanList) {
        String linkman1 = null, linkman2 = null, linkman3 = null, linkman4 = null, device1 = null, device2 = null, device3 = null, device4 = null;

        for (UsrLinkManInfo linkManInfo : linkmanList) {
            if (1 == linkManInfo.getSequence()) {
                linkman1 = CheakTeleUtils.telephoneNumberValid2(linkManInfo.getContactsMobile());
            } else if (2 == linkManInfo.getSequence()) {
                linkman2 = CheakTeleUtils.telephoneNumberValid2(linkManInfo.getContactsMobile());
            } else if (4 == linkManInfo.getSequence()) {
                linkman3 = CheakTeleUtils.telephoneNumberValid2(linkManInfo.getContactsMobile());
            } else if (5 == linkManInfo.getSequence()) {
                linkman4 = CheakTeleUtils.telephoneNumberValid2(linkManInfo.getContactsMobile());
            }
        }
        String deviceCompose = deviceInfo.getDeviceType() + deviceInfo.getDeviceName() + deviceInfo.getPhoneBrand() + deviceInfo.getCpuType();
        if (StringUtils.isNotEmpty(linkman1)) {
            device1 = MD5Util.md5LowerCase(deviceCompose + linkman1);
        }
        if (StringUtils.isNotEmpty(linkman2)) {
            device2 = MD5Util.md5LowerCase(deviceCompose + linkman2);
        }
        if (StringUtils.isNotEmpty(linkman3)) {
            device3 = MD5Util.md5LowerCase(deviceCompose + linkman3);
        }
        if (StringUtils.isNotEmpty(linkman4)) {
            device4 = MD5Util.md5LowerCase(deviceCompose + linkman4);
        }
        return Arrays.asList(device1, device2, device3, device4);
    }
    private void saveExtendInfo(OrdDeviceInfo deviceInfo, List<UsrLinkManInfo> linkmanList) {

        //检查订单是否已经保存相应记录
        OrdDeviceExtendInfo searchParam = new OrdDeviceExtendInfo();
        searchParam.setDisabled(0);
        searchParam.setOrderNo(deviceInfo.getOrderNo());
        List<OrdDeviceExtendInfo> dbData = ordDeviceExtendInfoDao.scan(searchParam);

        List<String> deviceFieldList = buildExtendDeviceField(deviceInfo,linkmanList);

        if (CollectionUtils.isEmpty(dbData)) {
            //insert
            OrdDeviceExtendInfo insertEntity = new OrdDeviceExtendInfo();
            insertEntity.setDisabled(0);
            Date createTime = deviceInfo != null && deviceInfo.getCreateTime() != null ? deviceInfo.getCreateTime() : new Date();
            Date updateTime = deviceInfo != null && deviceInfo.getUpdateTime() != null ? deviceInfo.getUpdateTime() : new Date();


            insertEntity.setCreateTime(createTime);
            insertEntity.setUpdateTime(updateTime);
            insertEntity.setDevice1(deviceFieldList.get(0));
            insertEntity.setDevice2(deviceFieldList.get(1));
            insertEntity.setDevice3(deviceFieldList.get(2));
            insertEntity.setDevice4(deviceFieldList.get(3));
            insertEntity.setUserUuid(deviceInfo.getUserUuid());
            insertEntity.setOrderNo(deviceInfo.getOrderNo());
            ordDeviceExtendInfoDao.insert(insertEntity);
        } else {
            //update
            OrdDeviceExtendInfo updateEntity = dbData.get(0);
            boolean updated = false;
            if (StringUtils.isNotEmpty(deviceFieldList.get(0))) {
                updateEntity.setDevice1(deviceFieldList.get(0));
                updated = true;
            }
            if (StringUtils.isNotEmpty(deviceFieldList.get(1))) {
                updateEntity.setDevice2(deviceFieldList.get(1));
                updated = true;
            }
            if (StringUtils.isNotEmpty(deviceFieldList.get(2))) {
                updateEntity.setDevice3(deviceFieldList.get(2));
                updated = true;
            }
            if (StringUtils.isNotEmpty(deviceFieldList.get(3))) {
                updateEntity.setDevice4(deviceFieldList.get(3));
                updated = true;
            }
            if (updated) {
                updateEntity.setUpdateTime(new Date());
            }
            ordDeviceExtendInfoDao.update(updateEntity);
        }
    }
}
