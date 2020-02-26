
package com.yqg.service.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import freemarker.template.utility.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.yqg.common.constants.MessageConstants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.DESUtils;
import com.yqg.user.dao.UsrPINDao;
import com.yqg.user.entity.UsrPIN;
import com.yqg.common.utils.EmailUtils;

@Service
@Slf4j
public class UsrPINService {
    
    private int expirationDays = 30;
    private int expirationTempDays = 1;

    @Autowired
    private UsrPINDao usrPINDao;

    public void newPIN(String mobileNumber, String emailAddress) throws Exception{

        if(EmailUtils.isValid(emailAddress) || "".equals(mobileNumber)){
            UsrPIN userPIN = new UsrPIN();
            userPIN.setDisabled(0);
            userPIN.setMobileNumberDES(DESUtils.encrypt(mobileNumber));
            userPIN.setEmailAddressDES(DESUtils.encrypt(emailAddress));
            
            List<UsrPIN> usrPINs = usrPINDao.scan(userPIN);
            if(usrPINs.isEmpty() || usrPINs.size() == 0){
                userPIN.setIsPhoneNumberVerified(0);
                userPIN.setIsTemporaryPIN(1);
                String pin = generatePIN();
                userPIN.setPinDES(DESUtils.encrypt(pin));
                userPIN.setExpiration(new Date());
                usrPINDao.insert(userPIN);

                EmailUtils.sendEmail(emailAddress, 
                    MessageFormat.format(MessageConstants.REGISTER_PIN_MESSAGE, pin), 
                    MessageConstants.REGISTER_PIN_SUBJECT);
            }
            else{
                throw new ServiceException(ExceptionEnum.INVALID_ACTION);
            }
        }
        else{
            throw new ServiceException(ExceptionEnum.INVALID_MOBILE_NO_OR_EMAIL);
        }
    }

    public void forgotPIN(String mobileNumber, String emailAddress) throws Exception{

        if(EmailUtils.isValid(emailAddress) || "".equals(mobileNumber)){
            UsrPIN userPIN = new UsrPIN();
            userPIN.setDisabled(0);
            userPIN.setMobileNumberDES(DESUtils.encrypt(mobileNumber));
            userPIN.setEmailAddressDES(DESUtils.encrypt(emailAddress));
    
            List<UsrPIN> usrPINs = usrPINDao.scan(userPIN);
            Date now = new Date();

            for (UsrPIN pin : usrPINs) {
                if(now.getTime() - pin.getCreateTime().getTime() < 1000 * 120){
                    throw new ServiceException(ExceptionEnum.INVALID_RESET_PIN_LONG);
                }
                pin.setDisabled(1);
                usrPINDao.update(pin);
            }

            userPIN.setExpiration(new Date());
            userPIN.setIsPhoneNumberVerified(0);
            userPIN.setIsTemporaryPIN(1);
            String pin = generatePIN();
            userPIN.setPinDES(DESUtils.encrypt(pin));
            usrPINDao.insert(userPIN);

            EmailUtils.sendEmail(emailAddress, 
                MessageFormat.format(MessageConstants.RESET_PIN_MESSAGE, pin), 
                MessageConstants.RESET_PIN_SUBJECT);
        }
        else{
            throw new ServiceException(ExceptionEnum.INVALID_MOBILE_NO_OR_EMAIL);
        }
    }

    public void changePIN(String mobileNumber, String emailAddress, String currentPIN, String newPIN) throws Exception{

        if(EmailUtils.isValid(emailAddress) || "".equals(mobileNumber)){
            UsrPIN userPIN = new UsrPIN();
            userPIN.setDisabled(0);
            userPIN.setMobileNumberDES(DESUtils.encrypt(mobileNumber));
            userPIN.setEmailAddressDES(DESUtils.encrypt(emailAddress));
            userPIN.setPinDES(DESUtils.encrypt(currentPIN));

            List<UsrPIN> usrPINs = usrPINDao.scan(userPIN);
            if(usrPINs.isEmpty() || usrPINs.size() == 0){
                throw new ServiceException(ExceptionEnum.INVALID_PIN);
            }

            for (UsrPIN itemPin : usrPINs) {
                itemPin.setDisabled(1);
                usrPINDao.update(itemPin);
            }

            userPIN.setExpiration(new Date());
            userPIN.setIsTemporaryPIN(0);
            userPIN.setIsPhoneNumberVerified(0);
            userPIN.setPinDES(DESUtils.encrypt(newPIN));
            usrPINDao.insert(userPIN);

            EmailUtils.sendEmail(emailAddress, MessageConstants.CHANGE_PIN_MESSAGE, MessageConstants.CHANGE_PIN_SUBJECT);
        }
        else{
            throw new ServiceException(ExceptionEnum.INVALID_MOBILE_NO_OR_EMAIL);
        }
        
    }

    public Boolean isLoginTemporary(String mobileNumber, String emailAddress, String currentPIN) throws Exception{
        // true : success
        // false : temporary pin
        if(EmailUtils.isValid(emailAddress) || "".equals(mobileNumber)){
            UsrPIN userPIN = new UsrPIN();
            userPIN.setDisabled(0);
            userPIN.setMobileNumberDES(DESUtils.encrypt(mobileNumber));
            userPIN.setEmailAddressDES(DESUtils.encrypt(emailAddress));
            userPIN.setPinDES(DESUtils.encrypt(currentPIN));

            List<UsrPIN> usrPINs = usrPINDao.scan(userPIN);
            if(usrPINs.size() > 0){
                if(usrPINs.get(0).getIsTemporaryPIN() == 1)
                {
                    return false;
                }
                return true;
            }
            else{
                throw new ServiceException(ExceptionEnum.INVALID_MOBILE_NO_OR_EMAIL);
            }
        }
        else{
            throw new ServiceException(ExceptionEnum.NO_USER_NAME);
        }
    }

    public Boolean isLoginTemporary(String mobileNumber, String emailAddress) throws Exception{
        // true : temporary pin
        // false : has change password
        if(EmailUtils.isValid(emailAddress) || "".equals(mobileNumber)){
            UsrPIN userPIN = new UsrPIN();
            userPIN.setDisabled(0);
            userPIN.setMobileNumberDES(DESUtils.encrypt(mobileNumber));
            userPIN.setEmailAddressDES(DESUtils.encrypt(emailAddress));

            List<UsrPIN> usrPINs = usrPINDao.scan(userPIN);
            if(usrPINs.size() > 0){
                if(usrPINs.get(0).getIsTemporaryPIN() == 1)
                {
                    return true;
                }
            }
        }
        return false;
    }

    private String generatePIN() throws Exception{
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(1000000);
        String formatted = String.format("%06d", num); 
        return formatted;
    }
}