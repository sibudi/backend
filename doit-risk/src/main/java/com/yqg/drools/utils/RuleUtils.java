package com.yqg.drools.utils;

import com.yqg.common.constants.RedisContants;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.NumberUtils;
import com.yqg.drools.model.base.RuleResult;
import com.yqg.drools.model.base.ScoreRuleResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import java.util.Date;


@Slf4j
public class RuleUtils {


    public static BigDecimal formatIncome(String strIncome) {
        try {
            if (com.yqg.common.utils.StringUtils.isNotEmpty(strIncome)) {
                String income = strIncome.replaceAll("\\.", "");
                income = income.replaceAll("\\s+", "");
                income = income.replaceAll("\\,", "");
                if (com.yqg.common.utils.StringUtils.isNotEmpty(income)) {
                    income = income.replaceAll("\\u202D", "");
                    income = income.replaceAll("\\u202d", "");
                }
                return com.yqg.common.utils.StringUtils.isEmpty(income) ? null : new BigDecimal(income);
            }
        } catch (NumberFormatException e) {
            log.error("the error str: " + strIncome, e);
            throw e;
        }
        return null;
    }

    public static String extractNumber(String content, String pattern) {
        //规则中按照正则表但是去数值
        Pattern p = Pattern.compile(pattern.toLowerCase());
        Matcher m = p.matcher(content.toLowerCase());
        while (m.find()) {
            int count = m.groupCount();
            for (int i = 1; i <= count; i++) {
                Long num = null;
                try {
                    String str = m.group(i);
                    num = Long.valueOf(str);
                    if (num > 999) {
                        return "";//忽略掉四位数的数值
                    }
                } catch (Exception e) {
                    //可能不是数值
                }
                if (num != null) {
                    return num.toString();
                }

            }
        }
        return "";
    }


    /**
     * 短信中出现逾期天数
     */
    public static String matchSmsBody(String smsBody, String ruleBody) {
        String num = "";
        if (com.yqg.common.utils.StringUtils.isNotEmpty(smsBody) && matchSmsBodyFilterWord(smsBody)
                .contains(matchSmsBodyFilterWord(ruleBody))) {
            num = matchSmsBodyFilterNum(smsBody, ruleBody);// 提取短信中包含短信模板之间的数字
        }
        return num;
    }

    /**
     * 只筛选出空格和字母
     */
    public static String matchSmsBodyFilterWord(String smsBody) {
        String regWord = "[^A-Za-z\\s]";//
        Pattern p = Pattern.compile(regWord);
        Matcher m = p.matcher(smsBody);
        return m.replaceAll("").trim();
    }


    /**
     * 只筛选出数字
     */
    public static String matchSmsBodyFilterNum(String smsBody, String ruleBody) {
        ruleBody = ruleBody.replace("16", "(\\d+)");
        String day = "0";
        Pattern p = Pattern.compile(ruleBody);
        Matcher matcher = p.matcher(smsBody);
        while (matcher.find()) {
            day = matcher.group(1);
        }
        return day;
    }

    /***
     * 构建命中规则
     * @param ruleName
     * @param realValue
     * @return
     */
    public static RuleResult buildHitRuleResult(String ruleName, String realValue, String desc) {
        RuleResult result = new RuleResult();
        result.setRealValue(realValue);
        result.setRuleName(ruleName);
        result.setPass(true);
        result.setDesc(desc);
        return result;
    }


    /***
     * 构建命中规则
     * @param ruleName
     * @param desc
     * @return
     */
    public static RuleResult buildEmptyDataRuleResult(String ruleName, String realValue,
                                                      String desc) {
        RuleResult result = new RuleResult();
        result.setRuleName(ruleName);
        result.setRealValue(realValue);
        result.setPass(true);
        result.setDesc(desc);
        result.setDataEmpty(true);
        return result;
    }


    /***
     * 构建不命中规则
     * @param ruleName
     * @param realValue
     * @return
     */
    public static RuleResult buildUnHitRuleResult(String ruleName, String realValue, String desc) {
        RuleResult result = new RuleResult();
        result.setRealValue(realValue);
        result.setRuleName(ruleName);
        result.setPass(false);
        result.setDesc(desc);
        return result;
    }

    public static Long distinctCount(List<String> dataList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return 0L;
        }
        return dataList.stream().distinct().count();
    }

    /***
     * 将originalData中基础数据和comparingDataList 数据用equal进行比较
     * @param originalData 原始基础数据
     * @param comparingDataList 待比较数据
     * @return comparingDataList中数据和originalData中基础数据相同的个数[对comparingDataList 去重]
     */
    public static Long distinctEqualsCount(String originalData, List<String> comparingDataList) {
        if (StringUtils.isEmpty(originalData)) {
            return 0L;
        }
        if (CollectionUtils.isEmpty(comparingDataList)) {
            return 0L;
        }
        List<String> originList = Arrays.asList(originalData.split("#"));
        List<String> upperCaseOriginList = originList.stream().map(elem -> elem.toUpperCase())
                .collect(
                        Collectors.toList());
        return comparingDataList.stream().distinct()
                .filter(elem -> upperCaseOriginList.contains(elem.toUpperCase()))
                .count();
    }

    /***
     *
     * @param originalData 原始基础数据
     * @param comparingDataList 待比较数据
     * @return comparingDataList中数据和originalData中基础数据相童的个数[不对comparingDataList 去重]
     */
    public static Long equalsCount(String originalData, List<String> comparingDataList) {
        if (StringUtils.isEmpty(originalData)) {
            return 0L;
        }
        if (CollectionUtils.isEmpty(comparingDataList)) {
            return 0L;
        }
        List<String> originList = Arrays.asList(originalData.split("#"));
        List<String> upperCaseOriginList = originList.stream().map(elem -> elem.toUpperCase())
                .collect(Collectors.toList());
        return comparingDataList.stream()
                .filter(elem -> upperCaseOriginList.contains(elem.toUpperCase()))
                .count();
    }


    /***
     * contains比较
     * @param originalData 原始基础数据
     * @param comparingDataList 待比较数据
     * @return comparingDataList中数据包含基础数据originalData的个数[对comparingDataList 去重]
     */
    public static Long distinctContainsCount(String originalData, List<String> comparingDataList) {
        if (StringUtils.isEmpty(originalData)) {
            return 0L;
        }
        if (CollectionUtils.isEmpty(comparingDataList)) {
            return 0L;
        }
        List<String> originList = Arrays.asList(originalData.split("#"));
        List<String> lowerCaseOriginList = originList.stream().map(elem -> elem.toLowerCase())
                .collect(
                        Collectors.toList());

        return comparingDataList.stream().distinct()
                .filter(elem -> lowerCaseOriginList.stream()
                        .filter(e1 -> {
                            Pattern p = Pattern.compile(e1);
                            Matcher m = p.matcher(elem.toLowerCase());
                            return m.find();
                        }).count()
                        > 0)
                .count();
    }

    /**
     * 是否包含%（通配符）的方法
     *
     * @param e1   配置数据
     * @param elem 短信数据
     */
    private static Boolean containsPercentSignFun(String e1, String elem) {
        String per = ".*";
        if (e1.contains(per)) {
            return elem.matches(e1);
        } else {
            return elem.contains(e1);
        }
    }


    /***
     * contains比较
     * @param originalData 原始基础数据
     * @param comparingDataList 待比较数据
     * @return comparingDataList中数据包含基础数据originalData的个数[不对comparingDataList 去重]
     */
    public static Long containsCount(String originalData, List<String> comparingDataList) {
        if (StringUtils.isEmpty(originalData)) {
            return 0L;
        }
        if (CollectionUtils.isEmpty(comparingDataList)) {
            return 0L;
        }
        List<String> originList = Arrays.asList(originalData.split("#"));
        List<String> upperCaseOriginList = originList.stream().map(elem -> elem.toUpperCase())
                .collect(Collectors.toList());
        return comparingDataList.stream()
                .filter(elem ->
                        upperCaseOriginList.stream().filter(e1 -> elem.toUpperCase().contains(e1)).count()
                                > 0)
                .count();
    }


    public static Long containsCountWithPattern(String originalData,
                                                List<String> comparingDataList) {
        if (StringUtils.isEmpty(originalData)) {
            return 0L;
        }
        if (CollectionUtils.isEmpty(comparingDataList)) {
            return 0L;
        }
        List<String> originList = Arrays.asList(originalData.split("#"));

        return comparingDataList.stream().filter(elem -> {
            for (String works : originList) {
                Pattern p = Pattern.compile(works);
                String data = elem;
                Matcher matcher = p.matcher(data);
                if (matcher.find()) {
                    return true;
                }
            }
            return false;

        }).count();
    }

    public static boolean containsWithPattern(String originalData, String comparingData) {

        List<String> originList = Arrays.asList(originalData.split("#"));
        for (String works : originList) {
            Pattern p = Pattern.compile(works);
            Matcher matcher = p.matcher(comparingData);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    public static String extractPatternData(String patterns, String comparingData) {
        List<String> originList = Arrays.asList(patterns.split("#"));
        for (String works : originList) {
            Pattern p = Pattern.compile(works);
            Matcher matcher = p.matcher(comparingData);
            if (matcher.find()) {
                if (works.contains("(")) {
                    return matcher.group(1);
                } else {
                    return matcher.group();
                }
            }
        }
        return null;
    }


    /****i
     * comparingDataList 数据包含originalData中关键词的个数
     * @param originalData
     * @param comparingDataList
     * @return
     */
    public static Long distinctKeyWordsCount(String originalData, List<String> comparingDataList) {
        if (CollectionUtils.isEmpty(comparingDataList)) {
            return 0L;
        }
        if (StringUtils.isEmpty(originalData)) {
            return 0L;
        }
        List<String> originList = Arrays.asList(originalData.split("#"));
        List<String> upperCaseOriginList = originList.stream().map(elem -> elem.toUpperCase())
                .collect(Collectors.toList());
        Set<String> keyWordSet = new HashSet<>();
        comparingDataList.stream().forEach(
                elem -> {
                    //该条记录包含的keyword数据
                    Set<String> tmpSet = upperCaseOriginList.stream()
                            .filter(keyWord -> elem.toUpperCase().contains(keyWord)).collect(
                                    Collectors.toSet());
                    if (tmpSet != null && tmpSet.size() > 0) {
                        keyWordSet.addAll(tmpSet);
                    }
                });
        return keyWordSet.stream().count();
    }


    /****
     * comparingDataList 数据包含originalData中关键词的个数
     * @param originalData
     * @param comparingDataList
     * @return
     */
    public static Long distinctKeyWordsCountWithEqual(String originalData,
                                                      List<String> comparingDataList) {
        if (CollectionUtils.isEmpty(comparingDataList)) {
            return 0L;
        }
        if (StringUtils.isEmpty(originalData)) {
            return 0L;
        }
        List<String> originList = Arrays.asList(originalData.split("#"));
        List<String> upperCaseOriginList = originList.stream().map(elem -> elem.toUpperCase())
                .collect(Collectors.toList());
        Set<String> keyWordSet = new HashSet<>();
        comparingDataList.stream().forEach(
                elem -> {
                    //该条记录包含的keyword数据
                    Set<String> tmpSet = upperCaseOriginList.stream()
                            .filter(keyWord -> elem.toUpperCase().equals(keyWord)).collect(
                                    Collectors.toSet());
                    if (tmpSet != null && tmpSet.size() > 0) {
                        keyWordSet.addAll(tmpSet);
                    }
                });
        return keyWordSet.stream().count();
    }

    public static boolean equalsIgnoreCase(String realValue, String thresholdValue) {
        if (StringUtils.isEmpty(realValue) || "null".equalsIgnoreCase(realValue)) {
            return false;
        }
        return realValue.equalsIgnoreCase(thresholdValue);
    }


    public static boolean greatThan(String realValue, String thresholdValue) {
        if (StringUtils.isEmpty(realValue) || "null".equalsIgnoreCase(realValue)) {
            return false;
        }
        return new BigDecimal(realValue).compareTo(new BigDecimal(thresholdValue)) > 0;
    }


    public static boolean greatOrEqualThan(String realValue, String thresholdValue) {
        if (StringUtils.isEmpty(realValue) || "null".equalsIgnoreCase(realValue)) {
            return false;
        }
        return new BigDecimal(realValue).compareTo(new BigDecimal(thresholdValue)) >= 0;
    }


    public static boolean lessThan(String realValue, String thresholdValue) {
        try {
            if (StringUtils.isEmpty(realValue) || "null".equalsIgnoreCase(realValue)) {
                return false;
            }
            return new BigDecimal(realValue).compareTo(new BigDecimal(thresholdValue)) < 0;
        } catch (Exception e) {
            log.info("realValue: {}, thresholdValue: {}", realValue, thresholdValue);
            throw e;
        }
    }


    public static boolean lessOrEqualThan(String realValue, String thresholdValue) {
        if (StringUtils.isEmpty(realValue) || "null".equalsIgnoreCase(realValue)) {
            return false;
        }
        return new BigDecimal(realValue).compareTo(new BigDecimal(thresholdValue)) <= 0;
    }

    public static boolean equalString(String realValue, String thresholdValue) {
        if ("null".equals(realValue)) {
            return false;
        }
        if (StringUtils.isEmpty(thresholdValue)) {
            return false;
        }
        if (StringUtils.isEmpty(realValue)) {
            return false;
        } else {
            return realValue.equals(thresholdValue);
        }
    }

    public static boolean equalTo(String realValue, String thresholdValue) {
        if (StringUtils.isEmpty(realValue) || "null".equalsIgnoreCase(realValue)) {
            return false;
        }
        return new BigDecimal(realValue).compareTo(new BigDecimal(thresholdValue)) == 0;
    }

    public static String getRuleParam(String params, int index) {
        String[] paramArray = params.split("#");
        if (index >= paramArray.length) {
            return null;
        }
        return paramArray[index];
    }

    public static boolean constansStringIgnoreCase(String realValue, String thresholdValue) {
        if (StringUtils.isEmpty(realValue)) {
            return false;
        }
        if (StringUtils.isEmpty(thresholdValue)) {
            return false;
        }
        return Arrays.asList(thresholdValue.toUpperCase().split("#")).contains(realValue.toUpperCase());
    }

    public static boolean containsString(List<String> dataList, String elem) {
        return dataList.contains(elem);
    }

    public static boolean containsStringIgnoreCase(String realValue, String thresholdValue) {
        if (StringUtils.isEmpty(realValue)) {
            return false;
        }
        if (StringUtils.isEmpty(thresholdValue)) {
            return false;
        }
        return Arrays.asList(thresholdValue.toUpperCase().split("#")).contains(realValue.toUpperCase());
    }

    public static boolean containsKeywords(String realValue, String thresholdValue) {
        if (StringUtils.isEmpty(realValue)) {
            return false;
        }
        if (StringUtils.isEmpty(thresholdValue)) {
            return false;
        }
        Long count = containsCount(thresholdValue, Arrays.asList(realValue));
        return count != null && count > 0;
    }

    public static String subString(String str, int begin, int end) {
        if (begin < 0) {
            throw new IllegalArgumentException("the string beginIndex cannot less than zero");
        }
        if (begin > end) {
            throw new IllegalArgumentException("the string beginIndex cannot large than endIndex");
        }
        if (StringUtils.isEmpty(str)) {
            return null;
        } else {
            end = str.length() < end ? str.length() : end;
            return str.substring(begin, end);
        }
    }

    public static boolean isEmpty(String value) {
        return StringUtils.isEmpty(value);
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }


    /**
     * 拒绝路由：根据拒绝率拒人 当走这个方法之前的拒绝率>=refuseRate ---> 就通过，反之就拒绝
     */
    public static Boolean refuseJudge(Float refuseRate, RedisClient redisClient) {
        // 每进入这个方法就+1，存到redis
        Integer son = 0;
        Integer mom = 0;
        if (redisClient.get(RedisContants.RISK_REFUSE_JUDGE_MOM) == null) {
            redisClient.set(RedisContants.RISK_REFUSE_JUDGE_MOM, mom);
        }
        if (redisClient.get(RedisContants.RISK_REFUSE_JUDGE_SON) == null) {
            redisClient.set(RedisContants.RISK_REFUSE_JUDGE_SON, son);
        }
        Integer momTemp = Integer.parseInt(redisClient.get(RedisContants.RISK_REFUSE_JUDGE_MOM));
        redisClient.set(RedisContants.RISK_REFUSE_JUDGE_MOM, ++momTemp);
        if (Integer.parseInt(redisClient.get(RedisContants.RISK_REFUSE_JUDGE_MOM)) == 1) {
            Integer sonTemp = Integer
                    .parseInt(redisClient.get(RedisContants.RISK_REFUSE_JUDGE_SON));
            redisClient.set(RedisContants.RISK_REFUSE_JUDGE_SON, ++sonTemp);
            return false;// 拒绝
        }
        Float rate = NumberUtils
                .division(Integer.parseInt(redisClient.get(RedisContants.RISK_REFUSE_JUDGE_SON)),
                        Integer.parseInt(redisClient.get(RedisContants.RISK_REFUSE_JUDGE_MOM)));
        if (rate >= refuseRate) {
            return true;
        } else {
            Integer sonTemp = Integer
                    .parseInt(redisClient.get(RedisContants.RISK_REFUSE_JUDGE_SON));
            redisClient.set(RedisContants.RISK_REFUSE_JUDGE_SON, ++sonTemp);
            return false;
        }
    }


    /***
     * 对象转为string【直接使用string的String.valueOf 有时候重载不被drools正确识别】
     * @param obj
     * @return
     */
    public static String valueOfStr(Object obj) {
        return (obj == null) ? "null" : obj.toString();
    }


    public static String mobileFormat(String phone) {
        if (StringUtils.isEmpty(phone)) {
            return null;
        }
        Pattern p = Pattern.compile("\\d*$");
        Matcher matcher = p.matcher(phone);
        if (matcher.find()) {
            String matchPhone = matcher.group();
            if (matchPhone.startsWith("86")) {
                return matchPhone.substring(2);
            }
            return matcher.group();
        }

        return null;
    }


    /***
     *
     * @param variableName
     * @param variableThresholdName
     * @param score
     * @param realValue
     * @param applyProduct
     * @return
     */
    public static ScoreRuleResult buildScoreRuleResult(String variableName, String variableThresholdName, BigDecimal score, String realValue,
                                                       String applyProduct) {
        ScoreRuleResult result = new ScoreRuleResult();
        result.setVariableName(variableName);
        result.setVariableThresholdName(variableThresholdName);
        result.setScore(score);
        result.setRealValue(realValue);
        result.setModelName(applyProduct);
        return result;
    }

    /***
     * 目前系统生日可能是：dd/mm/yyyy or d/m/yyyy or dd-mm-yyyy
     * 统一格式为dd/mm/yyyy
     */
    public static String formatBirthday(String birthday) {
        if (StringUtils.isEmpty(birthday)) {
            return null;
        }
        String[] elements = null;
        if (birthday.contains("/")) {
            elements = birthday.split("/");
        } else if (birthday.contains("-")) {
            elements = birthday.split("-");
        }
        if (elements == null) {
            return null;
        }
        String result = "";
        if (elements[0].length() == 1) {
            result += "0" + elements[0];
        } else {
            result += elements[0];
        }
        result += "/";
        if (elements[1].length() == 1) {
            result += "0" + elements[1];
        } else {
            result += elements[1];
        }
        result += "/";
        result += elements[2];
        return result;
    }

    public static Integer strLen(String str){
        if(StringUtils.isEmpty(str)){
            return 0;
        }else{
            return str.length();
        }

    }


    public static Boolean lessThanDate(String realValue, String thresholdValue, String compareFormat) {
        if (StringUtils.isEmpty(realValue) || StringUtils.isEmpty(thresholdValue) || StringUtils.isEmpty(compareFormat)) {
            return false;
        }
        Date d1 = DateUtil.stringToDate(realValue, compareFormat);
        Date d2 = DateUtil.stringToDate(thresholdValue, compareFormat);
        if (d1 == null || d2 == null) {
            return false;
        }
        return d1.compareTo(d2) < 0;
    }

    public static Boolean greatOrEqualThanDate(String realValue, String thresholdValue, String compareFormat) {
        if (StringUtils.isEmpty(realValue) || StringUtils.isEmpty(thresholdValue) || StringUtils.isEmpty(compareFormat)) {
            return false;
        }
        Date d1 = DateUtil.stringToDate(realValue, compareFormat);
        Date d2 = DateUtil.stringToDate(thresholdValue, compareFormat);
        if (d1 == null || d2 == null) {
            return false;
        }
        return d1.compareTo(d2) >= 0;
    }

    public static Boolean lessOrEqualThanDate(String realValue, String thresholdValue, String compareFormat) {
        if (StringUtils.isEmpty(realValue) || StringUtils.isEmpty(thresholdValue) || StringUtils.isEmpty(compareFormat)) {
            return false;
        }
        Date d1 = DateUtil.stringToDate(realValue, compareFormat);
        Date d2 = DateUtil.stringToDate(thresholdValue, compareFormat);
        if (d1 == null || d2 == null) {
            return false;
        }
        return d1.compareTo(d2) <= 0;
    }





}
