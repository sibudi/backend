package com.yqg.manage.service.system;

import com.yqg.service.util.LoginSysUserInfoHolder;
import com.yqg.user.dao.ManSysUserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: tonggen
 * Date: 2019/5/14
 * time: 11:00 AM
 */
@Slf4j
@Service
public class ManAuthManagerService {


    @Autowired
    private ManSysUserDao manSysUserDao;
    /**
     * Use a batch of role names to query whether the user has the role permissions
     * @return
     */
    public boolean hasAuthorityByRoleName(List<Integer> request) {
        if (CollectionUtils.isEmpty(request)) {

            log.error("hasAuthorityByRoleName param is null.");
            return false;
        }
        Integer userId = LoginSysUserInfoHolder.getLoginSysUserId();
        String roleNameStr = request.stream().map(elem -> String.valueOf(elem)).collect(Collectors.joining(","));
        log.info("hasAuthorityByRoleName role id is {}", roleNameStr);
        Integer result = manSysUserDao.hasAuthorityByRoleName(userId, roleNameStr);

        return result != null && result > 0;
    }
}
