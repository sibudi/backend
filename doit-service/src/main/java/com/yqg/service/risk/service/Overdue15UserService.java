package com.yqg.service.risk.service;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.MD5Util;
import com.yqg.mongo.entity.UserCallRecordsMongo;
import com.yqg.mongo.entity.UserContactsMongo;
import com.yqg.mongo.entity.UserMessagesMongo;
import com.yqg.service.user.service.UserDetailService;
import com.yqg.service.user.service.UsrCertificationService;
import com.yqg.user.dao.UsrBlackListDao;
import com.yqg.user.entity.UsrBlackList;
import com.yqg.user.entity.UsrCertificationInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class Overdue15UserService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UsrBlackListDao usrBlackListDao;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private RedisClient redisClient;

    public void addOverdue15Users() {
        try {
//            //逾期15天用户添加到黑名单用户表
//            usrBlackListDao.addOverdue15Users();
//            //将黑名单用户表中逾期[15,30)的用户如果已经到了逾期30天修改type
//            usrBlackListDao.transUserFromOverdue15To30();

            //逾期7天用户添加到黑名单用户表
            usrBlackListDao.addOverdue7Users();
            //将黑名单用户表中逾期[15,30)的用户如果已经到了逾期30天修改type
            usrBlackListDao.transUserFromOverdue15To30();
            //将黑名单用户表中逾期[7,15)的用户如果已经到了逾期[15,30)天修改type
            usrBlackListDao.transUserFromOverdue7To15();

            List<UsrBlackList> blackLists = usrBlackListDao.getCurrentDayTransFrom7to15();
            addCallRecordToRedis(blackLists, true, UsrBlackList.BlackUserCategory.OVERDUE15);
            addContactToRedis(blackLists, true, UsrBlackList.BlackUserCategory.OVERDUE15);
            addSmsToRedis(blackLists, true, UsrBlackList.BlackUserCategory.OVERDUE15);

            //redis中移除7天转为15天的数据
            addCallRecordToRedis(blackLists, true, UsrBlackList.BlackUserCategory.OVERDUE15, true);
            addContactToRedis(blackLists, true, UsrBlackList.BlackUserCategory.OVERDUE15, true);
            addSmsToRedis(blackLists, true, UsrBlackList.BlackUserCategory.OVERDUE15, true);

            //保存7天的数据
            blackLists = usrBlackListDao.getCurrentDayOverdue7();
            addCallRecordToRedis(blackLists, true, UsrBlackList.BlackUserCategory.OVERDUE7);
            addContactToRedis(blackLists, true, UsrBlackList.BlackUserCategory.OVERDUE7);
            addSmsToRedis(blackLists, true, UsrBlackList.BlackUserCategory.OVERDUE7);

            //设置七天数据对应的字段[whatsapp,公司地址，居住地址，公司电话]
            if (CollectionUtils.isEmpty(blackLists)) {
                return;
            }
            for (UsrBlackList item : blackLists) {
                addExternalData(item);
            }


        } catch (Exception e) {
            log.error("add overdue users to usrBlackList error", e);
        }
    }

    public void addExternalData(UsrBlackList item) {
        try {
            String whatsApp = userDetailService.getWhatsAppAccount(item.getUserUuid(), false);
            //公司电话
            String companyTel = userDetailService.getCompanyPhone(item.getUserUuid());
            //居住地址
            String homeAddress = userDetailService.getAddressUpperCaseWithSeparator(item.getUserUuid(), UsrAddressEnum.HOME);
            String md5ForHomeAddress = StringUtils.isEmpty(homeAddress) ? null : MD5Util.md5LowerCase(homeAddress);
            //公司地址
            String companyAddress = userDetailService.getAddressUpperCaseWithSeparator(item.getUserUuid(), UsrAddressEnum.COMPANY);

            String md5ForCompanyAddress = StringUtils.isEmpty(companyAddress) ? null : MD5Util.md5LowerCase(companyAddress);
            item.setCompanyTel(companyTel);
            item.setMd5ForCompanyAddress(md5ForCompanyAddress);
            item.setMd5ForHomeAddress(md5ForHomeAddress);
            item.setWhatsapp(whatsApp);
            if (item.getId() == null) {
                log.info("the id is empty, userUuid = {}", item.getUserUuid());
                return;
            }
            Integer affectRow = usrBlackListDao.update(item);
            if (affectRow != 1) {
                log.info("the affected row is not 1,userUuid: {}", item.getUserUuid());

            }
        } catch (Exception e) {
            log.error("add external data error,id = " + item.getId(), e);
        }
    }



    public void addContactToRedis(List<UsrBlackList> userList, boolean fromDoit, UsrBlackList.BlackUserCategory category, Boolean... isRemoved) {
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }
        int totalSize = 0;
        //分组查询，每次查询50个用户
        int maxLoop = (int) Math.ceil(userList.size() / 50.0);
        for (int i = 0; i < maxLoop; i++) {
            int startIndex = i * 50;
            int endIndex = (i + 1) * 50;
            if (endIndex >= userList.size()) {
                endIndex = userList.size();
            }
            List<String> userIdList = userList.subList(startIndex, endIndex).stream().map(elem -> elem.getUserUuid()).filter(elem->!StringUtils.isEmpty(elem)).distinct().collect(Collectors.toList());
            Query query = new Query();
            Criteria criteria = new Criteria("userUuid");
            criteria.in(userIdList);
            query.addCriteria(criteria);
            List<UserContactsMongo> resultTmp = null;
            if (fromDoit) {
                resultTmp = mongoTemplate.find(query, UserContactsMongo.class, "UserContactsMongo");
            } else {
                resultTmp = mongoTemplate.find(query, UserContactsMongo.class, "UUUserContactsMongo");
            }
            if (!CollectionUtils.isEmpty(resultTmp)) {
                for (UserContactsMongo mongo : resultTmp) {
                    String data = mongo.getData();
                    if (StringUtils.isEmpty(data)) {
                        continue;
                    }
                    List<ContactData> tmpList = null;
                    try {
                        tmpList = JsonUtils.toList(data, ContactData.class);
                    } catch (Exception e) {
                        log.info("error data: {} ,orderNo: {}", data, mongo.getOrderNo());
                        log.error("error", e);
                        continue;
                    }
                    if (!CollectionUtils.isEmpty(tmpList)) {

                        //doit中获得的手机号
                        List<String> doitContactPhoneList =
                                tmpList.stream().map(elem -> {
                                    if (StringUtils.isEmpty(elem.getPhone())) {
                                        return null;
                                    }
                                    return CheakTeleUtils.telephoneNumberValid2(elem.getPhone());
                                }).filter(elem -> !StringUtils.isEmpty(elem)).distinct().collect(Collectors.toList());

                        //手机号保存到redis
                        if (!CollectionUtils.isEmpty(doitContactPhoneList)) {
                            String key = "";
                            switch (category){
                                case OVERDUE7:
                                    key = RedisContants.RISK_BLACKLIST_OVERDUE7_CONTACT;
                                    break;
                                case FRAUD:
                                    key = RedisContants.RISK_BLACKLIST_FRAUD_CONTACT;
                                    break;
                                default:
                                    key = RedisContants.RISK_BLACKLIST_OVERDUE15_CONTACT;
                                    break;
                            }

                            if (isRemoved != null && isRemoved.length > 0 && isRemoved[0] == true) {
                                redisClient.setRemove(key, doitContactPhoneList);
                            } else {
                                redisClient.setAdd(key, doitContactPhoneList);
                            }
                            totalSize = totalSize + doitContactPhoneList.size();
                        }
                    }

                }
            }
        }
        log.info("contact totalSize: {}, from: {}", totalSize, fromDoit);
    }


    public void addCallRecordToRedis(List<UsrBlackList> userList, boolean fromDoit, UsrBlackList.BlackUserCategory category, Boolean... isRemoved) {
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }

        int totalSize = 0;

        //分组查询，每次查询50个用户
        int maxLoop = (int) Math.ceil(userList.size() / 50.0);
        for (int i = 0; i < maxLoop; i++) {

            int startIndex = i * 50;
            int endIndex = (i + 1) * 50;
            if (endIndex >= userList.size()) {
                endIndex = userList.size();
            }
            try {
                List<String> userIdList =
                        userList.subList(startIndex, endIndex).stream().map(elem -> elem.getUserUuid()).filter(elem->!StringUtils.isEmpty(elem)).distinct().collect(Collectors.toList());
                Query query = new Query();
                Criteria criteria = new Criteria("userUuid");
                criteria.in(userIdList);
                query.addCriteria(criteria);
                List<UserCallRecordsMongo> resultTmp = null;
                if (fromDoit) {
                    resultTmp = mongoTemplate.find(query, UserCallRecordsMongo.class, "UserCallRecordsMongo");
                } else {
                    resultTmp = mongoTemplate.find(query, UserCallRecordsMongo.class, "UUUserCallRecordsMongo");
                }
                if (!CollectionUtils.isEmpty(resultTmp)) {
                    for (UserCallRecordsMongo mongo : resultTmp) {
                        String data = mongo.getData();
                        if (StringUtils.isEmpty(data)) {
                            continue;
                        }

                        List<UserCallRecordsData> tmpList = null;
                        try {
                            tmpList = JsonUtils.toList(data, UserCallRecordsData.class);
                        } catch (Exception e) {
                            log.error("error format,orderNo: " + mongo.getOrderNo(), e);
                        }
                        if (!CollectionUtils.isEmpty(tmpList)) {


                            List<String> doitPhoneList =
                                    tmpList.stream().filter(elem -> elem.getDuration() != null && elem.getDuration() > 0).map(elem -> {
                                        if (StringUtils.isEmpty(elem.getNumber())) {
                                            return null;
                                        }
                                        return CheakTeleUtils.telephoneNumberValid2(elem.getNumber());
                                    }).filter(elem -> !StringUtils.isEmpty(elem)).distinct().collect(Collectors.toList());

                            //手机号保存到redis
                            if (!CollectionUtils.isEmpty(doitPhoneList)) {
                                String key = "";
                                switch (category){
                                    case OVERDUE7:
                                        key = RedisContants.RISK_BLACKLIST_OVERDUE7_CALL_RECORD;
                                        break;
                                    case FRAUD:
                                        key = RedisContants.RISK_BLACKLIST_FRAUD_CALL_RECORD;
                                        break;
                                    default:
                                        key = RedisContants.RISK_BLACKLIST_OVERDUE15_CALL_RECORD;
                                        break;
                                }
                                if (isRemoved != null && isRemoved.length > 0 && isRemoved[0] == true) {
                                    redisClient.setRemove(key, doitPhoneList);
                                } else {
                                    redisClient.setAdd(key, doitPhoneList);
                                }
                                totalSize = totalSize + doitPhoneList.size();
                            }
                        }

                    }
                }
            } catch (Exception e) {
                log.info("call record error with  startIndex: {}, endIndex: {}", startIndex, endIndex);
                log.error("error", e);
            }
        }

        log.info("call record totalSize: {}, from: {}", totalSize, fromDoit);

    }

    public void addSmsToRedis(List<UsrBlackList> userList, boolean fromDoit, UsrBlackList.BlackUserCategory category, Boolean... isRemoved) {
        if (CollectionUtils.isEmpty(userList)) {
            return;
        }
        int totalSize = 0;

        //分组查询，每次查询50个用户
        int maxLoop = (int) Math.ceil(userList.size() / 50.0);
        for (int i = 0; i < maxLoop; i++) {
            int startIndex = i * 50;
            int endIndex = (i + 1) * 50;
            if (endIndex >= userList.size()) {
                endIndex = userList.size();
            }
            List<String> userIdList = userList.subList(startIndex, endIndex).stream().map(elem -> elem.getUserUuid()).filter(elem->!StringUtils.isEmpty(elem)).distinct().collect(Collectors.toList());
            Query query = new Query();
            Criteria criteria = new Criteria("userUuid");
            criteria.in(userIdList);
            query.addCriteria(criteria);
            List<UserMessagesMongo> resultTmp = null;
            if (fromDoit) {
                resultTmp = mongoTemplate.find(query, UserMessagesMongo.class, "UserMessagesMongo");
            } else {
                resultTmp = mongoTemplate.find(query, UserMessagesMongo.class, "UUUserMessagesMongo");
            }
            if (!CollectionUtils.isEmpty(resultTmp)) {
                for (UserMessagesMongo mongo : resultTmp) {
                    String data = mongo.getData();
                    if (StringUtils.isEmpty(data)) {
                        continue;
                    }

                    List<ShortMessageData> tmpList = null;
                    try {
                        tmpList = JsonUtils.toList(data, ShortMessageData.class);
                    } catch (Exception e) {
                        log.info("sms error data: {} ,orderNo: {}", data, mongo.getOrderNo());
                        log.error("sms error", e);
                        continue;
                    }

                    if (!CollectionUtils.isEmpty(tmpList)) {
                        //doit中获得的手机号
                        List<String> doitSmsPhoneList =
                                tmpList.stream().map(elem -> {
                                    if (StringUtils.isEmpty(elem.getPhoneNumber())) {
                                        return null;
                                    }
                                    return CheakTeleUtils.telephoneNumberValid2(elem.getPhoneNumber());
                                }).filter(elem -> !StringUtils.isEmpty(elem)).distinct().collect(Collectors.toList());

                        //手机号保存到redis
                        if (!CollectionUtils.isEmpty(doitSmsPhoneList)) {
                            String key = "";
                            switch (category){
                                case OVERDUE7:
                                    key = RedisContants.RISK_BLACKLIST_OVERDUE7_SHORT_MESSAGE;
                                    break;
                                case FRAUD:
                                    key = RedisContants.RISK_BLACKLIST_FRAUD_SHORT_MESSAGE;
                                    break;
                                default:
                                    key = RedisContants.RISK_BLACKLIST_OVERDUE15_SHORT_MESSAGE;
                                    break;
                            }
                            if (isRemoved != null && isRemoved.length > 0 && isRemoved[0] == true) {
                                redisClient.setRemove(key, doitSmsPhoneList);
                            } else {
                                redisClient.setAdd(key, doitSmsPhoneList);
                            }

                            totalSize = totalSize + doitSmsPhoneList.size();
                        }
                    }

                }
            }
        }

        log.info("sms totalSize: {}, from: {}", totalSize, fromDoit);
    }

    public static void add(List<UsrBlackList> userList, boolean fromDoit, UsrBlackList.BlackUserCategory category,
                           Boolean... isRemoved) {
        if (isRemoved != null && isRemoved.length > 0 && isRemoved[0] == true) {
            System.err.println("remove");
        } else {
            System.err.println("add");
        }
    }

    public static void main(String[] args) {
        add(null, true, null);
    }

    @Getter
    @Setter
    public static class ContactData {
        private String name;
        private String phone;
    }


    @Getter
    @Setter
    public static class ShortMessageData {
        private String phoneNumber;

    }


    @Getter
    @Setter
    public static class UserCallRecordsData {
        private String date;//日期
        private String number;//号码
        private String name;//姓名
        private Integer duration;//时长
        private String type;//类型（1=呼入，2=呼出）
    }

    /***
     * 每日将当日逾期7天转到逾期15天的用户数据缓存到redis中
     */
    public void initRedisCachePerday() {
        //查询updateTime<>createTime and updateTime=当天，type=
    }


    public void test() {
        try {
            // initOverdue15BlackListPhones();
            List<String> phones = Arrays.asList("Y3UqgFp+pEvt9JxflDzj9Q==",
                    "99Vc0v2QFFV7LVLMP27mzw==",
                    "54GDqQnuThK+TEUaq+QOHA==",
                    "fCAPCEXWTuVrl6nxagOMiQ==",
                    "POaeMw/GFAe3gUkWeFN3+w==",
                    "WB4HXi7l+7NCvFX+qUjDCA==",
                    "+mbwNkaLtjo5A9AAaiD4gw==",
                    "N9Bb44sWYLZVAjIdzR5OPQ==",
                    "mC+j0An1naSQ5qqXecx5iA==",
                    "PFQcwAHfIsgzvwMZnJlO8w==",
                    "JFUjrdg2SUpzQKBE6XM0fw==");

//            for (String phone : phones) {
//                log.info("contact phone result: {}",isMobileInContactList(phone));
//                log.info("call record phone result: {}",isMobileInCallRecordList(phone));
//                log.info("sms phone result: {}",isMobileInSmsList(phone));
//            }
        } catch (Exception e) {
            log.info("error", e);
        }
    }


}
