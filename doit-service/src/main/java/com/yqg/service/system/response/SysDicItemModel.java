package com.yqg.service.system.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
public class SysDicItemModel implements Serializable {
    private Integer id;
    private String dicId;//
    private String dicItemValue;//
    private String dicItemName;//
    private String language;//

}
