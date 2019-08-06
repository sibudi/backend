package com.yqg.order.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by wanghuaizhou on 2018/7/25.
 */
@Data
public class XiaoMiRecord {

    /**
     *
     "imeimd5": "27c5f441176dc7fe28ff7226fdc7a016",
     "user_name": "Shalini D",
     "phone_no": "7795566780",
     "date_of_birth": "23/11/1989",
     "order_id": "dafdahkehak3141hkdah",
     "business_type": "1",
     "order_status": "01",
     "order_time": "1526442771",
     "order_amount": "5000 ",
     "order_tenure": "15",
     "order_due_time": "1526443771",
     "order_repay_time": "23/05/2018",
     "overdue_days ": "",
     "overdue_amount ": "",
     "clear_time": ""
     * */

    private String imeimd5;

    @JsonProperty(value = "user_name")
    private String userName;

    @JsonProperty(value = "phone_no")
    private String phoneNo;

    @JsonProperty(value = "date_of_birth")
    private String dateOfBirth;

    @JsonProperty(value = "order_id")
    private String orderId;

    @JsonProperty(value = "business_type")   //  1 - Cash Loan
    private String businessType = "1";

    /**
     01-Approved
     02-Rejected
     03-Eligible (ready to apply)
     04-Manual
     Leave
     * */
    @JsonProperty(value = "order_status")
    private String orderStatus;

    @JsonProperty(value = "order_time")
    private String orderTime = "";

    @JsonProperty(value = "order_amount")
    private String orderAmount = "";

    @JsonProperty(value = "order_tenure")
    private String orderTenure = "";

    @JsonProperty(value = "order_due_time")
    private String orderDueTime = "";

    @JsonProperty(value = "order_repay_time")
    private String orderRepayTime = "";

    @JsonProperty(value = "overdue_days")
    private String overdueDays = "0";

    @JsonProperty(value = "overdue_amount")
    private String overdueAmount = "";

    @JsonProperty(value = "clear_time")
    private String clearTime = "";

    @JsonProperty(value = "phone_brand")
    private String phoneBrand = "";


}
