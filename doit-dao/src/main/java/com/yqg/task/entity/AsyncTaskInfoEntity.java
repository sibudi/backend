package com.yqg.task.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Table("asyncTaskInfo")
@Getter
@Setter
public class AsyncTaskInfoEntity extends BaseEntity {
    private String userUuid;
    private String orderNo;
    private Integer taskType;
    private Integer taskStatus;


    @Getter
    public enum TaskTypeEnum {
        CONTRACT_SIGN_TASK(1);

        TaskTypeEnum(int code) {
            this.code = code;
        }

        private int code;
    }

    @Getter
    public enum TaskStatusEnum {
        WAITING(0),
        PROCESSING(1),
        FINISHED(2);

        TaskStatusEnum(int code){
            this.code = code;
        }
        private int code;
    }
}
