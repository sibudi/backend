package com.yqg.manage.service.collection.response;

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

@Setter
@Getter
@ApiModel(description = "????????")
public class CollectionPostResponse {

    @ApiModelProperty("岗位id")
    private Integer postId;

    @ApiModelProperty("岗位名称")
    private String postName;

    @ApiModelProperty("催收范围")
    private String section;

    @ApiModelProperty("该岗位下催收人员列表")
    private List<CollectorResponseInfo> staff;

    @ApiModelProperty("该岗位下休息中的催收人员列表")
    private List<CollectorResponseInfo> restTaff;

}
