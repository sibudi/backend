package com.yqg.manage.service.check.response;

import com.yqg.manage.entity.system.ManOrderRemark;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Jacob
 */
@Getter
@Setter
@ApiModel
public class ManOrderRemarkResponse extends ManOrderRemark{

    @ApiModelProperty(value = "公司电核对象")
    private String teleReviewObject;

    private Integer callResultType;  // 外呼结果大类 1 完全有效 2 可能有效 3无效

}
