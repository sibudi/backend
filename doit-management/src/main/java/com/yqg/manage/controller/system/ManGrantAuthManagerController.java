package com.yqg.manage.controller.system;

import com.yqg.common.models.ResponseEntitySpec;
import com.yqg.manage.service.system.ManAuthManagerService;
import com.yqg.service.util.LoginSysUserInfoHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: tonggen
 * Date: 2019/5/14
 * time: 3:42 PM
 */
@RestController
public class ManGrantAuthManagerController {


    @Autowired
    private ManAuthManagerService manAuthManagerService;

//    public ResponseEntitySpec<Boolean> showOrderDetail() {
//        LoginSysUserInfoHolder.getLoginSysUserId();
//    }
}
