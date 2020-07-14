package com.yqg.service.alicloud.access_role;

import java.time.LocalDateTime;

public interface IAccessRoleHandler {
    public enum RoleType {
        LOCAL, FUNCTION_COMPUTE, INSTANCE
    }
    public RoleType getRoleType();
    public String getAccessKeyId();
    public String getAccessKeySecret();
    public String getSecurityToken();
    public LocalDateTime getExpiration();
}