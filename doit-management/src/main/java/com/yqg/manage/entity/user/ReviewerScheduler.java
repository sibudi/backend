package com.yqg.manage.entity.user;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Getter
@Setter
@Table("reviewerScheduler")
public class ReviewerScheduler extends BaseEntity implements Serializable {
    private Long reviewerId;
    private Integer status;
    private String reviewerRole;

    public enum ReviewerSchedulerStatusEnum {
        VALID(0),
        INVALID(1);

        ReviewerSchedulerStatusEnum(int flag) {
            this.flag = flag;
        }

        private int flag;
    }
}
