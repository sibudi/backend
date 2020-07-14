package com.yqg.service.alicloud.access_role;

import java.io.IOException;
//import com.aliyun.fc.runtime.Context;

public class RoleService {

    private IAccessRoleHandler access_role;

    public IAccessRoleHandler getAccessRoleHandler() {
        return access_role;
    }
    // Disable unused FcRole
    // public RoleService(Context context) {
    //     access_role = new FcRole(context);
    // }

    public RoleService(String profile_name) throws IOException {
        access_role = new LocalRole(profile_name);
    }
    public RoleService() {
        access_role = new InstanceRole();
    }
}