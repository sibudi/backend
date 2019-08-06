package com.yqg.manage.service.collection.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@ApiModel
public class CollectorRequest {

    @ApiModelProperty(value = "是否第三方人员0:是,1:否")
    private Integer isThird;

    @ApiModelProperty(value = "催收人员岗位id")
    private Integer postId;

    @ApiModelProperty(value = "委外母账号ID")
    private Integer outSourceId;

    @ApiModelProperty(value = "是否判断催收员休息 0 ,否， 1，是")
    private Integer rest;

    @ApiModelProperty(value = "人员来源 0:催收人员分配; 1,质检人员分配(默认为0）")
    private Integer sourceType = 0;

}
