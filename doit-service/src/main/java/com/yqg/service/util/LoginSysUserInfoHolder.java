package com.yqg.service.util;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 ****/


public class LoginSysUserInfoHolder {

    private static ThreadLocal<Integer> sysUserIdHolders = new ThreadLocal<>();//记录每次调用对应的用户id

    private static ThreadLocal<String> sysSessionIdHolders = new ThreadLocal<>();

    public static void setLoginSysUserId(Integer sysUserId) {
        sysUserIdHolders.set(sysUserId);
    }

    public static Integer getLoginSysUserId() {
        return sysUserIdHolders.get();
    }

    public static void setSessionIdUser(String session) {
        sysSessionIdHolders.set(session);
    }

    public static String getUsrSessionId() {
        return sysSessionIdHolders.get();
    }

}
