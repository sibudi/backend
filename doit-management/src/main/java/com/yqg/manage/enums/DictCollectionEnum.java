package com.yqg.manage.enums;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 *
 ****/
public enum DictCollectionEnum {

    REVIEWER_ROLE_RELATION("审核人员角色关系"),
    REVIEWER_APPLY_PARAM("审核人员审核订单申请参数"),
    REVIEWER_APPLY_PARAM_MAX_COUNT("最大申请数量"),
    COLLECTION_POST("催收岗位集合"),//记录角色下的所有岗位
    COLLECTION_ROLE_PARAM("催收角色参数"),//记录催收岗的角色id

    THIRD_COMPANY("催收公司"),//委外公司母账号

    QUALITY_CHECK_POST("质检岗位集合");

    DictCollectionEnum (String desc){
        this.desc = desc;
    }
    private String  desc;
}
