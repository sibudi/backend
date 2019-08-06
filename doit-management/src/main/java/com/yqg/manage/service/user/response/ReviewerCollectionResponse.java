package com.yqg.manage.service.user.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 ****/


@Getter
@Setter
public class ReviewerCollectionResponse {
    private String postEnglishName;
    private String postName;
    private List<ManSysUserResponse> reviewers;
}
