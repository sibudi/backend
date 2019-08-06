package com.yqg.manage.service.system.request;

import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/12/27.
 */
@Data
public class CheckBalanceRequest {

    String channel;
    String account;
}
