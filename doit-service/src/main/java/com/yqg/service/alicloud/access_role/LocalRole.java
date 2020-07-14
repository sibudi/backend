package com.yqg.service.alicloud.access_role;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class LocalRole implements IAccessRoleHandler {
    private AliyunProfile aliyunProfile;
    public RoleType getRoleType() { return RoleType.LOCAL; }
    public String getAccessKeyId() { return this.aliyunProfile.getAccessKeyId(); }
    public String getAccessKeySecret() { return this.aliyunProfile.getAccessKeySecret(); }
    public String getSecurityToken() { return null; }
    public LocalDateTime getExpiration() { return LocalDateTime.now().plusDays(1); }

    public LocalRole(String profile_name) throws IOException {
        AliyunConfig localConfig = getAliyunConfig();
        this.aliyunProfile = localConfig.getProfiles().stream()
            .filter(entity -> entity.getName().equals(profile_name))
            .findFirst().orElseThrow(IndexOutOfBoundsException::new);
    }

    @SuppressWarnings("java:S106") // Initial configuration class, logging by System.out.println
    private AliyunConfig getAliyunConfig() throws IOException {
        String config_path = System.getProperty("user.home") + "/.aliyun/config.json";
        try (FileReader reader = new FileReader(config_path)) {
            return new Gson().fromJson(reader, AliyunConfig.class);
        } catch (FileNotFoundException | JsonIOException e) {
            log.error(String.format("!!!!! WARNING !!!!! Failed to read aliyun-cli config.json in: %s.", config_path),e);
            throw e;
        }
    }

    private static class AliyunConfig {
        private List<AliyunProfile> profiles;
        private List<AliyunProfile> getProfiles() { return this.profiles; }
    }
    private static class AliyunProfile {
        private String name;
        private String access_key_id;
        private String access_key_secret;
        public String getName() { return this.name; }
        public String getAccessKeyId() { return this.access_key_id; }
        public String getAccessKeySecret() { return this.access_key_secret; }
    }
}