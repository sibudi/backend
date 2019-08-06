package com.yqg.manage.service.user.response;


import com.yqg.manage.entity.user.ManUser;
import lombok.Data;

import java.util.Date;

/**
 * @author alan
 */
@Data
public class ManSysUserResponse {
    private Integer id;

    private String uuid;

    private String username;

    private String password;

    private String realname;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String roles;

    private Integer third;

    public Integer getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRealname() {
        return realname;
    }

    public Integer getStatus() {
        return status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public String getRoles() {
        return roles;
    }

    public Integer getThird() {
        return third;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public void setThird(Integer third) {
        this.third = third;
    }


    public static ManSysUserResponse buildResponseUserProfileFromUserDetail(ManUser elem){
        ManSysUserResponse responseUser = new ManSysUserResponse();
        responseUser.setId(elem.getId());
        responseUser.setStatus(elem.getStatus());
        responseUser.setUuid(elem.getUuid());
        responseUser.setRealname(elem.getRealname());
        responseUser.setUsername(elem.getUsername());
        responseUser.setThird(elem.getThird());
        return responseUser;
    }
}
