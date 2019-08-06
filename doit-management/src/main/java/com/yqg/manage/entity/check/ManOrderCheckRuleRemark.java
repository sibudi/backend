package com.yqg.manage.entity.check;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author alan
 */
@Data
@Table("manOrderCheckRuleRemark")
public class ManOrderCheckRuleRemark extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 4803807279900762092L;

    private String orderNo;

    private Integer infoType;
}
