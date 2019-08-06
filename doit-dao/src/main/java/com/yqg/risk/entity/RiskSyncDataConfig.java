package com.yqg.risk.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RiskSyncDataConfig {
    private Date startTime;
    private Date endTime;
}
