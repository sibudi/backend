package com.yqg.service.user.service;

import com.yqg.activity.dao.UsrGoPayDao;
import com.yqg.activity.entity.UsrGoPay;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.BaseRequest;
import com.yqg.service.user.request.UsrGoPayReq;
import com.yqg.service.user.response.UsrGoPayResp;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * Features:
 * Created by huwei on 18.8.16.
 */
@Service
@Slf4j
public class UsrGoPayService {
    @Autowired
    private UsrGoPayDao usrGoPayDao;
    @Autowired
    private UsrService usrService;

    /**
     * 添加用户gopay信息
     * @Author:huwei
     * @Date:18.8.16-18:09
     */
    @Transactional
    public void saveUserGoPay(UsrGoPayReq usrGoPayReq) throws ServiceException {
        UsrUser user = this.usrService.getUserByUuid(usrGoPayReq.getUserUuid());
        if (StringUtils.isEmpty(user)){
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
        //查询用户是否有gopay信息
        UsrGoPay usrGoPay = new UsrGoPay();
        usrGoPay.setDisabled(0);
        usrGoPay.setUserUuid(user.getUuid());
        usrGoPay.set_orderBy("createTime desc");
        List<UsrGoPay> scan = this.usrGoPayDao.scan(usrGoPay);
        if (CollectionUtils.isEmpty(scan)){
            usrGoPay.setUserName(usrGoPayReq.getUserName());
            usrGoPay.setMobileNumber(usrGoPayReq.getMobileNumber());
            usrGoPay.setCreateTime(new Date());
            usrGoPay.setUpdateTime(new Date());
            this.usrGoPayDao.insert(usrGoPay);
        }else{
            UsrGoPay goPay = scan.get(0);
            if (goPay.getMobileNumber().equals(usrGoPayReq.getMobileNumber())&&goPay.getUserName().equals(usrGoPayReq.getUserName())){
                goPay.setUpdateTime(new Date());
                this.usrGoPayDao.update(goPay);
            }else{
                goPay.setDisabled(1);
                this.usrGoPayDao.update(goPay);
                UsrGoPay usrGoPay1 = new UsrGoPay();
                usrGoPay1.setUserUuid(usrGoPayReq.getUserUuid());
                usrGoPay1.setDisabled(0);
                usrGoPay1.setUserName(usrGoPayReq.getUserName());
                usrGoPay1.setMobileNumber(usrGoPayReq.getMobileNumber());
                usrGoPay1.setCreateTime(new Date());
                usrGoPay1.setUpdateTime(new Date());
                this.usrGoPayDao.insert(usrGoPay1);
            }
        }
    }

    /**
     * 查询用户gopay信息
     * @Author:huwei
     * @Date:18.8.16-18:57
     */
    public UsrGoPayResp selectUserGoPay(BaseRequest baseRequest) {
        UsrGoPay usrGoPay = new UsrGoPay();
        usrGoPay.setUserUuid(baseRequest.getUserUuid());
        usrGoPay.setDisabled(0);
        usrGoPay.set_orderBy("createTime desc");
        List<UsrGoPay> scan = this.usrGoPayDao.scan(usrGoPay);
        if (scan.isEmpty()){
            return null;
        }
        UsrGoPayResp usrGoPayResp = new UsrGoPayResp();
        usrGoPayResp.setUserName(scan.get(0).getUserName());
        usrGoPayResp.setMobileNumber(scan.get(0).getMobileNumber());
        return usrGoPayResp;
    }
}
