package com.yqg.drools.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.utils.DateUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/31
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Getter
@Setter
public class JobData {

    @JsonProperty(value = "education_background")
    private List<EducationBackGround> educationBackGrounds;

    @JsonProperty(value = "working_experience")
    private List<WorkingExperience> workingExperiences;

    @Getter
    @Setter
    public static class EducationBackGround{
        private String duration;
        private String major;
        private String university;
        private String degree;
        private String source;
    }


    @Getter
    @Setter
    public static class WorkingExperience{
        private String duration;

        @JsonProperty(value = "company_name")
        private String companyName;

        private String position;

        private String source;

        /***
         * 工作起始时间
         * @return
         */
        public Date getJobStartDate(){
            if(StringUtils.isEmpty(duration)){
                return null;
            }
            String[] dataArray = duration.split("-");

            if (dataArray.length > 0 && !dataArray[0].contains("N/A")) {
                return DateUtil.stringToDate(dataArray[0],DateUtil.FMT_DDMMYYYY);
            }
            return null;
        }

        /***
         * 工作截止时间
         * @return
         */
        public Date getJobEndDate(){
            if(StringUtils.isEmpty(duration)){
                return null;
            }
            String[] dataArray = duration.split("-");
            if(dataArray.length>1){
                return DateUtil.stringToDate(dataArray[1],DateUtil.FMT_DDMMYYYY);
            }
            return null;
        }
    }

}
