package com.yqg.order.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Jacob
 * Date: 07/02/2018
 * Time: 12:50 PM
 */
@Data
@Table("ordPayCheck")
public class OrdPayCheck extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 4917044095644051791L;

    private String orderNo; //????
    private String userUuid; //??UUID
    private Integer status; //??????
    private Integer statusThird; //???????

    private BigDecimal amountApply; //????
    private BigDecimal amountApplyThird; //???????

    private Date lendingTime;  // ????
    private Date lendingTimeThird; // ???????

    private String disbursementId; //?????
    private Integer channel; //????

    private String errorCode; //??????
    private String errorMsg; //??????

}
