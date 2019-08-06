package com.yqg.service.p2p.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class P2PLoanResponse {
    private String borrowingPurpose; //用途
    private String borrowingTerm; //期限
    private BigDecimal amount;//金额
    private Integer status;//状态10:正常还款11:预期还款
}
