package com.yqg.drools.model;

import com.yqg.drools.beans.SnsData;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/*****
 * @Author zengxiangcai
 * Created at 2018/1/31
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Getter
@Setter
public class FaceBookModel {

    private int totalCommentCount;//总评论数
    private int recent2MonthCommentCount;//近两个月评论数
    private int currentMonthCommentCount;//当前月评论数
    private int totalLikesCount;//总点赞数
    private int recent2MonthLikesCount;//近2个月点赞数
    private int currentMonthLikesCount;//当前月点赞数
    private int totalPostCount;//总发帖数
    private int recent2MonthPostCount;//近2个月发帖数
    private int currentMonthPostCount;//当前月发帖数
    private BigDecimal monthAverageCommentCount;//月评论数
    private BigDecimal monthAverageLikesCount;//月平均点赞数
    private BigDecimal monthAveragePostCount;//月平均发帖数
    private int monthsWithPost;//总发帖月数【订单时间-首次发帖时间+1】
    private int monthsWithoutPost;//未发帖月数
    private Boolean academicDegreeNotSame;//学历不一致
    private Long diffDaysBetweenWorkStartAndOrderApply;//fb工作时间和订单申请时间的天数差
    private Boolean companyNameNotContain;//申请用户订单中的公司信息不再fb中的公司信息列表中

//    private Boolean hasTimeLines = false; //是否有timelines数据
//    private Boolean hasJob = false;//是否有job数据


}


