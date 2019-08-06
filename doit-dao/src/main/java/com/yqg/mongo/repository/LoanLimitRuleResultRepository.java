package com.yqg.mongo.repository;

import com.yqg.mongo.dao.LoanLimitRuleResultDal;
import com.yqg.mongo.entity.LoanLimitRuleResultMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Repository
public class LoanLimitRuleResultRepository {
    @Autowired
    private LoanLimitRuleResultDal loanLimitRuleResultDal;

    public void addRuleResultList(List<LoanLimitRuleResultMongo> paramList){
       if(CollectionUtils.isEmpty(paramList)){
           return;
       }

       for(LoanLimitRuleResultMongo mongo: paramList){
           mongo.setCreateTime(new Date());
           mongo.setUpdateTime(new Date());
           loanLimitRuleResultDal.insertObject(mongo);
       }
    }
}
