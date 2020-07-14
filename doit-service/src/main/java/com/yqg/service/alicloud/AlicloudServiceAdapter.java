package com.yqg.service.alicloud;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSException;
import com.yqg.service.alicloud.access_role.IAccessRoleHandler;
import com.yqg.service.alicloud.access_role.RoleService;
import com.yqg.service.alicloud.oss.OssService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AlicloudServiceAdapter {

    @Value("${alicloud.oss.endpoint}")
    private String ossEndpoint;
    @Value("${alicloud.oss.bucketName}")
    private String ossBucketName;
    @Value("${alicloud.runtime}")
    private String runtime;
    @Value("${alicloud.localProfile}")
    private String profile;
    
    private static RoleService role_service;
    private static Map<String, String> oss_config;

    @PostConstruct
    public void init() throws IOException {

        try {
            // region Get runtime Access Role
            switch (IAccessRoleHandler.RoleType.valueOf(runtime)) {
                case LOCAL:
                    log.info("Runtime: LOCAL");
                    role_service = new RoleService(profile);
                    break;
                case INSTANCE:
                    log.info("Runtime: INSTANCE");
                    role_service = new RoleService();
                    break;
                default:
                    throw new IllegalArgumentException(
                            String.format("Invalid bootstrap.properties - runtime: %s", runtime));
            }  
            // endregion Get runtime Access Role

            // region Get config 
            oss_config = new HashMap<String,String>();
            oss_config.put("endpoint", ossEndpoint);
            oss_config.put("bucket_name", ossBucketName);
            // endregion Get config 
        } catch (Exception ex) {
            String error_message = "Failed when initializing";
            log.error(error_message);
            throw ex;
        }
    }
    public byte[] getOssStreamData(String full_file_name) { 
        try (OssService oss_service = new OssService(null, role_service.getAccessRoleHandler(),
                oss_config.get("endpoint"), oss_config.get("bucket_name"))) {
            return oss_service.getStreamData(cleanupOldFilename(full_file_name));
        } catch (IOException ioe) {
            return null;
        } catch (OSSException oe) {
            return null;
        } catch (ClientException ce) {
            return null;
        }
    }
    public boolean isOssObjectExist(String full_file_name) {
        try (OssService oss_service = new OssService(null, role_service.getAccessRoleHandler(),
            oss_config.get("endpoint"), oss_config.get("bucket_name"))) {
            return oss_service.isExist(cleanupOldFilename(full_file_name));
        }
    }

    private String cleanupOldFilename(String full_file_name) {
        full_file_name = full_file_name.replace("/mnt/","/").replace("/MyUpload/","");
        return full_file_name;
    }
}