package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("usrFeedBack")
public class UsrFeedBack extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3466755431787510220L;

    private Integer id;

    private Integer disabled;

    private String uuid;

    private Date createTime;

    private Date updateTime;

    private Integer updateUser;

    private String remark;

    private String feedBackContent;

    private String feedBackImages;

    private String userUuid;

    private String userMobile;

    private Integer questionType;

    private Integer stageType;

    private String operatorName;

    private String netType;

    private String systemVersion;

    private String appVersion;

    private String clientType;

    private String channelSn;

    private String channelName;

    private String deviceId;

    private String resolution;

    private String iPAdress;

    /**
     * 数据来源 0 : 用户反馈; 1.催收投诉
     */
    private Integer sourceType;

    /**
     * 被投诉催收人名
     */
    private String collectionName;

    public enum ResolutionEnum {
        NO_SEE_IT(0,"",""),
        HAS_SOLVED(1,"已解决","sudah selesai"),
        WAIT_FOLLOW(2,"待跟进","lagi di followup"),
        FOLLOW_ING(3,"跟进中","Sedang di follow up"),
        NO_NEED_RESPONSE(4,"无须回复","tidak perlu di jawab");

        private Integer code;
        private String name;
        private String nameInn;

        ResolutionEnum(Integer code, String name, String nameInn) {
            this.code = code;
            this.name = name;
            this.nameInn = nameInn;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNameInn() {
            return nameInn;
        }

        public void setNameInn(String nameInn) {
            this.nameInn = nameInn;
        }
    }

}
