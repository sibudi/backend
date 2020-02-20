package com.yqg.manage.service.user;

import com.yqg.common.constants.MessageConstants;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.EmailUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.manage.service.user.request.ManUserUserRequest;
import com.yqg.service.user.service.UsrPINService;
import com.yqg.system.dao.SysSmsCodeDao;
import com.yqg.system.entity.SysSmsCode;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.entity.UsrUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author alan
 */
@Component
public class ManUserCommonInfoService {

    private Logger logger = LoggerFactory.getLogger(ManUserCommonInfoService.class);
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private SysSmsCodeDao sysSmsCodeDao;

    @Autowired
    private UsrPINService pinService;

    @Autowired
    private UsrDao userDao;

    public Object getSmsCodeByMobile(ManUserUserRequest request) throws ServiceExceptionSpec {
        String mobileNumber = request.getMobile();
        if (StringUtils.isEmpty(mobileNumber)) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        Map<String, Object> resultMap = new HashMap<>();
        if (mobileNumber.substring(0, 1).equals("0")) {
            mobileNumber = mobileNumber.substring(1, mobileNumber.length());
        }
        mobileNumber = "62" + mobileNumber;
        StringBuilder stringBuilder = new StringBuilder(RedisContants.SESSION_SMS_KEY);
        stringBuilder.append(mobileNumber);

        String code = redisClient.get(stringBuilder.toString());
        resultMap.put("mobile", mobileNumber);
        resultMap.put("smsCode", code);
        return resultMap;
    }

    public Object smsCodeByMobileFromMysql(ManUserUserRequest request) throws ServiceExceptionSpec {
        String mobileNumber = request.getMobile();
        if (StringUtils.isEmpty(mobileNumber)) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        //stageType null查询验证码
        if (request.getStageType() == null) {
            String userMobile = DESUtils.encrypt(request.getMobile());
            SysSmsCode sysSmsCode = new SysSmsCode();
            sysSmsCode.setDisabled(0);
            sysSmsCode.setMobile(userMobile);
            sysSmsCode.set_orderBy("createTime DESC ");
            List<SysSmsCode> lists = sysSmsCodeDao.scan(sysSmsCode);
            if (CollectionUtils.isEmpty(lists)) {
                return new ArrayList<>();
            }
            lists.stream().forEach(elem -> elem.setMobile(request.getMobile()));
            return lists;
        }

        // stageType 1 删除redis验证码上线
        if (request.getStageType().equals(1)) {

            StringBuilder stringBuilder=new StringBuilder(RedisContants.SESSION_SMS_KEY+"count");
            stringBuilder.append("62" + mobileNumber);
            stringBuilder.append(DateUtils.DateToString(new Date()));
            logger.info(redisClient.get(stringBuilder.toString()) + " 次" + request.getMobile() + "删除次数成功!");
            return redisClient.del(stringBuilder.toString());
        }
        return new ArrayList<>();
    }

    public String getUserEmail(ManUserUserRequest request) throws Exception {
        String mobileNumber = request.getMobile();
        if (StringUtils.isEmpty(mobileNumber)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_NOT_FOUND);
        }

        UsrUser user = new UsrUser();
        user.setDisabled(0);
        user.setMobileNumberDES(DESUtils.encrypt(mobileNumber));

        List<UsrUser> users =  userDao.scan(user);
        if(CollectionUtils.isEmpty(users)){
            throw new ServiceExceptionSpec(ExceptionEnum.USER_NOT_FOUND);
        }
        else{
            return DESUtils.decrypt(users.get(0).getEmailAddress());
        }
    }

    public String setUserEmail(ManUserUserRequest request) throws Exception {
        String mobileNumber = request.getMobile();
        if (StringUtils.isEmpty(mobileNumber)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_NOT_FOUND);
        }

        UsrUser user = new UsrUser();
        user.setDisabled(0);
        user.setMobileNumberDES(DESUtils.encrypt(mobileNumber));

        List<UsrUser> users =  userDao.scan(user);
        if(CollectionUtils.isEmpty(users)){
            throw new ServiceExceptionSpec(ExceptionEnum.USER_NOT_FOUND);
        }
        else{
            UsrUser checkEmail = new UsrUser(); 
            checkEmail.setDisabled(0);
            checkEmail.setEmailAddress(DESUtils.encrypt(request.getEmailAddress()));
            List<UsrUser> emails = userDao.scan(checkEmail);
            if(!CollectionUtils.isEmpty(emails)){
                return MessageConstants.DUPLICATE_EMAIL_MESSAGE;
            }

            UsrUser usr = users.get(0);
            if("".equals(usr.getEmailAddress()) || usr.getEmailAddress() == null){
                if(EmailUtils.isValid(request.getEmailAddress())){
                    usr.setEmailAddress(DESUtils.encrypt(request.getEmailAddress()));
                    userDao.update(usr);

                    return MessageConstants.SUCCESS_EMAIL_UPDATE_MESSAGE;
                }
                else{
                    throw new ServiceExceptionSpec(ExceptionEnum.INVALID_EMAIL);
                }
            }
            else{
                throw new ServiceExceptionSpec(ExceptionEnum.INVALID_ACTION);
            }
        }
    }

    public void resetPIN(ManUserUserRequest request) throws Exception {
        
        String mobileNumber = request.getMobile();
        if (StringUtils.isEmpty(mobileNumber)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_NOT_FOUND);
        }

        UsrUser user = new UsrUser();
        user.setDisabled(0);
        user.setMobileNumberDES(DESUtils.encrypt(mobileNumber));

        List<UsrUser> users =  userDao.scan(user);
        if(CollectionUtils.isEmpty(users)){
            throw new ServiceExceptionSpec(ExceptionEnum.USER_NOT_FOUND);
        }
        else{
            UsrUser usr = users.get(0);
            if(StringUtils.isEmpty(usr.getEmailAddress())){
                throw new ServiceExceptionSpec(ExceptionEnum.INVALID_EMAIL_NOT_FOUND);
            }
            else{
                String email = DESUtils.decrypt(usr.getEmailAddress());
                pinService.forgotPIN(request.getMobile(), email);
            }
        }
    }
}