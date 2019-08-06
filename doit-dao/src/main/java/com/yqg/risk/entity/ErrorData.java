package com.yqg.risk.entity;

import com.yqg.base.data.condition.BaseCondition;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Jeremy Lawrence on 2018/2/12.
 */

@Getter
@Setter
public class ErrorData extends BaseCondition {
  private String userUuid;
  private String orderNo;
}
