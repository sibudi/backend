package com.yqg.manage.service.collection.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@ApiModel
@Getter
@Setter
public class OverdueStatisticsItem {

    @ApiModelProperty(value = "已分配新用户催收订单合计")
    private Integer assignedReBorrowingCount0 = 0;

    @ApiModelProperty(value = "已分配老用户催收订单合计")
    private Integer assignedReBorrowingCountN = 0;

    @ApiModelProperty(value = "未分配新用户催收订单合计")
    private Integer unAssignedReBorrowingCount0 = 0;

    @ApiModelProperty(value = "未分配老用户催收订单合计")
    private Integer unAssignedReBorrowingCountN = 0;

    public boolean isElementValid() {
        if (unAssignedReBorrowingCount0 != null && unAssignedReBorrowingCount0 < 0) {
            return false;
        }
        if (unAssignedReBorrowingCountN != null && unAssignedReBorrowingCountN < 0) {
            return false;
        }
        return true;
    }
}
