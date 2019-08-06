package com.yqg.manage.entity.check;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Jacob
 */
@Data
@Table("manTeleReviewQuestion")
public class ManTeleReviewQuestion extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -837019474006254772L;


    private String code;

    private Integer type;

    private String question;

    private String answer;

    /**
     * 1中文，2印尼文
     */
    private Integer langue;

}
