package com.yqg.common.enums.system;

/**
 * Created by wanghuaizhou on 2019/2/28.
 */
public enum MoboxDataEnum {

    NPWP_DATA(1),       // NPWP
    BPJS_DATA(2),       // BPJS
    OPERATOR_DATA(3),   // 运营商
    LINKEDIN_DATA(4),   // LINKEDIN
    XDBB_FIR_DATA(5),   // 第一次信贷保镖
    XDBB_SECD_DATA(6),  // 第二次信贷保镖
    BLACK_BOX_DATA(7); //设备指纹

    private int type;

    private MoboxDataEnum(int type) {
        this.setType(type);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
