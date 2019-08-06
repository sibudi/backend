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
public class D0StatisticsItem {

    @ApiModelProperty(value = "已分配新用户")
    private Integer assignedReBorrowingCount0 = 0;

    @ApiModelProperty(value = "已分配复借1次订单数")
    private Integer assignedReBorrowingCount1 = 0;

    @ApiModelProperty(value = "已分配复借2次订单数")
    private Integer assignedReBorrowingCount2 = 0;

    @ApiModelProperty(value = "已分配复借3次订单数")
    private Integer assignedReBorrowingCount3 = 0;

    @ApiModelProperty(value = "已分配复借大于3次订单数")
    private Integer assignedReBorrowingCountN = 0;

    @ApiModelProperty(value = "已分配新用户")
    private Integer unAssignedReBorrowingCount0 = 0;

    @ApiModelProperty(value = "已分配复借1次订单数")
    private Integer unAssignedReBorrowingCount1 = 0;

    @ApiModelProperty(value = "已分配复借2次订单数")
    private Integer unAssignedReBorrowingCount2 = 0;

    @ApiModelProperty(value = "已分配复借3次订单数")
    private Integer unAssignedReBorrowingCount3 = 0;

    @ApiModelProperty(value = "已分配复借大于3次订单数")
    private Integer unAssignedReBorrowingCountN = 0;

    public boolean isElementValid() {
        if (unAssignedReBorrowingCount0 != null && unAssignedReBorrowingCount0 < 0) {
            return false;
        }
        if (unAssignedReBorrowingCount1 != null && unAssignedReBorrowingCount1 < 0) {
            return false;
        }
        if (unAssignedReBorrowingCount2 != null && unAssignedReBorrowingCount2 < 0) {
            return false;
        }
        if (unAssignedReBorrowingCount3 != null && unAssignedReBorrowingCount3 < 0) {
            return false;
        }
        return true;
    }


}
