package com.yqg.common.enums.order;

/**
 * ????
 * Created by Didit Dwianto on 2017/11/24.
 */
public enum OrdStepTypeEnum {
    CREAT_ORDER(0),// 创建订单
    CHOOSE_ROLE(1),// 选择角色
    IDENTITY(2), // 填写身份信息
    BASIC_INFO(3), //基本信息
    WORK_INFO(4), // 工作或学校或者家庭主妇信息
    CONTACT_INFO(5), //联系人信息
    CHECK_INFO(6), //验证信息
    BANK_INFO(7), //银行卡信息
    EXTRA_INFO(8); //额外的信息

    private int type;

    private OrdStepTypeEnum(int type) {
        this.setType(type);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
