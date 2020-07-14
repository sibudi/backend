package com.yqg.drools.model;

import java.math.BigDecimal;
import lombok.Data;

/**
 * Created by luhong on 2018/1/23.
 */
//ahalim: TODO remove keyword callrecord in drl
@Data
public class UserCallRecordsModel {
    private Long diffTime;//最早的一条短信距离申请的天数

    private Long recent180Time;//近180天通话时长
    private Long recent90Time;//近90天通话时长
    private Long recent30Time;//近30天通话时长

    private Long recent180Count;//近180天通话次数
    private Long recent90Count;//近90天通话次数
    private Long recent30Count;//近30天通话次数

    private Long recent180InTime;//近180天打入通话时长
    private Long recent90InTime;//近90天打入通话时长
    private Long recent30InTime;//近30天打入通话时长

    private Long recent180InCount;//近180天打入通话次数
    private Long recent90InCount;//近90天打入通话次数
    private Long recent30InCount;//近30天打入通话次数

    private Long recent180OutTime;//近180天打出通话时长
    private Long recent90OutTime;//近90天打出通话时长
    private Long recent30OutTime;//近30天打出通话时长

    private Long recent180OutCount;//近180天打出通话次数
    private Long recent90OutCount;//近90天打出通话次数
    private Long recent30OutCount;//近30天打出通话次数

    private Long firstContactDiffDay;//最后一次跟第一联系人有效通话时间距提交订单的天数
    private Long secondContactDiffDay;//最后一次跟第二联系人有效通话时间距提交订单的天数
    private Long lastContactDiffDay;//最后一次通话距提交订单日期的天数

    private Float recent90InRate;//近90天呼入占比
    private Float recent90OutRate;//近90天呼出占比
    private Float recent30InRate;//近30天呼入占比
    private Float recent30OutRate;//近30天呼出占比

    private Long recent180InNoCount;//近180天呼入未接听次数
    private Long recent90InNoCount;//近90天呼入未接听次数
    private Long recent30InNoCount;//近30天呼入未接听次数

    private Float recent180InNoRate;//近180天打入未接听占比
    private Float recent90InNoRate;//近90天打入未接听占比
    private Float recent30InNoRate;//近30天打入未接听占比

    private Float nightCallRate;// 近30天内夜间活跃占比

    private Float recent180CallRate;// 近180天内未接通电话占比
    private Float recent90CallRate;// 近90天内未接通电话占比
    private Float recent30CallRate;// 近30天内未接通电话占比

    private Long recent30NotConnectedCallCount;//近30天内未接通电话次数

    private boolean hasContact=false;//是否有通讯录信息
    private Long contacts;//近30天的通话记录中，出现在通讯录中的有效去重通话号码个数

    private Float contactsRate;//近30天的通话记录中，出现在通讯录中的有效去重通话号码占比

    private Long recent90CallRelaCount;//近90天内与亲属称谓联系人的通话次数
    private Long recent30CallRelaCount;//近30天内与亲属称谓联系人的通话次数

    private Integer recent90NoCallDay;// 近30天无通话记录总天数
    private Integer recent30NoCallDay;// 近90天无通话记录总天数
    private Integer recent180NoCallDay;//180天内的无通话天数

    private Long firstCall180Time; // 第一联系人,近180天,通话时长
    private Long firstCall180Count;// 第一联系人,近180天,通话次数

    private Long firstCall90Time; // 第一联系人,近90天,通话时长
    private Long firstCall90Count;// 第一联系人,近90天,通话次数

    private Long firstCall30Time; // 第一联系人,近30天,通话时长
    private Long firstCall30Count;// 第一联系人,近30天,通话次数

    private Long secondCall180Time; // 第二联系人,近180天,通话时长
    private Long secondCall180Count;// 第二联系人,近180天,通话次数

    private Long secondCall90Time; // 第二联系人,近90天,通话时长
    private Long secondCall90Count;// 第二联系人,近90天,通话次数

    private Long secondCall30Time; // 第二联系人,近30天,通话时长
    private Long secondCall30Count;// 第二联系人,近30天,通话次数

    private Boolean firstLinkManIn; // 第一联系人,在通话记录中，为true，反之为false
    private Boolean secondLinkManIn;// 第二联系人,在通话记录中，为true，反之为false


    //复借相关
    private BigDecimal recent15EveningActiveRatio = BigDecimal.ZERO;//近15天夜间活跃占比


    private Long recent30CallOutPhones;//近30天主叫号码数
    private Long recent30CallInPhones;//近30天被叫号码数

    private Long recent30DistinctCallNumbers;//近30天通话号码去重个数
    private Long recent90DistinctCallNumbers;//近90天通话号码去重个数
    private Long recent30DistinctCallInNumbers;//近30天被叫号码去重个数
    private Long recent90DistinctCallInNumbers;//近90天被叫号码去重个数
    private BigDecimal recent90StrangeNumberMissedCallRatio;//近90天通话记录中陌生号码打入未接通占比  =打入未接通的陌生号码/所有打入的陌生号码



}
