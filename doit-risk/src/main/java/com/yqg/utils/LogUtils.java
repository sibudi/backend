package com.yqg.utils;

import org.slf4j.MDC;

public class LogUtils {
    public static void addMDCRequestId(String requestId) {
        MDC.put("X-Request-Id", requestId);
    }

    public static void removeMDCRequestId() {
        MDC.remove("X-Request-Id");
    }

    public static void main(String[] args) {
        String str =  "select count(*) from teleCallResult t where t.callType = 3\n" +
                "and t.disabled=0 \n" +
                "and t.userUuid in (\n" +
                "select userUuid from usrProductRecord where disabled = 0\n" +
                ")\n" +
                "and exists(\n" +
                "select 1 from ordOrder o where o.userUuid = t.userUuid and o.borrowingCount = 1 and o.amountApply=1200000 and o.status = 10\n" +
                ")\n" +
                "and t.tellNumber = #{mobile}";
        System.err.println(str);
    }
}
