package com.yqg.service;

import com.yqg.user.dao.UsrLinkManDao;
import com.yqg.user.entity.UsrLinkManInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class UsrLinkManMobileFormatService {

    @Autowired
    private UsrLinkManDao usrLinkManDao;

    public void fixHistData() {
        for (int i = 0; i < 1000; i++) {
            try {
                List<UsrLinkManInfo> linkList = usrLinkManDao.getDataFixList();
                if (CollectionUtils.isEmpty(linkList)) {
                    log.info("the linkList is empty, i: {}", i);
                    return;
                }
                for (UsrLinkManInfo info : linkList) {
                    info.setFormatMobile(info.getContactsMobile());
                    usrLinkManDao.updateFormatMobile(info.getFormatMobile(), info.getId());
                }
            } catch (Exception e) {
                log.error("history data fix error", e);
            }
        }
        log.info("finished....");
    }
}
