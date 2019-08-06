package com.yqg.manage.service.system.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Didit Dwianto on 2018/1/7.
 */
@Data
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class DownLoadRequest {
    @ApiModelProperty(value = "用户session")
    @JsonProperty
    private String sessionId;

//    @ApiModelProperty(value = "文件")
//    @JsonProperty
//    private MultipartFile file;

    @ApiModelProperty(value = "类型")
    @JsonProperty
    private String type;

}
