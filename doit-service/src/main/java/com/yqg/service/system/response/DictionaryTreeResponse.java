package com.yqg.service.system.response;


import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
public class DictionaryTreeResponse {
    private Integer id;

    private Date createTime;

    private String dicItemName;

    private String dicItemValue;

    private List<SysDicItemModel> children;

}
