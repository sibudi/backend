package com.yqg.manage.service.order.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.manage.util.CustomNULLDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@ApiModel
public class OrderSearchRequest {

    @ApiModelProperty(value = "订单号")
    private String uuid; //订单号

    @ApiModelProperty(value = "姓名")
    private String realName; //姓名

    @ApiModelProperty(value = "手机号")
    private String mobile;//手机号

    @ApiModelProperty(value = "申请期限")
    private Integer borrowingTerm;//申请期限

    @ApiModelProperty(value = "借款身份")
    @JsonDeserialize(using = CustomNULLDeserializer.UserRoleDeserializer.class, contentUsing = CustomNULLDeserializer.UserRoleDeserializer.class)
    private UserRoleEnum userRole;

    @ApiModelProperty(value = "订单渠道")
    private Integer channel;

    @ApiModelProperty(value = "借款申请日期-起始")
    private String createBeginTime; //申请日期开始时间

    @ApiModelProperty(value = "借款申请日期-截止")
    private String createEndTime;//申请日期结束时间

    @ApiModelProperty(value = "更新日期-起始")
    private String updateBeginTime; //申请日期开始时间

    @ApiModelProperty(value = "更新申请日期-截止")
    private String updateEndTime;//申请日期结束时间

    @ApiModelProperty(value = "分页查询当前页码从1开始")
    private Integer pageNo = 1; //当前查询的页码

    @ApiModelProperty(value = "分页页大小")
    private Integer pageSize = 10;//每页大小

    @ApiModelProperty(value = "是否展期")
    private Integer extendType;

    @ApiModelProperty(value = "是否结清")
    private Integer calType;

    @ApiModelProperty(value = "订单扩展标记")
    private Integer orderType;

    @ApiModelProperty(value = "订单状态")
    @JsonDeserialize(using = CustomNULLDeserializer.OrderStatusDeserializer.class, contentUsing = CustomNULLDeserializer.OrderStatusDeserializer.class)
    private OrdStateEnum status;//訂單狀態

    @ApiModelProperty(value = "人员来源 0:催收人员分配; 1,质检人员分配(默认为0）")
    private Integer sourceType = 0;

    @ApiModelProperty(value = "是否分期 1 是 ，2 否 ")
    private Integer isTerms = 0;

    @Getter
    public enum UserRoleEnum {
        Student(1),
        WORKING_STAFF(2),
        House_Wife(3);


        UserRoleEnum(int code) {
            this.code = code;
        }

        private int code;

        public static UserRoleEnum valueOf(int code) {
            List<UserRoleEnum> allValues = Arrays.asList(UserRoleEnum.values());
            Optional<UserRoleEnum> enumOptional = allValues.stream()
                    .filter(elem -> elem.getCode() == code).findFirst();
            if (enumOptional.isPresent()) {
                return enumOptional.get();
            }
            return null;
        }
    }

    @Getter
    public enum OrderChannelEnum {
        DEFAULT(0),
        Android(1),
        iOS(2);

        OrderChannelEnum(int code) {
            this.code = code;
        }

        private int code;

        public static OrderChannelEnum valueOf(int code) {
            List<OrderChannelEnum> allValues = Arrays.asList(OrderChannelEnum.values());
            Optional<OrderChannelEnum> enumOptional = allValues.stream()
                    .filter(elem -> elem.getCode() == code).findFirst();
            if (enumOptional.isPresent()) {
                return enumOptional.get();
            }
            return null;
        }
    }

    @Getter
    public enum SearchSourceEnum {
        REVIEW_ASSIGNMENT("审核模块-待分配订单列表"),

        REVIEW_FINISHED("已完成订单统计");

        SearchSourceEnum(String desc) {
            this.desc = desc;
        }

        private String desc;
    }
}
