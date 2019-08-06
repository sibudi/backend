package com.yqg.service.check.response;

import lombok.Data;

/**
 * @program: microservice
 * @description: 录音文件下载实体
 * @author: 许金泉
 * @create: 2019-04-09 11:20
 **/
@Data
public class VoiceFileResponse {

    private String downurl;

    private String expiredtime;

}
