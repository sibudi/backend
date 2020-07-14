package com.yqg.service.alicloud.access_role;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InstanceRole implements IAccessRoleHandler {
    private static final String instance_role_endpoint = "http://100.100.100.200/latest/meta-data/Ram/security-credentials/";
    private OkHttpClient okHttpClient = new OkHttpClient();
    private String instanceRoleName;
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;
    private LocalDateTime expiration;
    public RoleType getRoleType() { return RoleType.INSTANCE; }
    public String getInstanceRoleName() { return this.instanceRoleName; }
    public String getAccessKeyId() { regenerateToken(); return this.accessKeyId; }
    public String getAccessKeySecret() { regenerateToken(); return this.accessKeySecret; }
    public String getSecurityToken() { regenerateToken(); return this.securityToken; }
    public LocalDateTime getExpiration() { return this.expiration; }

    @SuppressWarnings("java:S106") // Initial configuration class, logging by System.out.println
    public InstanceRole() throws RuntimeException {
        try {
            //region Get Instance Role Name
            Request first_request = new Request.Builder()
                    .url(instance_role_endpoint)
                    .build();
            Response first_response = okHttpClient.newCall(first_request).execute();
            //Somehow it return 2 values right now...
            this.instanceRoleName = first_response.body().string().split("\n")[0];
            //endregion Get Instance Role Name
            //Get Instance Role Credentials
            regenerateToken();
        } catch (IOException ioe) {
            log.error("IO Exception when getting Instance Role", ioe);
            throw new RuntimeException(ioe);
        } 
    }

    @SuppressWarnings("java:S106") // Initial configuration class, logging by System.out.println
    public void regenerateToken() throws RuntimeException {
        try {            
            if (this.getExpiration() == null
                            || LocalDateTime.now().minusMinutes(5).isAfter(this.getExpiration())) {               
                Request second_request = new Request.Builder()
                        .url(String.format("%s%s", instance_role_endpoint, this.getInstanceRoleName())).build();
                Response second_response = okHttpClient.newCall(second_request).execute();
                JSONObject jsonResult = new JSONObject(second_response.body().string());
                this.accessKeyId = jsonResult.getString("AccessKeyId");
                this.accessKeySecret = jsonResult.getString("AccessKeySecret");
                this.securityToken = jsonResult.getString("SecurityToken");
                this.expiration = LocalDateTime.parse(jsonResult.getString("Expiration"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
            }
        } catch (IOException ioe) {
            log.error("IO Exception when regenerating Instance Role", ioe);
            throw new RuntimeException(ioe);
        } 
    }
}