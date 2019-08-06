package com.yqg.drools.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 *
 ****/

@Getter
@Setter
public class JuXinLiData {
    private String code;
    private String message;
    private DataDetail data;


    @Getter
    @Setter
    public static class DataDetail{
        @JsonProperty(value = "datasource_status")
        private List<DataSourceStatus> dataSourceStatus;

        @JsonProperty(value = "report_data")
        private ReportData reportData;

        @Getter
        @Setter
        public static class DataSourceStatus {

            private String website;
            private String authToken;
            private String title;
            private String iconName;
            private String runningStatus;
            private String status;
        }

        @Getter
        @Setter
        public static class ReportData{
            private RideData rides;

            @JsonProperty(value = "e_commerce")
            private ECommerceData eCommerceData;

            private SnsData sns;

            private JobData job;
        }

    }



    @Getter
    public enum WebSiteSourceEnum{
        FACEBOOK("facebook"),
        TOKOPEDIA("tokopedia"),
        GOJEK("gojek");

        WebSiteSourceEnum(String website){
            this.website = website;
        }
        private String website;

    }
}
