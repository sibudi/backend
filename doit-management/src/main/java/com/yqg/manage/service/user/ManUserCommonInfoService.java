package com.yqg.manage.service.user;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.manage.service.user.request.ManUserUserRequest;
import com.yqg.system.dao.SysSmsCodeDao;
import com.yqg.system.entity.SysSmsCode;
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
}