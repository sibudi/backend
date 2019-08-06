package com.yqg.service;

import com.yqg.drools.utils.ModelVariableUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Author: tonggen
 * Date: 2019/4/1
 * time: 3:11 PM
 * 代码自动生成工具尝试
 */
public class CodeProductMain {

    public static void main(String[] args) {
        //1.生成sql;

        //2.生成drl.

    }
    private List<String> getProductSqlStr(String ruleDetailType, String ruleDesc, String ruleValue,
                                          Integer ruleResult, Integer ruleStatus, List<String> flowNames,
                                          Integer ruleType,
                                          Integer ruleSequence,Integer appliedTo, Integer specifiedProduct) {

        StringBuffer sysAutoReviewRule = new StringBuffer("insert into sysAutoReviewRule(uuid,disabled,createTime,updateTime,createUser,updateUser,remark,ruleType,ruleDetailType,ruleDesc,ruleValue,ruleResult,ruleStatus,ruleRejectDay,ruleSequence,ruleVersion,appliedTo,specifiedProduct) values(replace(uuid(), '-', ''),0,now(),now(),0,0,'',");
        sysAutoReviewRule.append(ruleType).append(",'").append(ruleDetailType).append("','").append(ruleDesc).append("',").append("'").append(ruleValue)
                .append("',").append(ruleResult).append(ruleStatus).append(15).append(ruleSequence).append("V1")
                .append(appliedTo).append(specifiedProduct).append(";");
        System.out.println("sysAutoReviewRule is :\n" + sysAutoReviewRule);

        for (String flowName : flowNames) {

            StringBuffer flowRuleSet = new StringBuffer("insert into flowRuleSet(uuid,flowName,ruleDetailType,createTime,updateTime,remark) select replace(uuid(), '-', ''),");
            flowRuleSet.append("'").append(flowName).append("',");
            flowRuleSet.append("s.ruleDetailType,now(), now(),'' from sysAutoReviewRule s where s.disabled = 0 and s.ruleDetailType in ('")
            .append(ruleDetailType).append("');");
            System.out.println("flowRuleSet is : \n " + flowRuleSet.toString());
        }
        String ruleParam = "insert into ruleParam(flowName,ruleDetailType,thresholdValue,createTime,updateTime,uuid) select f.flowName,s.ruleDetailType,s.ruleValue,now(),now(),replace(uuid(), '-', '') from flowRuleSet f join sysAutoReviewRule s on f.ruleDetailType = s.ruleDetailType where s.disabled = 0 and s.ruleDetailType in (' " + ruleDetailType + "');";
        System.out.println("flowRuleSet is : \n " + ruleParam);

        return null;
    }
//
//    private String getRuleDetailTypeName(List<String> names) {
//
//        if (CollectionUtils.isEmpty(names)) {
//            return null;
//        }
//        for (String name : names) {
//
//        }
//    }

    /**
     *
     * @param ruleDetailType
     * @param ruleDesc
     * @param solveType 对应每个阈值的处理方式 （0 不等于；1 等于 2，大于，3 小于，4 大于等于 5 小于等于 .
     * @return
     */
    private List<String> getProductDrlStr(String ruleDetailType, String ruleDesc, List<Integer> solveType) {

        StringBuffer sf = new StringBuffer("");
        sf.append("rule\"").append(ruleDetailType).append("_A\"\n");
        sf.append("salience($thresholdValues.getRuleOrder(BlackListTypeEnum.").append(ruleDetailType).append(".getMessage()))\n");
        sf.append("when\n");
        sf.append("RuleConditionModel(isSuitableForRule(BlackListTypeEnum.").append(ruleDetailType).append(".getMessage()));\n");
        sf.append("$thresholdValues:RuleThresholdValues($threshData:thresholdMap[BlackListTypeEnum.").append(ruleDetailType).append(".getMessage()]);\n");
        List<String> chineseNames = Arrays.asList(ruleDesc.split("&"));
        sf.append(getVariableValue(chineseNames));
//        sf.append(
//                "        RUserInfo($sex:sex, $whatsAppCheckResult:whatsAppCheckResult)\n" +
//                "        eval($sex==SexEnum.FEMALE.getCode()\n" +
//                "             && RuleUtils.equalString(RuleUtils.valueOfStr($whatsAppCheckResult),RuleUtils.getRuleParam($threshData,0)))\n" +
//                "    then\n" +
//                "        RuleUtils.buildHitRuleResult(BlackListTypeEnum.WHATSAPP_CONNECT_FEMALE.getMessage(),\"true\",\"命中IZI本人whatsapp未开通&女性\").addToResultList\n" +
//                "        (ruleSetResultList);\n" +
//                "end")
        return null;
    }

    private StringBuffer getVariableValue(List<String> chineseNames) {

        StringBuffer sf = new StringBuffer("");
        chineseNames.stream().forEach(elem -> {
            sf.append(ModelVariableUtils.globalVariable.get(elem).split(",")[0]).append("\n");
        });
        return sf;
    }


    private StringBuffer getEvalValue(List<Integer> solveType, List<String> chineseNames) {

        if (solveType.size() != chineseNames.size()) {
            return null;
        }
        StringBuffer sf = new StringBuffer("eval(");
        solveType.stream().forEach(elem -> {
            sf.append(ModelVariableUtils.globalVariable.get(elem).split(",")[0]).append("\n");
        });
        return sf;
    }
}
