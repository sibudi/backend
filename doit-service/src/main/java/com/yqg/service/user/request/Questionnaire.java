package com.yqg.service.user.request;

import lombok.Data;

import java.util.List;

/**
 * Author: tonggen
 * Date: 2019/6/20
 * time: 10:28 AM
 */
@Data
public class Questionnaire {
    private String fileUrl;

    private String attachType;
    private String comment;
}
