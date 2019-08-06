package com.yqg.manage.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 排除催收权限其他能够查询到订单详情页面的 角色列表
 * Author: tonggen
 * Date: 2019/5/14
 * time: 5:45 PM
 */
@Getter
public enum ManCollectorOperatorEnum {

    FIRST_CHECK(1),
    SECOND_CHECK(2),
    KEFU(5),
    REVIEWER_LEAR(4),
    SYS_ADMIN(6),
    OPERATOR(7),
    KEFU_LEAR(49),
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

    ManCollectorOperatorEnum(Integer desc){
        this.desc = desc;
    }
    private Integer  desc;

    public static List<Integer> listCollectorOperatorEnum() {
        return Arrays.asList(ManCollectorOperatorEnum.values()).stream()
                .map(ManCollectorOperatorEnum:: getDesc).collect(Collectors.toList());
    }
}
