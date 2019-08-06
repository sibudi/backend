package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseCondition;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/7/30.
 */
@Data
@Table("uangMobiel")
public class UangMobiel extends BaseCondition{

    private Integer id;
    private String remark;
    private String mobielDes;

}
