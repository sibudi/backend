package com.yqg.service.user.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.user.request.BackupLinkmanRequest;
import com.yqg.service.user.response.BackupLinkman;
import com.yqg.user.dao.BackupLinkmanItemDao;
import com.yqg.user.dao.UsrLinkManDao;
import com.yqg.user.entity.BackupLinkmanItem;
import com.yqg.user.entity.UsrLinkManInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserBackupLinkmanService {

    @Autowired
    private UsrLinkManDao usrLinkManDao;

    @Autowired
    private BackupLinkmanItemDao backupLinkmanItemDao;

    @Autowired
    private OrdDao ordDao;

    @Transactional(rollbackFor = Exception.class)
    public void confirmBackupLinkman(BackupLinkmanRequest request) {
        if (StringUtils.isEmpty(request.getBackupLinkmanList())) {
            log.warn("the confirm backupLinkman is empty");
            return;
        }

        List<OrdOrder> orderList = ordDao.getLatestOrder(request.getUserUuid());
        if(CollectionUtils.isEmpty(orderList)){
            log.info("the order is empty");
            return;
        }
        OrdOrder lastOrder = orderList.get(0);
        backupLinkmanItemDao.deleteHistoryItem(lastOrder.getUuid(),request.getUserUuid());
        for(BackupLinkman item: request.getBackupLinkmanList()){
            BackupLinkmanItem insertItem = new BackupLinkmanItem();
            insertItem.setUserUuid(request.getUserUuid());
            insertItem.setOrderNo(lastOrder.getUuid());
            insertItem.setLinkmanName(item.getLinkmanName());
            insertItem.setLinkmanNumber(item.getLinkmanNumber());
            insertItem.setIsConfirmed(item.getIsConfirmed());
            backupLinkmanItemDao.insert(insertItem);
        }

    }

    /***
     * Get emergency contact
     * @param userUuid
     * @return
     */
    public List<UsrLinkManInfo> getLinkManInfo(String userUuid) {
        UsrLinkManInfo searchEntity = new UsrLinkManInfo();
        searchEntity.setDisabled(0);
        searchEntity.setUserUuid(userUuid);
        List<UsrLinkManInfo> linkManList = usrLinkManDao.scan(searchEntity);
        if (CollectionUtils.isEmpty(linkManList)) {
            return new ArrayList<>();
        }
        return linkManList.stream().filter(elem -> elem.getSequence() != UsrLinkManInfo.SequenceEnum.OWNER_BACKUP.getCode()).collect(Collectors.toList());
    }

    public void copyFromLastOrder(String userUuid, String orderNo) {

        List<OrdOrder> orderList = ordDao.getLatestOrder(userUuid);
        if (CollectionUtils.isEmpty(orderList) || orderList.size() < 2) {
            log.info("the success order list is empty: {}", orderNo);
            return;
        }
        BackupLinkmanItem searchInfo = new BackupLinkmanItem();
        searchInfo.setOrderNo(orderList.get(1).getUuid());
        searchInfo.setDisabled(0);
        List<BackupLinkmanItem> lastBackupList = backupLinkmanItemDao.scan(searchInfo);
        for (BackupLinkmanItem item : lastBackupList) {
            item.setId(null);
            item.setOrderNo(orderNo);
            item.setCreateTime(new Date());
            item.setUpdateTime(new Date());
            backupLinkmanItemDao.insert(item);
        }
    }


    /**
     * Query the alternate contact corresponding to a certain order of the user
     * @param orderNo
     * @param userUuid
     * @return
     */
    public List<BackupLinkmanItem> getBackupLinkmanList(String orderNo, String userUuid) {
        BackupLinkmanItem searchItem = new BackupLinkmanItem();
        searchItem.setUserUuid(userUuid);
        searchItem.setOrderNo(orderNo);
        searchItem.setDisabled(0);
        return backupLinkmanItemDao.scan(searchItem);
    }

    public boolean needAutoUpdateBackupLinkman(String orderNo, String userUuid){
        List<BackupLinkmanItem> dbList = getBackupLinkmanList(orderNo,userUuid);
        if(CollectionUtils.isEmpty(dbList)){
            //No alternative contact, determine if there is an emergency contact
            List<UsrLinkManInfo> dbLinkManList = getLinkManInfo(userUuid);
            if(CollectionUtils.isEmpty(dbLinkManList)||dbLinkManList.size()!=4){
                return false;
            }else{
                return  true;
            }
        }else{
            return false;
        }
    }

    /***
     * Whether the number is legal Indonesian number
     * @param phone
     * @return
     */
    private boolean isValidPhone(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return false;
        }
        String formatPhone = CheakTeleUtils.telephoneNumberValid2(phone);
        if (StringUtils.isEmpty(formatPhone)) {
            return false;
        }
        return true;
    }
}
