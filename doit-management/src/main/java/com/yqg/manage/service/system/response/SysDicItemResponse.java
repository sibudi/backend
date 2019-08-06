package com.yqg.manage.service.system.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class SysDicItemResponse {
    @ApiModelProperty(value = "字典数据id")
    private Integer id;

    @ApiModelProperty(value = "字典数据父id")
    private String dicId;

    @ApiModelProperty(value = "字典数据名")
    private String dicItemName;

    @ApiModelProperty(value = "字典数据值")
    private String dicItemValue;
}
