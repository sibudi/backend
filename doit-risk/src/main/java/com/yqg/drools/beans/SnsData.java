package com.yqg.drools.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.utils.DateUtil;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/31
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Setter
@Getter
public class SnsData {

    @JsonProperty(value = "person_basic_info")
    private List<SnsPersonBasicInfo> personBasicInfo;

    @JsonProperty(value = "time_line")
    private List<TimeLine> timeLines;

    @Getter
    @Setter
    public static class SnsPersonBasicInfo {

        @JsonProperty(value = "photo_count")
        private Integer photoCount;

        private String name;

        @JsonProperty(value = "post_count")
        private Integer postCount;

        private String source;

        @JsonProperty(value = "friend_count")
        private Integer friendCount;
    }

    @Getter
    @Setter
    public static class TimeLine {

        @JsonProperty(value = "comment_count")
        private Integer commentCount;

        @JsonProperty(value = "likes_count")
        private Integer likesCount;

        @JsonProperty(value = "photo_count")
        private Integer photoCount;

        private String month;

        @JsonProperty(value = "post_count")
        private Integer postCount;


        public Date getTimeLineDate() {
            if (StringUtils.isEmpty(month)) {
                return null;
            }
            return DateUtil.stringToDate(month, DateUtil.FMT_MMYYYY);
        }
    }
}
