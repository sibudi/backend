package com.yqg.manage.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@AllArgsConstructor
public class ReviewOrderAssignParam {

    private Integer reviewerId;
    private Integer operatorId;
    private String orderUUID;
    private String reviewerRole;

}
