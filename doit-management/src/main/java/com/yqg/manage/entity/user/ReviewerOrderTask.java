package com.yqg.manage.entity.user;

import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 ****/

@Getter
@Setter
public class ReviewerOrderTask extends BaseEntity implements Serializable {
    private String orderUUID;
    private Integer reviewerId;
    private Integer finish;
    private String reviewerRole;
}
