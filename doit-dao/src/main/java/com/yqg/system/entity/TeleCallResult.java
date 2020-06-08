package com.yqg.system.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.*;

import java.io.Serializable;

/**
 * Created by wanghuaizhou on 2018/8/28.
 */
@Data
@Table("teleCallResult")
public class  TeleCallResult extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5034845040389770495L;

    private String orderNo;
    private String userUuid;
    private String tellNumber;  // 外呼的号码
    private Integer callNode;   // 外呼节点 1 机审到初审  2初审到复审
    private Integer callType;    // 外呼手机号类型（// 1本人电话 2公司电话 3 紧急联系人 4 备选联系人)

    private Integer callState;   // call status 0 Not called 1 Called 2 Call completed (capable of obtaining report results) 3 Outgoing call failed
    private String callBulkId;  // bulkId (用来获取报告)
    private String callMsgId;  // messageId （用来获取报告）
    private Integer callResult;  // 外呼结果 （1.接通  占线  接通 未接通  其他）
    private Integer callResultType;  // 外呼结果大类 1 完全有效 2 可能有效 3无效
    private Integer errorGroupId;  // 报告的Error 的 groupId
    private Integer errorId;  // 报告的ErrorId

    private String callBeginTime;   // 外呼开始时间
    private String callEndTime;  // 外呼结束时间
    private Integer callDuration;  // 外呼时长
    private String  callResponse;  // 整个响应结果
    private String remark;




    public boolean isCallFinished() {
        return CallStatusEnum.CALL_ERROR.getCode().equals(this.callState) || CallStatusEnum.CALL_FINISHED.getCode().equals(this.callState);
    }

    public boolean isCallValid(){
        return CallResultTypeEnum.VALID.getCode().equals(this.getCallResultType());
    }

    public boolean isCallInvalid(){
        return CallResultTypeEnum.INVALID.getCode().equals(this.getCallResultType());
    }

    //人工接通
    public boolean isCallReceived() {
        return this.errorId != null && errorId.intValue() == 5000;
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
    public enum CallStatusEnum{
        // Outgoing call status (0) Not called (1) Called (2) Completed (capable of obtaining report results) (3) Failed
        INIT(0),
        CALL_SEND(1),
        CALL_FINISHED(2),
        CALL_ERROR(3);
        CallStatusEnum(Integer code){
            this.code = code;
        }
        private Integer code;
    }

    @Getter
    public enum CallResultTypeEnum{
        // Outgoing call result category
        VALID(1),
        NOT_SURE(2),
        INVALID(3);
        CallResultTypeEnum(Integer code){
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
