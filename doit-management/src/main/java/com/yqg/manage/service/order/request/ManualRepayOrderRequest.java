package com.yqg.manage.service.order.request;

import com.yqg.common.models.BaseRequest;
import lombok.Data;

/**
 * Created by Didit Dwianto on 2018/2/14.
 */
@Data
public class ManualRepayOrderRequest extends BaseRequest {

    private String orderNo;
    private String userUuid;

    /**
     * 操作人Id
     */
    private Integer operatorId;

    /**
     * 实际还款金额
     */
    private String actualRepayAmout;

    /**
     * 用户手机号
     */
    private String mobile;

    /**
     * 查询联系人，区分类型 1 本人，2 备用，3 公司
     */
    private Integer type;

    /**
     * 查询还款的CODE
     */
    private String paymentCode;

    private Integer pageSize;

    private Integer pageNo;

    /**
     * 时间还款时间
     */
    private String actualRepayTime;

    /**
     * 用户拉黑的原因 1.客户本人原因 2.亲属代替申请  3.高风险客户 4.被盗用信息
     */
    private Integer addBlackReason;

    /**
     * 用户拉黑时填写的备注
     */
    private String addBlackRemark;
}
