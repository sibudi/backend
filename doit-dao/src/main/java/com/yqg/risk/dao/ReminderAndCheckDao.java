package com.yqg.risk.dao;

import com.yqg.risk.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by Didit Dwianto on 2018/2/8.
 */
@Mapper
public interface ReminderAndCheckDao {


    //1.1 审核人员完成情况
    @Select("select\n" +
            "ifnull(createDay,'') as createDay,                                                        -- 日期\n" +
            "ifnull(realname,'Total') as realName,                                                     -- 审核员\n" +
            "juniorTask,                                                                               -- 初审分配数\n" +
            "juniorFinish,                                                                             -- 初审完成数\n" +
            "juniorFinish - juniorRefuse as juniorPass,                                                -- 初审通过数\n" +
            "ifnull(concat(round(juniorFinish/juniorTask*100,2),'%'),'-') as juniorFinishRate,         -- 初审完成率\n" +
            "ifnull(concat(round(100 - juniorRefuse/juniorFinish*100,2),'%'),'-') as juniorPassRate,   -- 初审通过率\n" +
            "seniorTask,                                                                               -- 复审分配数\n" +
            "seniorFinish,                                                                             -- 复审完成数\n" +
            "seniorFinish - seniorRefuse as seniorPass,                                                -- 复审通过数\n" +
            "ifnull(concat(round(seniorFinish/seniorTask*100,2),'%'),'-') as seniorFinishRate,         -- 复审完成率\n" +
            "ifnull(concat(round(100 - seniorRefuse/seniorFinish*100,2),'%'),'-') as seniorPassRate    -- 复审通过率\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "realname,\n" +
            "sum(case when reviewerRole = 'JUNIOR_REVIEWER' then 1 else 0 end) as juniorTask,\n" +
            "sum(case when reviewerRole = 'JUNIOR_REVIEWER' and finish = 1 then 1 else 0 end) as juniorFinish,\n" +
            "sum(case when reviewerRole = 'JUNIOR_REVIEWER' and finish = 1 and status in (13,15) then 1 else 0 end) as juniorRefuse,\n" +
            "sum(case when reviewerRole = 'SENIOR_REVIEWER' then 1 else 0 end) as seniorTask,\n" +
            "sum(case when reviewerRole = 'SENIOR_REVIEWER' and finish = 1 then 1 else 0 end) as seniorFinish,\n" +
            "sum(case when reviewerRole = 'SENIOR_REVIEWER' and finish = 1 and status in(13,14) then 1 else 0 end) as seniorRefuse\n" +
            "from  \n" +
            "(\n" +
            "select\n" +
            "orderUUID,\n" +
            "createTime,\n" +
            "reviewerId,\n" +
            "finish,\n" +
            "reviewerRole\n" +
            "from reviewerOrderTask\n" +
            "where status = 0\n" +
            "and date(createTime) between date_sub(curdate(),interval 1 day) and curdate()\n" +
            ") Task\n" +
            "left join\n" +
            "(\n" +
            "select\n" +
            "id,\n" +
            "realname\n" +
            "from \n" +
            "manUser\n" +
            ") man on man.id = Task.reviewerId\n" +
            "left join\n" +
            "(\n" +
            "select\n" +
            "uuid,\n" +
            "status\n" +
            "from \n" +
            "ordOrder\n" +
            ") ord on ord.uuid = Task.orderUUID\n" +
            "group by\n" +
            "createDay,\n" +
            "realname with rollup\n" +
            "having createDay is not null xor realName is null\n" +
            ") reviewer;")
    List<ReminderAndCheck> getCheckerDailyTaskAndCompletion();


    // 1.1.2
    @Select("select               \n" +
            "createDay,           -- 日期\n" +
            "type,                -- 类型\n" +
            "noTaskNum            -- 未分配单量\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "createDay,     \n" +
            "'初审' as type,\n" +
            "count(orderId) as noTaskNum\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "createDay,orderId\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(createtime) as createDay,status,orderId\n" +
            "from ordHistory\n" +
            "where disabled = 0 and status = 3 and date(createTime) >= '2018-02-01'\n" +
            ") History\n" +
            "left join   \n" +
            "(\n" +
            "select uuid\n" +
            "from ordOrder\n" +
            "where disabled = 0 and status >= 4 and amountApply in (600000,800000,1000000)\n" +
            ") ord on History.orderId = ord.uuid\n" +
            "where uuid is null\n" +
            ") a\n" +
            "left join\n" +
            "(\n" +
            "select\n" +
            "orderUUID\n" +
            "from reviewerOrderTask\n" +
            "where status = 0\n" +
            "group by orderUUID\n" +
            ") b on b.orderUUID = a.orderId\n" +
            "where orderUUID is null\n" +
            "group by createDay\n" +
            "union all\n" +
            "select\n" +
            "createDay,\n" +
            "'复审' as type,\n" +
            "count(orderId) as noTaskNum\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "createDay,orderId\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(createtime) as createDay,status,orderId\n" +
            "from ordHistory\n" +
            "where disabled = 0 and status = 4 and date(createTime) >= '2018-02-01'\n" +
            ") History\n" +
            "left join   \n" +
            "(\n" +
            "select uuid\n" +
            "from ordOrder\n" +
            "where disabled = 0 and status >= 5 and amountApply in (600000,800000,1000000)\n" +
            ") ord on History.orderId = ord.uuid\n" +
            "where uuid is null\n" +
            ") a\n" +
            "left join\n" +
            "(\n" +
            "select\n" +
            "orderUUID\n" +
            "from reviewerOrderTask\n" +
            "where status = 0 and reviewerRole = 'SENIOR_REVIEWER'\n" +
            "group by orderUUID\n" +
            ") b on b.orderUUID = a.orderId\n" +
            "where orderUUID is null\n" +
            "group by\n" +
            "createDay\n" +
            ") result;")
    List<ReminderAndCheck> getChecker();

    // 1.2 初审未审核与未分配单量
    @Select("select\n" +
            "ifnull(updateDay,'Total') as date,                                -- 进入审核日期\n" +
            "if(updateDay is null,'','初审') as type,                          -- 审核类型\n" +
            "count(ord.uuid) as noFinishNum,                                       -- 未审核单量\n" +
            "sum(case when orderUUID is null then 1 else 0 end) as noTaskNum   -- 未分配单量\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "userUuid,\n" +
            "date(updateTime) as updateDay\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status = 3 \n" +
            "and amountApply in (600000,800000,1000000)\n" +
            ") ord\n" +
            "left join\n" +
            "(\n" +
            "select orderUUID\n" +
            "from reviewerOrderTask\n" +
            "where status = 0\n" +
            "group by orderUUID\n" +
            ") rot on rot.orderUUID = ord.uuid\n" +
            "inner join\n" +
            "(\n" +
            "select uuid\n" +
            "from usrUser \n" +
            "where disabled = 0\n" +
            ") usr on usr.uuid = ord.userUuid\n" +
            "group by updateDay with rollup;")
    List<ReminderAndCheck> getUnfinished1();

    // 1.3 复审未审核与未分配单量
    @Select("select\n" +
            "ifnull(updateDay,'Total') as date,                                -- 进入审核日期\n" +
            "if(updateDay is null,'','复审') as type,                          -- 审核类型\n" +
            "count(ord.uuid) as noFinishNum,                                       -- 未审核单量\n" +
            "sum(case when orderUUID is null then 1 else 0 end) as noTaskNum   -- 未分配单量\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "userUuid,\n" +
            "date(updateTime) as updateDay\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status = 4\n" +
            "and amountApply in (600000,800000,1000000)\n" +
            ") ord\n" +
            "left join\n" +
            "(\n" +
            "select orderUUID\n" +
            "from reviewerOrderTask\n" +
            "where status = 0 and reviewerRole = 'SENIOR_REVIEWER'\n" +
            "group by orderUUID\n" +
            ") rot on rot.orderUUID = ord.uuid\n" +
            "inner join\n" +
            "(\n" +
            "select uuid\n" +
            "from usrUser \n" +
            "where disabled = 0\n" +
            ") usr on usr.uuid = ord.userUuid\n" +
            "group by updateDay with rollup;")
    List<ReminderAndCheck> getUnfinished2();

    //  2.1 初审命中各个规则的问题描述
    @Select("select    \n" +
            "date(r.createTime) as date,                     -- 日期\n" +
            "case\n" +
            "when ruleLevel=1 then 'A类规则' \n" +
            "when ruleLevel=2 then 'B类规则'\n" +
            "when ruleLevel=3 then 'C类规则' \n" +
            "when ruleLevel=4 then 'D类规则' \n" +
            "else 'e类规则' end as ruleType,                 -- 规则类型\n" +
            "description,                                    -- 规则问题描述\n" +
            "count(distinct r.orderno) as hitOrders          -- 命中订单\n" +
            "from manOrderCheckRule r \n" +
            "left join reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='JUNIOR_REVIEWER' and t.`status`=0 \n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0 \n" +
            "where ruleResult = 1\n" +
            "and r.disabled = '0'\n" +
            "and datediff(now(),date(r.createTime)) < 3\n" +
            "group by 1,2,3  \n" +
            "order by 1 desc,3,4 desc; ")
    List<ReminderAndCheck> getProblemDesc();

    //  2.3 电核接通率表
    @Select("select   \n" +
            "z.PhoneCheckDate,                                                      -- 电核日期\n" +
            "z.CompanyCheckNumber,                                                  -- company电核数\n" +
            "FORMAT(CompanyPhoneSuccessRate*100, 1) as 'CompanyPhoneSuccessRate',  -- company接通率%  \n" +
            "FIRSTPhoneCheckNumber,                                                 -- FIRST电核数\n" +
            "FORMAT(FIRSTPhoneSuccessRate*100, 1) as 'FIRSTPhoneSuccessRate',      -- FIRST接通率%\n" +
            "SECONDPhoneCheckNumber,                                                -- SECOND电核数\n" +
            "FORMAT(SECONDPhoneSuccessRate*100,1) as 'SECONDPhoneSuccessRate',     -- SECOND接通率%\n" +
            "CumulativeNumberOfPhoneCheck                                           -- 累计电核数 \n" +
            "from   \n" +
            "(\n" +
            "SELECT   \n" +
            "date(createTime) as PhoneCheckDate,  \n" +
            "sum(IF(teleReviewOperationType=2,1,0)) as CompanyPhoneSuccessNumber,  \n" +
            "COUNT(DISTINCT orderNo) as CompanyCheckNumber, \n" +
            "sum(IF(teleReviewOperationType=2,1,0))/COUNT(DISTINCT orderNo) as CompanyPhoneSuccessRate \n" +
            "from   \n" +
            "(  \n" +
            "SELECT r.createTime,r.orderNo,m.realname,r.teleReviewOperationType  \n" +
            "from manOrderRemark r   \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID and t.reviewerRole='SENIOR_REVIEWER' and t.status = 0  \n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0  \n" +
            "where r.type =3  and r.createTime>='2018-03-10 00:00:00' and r.disabled=0  \n" +
            "and r.teleReviewOperationType in (1,2) and t.finish=1 and DATEDIFF(NOW(),r.createTime)<8 \n" +
            "GROUP BY 1,2,3,4  \n" +
            ") w GROUP BY 1  \n" +
            "ORDER BY 2,1   \n" +
            ")z  \n" +
            "LEFT JOIN  \n" +
            "(   \n" +
            "SELECT   \n" +
            "date(createTime) as PhoneCheckDate,  \n" +
            "sum(IF(teleReviewOperationType=4,1,0)) as FIRSTPhoneSuccessNumber,  \n" +
            "COUNT(DISTINCT orderNo) as FIRSTPhoneCheckNumber,  \n" +
            "sum(IF(teleReviewOperationType=4,1,0))/COUNT(DISTINCT orderNo) as FIRSTPhoneSuccessRate \n" +
            "from   \n" +
            "(  \n" +
            "SELECT r.createTime,r.orderNo,m.realname,r.teleReviewOperationType  \n" +
            "from manOrderRemark r   \n" +
            "JOIN reviewerOrderTask t on r.orderNo = t.orderUUID and t.reviewerRole = 'SENIOR_REVIEWER' and t.status = 0  \n" +
            "JOIN manUser m on t.reviewerId = m.id and m.disabled = 0  \n" +
            "where r.type = 3  and r.createTime >= '2018-03-10 00:00:00' and r.disabled = 0  \n" +
            "and r.teleReviewOperationType in (3,4) and t.finish = 1 and datediff(now(),r.createTime) < 8\n" +
            "group by 1,2,3,4\n" +
            ") w group by 1\n" +
            "order by 2,1\n" +
            ")zz on z.PhoneCheckDate = zz.PhoneCheckDate\n" +
            "left join\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as PhoneCheckDate, \n" +
            "sum(IF(teleReviewOperationType=6,1,0)) as SECONDPhoneSuccessNumber,\n" +
            "COUNT(DISTINCT orderNo) as SECONDPhoneCheckNumber,\n" +
            "sum(IF(teleReviewOperationType=6,1,0))/COUNT(DISTINCT orderNo) as SECONDPhoneSuccessRate \n" +
            "from   \n" +
            "(  \n" +
            "SELECT r.createTime,r.orderNo,m.realname,r.teleReviewOperationType  \n" +
            "from manOrderRemark r   \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.status=0  \n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0  \n" +
            "where r.type =3  and r.createTime>='2018-03-10 00:00:00' and r.disabled=0  \n" +
            "and r.teleReviewOperationType in (5,6) and t.finish=1 and DATEDIFF(NOW(),r.createTime)<8 \n" +
            "GROUP BY 1,2,3,4  \n" +
            ") w GROUP BY 1  \n" +
            "ORDER BY 2   \n" +
            ")zzz on z.PhoneCheckDate=zzz.PhoneCheckDate\n" +
            "LEFT JOIN  \n" +
            "(   \n" +
            "SELECT   \n" +
            "date(createTime) as PhoneCheckDate,   \n" +
            "COUNT(*) as CumulativeNumberOfPhoneCheck \n" +
            "from   \n" +
            "(  \n" +
            "SELECT r.createTime,r.orderNo,m.realname,r.teleReviewOperationType  \n" +
            " from manOrderRemark r   \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.`status`=0  \n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0  \n" +
            "where r.type =3  and r.createTime>='2018-03-10 00:00:00' and r.disabled=0  \n" +
            "and r.teleReviewOperationType in (1,2,3,4,5,6) and t.finish=1 and DATEDIFF(NOW(),r.createTime)<8 \n" +
            "GROUP BY 1,2,3,4  \n" +
            ") w GROUP BY 1  \n" +
            "ORDER BY 2  ,1   \n" +
            ")z4 on z.PhoneCheckDate = z4.PhoneCheckDate\n" +
            "GROUP BY 1,2,3,4,5,6,7,8\n" +
            "ORDER BY 1 desc;")
    List<ReminderAndCheck> getProblemDesc14();

    //  2.2 电核接通率表
    @Select("SELECT \n" +
            "z.PhoneCheckDate,                                    -- 电核日期\n" +
            "z.PhoneCheckResult,                                  -- 电核结果\n" +
            "z.PhoneCheckNumber as CompanyPhoneCheckNumber,       -- company电核数\n" +
            "FORMAT(z.Proportion*100,1) as 'p1', -- company占比%\n" +
            "zz.PhoneCheckNumber as FIRSTPhoneCheckNumber,        -- first电核数\n" +
            "FORMAT(zz.Proportion*100,1) as 'f1',  -- first占比%\n" +
            "zzz.PhoneCheckNumber as SECONDPhoneCheckNumber,      -- second电核数 \n" +
            "FORMAT(zzz.Proportion,1) as 's1'     -- second占比%\n" +
            "from \n" +
            "(\n" +
            "SELECT \n" +
            "date(w.createTime) as PhoneCheckDate,\n" +
            "'Company' as PhoneCheckType,\n" +
            "PhoneCheckResult,\n" +
            "COUNT(DISTINCT w.orderNo) as PhoneCheckNumber,\n" +
            "COUNT(DISTINCT w.orderNo)/COUNT(DISTINCT ww.orderNo) as Proportion\n" +
            "from \n" +
            "(\n" +
            "SELECT r.createTime,r.orderNo,m.realname,r.teleReviewOperationType,\n" +
            "case \n" +
            "when teleReviewResult REGEXP 'Best|Baik|Normal' THEN 'pass'\n" +
            "when teleReviewResult REGEXP 'tolak'THEN 'reject'\n" +
            "ELSE 'Go to the next step' end as 'PhoneCheckResult'\n" +
            "from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.status=0\n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0\n" +
            "where r.type =3  and date(r.createTime)>='2018-03-10' and r.disabled=0\n" +
            "and r.teleReviewOperationType in (2) and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "GROUP BY 1,2,3,4,5\n" +
            ") w \n" +
            "LEFT JOIN \n" +
            "(\n" +
            "SELECT r.createTime,r.orderNo,m.realname\n" +
            "from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.status=0\n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0\n" +
            "where r.type =3  and date(r.createTime)>='2018-03-10' and r.disabled=0\n" +
            "and r.teleReviewOperationType in (2) and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "GROUP BY 1,2,3\n" +
            ")ww on date(ww.createTime)=date(w.createTime) \n" +
            "GROUP BY 1,2,3\n" +
            "ORDER BY 1 desc,2\n" +
            ") z \n" +
            "LEFT JOIN\n" +
            "(\n" +
            "SELECT \n" +
            "date(w.createTime) as PhoneCheckDate,\n" +
            "PhoneCheckResult,\n" +
            "COUNT(DISTINCT w.orderNo) as PhoneCheckNumber,\n" +
            "COUNT(DISTINCT w.orderNo)/COUNT(DISTINCT ww.orderNo) as Proportion\n" +
            "from \n" +
            "(\n" +
            "SELECT r.createTime,r.orderNo,m.realname,r.teleReviewOperationType,\n" +
            "IF(r.teleReviewResult REGEXP'Tidak tersambung' , 'not success','success') as 'Success or not',\n" +
            "case \n" +
            "when teleReviewResult REGEXP 'LULUS' THEN 'pass'\n" +
            "when teleReviewResult REGEXP 'tolak' THEN 'reject'\n" +
            "ELSE 'Go to the next step' end as 'PhoneCheckResult'\n" +
            "from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.status=0\n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0\n" +
            "where r.type =3  and date(r.createTime)>='2018-03-10'  and r.disabled=0\n" +
            "and r.teleReviewOperationType in (4) and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "GROUP BY 1,2,3,4,5,6\n" +
            ") w \n" +
            "LEFT JOIN \n" +
            "(\n" +
            "SELECT r.createTime,r.orderNo,m.realname\n" +
            " from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.status=0\n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0\n" +
            "where r.type =3 and date(r.createTime)>='2018-03-10' and r.disabled=0\n" +
            "and r.teleReviewOperationType in (4) and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "GROUP BY 1,2,3\n" +
            ")ww on date(ww.createTime)=date(w.createTime)\n" +
            "GROUP BY 1,2\n" +
            "ORDER BY 1 desc ,2\n" +
            ")zz on z.PhoneCheckDate=zz.PhoneCheckDate  and z.PhoneCheckResult = zz.PhoneCheckResult\n" +
            "LEFT JOIN  \n" +
            "(\n" +
            "SELECT \n" +
            "date(w.createTime) as PhoneCheckDate,\n" +
            "PhoneCheckResult,\n" +
            "COUNT(DISTINCT w.orderNo) as PhoneCheckNumber,\n" +
            "COUNT(DISTINCT w.orderNo)/COUNT(DISTINCT ww.orderNo) as Proportion\n" +
            "from \n" +
            "(\n" +
            "SELECT r.createTime,r.orderNo,m.realname,r.teleReviewOperationType,\n" +
            "IF(r.teleReviewResult REGEXP'Tidak tersambung' , 'not success','success') as 'Success or not',\n" +
            "case \n" +
            "when teleReviewResult REGEXP 'LULUS'THEN 'Pass'\n" +
            "when teleReviewResult REGEXP 'tolak'THEN 'Reject'\n" +
            "ELSE 'Go to the next step' end as 'PhoneCheckResult' \n" +
            "from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on r.orderNo=t.orderUUID and t.reviewerRole='SENIOR_REVIEWER' and t.status=0\n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0\n" +
            "where r.type =3  and date(r.createTime)>='2018-03-10 '  and r.disabled=0\n" +
            "and r.teleReviewOperationType in (6) and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "GROUP BY 1,2,3,4,5,6\n" +
            ") w \n" +
            "LEFT JOIN \n" +
            "(\n" +
            "SELECT r.createTime,r.orderNo,m.realname\n" +
            " from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.status=0\n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0\n" +
            "where r.type =3  and date(r.createTime)>='2018-03-10'  and r.disabled=0\n" +
            "and r.teleReviewOperationType in (6) and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "GROUP BY 1,2,3\n" +
            ")ww on date(ww.createTime)=date(w.createTime) \n" +
            "GROUP BY 1,2\n" +
            "ORDER BY 1 desc ,2\n" +
            ")zzz on z.PhoneCheckDate=zzz.PhoneCheckDate and z.PhoneCheckResult = zzz.PhoneCheckResult\n" +
            "GROUP BY 1,2,3,4,5,6,7,8\n" +
            "ORDER BY 1 desc;")
    List<ReminderAndCheck> getProblemDesc15();

    //  2.5 接通电核的拒绝原因表
    @Select("SELECT \n" +
            "w.rejectDate,                                                     -- 拒绝日期\n" +
            "w.rejectReason,                                                   -- 拒绝原因\n" +
            "w.rejectOrders,                                                   -- 拒绝订单\n" +
            "FORMAT(w.RejectOrders/ww.RejectOrders*100,1) as proportion        -- 拒绝订单占比\n" +
            "from \n" +
            "(\n" +
            "SELECT date(r.createTime) as RejectDate, \n" +
            "r.description as RejectReason,\n" +
            "COUNT(DISTINCT r.orderNo) as RejectOrders\n" +
            "from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.status=0\n" +
            "where r.type =3  and date(r.createTime)>='2018-03-15' and r.disabled=0\n" +
            "and r.teleReviewOperationType=2 and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "and r.description !=''\n" +
            "GROUP BY 1,2 \n" +
            ")w\n" +
            "LEFT JOIN\n" +
            "(\n" +
            "SELECT date(r.createTime) as RejectDate,\n" +
            "COUNT(DISTINCT r.orderNo) as RejectOrders\n" +
            "from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.status=0\n" +
            "where r.type =3  and date(r.createTime)>='2018-03-15' and r.disabled=0\n" +
            "and r.teleReviewOperationType=2 and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "and r.description !=''\n" +
            "GROUP BY 1\n" +
            ")ww on w.RejectDate=ww.RejectDate\n" +
            "\n" +
            "UNION ALL\n" +
            "select\n" +
            "x.rejectDate,\n" +
            "x.rejectReason, \n" +
            "x.rejectOrders,\n" +
            "FORMAT(x.RejectOrders/xx.RejectOrders*100,1) as proportion \n" +
            "from\n" +
            "(\n" +
            "SELECT date(r.createTime) as RejectDate,\n" +
            "r.description as RejectReason,\n" +
            "COUNT(DISTINCT r.orderNo) as RejectOrders\n" +
            "from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.status=0\n" +
            "where r.type =3  and date(r.createTime)>='2018-03-15' and r.disabled=0\n" +
            "and r.teleReviewOperationType=4 and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "and r.description !=''\n" +
            "GROUP BY 1,2\n" +
            ")x\n" +
            "LEFT JOIN\n" +
            "(\n" +
            "SELECT date(r.createTime) as RejectDate,\n" +
            "COUNT(DISTINCT r.orderNo) as RejectOrders\n" +
            "from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.status=0\n" +
            "where r.type =3  and date(r.createTime)>='2018-03-15' and r.disabled=0\n" +
            "and r.teleReviewOperationType=4 and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "and r.description !=''\n" +
            "GROUP BY 1\n" +
            ")xx on x.RejectDate=xx.RejectDate\n" +
            "\n" +
            "UNION ALL\n" +
            "SELECT \n" +
            "z.rejectDate,\n" +
            "z.rejectReason, \n" +
            "z.rejectOrders,\n" +
            "FORMAT(z.RejectOrders/zz.RejectOrders*100,1) as proportion \n" +
            "from\n" +
            "(\n" +
            "SELECT date(r.createTime) as RejectDate,\n" +
            "r.description as RejectReason,\n" +
            "COUNT(DISTINCT r.orderNo) as RejectOrders\n" +
            "from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.status=0\n" +
            "where r.type =3  and date(r.createTime)>='2018-03-15' and r.disabled=0\n" +
            "and r.teleReviewOperationType=6 and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "and r.description !=''\n" +
            "GROUP BY 1,2\n" +
            ")z\n" +
            "LEFT JOIN\n" +
            "(\n" +
            "SELECT date(r.createTime) as RejectDate,\n" +
            "COUNT(DISTINCT r.orderNo) as RejectOrders\n" +
            "from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.status=0\n" +
            "where r.type =3  and date(r.createTime)>='2018-03-15' and r.disabled=0\n" +
            "and r.teleReviewOperationType=6 and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "and r.description !=''\n" +
            "GROUP BY 1\n" +
            ")zz on z.RejectDate=zz.RejectDate\n" +
            "ORDER BY 1 desc,2;")
    List<ReminderAndCheck> getProblemDesc16();


    //     审核员电核接通率表
    @Select("SELECT   \n" +
            "z.PhoneCheckDate,                                                                              -- 电核日期\n" +
            "z.Reviewers,                                                                                   -- 审核员\n" +
            "z.CompanyPhoneCheckNumber,                                                                     -- company电核数\n" +
            "FORMAT(CompanyPhoneSuccessRate*100, 1) as 'CompanyPhoneSuccessRate',                          -- company接通率% \n" +
            "FIRSTPhoneSuccessNumber,                                                                       -- FIRST接通数  \n" +
            "FIRSTPhoneCheckNumber,                                                                         -- FIRST电核数 \n" +
            "FORMAT(FIRSTPhoneSuccessRate*100,1) as 'FIRSTPhoneSuccessRate',                               -- FIRST接通率%\n" +
            "SECONDPhoneSuccessNumber,                                                                      -- SECOND接通数  \n" +
            "SECONDPhoneCheckNumber,                                                                        -- SECOND电核数 \n" +
            "FORMAT(SECONDPhoneSuccessRate*100, 1) as 'SECONDPhoneSuccessRate',                            -- SECOND接通率%\n" +
            "CumulativeNumberOfPhoneCheck                                                                   -- 累计电核数\n" +
            "from   \n" +
            "(  \n" +
            "SELECT   \n" +
            "date(createTime) as PhoneCheckDate,\n" +
            "realname as Reviewers, \n" +
            "sum(IF(teleReviewOperationType=2,1,0)) as CompanyPhoneSuccessNumber,\n" +
            "COUNT(DISTINCT orderNo) as CompanyPhoneCheckNumber, \n" +
            "sum(IF(teleReviewOperationType=2,1,0))/COUNT(DISTINCT orderNo) as CompanyPhoneSuccessRate \n" +
            "from   \n" +
            "(  \n" +
            "SELECT r.createTime,r.orderNo,m.realname,r.teleReviewOperationType  \n" +
            "from manOrderRemark r   \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.status=0  \n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0  \n" +
            "where r.type =3  and r.createTime>='2018-03-13 00:00:00' and r.disabled=0  \n" +
            "and r.teleReviewOperationType in (1,2) and t.finish=1 and DATEDIFF(NOW(),r.createTime)<3 \n" +
            "GROUP BY 1,2,3,4  \n" +
            ") w GROUP BY 1,2\n" +
            "ORDER BY 2,1\n" +
            ")z  \n" +
            "LEFT JOIN  \n" +
            "(\n" +
            "SELECT\n" +
            "date(createTime) as PhoneCheckDate,\n" +
            "realname as Reviewers,\n" +
            "sum(IF(teleReviewOperationType=4,1,0)) as FIRSTPhoneSuccessNumber,\n" +
            "COUNT(DISTINCT orderNo) as FIRSTPhoneCheckNumber,  \n" +
            "sum(IF(teleReviewOperationType=4,1,0))/COUNT(DISTINCT orderNo) as FIRSTPhoneSuccessRate\n" +
            "from   \n" +
            "(\n" +
            "SELECT r.createTime,r.orderNo,m.realname,r.teleReviewOperationType\n" +
            " from manOrderRemark r   \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.`status`=0  \n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0  \n" +
            "where r.type =3  and r.createTime>='2018-03-13 00:00:00' and r.disabled=0  \n" +
            "and r.teleReviewOperationType in (3,4) and t.finish=1 and DATEDIFF(NOW(),r.createTime)<3 \n" +
            "GROUP BY 1,2,3,4  \n" +
            ") w GROUP BY 1,2  \n" +
            "ORDER BY 2  ,1   \n" +
            ")zz on  z.PhoneCheckDate=zz.PhoneCheckDate and z.Reviewers = zz.Reviewers\n" +
            "LEFT JOIN  \n" +
            "(   \n" +
            "SELECT   \n" +
            "date(createTime) as PhoneCheckDate,  \n" +
            "realname as Reviewers, \n" +
            "sum(IF(teleReviewOperationType=6,1,0)) as SECONDPhoneSuccessNumber,\n" +
            "COUNT(DISTINCT orderNo) as SECONDPhoneCheckNumber,\n" +
            "sum(IF(teleReviewOperationType=6,1,0))/COUNT(DISTINCT orderNo) as SECONDPhoneSuccessRate\n" +
            "from   \n" +
            "(  \n" +
            "SELECT r.createTime,r.orderNo,m.realname,r.teleReviewOperationType  \n" +
            " from manOrderRemark r   \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.`status`=0  \n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0  \n" +
            "where r.type =3  and r.createTime>='2018-03-13 00:00:00' and r.disabled=0  \n" +
            "and r.teleReviewOperationType in (5,6) and t.finish=1 and DATEDIFF(NOW(),r.createTime)<3 \n" +
            "GROUP BY 1,2,3,4  \n" +
            ") w GROUP BY 1,2  \n" +
            "ORDER BY 2  ,1   \n" +
            ")zzz on  z.PhoneCheckDate=zzz.PhoneCheckDate and z.Reviewers = zzz.Reviewers   \n" +
            "LEFT JOIN  \n" +
            "(  \n" +
            "SELECT   \n" +
            "date(createTime) as PhoneCheckDate,\n" +
            "realname as Reviewers, \n" +
            "COUNT(*) as CumulativeNumberOfPhoneCheck\n" +
            "from   \n" +
            "(  \n" +
            "SELECT r.createTime,r.orderNo,m.realname,r.teleReviewOperationType  \n" +
            "from manOrderRemark r   \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.`status`=0  \n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0  \n" +
            "where r.type =3  and r.createTime>='2018-03-13 00:00:00' and r.disabled=0  \n" +
            "and r.teleReviewOperationType in (1,2,3,4,5,6) and t.finish=1 and DATEDIFF(NOW(),r.createTime)<3 \n" +
            "GROUP BY 1,2,3,4  \n" +
            ") w GROUP BY 1,2  \n" +
            "ORDER BY 2  ,1   \n" +
            ")z4 on z.PhoneCheckDate=z4.PhoneCheckDate and z.Reviewers = z4.Reviewers   \n" +
            "GROUP BY 1,2,3,4,5,6,7,8,9  \n" +
            "ORDER BY 1 desc;")
    List<ReminderAndCheck> getProblemDesc17();

    @Select("SELECT  \n" +
            "z.callDate,                        -- 拨打日期\n" +
            "z.realname as reviewers,           -- 审核员\n" +
            "z.companyOrdersNumber,             -- 公司订单数\n" +
            "z.numberOfCallsMadeByCompanyPhone, -- 公司拨打次数\n" +
            "firstOrdersNumber,                 -- first订单数\n" +
            "firstCallNumber,                   -- first拨打次数\n" +
            "secondOrdersNumber,                -- second订单数\n" +
            "secondCallNumber                   -- second拨打次数\n" +
            "from \n" +
            "(\n" +
            "SELECT  date(createTime) as CallDate,\n" +
            "CASE  \n" +
            "when  teleReviewOperationType in (1,2) then 'company'\n" +
            "when  teleReviewOperationType in (3,4) then  'contacts1'\n" +
            "else 'contacts2' end as 'PhoneCheckType',\n" +
            "m.realname, COUNT(DISTINCT orderno) as CompanyOrdersNumber,COUNT(*) as NumberOfCallsMadeByCompanyPhone\n" +
            "FROM(\n" +
            "SELECT r.createTime,r.orderNo,m.realname,r.teleReviewOperationType\n" +
            " from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.`status`=0\n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0\n" +
            "where r.type =3  and r.createTime>='2018-03-10 00:00:00' and r.disabled=0\n" +
            "and r.teleReviewOperationType in (1,2) and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "GROUP BY 1,2,3,4\n" +
            "ORDER BY 2 \n" +
            ") m GROUP BY 1,2,3\n" +
            ")z\n" +
            "LEFT JOIN \n" +
            "(\n" +
            "SELECT  date(createTime) as CallDate,\n" +
            "CASE  \n" +
            "when  teleReviewOperationType in (1,2) then 'company'\n" +
            "when  teleReviewOperationType in (3,4) then  'contacts1'\n" +
            "else 'contacts2' end as 'PhoneCheckType',\n" +
            "m.realname, COUNT(DISTINCT orderno) as FirstOrdersNumber,COUNT(*) as FirstCallNumber\n" +
            "FROM(\n" +
            "SELECT r.createTime,r.orderNo,m.realname,r.teleReviewOperationType\n" +
            " from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.`status`=0\n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0\n" +
            "where r.type =3  and r.createTime>='2018-03-10 00:00:00' and r.disabled=0\n" +
            "and r.teleReviewOperationType in (3,4) and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "GROUP BY 1,2,3,4\n" +
            "ORDER BY 2 \n" +
            ") m GROUP BY 1,2,3\n" +
            ")zz on z.CallDate=zz.CallDate and z.realname=zz.realname \n" +
            "LEFT JOIN \n" +
            "(\n" +
            "SELECT  date(createTime) as CallDate,\n" +
            "CASE  \n" +
            "when  teleReviewOperationType in (1,2) then 'company'\n" +
            "when  teleReviewOperationType in (3,4) then  'contacts1'\n" +
            "else 'contacts2' end as 'PhoneCheckType',\n" +
            "m.realname, COUNT(DISTINCT orderno) as SecondOrdersNumber,COUNT(*) as SecondCallNumber\n" +
            "FROM(\n" +
            "SELECT r.createTime,r.orderNo,m.realname,r.teleReviewOperationType\n" +
            " from manOrderRemark r \n" +
            "JOIN reviewerOrderTask t on  r.orderNo=t.orderUUID  and t.reviewerRole='SENIOR_REVIEWER' and t.`status`=0\n" +
            "JOIN manUser m on t.reviewerId=m.id and m.disabled=0\n" +
            "where r.type =3  and r.createTime>='2018-03-10 00:00:00' and r.disabled=0\n" +
            "and r.teleReviewOperationType in (5,6) and t.finish=1 and DATEDIFF(NOW(),r.createtime)<3\n" +
            "GROUP BY 1,2,3,4\n" +
            "ORDER BY 2 \n" +
            ") m GROUP BY 1,2,3\n" +
            ")zzz on z.CallDate=zzz.CallDate and z.realname=zzz.realname\n" +
            "GROUP BY 1,2,3,4,5,6,7,8\n" +
            "ORDER BY 1 desc;")
    List<ReminderAndCheck> getProblemDesc18();

    // 2.1 催收人员委案与回收时报
    @Select("select\n" +
            "realname,                      -- 催收员\n" +
            "step,                          -- 阶段\n" +
            "taskNum,                       -- 分案单量\n" +
            "target,                        -- 目标\n" +
            "recoveryNum,                   -- 回收数量\n" +
            "recoveryRate,                  -- 回收率\n" +
            "recoveryToday                  -- 今日回单量\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "'-' as realname,\n" +
            "'D-1' as step,\n" +
            "taskNum,\n" +
            "'-' as target,\n" +
            "recoveryNum,\n" +
            "'-' as recoveryRate,\n" +
            "recoveryNum as recoveryToday\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "sum(case when status = 7 then 1 else 0 end) + sum(case when date(actualRefundTime) = curdate() then 1 else 0 end) as taskNum,\n" +
            "sum(case when date(actualRefundTime) = curdate() then 1 else 0 end) as recoveryNum\n" +
            "from\n" +
            "ordOrder\n" +
            "where\n" +
            "disabled = 0\n" +
            "and amountApply in (600000,800000,1000000)\n" +
            "and status in (7,10)\n" +
            "and date(refundTime) = date_add(curdate(),interval 1 day)\n" +
            ") ord\n" +
            "union all\n" +
            "select\n" +
            "realname,\n" +
            "'D0' as dayn,\n" +
            "sum(taskNum) as taskNum,\n" +
            "round(sum(taskNum*0.7),0) as target,\n" +
            "sum(recoveryNum) as recoveryNum,\n" +
            "concat(round(sum(recoveryNum)/sum(taskNum)*100,2),'%') as recoveryRate,\n" +
            "sum(case when actualRefundDay = curdate() then recoveryNum else 0 end) as recoveryToday\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "realname,\n" +
            "actualRefundDay,\n" +
            "count(col.id) as taskNum,\n" +
            "sum(case when datediff(actualRefundDay,refundDay) = 0 then 1 else 0 end) as recoveryNum\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "id,createTime,outsourceId\n" +
            "from collectionOrderHistory\n" +
            "where disabled = 0\n" +
            ") col\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "max(id) as id,\n" +
            "orderUUID,\n" +
            "date(refundTime) as refundDay,\n" +
            "date(actualRefundTime) as actualRefundDay\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "id,orderUUID,createTime\n" +
            "from collectionOrderHistory\n" +
            "where disabled = 0\n" +
            ") col\n" +
            "inner join\n" +
            "(\n" +
            "select uuid,refundtime,actualRefundTime\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and datediff(curdate(),refundtime) = 0\n" +
            ") ord on ord.uuid = col.orderUUID\n" +
            "where datediff(createTime,refundtime) = 0\n" +
            "group by orderUUID\n" +
            ") screen on screen.id = col.id\n" +
            "left join manUser man on man.id = col.outsourceId\n" +
            "group by actualRefundDay,realname\n" +
            ") result\n" +
            "group by realname\n" +
            "union all\n" +
            "select\n" +
            "realname,\n" +
            "dayn,\n" +
            "sum(taskNum) as taskNum,\n" +
            "case\n" +
            "when dayn = 'D1' then round(sum(taskNum)*0.6,0)\n" +
            "when dayn = 'D2' then round(sum(taskNum)*0.65,0)\n" +
            "when dayn = 'D3' then round(sum(taskNum)*0.7,0)\n" +
            "when dayn = 'D4' then round(sum(taskNum)*0.7,0)\n" +
            "when dayn = 'D5' then round(sum(taskNum)*0.7,0)\n" +
            "when dayn = 'D6' then round(sum(taskNum)*0.7,0)\n" +
            "when dayn = 'D7' then round(sum(taskNum)*0.7,0)\n" +
            "else 0 end as target,\n" +
            "sum(recoveryNum) as recoveryNum,\n" +
            "concat(round(sum(recoveryNum)/sum(taskNum)*100,2),'%') as recoveryRate,\n" +
            "sum(case when actualRefundDay = curdate() then recoveryNum else 0 end) as recoveryToday\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "realname,\n" +
            "concat('D',datediff(curdate(),refundDay)) as dayn,\n" +
            "actualRefundDay,\n" +
            "count(col.id) as taskNum,\n" +
            "sum(case when datediff(actualRefundDay,refundDay) between 1 and 7 then 1 else 0 end) as recoveryNum\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "id,createTime,outsourceId\n" +
            "from collectionOrderHistory\n" +
            "where disabled = 0\n" +
            ") col\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "max(id) as id,\n" +
            "orderUUID,\n" +
            "date(refundTime) as refundDay,\n" +
            "date(actualRefundTime) as actualRefundDay\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "id,orderUUID,createTime\n" +
            "from collectionOrderHistory\n" +
            "where disabled = 0\n" +
            ") col\n" +
            "inner join\n" +
            "(\n" +
            "select uuid,refundtime,actualRefundTime\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and datediff(curdate(),refundtime) between 1 and 7\n" +
            ") ord on ord.uuid = col.orderUUID\n" +
            "where datediff(createTime,refundtime) between 1 and 7\n" +
            "group by orderUUID\n" +
            ") screen on screen.id = col.id\n" +
            "left join manUser man on man.id = col.outsourceId\n" +
            "group by actualRefundDay,dayn,realname\n" +
            ") result\n" +
            "group by realname,dayn\n" +
            "union all\n" +
            "select\n" +
            "realname,\n" +
            "'D8-30' as dayn,\n" +
            "sum(taskNum) as taskNum,\n" +
            "'-' as target,\n" +
            "sum(recoveryNum) as recoveryNum,\n" +
            "concat(round(sum(recoveryNum)/sum(taskNum)*100,2),'%') as recoveryRate,\n" +
            "sum(case when actualRefundDay = curdate() then recoveryNum else 0 end) as recoveryToday\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "realname,\n" +
            "actualRefundDay,\n" +
            "count(col.id) as taskNum,\n" +
            "sum(case when datediff(actualRefundDay,refundDay) between 8 and 30 then 1 else 0 end) as recoveryNum\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "id,createTime,outsourceId\n" +
            "from collectionOrderHistory\n" +
            "where disabled = 0\n" +
            ") col\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "max(id) as id,\n" +
            "orderUUID,\n" +
            "date(refundTime) as refundDay,\n" +
            "date(actualRefundTime) as actualRefundDay\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "id,orderUUID,createTime\n" +
            "from collectionOrderHistory\n" +
            "where disabled = 0\n" +
            ") col\n" +
            "inner join\n" +
            "(\n" +
            "select uuid,refundtime,actualRefundTime\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and datediff(curdate(),refundtime) between 8 and 30\n" +
            ") ord on ord.uuid = col.orderUUID\n" +
            "where datediff(createTime,refundtime) between 8 and 30\n" +
            "group by orderUUID\n" +
            ") screen on screen.id = col.id\n" +
            "left join manUser man on man.id = col.outsourceId\n" +
            "group by actualRefundDay,realname\n" +
            ") result\n" +
            "group by realname\n" +
            "union all\n" +
            "select\n" +
            "realname,\n" +
            "'D30+' as dayn,\n" +
            "sum(taskNum) as taskNum,\n" +
            "'-' as target,\n" +
            "sum(recoveryNum) as recoveryNum,\n" +
            "concat(round(sum(recoveryNum)/sum(taskNum)*100,2),'%') as recoveryRate,\n" +
            "sum(case when actualRefundDay = curdate() then recoveryNum else 0 end) as recoveryToday\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "realname,\n" +
            "actualRefundDay,\n" +
            "count(col.id) as taskNum,\n" +
            "sum(case when datediff(actualRefundDay,refundDay) >= 31 then 1 else 0 end) as recoveryNum\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "id,createTime,outsourceId\n" +
            "from collectionOrderHistory\n" +
            "where disabled = 0\n" +
            ") col\n" +
            "inner join\n" +
            "(\n" +
            "select \n" +
            "max(id) as id,\n" +
            "orderUUID,\n" +
            "date(refundTime) as refundDay,\n" +
            "date(actualRefundTime) as actualRefundDay\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "id,orderUUID,createTime\n" +
            "from collectionOrderHistory\n" +
            "where disabled = 0\n" +
            ") col\n" +
            "inner join\n" +
            "(\n" +
            "select uuid,refundtime,actualRefundTime\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and datediff(curdate(),refundtime) >= 31\n" +
            ") ord on ord.uuid = col.orderUUID\n" +
            "where datediff(createTime,refundtime) >= 31\n" +
            "group by orderUUID\n" +
            ") screen on screen.id = col.id\n" +
            "left join manUser man on man.id = col.outsourceId\n" +
            "group by actualRefundDay,realname\n" +
            ") result\n" +
            "group by realname\n" +
            ") mydata\n" +
            "order by realname,case step when 'D30+' then 'D9' else step end;")
    List<ReminderAndCheck> getPresser();

    // 2.2 批次逾期率
    @Select("select\n" +
            "ifnull(refundDay,'合计') as refundDay,                                                 -- 应还日期\n" +
            "orders,                                                                                -- 应还单量\n" +
            "resolvedOrders,                                                                        -- 已还单量\n" +
            "concat(round(100-resolvedOrders/orders*100,2),'%') as overdueRate,                     -- 逾期率\n" +
            "round(principal/2000,0) as principal,                                                  -- 应还金额(元)\n" +
            "round(resolvedPrincipal/2000,0) as resolvedPrincipal,                                  -- 已还金额(元)\n" +
            "concat(round(100-resolvedPrincipal/principal*100,2),'%') as aomuntOverdueRate,         -- 金额逾期率\n" +
            "round(principalJin/2000,0) as principalJin,                                            -- 应还金额(不含逾期费/元)\n" +
            "round(resolvedPrincipalJin/2000,0) as resolvedPrincipalJin,                            -- 已还金额(不含逾期费/元)\n" +
            "concat(round(100-resolvedPrincipalJin/principalJin*100,2),'%') as jinAomuntOverdueRate -- 金额逾期率(不含逾期费)\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "refundDay,\n" +
            "count(uuid) as orders,\n" +
            "sum(case when status in (10,11) then 1 else 0 end) as resolvedOrders,\n" +
            "sum(amountApply+interest+overdueFee) as principal,\n" +
            "sum(case when status in (10,11) then amountApply+interest+overdueFee else 0 end) as resolvedPrincipal,\n" +
            "sum(amountApply+interest) as principalJin,\n" +
            "sum(case when status in (10,11) then amountApply+interest else 0 end) as resolvedPrincipalJin\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(actualRefundTime) as actualRefundDay,\n" +
            "date(refundTime) as refundDay,\n" +
            "uuid,\n" +
            "status,\n" +
            "amountApply,\n" +
            "amountApply - serviceFee as amountActual,\n" +
            "serviceFee,\n" +
            "interest,\n" +
            "case\n" +
            "when status in (7,10) then 0\n" +
            "when status = 8 and datediff(curdate(),refundTime) between 1 and 3 then amountApply*0.01*datediff(curdate(),refundTime)+40000\n" +
            "when status = 8 and datediff(curdate(),refundTime) >= 4 then amountApply*0.01*3+amountApply*0.02*(datediff(curdate(),refundTime)-3)+40000\n" +
            "when status = 11 and datediff(actualRefundTime,refundTime) between 1 and 3 then amountApply*0.01*datediff(actualRefundTime,refundTime)+40000\n" +
            "when status = 11 and datediff(actualRefundTime,refundTime) >= 4 then amountApply*0.01*3+amountApply*0.02*(datediff(actualRefundTime,refundTime)-3)+40000\n" +
            "else 0 end overdueFee\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "actualRefundTime,\n" +
            "refundTime,\n" +
            "uuid,\n" +
            "status,\n" +
            "amountApply,\n" +
            "serviceFee,\n" +
            "interest,\n" +
            "borrowingTerm\n" +
            "from\n" +
            "ordOrder\n" +
            "where disabled = 0\n" +
            "and amountApply in (600000,800000,1000000)\n" +
            "and status in (7,8,10,11)\n" +
            "and date(refundTime) <= curdate()\n" +
            ") ord1\n" +
            ") ord2\n" +
            "group by refundDay with rollup\n" +
            ") ord3\n")
    List<ReminderAndCheck> getBatchOverdueRate();


    // 审核人员每日审核状况（初审）
    @Select("SELECT\n" +
            "t.date as p1,\n" +
            "t.realname as p2,\n" +
            "t.FC_Distribution as p3,\n" +
            "t.FC_finish as p4,\n" +
            "concat(round(FC_finish/FC_Distribution*100,2),'%') as p5,\n" +
            "concat(round((FC_finish-FC_reject)/FC_finish*100,2),'%') as p6\n" +
            "FROM\n" +
            "(SELECT\n" +
            "date(a.createTime) as date,\n" +
            "realname,\n" +
            "COUNT(DISTINCT a.orderUUID) as FC_Distribution,\n" +
            "COUNT(DISTINCT if(finish=1,orderUUID,0)) as FC_finish,\n" +
            "COUNT(DISTINCT if(c.status in(13,14),orderUUID,0)) as FC_reject\n" +
            "FROM doit.reviewerOrderTask a\n" +
            "JOIN doit.ordOrder c on a.orderUUID=c.uuid\n" +
            "join doit.manUser o on a.reviewerId=o.id\n" +
            "WHERE c.disabled=0\n" +
            "and a.reviewerRole = 'JUNIOR_REVIEWER'\n" +
            "and a.status = 0 and a.createTime>=current_date()\n" +
            "GROUP BY 1,2) t;")
    List<DailyCheck> getDayReviewFC();

    // 审核人员每日审核状况（复审）
    @Select("SELECT\n" +
            "w.date as p1,\n" +
            "w.realname as p2,\n" +
            "w.SC_Distribution as p3,\n" +
            "w.SC_finish as p4,\n" +
            "concat(round(SC_finish/SC_Distribution*100,2),'%') as p5,\n" +
            "concat(round((SC_finish-SC_reject-SC_150product)/SC_finish*100,2),'%') as p6\n" +
            "FROM\n" +
            "(SELECT\n" +
            "date(a.createTime) as date,\n" +
            "realname,\n" +
            "COUNT(DISTINCT a.orderUUID) as SC_Distribution,\n" +
            "COUNT(DISTINCT if(finish=1,orderUUID,0)) as SC_finish,\n" +
            "COUNT(DISTINCT if(c.status in(13,14),orderUUID,0)) as SC_reject\n" +
            "FROM doit.reviewerOrderTask a\n" +
            "JOIN doit.ordOrder c on a.orderUUID=c.uuid\n" +
            "join doit.manUser o on a.reviewerId=o.id\n" +
            "WHERE c.disabled=0\n" +
            "and a.reviewerRole = 'SENIOR_REVIEWER'\n" +
            "and a.status = 0 and a.createTime>=current_date()\n" +
            "GROUP BY 1,2) w\n" +
            "JOIN \n" +
            "(SELECT\n" +
            "date(a.createTime) as date,\n" +
            "realname,\n" +
            "COUNT(DISTINCT if(b.orderNo is not null,a.orderUUid,0)) as SC_150product\n" +
            "FROM doit.reviewerOrderTask a\n" +
            "JOIN doit.ordBlack b on b.orderNo= a.orderUUID\n" +
            "join doit.manUser o on a.reviewerId=o.id\n" +
            "WHERE  a.reviewerRole = 'SENIOR_REVIEWER'\n" +
            "and a.status = 0 and a.createTime>=current_date()\n" +
            "and b.remark ='PRODUCT600TO150_SENIOR_REVIEW' and b.disabled=1\n" +
            "GROUP BY 1,2) m on w.date=m.date and w.realname=m.realname;")
    List<DailyCheck> getDayReviewSC();

    // 初审
    @Select("select \n" +
            "ApplyDay as p1, \n" +
            "Distribution_WaitingFC as p2,\n" +
            "Waiting_FC_orders - Distribution_WaitingFC as p3\n" +
            "from\n" +
            "(\t\n" +
            "SELECT date(d.applyTime) as ApplyDay,         \n" +
            "COUNT(DISTINCT d.uuid) as  Waiting_FC_orders,\n" +
            "count(distinct if (case when reviewerRole = 'JUNIOR_REVIEWER' then 1 else 0 end, d.uuid, null)) as Distribution_WaitingFC -- 已分配待初审订单\t\t\n" +
            "from doit.ordOrder d \n" +
            "join doit.usrUser u on d.userUuid=u.uuid\n" +
            "left join doit.reviewerOrderTask r on d.uuid=r.orderUUID\t\t\t\n" +
            "WHERE d.disabled=0 and d.status=3 \n" +
            "and d.createtime >='2018-02-10' and borrowingCount=1 and u.disabled= 0\t\n" +
            "GROUP BY 1 \n" +
            ") s\t\t\t\n" +
            "order by 1 desc  ;\t")
    List<DayReview2> getDayReview2();

    // 复审
    @Select("select \n" +
            "ApplyDay as p1, \n" +
            "Distribution_WaitingSC as p2,\n" +
            "Waiting_SC_orders - Distribution_WaitingSC as p3\n" +
            "from\n" +
            "(\t\t\t\n" +
            "SELECT date(d.applyTime) as ApplyDay,          \n" +
            "COUNT(DISTINCT d.uuid) as  Waiting_SC_orders,\n" +
            "count(distinct if (case when reviewerRole = 'SENIOR_REVIEWER' then 1 else 0 end, d.uuid, null)) as Distribution_WaitingSC  -- 已分配待复审订单\t\t\n" +
            "from doit.ordOrder d \n" +
            "join doit.usrUser u on d.userUuid=u.uuid\t\n" +
            "left join doit.reviewerOrderTask r on d.uuid=r.orderUUID\t\t\t\t\t\n" +
            "WHERE d.disabled=0 and d.status=4 and d.createtime >='2018-02-10' and borrowingCount=1\tand u.disabled= 0\t\t\n" +
            "GROUP BY 1\n" +
            ") s\n" +
            "order by 1 desc  ;\n" +
            "\n")
    List<DayReview2> getDayReview3();


    // 每个复审人员对应的通过率
    @Select("SELECT\n" +
            "if(审核月份 is null , '合计' , 审核月份 ) as p1,                          -- 审核月份,\n" +
            "if(复审审核员 is null , 'Total' ,复审审核员 ) as p2,        -- 复审审核员,\n" +
            "复审完成订单 as p3,\n" +
            "复审完成订单-复审拒绝订单-分流到150产品 as p4,\n" +
            "CONCAT(FORMAT((复审完成订单-复审拒绝订单-分流到150产品)/复审完成订单*100,2),'%') as p5                   -- 复审拒绝订单\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "CONCAT(YEAR(t.createTime),'-',DATE_FORMAT(t.createTime,'%m')) as 审核月份,\n" +
            "m.realname as 复审审核员, \n" +
            "sum(if(t.`reviewerRole`='SENIOR_REVIEWER' and t.finish=1,1,0)) 复审完成订单,\n" +
            "sum(if(o.`status` in(13,14) and t.`reviewerRole`='SENIOR_REVIEWER' and t.finish=1,1,0)) 复审拒绝订单,\n" +
            "sum(case when b.orderNo is not null and reviewerRole='SENIOR_REVIEWER' then 1 else 0 end) AS 分流到150产品\n" +
            "from doit.reviewerOrderTask t\n" +
            "left join doit.manUser m on m.id=t.reviewerId\n" +
            "left join doit.ordOrder o on o.uuid=t.orderUUID\n" +
            "left join\n" +
            "(select \n" +
            "distinct orderNo\n" +
            "from doit.ordBlack \n" +
            "where remark ='PRODUCT600TO150_SENIOR_REVIEW' and disabled=1\n" +
            "and createTime>'2019-01-10'\n" +
            ") b on b.orderNo= t.orderUUID\n" +
            "where m.disabled=0 and o.disabled=0 and t.status = 0 \n" +
            "and DATE_FORMAT(t.createTime, '%Y%m' ) = DATE_FORMAT(CURDATE( ) , '%Y%m' ) -- 只统计本月累计到当前的数据\n" +
            "and m.realname not in ('胡静','郑巧玲', '周佩欣','张闪闪')\n" +
            "group by 1,2 \n" +
            ") s\n" +
            "where 复审完成订单<>0;")
    List<DayReview> getReviewUser();

    // 每个初审人员对应的通过率
    @Select("select \n" +
            "v.`Month` as p1,\n" +
            "v.FC_realname as p2,\n" +
            "v.FC_Finish as p3,\n" +
            "v.FC_Pass as p4,\n" +
            "FC_pass_per as p5\n" +
            "from\n" +
            "(\n" +
            "SELECT\n" +
            "审核月份 as Month,\n" +
            "if(初审审核员 is null , 'Total' ,初审审核员 ) FC_realname,         -- 初审审核员,\n" +
            "初审完成数 as FC_Finish,\n" +
            "初审完成数-初审拒绝数 as FC_Pass,\n" +
            "ifnull(concat(round(100-初审拒绝数/初审完成数*100,2),'%'),'-') as FC_pass_per\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "CONCAT(YEAR(t.createTime),'-',DATE_FORMAT(t.createTime,'%m')) as 审核月份,\n" +
            "m.realname as 初审审核员, \n" +
            "sum(case when reviewerRole = 'JUNIOR_REVIEWER' and finish = 1 then 1 else 0 end) as 初审完成数,\n" +
            "sum(case when reviewerRole = 'JUNIOR_REVIEWER' and finish = 1 and o.status in (13,15) then 1 else 0 end) as 初审拒绝数\n" +
            "from doit.reviewerOrderTask t\n" +
            "left join doit.manUser m on m.id=t.reviewerId\n" +
            "left join doit.ordOrder o on o.uuid=t.orderUUID\n" +
            "where m.disabled=0 and o.disabled=0 and t.status = 0 \n" +
            "and DATE_FORMAT(t.createTime, '%Y%m' ) = DATE_FORMAT(CURDATE( ) , '%Y%m' ) -- 只统计本月累计到当前的数据\n" +
            "and m.realname not in ('郑巧玲', '周佩欣','张闪闪','iceiceice','qiaoling')\n" +
            "group by 1,2\n" +
            ") s\n" +
            "where s.`初审完成数`<>0\n" +
            "order by 5 desc\n" +
            ")v;")
    List<DayReview> getReviewUser2();


    // 初审和复审合计通过率
    @Select("select \n" +
            "w.审核月份 as p1,\n" +
            "v.`初审完成数` as p2 , \n" +
            "v.`初审完成数`-v.`初审拒绝数` as p3, \n" +
            "CONCAT(FORMAT(((v.`初审完成数`-v.`初审拒绝数`)/v.`初审完成数`* 100),2),'%')  p4,   -- 初审通过率\n" +
            "w.复审完成订单 as p5, \n" +
            "w.复审完成订单-w.复审拒绝订单-w.分流到150产品 as p6,\n" +
            "CONCAT(FORMAT((w.复审完成订单-w.复审拒绝订单-w.分流到150产品)/w.复审完成订单*100,2),'%')  p7   -- 复审通过率\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "CONCAT(YEAR(t.createTime),'-',DATE_FORMAT(t.createTime,'%m')) as 审核月份,\n" +
            "sum(if(t.`reviewerRole`='SENIOR_REVIEWER' and t.finish=1,1,0)) 复审完成订单,\n" +
            "sum(if(o.`status` in(13,14) and t.`reviewerRole`='SENIOR_REVIEWER' and t.finish=1,1,0)) 复审拒绝订单,\n" +
            "sum(case when b.orderNo is not null and reviewerRole='SENIOR_REVIEWER' then 1 else 0 end) AS 分流到150产品\n" +
            "from doit.reviewerOrderTask t\n" +
            "left join doit.manUser m on m.id=t.reviewerId\n" +
            "left join doit.ordOrder o on o.uuid=t.orderUUID\n" +
            "left join\n" +
            "(select \n" +
            "distinct orderNo\n" +
            "from doit.ordBlack \n" +
            "where remark ='PRODUCT600TO150_SENIOR_REVIEW' and disabled=1\n" +
            "and createTime>'2019-01-10'\n" +
            ") b on b.orderNo= t.orderUUID\n" +
            "where m.disabled=0 and o.disabled=0 and t.status = 0 \n" +
            "and DATE_FORMAT(t.createTime, '%Y%m' ) = DATE_FORMAT(CURDATE( ) , '%Y%m' ) -- 只统计本月累计到当前的数据\n" +
            "and m.realname not in ('胡静','郑巧玲', '周佩欣','张闪闪')\n" +
            "group by 1\n" +
            ")w\n" +
            "left join\n" +
            "(\n" +
            "select\n" +
            "CONCAT(YEAR(t.createTime),'-',DATE_FORMAT(t.createTime,'%m')) as 审核月份,\n" +
            "sum(case when reviewerRole = 'JUNIOR_REVIEWER' and finish = 1 then 1 else 0 end) as 初审完成数,\n" +
            "sum(case when reviewerRole = 'JUNIOR_REVIEWER' and finish = 1 and o.status in (13,15) then 1 else 0 end) as 初审拒绝数\n" +
            "from doit.reviewerOrderTask t\n" +
            "left join doit.manUser m on m.id=t.reviewerId\n" +
            "left join doit.ordOrder o on o.uuid=t.orderUUID\n" +
            "where m.disabled=0 and o.disabled=0 and t.status = 0 \n" +
            "and DATE_FORMAT(t.createTime, '%Y%m' ) = DATE_FORMAT(CURDATE( ) , '%Y%m' ) -- 只统计本月累计到当前的数据\n" +
            "and m.realname not in ('胡静','郑巧玲', '周佩欣','张闪闪')\n" +
            "group by 1) v\n" +
            "on w.审核月份=v.审核月份;")
    List<DayReview> getReviewUser3();


    // 每个复审人员对应D8逾期率与平均预期率的差值，数值越小代表逾期率越低
    @Select("select \n" +
            "w.Month as p1,\n" +
            "w.SC_realname as p2,   \n" +
            "w.D8_DueOrder as p3,\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "w.D8_OverdueOrder as p4,\n" +
            "CONCAT(FORMAT((w.DiffOverdueRate* 100),2),'%') as p5\n" +
            "from\n" +
            "(\n" +
            "SELECT\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.审核月份 as Month,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.复审审核员 as SC_realname,   \n" +
            "b.D8到期笔数 as D8_DueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.D8逾期笔数 as D8_OverdueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.D8逾期率- (SELECT\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime, refundTime))>7,1,0))/Count(DISTINCT d.uuid) as D8逾期率\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "FROM\tdoit.ordOrder d\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "JOIN\tdoit.reviewerOrderTask t on d.uuid=t.orderUUid\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "join doit.manUser m on t.reviewerId=m.id and m.disabled=0\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "WHERE d.status in (7,8,10,11) and borrowingCount=1 and d.disabled=0 and DATEDIFF(NOW(),refundTime)>7\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "and t.reviewerRole='SENIOR_REVIEWER' and t.finish=1\t\n" +
            "and date_format(t.createTime,'%Y%m')=date_format(DATE_SUB(curdate(), INTERVAL 2 MONTH),'%Y%m')) as DiffOverdueRate\n" +
            "from\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "((\n" +
            "SELECT \t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "CONCAT(YEAR(t.createTime),'-',DATE_FORMAT(t.createTime,'%m')) as 审核月份,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "m.realname as 复审审核员, \t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime,refundTime))>7,1,0)) as D8逾期笔数,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "Count(DISTINCT d.uuid) as D8到期笔数,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime, refundTime))>7,1,0))/Count(DISTINCT d.uuid) as D8逾期率\n" +
            "FROM doit.ordOrder d\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "JOIN doit.reviewerOrderTask t on d.uuid=t.orderUUid\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "join doit.manUser m on t.reviewerId=m.id and m.disabled=0\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "WHERE d.status in (7,8,10,11) and borrowingCount=1 and d.disabled=0 and DATEDIFF(NOW(),refundTime)>7\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "and t.reviewerRole='SENIOR_REVIEWER' and t.finish=1\t\n" +
            "and date_format(t.createTime,'%Y%m')=date_format(DATE_SUB(curdate(), INTERVAL 2 MONTH),'%Y%m')  \n" +
            "and m.realname not in ('胡静','郑巧玲', '周佩欣','张闪闪')\n" +
            "GROUP BY 1,2 \n" +
            ")b)\n" +
            "group by 1,2,3,4,5\n" +
            "order by 5 asc\n" +
            ")w\n" +
            "\n" +
            "union all\n" +
            "(\n" +
            "select \n" +
            "w.Month,\n" +
            "w.SC_realname,   \n" +
            "w.D8_DueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "w.D8_OverdueOrder,\n" +
            "CONCAT(FORMAT((w.DiffOverdueRate* 100),2),'%') as 'Difference With Average Overdue Rate'\n" +
            "from\n" +
            "(\n" +
            "SELECT\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.审核月份 as Month,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.复审审核员 as SC_realname,   \n" +
            "b.D8到期笔数 as D8_DueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.D8逾期笔数 as D8_OverdueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.D8逾期率- (SELECT\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime, refundTime))>7,1,0))/Count(DISTINCT d.uuid) as D8逾期率\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "FROM\tdoit.ordOrder d\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "JOIN\tdoit.reviewerOrderTask t on d.uuid=t.orderUUid\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "join doit.manUser m on t.reviewerId=m.id and m.disabled=0\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "WHERE d.status in (7,8,10,11) and borrowingCount=1 and d.disabled=0 and DATEDIFF(NOW(),refundTime)>7\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "and t.reviewerRole='SENIOR_REVIEWER' and t.finish=1\t\n" +
            "and date_format(t.createTime,'%Y%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y%m')) as DiffOverdueRate\n" +
            "from\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "((\n" +
            "SELECT \t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "CONCAT(YEAR(t.createTime),'-',DATE_FORMAT(t.createTime,'%m')) as 审核月份,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "m.realname as 复审审核员, \t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime,refundTime))>7,1,0)) as D8逾期笔数,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "Count(DISTINCT d.uuid) as D8到期笔数,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime, refundTime))>7,1,0))/Count(DISTINCT d.uuid) as D8逾期率\n" +
            "FROM doit.ordOrder d\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "JOIN doit.reviewerOrderTask t on d.uuid=t.orderUUid\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "join doit.manUser m on t.reviewerId=m.id and m.disabled=0\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "WHERE d.status in (7,8,10,11) and borrowingCount=1 and d.disabled=0 and DATEDIFF(NOW(),refundTime)>7\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "and t.reviewerRole='SENIOR_REVIEWER' and t.finish=1\n" +
            "and date_format(t.createTime,'%Y%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y%m')  \n" +
            "and m.realname not in ('胡静','郑巧玲', '周佩欣','张闪闪')\n" +
            "GROUP BY 1,2 \n" +
            ")b)\n" +
            "group by 1,2,3,4,5\n" +
            "order by 5 asc\n" +
            ")w\n" +
            ")\n" +
            "\n" +
            "union all\n" +
            "\n" +
            "(\n" +
            "select \n" +
            "w.Month,\n" +
            "w.SC_realname,   \n" +
            "w.D8_DueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "w.D8_OverdueOrder,\n" +
            "CONCAT(FORMAT((w.DiffOverdueRate* 100),2),'%') as 'Difference With Average Overdue Rate'\n" +
            "from\n" +
            "(\n" +
            "SELECT\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.审核月份 as Month,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.复审审核员 as SC_realname,   \n" +
            "b.D8到期笔数 as D8_DueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.D8逾期笔数 as D8_OverdueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.D8逾期率- (SELECT\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime, refundTime))>7,1,0))/Count(DISTINCT d.uuid) as D8逾期率\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "FROM\tdoit.ordOrder d\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "JOIN\tdoit.reviewerOrderTask t on d.uuid=t.orderUUid\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "join doit.manUser m on t.reviewerId=m.id and m.disabled=0\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "WHERE d.status in (7,8,10,11) and borrowingCount=1 and d.disabled=0 and DATEDIFF(NOW(),refundTime)>7\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "and t.reviewerRole='SENIOR_REVIEWER' and t.finish=1\t\n" +
            "and DATE_FORMAT(t.createTime, '%Y%m' ) = DATE_FORMAT(CURDATE( ) , '%Y%m' )) as DiffOverdueRate   -- 'Difference With The Average Overdue Rate'     -- 与平均逾期率差值 \n" +
            "from\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "((\n" +
            "SELECT \t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "CONCAT(YEAR(t.createTime),'-',DATE_FORMAT(t.createTime,'%m')) as 审核月份,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "m.realname as 复审审核员, \t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime,refundTime))>7,1,0)) as D8逾期笔数,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "Count(DISTINCT d.uuid) as D8到期笔数,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime, refundTime))>7,1,0))/Count(DISTINCT d.uuid) as D8逾期率\n" +
            "FROM doit.ordOrder d\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "JOIN doit.reviewerOrderTask t on d.uuid=t.orderUUid\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "join doit.manUser m on t.reviewerId=m.id and m.disabled=0\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "WHERE d.status in (7,8,10,11) and borrowingCount=1 and d.disabled=0 and DATEDIFF(NOW(),refundTime)>7\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "and t.reviewerRole='SENIOR_REVIEWER' and t.finish=1\t\n" +
            "and DATE_FORMAT(t.createTime, '%Y%m' ) = DATE_FORMAT(CURDATE( ) , '%Y%m' ) -- 只统计本月累计到当前的数据 \n" +
            "\n" +
            "and m.realname not in ('胡静','郑巧玲', '周佩欣','张闪闪')\n" +
            "GROUP BY 1,2 \n" +
            ")b)\n" +
            "group by 1,2,3,4,5\n" +
            "order by 5 asc\n" +
            ")w\n" +
            ");")
    List<DayReview> getReviewUser4();


    // 每个初审人员对应D8逾期率与平均预期率的差值，数值越小代表逾期率越低
    @Select("select \n" +
            "v.Month as p1,\n" +
            "v.FC_realname as p2,   \n" +
            "v.D8_DueOrder as p3,\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "v.D8_OverdueOrder as p4,\n" +
            "CONCAT(FORMAT((v.DiffOverdueRate* 100),2),'%') as p5\n" +
            "from\n" +
            "(SELECT\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.审核月份 as Month,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.初审审核员 as FC_realname,   \n" +
            "b.D8到期笔数 as D8_DueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.D8逾期笔数 as D8_OverdueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.D8逾期率- (SELECT\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime, refundTime))>7,1,0))/Count(DISTINCT d.uuid) as D8逾期率\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "FROM\tdoit.ordOrder d\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "JOIN\tdoit.reviewerOrderTask t on d.uuid=t.orderUUid\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "join doit.manUser m on t.reviewerId=m.id and m.disabled=0\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "WHERE d.status in (7,8,10,11) and borrowingCount=1 and d.disabled=0 and DATEDIFF(NOW(),refundTime)>7\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "and t.reviewerRole='JUNIOR_REVIEWER' and t.finish=1\t\n" +
            "-- and DATE_FORMAT(t.createTime, '%Y%m' ) = DATE_FORMAT(CURDATE( ) , '%Y%m' )) as DiffOverdueRate -- 'Difference With The Average Overdue Rate' -- 与平均逾期率差值\n" +
            "and date_format(t.createTime,'%Y%m')=date_format(DATE_SUB(curdate(), INTERVAL 2 MONTH),'%Y%m')) as DiffOverdueRate\n" +
            "from\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "((\n" +
            "SELECT \t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "CONCAT(YEAR(t.createTime),'-',DATE_FORMAT(t.createTime,'%m')) as 审核月份,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "m.realname as 初审审核员, \t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime,refundTime))>7,1,0)) as D8逾期笔数,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "Count(DISTINCT d.uuid) as D8到期笔数,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime, refundTime))>7,1,0))/Count(DISTINCT d.uuid) as D8逾期率\n" +
            "FROM doit.ordOrder d\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "JOIN doit.reviewerOrderTask t on d.uuid=t.orderUUid\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "join doit.manUser m on t.reviewerId=m.id and m.disabled=0\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "WHERE d.status in (7,8,10,11) and borrowingCount=1 and d.disabled=0 and DATEDIFF(NOW(),refundTime)>7\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "and t.reviewerRole='JUNIOR_REVIEWER' and t.finish=1\t\n" +
            "-- and DATE_FORMAT(t.createTime, '%Y%m' ) = DATE_FORMAT(CURDATE( ) , '%Y%m' ) -- 只统计本月累计到当前的数据\n" +
            "and date_format(t.createTime,'%Y%m')=date_format(DATE_SUB(curdate(), INTERVAL 2 MONTH),'%Y%m')\n" +
            "and m.realname not in ('胡静','郑巧玲', '周佩欣','张闪闪')\n" +
            "GROUP BY 1,2 \n" +
            ")b)\n" +
            "group by 1,2,3,4,5\n" +
            "order by 5 asc\n" +
            ")v\n" +
            "\n" +
            "union all\n" +
            "(\n" +
            "select \n" +
            "v.Month,\n" +
            "v.FC_realname,   \n" +
            "v.D8_DueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "v.D8_OverdueOrder,\n" +
            "CONCAT(FORMAT((v.DiffOverdueRate* 100),2),'%') as 'Difference With Average Overdue Rate'\n" +
            "from\n" +
            "(SELECT\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.审核月份 as Month,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.初审审核员 as FC_realname,   \n" +
            "b.D8到期笔数 as D8_DueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.D8逾期笔数 as D8_OverdueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.D8逾期率- (SELECT\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime, refundTime))>7,1,0))/Count(DISTINCT d.uuid) as D8逾期率\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "FROM\tdoit.ordOrder d\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "JOIN\tdoit.reviewerOrderTask t on d.uuid=t.orderUUid\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "join doit.manUser m on t.reviewerId=m.id and m.disabled=0\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "WHERE d.status in (7,8,10,11) and borrowingCount=1 and d.disabled=0 and DATEDIFF(NOW(),refundTime)>7\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "and t.reviewerRole='JUNIOR_REVIEWER' and t.finish=1\t\n" +
            "and date_format(t.createTime,'%Y%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y%m')) as DiffOverdueRate -- 'Difference With The Average Overdue Rate' -- 与平均逾期率差值\n" +
            "from\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "((\n" +
            "SELECT \t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "CONCAT(YEAR(t.createTime),'-',DATE_FORMAT(t.createTime,'%m')) as 审核月份,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "m.realname as 初审审核员, \t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime,refundTime))>7,1,0)) as D8逾期笔数,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "Count(DISTINCT d.uuid) as D8到期笔数,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime, refundTime))>7,1,0))/Count(DISTINCT d.uuid) as D8逾期率\n" +
            "FROM doit.ordOrder d\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "JOIN doit.reviewerOrderTask t on d.uuid=t.orderUUid\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "join doit.manUser m on t.reviewerId=m.id and m.disabled=0\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "WHERE d.status in (7,8,10,11) and borrowingCount=1 and d.disabled=0 and DATEDIFF(NOW(),refundTime)>7\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "and t.reviewerRole='JUNIOR_REVIEWER' and t.finish=1\t\n" +
            "and date_format(t.createTime,'%Y%m')=date_format(DATE_SUB(curdate(), INTERVAL 1 MONTH),'%Y%m')\n" +
            "and m.realname not in ('胡静','郑巧玲', '周佩欣','张闪闪')\n" +
            "GROUP BY 1,2 \n" +
            ")b)\n" +
            "group by 1,2,3,4,5\n" +
            "order by 5 asc\n" +
            ")v\n" +
            ")\n" +
            "union all\n" +
            "( \n" +
            "select \n" +
            "v.Month,\n" +
            "v.FC_realname,   \n" +
            "v.D8_DueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "v.D8_OverdueOrder,\n" +
            "CONCAT(FORMAT((v.DiffOverdueRate* 100),2),'%') as 'Difference With Average Overdue Rate'\n" +
            "from\n" +
            "(SELECT\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.审核月份 as Month,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.初审审核员 as FC_realname,   \n" +
            "b.D8到期笔数 as D8_DueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.D8逾期笔数 as D8_OverdueOrder,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "b.D8逾期率- (SELECT\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime, refundTime))>7,1,0))/Count(DISTINCT d.uuid) as D8逾期率\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "FROM\tdoit.ordOrder d\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "JOIN\tdoit.reviewerOrderTask t on d.uuid=t.orderUUid\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "join doit.manUser m on t.reviewerId=m.id and m.disabled=0\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "WHERE d.status in (7,8,10,11) and borrowingCount=1 and d.disabled=0 and DATEDIFF(NOW(),refundTime)>7\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "and t.reviewerRole='JUNIOR_REVIEWER' and t.finish=1\t\n" +
            "and DATE_FORMAT(t.createTime, '%Y%m' ) = DATE_FORMAT(CURDATE( ) , '%Y%m' )) as DiffOverdueRate -- 'Difference With The Average Overdue Rate' -- 与平均逾期率差值\n" +
            "from\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "((\n" +
            "SELECT \t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "CONCAT(YEAR(t.createTime),'-',DATE_FORMAT(t.createTime,'%m')) as 审核月份,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "m.realname as 初审审核员, \t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime,refundTime))>7,1,0)) as D8逾期笔数,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "Count(DISTINCT d.uuid) as D8到期笔数,\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "SUM(IF(IF(d.status=8, DATEDIFF(NOW(),refundTime), DATEDIFF(actualRefundTime, refundTime))>7,1,0))/Count(DISTINCT d.uuid) as D8逾期率\n" +
            "FROM doit.ordOrder d\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "JOIN doit.reviewerOrderTask t on d.uuid=t.orderUUid\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "join doit.manUser m on t.reviewerId=m.id and m.disabled=0\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "WHERE d.status in (7,8,10,11) and borrowingCount=1 and d.disabled=0 and DATEDIFF(NOW(),refundTime)>7\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "and t.reviewerRole='JUNIOR_REVIEWER' and t.finish=1\t\n" +
            "and DATE_FORMAT(t.createTime, '%Y%m' ) = DATE_FORMAT(CURDATE( ) , '%Y%m' ) -- 只统计本月累计到当前的数据\n" +
            "and m.realname not in ('胡静','郑巧玲', '周佩欣','张闪闪')\n" +
            "GROUP BY 1,2 \n" +
            ")b)\n" +
            "group by 1,2,3,4,5\n" +
            "order by 5 asc\n" +
            ")v\n" +
            "); ")
    List<DayReview> getReviewUser5();


    // 每日新老户放款情况
    @Select("select\n" +
            "lendingDay as date,                                                                     -- 放款日期\n" +
            "format(orders,0) as orders,                                                             -- 总单量\n" +
            "format(newOrder,0) as newOrder,                                                         -- 新户单量\n" +
            "format(oldOrder,0) as oldOrder,                                                         -- 老户单量\n" +
            "concat(round(newOrder/orders*10,1),' : ',round(oldOrder/orders*10,1)) as nor,           -- 新老比\n" +
            "format(round(lendAmount/2000,0),0) as lendAmount,                                       -- 总放款\n" +
            "format(round(newAmount/2000,0),0) as newAmount,                                         -- 新户放款\n" +
            "format(round(oldAmount/2000,0),0) as oldAmount,                                         -- 老户放款\n" +
            "format(billOrders,0) as billOrders,                                                     -- 分期订单\n" +
            "format(round(billAmount/2000,0),0) as billAmount,                                       -- 分期订单金额\n" +
            "format(Extension,0) as extension,                                                       -- 展期订单\n" +
            "format(round(ExtAmount/2000,0),0) as extAmount                                          -- 展期订单金额\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "date(lendingtime) as lendingDay,\n" +
            "sum(case when orderType in (0,2,3) then 1 else 0 end) as orders,\n" +
            "sum(case when orderType in (0,2,3) and borrowingcount = 1 then 1 else 0 end) as newOrder,\n" +
            "sum(case when orderType in (0,2,3) and borrowingcount > 1 then 1 else 0 end) as oldOrder,\n" +
            "sum(case when orderType in (0,2,3) then amountApply else 0 end) as lendAmount,\n" +
            "sum(case when orderType in (0,2,3) and borrowingcount = 1 then amountApply else 0 end) as newAmount,\n" +
            "sum(case when orderType in (0,2,3) and borrowingcount > 1 then amountApply else 0 end) as oldAmount,\n" +
            "sum(case when orderType = 3 then 1 else 0 end) as billOrders,\n" +
            "sum(case when orderType = 3 then amountApply else 0 end) as billAmount,\n" +
            "sum(case when orderType = 1 then 1 else 0 end) as Extension,\n" +
            "sum(case when orderType = 1 then amountApply else 0 end) as ExtAmount\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and date(lendingtime) between date_sub(curdate(),interval 14 day) and date_sub(curdate(),interval 1 day)\n" +
            "and status in (7,8,10,11)\n" +
            "group by lendingDay\n" +
            ") ord\n" +
            "\n" +
            "union all\n" +
            "select\n" +
            "if(Months = month(date_sub(curdate(),interval 1 day)),'本月(CNY)','上月(CNY)') as date, \n" +
            "format(orders,0) as orders,\n" +
            "format(newOrder,0) as newOrder,\n" +
            "format(oldOrder,0) as oldOrder,\n" +
            "concat(round(newOrder/orders*10,1),' : ',round(oldOrder/orders*10,1)) as VS,\n" +
            "format(round(lendAmount/2000,0),0) as lendAmount,\n" +
            "format(round(newAmount/2000,0),0) as newAmount,\n" +
            "format(round(oldAmount/2000,0),0) as oldAmount,\n" +
            "format(billOrders,0) as billOrders,\n" +
            "format(round(billAmount/2000,0),0) as billAmount,\n" +
            "format(Extension,0) as Extension,\n" +
            "format(round(ExtAmount/2000,0),0) as ExtAmount\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "month(lendingTime) as Months,\n" +
            "sum(case when orderType in (0,2,3) then 1 else 0 end) as orders,\n" +
            "sum(case when orderType in (0,2,3) and borrowingcount = 1 then 1 else 0 end) as newOrder,\n" +
            "sum(case when orderType in (0,2,3) and borrowingcount > 1 then 1 else 0 end) as oldOrder,\n" +
            "sum(case when orderType in (0,2,3) then amountApply else 0 end) as lendAmount,\n" +
            "sum(case when orderType in (0,2,3) and borrowingcount = 1 then amountApply else 0 end) as newAmount,\n" +
            "sum(case when orderType in (0,2,3) and borrowingcount > 1 then amountApply else 0 end) as oldAmount,\n" +
            "sum(case when orderType = 3 then 1 else 0 end) as billOrders,\n" +
            "sum(case when orderType = 3 then amountApply else 0 end) as billAmount,\n" +
            "sum(case when orderType = 1 then 1 else 0 end) as Extension,\n" +
            "sum(case when orderType = 1 then amountApply else 0 end) as ExtAmount\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and date_format(lendingTime,'%Y-%m') in (date_format(date_sub(curdate(),interval 1 day),'%Y-%m')\n" +
            ",date_format(date_sub(date_sub(curdate(),interval 1 day),interval 1 month),'%Y-%m'))\n" +
            "and date(lendingtime) <= date_sub(curdate(),interval 1 day)\n" +
            "and status in (7,8,10,11)\n" +
            "group by Months\n" +
            ") ord;")
    List<DayLoanStatus> getDayLoanStatus();

    //  借款额度分布
    @Select("select\n" +
            "lendDay,                                            -- 放款日期\n" +
            "amountApply,                                        -- 放款金额\n" +
            "orders,                                             -- 放款单量\n" +
            "concat(round(orders/allorder*100,2),'%') as rate,   -- 占比\n" +
            "newOrder,                                           -- 新户\n" +
            "oldOrder,                                           -- 老户\n" +
            "concat(round(newOrder/orders*10,1),' : ',round(oldOrder/orders*10,1)) as nor -- 新老比\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "date(lendingTime) as lendDay,\n" +
            "amountApply,\n" +
            "sum(1) as orders,\n" +
            "sum(case when borrowingCount = 1 then 1 else 0 end) as newOrder,\n" +
            "sum(case when borrowingCount > 1 then 1 else 0 end) as oldOrder\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (7,8,10,11)\n" +
            "and ordertype in (0,2,3)\n" +
            "and date(lendingTime) = date_sub(curdate(),interval 1 day)\n" +
            "group by lendDay,amountApply\n" +
            ") ord\n" +
            "cross join\n" +
            "(\n" +
            "select\n" +
            "sum(1) as allorder\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (7,8,10,11)\n" +
            "and ordertype in (0,2,3)\n" +
            "and date(lendingTime) = date_sub(curdate(),interval 1 day)\n" +
            ") whole;")
    List<DayLoanAmout> getDayLoanAmout();

    // 在借率
    @Select("select \n" +
            "now() as createtime,                                                              -- 创建时间\n" +
            "lendUser,                                                                         -- 放款成功用户\n" +
            "repayUser + overUser as unableUser,                                               -- 失去资格用户\n" +
            "lendUser - repayUser - overUser as greenUser,                                     -- 可借款用户\n" +
            "borUser,                                                                          -- 借款中用户\n" +
            "concat(round(borUser/(lendUser - repayUser - overUser)*100,2),'%') as borRate     -- 在借率\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "count(ord.userId) as lendUser,\n" +
            "count(repay.userId) as repayUser,\n" +
            "sum(case when repay.userId is null and over.userId is not null then 1 else 0 end) as overUser,\n" +
            "count(bor.userId) as borUser\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "useruuid as userId\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (7,8,10,11) \n" +
            "and borrowingCount = 1\n" +
            "and ordertype in (0,2,3)\n" +
            "and date(lendingtime) <= date_sub(curdate(),interval 1 day)\n" +
            "group by userId\n" +
            ") ord\n" +
            "left join  \n" +
            "(\n" +
            "select \n" +
            "useruuid as userId,\n" +
            "max(datediff(actualRefundTime,refundTime)) as dayType\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (10,11) \n" +
            "group by useruuid\n" +
            "having dayType >= 10\n" +
            ") repay on repay.userId = ord.userId\n" +
            "left join  \n" +
            "(\n" +
            "select \n" +
            "useruuid as userId,\n" +
            "max(datediff(curdate(),refundTime)) as dayType\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (8) \n" +
            "group by useruuid\n" +
            "having dayType >= 10\n" +
            ") over on over.userId = ord.userId \n" +
            "left join  \n" +
            "(\n" +
            "select \n" +
            "useruuid as userId,\n" +
            "max(datediff(curdate(),refundTime)) as dayType\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (7,8) \n" +
            "group by useruuid\n" +
            "having dayType < 10\n" +
            ") bor on bor.userId = ord.userId\n" +
            ") result;")
    List<DayLoanInRate> getDayLoanInRate();

    // 沉默用户时长分布
    @Select("select\n" +
            "now() as createtime,                                                                       -- 创建时间\n" +
            "case \n" +
            "when timestampdiff(hour,repayTime,now()) between 0 and 23 then '0-24h'\n" +
            "when timestampdiff(hour,repayTime,now()) between 24 and 3*24-1 then '1-3天'\n" +
            "when timestampdiff(hour,repayTime,now()) between 3*24 and 10*24-1 then '3-10天'\n" +
            "when timestampdiff(hour,repayTime,now()) between 10*24 and 30*24-1 then '10-30天'\n" +
            "when timestampdiff(hour,repayTime,now()) >= 30*24 then '超过1个月' end as timeLength,      -- 沉默时长\n" +
            "count(1) as silenceUser                                                                    -- 沉默用户数\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "max(actualRefundTime) as repayTime,\n" +
            "useruuid as userId\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (10,11) \n" +
            "and actualRefundTime is not null\n" +
            "group by useruuid\n" +
            ") orda\n" +
            "left join \n" +
            "(\n" +
            "select useruuid\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (7,8) \n" +
            ") ordb on ordb.useruuid = orda.userId\n" +
            "left join  \n" +
            "(\n" +
            "select \n" +
            "useruuid,\n" +
            "max(datediff(actualRefundTime,refundTime)) as dayType\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (10,11) \n" +
            "group by useruuid\n" +
            "having dayType >= 10\n" +
            ") ordc on ordc.useruuid = orda.userId\n" +
            "left join \n" +
            "(\n" +
            "select userUuid\n" +
            "from usrBlackList\n" +
            "where disabled = 0\n" +
            ") black on black.userUuid = orda.userId\n" +
            "where ordb.useruuid is null and ordc.userUuid is null and black.userUuid is null\n" +
            "group by timeLength\n" +
            "order by \n" +
            "case  \n" +
            "when timeLength = '0-24h' then 1 \n" +
            "when timeLength = '1-3天' then 2 \n" +
            "when timeLength = '3-10天' then 3 \n" +
            "when timeLength = '10-30天' then 4\n" +
            "when timeLength = '超过1个月' then 5 \n" +
            "end;")
    List<DayLoanSilence> getDayLoanSilence();

    // 借款金额分布
    @Select("SELECT\n" +
            "now() AS createtime, -- 创建时间\n" +
            "amountApply,         -- 申请金额\n" +
            "orders,              -- 订单量\n" +
            "concat(round(Orders/total*100,2),'%') AS ratio  -- 各金额订单占比\n" +
            "FROM(\n" +
            "SELECT\n" +
            "amountApply,\n" +
            "COUNT(1) AS Orders\n" +
            "FROM ordOrder\n" +
            "WHERE\n" +
            "disabled = 0\n" +
            "AND `status` IN (7,10)\n" +
            "and orderType in (0,2)\n" +
            "and date(lendingTime) = date_sub(curdate(),interval 1 day)\n" +
            "GROUP BY amountApply\n" +
            ") t\n" +
            "cross JOIN\n" +
            "(\n" +
            "SELECT\n" +
            "COUNT(1) AS total\n" +
            "FROM ordOrder\n" +
            "WHERE\n" +
            "disabled = 0\n" +
            "AND `status` IN (7,10)\n" +
            "and orderType in (0,2)\n" +
            "and date(lendingTime) = date_sub(curdate(),interval 1 day)\n" +
            ")t1;")
    List<DayLoanMoney> getDayLoanMoney();

    // 借款期限分布
    @Select("SELECT\n" +
            "now() as createtime,  -- 创建时间\n" +
            "borrowingTerm,        -- 借款期限\n" +
            "orders,               -- 订单量\n" +
            "concat(round(Orders/total*100,2),'%') AS ratio   -- 各期限订单占比\n" +
            "FROM(\n" +
            "SELECT\n" +
            "borrowingTerm,\n" +
            "COUNT(1) AS Orders\n" +
            "FROM ordOrder\n" +
            "WHERE\n" +
            "disabled = 0\n" +
            "AND `status` IN (7,10)\n" +
            "and orderType in (0,2)\n" +
            "AND date(lendingTime) = date_sub(curdate(),interval 1 day)\n" +
            "GROUP BY borrowingTerm\n" +
            ") t\n" +
            "cross JOIN\n" +
            "(\n" +
            "SELECT\n" +
            "COUNT(1) AS total\n" +
            "FROM ordOrder\n" +
            "WHERE\n" +
            "disabled = 0\n" +
            "AND `status` IN (7,10)\n" +
            "and orderType in (0,2)\n" +
            "AND date(lendingTime) = date_sub(curdate(),interval 1 day)\n" +
            ")t1;")
    List<DayLoanTime> getDayLoanTime();

    // cashcash获客成本
    @Select("select \n" +
            "if(createDay is null,'Total',createDay) as createDay,              -- 进件日期\n" +
            "newOrders,                                                         -- 新户进件数\n" +
            "newSubmit,                                                         -- 新户提交数\n" +
            "sucNew,                                                            -- 新户放款数               \n" +
            "round(newOrders*0.75/sucNew,2) as newCost,                         -- 新户成本\n" +
            "concat(round(newSubmit/newOrders*100,2),'%') as newsubmitRatio,    -- 新户进件提交率\n" +
            "concat(round(sucNew/newSubmit*100,2),'%') as lendingRatio,         -- 新户提交放款率\n" +
            "oldOrders,                                                         -- 老户进件数\n" +
            "sucOld,                                                            -- 老户放款数\n" +
            "totalOrders                                                        -- 总进件数\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "date(createTime) as createDay,\n" +
            "count(1) as totalOrders,\n" +
            "sum(case when borrowingCount = 1 then 1 else 0 end) as newOrders,\n" +
            "sum(case when borrowingCount = 1 and status >= 2 then 1 else 0 end) as newSubmit,\n" +
            "sum(case when borrowingCount = 1 and status in (7,8,10,11) then 1 else 0 end) as sucNew,\n" +
            "sum(case when borrowingCount > 1 then 1 else 0 end) as oldOrders,\n" +
            "sum(case when borrowingCount > 1 and status in (7,8,10,11) then 1 else 0 end) as sucOld\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "createTime,\n" +
            "orderNo\n" +
            "from \n" +
            "externalOrderRelation\n" +
            "where channel = 'CASHCASH' \n" +
            "and date(createtime) between date_sub(curdate(),interval 20 day) and date_sub(curdate(),interval 1 day)\n" +
            ") eor\n" +
            "inner join \n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "borrowingCount,\n" +
            "status\n" +
            "from \n" +
            "ordOrder\n" +
            ") ord on ord.uuid = eor.orderNo\n" +
            "group by createDay with rollup\n" +
            ") result;")
    List<DayLoanCash2> getDayLoanCash2();


    // 20万产品贷后表现
    @Select("select \n" +
            "ifnull(refundDay,'合计') as refundDay,                                                               -- 到期日\n" +
            "totalorders,                                                                                                      -- 到期订单量\n" +
            "repayNum,                                                                                                       -- 已还款单量\n" +
            "repeatNum,                                                                                                      -- 复借单量\n" +
            "concat(round(repeatNum/repayNum*100,2),'%') as repeatRatio,                                            -- 复借率\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,dueOrdersD1,'-') as dueOrdersD1,  -- D1应还单量\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,D1Num,'-') as d1Num,              -- D1逾期单量\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,concat(round(D1Num/dueOrdersD1*100,2),'%'),'-') as d1,  -- D1逾期率\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,dueOrdersD8,'-') as dueOrdersD8,  -- D8应还单量\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,D8Num,'-') as d8Num,              -- D8逾期单量\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,concat(round(D8Num/dueOrdersD8*100,2),'%'),'-') as d8 -- D8逾期率\n" +
            "from(\n" +
            "select \n" +
            "refundDay,\n" +
            "count(a.uuid) as totalorders,\n" +
            "sum(case when status in (10,11) then 1 else 0 end) as repayNum,\n" +
            "sum(case when datediff(curdate(),refundDay) < 1 then 0 else 1 end) as dueOrdersD1,\n" +
            "sum(case when step >= 1 then 1 else 0 end) as D1Num,\n" +
            "sum(case when datediff(curdate(),refundDay) < 8 then 0 else 1 end) as dueOrdersD8,\n" +
            "sum(case when step >= 8 then 1 else 0 end) as D8Num,\n" +
            "count(b.uuid) as repeatNum\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "date(refundTime) as refundDay, \n" +
            "useruuid,\n" +
            "uuid,\n" +
            "borrowingCount,\n" +
            "status,\n" +
            "case when status = 8 then datediff(curdate(),refundtime) else datediff(actualRefundTime,refundtime) end as step\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,2)\n" +
            "and amountApply in (200000)\n" +
            "and date(refundTime)  BETWEEN date_sub(CURDATE(),interval 30 day) and CURDATE() \n" +
            ") a\n" +
            "left join \n" +
            "(\n" +
            "select \n" +
            "useruuid,\n" +
            "uuid,\n" +
            "borrowingcount\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,2)\n" +
            "and date(lendingTime) BETWEEN date_sub(CURDATE(),interval 44 day) and CURDATE() \n" +
            ") b on b.useruuid = a.useruuid and b.borrowingCount = a.borrowingCount +1 \n" +
            "group by refundDay with rollup\n" +
            ")result;")
    List<PerformanceWith20wProduct> getPerformanceWith20wProduct();

    // 20万产品用户第二次借款贷后表现
    @Select("select \n" +
            "ifnull(refundDay,'合计') as refundDay,                                                                             -- 第2次到期日\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,dueOrdersD1,'-') as dueOrdersD1,                        -- D1应还单量\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,D1Num,'-') as d1Num,                                    -- D1逾期单量\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,concat(round(D1Num/dueOrdersD1*100,2),'%'),'-') as d1,  -- D1逾期率\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,dueOrdersD8,'-') as dueOrdersD8,                        -- D8应还单量\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,D8Num,'-') as d8Num,                                    -- D8逾期单量\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,concat(round(D8Num/dueOrdersD8*100,2),'%'),'-') as d8   -- D8逾期率\n" +
            "from(\n" +
            "select \n" +
            "refundDay,\n" +
            "count(a.uuid) as totalorders,\n" +
            "sum(case when status in (10,11) then 1 else 0 end) as repayNum,\n" +
            "sum(case when datediff(curdate(),refundDay) < 1 then 0 else 1 end) as dueOrdersD1,\n" +
            "sum(case when step >= 1 then 1 else 0 end) as D1Num,\n" +
            "sum(case when datediff(curdate(),refundDay) < 8 then 0 else 1 end) as dueOrdersD8,\n" +
            "sum(case when step >= 8 then 1 else 0 end) as D8Num,\n" +
            "count(b.uuid) as repeatNum\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "date(refundTime) as refundDay, \n" +
            "useruuid,\n" +
            "uuid,\n" +
            "status,\n" +
            "borrowingcount,\n" +
            "case when status = 8 then datediff(curdate(),refundtime) else datediff(actualRefundTime,refundtime) end as step\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,2)\n" +
            "and date(refundTime) between date_sub(curdate(),interval 30 day) and date_sub(curdate(),interval 1 day)\n" +
            ") a\n" +
            "inner join \n" +
            "(\n" +
            "select \n" +
            "useruuid,\n" +
            "uuid,\n" +
            "borrowingCount\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,2)\n" +
            "and amountApply in (200000)\n" +
            ") b on b.useruuid = a.useruuid and b.borrowingCount = a.borrowingCount - 1\n" +
            "group by refundDay with rollup\n" +
            ")result;")
    List<PerformanceWith20wProduct2> getPerformanceWith20wProduct2();

    // 20万产品用户第三次借款贷后表现
    @Select("select \n" +
            "ifnull(refundDay,'合计') as refundDay,                                                                             -- 第3次到期日\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,dueOrdersD1,'-') as dueOrdersD1,                        -- D1应还单量\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,D1Num,'-') as d1Num,                                    -- D1逾期单量\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,concat(round(D1Num/dueOrdersD1*100,2),'%'),'-') as d1,  -- D1逾期率\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,dueOrdersD8,'-') as dueOrdersD8,                        -- D8应还单量\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,D8Num,'-') as d8Num,                                    -- D8逾期单量\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,concat(round(D8Num/dueOrdersD8*100,2),'%'),'-') as d8   -- D8逾期率\n" +
            "from(\n" +
            "select \n" +
            "refundDay,\n" +
            "count(a.uuid) as totalorders,\n" +
            "sum(case when status in (10,11) then 1 else 0 end) as repayNum,\n" +
            "sum(case when datediff(curdate(),refundDay) < 1 then 0 else 1 end) as dueOrdersD1,\n" +
            "sum(case when step >= 1 then 1 else 0 end) as D1Num,\n" +
            "sum(case when datediff(curdate(),refundDay) < 8 then 0 else 1 end) as dueOrdersD8,\n" +
            "sum(case when step >= 8 then 1 else 0 end) as D8Num,\n" +
            "count(b.uuid) as repeatNum\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "date(refundTime) as refundDay, \n" +
            "useruuid,\n" +
            "uuid,\n" +
            "status,\n" +
            "borrowingcount,\n" +
            "case when status = 8 then datediff(curdate(),refundtime) else datediff(actualRefundTime,refundtime) end as step\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,2)\n" +
            "and date(refundTime) between date_sub(curdate(),interval 30 day) and date_sub(curdate(),interval 1 day)\n" +
            ") a\n" +
            "inner join \n" +
            "(\n" +
            "select \n" +
            "useruuid,\n" +
            "uuid,\n" +
            "borrowingCount\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,2)\n" +
            "and amountApply in (200000)\n" +
            ") b on b.useruuid =  a.useruuid and b.borrowingCount = a.borrowingCount - 2\n" +
            "group by refundDay with rollup\n" +
            ")result;")
    List<PerformanceWith20wProduct2> getPerformanceWith20wProduct3();

    // 10万产品贷后表现
    @Select("select \n" +
            "ifnull(refundDay,'合计') as refundDay,                                                              -- 第1次到期日\n" +
            "orders,                                                                                             -- 到期单量\n" +
            "resolved,                                                                                           -- 已还单量\n" +
            "reborrow,                                                                                           -- 复借单量\n" +
            "concat(round(reborrow/resolved*100,2),'%') as reborrowRate,                                         -- 复借率\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,dueOrdersD1,'-') as dueOrdersD1,         -- D1应还单量\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,reachD1,'-') as reachD1,                 -- D1逾期单量\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,concat(round(D1*100,2),'%'),'-') as D1,  -- D1逾期率\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,dueOrdersD8,'-') as dueOrdersD8,         -- D8应还单量\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,reachD8,'-') as reachD8,                 -- D8逾期单量\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,concat(round(D8*100,2),'%'),'-') as D8   -- D8逾期率\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "refundDay,\n" +
            "count(a.uuid) as orders,\n" +
            "sum(case when status in (10,11) then 1 else 0 end) as resolved,\n" +
            "count(b.uuid) as reborrow,\n" +
            "sum(dueOrdersD1) as dueOrdersD1,\n" +
            "sum(reachD1) as reachD1,\n" +
            "sum(dueOrdersD8) as dueOrdersD8,\n" +
            "sum(reachD8) as reachD8,\n" +
            "sum(reachD1)/sum(dueOrdersD1) as D1,\n" +
            "sum(reachD8)/sum(dueOrdersD8) as D8\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "date(refundTime) as refundDay, \n" +
            "useruuid,\n" +
            "borrowingCount,\n" +
            "status,\n" +
            "uuid\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,2)\n" +
            "and amountApply in (100000)\n" +
            "and date(refundTime) between date_sub(curdate(),interval 16 day) and date_sub(curdate(),interval -3 day)\n" +
            ") a\n" +
            "left join \n" +
            "(\n" +
            "select \n" +
            "date(lendingtime) as lendingDay, \n" +
            "useruuid,\n" +
            "borrowingCount,\n" +
            "uuid\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,2)\n" +
            "and date(lendingtime) >= '2018-11-15'\n" +
            ") b on b.useruuid = a.useruuid and b.borrowingCount = a.borrowingCount + 1\n" +
            "left join \n" +
            "(\n" +
            "select\n" +
            "uuid,\n" +
            "case when datediff(curdate(),refundDay) < 1 then 0 else 1 end as dueOrdersD1,\n" +
            "case when step >= 1 then 1 else 0 end as reachD1,\n" +
            "case when datediff(curdate(),refundDay) < 8 then 0 else 1 end as dueOrdersD8,\n" +
            "case when step >= 8 then 1 else 0 end as reachD8\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "userUuid,\n" +
            "uuid,\n" +
            "case when status in (7,8) then datediff(curdate(),refundtime) else datediff(actualRefundTime,refundtime) end as step,\n" +
            "date(refundtime) as refundDay,\n" +
            "case when status in (7,8) then '' else date(actualRefundTime) end as actualDay\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,2)\n" +
            ") ord\n" +
            ") c on c.uuid = a.uuid\n" +
            "group by refundDay with rollup\n" +
            ") result;")
    List<PerformanceWith10wProduct> getPerformanceWith10wProduct();

    // 10万产品用户第二次借款贷后表现
    @Select("select \n" +
            "ifnull(refundDay,'合计') as refundDay,                                                                             -- 第2次到期日\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,dueOrdersD1,'-') as dueOrdersD1,                        -- D1应还单量\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,D1Num,'-') as D1Num,                                    -- D1逾期单量\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,concat(round(D1Num/dueOrdersD1*100,2),'%'),'-') as D1,  -- D1逾期率\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,dueOrdersD8,'-') as dueOrdersD8,                        -- D8应还单量\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,D8Num,'-') as D8Num,                                    -- D8逾期单量\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,concat(round(D8Num/dueOrdersD8*100,2),'%'),'-') as D8   -- D8逾期率\n" +
            "from(\n" +
            "select \n" +
            "refundDay,\n" +
            "count(a.uuid) as totalorders,\n" +
            "sum(case when status in (10,11) then 1 else 0 end) as repayNum,\n" +
            "sum(case when datediff(curdate(),refundDay) < 1 then 0 else 1 end) as dueOrdersD1,\n" +
            "sum(case when step >= 1 then 1 else 0 end) as D1Num,\n" +
            "sum(case when datediff(curdate(),refundDay) < 8 then 0 else 1 end) as dueOrdersD8,\n" +
            "sum(case when step >= 8 then 1 else 0 end) as D8Num,\n" +
            "count(b.uuid) as repeatNum\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "date(refundTime) as refundDay, \n" +
            "useruuid,\n" +
            "uuid,\n" +
            "status,\n" +
            "borrowingcount,\n" +
            "case when status = 8 then datediff(curdate(),refundtime) else datediff(actualRefundTime,refundtime) end as step\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,2)\n" +
            "and date(refundTime) between date_sub(curdate(),interval 20 day) and date_sub(curdate(),interval 1 day)\n" +
            ") a\n" +
            "inner join \n" +
            "(\n" +
            "select \n" +
            "useruuid,\n" +
            "uuid,\n" +
            "borrowingCount\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,2)\n" +
            "and amountApply in (100000)\n" +
            ") b on b.useruuid = a.useruuid and b.borrowingCount = a.borrowingCount - 1\n" +
            "group by refundDay with rollup\n" +
            ")result;")
    List<PerformanceWith10wProduct2> getPerformanceWith10wProduct2();

    // 10万产品用户第三次借款贷后表现
    @Select("select \n" +
            "ifnull(refundDay,'合计') as refundDay,                                                                             -- 第3次到期日\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,dueOrdersD1,'-') as dueOrdersD1,                        -- D1应还单量\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,D1Num,'-') as D1Num,                                    -- D1逾期单量\n" +
            "if(datediff(curdate(),refundDay) >= 1 or refundDay is null,concat(round(D1Num/dueOrdersD1*100,2),'%'),'-') as D1,  -- D1逾期率\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,dueOrdersD8,'-') as dueOrdersD8,                        -- D8应还单量\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,D8Num,'-') as D8Num,                                    -- D8逾期单量\n" +
            "if(datediff(curdate(),refundDay) >= 8 or refundDay is null,concat(round(D8Num/dueOrdersD8*100,2),'%'),'-') as D8   -- D8逾期率\n" +
            "from(\n" +
            "select \n" +
            "refundDay,\n" +
            "count(a.uuid) as totalorders,\n" +
            "sum(case when status in (10,11) then 1 else 0 end) as repayNum,\n" +
            "sum(case when datediff(curdate(),refundDay) < 1 then 0 else 1 end) as dueOrdersD1,\n" +
            "sum(case when step >= 1 then 1 else 0 end) as D1Num,\n" +
            "sum(case when datediff(curdate(),refundDay) < 8 then 0 else 1 end) as dueOrdersD8,\n" +
            "sum(case when step >= 8 then 1 else 0 end) as D8Num,\n" +
            "count(b.uuid) as repeatNum\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "date(refundTime) as refundDay, \n" +
            "useruuid,\n" +
            "uuid,\n" +
            "status,\n" +
            "borrowingcount,\n" +
            "case when status = 8 then datediff(curdate(),refundtime) else datediff(actualRefundTime,refundtime) end as step\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,2)\n" +
            "and date(refundTime) between date_sub(curdate(),interval 20 day) and date_sub(curdate(),interval 1 day)\n" +
            ") a\n" +
            "inner join \n" +
            "(\n" +
            "select \n" +
            "useruuid,\n" +
            "uuid,\n" +
            "borrowingCount\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,2)\n" +
            "and amountApply in (100000)\n" +
            ") b on b.useruuid =  a.useruuid and b.borrowingCount = a.borrowingCount - 2\n" +
            "group by refundDay with rollup\n" +
            ")result;")
    List<PerformanceWith10wProduct2> getPerformanceWith10wProduct3();


    // 各渠道页面通过率
    @Select("-- 各渠道页面通过率\n" +
            "\n" +
            "select \n" +
            "createDay,        -- 日期\n" +
            "userSource,       -- 渠道\n" +
            "applyRate,        -- 注册申请率\n" +
            "roleRate,         -- 选择角色通过率\n" +
            "identityRate,     -- 填写身份信息通过率\n" +
            "informationRate,  -- 基本信息通过率\n" +
            "workRate,         -- 工作或学校信息通过率\n" +
            "contactsRate,     -- 联系人信息通过率\n" +
            "verificateRate,   -- 验证信息通过率\n" +
            "bankRate          -- 银行卡通过率\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "createDay,\n" +
            "userSource,\n" +
            "concat(round(sum(apply)/count(distinct uuid)*100,2),'%') as applyRate, \n" +
            "concat(round(sum(role)/sum(apply)*100,2),'%') as roleRate,\n" +
            "concat(round(sum(identity)/sum(role)*100,2),'%') as identityRate,\n" +
            "concat(round(sum(information)/sum(identity)*100,2),'%') as informationRate,\n" +
            "concat(round(sum(work)/sum(information)*100,2),'%') as workRate,\n" +
            "concat(round(sum(contacts)/sum(work)*100,2),'%') as contactsRate,\n" +
            "concat(round(sum(verificate)/sum(contacts)*100,2),'%') as verificateRate,\n" +
            "concat(round(sum(bank)/sum(verificate)*100,2),'%') as bankRate\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "case\n" +
            "when userSource = 1 then 'Android'\n" +
            "when userSource = 2 then 'IOS'\n" +
            "else concat('贷超商',userSource) end as userSource,\n" +
            "uuid\n" +
            "from usrUser\n" +
            "where disabled = 0\n" +
            "and date(createTime) between date_sub(curdate(),interval 7 day) and date_sub(curdate(),interval 1 day)\n" +
            "and userSource in (1,2)\n" +
            "group by uuid,createDay\n" +
            ") usr\n" +
            "left join\n" +
            "(\n" +
            "select\n" +
            "useruuid,\n" +
            "case when max(orderStep) >=0 then 1 else 0 end apply,\n" +
            "case when max(orderStep) >=1 then 1 else 0 end role,\n" +
            "case when max(orderStep) >=2 then 1 else 0 end identity,\n" +
            "case when max(orderStep) >=3 then 1 else 0 end information,\n" +
            "case when max(orderStep) >=4 then 1 else 0 end work,\n" +
            "case when max(orderStep) >=5 then 1 else 0 end contacts,\n" +
            "case when max(orderStep) >=6 then 1 else 0 end verificate,\n" +
            "case when max(orderStep) >=7 then 1 else 0 end bank,\n" +
            "case when max(orderStep) >=8 then 1 else 0 end extra\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and borrowingCount = 1\n" +
            "and ordertype in (0,2)\n" +
            "and date(createTime) between date_sub(curdate(),interval 7 day) and date_sub(curdate(),interval 1 day)\n" +
            "group by useruuid\n" +
            ") ord on ord.useruuid = usr.uuid\n" +
            "group by userSource,createDay with rollup\n" +
            "\n" +
            "union all \n" +
            "\n" +
            "select\n" +
            "createDay,\n" +
            "'cashcash' as userSource,\n" +
            "'100.00%' as applyRate, \n" +
            "concat(round(sum(role)/sum(apply)*100,2),'%') as roleRate,\n" +
            "concat(round(sum(identity)/sum(role)*100,2),'%') as identityRate,\n" +
            "concat(round(sum(information)/sum(identity)*100,2),'%') as informationRate,\n" +
            "concat(round(sum(work)/sum(information)*100,2),'%') as workRate,\n" +
            "concat(round(sum(contacts)/sum(work)*100,2),'%') as contactsRate, \n" +
            "concat(round(sum(verificate)/sum(contacts)*100,2),'%') as verificateRate,\n" +
            "concat(round(sum(bank)/sum(verificate)*100,2),'%') as bankRate \n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "uuid,\n" +
            "useruuid,\n" +
            "date(createTime) as createDay,\n" +
            "case when orderStep >= 0 then 1 else 0 end apply,\n" +
            "case when orderStep >= 1 then 1 else 0 end role,\n" +
            "case when orderStep >= 2 then 1 else 0 end identity,\n" +
            "case when orderStep >= 3 then 1 else 0 end information,\n" +
            "case when orderStep >= 4 then 1 else 0 end work,\n" +
            "case when orderStep >= 5 then 1 else 0 end contacts,\n" +
            "case when orderStep >= 6 then 1 else 0 end verificate,\n" +
            "case when orderStep >= 7 then 1 else 0 end bank,\n" +
            "case when orderStep >= 8 then 1 else 0 end extra\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and borrowingCount = 1\n" +
            "and date(createTime) between date_sub(curdate(),interval 7 day) and date_sub(curdate(),interval 1 day)\n" +
            ") ord\n" +
            "inner join\n" +
            "(\n" +
            "select userUuid\n" +
            "from externalOrderRelation\n" +
            "where channel = 'CASHCASH' \n" +
            "group by userUuid\n" +
            ") eor on ord.useruuid = eor.useruuid\n" +
            "group by createDay with rollup\n" +
            ") un\n" +
            "where userSource is not null;")
    List<PagePassRate> getPagePassRate();


    // 新户转化率
    @Select("select\n" +
            "createDay,                                                                        -- 进件日期\n" +
            "sum(applyOrders) as applyOrders,                                                  -- 进件数\n" +
            "concat(round(sum(macExamine)/sum(applyOrders)*100,2),'%') as submitRate,          -- 申请提交率\n" +
            "concat(round(sum(junExamine)/sum(macExamine)*100,2),'%') as macPassRate,          -- 机审通过率\n" +
            "concat(round(sum(senExamine)/sum(junExamine)*100,2),'%') as junPassRate,          -- 初审通过率\n" +
            "concat(round(sum(examinePass)/sum(senExamine)*100,2),'%') as senPassRate,         -- 复审通过率\n" +
            "concat(round(sum(signPass)/sum(examinePass)*100,2),'%') as signPassRate,          -- 签章通过率\n" +
            "concat(round(sum(LenOrders)/sum(signPass)*100,2),'%') as sucLendRate,             -- 成功放款率\n" +
            "concat(round(sum(examinePass)/sum(macExamine)*100,2),'%') as exaPassRate,         -- 审核通过率\n" +
            "concat(round(sum(LenOrders)/sum(macExamine)*100,2),'%') as subLendRate            -- 提交放款率\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "uuid as orderId,\n" +
            "sum(case when status >= 1 then 1 else 0 end) as applyOrders,\n" +
            "sum(case when status >= 2 then 1 else 0 end) as macExamine,\n" +
            "sum(case when status in (3,18,4,5,6,7,8,10,11,13,14,15,16,20) then 1 else 0 end) as junExamine,\n" +
            "sum(case when status in (4,5,6,7,8,10,11,14,16,20) then 1 else 0 end) as senExamine,\n" +
            "sum(case when status in (5,6,7,8,10,11,16,20) then 1 else 0 end) as examinePass,\n" +
            "sum(case when status in (5,6,7,8,10,11,16) then 1 else 0 end) as signPass,\n" +
            "sum(case when status in (7,8,10,11) then 1 else 0 end) as LenOrders\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and borrowingCount = 1\n" +
            "and date(createTime) between date_sub(curdate(),interval 10 day) and date_sub(curdate(),interval 1 day)\n" +
            "and orderType in (0,2,3)\n" +
            "group by createDay,uuid\n" +
            ") ord\n" +
            "group by createDay\n" +
            "\n" +
            "union all \n" +
            "select\n" +
            "if(Months = month(date_sub(curdate(),interval 1 day)),'本月','上月') as Months,\n" +
            "sum(applyOrders) as applyOrders,\n" +
            "concat(round(sum(macExamine)/sum(applyOrders)*100,2),'%') as submitRate,\n" +
            "concat(round(sum(junExamine)/sum(macExamine)*100,2),'%') as macPassRate,\n" +
            "concat(round(sum(senExamine)/sum(junExamine)*100,2),'%') as junPassRate,\n" +
            "concat(round(sum(examinePass)/sum(senExamine)*100,2),'%') as senPassRate,\n" +
            "concat(round(sum(signPass)/sum(examinePass)*100,2),'%') as signPassRate,\n" +
            "concat(round(sum(LenOrders)/sum(signPass)*100,2),'%') as sucLendRate,\n" +
            "concat(round(sum(examinePass)/sum(macExamine)*100,2),'%') as ExaPassRate,\n" +
            "concat(round(sum(LenOrders)/sum(macExamine)*100,2),'%') as subLendRate\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "month(createTime) as Months,\n" +
            "uuid as orderId,\n" +
            "sum(case when status >= 1 then 1 else 0 end) as applyOrders,\n" +
            "sum(case when status >= 2 then 1 else 0 end) as macExamine,\n" +
            "sum(case when status in (3,18,4,5,6,7,8,10,11,13,14,15,16,20) then 1 else 0 end) as junExamine,\n" +
            "sum(case when status in (4,5,6,7,8,10,11,14,16,20) then 1 else 0 end) as senExamine,\n" +
            "sum(case when status in (5,6,7,8,10,11,16,20) then 1 else 0 end) as examinePass,\n" +
            "sum(case when status in (5,6,7,8,10,11,16) then 1 else 0 end) as signPass,\n" +
            "sum(case when status in (7,8,10,11) then 1 else 0 end) as LenOrders\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and borrowingCount = 1\n" +
            "and date_format(createTime,'%Y-%m') in (date_format(date_sub(curdate(),interval 1 day),'%Y-%m')\n" +
            ",date_format(date_sub(date_sub(curdate(),interval 1 day),interval 1 month),'%Y-%m'))\n" +
            "and date(createTime) <= date_sub(curdate(),interval 1 day)\n" +
            "and orderType in (0,2,3)\n" +
            "group by Months,uuid\n" +
            ") ord\n" +
            "group by Months;")
    List<NewUserRate> getNewUserRate();

    // 安卓页面通过率
    @Select("select \n" +
            "channel,                                                                     -- 渠道\n" +
            "registerDay,                                                                 -- 注册日期\n" +
            "sum(register) as registerNum,                                                -- 注册数\n" +
            "concat(round(sum(apply)/sum(register)*100,2),'%') as applyRate,              -- 注册申请率\n" +
            "concat(round(sum(role)/sum(apply)*100,2),'%') as roleRate,                   -- 选择角色通过率\n" +
            "concat(round(sum(identity)/sum(role)*100,2),'%') as identityRate,            -- 填写身份信息通过率\n" +
            "concat(round(sum(information)/sum(identity)*100,2),'%') as informationRate,  -- 基本信息通过率\n" +
            "concat(round(sum(work)/sum(information)*100,2),'%') as workRate,             -- 工作或学校信息通过率\n" +
            "concat(round(sum(contacts)/sum(work)*100,2),'%') as contactsRate,            -- 联系人信息通过率\n" +
            "concat(round(sum(bank)/sum(contacts)*100,2),'%') as bankRate,                -- 银行卡通过率\n" +
            "concat(round(sum(bank)/sum(register)*100,2),'%') as submitRate               -- 注册提交率\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "if(userSource = 1,'Android',if(userSource = 2,'IOS',userSource)) as channel,\n" +
            "registerDay,\n" +
            "sum(register) as register,\n" +
            "sum(apply) as apply,\n" +
            "sum(role) as role,\n" +
            "sum(identity) as identity,\n" +
            "sum(information) as information,\n" +
            "sum(work) as work,\n" +
            "sum(contacts) as contacts,\n" +
            "sum(bank) as bank,\n" +
            "sum(extra) as extra\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as registerDay,\n" +
            "uuid,\n" +
            "1 as register,\n" +
            "userSource\n" +
            "from usrUser\n" +
            "where disabled = 0\n" +
            "and userSource in (1,2)\n" +
            "and date(createTime) between date_sub(curdate(),interval 7 day) and date_sub(curdate(),interval 1 day)\n" +
            ") usr\n" +
            "left join\n" +
            "(\n" +
            "select\n" +
            "useruuid as userId,\n" +
            "case when max(orderStep) >=0 then 1 else 0 end apply,\n" +
            "case when max(orderStep) >=1 then 1 else 0 end role,\n" +
            "case when max(orderStep) >=2 then 1 else 0 end identity,\n" +
            "case when max(orderStep) >=3 then 1 else 0 end information,\n" +
            "case when max(orderStep) >=4 then 1 else 0 end work,\n" +
            "case when max(orderStep) >=5 then 1 else 0 end contacts,\n" +
            "case when max(orderStep) >=6 then 1 else 0 end verificate,\n" +
            "case when max(orderStep) >=7 then 1 else 0 end bank,\n" +
            "case when max(orderStep) >=8 then 1 else 0 end extra\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and borrowingCount = 1\n" +
            "and date(createTime) between date_sub(curdate(),interval 7 day) and date_sub(curdate(),interval 1 day)\n" +
            "group by useruuid\n" +
            ") ord on ord.userId = usr.uuid\n" +
            "group by registerDay,userSource\n" +
            "\n" +
            "union all\n" +
            "select\n" +
            "if(userSource = 1,'Android',if(userSource = 2,'IOS',userSource)) as channel,\n" +
            "if(Months = month(date_sub(curdate(),interval 1 day)),'本月','上月') as Months,\n" +
            "sum(register) as register,\n" +
            "sum(apply) as apply,\n" +
            "sum(role) as role,\n" +
            "sum(identity) as identity,\n" +
            "sum(information) as information,\n" +
            "sum(work) as work,\n" +
            "sum(contacts) as contacts,\n" +
            "sum(bank) as bank,\n" +
            "sum(extra) as extra\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "month(createTime) as Months,\n" +
            "uuid,\n" +
            "1 as register,\n" +
            "userSource\n" +
            "from usrUser\n" +
            "where disabled = 0\n" +
            "and userSource in (1,2)\n" +
            "and date_format(createTime,'%Y-%m') in (date_format(date_sub(curdate(),interval 1 day),'%Y-%m')\n" +
            ",date_format(date_sub(date_sub(curdate(),interval 1 day),interval 1 month),'%Y-%m'))\n" +
            "and date(createTime) <= date_sub(curdate(),interval 1 day)\n" +
            ") usr\n" +
            "left join\n" +
            "(\n" +
            "select\n" +
            "useruuid as userId,\n" +
            "case when max(orderStep) >=0 then 1 else 0 end apply,\n" +
            "case when max(orderStep) >=1 then 1 else 0 end role,\n" +
            "case when max(orderStep) >=2 then 1 else 0 end identity,\n" +
            "case when max(orderStep) >=3 then 1 else 0 end information,\n" +
            "case when max(orderStep) >=4 then 1 else 0 end work,\n" +
            "case when max(orderStep) >=5 then 1 else 0 end contacts,\n" +
            "case when max(orderStep) >=6 then 1 else 0 end verificate,\n" +
            "case when max(orderStep) >=7 then 1 else 0 end bank,\n" +
            "case when max(orderStep) >=8 then 1 else 0 end extra\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and borrowingCount = 1\n" +
            "and date_format(createTime,'%Y-%m') in (date_format(date_sub(curdate(),interval 1 day),'%Y-%m')\n" +
            ",date_format(date_sub(date_sub(curdate(),interval 1 day),interval 1 month),'%Y-%m'))\n" +
            "and date(createTime) <= date_sub(curdate(),interval 1 day)\n" +
            "group by useruuid\n" +
            ") ord on ord.userId = usr.uuid\n" +
            "group by Months,userSource\n" +
            ") result\n" +
            "group by channel,registerDay\n" +
            "having channel is not null;\n")
    List<AndroidPageRate> getAndroidPageRate();


    // CashCash页面通过率
    @Select("select \n" +
            "if(registerDay is null,'',channel) as channel,                               -- 渠道\n" +
            "if(registerDay is null,'Avg',registerDay) as registerDay,                    -- 注册日期\n" +
            "sum(register) as registerNum,                                                -- 注册数\n" +
            "concat(round(sum(apply)/sum(register)*100,2),'%') as applyRate,              -- 注册申请率\n" +
            "concat(round(sum(role)/sum(apply)*100,2),'%') as roleRate,                   -- 选择角色通过率\n" +
            "concat(round(sum(identity)/sum(role)*100,2),'%') as identityRate,            -- 填写身份信息通过率\n" +
            "concat(round(sum(information)/sum(identity)*100,2),'%') as informationRate,  -- 基本信息通过率\n" +
            "concat(round(sum(work)/sum(information)*100,2),'%') as workRate,             -- 工作或学校信息通过率\n" +
            "concat(round(sum(contacts)/sum(work)*100,2),'%') as contactsRate,            -- 联系人信息通过率\n" +
            "concat(round(sum(bank)/sum(contacts)*100,2),'%') as bankRate,                -- 银行卡通过率\n" +
            "concat(round(sum(bank)/sum(register)*100,2),'%') as submitRate               -- 注册提交率\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "'cashcash' as channel,\n" +
            "createDay as registerDay,\n" +
            "sum(1) as register,\n" +
            "sum(apply) as apply,\n" +
            "sum(role) as role,\n" +
            "sum(identity) as identity,\n" +
            "sum(information) as information,\n" +
            "sum(work) as work,\n" +
            "sum(contacts) as contacts,\n" +
            "sum(bank) as bank,\n" +
            "sum(extra) as extra\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "min(date(createTime)) as createDay,\n" +
            "userUuid\n" +
            "from externalOrderRelation\n" +
            "where channel = 'CASHCASH' \n" +
            "and date(createTime) between date_sub(curdate(),interval 7 day) and date_sub(curdate(),interval 1 day)\n" +
            "group by userUuid\n" +
            ") eor\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "useruuid as userId,\n" +
            "case when max(orderStep) >=0 then 1 else 0 end apply,\n" +
            "case when max(orderStep) >=1 then 1 else 0 end role,\n" +
            "case when max(orderStep) >=2 then 1 else 0 end identity,\n" +
            "case when max(orderStep) >=3 then 1 else 0 end information,\n" +
            "case when max(orderStep) >=4 then 1 else 0 end work,\n" +
            "case when max(orderStep) >=5 then 1 else 0 end contacts,\n" +
            "case when max(orderStep) >=6 then 1 else 0 end verificate,\n" +
            "case when max(orderStep) >=7 then 1 else 0 end bank,\n" +
            "case when max(orderStep) >=8 then 1 else 0 end extra\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and borrowingCount = 1\n" +
            "and date(createTime) between date_sub(curdate(),interval 7 day) and date_sub(curdate(),interval 1 day)\n" +
            "group by useruuid\n" +
            ") ord on ord.userId = eor.useruuid\n" +
            "group by registerDay\n" +
            "\n" +
            "union all\n" +
            "select\n" +
            "'cashcash' as channel,\n" +
            "if(Months = month(date_sub(curdate(),interval 1 day)),'本月','上月') as Months,\n" +
            "sum(1) as register,\n" +
            "sum(apply) as apply,\n" +
            "sum(role) as role,\n" +
            "sum(identity) as identity,\n" +
            "sum(information) as information,\n" +
            "sum(work) as work,\n" +
            "sum(contacts) as contacts,\n" +
            "sum(bank) as bank,\n" +
            "sum(extra) as extra\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "min(month(createTime)) as Months,\n" +
            "userUuid\n" +
            "from externalOrderRelation\n" +
            "where channel = 'CASHCASH' \n" +
            "and date_format(createTime,'%Y-%m') in (date_format(date_sub(curdate(),interval 1 day),'%Y-%m')\n" +
            ",date_format(date_sub(date_sub(curdate(),interval 1 day),interval 1 month),'%Y-%m'))\n" +
            "and date(createTime) <= date_sub(curdate(),interval 1 day)\n" +
            "group by userUuid\n" +
            ") eor\n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "useruuid as userId,\n" +
            "case when max(orderStep) >=0 then 1 else 0 end apply,\n" +
            "case when max(orderStep) >=1 then 1 else 0 end role,\n" +
            "case when max(orderStep) >=2 then 1 else 0 end identity,\n" +
            "case when max(orderStep) >=3 then 1 else 0 end information,\n" +
            "case when max(orderStep) >=4 then 1 else 0 end work,\n" +
            "case when max(orderStep) >=5 then 1 else 0 end contacts,\n" +
            "case when max(orderStep) >=6 then 1 else 0 end verificate,\n" +
            "case when max(orderStep) >=7 then 1 else 0 end bank,\n" +
            "case when max(orderStep) >=8 then 1 else 0 end extra\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and borrowingCount = 1\n" +
            "and date_format(createTime,'%Y-%m') in (date_format(date_sub(curdate(),interval 1 day),'%Y-%m')\n" +
            ",date_format(date_sub(date_sub(curdate(),interval 1 day),interval 1 month),'%Y-%m'))\n" +
            "and date(createTime) <= date_sub(curdate(),interval 1 day)\n" +
            "group by useruuid\n" +
            ") ord on ord.userId = eor.useruuid\n" +
            "group by Months\n" +
            ") result\n" +
            "group by channel,registerDay\n" +
            "having channel is not null;")
    List<AndroidPageRate> getCash2PageRate();

    // cashcash获客成本
    @Select("select \n" +
            "if(createDay is null,'Total',createDay) as createDay,              -- 进件日期\n" +
            "newOrders,                                                         -- 新户进件数\n" +
            "newSubmit,                                                         -- 新户提交数\n" +
            "sucNew,                                                            -- 新户放款数\n" +
            "round(newOrders*1/sucNew,2) as newCost,                            -- 新户成本\n" +
            "concat(round(newSubmit/newOrders*100,2),'%') as newSubRate,        -- 新户进件提交率\n" +
            "concat(round(sucNew/newSubmit*100,2),'%') as newLendRate,          -- 新户提交放款率\n" +
            "oldOrders,                                                         -- 老户进件数\n" +
            "sucOld,                                                            -- 老户放款数\n" +
            "totalOrders                                                        -- 总进件数\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "date(createTime) as createDay,\n" +
            "count(1) as totalOrders,\n" +
            "sum(case when borrowingCount = 1 then 1 else 0 end) as newOrders,\n" +
            "sum(case when borrowingCount = 1 and status >= 2 then 1 else 0 end) as newSubmit,\n" +
            "sum(case when borrowingCount = 1 and status in (7,8,10,11) then 1 else 0 end) as sucNew,\n" +
            "sum(case when borrowingCount > 1 then 1 else 0 end) as oldOrders,\n" +
            "sum(case when borrowingCount > 1 and status in (7,8,10,11) then 1 else 0 end) as sucOld\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "createTime,\n" +
            "orderNo\n" +
            "from externalOrderRelation\n" +
            "where channel = 'CASHCASH' \n" +
            "and date(createtime) between date_sub(curdate(),interval 20 day) and date_sub(curdate(),interval 1 day)\n" +
            ") eor\n" +
            "inner join \n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "borrowingCount,\n" +
            "status\n" +
            "from ordOrder\n" +
            ") ord on ord.uuid = eor.orderNo\n" +
            "group by createDay with rollup\n" +
            ") result;")
    List<Cash2Cost> getCash2Cost();


    // cashcash转自有
    @Select("select  \n" +
            "ifnull(repayDay,'Total') as repayDay,                                     -- 还款日期\n" +
            "count(orderId) as repayOrder,                                             -- 还款数\n" +
            "count(uuid) as againBorrow,                                               -- 复借数\n" +
            "concat(round(count(uuid)/count(orderId)*100,2),'%') as againBorRate,      -- 复借率\n" +
            "count(cashNo) as cashBorrow,                                              -- CashCash数\n" +
            "count(uuid) - count(cashNo) as ownBorrow,                                 -- 自有数\n" +
            "concat(round(100-count(cashNo)/count(uuid)*100,2),'%') as tranOwnRate     -- 转自有率\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "date(actualRefundTime) as repayDay,\n" +
            "uuid as orderId,\n" +
            "userUuid as userId,\n" +
            "borrowingCount as countA\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (10,11)\n" +
            "and ordertype in (0,1)\n" +
            "and date(actualRefundTime) between date_sub(curdate(),interval 10 day) and date_sub(curdate(),interval 1 day)\n" +
            "and datediff(actualRefundTime,refundTime) <= 8\n" +
            ") ordA\n" +
            "inner join \n" +
            "(\n" +
            "select orderNo\n" +
            "from externalOrderRelation\n" +
            "where channel = 'CASHCASH' \n" +
            "group by orderNo\n" +
            ") extA on extA.orderNo = ordA.orderId\n" +
            "left join \n" +
            "(\n" +
            "select \n" +
            "uuid,\n" +
            "userUuid,\n" +
            "borrowingCount as countB\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (7,8,10,11)\n" +
            "and ordertype in (0,2,3)\n" +
            ") ordB on ordB.userUuid = ordA.userId and countB = countA + 1\n" +
            "left join \n" +
            "(\n" +
            "select orderNo as cashNo\n" +
            "from externalOrderRelation\n" +
            "where channel = 'CASHCASH' \n" +
            "group by orderNo\n" +
            ") extB on extB.cashNo = ordB.uuid\n" +
            "group by repayDay with rollup;")
    List<Cash2TransferRate> getCash2TransferRate();


    // 借款次数分布
    @Select("select \n" +
            "now() as createtime,                     -- 创建时间\n" +
            "ifnull(sucLendTimes,'Total') as times,   -- 借款次数\n" +
            "users,                                   -- 用户数\n" +
            "boringUser,                              -- 在借数\n" +
            "overD8,                                  -- 逾期8天以上\n" +
            "silence                                  -- 沉默数\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "case\n" +
            "when orders between 11 and 20 then '11-20次'\n" +
            "when orders between 21 and 50 then '21-50次'\n" +
            "when orders >= 51 then '50次以上'\n" +
            "else concat(orders,'次') end as sucLendTimes,\n" +
            "sum(1) as users,     \n" +
            "count(userB) as boringUser,\n" +
            "sum(case when type = 'overDue' then 1 else 0 end) as 'overD8',\n" +
            "sum(case when userB is null and userC is not null then 1 else 0 end) as 'silence'\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "useruuid as userId,\n" +
            "count(1) as orders\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,2,3)\n" +
            "group by useruuid\n" +
            ") ordA\n" +
            "left join \n" +
            "(\n" +
            "select\n" +
            "useruuid as userB,\n" +
            "if(datediff(curdate(),refundtime) <= 8,'Normal','overDue') as type\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (7,8)\n" +
            "and orderType in (0,1,3)\n" +
            "group by useruuid,type\n" +
            ") ordB on ordB.userB = ordA.userId\n" +
            "left join \n" +
            "(\n" +
            "select \n" +
            "max(datediff(actualRefundTime,refundTime)) as repayDay,\n" +
            "useruuid as userC\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and status in (10,11)\n" +
            "and orderType in (0,1,2,3)\n" +
            "group by useruuid\n" +
            "having repayDay < 10\n" +
            ") ordC on ordC.userC = ordA.userId\n" +
            "group by sucLendTimes with rollup\n" +
            ") result\n" +
            "order by case \n" +
            "when Times = '10次' then 1 \n" +
            "when Times = '11-20次' then 2 \n" +
            "when Times = '21-50次' then 3\n" +
            "when Times = '50次以上' then 4\n" +
            "when Times = 'Total' then 5\n" +
            "else 0 end,Times;")
    List<DayLoanCount> getDayLoanCount();

    // 当日复借率
    @Select("select \n" +
            "ifnull(repayDay,'Avg') as repayDay,                                        -- 日期\n" +
            "count(repay.orderId) as toRepay,                                           -- 当日还\n" +
            "count(lend.orderId) as toLend,                                             -- 当日借\n" +
            "concat(round(count(lend.orderId)/count(repay.orderId)*100,2),'%') as rate  -- 占比\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "date(actualRefundTime) as repayDay,\n" +
            "uuid as orderId,\n" +
            "useruuid as userId,\n" +
            "borrowingCount as repayCount\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (10,11)\n" +
            "and date(actualRefundTime) between date_sub(curdate(),interval 10 day) and date_sub(curdate(),interval 1 day)\n" +
            "and orderType in (0,1)\n" +
            ") repay\n" +
            "left join \n" +
            "(\n" +
            "select \n" +
            "date(lendingTime) as lendDay,\n" +
            "uuid as orderId,\n" +
            "useruuid as userId,\n" +
            "borrowingCount as lendCount\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,3)\n" +
            ") lend on lendDay = repayDay and repayCount + 1 = lendCount and lend.userId = repay.userId\n" +
            "group by repayDay with rollup;")
    List<DayReborrowRate> getDayReborrowRate();

    // 到期订单
    @Select("select \n" +
            "dueDay,\n" +
            "sum(case when orderType in (0,1,2,3) then 1 else 0 end) as orders,                                  -- 到期订单\n" +
            "sum(case when orderType in (0,2) and borrowingcount = 1 then 1 else 0 end) as newOrder,             -- 新户订单\n" +
            "sum(case when orderType in (0,2) and borrowingcount > 1 then 1 else 0 end) as oldOrder,             -- 老户常规订单\n" +
            "sum(case when orderType = 3 then 1 else 0 end) as billOrders,                                       -- 分期订单\n" +
            "sum(case when orderType = 1 then 1 else 0 end) as Extension,                                        -- 展期订单 \n" +
            "sum(case when orderType in (0,1,2,3) then dueAmount else 0 end) as dueAmount,                       -- 总到期金额 \n" +
            "sum(case when orderType in (0,2) and borrowingcount = 1 then dueAmount else 0 end) as newAmount,    -- 新户到期金额\n" +
            "sum(case when orderType in (0,2) and borrowingcount > 1 then dueAmount else 0 end) as oldAmount,    -- 老户到期金额\n" +
            "sum(case when orderType = 3 then dueAmount else 0 end) as billAmount,                               -- 分期到期金额\n" +
            "sum(case when orderType = 1 then dueAmount else 0 end) as ExtAmount                                 -- 展期到期金额\n" +
            "from \n" +
            "(\n" +
            "select \n" +
            "case when orderType = 3 then billId when orderType in (0,1,2) then orderId else null end as orderId,\n" +
            "case when orderType = 3 then billDueDay when orderType in (0,1,2) then ordDueDay else null end as dueDay,\n" +
            "case when orderType = 3 then billDueAmount when orderType in (0,1,2) then ordApplyAmount else null end as dueAmount,\n" +
            "orderType,\n" +
            "borrowingcount\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "uuid as orderId,\n" +
            "date(refundtime) as ordDueDay,\n" +
            "date(lendingTime) as ordLendDay,\n" +
            "amountApply as ordApplyAmount,\n" +
            "status as ordStatus,\n" +
            "orderType,\n" +
            "borrowingcount\n" +
            "from ordOrder\n" +
            "where disabled = 0 \n" +
            "and status in (7,8,10,11)\n" +
            "and orderType in (0,1,2,3)\n" +
            "and date(refundTime) between date_sub(curdate(),interval 5 day) and date_sub(curdate(),interval -15 day)\n" +
            ") ord  \n" +
            "left join\n" +
            "(\n" +
            "select \n" +
            "orderNo,\n" +
            "uuid as billId,\n" +
            "billTerm,\n" +
            "date(refundTime) as billDueDay,\n" +
            "billAmout as billDueAmount,\n" +
            "status as billStatus\n" +
            "from ordBill\n" +
            "where disabled = 0\n" +
            "and status in (1,2,3,4)\n" +
            "and date(refundTime) between date_sub(curdate(),interval 5 day) and date_sub(curdate(),interval -15 day)\n" +
            ") bill on bill.orderNo = ord.orderId\n" +
            ") result\n" +
            "group by dueDay;")
    List<ExpireDateOrder> getExpireDateOrder();

    // Twilio外呼接通率
    @Select("select \n" +
            "if(callphase = 'Total','',createDay) as date,      -- 外呼日期\n" +
            "callPhase,                                         -- 外呼阶段\n" +
            "totalCall,                                         -- 外呼次数\n" +
            "connectCall,                                       -- 接通次数\n" +
            "connectRate,                                       -- 接通率\n" +
            "noAnswerRate,                                      -- 无人接听率\n" +
            "failedRate ,                                       -- 失败率\n" +
            "busyRate,                                          -- 繁忙率\n" +
            "callCost                                           -- 外呼花费\n" +
            "from \n" +
            "(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "ifnull(callPhase,'Total') as callPhase, \n" +
            "sum(case when callResult = 'completed' and duration > 0 then - price else 0 end) as callCost,\n" +
            "sum(1) as totalCall,\n" +
            "sum(case when callResult = 'completed' then 1 else 0 end) as connectCall,\n" +
            "concat(round(sum(case when callResult = 'completed' then 1 else 0 end)/sum(1)*100,2),'%') as connectRate,\n" +
            "concat(round(sum(case when callResult = 'no-answer' then 1 else 0 end)/sum(1)*100,2),'%') as noAnswerRate,\n" +
            "concat(round(sum(case when callResult = 'failed' then 1 else 0 end)/sum(1)*100,2),'%') as failedRate,\n" +
            "concat(round(sum(case when callResult = 'busy' then 1 else 0 end)/sum(1)*100,2),'%') as busyRate\n" +
            "from twilioCallResult\n" +
            "where disabled = 0\n" +
            "and callPhase in ('D0','D-1','D-2','D-3','D-5','D-10','D-15')\n" +
            "and date(createTime) between date_sub(curdate(),interval 5 day) and date_sub(curdate(),interval 1 day)\n" +
            "group by createDay,callPhase with rollup\n" +
            "having createDay is not null\n" +
            ") result\n" +
            "order by createDay,\n" +
            "case callPhase \n" +
            "when 'D-15' then 1 \n" +
            "when 'D-10' then 2 \n" +
            "when 'D-5' then 3\n" +
            "when 'D-3' then 4\n" +
            "when 'D-2' then 5\n" +
            "when 'D-1' then 6\n" +
            "when 'D0' then 7\n" +
            "when 'Total' then 8 end;")
    List<TwilioRate> getTwilioRate();
}
