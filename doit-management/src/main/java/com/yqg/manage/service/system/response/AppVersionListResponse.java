package com.yqg.manage.service.system.response;

import com.yqg.system.entity.SysAppVersion;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*****
 * @Author Jacob
 * created at ${date}
 ****/

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class AppVersionListResponse extends SysAppVersion{

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String memo;

    @ApiModelProperty(value = "版本号")
    private String versionNo;

    @ApiModelProperty(value = "是否强制更新")
    private Integer isForce;

    @ApiModelProperty(value = "左边按钮")
    private String leftButton;

    @ApiModelProperty(value = "右边按钮")
    private String rightButton;

}
