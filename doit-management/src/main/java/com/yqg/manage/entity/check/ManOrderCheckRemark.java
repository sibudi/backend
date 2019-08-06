package com.yqg.manage.entity.check;


import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author alan
 */
@Data
@Table("manOrderCheckRemark")
public class ManOrderCheckRemark extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 2137851924993493460L;

    private String orderNo;

    private Integer type;

    private Integer checkSuggest;

    private String burningTime;
}
