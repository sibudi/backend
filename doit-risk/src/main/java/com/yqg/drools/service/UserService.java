package com.yqg.drools.service;

import com.yqg.base.data.condition.BaseEntity;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.enums.user.UsrAttachmentEnum;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrderCheckService;
import com.yqg.service.user.service.*;
import com.yqg.service.util.RuleConstants;
import com.yqg.user.dao.*;
import com.yqg.user.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 ****/

@Service
@Slf4j
public class UserService {

    @Autowired
    private UsrService usrService;

    @Autowired
    private UsrAddressDetailDao usrAddressDetailDao;

    @Autowired
    private UsrStudentDetailDao usrStudentDetailDao;

    @Autowired
    private UsrWorkDetailDao usrWorkDetailDao;

    @Autowired
    private UsrDao usrDao;

    @Autowired
    private OrdDao ordDao;

    @Autowired
    private UsrLinkManDao usrLinkManDao;

    @Autowired
    private UsrAttachmentInfoDao usrAttachmentInfoDao;

    @Autowired
    private UsrHouseWifeDetailDao usrHouseWifeDetailDao;
    @Autowired
    private UsrBankService usrBankService;

    @Autowired
    private UsrCertificationService usrCertificationService;
    @Autowired
    private UserBackupLinkmanService userBackupLinkmanService;
    @Autowired
    private UserLinkManService userLinkManService;
    @Autowired
    private UserDetailService userDetailService;
    @Autowired
    private OrderCheckService orderCheckService;


    public UsrUser getUserInfo(String userUuid) {
        return usrService.getUserByUuid(userUuid);
    }

    public UsrAddressDetail getUserAddressDetailByType(String userUUID, UsrAddressEnum type) {
        UsrAddressDetail searchInfo = new UsrAddressDetail();
        searchInfo.setUserUuid(userUUID);
        searchInfo.setAddressType(type.getType());
        List<UsrAddressDetail> usrAddressDetails = usrAddressDetailDao.scan(searchInfo);
        if (CollectionUtils.isEmpty(usrAddressDetails)) {
            return null;
        }
        return usrAddressDetails.get(0);
    }


    public Map<String, String> getOrderUserInfo(String userUUID) {
        UsrUser user = this.getUserInfo(userUUID);
        Map<String, String> userMap = new HashMap<>();
        userMap.put("mobileNumber", user.getMobileNumberDES());
        if (user.getUserRole() == 1) {// 学生
            UsrStudentDetail usrStudentDetail = new UsrStudentDetail();
            usrStudentDetail.setUserUuid(userUUID);
            List<UsrStudentDetail> usrStudentDetails = usrStudentDetailDao.scan(usrStudentDetail);
            if (!CollectionUtils.isEmpty(usrStudentDetails)) {
                UsrStudentDetail usrStudentDetailObj = usrStudentDetails.get(0);
                userMap.put("birthday", usrStudentDetailObj.getBirthday() == null ? ""
                        : usrStudentDetailObj.getBirthday());
                userMap.put("email",
                        usrStudentDetailObj.getEmail() == null ? "" : usrStudentDetailObj.getEmail());
            }
        } else if (user.getUserRole() == 2) {// 工作
            UsrWorkDetail usrWorkDetailObj = this.getUserWorkDetail(userUUID);
            if (usrWorkDetailObj != null) {
                userMap.put("birthday",
                        usrWorkDetailObj.getBirthday() == null ? "" : usrWorkDetailObj.getBirthday());
                userMap.put("email",
                        usrWorkDetailObj.getEmail() == null ? "" : usrWorkDetailObj.getEmail());
                userMap.put("monthlyIncome", usrWorkDetailObj.getReplaceMonthlyIncome() == null ? ""
                        : usrWorkDetailObj.getReplaceMonthlyIncome());
            }
        } else if (user.getUserRole() == 3) {
            //家庭主妇
            UsrHouseWifeDetail usrHouseWifeDetailObj = this.getUserHouseWifeDetail(userUUID);
            if (usrHouseWifeDetailObj != null) {
                userMap.put("birthday",
                        usrHouseWifeDetailObj.getBirthday() == null ? "" : usrHouseWifeDetailObj.getBirthday());
                userMap.put("email",
                        usrHouseWifeDetailObj.getEmail() == null ? "" : usrHouseWifeDetailObj.getEmail());
                String income = StringUtils.isEmpty(usrHouseWifeDetailObj.getMouthIncome()) ? "" : usrHouseWifeDetailObj.getMouthIncome().replaceAll
                        ("\\.", "");
                if (com.yqg.common.utils.StringUtils.isNotEmpty(income)) {
                    income = income.replaceAll("\\u202D", "");
                    income = income.replaceAll("\\u202d", "");
                }
                userMap.put("monthlyIncome", StringUtils.isEmpty(income) ? "" : income);
            }
        }
        return userMap;
    }

    public UsrWorkDetail getUserWorkDetail(String userUUID) {
        return userDetailService.getUserWorkDetail(userUUID);
    }

    public UsrStudentDetail getUserStudentDetail(String userUUID) {
        return userDetailService.getUserStudentDetail(userUUID);
    }


    public UsrHouseWifeDetail getUserHouseWifeDetail(String userUUID) {
       return userDetailService.getUserHouseWifeDetail(userUUID);
    }

    /***
     *
     * @param phones
     * @return
     */
    private List<UsrUser> getUserByMobileList(List<String> phones) {
        //手机号过滤+62/0 等手机号前缀
        if (CollectionUtils.isEmpty(phones)) {
            return null;
        }

        List<String> validPhones = phones.stream()
                .map(elem -> CheakTeleUtils.telephoneNumberValid2(elem)).distinct()
                .filter(elem -> !StringUtils.isEmpty(elem)).collect(
                        Collectors.toList());
        if (CollectionUtils.isEmpty(validPhones)) {
            return null;
        }

        //手机号加密
        List<String> encryptedPhones = validPhones.stream().map(elem -> DESUtils.encrypt(elem)).collect(
                Collectors.toList());

        //根据手机号查询用户
        return usrDao.getUsersByDesMobileList(encryptedPhones);
    }

    /***
     * phones的所有用户的订单中，逾期超过minOverdueDays的订单
     * @param phones 待查手机号
     * @param minOverdueDays 逾期天数最小值
     * @return
     */
    public List<OrdOrder> getOverdueOrdersByMobilesAndOverdueDays(List<String> phones,
                                                                  Integer minOverdueDays) {
        List<UsrUser> users = getUserByMobileList(phones);
        if (CollectionUtils.isEmpty(users)) {
            return null;
        }
        List<String> userUUIDList = users.stream().map(UsrUser::getUuid)
                .collect(Collectors.toList());
        //根据uuidlist查找用户订单是否逾期超过minOverdueDays的用户
        List<OrdOrder> overdueDaysMoreThan15List = ordDao
                .getOrdersByMinOverdueDaysAndUUIDS(minOverdueDays, userUUIDList);
        return overdueDaysMoreThan15List;
    }


    /***
     * 查询联系人号码
     * @param type
     * @param userUUID
     * @return
     */
    public String getUserLinkManPhoneNumber(int type, String userUUID) {

        //联系人是否在通讯录
        UsrLinkManInfo search = new UsrLinkManInfo();
        search.setUserUuid(userUUID);
        search.setSequence(type);
        search.setDisabled(0);
        List<UsrLinkManInfo> infoList = this.usrLinkManDao.scan(search);
        if (CollectionUtils.isEmpty(infoList)) {
            return null;
        }
        if (!CollectionUtils.isEmpty(infoList)) {
            String phone = infoList.get(0).getContactsMobile();
            phone = CheakTeleUtils.telephoneNumberValid2(phone);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(phone)) {
                return phone;
            }
        }
        return null;
    }

    private List<String> getLinkmanFormatNumberList(String userUuid) {
        if (StringUtils.isEmpty(userUuid)) {
            return null;
        }
        UsrLinkManInfo search = new UsrLinkManInfo();
        search.setUserUuid(userUuid);
        search.setDisabled(0);
        List<UsrLinkManInfo> infoList = this.usrLinkManDao.scan(search);
        if (CollectionUtils.isEmpty(infoList)) {
            return null;
        }
        List<String> formatNumbers =
                infoList.stream().map(elem -> elem.getFormatMobile()).filter(elem -> StringUtils.isEmpty(elem)).collect(Collectors.toList());
        return formatNumbers;
    }

    /**
     * Currently this method is only used to check attachment existannce
     * Called by:
     * 1. IdentityExtractor.java
     * 2. LoanLimitDecreaseExtractor.java
     * 3. ReborrowingIdentityExtractor.java
     */
    public UsrAttachmentInfo getAttachmentInfo(UsrAttachmentEnum attachmentType,String userUUID){
        UsrAttachmentInfo searchInfo = new UsrAttachmentInfo();
        searchInfo.setUserUuid(userUUID);
        searchInfo.setDisabled(0);
        searchInfo.setAttachmentType(attachmentType.getType());
        List<UsrAttachmentInfo> resultList = this.usrAttachmentInfoDao.scan(searchInfo);
        if (CollectionUtils.isEmpty(resultList)) {
            return null;
        } else {
            return resultList.get(0);
        }
    }



    /***
     * 手机号作为紧急联系人的次数
     * @param mobileDes
     * @return
     */
    public Integer getEmergencyTelCountWithMobiles(String mobileDes) {
        if (StringUtils.isEmpty(mobileDes)) {
            return 0;
        }
        String mobileNumber = DESUtils.decrypt(mobileDes);
        return usrLinkManDao.getEmergencyTelCountWithMobiles(getAllTypesMobile(mobileNumber));
    }



    /***
     *
     * 根据注册的手机号反推可能的手机号码格式
     * @return
     */
    public List<String> getAllTypesMobile(String mobileNumber) {
        Set<String> hashSet = new HashSet<>();
        hashSet.add(mobileNumber);
        hashSet.add("0" + mobileNumber);
        hashSet.add("62" + mobileNumber);
        hashSet.add("+62" + mobileNumber);
        return new ArrayList<>(hashSet);
    }


    /***
     * 获取第一联系人相同的首个用户的userId
     * @param userUuid
     * @return
     */
    public String getTheFirstUserWithSameFirstLinkman(String userUuid) {
        String mobile = this.getUserLinkManPhoneNumber(1, userUuid);
        if (StringUtils.isEmpty(mobile)) {
            return null;
        }
        String relatedUserId = usrLinkManDao.getTheFirstUserIdWithSameFirstEmergencyTel(userUuid, mobile);
        return relatedUserId;

    }


    /**
     * 用户首借是否是600的还款用户
     * @param relatedUserId
     * @return
     */
    public Boolean isUserFirstOrderSettledWith600(String relatedUserId) {
        if (StringUtils.isEmpty(relatedUserId)) {
            return false;
        }
        //判断用户是否是首借600成功还款用户
        Optional<OrdOrder> firstOrder = orderCheckService.getFirstSettledOrder(relatedUserId);
        if (firstOrder.isPresent()) {
            return firstOrder.get().getAmountApply().compareTo(RuleConstants.PRODUCT600) == 0;
        }
        return false;
    }

    /***
     * 首个相同联系人的用户
     * @param userUuid
     * @return
     */
    public String getTheFirstUserWithSameLinkman(String userUuid) {
        List<String> formatNumbers = getLinkmanFormatNumberList(userUuid);
        if (CollectionUtils.isEmpty(formatNumbers)) {
            return null;
        }
        String relatedUserId = usrLinkManDao.getTheFirstUserIdWithSameEmergencyTel(userUuid, formatNumbers);
        return relatedUserId;
    }

    /***
     * 用户是否有成功的借款订单
     * @param userUuid
     * @return
     */
    public Boolean isUserWithoutSuccessOrder(String userUuid) {
        Long count = orderCheckService.successLoanCount(userUuid);
        return 0 == count;
    }

    /***
     * 获取eamil相同的首个用户的userId
     * @param user
     * @return
     */
    public String getFirstEmailExistsUser(UsrUser user) {
        String email = getUserEmail(user);
        if (StringUtils.isEmpty(email)) {
            return null;
        }
        UsrStudentDetail hitInStudent = usrStudentDetailDao.getFirstEmailExistsUser(user.getUuid(), email);
        UsrWorkDetail hitInWork = usrWorkDetailDao.getFirstEmailExistsUser(user.getUuid(), email);
        UsrHouseWifeDetail hitInHouseWife = usrHouseWifeDetailDao.getFirstEmailExistsUser(user.getUuid(), email);

        //查询最早的一个用户Id
        List<BaseEntity> entities = Arrays.asList(hitInStudent, hitInWork, hitInHouseWife);
        Optional<BaseEntity> entity = entities.stream().filter(elem -> elem != null).min(Comparator.comparing(BaseEntity::getCreateTime));
        if (!entity.isPresent()) {
            return null;
        }
        String relatedUserId = null;
        if (entity.get() instanceof UsrWorkDetail) {
            relatedUserId = ((UsrWorkDetail) entity.get()).getUserUuid();
        } else if (entity.get() instanceof UsrHouseWifeDetail) {
            relatedUserId = ((UsrHouseWifeDetail) entity.get()).getUserUuid();
        } else if (entity.get() instanceof UsrStudentDetail) {
            relatedUserId = ((UsrStudentDetail) entity.get()).getUserUuid();
        }
        return relatedUserId;
    }

    public Boolean isEmailExists(UsrUser user) {
        String email = getUserEmail(user);
        if (StringUtils.isEmpty(email)) {
            return false;
        }
        UsrStudentDetail hitInStudent = usrStudentDetailDao.getFirstEmailExistsUser(user.getUuid(), email);
        UsrWorkDetail hitInWork = usrWorkDetailDao.getFirstEmailExistsUser(user.getUuid(), email);
        UsrHouseWifeDetail hitInHouseWife = usrHouseWifeDetailDao.getFirstEmailExistsUser(user.getUuid(), email);
        if (hitInStudent == null && hitInWork == null && hitInHouseWife == null) {
            return false;
        } else {
            return true;
        }
    }

    private String getUserEmail(UsrUser user){
        String email = "";
        if (1 == user.getUserRole()) {
            //学生
            UsrStudentDetail detail = getUserStudentDetail(user.getUuid());
            if (detail == null || StringUtils.isEmpty(detail.getEmail())) {
                return null;
            }
            email = detail.getEmail();
        } else if (2 == user.getUserRole()) {
            //工作人员
            UsrWorkDetail detail = getUserWorkDetail(user.getUuid());
            if (detail == null || StringUtils.isEmpty(detail.getEmail())) {
                return null;
            }
            email = detail.getEmail();

        } else if (3 == user.getUserRole()) {
            //家庭主妇
            UsrHouseWifeDetail detail = getUserHouseWifeDetail(user.getUuid());
            if (detail == null || StringUtils.isEmpty(detail.getEmail())) {
                return null;
            }
            email = detail.getEmail();

        }
        return email;
    }
    public Boolean existsSameSmallDirectAndDetail(String userUuid, String smallDirect, String detail, UsrAddressEnum addressEnum) {
        Integer count = usrAddressDetailDao.countOfSameSmallDirectAndDetail(userUuid, smallDirect, detail, addressEnum.getType());
        return count != null && count > 0;
    }


    public String getBorrowingPurpose(String userUuid) {
        UsrUser user = this.getUserInfo(userUuid);
        if (user.getUserRole() == 1) {// 学生
            UsrStudentDetail usrStudentDetail = new UsrStudentDetail();
            usrStudentDetail.setUserUuid(userUuid);
            usrStudentDetail.setDisabled(0);
            List<UsrStudentDetail> usrStudentDetails = usrStudentDetailDao.scan(usrStudentDetail);
            if (!CollectionUtils.isEmpty(usrStudentDetails)) {
                UsrStudentDetail usrStudentDetailObj = usrStudentDetails.get(0);
                return usrStudentDetailObj.getBorrowUse();
            }
        } else if (user.getUserRole() == 2) {// 工作
            UsrWorkDetail usrWorkDetailObj = this.getUserWorkDetail(userUuid);
            if (usrWorkDetailObj != null) {
                return usrWorkDetailObj.getBorrowUse();
            }
        } else if (user.getUserRole() == 3) {
            //家庭主妇
            UsrHouseWifeDetail usrHouseWifeDetailObj = this.getUserHouseWifeDetail(userUuid);
            if (usrHouseWifeDetailObj != null) {
                return usrHouseWifeDetailObj.getBorrowUse();
            }
        }
        return null;
    }

    public Integer countOfOverdueLessThanNUsersByLinkman(String userUuid, Integer days) {
        List<UsrLinkManInfo> linkmans = userBackupLinkmanService.getLinkManInfo(userUuid);
        if (CollectionUtils.isEmpty(linkmans)) {
            return 0;
        }

        List<String> linkmanMobiles = linkmans.stream().map(elem -> {
            String mobile = elem.getContactsMobile();
            String formatMobile = CheakTeleUtils.telephoneNumberValid2(mobile);
            if (!StringUtils.isEmpty(formatMobile)) {
                return DESUtils.encrypt(formatMobile);
            }
            return null;
        }).filter(elem -> !StringUtils.isEmpty(elem)).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(linkmanMobiles)) {
            return 0;
        }
        return usrDao.countOfOverdueLessThan5UsersByMobiles(linkmanMobiles, days);
    }

    public Integer countemergeInIQorGood600(OrdOrder ordOrder) {

        UsrUser usrUser = new UsrUser();
        usrUser.setDisabled(0);
        usrUser.setUuid(ordOrder.getUserUuid());
        List<UsrUser> usrUsers = usrDao.scan(usrUser);
        if (CollectionUtils.isEmpty(usrUsers)) {
            return 0;
        }

        String mobile = usrUsers.get(0).getMobileNumberDES();
        mobile = "62" + CheakTeleUtils.telephoneNumberValid2(DESUtils.decrypt(mobile));

        return usrDao.countemergeInIQorGood600(mobile);
    }


    public Boolean certificationVerified(String userUuid, CertificationEnum certificationEnum) {
        try {
            UsrCertificationInfo gojekCertification = usrCertificationService.getCertificationByUserUuidAndType(userUuid,
                    certificationEnum.getType());
            return gojekCertification != null;
        } catch (Exception e) {
            log.error("get gojek certification error", e);
        }
        return null;
    }


    public Boolean isLinkmanInMultiBankcardUser(String userUuid) {
        List<UsrLinkManInfo> linkManInfos = userBackupLinkmanService.getLinkManInfo(userUuid);
        if (CollectionUtils.isEmpty(linkManInfos)) {
            return false;
        }
        List<String> numberes = linkManInfos.stream().map(elem -> {
            String tel = CheakTeleUtils.telephoneNumberValid2(elem.getContactsMobile());
            if (StringUtils.isEmpty(tel)) {
                return "";
            }
            return DESUtils.encrypt(tel);
        }).filter(elem -> !StringUtils.isEmpty(elem)).collect(Collectors.toList());

        if (StringUtils.isEmpty(numberes)) {
            return false;
        }
        //查询联系人号码命中的注册用户
        List<UsrUser> hitUserList = usrDao.getUsersByDesMobileList(numberes);
        //判断这些用户是否是多卡用户
        if (CollectionUtils.isEmpty(hitUserList)) {
            return false;
        }

        for (UsrUser userUser : hitUserList) {
            boolean isMultiBankcardUser = usrBankService.userInMultiBankcardUser(userUser.getUuid());
            if (isMultiBankcardUser) {
                return true;
            }
        }
        return false;
    }

    /***
     * 手机号是否是 “多个相同卡号人不同的用户群”的用户紧急联系人
     * @param formatMobile
     * @return
     */
    public Boolean isMobileInMultiBankcardUserEmergencyTel(String formatMobile) {
        if (StringUtils.isEmpty(formatMobile)) {
            return false;
        }
        //用户号码是否是紧急联系人号码
        List<UsrLinkManInfo> linkmanList = userLinkManService.getLinkmanByFormatMobile(formatMobile);
        if (CollectionUtils.isEmpty(linkmanList)) {
            return false;
        }
        //紧急联系人对应的用户是否是同卡用户
        for (UsrLinkManInfo linkManInfo : linkmanList) {
            boolean isMultiBankcardUser = usrBankService.userInMultiBankcardUser(linkManInfo.getUserUuid());
            if (isMultiBankcardUser) {
                return true;
            }
        }
        return false;

    }


    public Boolean isMobileInUpLoanLimitUser(String mobileDesc) {
        Integer count = usrDao.mobileExistInUpLoanLimitInfo(mobileDesc);
        return count != null && count > 0;
    }
    public Boolean isMobileInFirstBorrow600NotOverdueUser(String mobileDesc) {
        Integer count = usrDao.mobileExistInFirstBorrow600NotOverdueUser(mobileDesc);
        return count != null && count > 0;
    }


    /***
     * 手机号是否是首次600借款逾期小于5天的用户的紧急联系人
     * @param mobileDesc
     * @return
     */
    public Boolean isEmergencyTelForOverdueLessThanNUsers(String mobileDesc, int nDays) {
        String mobile = DESUtils.decrypt(mobileDesc);

        //1、是紧急联系人号码
        List<UsrLinkManInfo> linkmanList = userLinkManService.getLinkmanByFormatMobile(mobile);
        if (CollectionUtils.isEmpty(linkmanList)) {
            return false;
        }
        //用户在逾期小于n天的用户中
        List<String> userIdList = linkmanList.stream().map(elem -> elem.getUserUuid()).collect(Collectors.toList());

        Integer count = usrDao.countOfOverdueLessThanNUsersByUserIds(userIdList, nDays);
        return count != null && count > 0;
    }





}
