package com.yqg.service.system.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
public class SysDistModel implements Serializable {
    private String uuid;
    private String distName;//????
    private String distCode;//????
    private String distLevel;//????
    private String parentCode;//??????
    private String language;//?? ??zh_CN ???NB

}
