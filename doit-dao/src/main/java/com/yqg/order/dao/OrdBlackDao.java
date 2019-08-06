package com.yqg.order.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.order.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by Didit Dwianto on 2017/12/8.
 */
@Mapper
public interface OrdBlackDao extends BaseMapper<OrdBlack> {

    //  表1、每日规则拒绝人数统计（新户自动审核）
    @Select("select z.拒绝日期 as refuseDate,\n" +
            "   z.拒绝原因 as refuseReason,\n" +
            "   z.拒绝订单数 as refuseNum,\n" +
            "   y.提交数 as commitNum,\n" +
            "   concat(format(z.拒绝订单数/y.提交数*100,1),'%') as  commitRefuseRate #在提交订单中的命中率\n" +
            "from\n" +
            "(select x.拒绝日期 , x.拒绝原因 , sum(x.拒绝订单数) as 拒绝订单数\n" +
            "from\n" +
            "(select  date(b.createTime) as 拒绝日期,responseMessage as 拒绝原因, count(distinct orderNo) as 拒绝订单数\n" +
            "from doit.ordBlack b\n" +
            "join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            "join doit.ordHistory h on b.userUuid = h.userUuid and b.orderNo = h.orderId\n" +
            "where  \n" +
            "d.disabled = 0 and d.borrowingCount = 1 and b.disabled = 0 and h.status = 12 and d.amountApply = 1200000\n" +
            "and b.orderNo not in (select distinct orderNo from doit.ordBlack\n" +
            "where remark in ('PRODUCT600TO50_FRAUD','PRODUCT600TO150'))\n" +
            "and  b.createTime > '2019-01-07 18:30:00' \n" +
            "and date(applyTime)= date(now()) # 600没有流入其他产品\n" +
            "group by 1,2\n" +
            "union all\n" +
            "select  date(b.createTime) as 拒绝日期,responseMessage as 拒绝原因, count(distinct orderNo) as 拒绝订单数\n" +
            "from doit.ordBlack b \n" +
            "join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            "where  \n" +
            "d.disabled = 0 and d.borrowingCount = 1  and \n" +
            "b.remark in ('PRODUCT600TO50_FRAUD','PRODUCT600TO150') and  b.createTime > '2019-01-07 18:30:00'\n" +
            "and date(applyTime)= date(now()) -- 600流入其它产品\n" +
            "group by 1,2\n" +
            ") x\n" +
            "group by 1,2\n" +
            "order by 1 desc, 3 desc) z\n" +
            "join\n" +
            "(select date(applyTime) as 提交日期,sum(if(status>1,1,0)) as 提交数\n" +
            "from doit.ordOrder\n" +
            "where disabled = 0 and borrowingCount = 1 and applyTime > '2019-01-07 18:30:00' \n" +
            "and date(applyTime)= date(now())\n" +
            "group by 1 desc) y on z.拒绝日期 = y.提交日期\n" +
            "order by 1 desc, 3 desc;")
    List<RegisterNum2> getRefusedDailyNumNew();


    //  表2、每日规则拒绝人数统计（新户初审））
    @Select("SELECT \n" +
            "    w.refusedDate,  --  拒绝日期\n" +
            "    w.refusedReason,   --  拒绝原因\n" +
            "    ww.refusedAllPeople,  --  拒绝总人数\n" +
            "    w.refusedPeople,  -- 拒绝人数\n" +
            "    CONCAT(FORMAT(w.refusedPeople / ww.refusedAllPeople * 100,1),'%') AS rejectrate  --  拒绝占比\n" +
            "FROM\n" +
            "    (SELECT DATE(b.createTime) AS refusedDate,\n" +
            "    responseMessage refusedReason,\n" +
            "    COUNT(DISTINCT b.userUuid) refusedPeople\n" +
            "    FROM ordBlack b\n" +
            "\tinner join ordOrder d\n" +
            "    on b.orderNo=d.uuid and b.userUuid=d.userUuid and d.disabled=0\n" +
            "    WHERE  b.disabled = 0\n" +
            "    AND DATEDIFF(CURDATE(), b.createTime)<3\n" +
            "\tand borrowingCount=1 and d.`status` in (13,15)\n" +
            "    and date(b.createTime)>'2018-06-07'\n" +
            "    GROUP BY 1 , 2) w\n" +
            "JOIN\n" +
            "    (SELECT DATE(b.createTime) AS refusedDate,\n" +
            "    COUNT(DISTINCT b.userUuid) refusedAllPeople\n" +
            "    FROM ordBlack b\n" +
            "\tinner join ordOrder d\n" +
            "    on b.orderNo=d.uuid and b.userUuid=d.userUuid and d.disabled=0\n" +
            "    WHERE b.disabled = 0\n" +
            "    AND DATEDIFF(CURDATE(), b.createTime)<3\n" +
            "\tand borrowingCount=1 and d.`status` in (13,15)\n" +
            "    and date(b.createTime)>'2018-06-07'\n" +
            "    GROUP BY 1) ww ON w.refusedDate = ww.refusedDate\n" +
            "ORDER BY 1 DESC,4 DESC;")
    List<RegisterNum> getRefusedDailyNumNew2();


    //  表3、每日规则拒绝人数统计（新户复审）
    @Select("SELECT \n" +
            "    w.refusedDate,  --  拒绝日期\n" +
            "    w.refusedReason,   --  拒绝原因\n" +
            "    ww.refusedAllPeople,  --  拒绝总人数\n" +
            "    w.refusedPeople,  -- 拒绝人数\n" +
            "    CONCAT(FORMAT(w.refusedPeople / ww.refusedAllPeople * 100,1),'%') AS rejectrate  --  拒绝占比 \n" +
            "FROM\n" +
            "    (SELECT DATE(b.createTime) AS refusedDate,\n" +
            "    responseMessage refusedReason,\n" +
            "    COUNT(DISTINCT b.userUuid) refusedPeople\n" +
            "    FROM ordBlack b\n" +
            "\tinner join ordOrder d\n" +
            "    on b.orderNo=d.uuid and b.userUuid=d.userUuid and d.disabled=0\n" +
            "    WHERE  b.disabled = 0\n" +
            "    AND DATEDIFF(CURDATE(), b.createTime)<3\n" +
            "\tand borrowingCount=1 and d.`status` in (14)\n" +
            "    and date(b.createTime)>'2018-06-07'\n" +
            "    GROUP BY 1 , 2) w\n" +
            "JOIN\n" +
            "    (SELECT DATE(b.createTime) AS refusedDate,\n" +
            "    COUNT(DISTINCT b.userUuid) refusedAllPeople\n" +
            "    FROM ordBlack b\n" +
            "\tinner join ordOrder d\n" +
            "    on b.orderNo=d.uuid and b.userUuid=d.userUuid and d.disabled=0\n" +
            "    WHERE b.disabled = 0\n" +
            "    AND DATEDIFF(CURDATE(), b.createTime)<3\n" +
            "\tand borrowingCount=1 and d.`status` in (14)\n" +
            "    and date(b.createTime)>'2018-06-07'\n" +
            "    GROUP BY 1) ww ON w.refusedDate = ww.refusedDate\n" +
            "ORDER BY 1 DESC,4 DESC;")
    List<RegisterNum> getRefusedDailyNumNew3();



    // 表4、每日规则拒绝人数统计（老户）
    @Select("select\n" +
            "w.p1,\n" +
            "w.p2,\n" +
            "ww.p3, \n" +
            "w.p4,\n" +
            "CONCAT(FORMAT((w.p4/ww.p3* 100),1),'%') as p5\n" +
            "from\n" +
            "(select\n" +
            "date(b.createTime) as p1,\n" +
            "responseMessage p2,\n" +
            "COUNT(DISTINCT b.userUuid) p4\n" +
            "from ordBlack b\n" +
            "inner join ordOrder d\n" +
            "on b.orderNo=d.uuid and b.userUuid=d.userUuid and d.disabled=0\n" +
            "where b.disabled=0\n" +
            "and datediff(CURDATE(),b.createTime)<=3\n" +
            "and borrowingCount>1#老户\n" +
            "and date(b.createTime)>'2018-06-07'\n" +
            "group by 1,2) w\n" +
            "join\n" +
            "(select\n" +
            "date(b.createTime) as p1,\n" +
            "COUNT(DISTINCT b.userUuid) p3\n" +
            "from ordBlack b\n" +
            "inner join ordOrder d\n" +
            "on b.orderNo=d.uuid and b.userUuid=d.userUuid and d.disabled=0\n" +
            "where b.disabled=0\n" +
            "and datediff(CURDATE(),b.createTime)<=3\n" +
            "and borrowingCount>1#老户\n" +
            "and date(b.createTime)>'2018-06-07'\n" +
            "group by 1) ww on w.p1=ww.p1\n" +
            "order by 1 desc,4 desc;\n" +
            "\n")
    List<RefusedDailyNum> getRefusedDailyNumOld();

//    // 表5、各环节当天累计订单数（新户+老户）
//    @Select("SELECT \n" +
//            "a.date ,  -- 日期 \n" +
//            "applyingNum,  -- 申请订单\n" +
//            "commitNum,  -- 提交订单\n" +
//            "(autoCheckPassNum+nucpfnum+reborrnum) as autoCheckPassNum,  -- 机审通过\n" +
//            "(firstCheckPassNum+nucpfnum+reborrnum) as firstCheckPassNum,  --  初审通过\n" +
//            "secondCheckPassNum,  -- 复审通过\n" +
//            "lendNum ,  -- 放款\n" +
//            "lendSuccessNum,  -- 放款成功\n" +
//            "autoCheckNoPassNum,  --  机审不通过\n" +
//            "firstCheckNoPassNum ,  -- 初审不通过\n" +
//            "secondCheckNoPassNum   -- 复审不通过\n" +
//            "from \n" +
//            "(SELECT \n" +
//            "DATE(createTime) date,\n" +
//            "SUM(IF(h.status IN (1), 1, 0)) applyingNum,\n" +
//            "SUM(IF(h.status IN (2), 1, 0)) commitNum,\n" +
//            "SUM(IF(h.status IN (3), 1, 0)) autoCheckPassNum,\n" +
//            "SUM(IF(h.status IN (4), 1, 0)) firstCheckPassNum,\n" +
//            "SUM(IF(h.status IN (5), 1, 0)) secondCheckPassNum,\n" +
//            "SUM(IF(h.status IN (6), 1, 0)) lendNum,\n" +
//            "SUM(IF(h.status IN (7), 1, 0)) lendSuccessNum,\n" +
//            "SUM(IF(h.status IN (12), 1, 0)) autoCheckNoPassNum,\n" +
//            "SUM(IF(h.status IN (13), 1, 0)) firstCheckNoPassNum,\n" +
//            "SUM(IF(h.status IN (14), 1, 0)) secondCheckNoPassNum\n" +
//            "FROM ordHistory h\n" +
//            "WHERE h.disabled = 0\n" +
//            "AND createTime > '2018-01-04 14:30:00'\n" +
//            "group by 1)a \n" +
//            "left join \n" +
//            "(SELECT date(h.createTime) as date,count(distinct h.orderId) as nucpfnum\n" +
//            "FROM ordHistory h\n" +
//            "inner join ordRiskRecord \tr on h.orderId=r.orderNo\t\n" +
//            "join (select distinct ruleDetailType,ruleDesc,remark from sysAutoReviewRule u where u.disabled=0 and ruleType in (22))u on u.ruleDetailType=r.ruleDetailType\t\t\n" +
//            "where r.disabled = 0 and h.disabled = 0 and r.ruleRealValue ='true'\n" +
//            "and h.status in (5) \n" +
//            "group by 1)b on a.date=b.date\n" +
//            "left join \n" +
//            "(SELECT date(h.createTime) as date,count(distinct h.orderId) as reborrnum\n" +
//            "FROM ordHistory h\n" +
//            "inner join ordOrder d on h.orderId=d.uuid\t\n" +
//            "where  d.disabled = 0 and h.disabled = 0 and borrowingCount>1\n" +
//            "and h.status in (5)\n" +
//            "group by 1)c on a.date=c.date\n" +
//            "order by 1 desc\n" +
//            "limit 10;")

    @Select("SELECT \n" +
            "a.date ,  -- 日期 \n" +
            "applyingNum,  -- 申请订单\n" +
            "commitNum,  -- 提交订单\n" +
            "(autoCheckPassNum+nucpfnum) as autoCheckPassNum,  -- 机审通过\n" +
            "(firstCheckPassNum+nucpfnum) as firstCheckPassNum,  --  初审通过\n" +
            "secondCheckPassNum,  -- 复审通过\n" +
            "lendNum ,  -- 放款\n" +
            "lendSuccessNum,  -- 放款成功\n" +
            "autoCheckNoPassNum,  --  机审不通过\n" +
            "firstCheckNoPassNum ,  -- 初审不通过\n" +
            "secondCheckNoPassNum   -- 复审不通过\n" +
            "from \n" +
            "(SELECT \n" +
            "DATE(createTime) date,\n" +
            "SUM(IF(h.status IN (1), 1, 0)) applyingNum,\n" +
            "SUM(IF(h.status IN (2), 1, 0)) commitNum,\n" +
            "SUM(IF(h.status IN (3), 1, 0)) autoCheckPassNum,\n" +
            "SUM(IF(h.status IN (4), 1, 0)) firstCheckPassNum,\n" +
            "SUM(IF(h.status IN (5), 1, 0)) secondCheckPassNum,\n" +
            "SUM(IF(h.status IN (6), 1, 0)) lendNum,\n" +
            "SUM(IF(h.status IN (7), 1, 0)) lendSuccessNum,\n" +
            "SUM(IF(h.status IN (12), 1, 0)) autoCheckNoPassNum,\n" +
            "SUM(IF(h.status IN (13), 1, 0)) firstCheckNoPassNum,\n" +
            "SUM(IF(h.status IN (14), 1, 0)) secondCheckNoPassNum\n" +
            "FROM ordHistory h\n" +
            "WHERE h.disabled = 0\n" +
            "AND h.createTime > '2018-01-04 14:30:00'\n" +
            "group by 1)a \n" +
            "left join \n" +
            "(SELECT date(createTime) as date,count(distinct orderId) as nucpfnum\n" +
            "FROM ordHistory \n" +
            "where orderId not in \n" +
            "(select DISTINCT orderId from ordHistory where status=3)\t\n" +
            "and status=5 and disabled=0 \n" +
            "group by 1)b on a.date=b.date\n" +
            "order by 1 desc\n" +
            "limit 10;")
    List<PassLinksNum> getPassLinksNum();

    // 各环节当天累计订单数（新户）
//    @Select("SELECT \n" +
//            "a.date ,  -- 日期 \n" +
//            "applyingNum,  --  申请订单\n" +
//            "commitNum,  -- 提交订单\n" +
//            "(autoCheckPassNum+nucpfnum) as autoCheckPassNum,  --  机审通过\n" +
//            "(firstCheckPassNum+nucpfnum) as firstCheckPassNum,  -- 初审通过\n" +
//            "secondCheckPassNum ,  -- 复审通过\n" +
//            "lendNum ,  -- 放款\n" +
//            "lendSuccessNum,  -- 放款成功\n" +
//            "autoCheckNoPassNum,  -- 机审不通过\n" +
//            "firstCheckNoPassNum ,  -- 初审不通过\n" +
//            "secondCheckNoPassNum   -- 复审不通过\n" +
//            "from \n" +
//            "(SELECT \n" +
//            "DATE(h.createTime) date,\n" +
//            "SUM(IF(h.status IN (1), 1, 0)) applyingNum,\n" +
//            "SUM(IF(h.status IN (2), 1, 0)) commitNum,\n" +
//            "SUM(IF(h.status IN (3), 1, 0)) autoCheckPassNum,\n" +
//            "SUM(IF(h.status IN (4), 1, 0)) firstCheckPassNum,\n" +
//            "SUM(IF(h.status IN (5), 1, 0)) secondCheckPassNum,\n" +
//            "SUM(IF(h.status IN (6), 1, 0)) lendNum,\n" +
//            "SUM(IF(h.status IN (7), 1, 0)) lendSuccessNum,\n" +
//            "SUM(IF(h.status IN (12), 1, 0)) autoCheckNoPassNum,\n" +
//            "SUM(IF(h.status IN (13), 1, 0)) firstCheckNoPassNum,\n" +
//            "SUM(IF(h.status IN (14), 1, 0)) secondCheckNoPassNum\n" +
//            "FROM ordHistory h\n" +
//            "join ordOrder d on h.orderId=d.uuid\n" +
//            "WHERE h.disabled = 0 and d.disabled=0 and borrowingCount = 1\n" +
//            "AND h.createTime > '2018-01-04 14:30:00'\n" +
//            "group by 1)a \n" +
//            "left join \n" +
//            "(SELECT date(h.createTime) as date,count(distinct h.orderId) as nucpfnum\n" +
//            "FROM ordHistory h\n" +
//            "join ordOrder d on h.orderId=d.uuid\n" +
//            "inner join ordRiskRecord \tr on h.orderId=r.orderNo\t\n" +
//            "join (select distinct ruleDetailType,ruleDesc,remark from sysAutoReviewRule u where u.disabled=0 and ruleType in (22))u on u.ruleDetailType=r.ruleDetailType\t\t\n" +
//            "where r.disabled = 0 and h.disabled = 0 and r.ruleRealValue ='true' and d.disabled=0 and borrowingCount = 1\n" +
//            "and h.status in (5) \n" +
//            "group by 1)b on a.date=b.date\n" +
//            "order by 1 desc\n" +
//            "limit 10;")

    @Select("SELECT \n" +
            "a.date ,  -- 日期 \n" +
            "applyingNum,  --  申请订单\n" +
            "commitNum,  -- 提交订单\n" +
            "(autoCheckPassNum+nucpfnum) as autoCheckPassNum,  --  机审通过\n" +
            "(firstCheckPassNum+nucpfnum) as firstCheckPassNum,  -- 初审通过\n" +
            "secondCheckPassNum ,  -- 复审通过\n" +
            "lendNum ,  -- 放款\n" +
            "lendSuccessNum,  -- 放款成功\n" +
            "autoCheckNoPassNum,  -- 机审不通过\n" +
            "firstCheckNoPassNum ,  -- 初审不通过\n" +
            "secondCheckNoPassNum   -- 复审不通过\n" +
            "from \n" +
            "(SELECT \n"+
            "DATE(h.createTime) date,\n" +
            "SUM(IF(h.status IN (1), 1, 0)) applyingNum,\n" +
            "SUM(IF(h.status IN (2), 1, 0)) commitNum,\n" +
            "SUM(IF(h.status IN (3), 1, 0)) autoCheckPassNum,\n" +
            "SUM(IF(h.status IN (4), 1, 0)) firstCheckPassNum,\n" +
            "SUM(IF(h.status IN (5), 1, 0)) secondCheckPassNum,\n" +
            "SUM(IF(h.status IN (6), 1, 0)) lendNum,\n" +
            "SUM(IF(h.status IN (7), 1, 0)) lendSuccessNum,\n" +
            "SUM(IF(h.status IN (12), 1, 0)) autoCheckNoPassNum,\n" +
            "SUM(IF(h.status IN (13), 1, 0)) firstCheckNoPassNum,\n" +
            "SUM(IF(h.status IN (14), 1, 0)) secondCheckNoPassNum\n" +
            "FROM ordHistory h\n" +
            "join ordOrder d on h.orderId=d.uuid\n" +
            "WHERE h.disabled = 0 and d.disabled=0 and borrowingCount = 1\n" +
            "AND h.createTime > '2018-01-04 14:30:00'\n" +
            "group by 1)a \n" +
            "left join \n" +
            "(SELECT date(h.createTime) as date,count(distinct orderId) as nucpfnum\n" +
            "FROM ordHistory h\n" +
            "join \n" +
            "ordOrder d on h.orderId=d.uuid\n" +
            "where orderId not in \n" +
            "(select DISTINCT orderId from ordHistory where status=3)\t\n" +
            "and h.status=5 and h.disabled=0 and borrowingCount=1\n" +
            "group by 1)b on a.date=b.date\n" +
            "order by 1 desc\n" +
            "limit 10;")
    List<PassLinksNum> getPassLinksNumWithNewUser();

    // 表6、各环节当天申请订单的当前通过率（新户+老户）
    @Select("SELECT\n" +
            "z.p1,\n" +
            "z.p2,\n" +
            "z.p3,\n" +
            "z.p4,\n" +
            "z.p5,\n" +
            "CONCAT(FORMAT(z.p3/z.p2 *100,1),'%') as p6,\n" +
            "CONCAT(FORMAT(z.p4/z.p3 *100,1),'%') as p7,\n" +
            "CONCAT(FORMAT(z.p4/z.p2 *100,1),'%') as p8\n" +
            "FROM\n" +
            "(SELECT\n" +
            "date(createTime) as p1,\n" +
            "sum(if(status in (1,2,3,4,5,6,7,8,10,11,12,13,14,15,16,17,18,19),1,0)) as p2,\n" +
            "sum(if(status in (2,3,4,5,6,7,8,10,11,12,13,14,15,16,17,18,19),1,0)) as p3,\n" +
            "sum(if(status not in (1,2,3,4,5,12,13,14,15,16,17,18,19),1,0)) p5,\n" +
            "sum(if(status not in (1,2,3,4,12,13,14,15,16,17,18),1,0)) p4\n" +
            "FROM ordOrder \n" +
            "where disabled=0\n" +
            "group by 1\n" +
            "order by 1 desc)z\n" +
            "limit 10\n" +
            ";")
    List<PassApplyLinksNum> getPassApplyLinksNum();

    // 表5、各环节当天申请订单的当前通过笔数（新户）
    @Select("SELECT \n" +
            "    t2.date AS date,--  申请日期\n" +
            "    t2.applyingNum,--  申请订单\n" +
            "    t2.commitNum,--  提交订单\n" +
            "    t2.autoCheckPassNum,--  机审通过\n" +
            "    t2.firstCheckPassNum,--  初审通过\n" +
            "    t2.secondCheckPassNum,--  复审通过\n" +
            "    t2.lendNum,--  放款\n" +
            "    t2.lendSuccessNum,--  放款成功\n" +
            "    t2.autoCheckNoPassNum,--  机审不通过\n" +
            "    t2.firstCheckNoPassNum,--  初审不通过\n" +
            "\tt2.firstCheckhandNoPassNum,-- 初审手动取消\n" +
            "    t2.secondCheckNoPassNum--  复审不通过\n" +
            "FROM\n" +
            "    \n" +
            "    (SELECT \n" +
            "        DATE(createTime) date, \n" +
            "            sum(if(h.status in (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19),1,0)) applyingNum,\n" +
            "            sum(if(h.status not in (1),1,0)) commitNum,\n" +
            "            sum(if(h.status not in (1,2,12,17),1,0)) autoCheckPassNum, \n" +
            "            sum(if(h.status not in (1,2,3,12,13,15,16,17,18),1,0)) firstCheckPassNum,\n" +
            "            sum(if(h.status not in (1,2,3,4,12,13,14,15,16,17,18),1,0)) secondCheckPassNum,\n" +
            "            sum(if(h.status not in (1,2,3,4,5,12,13,14,15,16,17,18,19),1,0)) lendNum,\n" +
            "            sum(if(h.status in (7,8,9,10,11),1,0)) lendSuccessNum,\n" +
            "            sum(if(h.status in (12),1,0)) autoCheckNoPassNum,\n" +
            "            sum(if(h.status in (13),1,0)) firstCheckNoPassNum,\n" +
            "\t\t\tsum(if(h.status in (15),1,0)) firstCheckhandNoPassNum,\n" +
            "            sum(if(h.status in (14),1,0)) secondCheckNoPassNum\n" +
            "    FROM\n" +
            "        ordOrder h\n" +
            "\tWHERE\n" +
            "        h.disabled = 0\n" +
            "AND borrowingCount=1\n" +
            "    GROUP BY 1) t2\n" +
            "ORDER BY 1 DESC\n" +
            "LIMIT 10;")
    List<PassLinksRate> getPassLinksRate();

    // 表6、各环节当天申请订单的当前通过率（新户）
    @Select("SELECT \n" +
            "    t2.date AS date,\n" +
            "    CONCAT(FORMAT(SUM(t2.commitNum) / SUM(t2.applyingNum) * 100,\n" +
            "                1),\n" +
            "            '%') AS commitRate,--  申请提交率\n" +
            "    CONCAT(FORMAT(SUM(t2.autoCheckPassNum) / SUM(t2.commitNum) * 100,\n" +
            "                1),\n" +
            "            '%') AS autoCheckPassRate,--  机审通过率\n" +
            "    CONCAT(FORMAT(SUM(t2.firstCheckPassNum) / SUM(t2.autoCheckPassNum) * 100,\n" +
            "                1),\n" +
            "            '%') AS firstCheckPassRate,--  初审通过率\n" +
            "    CONCAT(FORMAT(SUM(t2.secondCheckPassNum) / SUM(t2.firstCheckPassNum) * 100,\n" +
            "                1),\n" +
            "            '%') AS secondCheckPassRate,--  复审通过率\n" +
            "    CONCAT(FORMAT(SUM(t2.secondCheckPassNum) / SUM(t2.applyingNum) * 100,\n" +
            "                1),\n" +
            "            '%') AS applyPassRate,--  申请通过率\n" +
            "    CONCAT(FORMAT(SUM(t2.lendSuccessNum) / SUM(t2.lendNum) * 100,\n" +
            "                1),\n" +
            "            '%') AS lendSuccessRate,--  放款成功率\n" +
            "    CONCAT(FORMAT(SUM(t2.secondCheckPassNum) / SUM(t2.commitNum) * 100,\n" +
            "                1),\n" +
            "            '%') AS applyAllRate--  机审+人工审核通过率  \n" +
            "FROM\n" +
            "    \n" +
            "    (SELECT \n" +
            "        DATE(createTime) date,\n" +
            "            sum(if(h.status in (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19),1,0)) applyingNum,\n" +
            "            sum(if(h.status not in (1),1,0)) commitNum,\n" +
            "            sum(if(h.status not in (1,2,12,17),1,0)) autoCheckPassNum,\n" +
            "            sum(if(h.status not in (1,2,3,12,13,15,16,17,18),1,0)) firstCheckPassNum,\n" +
            "            sum(if(h.status not in (1,2,3,4,12,13,14,15,16,17,18),1,0)) secondCheckPassNum,\n" +
            "            sum(if(h.status not in (1,2,3,4,5,12,13,14,15,16,17,18,19),1,0)) lendNum,\n" +
            "            sum(if(h.status in (7,8,9,10,11),1,0)) lendSuccessNum\n" +
            "   \n" +
            "    FROM\n" +
            "        ordOrder h\n" +
            "\tWHERE\n" +
            "        h.disabled = 0\n" +
            "AND borrowingCount=1\n" +
            "    GROUP BY 1) t2\n" +
            "GROUP BY 1\n" +
            "ORDER BY 1 DESC\n" +
            "LIMIT 10;")
    List<PassLinksRateOld> getPassLinksRateOld();

    // 表7、各环节当天申请订单的当前通过率（老户）
    @Select("SELECT \n" +
            "    t2.date AS date,\n" +
            "\tsum(t2.applyingNum) as applyingNum,-- 申请笔数\n" +
            "\tsum(t2.commitNum) as commitNum,-- 提交笔数\n" +
            "\tsum(t2.autoCheckPassNum) as autoCheckPassNum,-- 通过笔数\n" +
            "\tsum(t2.lendNum) as lendNum,-- 放款笔数\n" +
            "    CONCAT(FORMAT(SUM(t2.commitNum) / SUM(t2.applyingNum) * 100,\n" +
            "                1),\n" +
            "            '%') AS commitRate,--  申请提交率\n" +
            "\tCONCAT(FORMAT(SUM(t2.autoCheckPassNum) / SUM(t2.commitNum) * 100,\n" +
            "                1),\n" +
            "            '%') AS applyAllRate,--  提交通过率（机审）  \t\t\n" +
            "    \n" +
            "    CONCAT(FORMAT(SUM(t2.autoCheckPassNum) / SUM(t2.applyingNum) * 100,\n" +
            "                1),\n" +
            "            '%') AS applyPassRate,--  申请通过率\n" +
            "    CONCAT(FORMAT(SUM(t2.lendSuccessNum) / SUM(t2.lendNum) * 100,\n" +
            "                1),\n" +
            "            '%') AS lendSuccessRate--  放款成功率\n" +
            "\n" +
            "FROM\n" +
            "    \n" +
            "    (SELECT \n" +
            "        DATE(createTime) date,\n" +
            "            sum(if(h.status in (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17),1,0)) applyingNum,\n" +
            "            sum(if(h.status not in (1),1,0)) commitNum,\n" +
            "            sum(if(h.status not in (1,2,12,17),1,0)) autoCheckPassNum,\n" +
            "            sum(if(h.status not in (1,2,3,4,5,12,13,14,15,16,17),1,0)) lendNum,\n" +
            "            sum(if(h.status in (7,8,9,10,11),1,0)) lendSuccessNum\n" +
            "   \n" +
            "    FROM\n" +
            "        ordOrder h\n" +
            "\tWHERE\n" +
            "        h.disabled = 0\n" +
            "AND borrowingCount>1\n" +
            "    GROUP BY 1) t2\n" +
            "GROUP BY 1\n" +
            "ORDER BY 1 DESC\n" +
            "LIMIT 10;")
    List<TodayApplyOrderRate> getTodayApplyOrderRate();

    // 表8、注册用户当日页面通过率
    @Select("--  注册用户当日页面通过率\n" +
            "select\n" +
            "usr.createDay,                                                                                        -- 日期\n" +
            "count(usr.uuid) as registerNum,                                                            -- 注册数\n" +
            "concat(round(sum(apply)/count(usr.uuid)*100,2),'%') as applyRate,              -- 注册申请率\n" +
            "concat(round(sum(role)/sum(apply)*100,2),'%') as roleRate,                     -- 选择角色通过率\n" +
            "concat(round(sum(identity)/sum(role)*100,2),'%') as identityRate,              -- 填写身份信息通过率\n" +
            "concat(round(sum(information)/sum(identity)*100,2),'%') as informationRate,    -- 基本信息通过率\n" +
            "concat(round(sum(work)/sum(information)*100,2),'%') as workRate,               -- 工作或学校信息通过率\n" +
            "concat(round(sum(contacts)/sum(work)*100,2),'%') as contactsRate,              -- 紧急联系人信息通过率\n" +
            "concat(round(sum(verificate)/sum(contacts)*100,2),'%') as verificateRate,      -- 验证信息通过率\n" +
            "concat(round(sum(bank)/sum(verificate)*100,2),'%') as bankRate,                -- 银行卡通过率\n" +
            "concat(round(sum(extra)/sum(bank)*100,2),'%') as extraRate                     -- 额外信息填写率\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "uuid\n" +
            "from usrUser\n" +
            "where disabled = 0\n" +
            "and date(createTime) between date_sub(curdate(),interval 10 day) and curdate()\n" +
            "group by createDay,uuid\n" +
            ")usr\n" +
            "left join\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "useruuid,\n" +
            "uuid,\n" +
            "case when orderStep >=0 then 1 else 0 end apply,\n" +
            "case when orderStep >=1 then 1 else 0 end role,\n" +
            "case when orderStep >=2 then 1 else 0 end identity,\n" +
            "case when orderStep >=3 then 1 else 0 end information,\n" +
            "case when orderStep >=4 then 1 else 0 end work,\n" +
            "case when orderStep >=5 then 1 else 0 end contacts,\n" +
            "case when orderStep >=6 then 1 else 0 end verificate,\n" +
            "case when orderStep >=7 then 1 else 0 end bank,\n" +
            "case when orderStep >=8 then 1 else 0 end extra\n" +
            "from ordOrder\n" +
            "where id in \n" +
            "(select min(id) from ordOrder \n" +
            " where disabled = 0 \n" +
            " and borrowingCount = 1 \n" +
            " and date(createTime) BETWEEN date_sub(CURDATE(), interval 10 day) and CURDATE() \n" +
            " GROUP BY date(createTime),useruuid)\n" +
            ") ord on ord.useruuid = usr.uuid and ord.createDay = usr.createDay\n" +
            "group by usr.createDay\n" +
            "order by usr.createDay desc;\n")
    List<PassLinksAllRate> getPassLinksAllRate();


    // ????uuid ? updateTime ??
    @Select("select * from ordBlack where disabled = 0 and  userUuid = #{userUuid} order by updateTime desc ")
    List<OrdBlack> getOrderBlakByUserUuidAndDescByUpdateTime(String userUuid);

    // 表9、验证信息页面各段通过率
    @Select("SELECT\n" +
            "usr.createDay,                                                                               -- 日期   \n" +
            "concat(round(sum(bodyIdentify)/count(backlink.orderNo)*100,2),'%') as bodyPassRate,          -- 活体识别通过率\n" +
            "concat(round(sum(videoIdentify)/sum(bodyIdentify)*100,2),'%') as videoPassRate,              -- 视频认证通过率\n" +
            "concat(round(sum(thirdIdentify)/sum(videoIdentify)*100,2),'%') as thirdPassRate              -- 三方认证通过率\n" +
            "from(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "uuid\n" +
            "from usrUser\n" +
            "where disabled = 0\n" +
            "and date(createTime) BETWEEN date_sub(CURDATE(), interval 10 day)  and CURDATE()\n" +
            "and userSource not in (2)\n" +
            "group by createDay,uuid\n" +
            ")usr \n" +
            "inner join\n" +
            "(\n" +
            "select\n" +
            "date(createTime) as createDay,\n" +
            "useruuid,\n" +
            "uuid,\n" +
            "orderStep\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and orderStep >= 4\n" +
            "and borrowingCount = 1\n" +
            "and id in \n" +
            "(select min(id) from ordOrder \n" +
            " where disabled = 0 \n" +
            " and borrowingCount = 1 \n" +
            " and date(createTime) BETWEEN date_sub(CURDATE(), interval 10 day) and CURDATE() \n" +
            " GROUP BY date(createTime),useruuid)\n" +
            "and orderType in(0,2)\n" +
            ")ord on usr.uuid = ord.useruuid \n" +
            "left join\n" +
            "(\n" +
            "SELECT\n" +
            "userUuid,\n" +
            "count(DISTINCT contactsMobile) as linkNum\n" +
            "FROM\n" +
            "\t`usrLinkManInfo`\n" +
            "where disabled = 0\n" +
            "GROUP BY useruuid\n" +
            ")usrlink  on ord.useruuid = usrlink.userUuid\n" +
            "left join\n" +
            "(\n" +
            "SELECT \n" +
            "date(createTime) as createDay,\n" +
            "orderNo,\n" +
            "userUuid,\n" +
            "count(distinct linkmanNumber) as backlinkNum\n" +
            "FROM `backupLinkmanItem`\n" +
            "where disabled = 0\n" +
            "GROUP BY createDay,orderNo,useruuid\n" +
            ") backlink on ord.uuid = backlink.orderNo\n" +
            "left join\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\n" +
            "(\n" +
            "select\n" +
            "useruuid,\n" +
            "max(case when certificationType = 2 then 1 else 0 end)  as bodyIdentify,\n" +
            "max(case when certificationType = 3 then 1 else 0 end) videoIdentify,\n" +
            "max(case when certificationType in (4,9,10) then 1 else 0 end) thirdIdentify\n" +
            "from usrCertificationInfo\n" +
            "where disabled = 0\n" +
            "and certificationType in (2,3,4,9,10)\n" +
            "group by useruuid\n" +
            ")usrcer on usrcer.useruuid = ord.useruuid\n" +
            "group by usr.createDay    \n" +
            "ORDER BY usr.createDay DESC;")
    List<CertifacationRate> getCertifacationRate();

    // 实名认证每日拒绝率 聚信立 Advance 税卡号
    @Select("select\n" +
            "  DATE(p.updateTime) as p1, #验证时间\n" +
            "SUM(IF(p.verifyType=6 and p.verifyResult=2,1,0)) as p2, #IZI未通过人数\n" +
            "SUM(IF(p.verifyType=6 and p.verifyResult in (1,2),1,0)) as p3, #IZI总人数\n" +
            "Concat(Format(SUM(IF(p.verifyType=6 and p.verifyResult=2,1,0))/SUM(IF(p.verifyType=6 and p.verifyResult in (1,2),1,0))*100,1),'%') as 'p4', #IZI未通过比例\n" +
            "SUM(IF(m.verifyType=1 and m.verifyResult=2,1,0)) as 'p5', #JXL未通过人数\n" +
            "SUM(IF(m.verifyType=1 and m.verifyResult in (1,2),1,0)) as 'p6', #JXL总人数\n" +
            "Concat(Format(SUM(IF(m.verifyType=1 and m.verifyResult=2,1,0))/SUM(IF(m.verifyType=1 and m.verifyResult in (1,2),1,0))*100,1),'%') as 'p7', #JXL未通过率\n" +
            "CONCAT(Format(SUM(IF(m.verifyType=1 and m.verifyResult=2,1,0))/SUM(IF(p.verifyType=6 and p.verifyResult in (1,2),1,0))*100,1),'%') as 'p8', #IZI聚信立拒绝率\n" +
            "SUM(IF(n.verifyType=2 and n.verifyResult=2,1,0)) as 'p9', #Advance未通过人数\n" +
            "SUM(IF(n.verifyType=2 and n.verifyResult in (1,2),1,0)) as 'p10', #Advance总人数\n" +
            "CONCAT(FORMAT(SUM(IF(n.verifyType=2 and n.verifyResult=2,1,0))/SUM(IF(n.verifyType=2 and n.verifyResult in (1,2),1,0))*100,1),'%') as 'p11', #Advance未通过率\n" +
            "concat(format((1-(sum(if(m.verifyType=1 and m.verifyResult=1,1,0))+sum(if(n.verifyType=2 and n.verifyResult=1,1,0))+sum(if(p.verifyType=6 and p.verifyResult=1,1,0)))/\n" +
            "SUM(IF(p.verifyType=6 and p.verifyResult in (1,2),1,0)))*100,1),'%') as 'p12' #实名认证拒绝率\n" +
            "from doit.usrVerifyResult p\n" +
            "left join \n" +
            "(SELECT distinct\n" +
            " orderNo,userUuid,verifyType,verifyResult,updateTime\n" +
            "FROM doit.usrVerifyResult \n" +
            "where verifyType=1 #JXL\n" +
            "and disabled=0 \n" +
            "GROUP BY 1,2,3,4,5) m on m.userUuid=p.userUuid\n" +
            "left join \n" +
            "(SELECT distinct\n" +
            "userUuid, verifyResult,verifyType\n" +
            "FROM doit.usrVerifyResult\n" +
            "where verifyType=2 #Advance\n" +
            "and disabled=0\n" +
            "GROUP BY 1,2,3) n on n.userUuid=p.userUuid\n" +
            "JOIN \n" +
            "doit.ordOrder d on d.userUuid=p.userUuid\n" +
            "where d.disabled=0 and p.verifyType=6 and borrowingCount=1\n" +
            "and status>1\n" +
            "GROUP BY 1\n" +
            "ORDER BY 1 DESC\n" +
            "limit 10;")
    List<AdvanceVerifyRejectRate> getAdvanceVerifyRejectList();

    // 表11、各渠道今日注册、申请、提交情况
    @Select("select\n" +
            "createDay,                                                                  -- 日期\n" +
            "userSource,                                                                 -- 渠道\n" +
            "registerNum,                                                                -- 注册数\n" +
            "applyNum,                                                                   -- 申请数\n" +
            "submitNum,                                                                  -- 提交数\n" +
            "lengdingNum,                                                                -- 放款数\n" +
            "ifnull(concat(round(applyNum/registerNum*100,2),'%'),'-') as applyRate,     -- 注册申请率\n" +
            "ifnull(concat(round(submitNum/applyNum*100,2),'%'),'-') as submitRate,      -- 申请提交率\n" +
            "ifnull(concat(round(lengdingNum/submitNum*100,2),'%'),'-') as lendingRate   -- 提交放款率\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "date(usr.createTime) as createDay,\n" +
            "case\n" +
            "when userSource = 1 then 'Android'\n" +
            "when userSource = 2 then 'IOS'\n" +
            "else concat('贷超商',userSource) end as userSource,\n" +
            "count(uuid) as registerNum,\n" +
            "sum(case when applyNum >= 1 then 1 else 0 end) as applyNum,\n" +
            "sum(case when submitNum >= 1 then 1 else 0 end) as submitNum,\n" +
            "sum(case when lengdingNum >= 1 then 1 else 0 end) as lengdingNum\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "createTime,\n" +
            "uuid,\n" +
            "mobileNumberDES,\n" +
            "userSource\n" +
            "from usrUser\n" +
            "where disabled = 0\n" +
            "and date(createTime) between date_sub(curdate(),interval 2 day) and date_sub(curdate(),interval 0 day)\n" +
            ") usr\n" +
            "left join\n" +
            "(\n" +
            "select\n" +
            "userUuid,\n" +
            "sum(case when status >= 1 then 1 else 0 end) as applyNum,\n" +
            "sum(case when status >= 2 then 1 else 0 end) as submitNum,\n" +
            "sum(case when status in (7,8,10,11) then 1 else 0 end) as lengdingNum\n" +
            "from ordOrder\n" +
            "where disabled = 0\n" +
            "and borrowingCount = 1\n" +
            "and date(createTime) between date_sub(curdate(),interval 2 day) and date_sub(curdate(),interval 0 day)\n" +
            "and ordertype in (0,2)\n" +
            "group by userUuid\n" +
            ") ord on ord.userUuid = usr.uuid\n" +
            "group by createDay,userSource\n" +
            ") result\n" +
            "order by createDay,userSource;")
    List<ApplyAndSubmitRate> getApplyAndSubmitRate();


    //  依图认证失败
    @Select("SELECT distinct orderNo,userUuid,responseMessage\n" +
            "FROM doit.ordBlack                    \n" +
            "WHERE disabled = 0 \n" +
            "and responseMessage REGEXP'依图人脸识别相似度小于40'\n" +
            "GROUP BY 1,2,3;")
    List<YituFaildRecord> getYituFaildRecord();


    // 风控内部数据
    // 每日规则拒绝表（命中免电核且被机审拒绝）
    @Select("select \n" +
            "a.date,\n" +
            "b.responseMessage,  -- 被拒规则\n" +
            "b.rejectnum\t,  -- 被拒人数\n" +
            "a.rejetotalnum,  -- 被拒总人数\n" +
            "CONCAT(FORMAT(b.rejectnum/a.rejetotalnum*100,2),'%') as rejetotalrate   -- 占被拒总人数比例\n" +
            "from \n" +
            "(SELECT \t\t\n" +
            "date(d.createTime) as date,\n" +
            "count(distinct d.uuid) as rejetotalnum\t\n" +
            "from ordOrder d \t\t\n" +
            "join ordRiskRecord r on d.uuid=r.orderNo\t\n" +
            "join ordBlack b on b.orderNo=d.uuid \n" +
            "join (select distinct ruleDetailType,ruleDesc,remark from sysAutoReviewRule u where u.disabled=0 and ruleType in (22))u\n" +
            "on u.ruleDetailType=r.ruleDetailType\n" +
            "where STATUS in (12) and d.disabled=0\tand b.disabled=0\t\n" +
            "and borrowingCount=1\t\n" +
            "and datediff(CURDATE(),d.createTime)<3\n" +
            "and r.disabled = 0 and r.ruleRealValue ='true'\n" +
            "group by 1)a\n" +
            "join \n" +
            "(SELECT \t\t\n" +
            "date(d.createTime) as date,\n" +
            "responseMessage,\n" +
            "count(distinct d.uuid) as rejectnum\t\t\n" +
            "from ordOrder d \t\t\n" +
            "join ordRiskRecord r on d.uuid=r.orderNo\t\n" +
            "join ordBlack b on b.orderNo=d.uuid \n" +
            "join (select distinct ruleDetailType,ruleDesc,remark from sysAutoReviewRule u where u.disabled=0 and ruleType in (22))u\n" +
            "on u.ruleDetailType=r.ruleDetailType\n" +
            "where STATUS in (12) and d.disabled=0\tand b.disabled=0\t\n" +
            "and borrowingCount=1\t\n" +
            "and datediff(CURDATE(),d.createTime)<3\n" +
            "and r.disabled = 0 and r.ruleRealValue ='true'\n" +
            "group by 1,2)b on a.date=b.date\n" +
            "where a.date>='2018-06-20'\n" +
            "order by 1 desc,3 desc;")
    List<GetRejectWithNoPhone> getRejectWithNoPhone();

    // 免电核规则当天申请当前通过率
    @Select("select \n" +
            "date,  -- 申请日期\n" +
            "ruleDesc,  -- 免电核规则\n" +
            "commitNum,  -- 提交人数\n" +
            "autoCheckPassNum,  -- 机审通过人数\n" +
            "nupofree,  -- 命中且通过人数\n" +
            "commitrate,  -- 占提交的比例\n" +
            "autocheckrate  -- 占机审的比例\n" +
            "from \n" +
            "((select\n" +
            "a.date, \n" +
            "'合计' as ruleDesc,\n" +
            "a.commitNum,\n" +
            "a.autoCheckPassNum,\n" +
            "b.nupofree,\n" +
            "CONCAT(FORMAT(b.nupofree/a.commitNum*100,2),'%') as commitrate,\n" +
            "CONCAT(FORMAT(b.nupofree/a.autoCheckPassNum*100,2),'%') as autocheckrate\n" +
            "from\n" +
            "(SELECT \n" +
            "DATE(createTime) date,\n" +
            "sum(if(h.status in (2,3,4,5,6,7,8,9,10,11,12,13,14,15,16),1,0)) commitNum,\n" +
            "sum(if(h.status not in (1,2,12),1,0)) autoCheckPassNum \n" +
            "FROM ordOrder h\n" +
            "WHERE h.disabled = 0 AND borrowingCount=1\n" +
            "GROUP BY 1) a\n" +
            "join \n" +
            "(SELECT \t\t\n" +
            "date(d.createTime) date,\n" +
            "count(distinct d.uuid) as nupofree\n" +
            "from ordOrder d \t\t\n" +
            "inner join ordRiskRecord \tr on d.uuid=r.orderNo\t\n" +
            "join (select distinct ruleDetailType,ruleDesc,remark from sysAutoReviewRule u where u.disabled=0 and ruleType in (22))u on u.ruleDetailType=r.ruleDetailType\t\n" +
            "where d.disabled=0\tand r.disabled = 0\n" +
            "and borrowingCount=1\t\n" +
            "and `status` not in (1,2,3,4,12,13,14,15)\n" +
            "and r.ruleRealValue ='true'\n" +
            "group by 1)\tb on  a.date=b.date)\n" +
            "union all\n" +
            "(select\n" +
            "a.date, \n" +
            "ruleDesc,\n" +
            "a.commitNum,\n" +
            "a.autoCheckPassNum,\n" +
            "b.nupofree,\n" +
            "CONCAT(FORMAT(b.nupofree/a.commitNum*100,2),'%') as commitrate,\n" +
            "CONCAT(FORMAT(b.nupofree/a.autoCheckPassNum*100,2),'%') as autocheckrate\n" +
            "from\n" +
            "(SELECT \n" +
            "DATE(createTime) date,\n" +
            "sum(if(h.status in (2,3,4,5,6,7,8,9,10,11,12,13,14,15,16),1,0)) commitNum,\n" +
            "sum(if(h.status not in (1,2,12),1,0)) autoCheckPassNum \n" +
            "FROM ordOrder h\n" +
            "WHERE h.disabled = 0 AND borrowingCount=1\n" +
            "GROUP BY 1) a\n" +
            "left join \n" +
            "(SELECT \t\t\n" +
            "date(d.createTime) date,\n" +
            "u.ruleDesc,\n" +
            "count(distinct d.uuid) as nupofree\n" +
            "from ordOrder d \t\t\n" +
            "inner join ordRiskRecord \tr on d.uuid=r.orderNo\t\n" +
            "join (select distinct ruleDetailType,ruleDesc,remark from sysAutoReviewRule u where u.disabled=0 and ruleType in (22))u on u.ruleDetailType=r.ruleDetailType\t\t\n" +
            "where d.disabled=0\tand r.disabled = 0\n" +
            "and borrowingCount=1\t\n" +
            "and `status` not in (1,2,3,4,12,13,14,15)\n" +
            "and r.ruleRealValue ='true'\n" +
            "group by 1,2)\tb on  a.date=b.date\n" +
            "where DATEDIFF(CURDATE(),a.date)<3\n" +
            "and ruleDesc is not null)\n" +
            " ) a\n" +
            "where datediff(CURDATE(),date)<3\n" +
            "and date>='2018-06-20'\n" +
            "order by 1 desc,5;")
    List<GetPassRateWithNoPhone> getPassRateWithNoPhone();

    // 免电核规则机审拒绝率
    @Select("select \n" +
            "a.date,  -- 申请日期\n" +
            "a.ruleDesc,  -- 免电核规则\n" +
            "a.hitsnum,  -- 命中人数\n" +
            "a.rejectnum,  -- 拒绝人数\n" +
            "autocheckrate  -- 免电核规则机审拒绝率\n" +
            "from \n" +
            "((select \n" +
            "a.date,\n" +
            "a.ruleDesc,\n" +
            "a.hitsnum,\n" +
            "IFNULL(b.rejectnum,0) as rejectnum,\n" +
            "CONCAT(FORMAT(b.rejectnum/a.hitsnum*100,2),'%') as autocheckrate\n" +
            "from\n" +
            "(SELECT \n" +
            "date(d.createTime) as date,\t\t\n" +
            "'合计' as ruleDesc,\n" +
            "count(distinct d.uuid) as hitsnum\t\t\n" +
            "from ordOrder d \t\t\n" +
            "join ordRiskRecord r on d.uuid=r.orderNo\t\n" +
            "join (select distinct ruleDetailType,ruleDesc,remark from sysAutoReviewRule u where u.disabled=0 and ruleType in (22))u\n" +
            "on u.ruleDetailType=r.ruleDetailType\n" +
            "where  d.disabled=0\t\t\n" +
            "and borrowingCount=1\t\n" +
            "and date(d.createTime)>='2018-06-20'\n" +
            "and r.disabled = 0 and r.ruleRealValue ='true'\n" +
            "group by 1,2)a\n" +
            "left join \n" +
            "(SELECT \n" +
            "date(d.createTime) as date,\t\n" +
            "'合计' as ruleDesc,\n" +
            "count(distinct d.uuid) as rejectnum\t\n" +
            "from ordOrder d \t\t\n" +
            "join ordRiskRecord r on d.uuid=r.orderNo\n" +
            "join (select distinct ruleDetailType,ruleDesc,remark from sysAutoReviewRule u where u.disabled=0 and ruleType in (22))u\n" +
            "on u.ruleDetailType=r.ruleDetailType\t\n" +
            "where  d.disabled=0\t\t\n" +
            "and borrowingCount=1\t\n" +
            "and date(d.createTime)>='2018-06-20'\n" +
            "and `status` in (12)\n" +
            "and r.disabled = 0 and r.ruleRealValue ='true'\n" +
            "group by 1,2)b on a.date=b.date and a.ruleDesc=b.ruleDesc)\n" +
            "union ALL\n" +
            "(select \n" +
            "a.date,\n" +
            "a.ruleDesc,\n" +
            "a.hitsnum,\n" +
            "IFNULL(b.rejectnum,0) as rejectnum,\n" +
            "CONCAT(FORMAT(b.rejectnum/a.hitsnum*100,2),'%') as autocheckrate\n" +
            "from\n" +
            "(SELECT \n" +
            "date(d.createTime) as date,\t\t\n" +
            "u.ruleDesc,\n" +
            "u.ruleDetailType,\n" +
            "count(distinct d.uuid) as hitsnum\t\t\n" +
            "from ordOrder d \t\t\n" +
            "join ordRiskRecord r on d.uuid=r.orderNo\t\n" +
            "join (select distinct ruleDetailType,ruleDesc,remark from sysAutoReviewRule u where u.disabled=0 and ruleType in (22))u\n" +
            "on u.ruleDetailType=r.ruleDetailType\t\n" +
            "where  d.disabled=0\t\t\n" +
            "and borrowingCount=1\t\n" +
            "and date(d.createTime)>='2018-06-20'\n" +
            "and r.disabled = 0 and r.ruleRealValue ='true'\n" +
            "group by 1,2,3)a\n" +
            "left join \n" +
            "(SELECT \n" +
            "date(d.createTime) as date,\t\t\n" +
            "u.ruleDesc,\n" +
            "u.ruleDetailType,\n" +
            "count(distinct d.uuid) as rejectnum\t\n" +
            "from ordOrder d \t\t\n" +
            "join ordRiskRecord \tr on d.uuid=r.orderNo\t\n" +
            "join (select distinct ruleDetailType,ruleDesc,remark from sysAutoReviewRule u where u.disabled=0 and ruleType in (22))u\n" +
            "on u.ruleDetailType=r.ruleDetailType\t\n" +
            "where  d.disabled=0\t\t\n" +
            "and borrowingCount=1\t\n" +
            "and date(d.createTime)>='2018-06-20'\n" +
            "and r.disabled = 0 and r.ruleRealValue ='true'\n" +
            "and `status` in (12)\n" +
            "group by 1,2,3)b on a.date=b.date and a.ruleDetailType=b.ruleDetailType))a\n" +
            "where datediff(CURDATE(),date)<3\n" +
            "order by 1 desc ,4;")
    List<GetRefuseRateWithNoPhone> getRefuseRateWithNoPhone();

    @Select("select count(1) from ordBlack where orderNo=#{orderNo} and remark='ONLY_REAL_NAME_VERIFY_FAILED:HAS_INSURANCE_CARD' and ruleHitNo like" +
            " '%ADVANCE_VERIFY_RULE'")
    Integer advanceVerifyFailedWithInsuranceCard(@Param("orderNo") String orderNo);

    @Select("select count(1) from ordBlack where orderNo=#{orderNo} and remark like 'ONLY_REAL_NAME_VERIFY_FAILED:%' and ruleHitNo " +
            " like '%ADVANCE_VERIFY_RULE'")
    Integer advanceVerifyFailedWithSpecialRemark(@Param("orderNo") String orderNo);

    @Select("\n" +
            "select count(1) from ordOrder o where o.uuid in (\n" +
            "  select orderNo from ordBlack ob where ob.remark = 'ONLY_REAL_NAME_VERIFY_FAILED:HAS_INSURANCE_CARD'\n" +
            ") and o.disabled=0 and o.status in (5,6,7,8,10,11)")
    Integer totalAdvanceVerifyFailedWithInsuranceCard();

    @Select("\n" +
            "select count(1) from ordOrder o where o.uuid in (\n" +
            "  select orderNo from ordBlack ob where ob.remark = #{remark}\n" +
            ") and o.disabled=0 and o.status  in (5,6,7,8,10,11)")
    Integer totalIssuedABTestOrders(@Param("remark") String remark);
    @Select("\n" +
            "select count(1) from ordOrder o where o.uuid in (\n" +
            "  select orderNo from ordBlack ob where ob.remark = #{remark}\n" +
            ") and o.disabled=0 and o.status  in (17,5,6,7,8,10,11)")
    Integer totalAutoReviewPassABTestOrders(@Param("remark") String remark);

    @Select("\n" +
            "select count(1) from ordOrder o where o.uuid in (\n" +
            "  select orderNo from ordBlack ob where ob.remark = #{remark}\n" +
            ") and o.disabled=0 and o.status  in (5,6,7,8,10,11) and o.uuid in (select orderNo from orderScore where manualReview = 0 )")
    Integer getABTestNotManualReviewCount(@Param("remark") String remark);


    // 当前累计外呼通过率
    @Select("SELECT \n" +
            "m.本人外呼日期 as cusNewCallDate,\n" +
            "外呼已发送未拿到报告订单数 as calledNoReportOrd,\n" +
            "发送外呼请求失败订单数 as calledFailedOrd,\n" +
            "有效外呼订单数 as validCallOrd,\n" +
            "外呼接通订单数 as connectOrd,\n" +
            "(外呼表的继续拨打订单数-ifnull(本人号码外呼超过次数的订单,0)) as  keepCallingOrd, #外呼继续拨打订单数\n" +
            "(外呼表的拒绝订单数+ifnull(本人号码外呼超过次数的订单,0)) as callRejectOrd, #外呼拒绝订单数\n" +
            "外呼完成订单的接通率 as connectRate\n" +
            "from \n" +
            "(select\n" +
            "  DATE(t.本人外呼日期) as 本人外呼日期,\n" +
            "  SUM(if(callState=1,1,0)) as 外呼已发送未拿到报告订单数,\n" +
            "  SUM(if(callState=3,1,0)) as 发送外呼请求失败订单数,\n" +
            "  SUM(if(callState=2,1,0)) as 有效外呼订单数, \n" +
            "  SUM(if(errorId=5000,1,0)) as 外呼接通订单数,\n" +
            "  SUM(if(callResultType=2 or errorId in (5002,5003,5603),1,0)) as 外呼表的继续拨打订单数, #最后一次外呼本人的时间，状态。如果该状态为2\n" +
            "  SUM(if(callResultType=3,1,0)) as 外呼表的拒绝订单数, #最后一次外呼本人的时间，状态。如果该状态为3\n" +
            "  concat(format(SUM(if(errorId=5000,1,0))/SUM(if(callState=2,1,0))*100,2),'%') as 外呼完成订单的接通率\n" +
            "from \n" +
            "doit.teleCallResult r\n" +
            "join \n" +
            "  (select Max(createTime) as 本人外呼日期, orderNo from doit.teleCallResult where callType=1 and disabled=0\n" +
            "GROUP BY 2) t on r.orderNo=t.orderNo and r.createTime= t.本人外呼日期\n" +
            "where callType=1 and disabled=0  \n" +
            "GROUP BY 1\n" +
            "ORDER BY 1 DESC) m  -- 外呼表里的详情\n" +
            "left join \n" +
            "(select DATE(createTime) as 本人外呼日期, count(DISTINCT orderNo) as 本人号码外呼超过次数的订单 from doit.ordBlack where responseMessage='本人号码外呼超过次数'\n" +
            "and disabled=0\n" +
            "GROUP BY 1) w -- 拒绝表里因超过外呼次数被拒的订单\n" +
            "on w.本人外呼日期=m.本人外呼日期\n" +
            "limit 10;")
    List<RiskCall1> getRiskCall1();

    // 当前累计外呼通过率
    @Select("select m.本人外呼日期 as cusNewCallDate, -- cusNewCallDate\n" +
            "有效外呼订单数 as validCallOrd, -- validCallOrd\n" +
            "号码无效订单数 as invalidNumOrd, -- invalidNumOrd\n" +
            "运营商拒绝订单数 as opeRejectOrd, -- opeRejectOrd\n" +
            "号码不存在订单数 as notExistNumOrd, -- notExistNumOrd\n" +
            "本人号码外呼超过次数的订单 as overTimesOrd, -- overTimesOrd\n" +
            "外呼拒绝订单数 as callRejectOrd,  -- callRejectOrd\n" +
            "号码不存在的拒绝率 as notExistNumRate, -- notExistNumRate\n" +
            "号码无效的拒绝率 as invalidNumRejectRate, -- invalidNumRejectRate\n" +
            "运营商拒绝的拒绝率 as opeRejectRate, -- opeRejectRate\n" +
            "本人号码外呼超过次数的订单拒绝率 as overTimesRate, -- overTimesRate\n" +
            "外呼拒绝率 as callRejectRate -- callRejectRate\n" +
            " from(\n" +
            "SELECT \n" +
            "m.本人外呼日期, -- cusNewCallDate\n" +
            "有效外呼订单数, -- validCallOrd\n" +
            "号码无效订单数, -- invalidNumOrd\n" +
            "运营商拒绝订单数, -- opeRejectOrd\n" +
            "号码不存在订单数, -- notExistNumOrd\n" +
            "ifnull(本人号码外呼超过次数的订单,0) as 本人号码外呼超过次数的订单, -- overTimesOrd\n" +
            "(外呼表的拒绝订单数+ifnull(本人号码外呼超过次数的订单,0)) as 外呼拒绝订单数,  -- callRejectOrd\n" +
            "concat(format(号码不存在订单数/有效外呼订单数*100,2),'%') as 号码不存在的拒绝率, -- notExistNumRate\n" +
            "concat(format(号码无效订单数/有效外呼订单数*100,2),'%') as 号码无效的拒绝率, -- invalidNumRejectRate\n" +
            "concat(format(运营商拒绝订单数/有效外呼订单数*100,2),'%') as 运营商拒绝的拒绝率, -- opeRejectRate\n" +
            "concat(format(ifnull(本人号码外呼超过次数的订单,0)/有效外呼订单数*100,2),'%') as 本人号码外呼超过次数的订单拒绝率, -- overTimesRate\n" +
            "concat(format(((ifnull(本人号码外呼超过次数的订单,0)+外呼表的拒绝订单数))/有效外呼订单数*100,2),'%') as 外呼拒绝率 -- callRejectRate\n" +
            "from \n" +
            "(select\n" +
            "  DATE(t.本人外呼日期) as 本人外呼日期,\n" +
            "  SUM(if(callState=2,1,0)) as 有效外呼订单数, \n" +
            "  SUM(if(errorId=5404,1,0)) as 号码不存在订单数,\n" +
            "  SUM(if(errorId=5484,1,0)) as 号码无效订单数,\n" +
            "  SUM(if(errorId=5492,1,0)) as 运营商拒绝订单数,\n" +
            "  SUM(if(callResultType=3,1,0)) as 外呼表的拒绝订单数\n" +
            "from \n" +
            "teleCallResult r\n" +
            "join \n" +
            "  (select Max(createTime) as 本人外呼日期, orderNo from teleCallResult where callType=1 and disabled=0\n" +
            "GROUP BY 2) t on r.orderNo=t.orderNo and r.createTime=本人外呼日期\n" +
            "where callType=1 and disabled=0\n" +
            "GROUP BY 1\n" +
            "ORDER BY 1 DESC) m -- 外呼表里的详情\n" +
            "left join \n" +
            "  (select DATE(createTime) 本人外呼日期, count(DISTINCT orderNo) as 本人号码外呼超过次数的订单 from ordBlack where responseMessage='本人号码外呼超过次数'\n" +
            "and disabled=0\n" +
            "GROUP BY 1) w -- 拒绝表里因超过外呼次数被拒的订单\n" +
            "on w.本人外呼日期=m.本人外呼日期\n" +
            "limit 10)m;")
    List<RiskCall2> getRiskCall2();

    // 当天创建订单的外呼通过率--漏斗
    @Select("SELECT \n" +
            "m.本人开始外呼日期 as p1,\n" +
            "外呼已发送未拿到报告订单数 as p2,\n" +
            "发送外呼请求失败订单数 as p3,\n" +
            "有效外呼订单数 as p4,\n" +
            "外呼接通订单数 as p5, \n" +
            "(外呼表的继续拨打订单数-ifnull(本人号码外呼超过次数的订单,0)) as p6, #外呼继续拨打订单数\n" +
            "(外呼表的拒绝订单数+ifnull(本人号码外呼超过次数的订单,0)) as p7, #外呼拒绝订单数\n" +
            "外呼完成订单的接通率 as p8\n" +
            "from \n" +
            "(select\n" +
            "  DATE(s.本人开始外呼日期) as 本人开始外呼日期,\n" +
            "  SUM(if(callState=1,1,0)) as 外呼已发送未拿到报告订单数,\n" +
            "  SUM(if(callState=3,1,0)) as 发送外呼请求失败订单数,\n" +
            "  SUM(if(callState=2,1,0)) as 有效外呼订单数, \n" +
            "  SUM(if(errorId=5000,1,0)) as 外呼接通订单数,\n" +
            "  SUM(if(callResultType=2 or errorId in (5002,5003,5603),1,0)) as 外呼表的继续拨打订单数,\n" +
            "  SUM(if(callResultType=3,1,0)) as 外呼表的拒绝订单数,\n" +
            "  concat(format(SUM(if(errorId=5000,1,0))/SUM(if(callState=2,1,0))*100,2),'%') as 外呼完成订单的接通率\n" +
            "from \n" +
            "(select MIN(createTime) 本人开始外呼日期, orderNo from doit.teleCallResult where disabled=0 and callType=1 group by 2) s\n" +
            "LEFT JOIN\n" +
            "doit.teleCallResult r on r.orderNo=s.orderNo\n" +
            "join \n" +
            "  (select Max(createTime) as 本人外呼日期, orderNo from doit.teleCallResult where callType=1 and disabled=0\n" +
            "GROUP BY 2) t on r.orderNo=t.orderNo and r.createTime=本人外呼日期\n" +
            "where callType=1 and disabled=0  \n" +
            "GROUP BY 1\n" +
            "ORDER BY 1 DESC) m  -- 外呼表里的详情\n" +
            "left join \n" +
            "  (select date(本人开始外呼日期) as 本人开始外呼日期, count(DISTINCT d.orderNo) as 本人号码外呼超过次数的订单 from \n" +
            "doit.ordBlack b\n" +
            "right join \n" +
            "(select MIN(createTime) as 本人开始外呼日期, orderNo from doit.teleCallResult \n" +
            "where disabled=0 and callType=1 GROUP BY 2) d on d.orderNo=b.orderNo\n" +
            "where responseMessage='本人号码外呼超过次数' and disabled=0\n" +
            "GROUP BY 1) w -- 拒绝表里因超过外呼次数被拒的订单\n" +
            "on w.本人开始外呼日期=m.本人开始外呼日期\n" +
            "limit 10;")
    List<RiskCall3> getRiskCall3();

    // 当天创建订单的外呼拒绝率详情--漏斗
    @Select("select 本人开始外呼日期 as p1, -- cusEarliestCallDate\n" +
            "有效外呼订单数 as p2, -- validCallOrd\n" +
            "号码无效订单数 as p3, -- invalidNumOrd\n" +
            "运营商拒绝订单数 as p4, -- opeRejectOrd\n" +
            "号码不存在订单数 as p5,-- notExistNumOrd\n" +
            "本人号码外呼超过次数的订单 as p6, -- overTimesOrd\n" +
            "外呼拒绝订单数 as p7,  -- callRejectOrd\n" +
            "号码不存在的拒绝率 as p8, -- notExistNumRate\n" +
            "号码无效的拒绝率 as p9,  -- invalidNumRejectRate\n" +
            "运营商拒绝的拒绝率 as p10, -- opeRejectRate\n" +
            "本人号码外呼超过次数的订单拒绝率 as p11, -- overTimesRate\n" +
            "外呼拒绝率 as p12 -- callRejectRate\n" +
            " from(\n" +
            "SELECT \n" +
            "m.本人开始外呼日期, -- cusEarliestCallDate\n" +
            "有效外呼订单数, -- validCallOrd\n" +
            "号码无效订单数, -- invalidNumOrd\n" +
            "运营商拒绝订单数, -- opeRejectOrd\n" +
            "号码不存在订单数,-- notExistNumOrd\n" +
            "ifnull(本人号码外呼超过次数的订单,0) as 本人号码外呼超过次数的订单, -- overTimesOrd\n" +
            "(外呼表的拒绝订单数+ifnull(本人号码外呼超过次数的订单,0)) as 外呼拒绝订单数,  -- callRejectOrd\n" +
            "concat(format(号码不存在订单数/有效外呼订单数*100,2),'%') as 号码不存在的拒绝率, -- notExistNumRate\n" +
            "concat(format(号码无效订单数/有效外呼订单数*100,2),'%') as 号码无效的拒绝率,  -- invalidNumRejectRate\n" +
            "concat(format(运营商拒绝订单数/有效外呼订单数*100,2),'%') as 运营商拒绝的拒绝率, -- opeRejectRate\n" +
            "concat(format(ifnull(本人号码外呼超过次数的订单,0)/有效外呼订单数*100,2),'%') as 本人号码外呼超过次数的订单拒绝率, -- overTimesRate\n" +
            "concat(format(((ifnull(本人号码外呼超过次数的订单,0)+外呼表的拒绝订单数))/有效外呼订单数*100,2),'%') as 外呼拒绝率 -- callRejectRate\n" +
            "from \n" +
            "(select\n" +
            "  DATE(s.本人开始外呼日期) as 本人开始外呼日期,\n" +
            "  SUM(if(callState=2,1,0)) as 有效外呼订单数, \n" +
            "  SUM(if(errorId=5404,1,0)) as 号码不存在订单数,\n" +
            "  SUM(if(errorId=5484,1,0)) as 号码无效订单数,\n" +
            "  SUM(if(errorId=5492,1,0)) as 运营商拒绝订单数,\n" +
            "  SUM(if(callResultType=3,1,0)) as 外呼表的拒绝订单数\n" +
            "from \n" +
            "(select MIN(createTime) 本人开始外呼日期, orderNo from teleCallResult where disabled=0 and callType=1 group by 2) s\n" +
            "LEFT JOIN\n" +
            "teleCallResult r on r.orderNo=s.orderNo\n" +
            "join \n" +
            "  (select Max(createTime) as 本人外呼日期, orderNo from teleCallResult where callType=1 and disabled=0\n" +
            "GROUP BY 2) t on r.orderNo=t.orderNo and r.createTime=本人外呼日期\n" +
            "where callType=1 and disabled=0  \n" +
            "GROUP BY 1\n" +
            "ORDER BY 1 DESC) m  -- 外呼表里的详情\n" +
            "left join \n" +
            "  (select date(本人开始外呼日期) as 本人开始外呼日期, count(DISTINCT d.orderNo) as 本人号码外呼超过次数的订单 from \n" +
            "ordBlack b\n" +
            "right join \n" +
            "(select MIN(createTime) as 本人开始外呼日期, orderNo from teleCallResult where disabled=0 and callType=1 GROUP BY 2) d on d.orderNo=b.orderNo\n" +
            "where responseMessage='本人号码外呼超过次数' and disabled=0\n" +
            "GROUP BY 1) w -- 拒绝表里因超过外呼次数被拒的订单\n" +
            "on w.本人开始外呼日期=m.本人开始外呼日期\n" +
            "limit 10)m；")
    List<RiskCall4> getRiskCall4();



    // 老户备选联系人外呼
    @Select(" select b.callDate, #'外呼日期' \n" +
            "\n" +
            "               b.oldAlterCallTotal,  #'老户备选联系人外呼订单数' \n" +
            "\t\n" +
            "               concat(format((a.alterEffectAtLeast6 + a.alterEffect5 + a.alterEffect4 + a.alterEffect3)/b.oldAlterCallTotal*100,1),'%') as 'oldAlterAtLeast3Rate', #'老户外呼备选联系人至少3个号码完全有效'\n" +
            "\n" +
            "               a.alterEffectAtLeast6,#'备选联系人号码完全有效>= 6'\n" +
            "\n" +
            "               a.alterEffect5,#'备选联系人号码完全有效 = 5'\n" +
            "\t\n" +
            "               a.alterEffect4,#'备选联系人号码完全有效 = 4'\n" +
            "\t\n" +
            "               a.alterEffect3,#'备选联系人号码完全有效 = 3'\n" +
            "\n" +
            "               a.alterEffect2,#'备选联系人号码完全有效 = 2'\n" +
            "   \n" +
            "               a.alterEffect1,#'备选联系人号码完全有效 = 1'\n" +
            "\n" +
            "               a.alterEffect0 #'备选联系人号码完全有效 = 0'\n" +
            "\n" +
            "from\n" +
            "\n" +
            "(select date(x.createTime)as 'callDate',\n" +
            " \n" +
            "            count(distinct x.orderNo) as 'oldAlterCallTotal'\n" +
            "\n" +
            " from doit.teleCallResult x \n" +
            "\n" +
            " where x.disabled = 0 and x.callType = 4 and x.callState = 2\n" +
            "   \n" +
            " and exists(select 1 from doit.ordOrder oo where oo.uuid = x.orderNo and oo.borrowingCount>1 and oo.disabled = 0)\n" +
            "\n" +
            " group by 1 desc) b\n" +
            "\n" +
            "join\n" +
            "(select r.callDate,\n" +
            "       sum(if(r.备选联系人号码完全有效 >= 6,1,0)) as 'alterEffectAtLeast6',\n" +
            " \n" +
            "      sum(if(r.备选联系人号码完全有效 = 5,1,0)) as 'alterEffect5',\n" +
            "\n" +
            "       sum(if(r.备选联系人号码完全有效 = 4,1,0)) as 'alterEffect4',\n" +
            "\n" +
            "       sum(if(r.备选联系人号码完全有效 = 3,1,0)) as 'alterEffect3', \n" +
            "\n" +
            "       sum(if(r.备选联系人号码完全有效 = 2,1,0)) as 'alterEffect2',\n" +
            "\t\t\n" +
            "       sum(if(r.备选联系人号码完全有效 = 1,1,0)) as 'alterEffect1',\n" +
            "\t\n" +
            "       sum(if(r.备选联系人号码完全有效 = 0,1,0)) as 'alterEffect0' \n" +
            "\n" +
            "from  \n" +
            "\n" +
            "(select date(c.createTime)as 'callDate', c.orderNo, \n" +
            " count(1) as '备选联系人号码完全有效' \n" +
            "   \n" +
            "         from doit.teleCallResult c\n" +
            "            \n" +
            "         where c.disabled = 0 and c.callType = 4  and c.callResultType = 1\n" +
            "    \n" +
            "        and exists(select 1 from doit.ordOrder o where o.uuid = c.orderNo and o.borrowingCount>1 and o.disabled = 0)  \n" +
            " \n" +
            "           group by 1,2) r          \n" +
            "  \n" +
            "group by 1 desc) a on a.callDate = b.callDate\n" +
            " \n" +
            " limit 20; ")
    List<RiskCall5> getRiskCall5();

    // 新户备选联系人外呼
    @Select("select b.callDate, #'外呼日期' \n" +
            "\n" +
            "               b.newAlterCallTotal,  #'新户备选联系人外呼订单数' \n" +
            "\t\n" +
            "               concat(format((a.alterEffectAtLeast6 + a.alterEffect5 )/b.newAlterCallTotal*100,1),'%') as 'newAlterAtLeast5Rate', #'新户外呼备选联系人至少5个号码完全有效'\n" +
            "\n" +
            "               a.alterEffectAtLeast6,#'备选联系人号码完全有效>= 6'\n" +
            "\n" +
            "               a.alterEffect5,#'备选联系人号码完全有效 = 5'\n" +
            "\t\n" +
            "               a.alterEffect4,#'备选联系人号码完全有效 = 4'\n" +
            "\t\n" +
            "               a.alterEffect3,#'备选联系人号码完全有效 = 3'\n" +
            "\n" +
            "               a.alterEffect2,#'备选联系人号码完全有效 = 2'\n" +
            "   \n" +
            "               a.alterEffect1,#'备选联系人号码完全有效 = 1'\n" +
            "\n" +
            "               a.alterEffect0 #'备选联系人号码完全有效 = 0'\n" +
            "\n" +
            "from\n" +
            "\n" +
            "(select date(x.createTime)as 'callDate',\n" +
            " \n" +
            "            count(distinct x.orderNo) as 'newAlterCallTotal'\n" +
            "\n" +
            " from doit.teleCallResult x \n" +
            "\n" +
            " where x.disabled = 0 and x.callType = 4 and x.callState = 2\n" +
            "   \n" +
            " and exists(select 1 from doit.ordOrder oo where oo.uuid = x.orderNo and oo.borrowingCount =1 and oo.disabled = 0)\n" +
            "\n" +
            " group by 1 desc) b\n" +
            "\n" +
            "join\n" +
            "(select r.callDate,\n" +
            "       sum(if(r.备选联系人号码完全有效 >= 6,1,0)) as 'alterEffectAtLeast6',\n" +
            " \n" +
            "      sum(if(r.备选联系人号码完全有效 = 5,1,0)) as 'alterEffect5',\n" +
            "\n" +
            "       sum(if(r.备选联系人号码完全有效 = 4,1,0)) as 'alterEffect4',\n" +
            "\n" +
            "       sum(if(r.备选联系人号码完全有效 = 3,1,0)) as 'alterEffect3', \n" +
            "\n" +
            "       sum(if(r.备选联系人号码完全有效 = 2,1,0)) as 'alterEffect2',\n" +
            "\t\t\n" +
            "       sum(if(r.备选联系人号码完全有效 = 1,1,0)) as 'alterEffect1',\n" +
            "\t\n" +
            "       sum(if(r.备选联系人号码完全有效 = 0,1,0)) as 'alterEffect0' \n" +
            "\n" +
            "from  \n" +
            "\n" +
            "(select date(c.createTime)as 'callDate', c.orderNo, \n" +
            " count(1) as '备选联系人号码完全有效' \n" +
            "   \n" +
            "         from doit.teleCallResult c\n" +
            "            \n" +
            "         where c.disabled = 0 and c.callType = 4  and c.callResultType = 1\n" +
            "    \n" +
            "        and exists(select 1 from doit.ordOrder o where o.uuid = c.orderNo and o.borrowingCount =1 and o.disabled = 0)  \n" +
            " \n" +
            "           group by 1,2) r          \n" +
            "  \n" +
            "group by 1 desc) a on a.callDate = b.callDate\n" +
            " \n" +
            " limit 20;  ")
    List<RiskCall6> getRiskCall6();

    // 老户紧急联系人外呼
    @Select("select c.date as callDate,#外呼日期\n" +
            "      count(1) as oldEmerCallNum, #老户联系人外呼订单数\n" +
            "      sum(if(c.紧急联系人有效号码个数=4,1,0)) as effect_4Num,#有效4订单数\n" +
            "      sum(if(c.紧急联系人有效号码个数=3,1,0)) as effect_3Num,#有效3订单数\n" +
            "      sum(if(c.紧急联系人有效号码个数=2,1,0)) as effect_2Num,#有效2订单数 \n" +
            "      sum(if(c.紧急联系人有效号码个数=1,1,0)) as effect_1Num,#有效1订单数 \n" +
            "      sum(if(c.紧急联系人有效号码个数=0,1,0)) as effect_0Num,#有效0订单数 \n" +
            "      concat(format(sum(if(c.紧急联系人有效号码个数 = 4,1,0))/count(1)*100,1) ,'%') as effect_4rate,# 有效号码_4的占比\n" +
            "      concat(format(sum(if(c.紧急联系人有效号码个数 = 3,1,0))/count(1)*100,1) ,'%') as effect_3rate,# 有效号码_3的占比\n" +
            "      concat(format(sum(if(c.紧急联系人有效号码个数 = 2,1,0))/count(1)*100,1) ,'%') as effect_2rate,# 有效号码_2的占比\n" +
            "      concat(format(sum(if(c.紧急联系人有效号码个数 = 1,1,0))/count(1)*100,1) ,'%') as effect_1rate,# 有效号码_1的占比\n" +
            "      concat(format(sum(if(c.紧急联系人有效号码个数 = 0,1,0))/count(1)*100,1) ,'%') as effect_0rate,# 有效号码_0的占比\n" +
            "      concat(format(sum(if(c.紧急联系人有效号码个数>=1,1,0))/count(1)*100,1) ,'%') as  oldEmerCallPassRate #老户联系人外呼通过率\n" +
            "from\n" +
            "(select a.date,a.orderNo,a.inforbip有效号码个数,\n" +
            "  b.twilio有效号码个数,\n" +
            "  (a.inforbip有效号码个数+ifnull(b.twilio有效号码个数,0)) as 紧急联系人有效号码个数\n" +
            "from\n" +
            "(select date(createTime) as date , orderNo, count(distinct tellNumber) as inforbip有效号码个数 from\n" +
            "doit.teleCallResult\n" +
            "where disabled = 0 and callType = 3 and errorId in (5000,5002,5003,5603)\n" +
            "group by 1,2\n" +
            "order by 1 desc) a\n" +
            "left join\n" +
            "(select date(createTime) as date, orderNo, count(distinct phoneNumber) as twilio有效号码个数 from\n" +
            "doit.twilioCallResult\n" +
            "where disabled = 0 and callPhase = '-' and  callResult in ('completed','no-answer','busy')\n" +
            "group by 1,2\n" +
            "order by 1 desc) b on a.orderNo = b.orderNo and a.date = b.date\n" +
            "where a.orderNo in (select distinct uuid from doit.ordOrder where disabled = 0 and borrowingCount > 1)) c\n" +
            "group by 1 desc\n" +
            "limit 10;")
    List<RiskCall7> getRiskCall7();

    // 新户紧急联系人外呼
    @Select("select c.date as calldate,#外呼日期\n" +
            "      count(1) as newEmerCallNum,#新户紧急联系人外呼订单数\n" +
            "      count(1) as oldEmerCallNum, #老户联系人外呼订单数\n" +
            "      sum(if(c.紧急联系人有效号码个数=4,1,0)) as effect_4Num,#有效4订单数\n" +
            "      sum(if(c.紧急联系人有效号码个数=3,1,0)) as effect_3Num,#有效3订单数\n" +
            "      sum(if(c.紧急联系人有效号码个数=2,1,0)) as effect_2Num,#有效2订单数 \n" +
            "      sum(if(c.紧急联系人有效号码个数=1,1,0)) as effect_1Num,#有效1订单数 \n" +
            "      sum(if(c.紧急联系人有效号码个数=0,1,0)) as effect_0Num,#有效0订单数 \n" +
            "      concat(format(sum(if(c.紧急联系人有效号码个数 = 4,1,0))/count(1)*100,1) ,'%') as effect_4rate,# 有效号码_4的占比\n" +
            "      concat(format(sum(if(c.紧急联系人有效号码个数 = 3,1,0))/count(1)*100,1) ,'%') as effect_3rate,# 有效号码_3的占比\n" +
            "      concat(format(sum(if(c.紧急联系人有效号码个数 = 2,1,0))/count(1)*100,1) ,'%') as effect_2rate,# 有效号码_2的占比\n" +
            "      concat(format(sum(if(c.紧急联系人有效号码个数 = 1,1,0))/count(1)*100,1) ,'%') as effect_1rate,# 有效号码_1的占比\n" +
            "      concat(format(sum(if(c.紧急联系人有效号码个数 = 0,1,0))/count(1)*100,1) ,'%') as effect_0rate,# 有效号码_0的占比\n" +
            "      concat(format(sum(if(c.紧急联系人有效号码个数>= 2,1,0))/count(1)*100,1) ,'%') as  oldEmerCallPassRate #新户联系人外呼通过率\n" +
            "from\n" +
            "(select a.date,a.orderNo,a.inforbip有效号码个数,\n" +
            "  b.twilio有效号码个数,\n" +
            "  (a.inforbip有效号码个数+ifnull(b.twilio有效号码个数,0)) as 紧急联系人有效号码个数\n" +
            "from\n" +
            "(select date(createTime) as date , orderNo, count(distinct tellNumber) as inforbip有效号码个数 from\n" +
            "doit.teleCallResult\n" +
            "where disabled = 0 and callType = 3 and errorId in (5000,5002,5003,5603)\n" +
            "group by 1,2\n" +
            "order by 1 desc) a\n" +
            "left join\n" +
            "(select  date(createTime) as date , orderNo, count(distinct phoneNumber) as twilio有效号码个数 from\n" +
            "doit.twilioCallResult\n" +
            "where disabled = 0 and callPhase = '-' and  callResult in ('completed','no-answer','busy')\n" +
            "group by 1,2\n" +
            "order by 1 desc) b on a.orderNo = b.orderNo and a.date = b.date\n" +
            "where a.orderNo in (select distinct uuid from doit.ordOrder where disabled = 0 and borrowingCount = 1)) c\n" +
            "group by 1 desc\n" +
            "limit 10;")
    List<RiskCall8> getRiskCall8();

    // inforbip 每日紧急联系人外呼结果（按号码）
    @Select("select date(createTime)as calldate, #外呼日期\n" +
            "       count(1) as inforbipEmerCallNum, #inforbip紧急联系人外呼号码个数\n" +
            "       concat(format(sum(if(callResultType = 1,1,0))/count(1)*100,2),'%') as EffectRate, #完全有效占比\n" +
            "       concat(format(sum(if(callResultType = 2,1,0))/count(1)*100,2),'%')as MaybeEffectRate, #可能有效占比\n" +
            "       concat(format(sum(if(callResultType = 3,1,0))/count(1)*100,2),'%') as InvalidRate #无效占比\n" +
            "from doit.teleCallResult\n" +
            "where disabled = 0 and callType = 3 and callState = 2\n" +
            "group by 1 desc\n" +
            "limit 10; ")
    List<RiskCall9> getRiskCall9();

    // twilio 每日紧急联系人外呼结果（按号码）
    @Select("select date(createTime)as calldate,#外呼日期\n" +
            "       count(1)as twilioEmerCallNum,# twilio紧急联系人外呼号码个数\n" +
            "       concat(format(sum(if(callResultType=1,1,0))/count(1)*100,2),'%')as TotalEffectRate,#完全有效占比\n" +
            "\t   concat(format(sum(if(callResult= 'busy',1,0))/count(1)*100,2),'%') as maybeEffect_busyRate,#可能有效_busy占比\n" +
            "       concat(format(sum(if(callResult= 'no-answer',1,0))/count(1)*100,2),'%') as maybeEffect_NoAnswerRate,#可能有效_NoAnswer占比\n" +
            "       concat(format(sum(if(callResultType in (1,2),1,0))/count(1)*100,2),'%') as effectRate,#判定为有效号码的比例\n" +
            "       concat(format(sum(if(callResultType=3,1,0))/count(1)*100,2),'%') as InvalidRate #无效占比\n" +
            "from doit.twilioCallResult\n" +
            "where disabled = 0  and callState = 2 and callPhase = '-'\n" +
            "group by 1 desc\n" +
            "limit 10;")
    List<RiskCall10> getRiskCall10();

    // 公司外呼返回结果
    @Select("select date(createTime) as callDate,#外呼日期\n" +
            "       count(1) as companyCallNum,#公司外呼总数\n" +
            "       sum(if(callResultType = 1,1,0)) as TotalEffect,#完全有效\n" +
            "\t   sum(if(callResultType = 2,1,0)) as maybeEffect,#可能有效\n" +
            "       sum(if(callResultType = 3,1,0)) as Invalid,#无效\n" +
            "       concat(format(sum(if(callResultType = 1,1,0))/count(1)*100,1),'%') as totalEffectRate,#完全有效占比\n" +
            "\t   concat(format(sum(if(callResultType = 2,1,0))/count(1)*100,1),'%') as maybeEffectRate,#可能有效占比\n" +
            "       concat(format(sum(if(callResultType = 3,1,0))/count(1)*100,1),'%') as invalidRate #无效\n" +
            "from doit.teleCallResult\n" +
            "where disabled = 0 and callType = 2 \n" +
            "and orderNo in (select distinct uuid from doit.ordOrder where disabled = 0 and borrowingCount = 1)\n" +
            "group by 1 desc\n" +
            "limit 10;")
    List<RiskCall11> getRiskCall11();

    @Update("update ordBlack set remark=#{remark} where orderNo=#{orderNo} and disabled=0")
    Integer updateBlackInfo(@Param("orderNo") String orderNo,@Param("remark") String remark);

    @Update("update usrLinkManInfo u set u.disabled=1 ,u.remark='cashcash联系人出错-2018-12-18' where userUuid = #{userUuid}")
    Integer updateUsrLinkmanInfo(@Param("userUuid") String userUuid);


    //  150产品机审拒绝原因
    @Select("select \n" +
            "x.拒绝日期 as refuseDate,\n" +
            "x.拒绝原因 as refuseReason,\n" +
            "x.拒绝订单数 as refuseNum,\n" +
            "y.进入150 as getIntoProduct150,\n" +
            "concat(format(x.拒绝订单数/y.进入150*100,1),'%') as refuseRateProduct150 #在进入150订单中的命中率\n" +
            "from\n" +
            "(select date(b.createTime) as 拒绝日期, \n" +
            "b.responseMessage as 拒绝原因, count(distinct b.orderNo) as 拒绝订单数\n" +
            "from doit.ordBlack b \n" +
            "join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            "where  \n" +
            "d.disabled = 0 and d.borrowingCount = 1 and\n" +
            "b.remark in ('PRODUCT150TO80')\n" +
            "and  b.createTime > '2019-01-07 18:30:00'\n" +
            "and date(now()) - date(applyTime) in (0,1)\n" +
            "group by 1,2\n" +
            "order by 1 desc ,3 desc) x\n" +
            "join\n" +
            "(select a.提交日期 , a.600到150, b.600复审拒绝到150, (a.600到150 + ifnull(b.600复审拒绝到150,0)) as 进入150\n" +
            "from \n" +
            "(select date(d.applyTime) as 提交日期, count(distinct b.orderNo) as 600到150\n" +
            "from doit.ordBlack b\n" +
            "join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and\n" +
            "b.remark = 'PRODUCT600TO150' \n" +
            "and  date(now()) - date(applyTime) in (0,1)\n" +
            "group by 1\n" +
            "order by 1 desc) a\n" +
            "left join\n" +
            "(select date(d.applyTime) as 提交日期, count(distinct b.orderNo) as 600复审拒绝到150\n" +
            "from doit.ordBlack b\n" +
            "join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and \n" +
            "b.remark = 'PRODUCT600TO150_SENIOR_REVIEW' \n" +
            "and date(now()) - date(applyTime) in (0,1)\n" +
            "group by 1\n" +
            "order by 1 desc) b on a.提交日期 = b.提交日期) y on x.拒绝日期 = y.提交日期\n" +
            "order by 1 desc , 3 desc;")
    List<RefusedReasonWith150Product> getRefusedReasonWith150Product();

    //  50产品机审拒绝原因
    @Select("select \n" +
            "x.拒绝日期 as refuseDate,\n" +
            "x.拒绝原因 as refuseReason,\n" +
            "x.拒绝订单数 as refuseNum,\n" +
            "y.进入80 as getIntoProduct80,\n" +
            "concat(format(x.拒绝订单数/y.进入80*100,1),'%') as refuseRateProduct80 #在进入80订单中的命中率\n" +
            "from\n" +
            "(select date(b.createTime) as 拒绝日期, \n" +
            "b.responseMessage as 拒绝原因, count(distinct b.orderNo) as 拒绝订单数\n" +
            "from doit.ordBlack b\n" +
            "join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            "where  \n" +
            "d.disabled = 0 and d.borrowingCount = 1 and b.disabled = 0 and d.status = 12 and \n" +
            "d.amountApply = 160000 and date(now()) - date(applyTime) in (0,1)\n" +
            "group by 1,2\n" +
            "order by 1 desc) x\n" +
            "join\n" +
            "(select a.提交日期 , a.150到80, b.600到80, (a.150到80 + ifnull(b.600到80,0)) as 进入80\n" +
            "from \n" +
            "(select date(d.applyTime) as 提交日期, count(distinct b.orderNo) as 150到80 \n" +
            "from doit.ordBlack b\n" +
            "join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and \n" +
            "b.remark = 'PRODUCT150TO80' \n" +
            "and date(now()) - date(applyTime) in (0,1)\n" +
            "group by 1 desc) a\n" +
            "left join\n" +
            "(select date(d.applyTime) as 提交日期, count(distinct b.orderNo) as 600到80\n" +
            "from doit.ordBlack b\n" +
            "join doit.ordOrder d on b.userUuid = d.userUuid and b.orderNo = d.uuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and\n" +
            "b.remark = 'PRODUCT600TO80_FRAUD' \n" +
            "and date(now()) - date(applyTime) in (0,1)\n" +
            "group by 1 desc) b on a.提交日期 = b.提交日期\n" +
            ") y on x.拒绝日期 = y.提交日期\n" +
            "order by 1 desc,3 desc;")
    List<RefusedReasonWith80Product> getRefusedReasonWith80Product();

    //  每日放款笔数_新户
    @Select("select \n" +
            "y.放款日期 as lendDate,\n" +
            "y.新户合计 as NewTotalLendNum,\n" +
            "y.新户600 as new600,\n" +
            "y.新户150 as new150,\n" +
            "y.新户80 as new80,\n" +
            "concat(format(y.新户600/y.新户合计*100,1),'%') as proportion1,#600占比\n" +
            "concat(format(y.新户150/y.新户合计*100,1),'%') as proportion2,#150占比 \n" +
            "concat(format(y.新户80/y.新户合计*100,1),'%') as proportion3 #80占比\n" +
            "from \n" +
            "(select date(lendingTime) as 放款日期,\n" +
            "count(distinct uuid) as 新户合计,\n" +
            "sum(if(amountApply = 1200000,1,0)) as 新户600,\n" +
            "sum(if(amountApply = 300000,1,0)) as 新户150,\n" +
            "sum(if(amountApply = 160000,1,0)) as 新户80\n" +
            "from doit.ordOrder\n" +
            "where disabled = 0 and borrowingCount = 1 and orderType in (0\n" +
            ",2)\n" +
            "and status in (7,8,9,10,11)\n" +
            "group by 1 desc) y\n" +
            "limit 15;")
    List<LoanCountWithNewUser> getLoanCountWithNewUser();

    //  每日放款笔数_老户
    @Select("select \n" +
            "x.放款时间 as lendDate,\n" +
            "x.老户合计 OldTotalLendNum,\n" +
            "x.老户80 as Old80,\n" +
            "x.老户200 as Old200,\n" +
            "x.老户400 as Old400,\n" +
            "x.老户600 as Old600,\n" +
            "x.老户750 as Old750,\n" +
            "x.老户1000 as Old1000,\n" +
            "x.老户其它金额 as OtherAmout,\n" +
            "concat(format(x.老户80/x.老户合计*100,1),'%') as proportion80,#80占比\n" +
            "concat(format(x.老户200/x.老户合计*100,1),'%') as proportion200,#200占比\n" +
            "concat(format(x.老户400/x.老户合计*100,1),'%') as proportion400,#400占比\n" +
            "concat(format(x.老户600/x.老户合计*100,1),'%') as proportion600, #600占比\n" +
            "concat(format(x.老户750/x.老户合计*100,1),'%') as proportion750,#750占比\n" +
            "concat(format(x.老户1000/x.老户合计*100,1),'%') as proportion1000, #1000占比\n" +
            "concat(format(x.老户其它金额/x.老户合计*100,1),'%') as OtherAmountproportion#其他金额占比\n" +
            "from\n" +
            "(select date(lendingTime) as 放款时间, \n" +
            "count(distinct uuid) as 老户合计,  \n" +
            "sum(if(amountApply = 160000,1,0)) as 老户80,     \n" +
            "sum(if(amountApply = 400000,1,0)) as 老户200,\n" +
            "sum(if(amountApply = 800000,1,0)) as 老户400,\n" +
            "sum(if(amountApply = 1200000,1,0)) as 老户600,\n" +
            "sum(if(amountApply = 1500000,1,0)) as 老户750,\n" +
            "sum(if(amountApply = 2000000,1,0)) as 老户1000,\n" +
            "sum(if(amountApply not in (160000,400000,800000,1200000,1500000,2000000),1,0)) as 老户其它金额\n" +
            "from doit.ordOrder \n" +
            "where disabled = 0 and borrowingCount >1 and orderType in (0,2)\n" +
            "and status in (7,8,9,10,11)\n" +
            "group by 1 desc) x\n" +
            "limit 15;")
    List<LoanCountWithOldUser> getLoanCountWithOldUser();

    //  每日放款金额_新户 （单位：万/RMB）
    @Select("select \n" +
            "x.放款日期 as LendDate,\n" +
            "format(x.新户合计/20000000,2) as  NewTotalAmount ,#新户合计金额\n" +
            "format(x.600金额/20000000,1) as New600, #新户600\n" +
            "format(x.150金额/20000000,1) as New150, #新户150\n" +
            "format(x.80金额/20000000,1) as New80, #新户80\n" +
            "concat(format(x.600金额/x.新户合计*100,1),'%') as proportion600, #600占比\n" +
            "concat(format(x.150金额/x.新户合计*100,1),'%') as proportion150, #150占比\n" +
            "concat(format(x.80金额/x.新户合计*100,1),'%') as proportion80 #50占比\n" +
            "from\n" +
            "(select date(lendingTime) as 放款日期,\n" +
            "sum(amountApply) as 新户合计,\n" +
            "sum(if(amountApply = 1200000,amountApply,0)) as 600金额,\n" +
            "sum(if(amountApply = 300000,amountApply,0)) as 150金额,\n" +
            "sum(if(amountApply = 160000,amountApply,0)) as 80金额\n" +
            "from doit.ordOrder \n" +
            "where disabled = 0 and borrowingCount = 1 and orderType in (0,2)\n" +
            "and status in (7,8,9,10,11)\n" +
            "group by 1\n" +
            "order by 1 desc) x\n" +
            "limit 15;")
    List<LoanAmoutWithNewUser> getLoanAmoutWithNewUser();

    //  每日放款金额_老户 （单位：万/RMB）
    @Select("select \n" +
            "x.放款日期 as LendDate,\n" +
            "format(x.老户合计/20000000,2) as  OldTotalAmount ,#老户合计金额\n" +
            "format(x.80金额/20000000,1) as Old80, #老户200\n" +
            "format(x.200金额/20000000,1) as Old200, #老户200\n" +
            "format(x.400金额/20000000,1) as Old400, #老户400\n" +
            "format(x.600金额/20000000,1) as Old600, #新户600\n" +
            "format(x.750金额/20000000,1) as Old750, #新户750\n" +
            "format(x.1000金额/20000000,1) as Old1000, #新户1000\n" +
            "format(x.其它金额/20000000,1) as otherAmount, #老户其它金额\n" +
            "concat(format(x.80金额/x.老户合计*100,1),'%') as proportion80, #80占比\n" +
            "concat(format(x.200金额/x.老户合计*100,1),'%') as proportion200, #200占比\n" +
            "concat(format(x.400金额/x.老户合计*100,1),'%') as proportion400, #400占比\n" +
            "concat(format(x.600金额/x.老户合计*100,1),'%') as proportion600, #600占比\n" +
            "concat(format(x.750金额/x.老户合计*100,1),'%') as proportion750, #750占比\n" +
            "concat(format(x.1000金额/x.老户合计*100,1),'%') as proportion1000, #1000占比\n" +
            "concat(format(x.其它金额/x.老户合计*100,1),'%') as otherAmountProportion #其他金额占比\n" +
            "from\n" +
            "(select date(lendingTime) as 放款日期,\n" +
            "sum(amountApply) as 老户合计,\n" +
            "sum(if(amountApply = 160000,amountApply,0)) as 80金额,\n" +
            "sum(if(amountApply = 400000,amountApply,0)) as 200金额,\n" +
            "sum(if(amountApply = 800000,amountApply,0)) as 400金额,\n" +
            "sum(if(amountApply = 1200000,amountApply,0)) as 600金额,\n" +
            "sum(if(amountApply = 1500000,amountApply,0)) as 750金额,\n" +
            "sum(if(amountApply = 2000000,amountApply,0)) as 1000金额,\n" +
            "sum(if(amountApply not in (160000,400000,800000,1200000,1500000,2000000),amountApply,0)) as 其它金额\n" +
            "from doit.ordOrder \n" +
            "where disabled = 0 and borrowingCount >1 and orderType in (0,2)\n" +
            "and status in (7,8,9,10,11)\n" +
            "group by 1\n" +
            "order by 1 desc) x\n" +
            "limit 15;")
    List<LoanAmoutWithOldUser> getLoanAmoutWithOldUser();

    //  各环节当天申请订单的当前通过笔数（非cashcash_新户600产品）
    @Select("select \n" +
            "a.申请日 as applyDate,\n" +
            "a.申请订单 as applyNum,\n" +
            "b.提交订单 as commmitNum,\n" +
            "e.进入初审 + c.免审核风控通过 + ifnull(s.免审核多头黑名单拒绝,0) as autoCheckPass,#机审通过\n" +
            "e.进入初审 as intoFC,\n" +
            "i.初审完成 as FCfinish,\n" +
            "l.初审通过本人外呼 as FCPassOutCall,\n" +
            "f.进入复审 as intoSC,\n" +
            "j.复审完成 as SCfinish,\n" +
            "k.复审通过风控通过 + ifnull(r.复审通过多头黑名单拒绝,0) as SCpass, # 复审通过\n" +
            "g.风控and黑名单通过+ ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0) as riskPass ,#风控通过\n" +
            "ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0) as BlackListMultiHeadNum, #多头黑名单拒绝,\n" +
            "g.风控and黑名单通过 as riskBlacklistPassNum,\n" +
            "x.待放款 as lendNum,\n" +
            "h.放款成功 as LendSuccessNum\n" +
            "from\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 申请订单\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 1 and d.borrowingCount = 1 \n" +
            "and d.disabled = 0  and d.thirdType != 1\n" +
            "group by 1 desc) a\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 提交订单\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 2 and d.borrowingCount = 1\n" +
            "and d.disabled = 0  and d.thirdType != 1\n" +
            "group by 1 desc) b on a.申请日=b.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 免审核风控通过\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 1200000 and orderType in (0,2)  \n" +
            "and h.disabled = 0 and orderType != 1\n" +
            "and h.status in (5,20)  and d.thirdType != 1\n" +
            "and h.orderId not in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 3)\n" +
            "and d.applyTime > '19-01-07 18:30:00' \n" +
            "group by 1 desc) c on a.申请日 = c.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 进入初审\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and h.status = 3\n" +
            "and d.applyTime > '19-01-07 18:30:00' and d.thirdType != 1\n" +
            "group by 1 desc) e on a.申请日 = e.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 初审通过本人外呼\n" +
            " from doit.ordOrder d\n" +
            " join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 \n" +
            " and h.disabled = 0 \n" +
            "and h.status = 18 and d.applyTime > '19-01-07 18:30:00'  and d.thirdType != 1\n" +
            " group by 1 desc) l on a.申请日 = l.申请日\n" +
            " left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 进入复审\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and h.status = 4 \n" +
            "and d.applyTime > '19-01-07 18:30:00'  and d.thirdType != 1\n" +
            "group by 1 desc) f on a.申请日 = f.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审通过风控通过\n" +
            "from doit.ordOrder d \n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and d.applyTime > '19-01-07 18:30:00' and h.status = 4 \n" +
            "and h.orderId in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'SENIOR_REVIEWER')\n" +
            "and orderId in (select distinct orderId from doit.ordHistory where disabled = 0 and status in (5,20)) \n" +
            "and d.amountApply =1200000 and d.orderType != 1  and d.thirdType != 1 \n" +
            "group by 1 desc)k on a.申请日 = k.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct h.orderId) as 风控and黑名单通过\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' \n" +
            "and h.status in (5,20) and d.borrowingCount = 1 and d.amountApply = 1200000 and orderType != 1\n" +
            "and d.disabled = 0  and d.thirdType != 1\n" +
            "group by 1 desc\n" +
            ") g on a.申请日 = g.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 待放款\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 5 and d.borrowingCount = 1 and d.amountApply = 1200000 \n" +
            "and d.disabled = 0  and orderType != 1  and d.thirdType != 1\n" +
            "group by 1 desc) x on a.申请日 = x.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 放款成功\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 7 and d.borrowingCount = 1 and d.amountApply = 1200000 \n" +
            "and d.disabled = 0 and orderType != 1  and d.thirdType != 1\n" +
            "group by 1 desc) h on a.申请日 = h.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 初审完成 \n" +
            "from doit.ordOrder d\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and d.applyTime > '19-01-07 18:30:00' \n" +
            " and d.thirdType != 1 \n" +
            "and d.uuid in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'JUNIOR_REVIEWER')\n" +
            "group by 1 desc) i on a.申请日 = i.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审完成 \n" +
            "from doit.ordOrder d\n" +
            "where d.disabled = 0 and d.borrowingCount = 1  and d.applyTime > '19-01-07 18:30:00' and d.thirdType != 1\n" +
            "and d.uuid in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'SENIOR_REVIEWER')\n" +
            "group by 1 desc) j on a.申请日 = j.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审通过多头黑名单拒绝 \n" +
            "from doit.ordOrder d \n" +
            "join doit.ordBlack b on d.userUuid = b.userUuid and d.uuid = b.orderNo\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and b.responseMessage in ('命中advance黑名单','advance近7天内多头借贷异常')\n" +
            "and d.uuid in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 4)\n" +
            "and d.thirdType !=1\n" +
            "group by 1 desc) r on a.申请日 = r.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 免审核多头黑名单拒绝 \n" +
            "from doit.ordOrder d \n" +
            "join doit.ordBlack b on d.userUuid = b.userUuid and d.uuid = b.orderNo\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and b.responseMessage in ('命中advance黑名单','advance近7天内多头借贷异常')\n" +
            "and d.uuid not in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 4)\n" +
            "and d.thirdType != 1\n" +
            "group by 1 desc) s on a.申请日 = s.申请日\n" +
            "limit 15;")
    List<OrdPassCountWithStage> getOrdPassCountWithStage();

    //  各环节当天申请订单的当前通过率（非cashcash_新户600产品）
    @Select("select \n" +
            "a.申请日 as applyDate,\n" +
            "concat(format(b.提交订单/a.申请订单*100,1),'%') as commitRate,#600申请提交率\n" +
            "concat(format((e.进入初审 + c.免审核风控通过 + ifnull(s.免审核多头黑名单拒绝,0))/b.提交订单*100,1),'%') as autoCheckPassRate,#机审通过率\n" +
            "concat(format(l.初审通过本人外呼/i.初审完成*100,1),'%') as manFCPassRate, #人工初审通过率\n" +
            "concat(format(f.进入复审/l.初审通过本人外呼*100,1),'%') as  selfCallPassRate,#本人外呼通过率\n" +
            "concat(format((k.复审通过风控通过+ ifnull(r.复审通过多头黑名单拒绝,0))/j.复审完成*100,1),'%') as SCPassRate, #复审通过率\n" +
            "concat(format((g.风控and黑名单通过+ ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0))/b.提交订单*100,1),'%') as riskPassRate,#风控通过率\n" +
            "concat(format(g.风控and黑名单通过 /b.提交订单*100,1),'%') as riskandBlackListPassRate, #风控and黑名单通过率\n" +
            "concat(format((c.免审核风控通过+ifnull(s.免审核多头黑名单拒绝,0))/(e.进入初审 + c.免审核风控通过 + ifnull(s.免审核多头黑名单拒绝,0))*100,1),'%') as noMCRateInACpass,#免审核占机审通过比例\n" +
            "concat(format((c.免审核风控通过+ifnull(s.免审核多头黑名单拒绝,0))/b.提交订单*100,1),'%') as noMCrateInCommit, #免审核占提交比例\n" +
            "concat(format((ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0))/(g.风控and黑名单通过+ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0))*100,1),'%') as BlackListMuliHeadRate,#多头黑名单拒绝率\n" +
            "concat(format(h.放款成功 /x.待放款*100,1),'%') as lendSuccessRate #放款成功率\n" +
            "from\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 申请订单\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 1 and d.borrowingCount = 1 \n" +
            "and d.disabled = 0  and d.thirdType != 1\n" +
            "group by 1 desc) a\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 提交订单\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 2 and d.borrowingCount = 1\n" +
            "and d.disabled = 0  and d.thirdType != 1\n" +
            "group by 1 desc) b on a.申请日=b.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 免审核风控通过\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 1200000 and orderType in (0,2)  \n" +
            "and h.disabled = 0 and orderType != 1\n" +
            "and h.status in (5,20)  and d.thirdType != 1\n" +
            "and h.orderId not in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 3)\n" +
            "and d.applyTime > '19-01-07 18:30:00' \n" +
            "group by 1 desc) c on a.申请日 = c.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 进入初审\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and h.status = 3\n" +
            "and d.applyTime > '19-01-07 18:30:00' and d.thirdType != 1\n" +
            "group by 1 desc) e on a.申请日 = e.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 初审通过本人外呼\n" +
            " from doit.ordOrder d\n" +
            " join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 \n" +
            " and h.disabled = 0 \n" +
            "and h.status = 18 and d.applyTime > '19-01-07 18:30:00'  and d.thirdType != 1\n" +
            " group by 1 desc) l on a.申请日 = l.申请日\n" +
            " left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 进入复审\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and h.status = 4 \n" +
            "and d.applyTime > '19-01-07 18:30:00'  and d.thirdType != 1\n" +
            "group by 1 desc) f on a.申请日 = f.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审通过风控通过\n" +
            "from doit.ordOrder d \n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and d.applyTime > '19-01-07 18:30:00' and h.status = 4 \n" +
            "and h.orderId in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'SENIOR_REVIEWER')\n" +
            "and orderId in (select distinct orderId from doit.ordHistory where disabled = 0 and status in (5,20)) \n" +
            "and d.amountApply =1200000 and d.orderType != 1  and d.thirdType != 1 \n" +
            "group by 1 desc)k on a.申请日 = k.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct h.orderId) as 风控and黑名单通过\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' \n" +
            "and h.status in (5,20) and d.borrowingCount = 1 and d.amountApply = 1200000 and orderType != 1\n" +
            "and d.disabled = 0  and d.thirdType != 1\n" +
            "group by 1 desc\n" +
            ") g on a.申请日 = g.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 待放款\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 5 and d.borrowingCount = 1 and d.amountApply = 1200000 \n" +
            "and d.disabled = 0  and orderType != 1  and d.thirdType != 1\n" +
            "group by 1 desc) x on a.申请日 = x.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 放款成功\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 7 and d.borrowingCount = 1 and d.amountApply = 1200000 \n" +
            "and d.disabled = 0 and orderType != 1  and d.thirdType != 1\n" +
            "group by 1 desc) h on a.申请日 = h.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 初审完成 \n" +
            "from doit.ordOrder d\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and d.applyTime > '19-01-07 18:30:00' \n" +
            " and d.thirdType != 1 \n" +
            "and d.uuid in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'JUNIOR_REVIEWER')\n" +
            "group by 1 desc) i on a.申请日 = i.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审完成 \n" +
            "from doit.ordOrder d\n" +
            "where d.disabled = 0 and d.borrowingCount = 1  and d.applyTime > '19-01-07 18:30:00' and d.thirdType != 1\n" +
            "and d.uuid in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'SENIOR_REVIEWER')\n" +
            "group by 1 desc) j on a.申请日 = j.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审通过多头黑名单拒绝 \n" +
            "from doit.ordOrder d \n" +
            "join doit.ordBlack b on d.userUuid = b.userUuid and d.uuid = b.orderNo\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and b.responseMessage in ('命中advance黑名单','advance近7天内多头借贷异常')\n" +
            "and d.uuid in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 4)\n" +
            "and d.thirdType !=1\n" +
            "group by 1 desc) r on a.申请日 = r.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 免审核多头黑名单拒绝 \n" +
            "from doit.ordOrder d \n" +
            "join doit.ordBlack b on d.userUuid = b.userUuid and d.uuid = b.orderNo\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and b.responseMessage in ('命中advance黑名单','advance近7天内多头借贷异常')\n" +
            "and d.uuid not in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 4)\n" +
            "and d.thirdType != 1\n" +
            "group by 1 desc) s on a.申请日 = s.申请日\n" +
            "limit 15;")
    List<OrdPassRateWithStage> getOrdPassRateWithStage();


    //  各环节当天申请订单的当前通过笔数（cashcash_新户600产品）
    @Select("select \n" +
            "a.申请日 as applyDate,\n" +
            "a.申请订单 as applyNum,\n" +
            "b.提交订单 as commmitNum,\n" +
            "e.进入初审 + c.免审核风控通过 + ifnull(s.免审核多头黑名单拒绝,0) as autoCheckPass,#机审通过\n" +
            "e.进入初审 as intoFC,\n" +
            "i.初审完成 as FCfinish,\n" +
            "l.初审通过本人外呼 as FCPassOutCall,\n" +
            "f.进入复审 as intoSC,\n" +
            "j.复审完成 as SCfinish,\n" +
            "k.复审通过风控通过 + ifnull(r.复审通过多头黑名单拒绝,0) as SCpass, # 复审通过\n" +
            "g.风控and黑名单通过+ ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0) as riskPass ,#风控通过\n" +
            "ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0) as BlackListMultiHeadNum, #多头黑名单拒绝,\n" +
            "g.风控and黑名单通过 as riskBlacklistPassNum,\n" +
            "x.待放款 as lendNum,\n" +
            "h.放款成功 as LendSuccessNum\n" +
            "from\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 申请订单\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 1 and d.borrowingCount = 1 \n" +
            "and d.disabled = 0  and d.thirdType = 1\n" +
            "group by 1 desc) a\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 提交订单\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 2 and d.borrowingCount = 1\n" +
            "and d.disabled = 0  and d.thirdType = 1\n" +
            "group by 1 desc) b on a.申请日=b.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 免审核风控通过\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 1200000 and orderType in (0,2)  \n" +
            "and h.disabled = 0 and orderType != 1\n" +
            "and h.status in (5,20)  and d.thirdType = 1\n" +
            "and h.orderId not in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 3)\n" +
            "and d.applyTime > '19-01-07 18:30:00' \n" +
            "group by 1 desc) c on a.申请日 = c.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 进入初审\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and h.status = 3\n" +
            "and d.applyTime > '19-01-07 18:30:00' and d.thirdType = 1\n" +
            "group by 1 desc) e on a.申请日 = e.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 初审通过本人外呼\n" +
            " from doit.ordOrder d\n" +
            " join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 \n" +
            " and h.disabled = 0 \n" +
            "and h.status = 18 and d.applyTime > '19-01-07 18:30:00'  and d.thirdType = 1\n" +
            " group by 1 desc) l on a.申请日 = l.申请日\n" +
            " left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 进入复审\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and h.status = 4 \n" +
            "and d.applyTime > '19-01-07 18:30:00'  and d.thirdType = 1\n" +
            "group by 1 desc) f on a.申请日 = f.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审通过风控通过\n" +
            "from doit.ordOrder d \n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and d.applyTime > '19-01-07 18:30:00' and h.status = 4 \n" +
            "and h.orderId in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'SENIOR_REVIEWER')\n" +
            "and orderId in (select distinct orderId from doit.ordHistory where disabled = 0 and status in (5,20)) \n" +
            "and d.amountApply =1200000 and d.orderType != 1  and  d.thirdType = 1 \n" +
            "group by 1 desc)k on a.申请日 = k.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct h.orderId) as 风控and黑名单通过\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' \n" +
            "and h.status in (5,20) and d.borrowingCount = 1 and d.amountApply = 1200000 and orderType != 1\n" +
            "and d.disabled = 0  and d.thirdType = 1\n" +
            "group by 1 desc\n" +
            ") g on a.申请日 = g.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 待放款\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 5 and d.borrowingCount = 1 and d.amountApply = 1200000 \n" +
            "and d.disabled = 0  and orderType != 1  and d.thirdType = 1\n" +
            "group by 1 desc) x on a.申请日 = x.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 放款成功\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 7 and d.borrowingCount = 1 and d.amountApply = 1200000 \n" +
            "and d.disabled = 0 and orderType != 1  and d.thirdType = 1\n" +
            "group by 1 desc) h on a.申请日 = h.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 初审完成 \n" +
            "from doit.ordOrder d\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and d.applyTime > '19-01-07 18:30:00' \n" +
            " and d.thirdType = 1 \n" +
            "and d.uuid in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'JUNIOR_REVIEWER')\n" +
            "group by 1 desc) i on a.申请日 = i.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审完成 \n" +
            "from doit.ordOrder d\n" +
            "where d.disabled = 0 and d.borrowingCount = 1  and d.applyTime > '19-01-07 18:30:00' and d.thirdType = 1\n" +
            "and d.uuid in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'SENIOR_REVIEWER')\n" +
            "group by 1 desc) j on a.申请日 = j.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审通过多头黑名单拒绝 \n" +
            "from doit.ordOrder d \n" +
            "join doit.ordBlack b on d.userUuid = b.userUuid and d.uuid = b.orderNo\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and b.responseMessage in ('命中advance黑名单','advance近7天内多头借贷异常')\n" +
            "and d.uuid in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 4)\n" +
            "and d.thirdType =1\n" +
            "group by 1 desc) r on a.申请日 = r.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 免审核多头黑名单拒绝 \n" +
            "from doit.ordOrder d \n" +
            "join doit.ordBlack b on d.userUuid = b.userUuid and d.uuid = b.orderNo\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and b.responseMessage in ('命中advance黑名单','advance近7天内多头借贷异常')\n" +
            "and d.uuid not in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 4)\n" +
            "and d.thirdType = 1\n" +
            "group by 1 desc) s on a.申请日 = s.申请日\n" +
            "limit 15;")
    List<OrdPassCountWithStage> getCash2OrdPassCountWithStage();

    //   各环节当天申请订单的当前通过率（cashcash_新户600产品）
    @Select("select \n" +
            "a.申请日 as applyDate,\n" +
            "concat(format(b.提交订单/a.申请订单*100,1),'%') as commitRate,#600申请提交率\n" +
            "concat(format((e.进入初审 + c.免审核风控通过 + ifnull(s.免审核多头黑名单拒绝,0))/b.提交订单*100,1),'%') as autoCheckPassRate,#机审通过率\n" +
            "concat(format(l.初审通过本人外呼/i.初审完成*100,1),'%') as manFCPassRate, #人工初审通过率\n" +
            "concat(format(f.进入复审/l.初审通过本人外呼*100,1),'%') as  selfCallPassRate,#本人外呼通过率\n" +
            "concat(format((k.复审通过风控通过+ ifnull(r.复审通过多头黑名单拒绝,0))/j.复审完成*100,1),'%') as SCPassRate, #复审通过率\n" +
            "concat(format((g.风控and黑名单通过+ ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0))/b.提交订单*100,1),'%') as riskPassRate,#风控通过率\n" +
            "concat(format(g.风控and黑名单通过 /b.提交订单*100,1),'%') as riskandBlackListPassRate, #风控and黑名单通过率\n" +
            "concat(format((c.免审核风控通过+ifnull(s.免审核多头黑名单拒绝,0))/(e.进入初审 + c.免审核风控通过 + ifnull(s.免审核多头黑名单拒绝,0))*100,1),'%') as noMCRateInACpass,#免审核占机审通过比例\n" +
            "concat(format((c.免审核风控通过+ifnull(s.免审核多头黑名单拒绝,0))/b.提交订单*100,1),'%') as noMCrateInCommit, #免审核占提交比例\n" +
            "concat(format((ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0))/(g.风控and黑名单通过+ifnull(r.复审通过多头黑名单拒绝,0)+ifnull(s.免审核多头黑名单拒绝,0))*100,1),'%') as BlackListMuliHeadRate,#多头黑名单拒绝率\n" +
            "concat(format(h.放款成功 /x.待放款*100,1),'%') as lendSuccessRate #放款成功率\n" +
            "from\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 申请订单\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 1 and d.borrowingCount = 1 \n" +
            "and d.disabled = 0  and d.thirdType = 1\n" +
            "group by 1 desc) a\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 提交订单\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 2 and d.borrowingCount = 1\n" +
            "and d.disabled = 0  and d.thirdType = 1\n" +
            "group by 1 desc) b on a.申请日=b.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 免审核风控通过\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and d.amountApply = 1200000 and orderType in (0,2)  \n" +
            "and h.disabled = 0 and orderType != 1\n" +
            "and h.status in (5,20)  and d.thirdType = 1\n" +
            "and h.orderId not in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 3)\n" +
            "and d.applyTime > '19-01-07 18:30:00' \n" +
            "group by 1 desc) c on a.申请日 = c.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 进入初审\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and h.status = 3\n" +
            "and d.applyTime > '19-01-07 18:30:00' and d.thirdType = 1\n" +
            "group by 1 desc) e on a.申请日 = e.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 初审通过本人外呼\n" +
            " from doit.ordOrder d\n" +
            " join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 \n" +
            " and h.disabled = 0 \n" +
            "and h.status = 18 and d.applyTime > '19-01-07 18:30:00'  and d.thirdType = 1\n" +
            " group by 1 desc) l on a.申请日 = l.申请日\n" +
            " left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 进入复审\n" +
            "from doit.ordOrder d\n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and h.status = 4 \n" +
            "and d.applyTime > '19-01-07 18:30:00'  and d.thirdType = 1\n" +
            "group by 1 desc) f on a.申请日 = f.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审通过风控通过\n" +
            "from doit.ordOrder d \n" +
            "join doit.ordHistory h on d.uuid = h.orderId and d.userUuid = h.userUuid\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and h.disabled = 0 \n" +
            "and d.applyTime > '19-01-07 18:30:00' and h.status = 4 \n" +
            "and h.orderId in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'SENIOR_REVIEWER')\n" +
            "and orderId in (select distinct orderId from doit.ordHistory where disabled = 0 and status in (5,20)) \n" +
            "and d.amountApply =1200000 and d.orderType != 1  and  d.thirdType = 1 \n" +
            "group by 1 desc)k on a.申请日 = k.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct h.orderId) as 风控and黑名单通过\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' \n" +
            "and h.status in (5,20) and d.borrowingCount = 1 and d.amountApply = 1200000 and orderType != 1\n" +
            "and d.disabled = 0  and d.thirdType = 1\n" +
            "group by 1 desc\n" +
            ") g on a.申请日 = g.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 待放款\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 5 and d.borrowingCount = 1 and d.amountApply = 1200000 \n" +
            "and d.disabled = 0  and orderType != 1  and d.thirdType = 1\n" +
            "group by 1 desc) x on a.申请日 = x.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct orderId) as 放款成功\n" +
            "FROM doit.ordHistory h\n" +
            "join doit.ordOrder d on h.userUuid = d.userUuid and h.orderId = d.uuid \n" +
            "WHERE h.disabled = 0 and d.applyTime > '2019-01-07 18:30:00' and h.status = 7 and d.borrowingCount = 1 and d.amountApply = 1200000 \n" +
            "and d.disabled = 0 and orderType != 1  and d.thirdType = 1\n" +
            "group by 1 desc) h on a.申请日 = h.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 初审完成 \n" +
            "from doit.ordOrder d\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and d.applyTime > '19-01-07 18:30:00' \n" +
            " and d.thirdType = 1 \n" +
            "and d.uuid in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'JUNIOR_REVIEWER')\n" +
            "group by 1 desc) i on a.申请日 = i.申请日\n" +
            "left join\n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审完成 \n" +
            "from doit.ordOrder d\n" +
            "where d.disabled = 0 and d.borrowingCount = 1  and d.applyTime > '19-01-07 18:30:00' and d.thirdType = 1\n" +
            "and d.uuid in (select distinct orderUUID from doit.reviewerOrderTask where status = 0 and finish = 1 and reviewerRole = 'SENIOR_REVIEWER')\n" +
            "group by 1 desc) j on a.申请日 = j.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 复审通过多头黑名单拒绝 \n" +
            "from doit.ordOrder d \n" +
            "join doit.ordBlack b on d.userUuid = b.userUuid and d.uuid = b.orderNo\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and b.responseMessage in ('命中advance黑名单','advance近7天内多头借贷异常')\n" +
            "and d.uuid in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 4)\n" +
            "and d.thirdType =1\n" +
            "group by 1 desc) r on a.申请日 = r.申请日\n" +
            "left join \n" +
            "(select date(d.createTime) as 申请日, count(distinct d.uuid) as 免审核多头黑名单拒绝 \n" +
            "from doit.ordOrder d \n" +
            "join doit.ordBlack b on d.userUuid = b.userUuid and d.uuid = b.orderNo\n" +
            "where d.disabled = 0 and d.borrowingCount = 1 and b.responseMessage in ('命中advance黑名单','advance近7天内多头借贷异常')\n" +
            "and d.uuid not in (select distinct orderId from doit.ordHistory where disabled = 0 and status = 4)\n" +
            "and d.thirdType = 1\n" +
            "group by 1 desc) s on a.申请日 = s.申请日\n" +
            "limit 15;\n")
    List<OrdPassRateWithStage> getCash2OrdPassRateWithStage();
}
