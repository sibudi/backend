package com.yqg.scheduling;


import com.yqg.service.risk.service.Overdue15UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BlackListUsersTask {

    @Autowired
    private Overdue15UserService overdue15UserService;


    /****
     * 黑名单用户定时更新【逾期15天用户每天增加，逾期29天变到30天的用户更新类型】
     */

    @Scheduled(cron = "0 30 0 * * ?")
    public void addOverdue15DaysUsers() {
        overdue15UserService.addOverdue15Users();
    }

    public static void main(String[] args) {

    }
}
