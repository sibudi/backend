package com.yqg.drools.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortMessage {

    //    private List<String> allSmsBody;
//    private List<String> recent30DaySmsBody;
//    private int recent15DaysMsgCount;

    private Long overdueWordsCount;//逾期词个数
    private Long negativeWordsCount;//负面词个数
    private Long interrelatedWordsCount;//同业词个数
    private Long totalCount;//短信总条数
    private Long recent90OverdueMsgDistinctCountByNumber;//近90天逾期短信号码数【按号码去重】
    private Long recent30OverdueMsgDistinctCountByNumber;//近30天逾期短信号码数

    private Long recent30RejectedMsgCount;//近30天被拒短信号码数
    private Long recent90TotalMsgCount;//近90天短信条数合计
    private Long recent30TotalMsgCount;//近30天短信合计
    private Long recent30TotalMsgWithPhoneCount;//近30手机号码发送的短信条数
    private Long recent90TotalMsgWithPhoneCount;//近90手机号码发送的短信条数
    private Long diffDaysForEarliestMsgAndApplyTime;//最早的一条短信距离申请的天数

    //
    private Long recent30OverdueMsgTotalCount;//近30天逾期短息累计条数
    private Long recent15TotalMsgWithPhoneCount;//近15天手机号码发送的短信条数

    private Integer morethan15Count;// 命中逾期大于15天的短信条数 SMS_OVERDUE_MORETHAN_15DAYS_COUNT
    private Integer lessthan15AndMoreThan10Count;// 短信出现逾期10-15（含）天的平台个数 SMS_OVERDUE_LESSTHAN_15DAYS_COUNT
    private Integer smsOverdueMaxDays;// 短信中出现的最大逾期天数 SMS_OVERDUE_MAX_DAYS


}
