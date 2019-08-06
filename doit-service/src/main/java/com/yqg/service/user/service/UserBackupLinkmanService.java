package com.yqg.service.user.service;

import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.mongo.dao.UserCallRecordsDal;
import com.yqg.mongo.dao.UserContactsDal;
import com.yqg.mongo.entity.UserCallRecordsMongo;
import com.yqg.mongo.entity.UserContactsMongo;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.user.request.BackupLinkmanRequest;
import com.yqg.service.user.response.BackupLinkman;
import com.yqg.service.util.CustomEmojiUtils;
import com.yqg.user.dao.BackupLinkmanItemDao;
import com.yqg.user.dao.UsrLinkManDao;
import com.yqg.user.entity.BackupLinkmanItem;
import com.yqg.user.entity.UsrLinkManInfo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserBackupLinkmanService {

    @Autowired
    private UserCallRecordsDal userCallRecordsDal;

    @Autowired
    private UserContactsDal userContactsDal;

    @Autowired
    private UserRiskService userRiskService;

    @Autowired
    private UsrLinkManDao usrLinkManDao;

    @Autowired
    private BackupLinkmanItemDao backupLinkmanItemDao;

    @Autowired
    private OrdDao ordDao;

    @Transactional(rollbackFor = Exception.class)
    public void batchAddBackupLinkmanItem(List<BackupLinkmanItem> items,String orderNo,String userUuid) {
        //删除该订单之前的备选联系人
        backupLinkmanItemDao.deleteHistoryItem(orderNo,userUuid);
        if(CollectionUtils.isEmpty(items)){
            return;
        }
        //插入新的
        for (BackupLinkmanItem item : items) {
            item.setCreateTime(new Date());
            item.setUpdateTime(new Date());
            backupLinkmanItemDao.insert(item);
        }

    }

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

    public List<SelectedBackupItem> filterBackupLinkman(String orderNo, String userUuid) {

        List<SelectedBackupItem> resultList = new ArrayList<>();
        List<String> relativeWords = userRiskService.getUppercaseRelativeWords();

        List<SelectedBackupItem> callRecordBackupLinkMans = selectBackupLinkManFromCallRecord(orderNo, userUuid);
        resultList.addAll(callRecordBackupLinkMans);
        List<SelectedBackupItem> contactBackupLinkMans = selectBackupLinkmanFromContacts(orderNo, userUuid, relativeWords);
        resultList.addAll(contactBackupLinkMans);

        if (CollectionUtils.isEmpty(resultList)) {
            log.warn("there is no backup linkMan");
            return new ArrayList<>();
        }
        //过滤表情符号：
        resultList.stream().forEach(elem->{
            if(!StringUtils.isEmpty(elem.getLinkmanName())){
                elem.setLinkmanName(CustomEmojiUtils.removeEmoji(elem.getLinkmanName()));
            }
        });
        //按照号码去重，然后按照姓名去重【1、号码相同则相同 2、姓名相同】
        resultList = resultList.stream().distinct().collect(Collectors.toList());
        //排除4个紧急联系人
        List<UsrLinkManInfo> linkManList = getLinkManInfo(userUuid);
        resultList = resultList.stream().filter(elem -> !backupLinkManInEmergency(elem, linkManList)).collect(Collectors.toList());
        //从结果中取10个通话记录联系人+5个亲属词联系人,不足取其他联系人
        //按次数排序
        resultList.sort(Comparator.comparing(SelectedBackupItem::getCallRecordTimes).reversed());
        //第十一个元素开始讲callRecordTimes设置为0,
        int resultSize = resultList.size();
        if (resultSize > 10) {
            for (int i = 10; i < resultSize; i++) {
                SelectedBackupItem item = resultList.get(i);
                item.setCallRecordTimes(0L);
                if (item.getIsRelative() == 0) {
                    item.setIsRelative(!StringUtils.isEmpty(item.getLinkmanName()) && relativeWords.contains(item.getLinkmanName().toUpperCase()) ? 1 : 0);
                }

            }
        }
        //讲结果先按照callRecordTimes排序，然后按照isRelative排序[上述循环已经将callRecord中多余的元素callResultTimes设置为0]
        resultList.sort(Comparator.comparing(SelectedBackupItem::getCallRecordTimes).reversed().thenComparing(SelectedBackupItem::getIsRelative).reversed());

        if (resultSize > 15) {
            return resultList.subList(0, 15);
        } else {
            return resultList;
        }
    }

    /***
     * 取紧急联系人
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

    @Transactional(rollbackFor = Exception.class)
    public List<BackupLinkmanItem> addBackupLinkmanList(String userUuid,String orderNo) {
        Long startTime = System.currentTimeMillis();
        List<UserBackupLinkmanService.SelectedBackupItem> backupItems = this.filterBackupLinkman(orderNo,
                userUuid);
        log.info("the cost of filter backupLinkman is {} ms",(System.currentTimeMillis()-startTime));
        List<BackupLinkmanItem> insertList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(backupItems)){
            //保存备选联系人
            int backupSize= backupItems.size();
            for (int i = 0; i < backupSize; i++) {
                UserBackupLinkmanService.SelectedBackupItem elem = backupItems.get(i);
                BackupLinkmanItem insertItem = new BackupLinkmanItem(orderNo, userUuid,
                        elem.getLinkmanName(),
                        elem.getLinkmanNumber()
                        , elem.getCallRecordTimes() > 0 ? 1 : 0, elem.getIsRelative(), elem.getIsConfirmed(), 0);
                insertItem.setOrderSequence(i);
                insertList.add(insertItem);
            }
        }

        this.batchAddBackupLinkmanItem(insertList,orderNo,userUuid);
        return insertList;
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
     * 查询用户某一个订单对应的备选联系人
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
            //无备选联系人，判定是否有紧急联系人
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
     * 检查某个备选联系人是否在紧急联系人中
     * @param backupLinkMan
     * @param emergencyLinkMans
     * @return
     */
    private boolean backupLinkManInEmergency(SelectedBackupItem backupLinkMan, List<UsrLinkManInfo> emergencyLinkMans) {
        if (CollectionUtils.isEmpty(emergencyLinkMans)) {
            return false;
        }
        String formatBackupTel = CheakTeleUtils.telephoneNumberValid2(backupLinkMan.getLinkmanNumber());
        for (UsrLinkManInfo item : emergencyLinkMans) {
            String emergencyFormatTel = CheakTeleUtils.telephoneNumberValid2(item.getContactsMobile());
            if (formatBackupTel.equalsIgnoreCase(emergencyFormatTel)) {
                return true;
            }
            //姓名是否相同
            if (!StringUtils.isEmpty(item.getContactsName()) &&
                    (item.getContactsName().startsWith("Unreported contacts") || item.getContactsName().startsWith("未备注联系人"))) {
                continue;
            }

            if (!StringUtils.isEmpty(item.getContactsName()) && item.getContactsName().equalsIgnoreCase(backupLinkMan.getLinkmanName())) {
                return true;
            }

        }
        return false;
    }

    /***
     * 从通话记录中选择备选联系人
     * @param orderNo
     * @param userUuid
     * @return
     */
    private List<SelectedBackupItem> selectBackupLinkManFromCallRecord(String orderNo, String userUuid) {
        //选中的通话记录中联系人
        List<CallRecordData> callRecords = getCallRecords(orderNo, userUuid);
        if (CollectionUtils.isEmpty(callRecords)) {
            return new ArrayList<>();
        }
        List<Map.Entry<CallRecordData, Long>> selectedCallRecords;
        //从通话记录中找最近60的数据筛选15人
        Date before60 = DateUtils.addDate(new Date(), -60);
        //60天内的手机号码
        List<CallRecordData> recent60List =
                callRecords.stream().filter(elem -> elem.getMsgDate() != null
                        && elem.getMsgDate().compareTo(before60) >= 0
                        && isValidPhone(elem.getNumber())
                        && elem.getDuration() != null && elem.getDuration() > 0)
                        .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(recent60List)) {
            return new ArrayList<>();
        }

        //不为空,按手机号分组，按次数排序取前15个
        Map<CallRecordData, Long> dataMap = recent60List.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<Map.Entry<CallRecordData, Long>> sortedList =
                dataMap.entrySet().stream().sorted(Map.Entry.<CallRecordData, Long>comparingByValue().reversed()).collect(Collectors.toList());
        if (sortedList.size() > 15) {
            selectedCallRecords = sortedList.subList(0, 15).stream().collect(Collectors.toList());
        } else {
            selectedCallRecords = sortedList.stream().collect(Collectors.toList());
        }

        if (CollectionUtils.isEmpty(selectedCallRecords)) {
            return new ArrayList<>();
        }
        return selectedCallRecords.stream().map(elem -> new SelectedBackupItem(elem.getKey().getName(), elem.getKey().getNumber(), elem.getValue(),
                0, 1)).collect(Collectors.toList());
    }

    /***
     * 从通讯录中选择备选联系人
     * @param orderNo
     * @param userUuid
     * @param relativeWords
     * @return
     */
    private List<SelectedBackupItem> selectBackupLinkmanFromContacts(String orderNo, String userUuid, List<String> relativeWords) {
        List<UserContactData> contactList = getContactDataList(orderNo, userUuid);
        if (CollectionUtils.isEmpty(contactList)) {
            return new ArrayList<>();
        }
        List<UserContactData> validContactList = contactList.stream().filter(elem -> isValidPhone(elem.getPhone())).
                distinct().collect(Collectors.toList());
        //设置亲属标记
        validContactList.stream().forEach(elem -> elem.setIsRelative(!StringUtils.isEmpty(elem.getName()) && relativeWords.contains(elem.getName().toUpperCase()) ? 1 : 0)
        );
        //按照亲属非亲属排序，亲属优先
        List<UserContactData> sortedList =
                validContactList.stream().sorted(Comparator.comparing(UserContactData::getIsRelative).reversed()).collect(Collectors.toList());
        if (sortedList.size() >= 15) {
            return sortedList.subList(0, 15).stream().map(elem -> new SelectedBackupItem(elem.getName(), elem.getPhone(), 0L, elem.isRelative, 1)).collect(Collectors.toList());
        } else {
            return sortedList.stream().map(elem -> new SelectedBackupItem(elem.getName(), elem.getPhone(), 0L, elem.isRelative, 1)).collect(Collectors.toList());
        }
    }

    /***
     * 号码是否合法的印尼号码
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


    /***
     * 获取通话记录列表
     * @param orderNo
     * @param userUuid
     * @return
     */
    private List<CallRecordData> getCallRecords(String orderNo, String userUuid) {
        // 手机通话记录
        UserCallRecordsMongo search = new UserCallRecordsMongo();
        search.setOrderNo(orderNo);
        search.setUserUuid(userUuid);


        List<UserCallRecordsMongo> orderUserCallRecordsLists = this.userCallRecordsDal.find(search);
        if (CollectionUtils.isEmpty(orderUserCallRecordsLists)) {
            log.error("mongodb data is empty, orderNo: {} ,userUuid: {}", orderNo, userUuid);
            return null;
        }

        UserCallRecordsMongo userCallRecordsMongo = orderUserCallRecordsLists.get(0);
        String dataStr = userCallRecordsMongo.getData();
        if (StringUtils.isEmpty(dataStr)) {
            log.info("the dataStr is empty , orderNo: {} ,userUuid: {}", orderNo, userUuid);
            return null;
        }

        List<CallRecordData> userCallRecordsList = JsonUtils.toList(dataStr, CallRecordData.class);
        if (CollectionUtils.isEmpty(userCallRecordsList)) {
            log.error("record data is empty,  orderNo: {} ,userUuid: {}", orderNo, userUuid);
            return null;
        }
        return userCallRecordsList;
    }

    /***
     * 获取通讯录列表
     * @param orderNo
     * @param userUuid
     * @return
     */
    private List<UserContactData> getContactDataList(String orderNo, String userUuid) {
        UserContactsMongo search = new UserContactsMongo();
        search.setUserUuid(userUuid);
        search.setOrderNo(orderNo);

        List<UserContactsMongo> orderUserContacts = this.userContactsDal.find(search);
        if (CollectionUtils.isEmpty(orderUserContacts)) {
            log.warn("mongodb contact data is empty orderNo: {},userUuid: {}", orderNo, userUuid);
            return null;
        }

        UserContactsMongo userContactsMongo = orderUserContacts.get(0);
        String userContactData = userContactsMongo.getData();
        if (org.apache.commons.lang3.StringUtils.isEmpty(userContactData)) {
            log.warn("mongodb contact data is empty orderNo: {},userUuid: {}", orderNo, userUuid);
            return null;
        }
        List<UserContactData> contactList = JsonUtils.toList(userContactData, UserContactData.class);
        return contactList;
    }


    @Data
    public static class CallRecordData {

        private String date;//日期
        private String number;//号码
        private String name;//姓名
        private Integer duration;//时长
        private String type;//类型（1=呼入，2=呼出）

        public Date getMsgDate() {
            if (com.yqg.common.utils.StringUtils.isEmpty(date)) {
                return null;
            }
            return DateUtils.stringToDate(date, DateUtils.FMT_YYYY_MM_DD_HH_mm_ss);
        }

        @Override
        public int hashCode() {
            String formatData = "";
            if (StringUtils.isEmpty(number)) {
                String formatNumber = CheakTeleUtils.telephoneNumberValid2(number);
                formatData = formatNumber;
            }
            return formatData.hashCode();
        }

        @Override
        public boolean equals(Object target) {
            if (target == null) {
                return false;
            }
            if (target == this) {
                return true;
            }
            if (!(target instanceof CallRecordData)) {
                return false;
            }

            if (StringUtils.isEmpty(this.getNumber())) {
                String targetFormatPhone = CheakTeleUtils.telephoneNumberValid2(((CallRecordData) target).getNumber());
                String formatPhone = CheakTeleUtils.telephoneNumberValid2(this.getNumber());
                return !StringUtils.isEmpty(formatPhone) && formatPhone.equals(targetFormatPhone);
            }
            return false;
        }
    }

    @Getter
    @Setter
    public static class UserContactData {
        private String name;
        private String phone;
        private int isRelative;//是否亲属1:是0：否

        public boolean isNotEmpty() {
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(name) && org.apache.commons.lang3.StringUtils.isEmpty(phone)) {
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            String formatData = "";
            if (StringUtils.isEmpty(phone)) {
                String formatNumber = CheakTeleUtils.telephoneNumberValid2(phone);
                formatData = formatNumber;
            }
            return formatData.hashCode();
        }

        @Override
        public boolean equals(Object target) {
            if (target == null) {
                return false;
            }
            if (target == this) {
                return true;
            }
            if (!(target instanceof UserContactData)) {
                return false;
            }
            if (StringUtils.isEmpty(this.getPhone())) {
                String targetFormatPhone = CheakTeleUtils.telephoneNumberValid2(((UserContactData) target).getPhone());
                String formatPhone = CheakTeleUtils.telephoneNumberValid2(this.getPhone());
                return !StringUtils.isEmpty(formatPhone) && formatPhone.equals(targetFormatPhone);
            }

            return false;
        }

    }

    //筛选出来的备选联系人项
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class SelectedBackupItem {
        private String linkmanName;
        private String linkmanNumber;
        private Long callRecordTimes;
        private Integer isRelative;//1:是0:否
        private Integer isConfirmed;//用户是否确认 1:是，0：否

        @Override
        public int hashCode() {
            String formatData = "";
            if (StringUtils.isEmpty(linkmanNumber)) {
                String formatNumber = CheakTeleUtils.telephoneNumberValid2(linkmanNumber);
                formatData = formatNumber;
            }
            return formatData.hashCode();
        }

        @Override
        public boolean equals(Object target) {
            System.err.println("in equal");
            if (target == null) {
                return false;
            }
            if (!(target instanceof SelectedBackupItem)) {
                return false;
            }
            String formatNumber = CheakTeleUtils.telephoneNumberValid2(linkmanNumber);
            String targetFormatNumber = CheakTeleUtils.telephoneNumberValid2(((SelectedBackupItem) target).getLinkmanNumber());
            if (com.yqg.common.utils.StringUtils.isNotEmpty(formatNumber) && formatNumber.equals(targetFormatNumber)) {
                //号码相同
                return true;
            }
            //排除特殊姓名

            if (com.yqg.common.utils.StringUtils.isNotEmpty(linkmanName) && linkmanName.equalsIgnoreCase(((SelectedBackupItem) target).getLinkmanName())) {
                if (linkmanName.startsWith("Unreported contacts") || linkmanName.startsWith("未备注联系人")) {
                    return false;
                }
                //姓名相同
                return true;
            }
            return false;
        }
    }
}
