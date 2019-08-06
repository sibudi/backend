package com.yqg.manage.service.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.manage.enums.ReviewerPostEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *

 ****/


@Getter
@Setter
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewerSchedulerRequest {

    @ApiModelProperty("审核人员id列表")
    @JsonProperty
    private List<Integer> reviewerIds;

    private ReviewerPostEnum postEnglishName;
}
