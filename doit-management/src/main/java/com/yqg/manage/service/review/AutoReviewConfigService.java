package com.yqg.manage.service.review;

import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.manage.dal.system.AutoReviewRuleDao;
import com.yqg.manage.service.review.request.AutoReviewRuleParam;
import com.yqg.manage.service.review.response.AutoReviewRuleResponse;
import com.yqg.system.entity.SysAutoReviewRule;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Slf4j
@Service
public class AutoReviewConfigService {

    @Autowired
    private AutoReviewRuleDao autoReviewRuleDao;

    public List<AutoReviewRuleResponse> getAutoReviewRuleConfigByType(Integer type) {
        List<SysAutoReviewRule> allRules = autoReviewRuleDao.getAllAutoReviewRules();
        if (CollectionUtils.isEmpty(allRules)) {
            return new ArrayList<>();
        }
        return allRules.stream()
                .filter(elem -> elem.getRuleType() != null && elem.getRuleType().equals(type))
                .map(elem ->
                        new AutoReviewRuleResponse(elem.getRuleType(),
                                elem.getRuleDesc(),
                                elem.getRuleCondition(), elem.getRuleValue(), elem.getRuleStatus(),
                                elem.getUuid(),
                                elem.getId(), elem.getRuleRejectDay(), elem.getRuleData())
                ).collect(Collectors.toList());
    }

    @Transactional
    public boolean editAutoReviewRule(Integer operatorId, AutoReviewRuleParam param) {
        if (StringUtils.isEmpty(param.getUuid())) {
            log.error("the input param is not valid, param= " + JsonUtils.serialize(param));
            return false;
        }


        SysAutoReviewRule searchInfo = new SysAutoReviewRule();
        searchInfo.setUuid(param.getUuid());
        searchInfo.setDisabled(0);
        //查询数据库中数据：
        List<SysAutoReviewRule> dbRuleList = autoReviewRuleDao.scan(searchInfo);
        if (CollectionUtils.isEmpty(dbRuleList) || dbRuleList.size() > 1) {
            log.error("the fetch rule more than one ,uuid is: {}", param.getUuid());
        }
        searchInfo.setDisabled(1);
        searchInfo.setUpdateTime(new Date());
        searchInfo.setUpdateUser(operatorId);
        //disabled 原来的数据
        autoReviewRuleDao.update(searchInfo);

        //插入新的规则记录
        SysAutoReviewRule dbRule = dbRuleList.get(0);
        dbRule.setDisabled(0);
        dbRule.setId(null);
        if (param.getRuleStatus() != null) {
            dbRule.setRuleStatus(param.getRuleStatus());
        }
        if (!StringUtils.isEmpty(param.getRuleCondition())) {
            dbRule.setRuleCondition(param.getRuleCondition());
        }
        if (!StringUtils.isEmpty(param.getRuleValue())) {
            dbRule.setRuleValue(param.getRuleValue());
        }
        if (!StringUtils.isEmpty(param.getRuleData())) {
            dbRule.setRuleData(param.getRuleData());
        }
        if (!StringUtils.isEmpty(param.getRuleDesc())) {
            dbRule.setRuleDesc(param.getRuleDesc());
        }
        if (param.getRuleRejectDay() != null) {
            dbRule.setRuleRejectDay(param.getRuleRejectDay());
        }
        if (StringUtils.isEmpty(param.getRuleData())) {
            dbRule.setRuleData(param.getRuleData());
        }
        dbRule.setCreateTime(new Date());
        dbRule.setUpdateTime(new Date());
        dbRule.setUuid(UUIDGenerateUtil.uuid());
        String version = dbRule.getRuleVersion();
        Integer intVersion = Integer.valueOf(version.substring(1, version.length())) + 1;
        dbRule.setRuleVersion("V" + intVersion);

        boolean updateResult = autoReviewRuleDao.insert(dbRule) == 1;
        if (!updateResult) {
            log.error("rule update failed, param = " + JsonUtils.serialize(param));
            throw new RuntimeException("error");
        }
        return updateResult;
    }
}
