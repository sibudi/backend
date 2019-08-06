package com.yqg.manage.service.collection.request;

import com.yqg.manage.service.collection.response.CollectorResponseInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 ****/

@Getter
@Setter
@ApiModel
public class CollectionPostRequest {
    @ApiModelProperty(value = "催收岗位id")
    private Integer postId;

    @ApiModelProperty(value = "当前调度的催收人员列表")
    private List<CollectorResponseInfo> staff;

    @ApiModelProperty(value = "催收区间")
    private String section;

    @ApiModelProperty(value = "是否休息0 否，1是")
    private Integer rest;

    @ApiModelProperty(value = "人员来源 0:催收人员分配; 1,质检人员分配(默认为0）")
    private Integer sourceType = 0;
}
