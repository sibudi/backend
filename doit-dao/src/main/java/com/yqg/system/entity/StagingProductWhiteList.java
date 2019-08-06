package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by wanghuaizhou on 2019/4/18.
 */
@Data
@Table("stagingProductWhiteList")
public class StagingProductWhiteList extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7283042615443997599L;

    private String userUuid;

    private String productUuid;

    private String beachId;

    private String ruleName;
}
