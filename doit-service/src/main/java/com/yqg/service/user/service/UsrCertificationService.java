package com.yqg.service.user.service;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.CertificationResultEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.user.dao.UsrCertificationInfoDao;
import com.yqg.user.entity.UsrCertificationInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Didit Dwianto on 2017/11/28.
 */
@Service
@Slf4j
public class UsrCertificationService {
    @Autowired
    private UsrCertificationInfoDao usrCertificationInfoDao;

    /**
     *??userUuid????? ?? ????
     * @return
     */
    public UsrCertificationInfo getCertificationByUserUuidAndType(String userUuid, int certificationType) throws ServiceException{
        UsrCertificationInfo usrCertificationInfo = new UsrCertificationInfo();
        usrCertificationInfo.setUserUuid(userUuid);
        usrCertificationInfo.setDisabled(0);
        usrCertificationInfo.setCertificationType(certificationType);
        List<UsrCertificationInfo> usrCertificationInfoList = usrCertificationInfoDao.scan(usrCertificationInfo);
        if (usrCertificationInfoList.isEmpty()){
            return null;
        }
        usrCertificationInfo = usrCertificationInfoList.get(0);
        return usrCertificationInfo;
    }

    /**
     *??userUuid????? ?? ??????
     * @return
     */
    public void getSuccessCertificationByUserUuidAndType(String userUuid, int certificationType) throws ServiceException{
        UsrCertificationInfo usrCertificationInfo = new UsrCertificationInfo();
        usrCertificationInfo.setUserUuid(userUuid);
        usrCertificationInfo.setDisabled(0);
        usrCertificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
        usrCertificationInfo.setCertificationType(certificationType);
        List<UsrCertificationInfo> usrCertificationInfoList = usrCertificationInfoDao.scan(usrCertificationInfo);
        if (usrCertificationInfoList.isEmpty()){
            log.error("???????");
            throw new ServiceException(ExceptionEnum.USER_NO_VIFIFY);
        }
    }

    /**
     *??userUuid ?? ??????
     * @return
     */
    public List<UsrCertificationInfo> getCertificationByUserUuid(String userUuid){
        UsrCertificationInfo usrCertificationInfo = new UsrCertificationInfo();
        usrCertificationInfo.setUserUuid(userUuid);
        usrCertificationInfo.setDisabled(0);
        List<UsrCertificationInfo> usrCertificationInfoList = usrCertificationInfoDao.scan(usrCertificationInfo);
        return usrCertificationInfoList;
    }

    /**
     * ??userUuid????? ?? ????
     * @param userUuid
     * @param certificationTypeList
     * @param resultEnum
     */
    public void updateCertificationByUserUuidAndType(String userUuid, String[] certificationTypeList,CertificationResultEnum resultEnum) throws ServiceException{
        for (String certificationEnum :certificationTypeList){
            UsrCertificationInfo certificationInfo = this.getCertificationByUserUuidAndType(userUuid,Integer.parseInt(certificationEnum));
            if (certificationInfo != null){
                certificationInfo.setCertificationResult(resultEnum.getType());
                this.usrCertificationInfoDao.update(certificationInfo);
            }
        }
    }


    public String getTaxNumber(String userUuid) {
        UsrCertificationInfo searchParam = new UsrCertificationInfo();
        searchParam.setCertificationType(CertificationEnum.STEUERKARTED.getType());
        searchParam.setUserUuid(userUuid);
        searchParam.setDisabled(0);
        List<UsrCertificationInfo> scanList = usrCertificationInfoDao.scan(searchParam);
        if (CollectionUtils.isEmpty(scanList)) {
            log.info("the certificationData is empty");
            return null;
        }
        if (StringUtils.isEmpty(scanList.get(0).getCertificationData())) {
            log.info("the certificationData for taxNumber is empty");
            return null;
        }
        Map data = JsonUtils.toMap(scanList.get(0).getCertificationData());
        if (data == null || data.get("account") == null || StringUtils.isEmpty(data.get("account").toString())) {
            log.info("the taxNumber is empty");
            return null;
        }

        String accountNumber = data.get("account").toString();
        accountNumber = accountNumber.replace("\"", "");
        return accountNumber;
    }


    /***
     * 查询成功验证的数据
     * @param userUuid
     * @param certificationType
     * @return
     */
    public UsrCertificationInfo getSuccessVerificationInfo(String userUuid,CertificationEnum certificationType){
        UsrCertificationInfo usrCertificationInfo = new UsrCertificationInfo();
        usrCertificationInfo.setUserUuid(userUuid);
        usrCertificationInfo.setDisabled(0);
        usrCertificationInfo.setCertificationType(certificationType.getType());
        usrCertificationInfo.setCertificationResult(CertificationResultEnum.AUTH_SUCCESS.getType());
        List<UsrCertificationInfo> certificationInfoList = this.usrCertificationInfoDao.scan(usrCertificationInfo);
        if(CollectionUtils.isEmpty(certificationInfoList)){
            return null;
        }
        return certificationInfoList.get(0);
    }

    public List<UsrCertificationInfo> getCertificationList(String userUuid){
        if(StringUtils.isEmpty(userUuid)){
            return new ArrayList<>();
        }
        UsrCertificationInfo searchParam = new UsrCertificationInfo();
        searchParam.setCertificationType(CertificationEnum.WHATS_APP.getType());
        searchParam.setUserUuid(userUuid);
        searchParam.setDisabled(0);
        List<UsrCertificationInfo> dbList = usrCertificationInfoDao.scan(searchParam);
        return dbList;
    }


    /***
     * 更新记录验证信息表
     * @param userUuid
     * @param certificationResult
     * @param certiType
     */
    public void updateUsrCertificationInfo(String userUuid, int certificationResult,Integer certiType,String... remark) {
        log.warn("updateUsrCertificationInfo: userUuid {} , result: {}", userUuid, certificationResult);
        UsrCertificationInfo usrCertificationInfo = new UsrCertificationInfo();
        usrCertificationInfo.setUserUuid(userUuid);
        usrCertificationInfo.setDisabled(0);
        usrCertificationInfo.setCertificationType(certiType);
        List<UsrCertificationInfo> users = usrCertificationInfoDao.scan(usrCertificationInfo);
        if (!CollectionUtils.isEmpty(users)) {
            UsrCertificationInfo usrCertification = users.get(0);
            usrCertification.setCertificationResult(certificationResult);
            for(String tmp: remark){
                usrCertification.setRemark(tmp);
            }
            this.usrCertificationInfoDao.update(usrCertification);
        }
    }

}