package com.yqg.manage.entity.collection;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import com.yqg.manage.enums.CollectionSmsType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: caomiaoke
 * Date: 23/03/2018
 * Time: 3:21 PM
 */

@Setter
@Getter
@Table("collectionSmsRecord")
public class CollectionSmsRecordEntity extends BaseEntity implements Serializable {


    private static final long serialVersionUID = 8260033412844197662L;

    private String orderNo;
    private String userUuid;
    private String sender;
    private Date   sendTime;
    private CollectionSmsType receiverType;
    private String smsTitle;
    private String smsContent;
}
