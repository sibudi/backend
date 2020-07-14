package com.yqg.service.risk.service;

import java.util.Arrays;
import java.util.List;

import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.MD5Util;
import com.yqg.service.user.service.UserDetailService;
import com.yqg.user.dao.UsrBlackListDao;
import com.yqg.user.entity.UsrBlackList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
            addContactToRedis(blackLists, true, UsrBlackList.BlackUserCategory.OVERDUE15);
            addSmsToRedis(blackLists, true, UsrBlackList.BlackUserCategory.OVERDUE15);

            //Redis removed 7 days to 15 days of data
            addContactToRedis(blackLists, true, UsrBlackList.BlackUserCategory.OVERDUE15, true);
            addSmsToRedis(blackLists, true, UsrBlackList.BlackUserCategory.OVERDUE15, true);

            //保存7天的数据
            blackLists = usrBlackListDao.getCurrentDayOverdue7();
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
        //ahalim remove usercontact
        //ahalim: TODO remove unused method
        return;
    }

    public void addSmsToRedis(List<UsrBlackList> userList, boolean fromDoit, UsrBlackList.BlackUserCategory category, Boolean... isRemoved) {
        //ahalim remove usermessage
        //ahalim: TODO remove unused method
        return;
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

        } catch (Exception e) {
            log.info("error", e);
        }
    }


}
