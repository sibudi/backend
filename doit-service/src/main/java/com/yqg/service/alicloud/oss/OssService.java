package com.yqg.service.alicloud.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.OSSObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import com.yqg.service.alicloud.logger.ILogHandler;
import com.yqg.service.alicloud.access_role.IAccessRoleHandler;


@Slf4j
public class OssService implements AutoCloseable {

    //ahalim: Use old log for now
    //private static ILogHandler logger;
    private static IAccessRoleHandler access_role;
    private static OSS oss_client;
    private static String bucket_name;
    private static String oss_endpoint;

    @Override
    @SuppressWarnings("java:S2093") // Implementation of AutoCloseable
    public void close() {
        oss_client.shutdown();
    }

    public OssService(ILogHandler _log_handler, IAccessRoleHandler _access_role, String _oss_endpoint, String _bucket_name) {
        switch (_access_role.getRoleType()) {
            case FUNCTION_COMPUTE:
            case INSTANCE:
                oss_endpoint = _oss_endpoint;
                break;
            case LOCAL: //Local must use internet endpoint
                oss_endpoint = _oss_endpoint.replace("-internal.aliyuncs.com", ".aliyuncs.com");
                break;
        }
        //ahalim: Use old log for now
        //logger = _log_handler;
        access_role = _access_role;
        bucket_name = _bucket_name;        
    }

    private static void getOssClient() {
        oss_client = new OSSClientBuilder().build(oss_endpoint, access_role.getAccessKeyId(),
                access_role.getAccessKeySecret(), access_role.getSecurityToken());
    }
    /**
     *  To fix OSS sdk bug, error if file name prefixes with /, event with is Exist method
     */
    private static String cleanupOssFilename(String full_file_name) {
        if (full_file_name.charAt(0) == '/') {
            full_file_name = full_file_name.substring(1);
        }
        return full_file_name;
    }
    public boolean isExist(String full_file_name) {
        full_file_name = cleanupOssFilename(full_file_name);
        getOssClient();
        return oss_client.doesObjectExist(bucket_name, full_file_name);
    }
    
    public byte[] getStreamData(String full_file_name) throws IOException {
        try {
            full_file_name = cleanupOssFilename(full_file_name);
            getOssClient();
            OSSObject oss_object = oss_client.getObject(bucket_name, full_file_name);
            return IOUtils.toByteArray(oss_object.getObjectContent());
        } catch (IOException ioe) {
            log.error("IO Exception - getStreamData:", ioe);
            throw ioe;
        } catch (OSSException oe) {
            log.error("OSS Server Exception - getStreamData:", oe);
            throw oe;
        } catch (ClientException ce) {
            log.error("OSS Client Exception - getStreamData:", ce);
            throw ce;
        }
    }

    public void putStreamData(String full_file_name, ByteArrayInputStream stream) throws IOException {
        try {
            getOssClient();
            oss_client.putObject(bucket_name, full_file_name, stream);
        } catch (OSSException oe) {
            log.error("OSS Server Exception - putStreamData:", oe);
            throw oe;
        } catch (ClientException ce) {
            log.error("OSS Client Exception - putStreamData:", ce);
            throw ce;
        }
    }

    /**
     * @param full_file_name  Full path of the file. (Ex: sample_dir/sample_data.csv) 
     * @param expiration_time Expiration time in second
     * @param headers         Optional headers (Ex: Content-Type: application/octet-stream)
     * @return
     */
    public String getDownloadSignedUrl(String full_file_name, int expiration_time,
            Map<String, String> headers) {
        return getSignedUrl(HttpMethod.GET, full_file_name, expiration_time, headers);
    }

    /**
     * @param full_file_name  Full path of the file. (Ex: sample_dir/sample_data.csv) 
     * @param expiration_time Expiration time in second
     * @param headers         Optional headers (Ex: Content-Type: application/octet-stream)
     * @return
     */
    public String getUploadSignedUrl(String full_file_name, int expiration_time,
        Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        if (!headers.containsKey("Content-Type")) {
            headers.put("Content-Type", "application/octet-stream");
        }
        return getSignedUrl(HttpMethod.PUT, full_file_name, expiration_time, headers);
    }

    /**
     * @param full_file_name  Full path of the file. (Ex: sample_dir/sample_data.csv) 
     * @param expiration_time Expiration time in second
     * @param headers         Optional headers (Ex: Content-Type: application/octet-stream)
     * @return
     */
    public String getSignedUrl(HttpMethod oss_http_method, String full_file_name, int expiration_time, Map<String,String> headers) {
        try {
            getOssClient();
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket_name, full_file_name, oss_http_method);
            if (headers != null) {
                request.setHeaders(headers);
            }
            request.setExpiration(new Date(new Date().getTime() + expiration_time * 1000));
            return oss_client.generatePresignedUrl(request).toString();
        } catch (ClientException ce) {
            log.error("OSS Client Exception:", ce);
            throw ce;
        }
    }

}