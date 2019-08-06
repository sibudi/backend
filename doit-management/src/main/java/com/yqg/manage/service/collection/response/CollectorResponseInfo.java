package com.yqg.manage.service.collection.response;

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
 * ???????
 ****/

@Getter
@Setter
@ApiModel(description = "????????")
@AllArgsConstructor
@NoArgsConstructor
public class CollectorResponseInfo {

    @ApiModelProperty(value = "催收员id")
    private String code;
    @ApiModelProperty(value = "催收员姓名")
    private String name;

}
