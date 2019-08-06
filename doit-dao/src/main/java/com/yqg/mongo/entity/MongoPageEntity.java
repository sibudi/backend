package com.yqg.mongo.entity;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
public class MongoPageEntity<T> {
    Class<T> entityClass;
    private Date createStartTime;
    private Date createEndTime;
    private String lastPageMaxObjectId;
    private Integer pageSize;

    private Map<String,Object> paramMap;

}
