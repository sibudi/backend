package com.yqg.manage.entity.collection;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: caomiaoke
 * Date: 23/03/2018
 * Time: 3:09 PM
 */

@Setter
@Getter
@Table(value = "smsTemplate")
public class SmsTemplateEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -7756652869034667672L;

    private String smsTemplateId;
    private String smsTitle;
    private String smsContent;
}
