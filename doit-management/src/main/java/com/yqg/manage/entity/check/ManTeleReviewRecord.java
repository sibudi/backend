package com.yqg.manage.entity.check;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Jacob
 */
@Data
@Table("manTeleReviewRecord")
public class ManTeleReviewRecord extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -837019474006254772L;

    private String question;

    private String answer;

    private Integer result;

    private String userUuid;

    private String orderNo;

    /**
     * 1中文，2印尼文
     */
    private Integer langue;

    /**
     * 公司电核弹框中问题记录
     */
    private Integer manOrderRemarkId;

    /**
     * 拒绝原因描述
     */
    private String description;

}
