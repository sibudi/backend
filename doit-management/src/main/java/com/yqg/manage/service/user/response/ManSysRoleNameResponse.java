package com.yqg.manage.service.user.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;

/**
 * @author Jacob
 */
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ManSysRoleNameResponse {

    /**
     */
    private Boolean isRepeat;

    public Boolean getRepeat() {
        return isRepeat;
    }

    public void setRepeat(Boolean repeat) {
        isRepeat = repeat;
    }


}
