package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseCondition;
import com.yqg.base.data.condition.BaseEntity;
import com.yqg.common.models.BaseRequest;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("ordOrder")
public class OrdOrder extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 6491820553537284577L;
    private String productUuid;//
    private Integer status;//
    private Integer orderStep; //
    private BigDecimal amountApply;//
    private Integer borrowingTerm;//
    private BigDecimal serviceFee;//
    private BigDecimal interest;//
    private Integer borrowingCount;//
    private String channel;//???? (2=iOS,1=???0=??)
    private String payChannel;//????
    private String orderPositionId;//????id
    private String userUuid;//??UUID
    private String userBankUuid;//??????UUID
    private Date applyTime;//???? 1
    private Date lendingTime;//????
    private Date refundTime;//?????
    private Date actualRefundTime;//??????
    private Integer firstChecker;//????
    private Integer secondChecker;//????

    private BigDecimal lendingRating;//借款评分
    private Integer overOneApply;//是否多头借贷
    private String requestGuarantee;// 抵押物
    private Integer paymentFrequencyType;//还款频率类型 (1 = daily, 2 = weekly, 3 = monthly, 4 = quarter, 5 = semester, 6 = yearly)
    private Integer paymentType;// 还款方式（1 = amortization, 2= bullet payment, 3 = discount, 4 = grace period, 5 = other）
    private String otherLoaninformation;// 剩余借款信息
    private Integer loanStatus;//放款状态 （1、未逾期 2、已逾期 3、坏账）
    private String approvedAmount;// 审批通过金额
    private String markStatus;  //标状态 （0.未发标 1.投标中 2.放款中 3.放款成功 4.放款失败 5.还款处理中 6.还款成功 7.还款失败 8.流标）
    private BigDecimal score;// 订单打分


    private String orderType;// 订单类型 0 普通订单 1 展期订单 2 部分结清订单 3 分期订单

    private Integer thirdType;  // 默认0 第三方订单 1 cashcash 2 cheetah 猎豹金融

    private Integer totalTerm;  // 总借款天数  只有分期订单才有

    //对应于markStatus
    @Getter
    public enum P2PLoanStatusEnum {
        INIT("0"), //初始化默认值
        FUNDING("1"),//投标中
        ISSUING("2"),//放款中
        ISSUED("3"),//放款成功
        ISSUE_FAILED("4"),//放款失败
        REPAYMENT_PENDING("5"),//还款处理中
        REPAYMENT_SUCCESS("6"),//还款成功
        REPAYMENT_FAILED("7"),//还款失败
        MISS_FUNDING("8"), //流标
        SEND_2_P2P_SUCCESS("20"), //推送到p2p成功
        SEND_2_P2P_FAILED("21"),//推送到p2p失败
        ;

        P2PLoanStatusEnum(String statusCode) {
            this.statusCode = statusCode;
        }
        private String statusCode;

        public static P2PLoanStatusEnum getEnumFromValue(String code) {
            P2PLoanStatusEnum allEnums[] = P2PLoanStatusEnum.values();
            for (P2PLoanStatusEnum enumItem : allEnums) {
                if (enumItem.getStatusCode().equals(code)) {
                    return enumItem;
                }
            }
            return null;
        }
    }
}
