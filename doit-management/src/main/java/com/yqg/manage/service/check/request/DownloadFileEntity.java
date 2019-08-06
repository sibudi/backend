package com.yqg.manage.service.check.request;

import lombok.Data;

import java.io.InputStream;

/**
 * @program: microservice
 * @description: 下载文件时的实体类
 * @author: 许金泉
 * @create: 2019-04-04 11:07
 **/
@Data
public class DownloadFileEntity {

    private String fileName;

    private InputStream inputStream;

    private String contentType;


}
