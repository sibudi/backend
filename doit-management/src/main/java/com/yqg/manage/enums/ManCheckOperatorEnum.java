package com.yqg.manage.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 排除审核权限其他能够查询到订单详情页面的 角色列表
 * Author: tonggen
 * Date: 2019/5/14
 * time: 5:45 PM
 */
@Getter
public enum ManCheckOperatorEnum {

    KEFU(5),
    COLLECTOR(3),
    KEFU_LEAR(49),
    SYS_ADMIN(6),
    OPERATOR(7),
    BROWER_LEAR(8),
    CHECK_QUALITYER(13),
    WEIWAI_COMPANY(16),
    CHECK_OPERTATOR(20),
    LOOK_ALL_COLLECT_ORDERS(21),
    COLLECTOR_LIST(22),
    OPERATOR_CHECK(25),
    OUT_DATE_COLLECTOR(30),
    QUALITY_CHECKOR(36),
    QUALITY_CHECKER_LEAR(37),
    HAS_PAY_LOOKER(39),
    HAS_PAY_ADMIN(45),
    ALL_ORDERS_LIST(32);

    ManCheckOperatorEnum(Integer desc){
        this.desc = desc;
    }
    private Integer  desc;

    public static List<Integer> listCheckOperatorEnum() {
        return Arrays.asList(ManCheckOperatorEnum.values()).stream()
                .map(ManCheckOperatorEnum:: getDesc).collect(Collectors.toList());
    }

    public static List<Integer> listCheckOperatorEnumAddSC() {
        List<Integer> result = Arrays.asList(ManCheckOperatorEnum.values()).stream()
                .map(ManCheckOperatorEnum:: getDesc).collect(Collectors.toList());
        result.add(2);
        return result;

    }

}
