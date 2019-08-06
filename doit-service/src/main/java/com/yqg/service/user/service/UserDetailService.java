
package com.yqg.service.user.service;

import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.UsrAddressEnum;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.user.dao.*;
import com.yqg.user.entity.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserDetailService {

    @Autowired
    private UsrWorkDetailDao usrWorkDetailDao;
    @Autowired
    private UsrHouseWifeDetailDao usrHouseWifeDetailDao;
    @Autowired
    private UsrDao usrDao;
    @Autowired
    private UsrAddressDetailDao usrAddressDetailDao;
    @Autowired
    private UsrStudentDetailDao usrStudentDetailDao;
    @Autowired
    private UsrCertificationService usrCertificationService;

    public String getCompanyPhone(String userUuid) {
        if (StringUtils.isEmpty(userUuid)) {
            return null;
        }
        UsrUser searchParam = new UsrUser();
        searchParam.setUuid(userUuid);
        searchParam.setDisabled(0);
        List<UsrUser> userList = usrDao.scan(searchParam);
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        Integer userRole = userList.get(0).getUserRole();
        if (userRole == null || userRole == 1) {
            //学生or userRole为空
        }
        if (userRole == 2) {
            //工作人员
            UsrWorkDetail workDetailSearchParam = new UsrWorkDetail();
            workDetailSearchParam.setUserUuid(userUuid);
            workDetailSearchParam.setDisabled(0);
            List<UsrWorkDetail> workDetails = usrWorkDetailDao.scan(workDetailSearchParam);
            if (CollectionUtils.isEmpty(workDetails)) {
                return null;
            }
            return workDetails.get(0).getCompanyPhone();
        }

        if (userRole == 3) {
            //家庭主妇
            UsrHouseWifeDetail houseWifeDetailSearchParam = new UsrHouseWifeDetail();
            houseWifeDetailSearchParam.setUserUuid(userUuid);
            houseWifeDetailSearchParam.setDisabled(0);
            List<UsrHouseWifeDetail> houseWifeDetails = usrHouseWifeDetailDao.scan(houseWifeDetailSearchParam);
            if (CollectionUtils.isEmpty(houseWifeDetails)) {
                return null;
            }
            return houseWifeDetails.get(0).getCompanyPhone();
        }

        return null;
    }

    public String getAddressUpperCaseWithSeparator(String userUuid, UsrAddressEnum addressEnum) {
        if (StringUtils.isEmpty(userUuid)) {
            return null;
        }
        List<UsrAddressDetail> dbList = getUserAddressList(userUuid,addressEnum);
        if (CollectionUtils.isEmpty(dbList)) {
            return null;
        }
        UsrAddressDetail addressEntity = dbList.get(0);
        if (StringUtils.isEmpty(addressEntity.getDetailed())) {
            return null;
        }
        String detail = "";
        if (!StringUtils.isEmpty(addressEntity.getProvince())) {
            detail += addressEntity.getProvince();
        }
        if (!StringUtils.isEmpty(addressEntity.getCity())) {
            detail += addressEntity.getCity();
        }
        if (!StringUtils.isEmpty(addressEntity.getBigDirect())) {
            detail += addressEntity.getBigDirect();
        }
        if (!StringUtils.isEmpty(addressEntity.getSmallDirect())) {
            detail += addressEntity.getSmallDirect();
        }
        if (!StringUtils.isEmpty(addressEntity.getDetailed())) {
            detail += addressEntity.getDetailed();
        }

        if (StringUtils.isEmpty(detail)) {
            return null;
        }
        return detail.toUpperCase();
    }


    public String getWhatsAppAccount(String userUuid, boolean flag) {
        if (org.springframework.util.StringUtils.isEmpty(userUuid)) {
            return null;
        }
        List<UsrCertificationInfo> dbList = usrCertificationService.getCertificationList(userUuid);
        if (CollectionUtils.isEmpty(dbList)) {
            return null;
        }

        Optional<UsrCertificationInfo> certificationInfoFroWhatsapp =
                dbList.stream().filter(elem -> elem.getCertificationType() == CertificationEnum.WHATS_APP.getType()).findFirst();
        if (!certificationInfoFroWhatsapp.isPresent()) {
            return null;
        }
        String whatsappData = certificationInfoFroWhatsapp.get().getCertificationData();
        if (org.springframework.util.StringUtils.isEmpty(whatsappData)) {
            return null;
        }
        Map<String, Object> dataMap = JsonUtils.toMap(whatsappData);
        if (dataMap != null) {
            String account = dataMap.get("account") != null ? dataMap.get("account").toString() : null;
            if (flag) {
                return account;
            }
            account = org.springframework.util.StringUtils.isEmpty(account) ? null : CheakTeleUtils.telephoneNumberValid2(account);
            return account;
        }
        return null;
    }

    public List<UsrAddressDetail> getUserAddressList(String userUuid, UsrAddressEnum... addressType) {
        UsrAddressDetail addressSearchParam = new UsrAddressDetail();
        addressSearchParam.setUserUuid(userUuid);
        addressSearchParam.setDisabled(0);
        if (addressType != null && addressType.length>=1) {
            addressSearchParam.setAddressType(addressType[0].getType());
        }
        List<UsrAddressDetail> dbList = usrAddressDetailDao.scan(addressSearchParam);
        return dbList;
    }


    public UsrWorkDetail getUserWorkDetail(String userUUID) {
        UsrWorkDetail usrWorkDetail = new UsrWorkDetail();
        usrWorkDetail.setUserUuid(userUUID);
        usrWorkDetail.setDisabled(0);
        List<UsrWorkDetail> usrWorkDetails = usrWorkDetailDao.scan(usrWorkDetail);
        if (CollectionUtils.isEmpty(usrWorkDetails)) {
            return null;
        }
        return usrWorkDetails.get(0);
    }

    public UsrStudentDetail getUserStudentDetail(String userUUID) {
        UsrStudentDetail usrStudentDetail = new UsrStudentDetail();
        usrStudentDetail.setUserUuid(userUUID);
        usrStudentDetail.setDisabled(0);
        List<UsrStudentDetail> usrStudentDetails = usrStudentDetailDao.scan(usrStudentDetail);
        if (CollectionUtils.isEmpty(usrStudentDetails)) {
            return null;
        }
        return usrStudentDetails.get(0);
    }


    public UsrHouseWifeDetail getUserHouseWifeDetail(String userUUID) {
        UsrHouseWifeDetail usrHouseWifeDetail = new UsrHouseWifeDetail();
        usrHouseWifeDetail.setUserUuid(userUUID);
        usrHouseWifeDetail.setDisabled(0);
        List<UsrHouseWifeDetail> usrHouseWifeDetails = usrHouseWifeDetailDao.scan(usrHouseWifeDetail);
        if (CollectionUtils.isEmpty(usrHouseWifeDetails)) {
            return null;
        }
        return usrHouseWifeDetails.get(0);
    }


    public UserDetailInfo getUserDetailInfo(UsrUser usrUser) {
        UserDetailInfo userDetailInfo = new UserDetailInfo();
        if (usrUser.getUserRole() == 2) {
            //已经工作
            UsrWorkDetail usrWorkDetail = getUserWorkDetail(usrUser.getUuid());
            if(usrWorkDetail==null){
                return userDetailInfo;
            }
            String birthday = usrWorkDetail.getBirthday();

            userDetailInfo.setBirthday(StringUtils.isNotEmpty(birthday) ? birthday.replaceAll("/", "-") : "");
            userDetailInfo.setCompanyName(usrWorkDetail.getCompanyName());
            userDetailInfo.setEmail(usrWorkDetail.getEmail());
            userDetailInfo.setReligionName(usrWorkDetail.getReligion());
            userDetailInfo.setMotherName(usrWorkDetail.getMotherName());
            userDetailInfo.setAcademic(usrWorkDetail.getAcademic());
            userDetailInfo.setMarriageStatus(String.valueOf(usrWorkDetail.getMaritalStatus()));
        } else if (usrUser.getUserRole() == 3) {
            //家庭主妇
            UsrHouseWifeDetail usrHouseWifeDetail = getUserHouseWifeDetail(usrUser.getUuid());
            String birthday = usrHouseWifeDetail.getBirthday();
            userDetailInfo.setBirthday(StringUtils.isNotEmpty(birthday) ? birthday.replaceAll("/", "-") : "");
            userDetailInfo.setCompanyName(usrHouseWifeDetail.getCompanyName());
            userDetailInfo.setEmail(usrHouseWifeDetail.getEmail());
            userDetailInfo.setReligionName(usrHouseWifeDetail.getReligion());
            userDetailInfo.setMotherName(usrHouseWifeDetail.getMotherName());
            userDetailInfo.setAcademic(usrHouseWifeDetail.getAcademic());
            userDetailInfo.setMarriageStatus(String.valueOf(usrHouseWifeDetail.getMaritalStatus()));
        } else if (usrUser.getUserRole() == 1) {
            //学生
            UsrStudentDetail usrStudentDetail = getUserStudentDetail(usrUser.getUuid());
            String birthday = usrStudentDetail.getBirthday();
            userDetailInfo.setBirthday(StringUtils.isNotEmpty(birthday) ? birthday.replaceAll("/", "-") : "");
            userDetailInfo.setEmail(usrStudentDetail.getEmail());
            userDetailInfo.setMotherName(usrStudentDetail.getMotherName());
            userDetailInfo.setAcademic(usrStudentDetail.getAcademic());
        }
        return userDetailInfo;
    }


    @Getter
    @Setter
    public static class UserDetailInfo {
        private String religionName;
        private String companyName;
        private String birthday;  //dd-mm-yyyy
        private String email;
        private String motherName;

        private String marriageStatus;
        private String academic;
    }


}