package com.yqg.mongo.entity;

import com.yqg.base.data.condition.BaseMongoEntity;
import com.yqg.base.mongo.anaotations.Table;
import lombok.Data;

/**
 * @author alan
 */
@Table("manOverDueOrderAnalysis")
@Data
public class ManOverDueOrderAnalysisMongo extends BaseMongoEntity {

    private String dueDay;

    private String dueOrderCount;

    private String notPaidCount;

    private String overDueRate;

    private String collectionStage;

    private String todayRepay;

}
