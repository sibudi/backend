package com.yqg.manage.service.user.response;

import com.yqg.manage.entity.user.ManUser;
import lombok.Data;

import java.util.List;

/**
 * Author: tonggen
 * Date: 2018/10/25
 * time: 2:34 PM
 */
@Data
public class TeamParentResponse {

    private Integer id;
    private String username;

    private List<ManUser> manUserList;
}
