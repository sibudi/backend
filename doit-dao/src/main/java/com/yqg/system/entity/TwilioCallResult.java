package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import com.yqg.common.utils.StringUtils;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Author: tonggen
 * Date: 2018/10/16
 * time: 上午9:53
 */
@Data
@Table("twilioCallResult")
public class TwilioCallResult extends BaseEntity implements Serializable {

    private String userUuid; //varchar(32) not null default '' comment '用户uuid',
    private String orderNo; // varchar(32) not null default '' comment '订单号',
    private String sid; // varchar(32) not null default '' comment '外呼的标识',
    private String duration; // varchar(32) not null default '' comment '通话时长',
    private Date startTime; //datetime comment '开始时间',
    private Date endTime; // datetime comment '结束时间',
    private String phoneNumber; // varchar(32) not null default '' comment '外呼人员的电话',
    private Integer callState; // tinyint not null default 0 comment '外呼状态  1 外呼已发送  2 呼叫完成（能够获取报告结果）3：发送外呼请求失败',
    private String callResult; // varchar(20) not null default '' comment '外呼的结果:queued,ringing,in-progress,completed,busy,failed,no-answer,
    private Integer callResultType; // int(4) not null default 0 comment '外呼结果大类 1 完全有效 2 可能有效 3无效'
    private BigDecimal price; //每条价格
    private String callPhase; //varchar(10) not null default '' comment '外呼的订单阶段 D-1, D-2,D-3',
    private Integer callPhaseType; // tinyint not null default 0 comment '外呼的阶段类型 1.全部；2.未接通订单',

    private String batchNo;//批次号

    private Integer callNode;   // 外呼节点 1 机审到初审  2初审到复审
    private Integer callType;    // 外呼手机号类型（// 1本人电话 2公司电话 3 紧急联系人 4 备选联系人)

    @Getter
    public enum CallStateEnum {
        //外呼状态  1 外呼已发送  2 呼叫完成（能够获取报告结果）3：发送外呼请求失败
        CALL_SEND(1),
        CALL_COMPLETE(2),
        CALL_SEND_ERROR(3);
        CallStateEnum(Integer code) {
            this.code = code;
        }
        private Integer code;
    }
    @Getter
    public enum CallPhaseTypeEnum {
        //外呼的阶段类型 1.全部；2.未接通订单
        CALL_PHASE_ALL(1),
        CALL_PHASE_NOT_RESPONSE(2);
        CallPhaseTypeEnum(Integer code) {
            this.code = code;
        }
        private Integer code;
    }

    public void setCallResultType() {
        if (StringUtils.isEmpty(this.callResult)) {
            return ;
        }
        switch (this.callResult) {
            case "busy":
            case "no-answer":
                this.callResultType = 2;
                break;
            case "canceled":
            case "failed":
                this.callResultType = 3;
                break;
            case "completed":
                this.callResultType = 1;
                break;
                default:
                    this.callResultType = 0;
//                    break;
        }

    }

    @Getter
    public enum CallTypeEnum{
        // 1本人电话 2公司电话 3 紧急联系人 4 备选联系人
        OWNER(1),
        COMPANY(2),
        EMERGENCY_LINKMAN(3),
        BACKUP_LINKMAN(4);
        CallTypeEnum(Integer code){
            this.code = code;
        }
        private Integer code;
    }

    @Getter
    public enum CallNodeEnum{
        //外呼节点 1 机审到初审  2初审到复审
        BETWEEN_MACHINE_CHECK_AND_FIRST_REVIEW(1),
        BETWEEN_FIRST_REVIEW_AND_SECOND_REVIEW(2);
        CallNodeEnum(Integer code){
            this.code = code;
        }
        private Integer code;
    }

}
