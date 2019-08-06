package com.yqg.service.system.service;

import com.yqg.system.dao.SysAutoReviewRuleDao;
import com.yqg.system.entity.SysAutoReviewRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysAutoReviewRuleService {
    @Autowired
    private SysAutoReviewRuleDao sysAutoReviewRuleDao;

    public SysAutoReviewRule getRuleConfigByName(String ruleName) {
        return sysAutoReviewRuleDao.getRuleConfigByName(ruleName);
    }

    public List<SysAutoReviewRule> getAllRuleConfigs() {
        return sysAutoReviewRuleDao.reviewRuleList();
    }
}
