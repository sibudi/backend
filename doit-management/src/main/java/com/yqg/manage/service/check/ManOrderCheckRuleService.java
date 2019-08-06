package com.yqg.manage.service.check;


import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.manage.dal.order.ManOrderCheckRuleDao;
import com.yqg.manage.dal.order.ManOrderOrderDao;
import com.yqg.manage.entity.check.ManOrderCheckRule;
import com.yqg.manage.service.check.request.OrderCheckRule;
import com.yqg.order.entity.OrdOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author alan
 */
@Component
public class ManOrderCheckRuleService {
    @Autowired
    private ManOrderCheckRuleDao manOrderCheckRuleDao;

    @Autowired
    private ManOrderOrderDao manOrderOrderDao;

    /**
     * 添加一条审核规则历史记录*/
    public void addOrderCheckRule(OrderCheckRule item,String orderNo,Integer updateUser,
                                  Integer infoType, String description, String descriptionInn) throws ServiceExceptionSpec {
        ManOrderCheckRule addInfo = new ManOrderCheckRule();
        addInfo.setOrderNo(orderNo);
        addInfo.setUuid(UUIDGenerateUtil.uuid());
        addInfo.setUpdateUser(updateUser);
        addInfo.setInfoType(infoType);
        addInfo.setRuleCount(item.getRuleCount());
        addInfo.setRuleLevel(item.getRuleLevel());
        addInfo.setType(item.getType());
        addInfo.setDescription(description);
        addInfo.setDescriptionInn(descriptionInn);
        if(item.getRuleResult() == true){   //true为1命中审核规则，false为0
            addInfo.setRuleResult(1);
        }else{
            addInfo.setRuleResult(0);
        }

        this.manOrderCheckRuleDao.insert(addInfo);
    }

    /**
     * 修改一条审核记录*/
    public void editOrderCheckRule(ManOrderCheckRule item,Boolean ruleResult,
                                   Integer updateUser, String description, String descriptionInn, Integer ruleLevel) throws ServiceExceptionSpec {
        ManOrderCheckRule editInfo = new ManOrderCheckRule();
        editInfo.setUuid(item.getUuid());
        if(ruleResult == true){     //true为1命中审核规则，false为0
            editInfo.setRuleResult(1);
        }else {
            editInfo.setRuleResult(0);
        }
        editInfo.setRuleLevel(ruleLevel);
        editInfo.setUpdateUser(updateUser);
        editInfo.setDescription(description);
        editInfo.setDescriptionInn(descriptionInn);
        editInfo.setType(item.getType());
        this.manOrderCheckRuleDao.update(editInfo);
    }

    /**
     * 通过orderNo和infoType查询审核规则历史记录*/
    public List<ManOrderCheckRule> manOrderCheckRuleList(String orderNo,Integer infoType) throws ServiceExceptionSpec {
        ManOrderCheckRule search = new ManOrderCheckRule();
        search.setOrderNo(orderNo);
        search.setInfoType(infoType);
        search.setDisabled(0);
        search.set_orderBy("ruleCount");
        List<ManOrderCheckRule> result = this.manOrderCheckRuleDao.scan(search);
        if(!CollectionUtils.isEmpty(result)){
            return result;
        }
        //如果是完成审核的订单，查询被删除的审核记录展示
        OrdOrder ordOrder = new OrdOrder();
        ordOrder.setDisabled(0);
        ordOrder.setUuid(orderNo);
        List<OrdOrder> lists = manOrderOrderDao.scan(ordOrder);
        if (CollectionUtils.isEmpty(lists)) {
            return null;
        }
        if (lists.get(0).getStatus().equals(OrdStateEnum.FIRST_CHECK.getCode())) {
            return null;
        }
        List<ManOrderCheckRule> complanyResult = this.manOrderCheckRuleDao.getDisabedList(orderNo, infoType);
        if(CollectionUtils.isEmpty(complanyResult)){
            return null;
        }
        //排除多次订单回退的情况
        Date createTime = complanyResult.get(0).getCreateTime();
        complanyResult = complanyResult.stream().filter(elem -> {
            return Math.abs(createTime.getTime() - elem.getCreateTime().getTime()) < 3000;
        }).collect(Collectors.toList());

        return complanyResult;
    }

    /**
     * 通过orderNo和ruleResult查询审核历史记录*/
    public List<ManOrderCheckRule> orderCheckRuleCountByNoResult(String orderNo,Integer ruleResult) throws ServiceExceptionSpec {
        ManOrderCheckRule search = new ManOrderCheckRule();
        search.setOrderNo(orderNo);
        search.setDisabled(0);
        search.setRuleResult(ruleResult);
        return this.manOrderCheckRuleDao.scan(search);
    }

    /**
     * */
    public Integer orderCheckRuleCountByNoLevel(String orderNo,Integer ruleLevel) throws ServiceExceptionSpec {
        ManOrderCheckRule search = new ManOrderCheckRule();
        search.setOrderNo(orderNo);
        search.setRuleLevel(ruleLevel);
        search.setDisabled(0);
        search.setRuleResult(1);
        return this.manOrderCheckRuleCount(search);
    }

    /**
     * 计算审核规则历史记录数*/
    public Integer manOrderCheckRuleCount(ManOrderCheckRule rule) throws ServiceExceptionSpec {
        Integer count = this.manOrderCheckRuleDao.count(rule);
        return count;
    }
}
