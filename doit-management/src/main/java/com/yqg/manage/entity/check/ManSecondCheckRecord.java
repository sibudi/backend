package com.yqg.manage.entity.check;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Author: tonggen
 * Date: 2018/6/13
 * time: 下午2:31
 */
@Data
@Table("manSecondCheckRecord")
public class ManSecondCheckRecord extends BaseEntity implements Serializable {

    private String orderNo;

    private String userUuid;

    /**
     *订单复审标签，1表示稍后再审
     */
    private Integer operatorType;

}
