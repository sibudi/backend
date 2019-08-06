package com.yqg.management.dao;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.management.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Mapper
public interface CollectionOrderHistoryDao extends BaseMapper<CollectionOrderHistory> {

    // D0_D1-2_D3-7新增未还款订单处理情况
    @Select("select \n" +
            "stage ,        -- 阶段\n" +
            "now() as createDay,     -- 日期\n" +
            "parentName,    -- 组长\n" +
            "studentName,   -- 催收员\n" +
            "taskNum,       -- 新增未还款单数 \n" +
            "dealNum,       -- WA或电话处理过单数\n" +
            "taskNum - dealNum as notdeal  -- 未处理单数\n" +
            "from(\n" +
            "select \n" +
            "'D0' as stage,      \n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "t.taskNum,   \n" +
            "ifnull(t1.dealNum,0) as dealNum   \n" +
            "from(\n" +
            "select \n" +
            "outsourceid,\n" +
            "count(orderUUID) as taskNum\n" +
            "from(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in (\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where date(a.createtime) =CURDATE() \n" +
            "and sourceType = 0\n" +
            "and datediff(a.createtime,b.refundtime) = 0 \n" +
            "and b.status in (7)\n" +
            "group by orderUUID)\n" +
            ")COH \n" +
            " group by outsourceid\n" +
            ")t\n" +
            "LEFT JOIN(\n" +
            "select \n" +
            "createUser,\n" +
            "count(orderNo) as dealnum\n" +
            "from(\n" +
            "select \n" +
            "a.createUser,\n" +
            "orderNo\n" +
            "from manCollectionRemark  a inner join ordOrder b on a.orderNo = b.uuid\n" +
            "where a.disabled = 0\n" +
            "and datediff(a.createTime,b.refundTime) = 0\n" +
            "and b.status in (7)\n" +
            "and a.contactMode in (1,2,3,4)\n" +
            "and date(a.createTime) = CURDATE()\n" +
            "GROUP BY a.createUser,orderNo\n" +
            ")mcr\n" +
            " GROUP BY createUser\n" +
            ")t1 on t.outsourceid=t1.createUser\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid on t.outsourceid = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            ")result\n" +
            "GROUP BY stage ,parentName,studentName,taskNum,dealNum,Notdeal\n" +
            "\n" +
            "union ALL\n" +
            "\n" +
            "\n" +
            "select \n" +
            "stage ,\n" +
            "now() as createDay,\n" +
            "parentName,\n" +
            "studentName,\n" +
            "taskNum,\n" +
            "dealNum,\n" +
            "taskNum - dealNum as notdeal\n" +
            "from(\n" +
            "select \n" +
            "'D1-2' as stage,\n" +
            "t.createDay,      \n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "t.taskNum,    \n" +
            "ifnull(t1.dealNum,0) as dealNum   \n" +
            "from(\n" +
            "select \n" +
            "createDay,\n" +
            "outsourceid,\n" +
            "count(orderUUID) as taskNum\n" +
            "from(\n" +
            "select\n" +
            "orderUUID,\n" +
            "date(createtime) as createDay,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where sourceType = 0\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 1 and 2\n" +
            "and b.`status` in (8)\n" +
            "group by orderUUID)\n" +
            "and date(createtime) = CURDATE() \n" +
            ")COH group by createDay,outsourceid\n" +
            ")t\n" +
            "LEFT JOIN\n" +
            "(\n" +
            "select \n" +
            "createUser,\n" +
            "count(orderNo) as dealNum\n" +
            "from(\n" +
            "select \n" +
            "a.createUser,\n" +
            "orderNo\n" +
            "from manCollectionRemark  a inner join ordOrder b on a.orderNo = b.uuid\n" +
            "where a.disabled = 0\n" +
            "and a.contactMode in (1,2,3,4)\n" +
            "and datediff(a.createTime,b.refundTime) between 1 and 2\n" +
            "and date(a.createTime) = CURDATE()\n" +
            "and b.status in (8)\n" +
            "GROUP BY a.createUser,orderNo\n" +
            ")mcr \n" +
            "inner  join (\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0\n" +
            "and sourceType = 0\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 1 and 2\n" +
            "and b.status in (8)\n" +
            "group by orderUUID)\n" +
            "and date(createtime) = CURDATE() \n" +
            ")colorder on mcr.createUser = colorder.outsourceid  and mcr.orderNo = colorder.orderUUID\n" +
            "GROUP BY createUser\n" +
            ")t1 on t.outsourceid=t1.createUser\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid on t.outsourceid = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            ")result\n" +
            "GROUP BY stage ,createDay,parentName,studentName,taskNum,dealNum,Notdeal\n" +
            "\n" +
            "union all\n" +
            "\n" +
            "select \n" +
            "stage ,\n" +
            "now() as createDay,\n" +
            "parentName,\n" +
            "studentName,\n" +
            "taskNum,\n" +
            "dealNum,\n" +
            "taskNum - dealNum as notdeal\n" +
            "from(\n" +
            "select \n" +
            "'D3-7' as stage,    \n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "t.taskNum,    \n" +
            "ifnull(t1.dealNum,0) as dealNum   \n" +
            "from(\n" +
            "select \n" +
            "outsourceid,\n" +
            "count(orderUUID) as taskNum\n" +
            "from(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where sourceType = 0\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 3 and 7\n" +
            "and b.status in (8)\n" +
            "group by orderUUID)\n" +
            "and date(createtime) = CURDATE() \n" +
            ")COH\n" +
            " group by outsourceid\n" +
            ")t\n" +
            "LEFT JOIN\n" +
            "(\n" +
            "select \n" +
            "createUser,\n" +
            "count(orderNo) as dealNum\n" +
            "from(\n" +
            "select \n" +
            "a.createUser,\n" +
            "orderNo\n" +
            "from manCollectionRemark  a inner join ordOrder b on a.orderNo = b.uuid\n" +
            "where a.disabled = 0\n" +
            "and a.contactMode in (1,2,3,4)\n" +
            "and datediff(a.createTime,b.refundTime) between 3 and 7\n" +
            "and date(a.createTime) = CURDATE()\n" +
            "and b.status in (8)\n" +
            "GROUP BY a.createUser,orderNo\n" +
            ")mcr \n" +
            "inner  join (\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0\n" +
            "and sourceType = 0\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 3 and 7\n" +
            "and b.status in (8)\n" +
            "group by orderUUID)\n" +
            "and date(createtime) = CURDATE() \n" +
            ")colorder on mcr.createUser = colorder.outsourceid  and mcr.orderNo = colorder.orderUUID\n" +
            "GROUP BY createUser\n" +
            ")t1 on t.outsourceid=t1.createUser\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid on t.outsourceid = manid.id\n" +
            "inner join(\n" +
            "select id,realname\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            ")result\n" +
            "GROUP BY stage ,createDay,parentName,studentName,taskNum,dealNum,Notdeal;")
    List<D0ToD7OrderCollectionData> getD0ToD7OrderCollectionData();

    // D0_D1-2_D3-7当日承诺还款时间前后半小时未跟进(新增+存量)
    @Select("select \n" +
            "'D0' as stage,                   -- 阶段\n" +
            "NOW() as createDay ,             -- 打标签时间\n" +
            "manid2.realname as parentName,   -- 组长\n" +
            "manid.realname as studentName,   -- 催收员\n" +
            "count(orderNo) as notFollowed    -- 承诺还款未跟进\n" +
            "from(\n" +
            "select \n" +
            "createUser, \n" +
            "orderNo,\n" +
            "promiseRepaymentTime\n" +
            "from manCollectionRemark  \n" +
            "where disabled = 0\n" +
            "and orderTag in (4)\n" +
            "and timestampdiff(minute,promiseRepaymentTime,now())>= 30\n" +
            "and id in(\n" +
            "select max(a.id) from manCollectionRemark a inner join ordOrder b on a.orderNo = b.uuid \n" +
            "where a.disabled = 0\n" +
            "and datediff(a.createTime,b.refundTime) = 0 \n" +
            "and b.status = 7\n" +
            "and date(a.createtime) = curdate() \n" +
            "GROUP BY orderNo)\n" +
            ")mcr\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid on mcr.createUser = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            "GROUP BY manid2.realname,manid.realname  \n" +
            "\n" +
            "union all\n" +
            "\n" +
            "select \n" +
            "'D1-2' as stage,\n" +
            "NOW() as createDay ,\n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "count(orderNo) as notFollowed\n" +
            "from(\n" +
            "select   \n" +
            "createUser, \n" +
            "orderNo,\n" +
            "promiseRepaymentTime\n" +
            "from manCollectionRemark  \n" +
            "where disabled = 0\n" +
            "and orderTag in (4)\n" +
            "and timestampdiff(minute,promiseRepaymentTime,now())>= 30\n" +
            "and id in(\n" +
            "select max(a.id) from manCollectionRemark a inner join ordOrder b on a.orderNo = b.uuid \n" +
            "where a.disabled = 0\n" +
            "and b.status in (8)\n" +
            "and datediff(CURDATE(),a.createTime) = 0 \n" +
            "and datediff(a.createTime,b.refundTime) BETWEEN 1 and 2\n" +
            "GROUP BY orderNo)\n" +
            ")mcr\n" +
            "inner  join (\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0\n" +
            "and sourceType = 0\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 1 and 2\n" +
            "and b.status in (8)\n" +
            "group by orderUUID)\n" +
            "and date(createtime) BETWEEN  date_sub(CURDATE(), interval 1 day)  AND CURDATE()\n" +
            ")colorder on mcr.createUser = colorder.outsourceid  and mcr.orderNo = colorder.orderUUID\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid on mcr.createUser = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            "GROUP BY  manid2.realname,manid.realname \n" +
            " \n" +
            "union all \n" +
            "\n" +
            "select  \n" +
            "'D3-7' as stage,\n" +
            "NOW() as createDay ,\n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "count(orderNo) as notFollowed\n" +
            "from(\n" +
            "select \n" +
            "createUser, \n" +
            "orderNo,\n" +
            "promiseRepaymentTime\n" +
            "from manCollectionRemark  \n" +
            "where disabled = 0\n" +
            "and orderTag in (4)\n" +
            "and timestampdiff(minute,promiseRepaymentTime,now())>= 30\n" +
            "and id in(\n" +
            "select max(a.id) from manCollectionRemark a inner join ordOrder b on a.orderNo = b.uuid \n" +
            "where a.disabled = 0\n" +
            "and b.status in (8)\n" +
            "and datediff(CURDATE(),a.createTime) = 0 \n" +
            "and datediff(a.createTime,b.refundTime) BETWEEN 3 and 7    \n" +
            "GROUP BY orderNo)\n" +
            ")mcr\n" +
            "inner join (\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0\n" +
            "and sourceType = 0\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 3 and 7\n" +
            "and b.status in (8)\n" +
            "group by orderUUID)\n" +
            "and date(createtime) BETWEEN  date_sub(CURDATE(), interval 4 day) and CURDATE()\n" +
            ")colorder on mcr.createUser = colorder.outsourceid  and mcr.orderNo = colorder.orderUUID\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid on mcr.createUser = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            "GROUP BY  manid2.realname,manid.realname ;")
    List<D0ToD7OrderNotFollowUpCollectionData> getD0ToD7OrderNotFollowUpCollectionData();

    // D1-2_D3-7 当日新增未还款拨打紧急联系人情况
    @Select("select \n" +
            "stage,                  -- 阶段\n" +
            "now() as createDay,     -- 日期\n" +
            "parentName,             -- 组长\n" +
            "studentName,            -- 催收员\n" +
            "taskNum,                -- 未还款单数\n" +
            "contactUser,            -- 联系本人单数\n" +
            "notContactUser,        -- 未联系本人单数\n" +
            "call3,                  -- 联系1-3个紧急联系人的单数\n" +
            "allCall,                -- 联系4个紧急联系人单数\n" +
            "onlyself                -- 只联系本人\n" +
            "from(\n" +
            "select\n" +
            "'D1-2' as stage,\n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "t.taskNum,   \n" +
            "ifnull(t1.usrlinkNum,0) as linkNum,\n" +
            "ifnull(t1.contactUser,0) as contactUser,\n" +
            "ifnull(t1.notContactUser,0) as notContactUser,\n" +
            "ifnull(t1.call3,0) as call3,\n" +
            "ifnull(t1.allCall,0) as allCall,\n" +
            "ifnull(t1.onlyself,0) as onlyself\n" +
            "from(\n" +
            "select \n" +
            "outsourceid,\n" +
            "count(orderUUID) as taskNum  \n" +
            "from(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0 and sourceType = 0 and b.status in (8) and datediff(CURDATE(),a.createTime) = 0\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 1 and 2 group by orderUUID\n" +
            ")\n" +
            ")COH group by outsourceid\n" +
            ")t\n" +
            "LEFT JOIN\n" +
            "(\n" +
            "select \n" +
            "createUser,\n" +
            "count(orderNo) as usrlinkNum,\n" +
            "sum(userSelf) as  contactUser,\n" +
            "sum(notUserSelf) as notContactUser,\n" +
            "sum(call3) as call3,\n" +
            "sum(allcall) as allcall,\n" +
            "sum(onlyusrself) as onlyself\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "createUser,\n" +
            "orderNo,\n" +
            "case when sum(mobile1 + WA1) in (1,2) then 1 else 0 end as userSelf,\n" +
            "case when sum(mobile1 + WA1) = 0  then 1 else 0 end as notUserSelf,\n" +
            "if(sum(mobile3 + mobile4 + mobile5 + mobile6) in (1,2,3),1,0) as call3,\n" +
            "if(sum(mobile3 + mobile4 + mobile5 + mobile6) = 4,1,0) as allcall,\n" +
            "case when sum(mobile1 + WA1) in (1,2) and sum(mobile3+mobile4+mobile5+mobile6) = 0 then 1 else 0 end as onlyusrself\n" +
            "from(\n" +
            "select \n" +
            "createUser,\n" +
            "orderNo,\n" +
            "if(contactType = 1,1,0) as mobile1,\n" +
            "if(contactType = 2,1,0) as WA1,\n" +
            "if(contactType = 3,1,0) as mobile3,\n" +
            "if(contactType = 4,1,0) as mobile4,\n" +
            "if(contactType = 5,1,0) as mobile5,\n" +
            "if(contactType = 6,1,0) as mobile6\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "a.createUser,\n" +
            "orderNo,\n" +
            "contactType\n" +
            "from manCollectionRemark  a inner join ordOrder b on a.orderNo = b.uuid\n" +
            "where a.disabled = 0\n" +
            "and a.contactMode in (1,2,3)\n" +
            "and a.contactType in (1,2,3,4,5,6)\n" +
            "and datediff(CURDATE(),a.createtime) = 0\n" +
            "and datediff(a.createTime,b.refundTime) BETWEEN 1 and 2\n" +
            "and b.status in (8)\n" +
            "GROUP BY a.createUser,orderNo,contactType\n" +
            ")mc\n" +
            ")tt\n" +
            "GROUP BY createUser,orderNo\n" +
            ")mcr\n" +
            "inner join \n" +
            "(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0 and sourceType = 0 and b.status in (8) and datediff(CURDATE(),a.createTime) = 0 \n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 1 and 2  group by orderUUID\n" +
            ")\n" +
            ")colorder on mcr.createUser = colorder.outsourceid  and mcr.orderNo = colorder.orderUUID\n" +
            "GROUP BY createUser\n" +
            ")t1 on t.outsourceid=t1.createUser\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled = 0\n" +
            ") manid on t.outsourceid = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            ")result\n" +
            "GROUP BY stage,createDay,parentName,studentName,taskNum,contactUser,notContactUser,call3,allCall,onlyself\n" +
            "\n" +
            "union all\n" +
            "\n" +
            "select \n" +
            "stage,\n" +
            "now() as createDay,\n" +
            "parentName,\n" +
            "studentName,\n" +
            "taskNum,\n" +
            "contactUser,\n" +
            "notContactUser,\n" +
            "call3,\n" +
            "allCall,\n" +
            "onlyself\n" +
            "from(\n" +
            "select \n" +
            "'D3-7' as stage,\n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "t.taskNum,   \n" +
            "ifnull(t1.usrlinkNum,0) as linkNum,\n" +
            "ifnull(t1.contactUser,0) as contactUser,\n" +
            "ifnull(t1.notContactUser,0) as notContactUser,\n" +
            "ifnull(t1.call3,0) as call3,\n" +
            "ifnull(t1.allCall,0) as allCall,\n" +
            "ifnull(t1.onlyself,0) as onlyself\n" +
            "from(\n" +
            "select \n" +
            "outsourceid,\n" +
            "count(orderUUID) as taskNum\n" +
            "from(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0 and sourceType = 0 and b.status in (8) and datediff(CURDATE(),a.createTime) = 0\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 3 and 7 group by orderUUID\n" +
            ")\n" +
            ")COH \n" +
            "group by outsourceid\n" +
            ")t\n" +
            "LEFT JOIN\n" +
            "(\n" +
            "select \n" +
            "createUser,\n" +
            "count(orderNo) as usrlinkNum,\n" +
            "sum(userSelf) as  contactUser,\n" +
            "sum(notUserSelf) as notContactUser,\n" +
            "sum(onlyusrself) as onlyself,\n" +
            "sum(call3) as call3,\n" +
            "sum(allcall) as allcall\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "createUser,\n" +
            "orderNo,\n" +
            "case when sum(mobile1 + WA1) in (1,2) then 1 else 0 end as userSelf,\n" +
            "case when sum(mobile1 + WA1) = 0  then 1 else 0 end as notUserSelf,\n" +
            "case when sum(mobile1 + WA1) in (1,2) and sum(mobile3+mobile4+mobile5+mobile6) = 0 then 1 else 0 end as onlyusrself,\n" +
            "if(sum(mobile3 + mobile4 + mobile5 + mobile6) in (1,2,3),1,0) as call3,\n" +
            "if(sum(mobile3 + mobile4 + mobile5 + mobile6) = 4,1,0) as allcall\n" +
            "from(\n" +
            "select \n" +
            "createUser,\n" +
            "orderNo,\n" +
            "if(contactType = 1,1,0) as mobile1,\n" +
            "if(contactType = 2,1,0) as WA1,\n" +
            "if(contactType = 3,1,0) as mobile3,\n" +
            "if(contactType = 4,1,0) as mobile4,\n" +
            "if(contactType = 5,1,0) as mobile5,\n" +
            "if(contactType = 6,1,0) as mobile6\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "a.createUser,\n" +
            "orderNo,\n" +
            "contactType\n" +
            "from manCollectionRemark  a inner join ordOrder b on a.orderNo = b.uuid\n" +
            "where a.disabled = 0\n" +
            "and a.contactMode in (1,2)\n" +
            "and a.contactType in (1,2,3,4,5,6)\n" +
            "and datediff(CURDATE(),a.createtime) = 0\n" +
            "and datediff(a.createTime,b.refundTime) BETWEEN 3 and 7\n" +
            "and b.status in (8)\n" +
            "GROUP BY a.createUser,orderNo,contactType\n" +
            ")mc\n" +
            ")tt\n" +
            "GROUP BY createUser,orderNo\n" +
            ")mcr\n" +
            "inner join \n" +
            "(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0 and sourceType = 0 and b.status in (8)  and datediff(CURDATE(),a.createTime) = 0 \n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 3 and 7  group by orderUUID\n" +
            ")\n" +
            ")colorder on mcr.createUser = colorder.outsourceid  and mcr.orderNo = colorder.orderUUID\n" +
            "GROUP BY createUser\n" +
            ")t1 on t.outsourceid=t1.createUser\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled = 0\n" +
            ") manid on t.outsourceid = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            ")result\n" +
            "GROUP BY stage,createDay,parentName,studentName,taskNum,contactUser,notContactUser,call3,allCall,onlyself;")
    List<D1ToD7OldOrderNotCallCollectionData> getD1ToD7NewOrderNotCallCollectionData();

    // D1-2_D3-7 存量未还款拨打紧急联系人情况
    @Select("select \n" +
            "step,                  -- 阶段\n" +
            "now() as createDay,     -- 日期\n" +
            "parentName,             -- 组长\n" +
            "studentName,            -- 催收员\n" +
            "taskNum,                -- 未还款单数\n" +
            "contactUser,            -- 联系本人单数\n" +
            "notContactUser,         -- 未联系本人单数\n" +
            "call3,                  -- 联系1-3个紧急联系人的单数\n" +
            "allCall,                -- 联系4个紧急联系人单数\n" +
            "onlyself                -- 只联系本人\n" +
            "from(\n" +
            "select\n" +
            "'D1-2' as step,\n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "t.taskNum,   \n" +
            "ifnull(t1.usrlinkNum,0) as linkNum,\n" +
            "ifnull(t1.contactUser,0) as contactUser,\n" +
            "ifnull(t1.notContactUser,0) as notContactUser,\n" +
            "ifnull(t1.call3,0) as call3,\n" +
            "ifnull(t1.allCall,0) as allCall,\n" +
            "ifnull(t1.onlyself,0) as onlyself\n" +
            "from(\n" +
            "select \n" +
            "outsourceid,\n" +
            "count(orderUUID) as taskNum  \n" +
            "from(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0 and sourceType = 0 and b.status in (8) and datediff(CURDATE(),refundTime) = 2\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 1 and 2 group by orderUUID\n" +
            ")\n" +
            ")COH group by outsourceid\n" +
            ")t\n" +
            "LEFT JOIN\n" +
            "(\n" +
            "select \n" +
            "createUser,\n" +
            "count(orderNo) as usrlinkNum,\n" +
            "sum(userSelf) as  contactUser,\n" +
            "sum(notUserSelf) as notContactUser,\n" +
            "sum(call3) as call3,\n" +
            "sum(allcall) as allcall,\n" +
            "sum(onlyusrself) as onlyself\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "createUser,\n" +
            "orderNo,\n" +
            "case when sum(mobile1 + WA1) in (1,2) then 1 else 0 end as userSelf,\n" +
            "case when sum(mobile1 + WA1) = 0  then 1 else 0 end as notUserSelf,\n" +
            "if(sum(mobile3 + mobile4 + mobile5 + mobile6) in (1,2,3),1,0) as call3,\n" +
            "if(sum(mobile3 + mobile4 + mobile5 + mobile6) = 4,1,0) as allcall,\n" +
            "case when sum(mobile1 + WA1) in (1,2) and sum(mobile3+mobile4+mobile5+mobile6) = 0 then 1 else 0 end as onlyusrself\n" +
            "from(\n" +
            "select \n" +
            "createUser,\n" +
            "orderNo,\n" +
            "if(contactType = 1,1,0) as mobile1,\n" +
            "if(contactType = 2,1,0) as WA1,\n" +
            "if(contactType = 3,1,0) as mobile3,\n" +
            "if(contactType = 4,1,0) as mobile4,\n" +
            "if(contactType = 5,1,0) as mobile5,\n" +
            "if(contactType = 6,1,0) as mobile6\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "a.createUser,\n" +
            "orderNo,\n" +
            "contactType\n" +
            "from manCollectionRemark  a inner join ordOrder b on a.orderNo = b.uuid\n" +
            "where a.disabled = 0\n" +
            "and a.contactMode in (1,2,3)\n" +
            "and a.contactType in (1,2,3,4,5,6)\n" +
            "and datediff(CURDATE(),a.createtime) = 0\n" +
            "and datediff(a.createTime,b.refundTime) BETWEEN 1 and 2\n" +
            "and b.status in (8)\n" +
            "GROUP BY a.createUser,orderNo,contactType\n" +
            ")mc\n" +
            ")tt\n" +
            "GROUP BY createUser,orderNo\n" +
            ")mcr\n" +
            "inner join \n" +
            "(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0 and sourceType = 0 and b.status in (8) and datediff(CURDATE(),refundTime) = 2 \n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 1 and 2  group by orderUUID\n" +
            ")\n" +
            ")colorder on mcr.createUser = colorder.outsourceid  and mcr.orderNo = colorder.orderUUID\n" +
            "GROUP BY createUser\n" +
            ")t1 on t.outsourceid=t1.createUser\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled = 0\n" +
            ") manid on t.outsourceid = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            ")result\n" +
            "GROUP BY step,createDay,parentName,studentName,taskNum,contactUser,notContactUser,call3,allCall,onlyself\n" +
            "\n" +
            "union all\n" +
            "\n" +
            "select \n" +
            "step,\n" +
            "now() as createDay,\n" +
            "parentName,\n" +
            "studentName,\n" +
            "taskNum,\n" +
            "contactUser,\n" +
            "notContactUser,\n" +
            "call3,\n" +
            "allCall,\n" +
            "onlyself\n" +
            "from(\n" +
            "select \n" +
            "'D3-7' as step,\n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "t.taskNum,   \n" +
            "ifnull(t1.usrlinkNum,0) as linkNum,\n" +
            "ifnull(t1.contactUser,0) as contactUser,\n" +
            "ifnull(t1.notContactUser,0) as notContactUser,\n" +
            "ifnull(t1.call3,0) as call3,\n" +
            "ifnull(t1.allCall,0) as allCall,\n" +
            "ifnull(t1.onlyself,0) as onlyself\n" +
            "from(\n" +
            "select \n" +
            "outsourceid,\n" +
            "count(orderUUID) as taskNum\n" +
            "from(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0 and sourceType = 0 and b.status in (8) and datediff(CURDATE(),refundtime) BETWEEN 4 and 7\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 3 and 7 group by orderUUID\n" +
            ")\n" +
            ")COH \n" +
            "group by outsourceid\n" +
            ")t\n" +
            "LEFT JOIN\n" +
            "(\n" +
            "select \n" +
            "createUser,\n" +
            "count(orderNo) as usrlinkNum,\n" +
            "sum(userSelf) as  contactUser,\n" +
            "sum(notUserSelf) as notContactUser,\n" +
            "sum(onlyusrself) as onlyself,\n" +
            "sum(call3) as call3,\n" +
            "sum(allcall) as allcall\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "createUser,\n" +
            "orderNo,\n" +
            "case when sum(mobile1 + WA1) in (1,2) then 1 else 0 end as userSelf,\n" +
            "case when sum(mobile1 + WA1) = 0  then 1 else 0 end as notUserSelf,\n" +
            "case when sum(mobile1 + WA1) in (1,2) and sum(mobile3+mobile4+mobile5+mobile6) = 0 then 1 else 0 end as onlyusrself,\n" +
            "if(sum(mobile3 + mobile4 + mobile5 + mobile6) in (1,2,3),1,0) as call3,\n" +
            "if(sum(mobile3 + mobile4 + mobile5 + mobile6) = 4,1,0) as allcall\n" +
            "from(\n" +
            "select \n" +
            "createUser,\n" +
            "orderNo,\n" +
            "if(contactType = 1,1,0) as mobile1,\n" +
            "if(contactType = 2,1,0) as WA1,\n" +
            "if(contactType = 3,1,0) as mobile3,\n" +
            "if(contactType = 4,1,0) as mobile4,\n" +
            "if(contactType = 5,1,0) as mobile5,\n" +
            "if(contactType = 6,1,0) as mobile6\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "a.createUser,\n" +
            "orderNo,\n" +
            "contactType\n" +
            "from manCollectionRemark  a inner join ordOrder b on a.orderNo = b.uuid\n" +
            "where a.disabled = 0\n" +
            "and a.contactMode in (1,2,3)\n" +
            "and a.contactType in (1,2,3,4,5,6)\n" +
            "and datediff(CURDATE(),a.createtime) = 0\n" +
            "and datediff(a.createTime,b.refundTime) BETWEEN 3 and 7\n" +
            "and b.status in (8)\n" +
            "GROUP BY a.createUser,orderNo,contactType\n" +
            ")mc\n" +
            ")tt\n" +
            "GROUP BY createUser,orderNo\n" +
            ")mcr\n" +
            "inner join \n" +
            "(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0 and sourceType = 0 and b.status in (8)  and datediff(CURDATE(),refundtime) BETWEEN 4 and 7\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 3 and 7  group by orderUUID\n" +
            ")\n" +
            ")colorder on mcr.createUser = colorder.outsourceid  and mcr.orderNo = colorder.orderUUID\n" +
            "GROUP BY createUser\n" +
            ")t1 on t.outsourceid=t1.createUser\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled = 0\n" +
            ") manid on t.outsourceid = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            ")result\n" +
            "GROUP BY step,createDay,parentName,studentName,taskNum,contactUser,notContactUser,call3,allCall,onlyself;\n" +
            "\n")
    List<D1ToD7NewOrderNotCallCollectionData> getD1ToD7OldOrderNotCallCollectionData();

    // D0当日未还款订单WA和电话是否全部标亮
    @Select("select \n" +
            "stage ,                -- 阶段\n" +
            "now()  as createDay,   -- 分案时间\n" +
            "parentName,            -- 组长\n" +
            "studentName,           -- 催收员\n" +
            "taskNum,               -- 未还款单量\n" +
            "dealNum,               -- 处理过单量,\n" +
            "taskNum - dealNum as notDealNum  , -- 未还款且未跟进WA或者电话\n" +
            "allMode                             -- 用过WA和电话单量\n" +
            "from(\n" +
            "select \n" +
            "'D0' as stage,\n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "t.taskNum,    \n" +
            "ifnull(t1.dealNum,0) as dealNum,\n" +
            "ifnull(t1.allMode,0) as allMode \n" +
            "from(\n" +
            "select \n" +
            "outsourceid,\n" +
            "count(orderUUID) as taskNum\n" +
            "from(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in (\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where sourceType = 0\n" +
            "and datediff(a.createtime,b.refundtime) = 0 \n" +
            "and b.status in (7)\n" +
            "group by orderUUID)\n" +
            "and date(createtime) = CURDATE() \n" +
            ")COH  \n" +
            "group by outsourceid\n" +
            ")t\n" +
            "LEFT JOIN(\n" +
            "select \n" +
            "createUser,\n" +
            "count(orderNo) as dealnum,\n" +
            "sum(allMode) as allMode\n" +
            "from(\n" +
            "select \n" +
            "a.createUser,\n" +
            "orderNo,\n" +
            "if(count(distinct contactMode)=2,1,0) as allMode\n" +
            "from manCollectionRemark  a inner join ordOrder b on a.orderNo = b.uuid\n" +
            "where a.disabled = 0\n" +
            "and datediff(a.createTime,b.refundTime) = 0\n" +
            "and b.status in (7)\n" +
            "and a.contactMode in (1,2)\n" +
            "and date(a.createTime) = CURDATE()\n" +
            "GROUP BY a.createUser,orderNo\n" +
            ")mcr \n" +
            "GROUP BY createUser\n" +
            ")t1 on t.outsourceid=t1.createUser\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled = 0\n" +
            ") manid on t.outsourceid = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            ")result\n" +
            "GROUP BY stage ,createDay,parentName,studentName,taskNum,dealNum,allMode;")
    List<D0OrderCallCollectionData> getD0OrderCallCollectionData();

    // D1-2_D3-7逾期订单是否全部跟进(新增+存量
    @Select("\n" +
            "select \n" +
            "stage ,                 -- 阶段\n" +
            "now() as createDay,     -- 日期\n" +
            "parentName,             -- 组长\n" +
            "studentName,            -- 催收员\n" +
            "taskNum,                -- 未还款单量\n" +
            "linkNum,                -- 未还款处理过单量\n" +
            "taskNum- linkNum as notLinkNum   -- 未还款未处理过单量\n" +
            "from(\n" +
            "select \n" +
            "'D1-2' as stage,\n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "t.taskNum , \n" +
            "case when ifnull(t1.linkNum,0) > t.taskNum then t.taskNum else ifnull(t1.linkNum,0) end as linkNum\n" +
            "from(\n" +
            "select \n" +
            "outsourceid,\n" +
            "count(orderUUID) as taskNum\n" +
            "from(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0\n" +
            "and sourceType = 0\n" +
            "and datediff(a.createtime,b.refundtime) between 1 and 2\n" +
            "and b.status in (8)\n" +
            "group by orderUUID)\n" +
            "and date(createtime)  BETWEEN  date_sub(CURDATE(), interval 1 day) and CURDATE()\n" +
            ")COH group by outsourceid\n" +
            ")t\n" +
            "LEFT JOIN\n" +
            "(\n" +
            "select \n" +
            "createUser,\n" +
            "count(orderNo) as linkNum  \n" +
            "from(\n" +
            "select \n" +
            "a.createUser,\n" +
            "orderNo\n" +
            "from manCollectionRemark  a inner join ordOrder b on a.orderNo = b.uuid\n" +
            "where a.disabled = 0\n" +
            "and a.contactMode in (1,2,3,4)\n" +
            "and datediff(CURDATE(),a.createTime) = 0\n" +
            "and datediff(a.createTime,b.refundTime) BETWEEN 1 and 2\n" +
            "and b.status in (8)\n" +
            "GROUP BY a.createUser,orderNo\n" +
            ")mcr GROUP BY createUser\n" +
            ")t1 on t.outsourceid=t1.createUser\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled = 0\n" +
            ") manid on t.outsourceid = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            ")result\n" +
            "GROUP BY stage ,createDay,parentName,studentName,taskNum,linkNum\n" +
            "\n" +
            "union  all\n" +
            "\n" +
            "select \n" +
            "stage ,\n" +
            "now() as createDay,\n" +
            "parentName,\n" +
            "studentName,\n" +
            "taskNum,\n" +
            "linkNum,\n" +
            "taskNum- linkNum as notLinkNum\n" +
            "from(\n" +
            "select \n" +
            "'D3-7' as stage,\n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "t.taskNum,\n" +
            "case when ifnull(t1.linkNum,0) > t.taskNum then t.taskNum else ifnull(t1.linkNum,0) end as linkNum    \n" +
            "from(\n" +
            "select \n" +
            "outsourceid,\n" +
            "count(orderUUID) as taskNum\n" +
            "from(\n" +
            "select\n" +
            "orderUUID,\n" +
            "date(createtime) as createDay,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0\n" +
            "and sourceType = 0\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 3 and 7\n" +
            "and b.status in (8)\n" +
            "group by orderUUID)\n" +
            "and date(createtime)  BETWEEN  date_sub(CURDATE(), interval 4 day) and CURDATE()\n" +
            ")COH group by outsourceid\n" +
            ")t\n" +
            "LEFT JOIN\n" +
            "(\n" +
            "select \n" +
            "createUser,\n" +
            "count(orderNo) as linkNum    \n" +
            "from(\n" +
            "select \n" +
            "a.createUser,\n" +
            "orderNo\n" +
            "from manCollectionRemark  a inner join ordOrder b on a.orderNo = b.uuid\n" +
            "where a.disabled = 0\n" +
            "and a.contactMode in (1,2,3,4)\n" +
            "and datediff(a.createTime,b.refundTime) BETWEEN 3 and 7\n" +
            "and datediff(CURDATE(),a.createTime) = 0\n" +
            "and b.status in (8)\n" +
            "GROUP BY a.createUser,orderNo\n" +
            ")mcr GROUP BY createUser\n" +
            ")t1 on t.outsourceid=t1.createUser\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid on t.outsourceid = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            ")result\n" +
            "GROUP BY stage ,createDay,parentName,studentName,taskNum,linkNum;")
    List<D1ToD7OverdueOrderCollectionData> getD1ToD7OverdueOrderCollectionData();


    // D0_D1-2_D3-7新增未还款未处理订单明细
    @Select("select \n" +
            "step ,                          -- 阶段\n" +
            "now() as createDay,     -- 日期\n" +
            "parentName,              -- 组长\n" +
            "studentName,            -- 催收员\n" +
            "orderUUID                 -- 订单编号\n" +
            "from(\n" +
            "select \n" +
            "'D0' as step,      \n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "orderuuid\n" +
            "from(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where datediff(CURDATE(),a.createtime) = 0  and sourceType = 0 and b.status in (7)\n" +
            "and datediff(a.createtime,b.refundtime) = 0 group by orderUUID\n" +
            ")\n" +
            "and orderUUID not in\n" +
            "(\n" +
            "select \n" +
            "orderNo\n" +
            "from manCollectionRemark  a inner join ordOrder b on a.orderNo = b.uuid\n" +
            "where a.disabled = 0\n" +
            "and datediff(a.createTime,b.refundTime) = 0\n" +
            "and b.status in (7)\n" +
            "and a.contactMode in (1,2,3,4)\n" +
            "and datediff(CURDATE(),a.createTime) = 0\n" +
            "GROUP BY orderNo\n" +
            ")\n" +
            ")t \n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid on t.outsourceid = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            ")result\n" +
            "GROUP BY step ,parentName,studentName,orderUUID\n" +
            "\n" +
            "union ALL\n" +
            "\n" +
            "select \n" +
            "step ,\n" +
            "now() as createDay,\n" +
            "parentName,\n" +
            "studentName,\n" +
            "orderUUID\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "'D1-2' as step,     \n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "orderUUID\n" +
            "from(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where sourceType = 0  and b.status in (8) and datediff(CURDATE(),a.createtime) = 0\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 1 and 2 group by orderUUID\n" +
            ")\n" +
            ")COH\n" +
            "LEFT JOIN\n" +
            "(\n" +
            "select \n" +
            "orderNo\n" +
            "from manCollectionRemark  a inner join ordOrder b on a.orderNo = b.uuid\n" +
            "where a.disabled = 0 and a.contactMode in (1,2,3,4) and b.status in (8)\n" +
            "and datediff(a.createTime,b.refundTime) between 1 and 2\n" +
            "and datediff(CURDATE(),a.createTime) = 0  \n" +
            "GROUP BY orderNo\n" +
            ")MCR on COH.orderUUID = MCR.orderNo\n" +
            "inner join\n" +
            "(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid on COH.outsourceid = manid.id\n" +
            "inner join\n" +
            "(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            "where MCR.orderNo is null\n" +
            ")result\n" +
            "GROUP BY step,createDay,parentName,studentName,orderUUID\n" +
            "\n" +
            "union ALL\n" +
            "\n" +
            "select \n" +
            "step ,\n" +
            "now() as createDay,\n" +
            "parentName,\n" +
            "studentName,\n" +
            "orderUUID\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "'D3-7' as step,     \n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "orderUUID\n" +
            "from(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where sourceType = 0  and b.status in (8) and datediff(CURDATE(),a.createtime) = 0\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 3 and 7 group by orderUUID\n" +
            ")\n" +
            ")COH\n" +
            "LEFT JOIN\n" +
            "(\n" +
            "select \n" +
            "orderNo\n" +
            "from manCollectionRemark  a inner join ordOrder b on a.orderNo = b.uuid\n" +
            "where a.disabled = 0 and a.contactMode in (1,2,3,4) and b.status in (8)\n" +
            "and datediff(a.createTime,b.refundTime) between 3 and 7\n" +
            "and datediff(CURDATE(),a.createTime) = 0  GROUP BY orderNo\n" +
            ")MCR on COH.orderUUID = MCR.orderNo\n" +
            "inner join\n" +
            "(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid on COH.outsourceid = manid.id\n" +
            "inner join\n" +
            "(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            "where MCR.orderNo is null\n" +
            ")result\n" +
            "GROUP BY step,createDay,parentName,studentName,orderUUID;")
    List<D0ToD7PendingOrderDetails> getD0ToD7PendingOrderDetails();

    // D0_D1-2_D3-7可联承诺前后半小时未处理订单明细
    @Select("select \n" +
            "'D0' as step,                    -- 阶段\n" +
            "NOW() as createDay ,             -- 日期\n" +
            "manid2.realname as parentName,   -- 组长\n" +
            "manid.realname as studentName,   -- 催收员\n" +
            "orderNo,                         -- 订单号\n" +
            "promiseRepaymentTime             -- 承诺还款时间\n" +
            "from(\n" +
            "select \n" +
            "createUser, \n" +
            "orderNo,\n" +
            "promiseRepaymentTime\n" +
            "from manCollectionRemark  \n" +
            "where disabled = 0\n" +
            "and orderTag in (4)\n" +
            "and timestampdiff(minute,promiseRepaymentTime,now())>= 30\n" +
            "and id in\n" +
            "(\n" +
            "select max(a.id) from manCollectionRemark a inner join ordOrder b on a.orderNo = b.uuid \n" +
            "where a.disabled = 0 and  b.status = 7 and datediff(a.createTime,b.refundTime) = 0 and date(a.createtime) = curdate() \n" +
            "GROUP BY orderNo\n" +
            ")\n" +
            ")mcr\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid on mcr.createUser = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            "\n" +
            "union all\n" +
            "\n" +
            "select \n" +
            "'D1-2' as step,\n" +
            "NOW() as createDay ,\n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "orderNo,\n" +
            "promiseRepaymentTime\n" +
            "from(\n" +
            "select   \n" +
            "createUser, \n" +
            "orderNo,\n" +
            "promiseRepaymentTime\n" +
            "from manCollectionRemark  \n" +
            "where disabled = 0\n" +
            "and orderTag in (4)\n" +
            "and timestampdiff(minute,promiseRepaymentTime,now())>= 30\n" +
            "and id in\n" +
            "(\n" +
            "select max(a.id) from manCollectionRemark a inner join ordOrder b on a.orderNo = b.uuid \n" +
            "where a.disabled = 0 and b.status in (8) and datediff(CURDATE(),a.createTime) = 0 \n" +
            "and datediff(a.createTime,b.refundTime) BETWEEN 1 and 2\n" +
            "GROUP BY orderNo\n" +
            ")\n" +
            ")mcr\n" +
            "inner  join (\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0 and sourceType = 0 and b.status in (8)\n" +
            "and datediff(a.createtime,b.refundtime) BETWEEN 1 and 2\n" +
            "and datediff(CURDATE(),a.createtime) BETWEEN 0 AND 1\n" +
            "group by orderUUID\n" +
            ")\n" +
            ")colorder on mcr.createUser = colorder.outsourceid  and mcr.orderNo = colorder.orderUUID\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid on mcr.createUser = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            "\n" +
            "union all \n" +
            "\n" +
            "select  \n" +
            "'D3-7' as step,\n" +
            "NOW() as createDay ,\n" +
            "manid2.realname as parentName,\n" +
            "manid.realname as studentName,\n" +
            "orderNo,\n" +
            "promiseRepaymentTime\n" +
            "from(\n" +
            "select \n" +
            "createUser, \n" +
            "orderNo,\n" +
            "promiseRepaymentTime\n" +
            "from manCollectionRemark  \n" +
            "where disabled = 0\n" +
            "and orderTag in (4)\n" +
            "and timestampdiff(minute,promiseRepaymentTime,now())>= 30\n" +
            "and id in\n" +
            "(\n" +
            "select max(a.id) from manCollectionRemark a inner join ordOrder b on a.orderNo = b.uuid \n" +
            "where a.disabled = 0 and b.status in (8) and datediff(a.createTime,b.refundTime) BETWEEN 3 and 7\n" +
            "and datediff(CURDATE(),a.createTime) = 0 \n" +
            "GROUP BY orderNo\n" +
            ")\n" +
            ")mcr\n" +
            "inner join \n" +
            "(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in\n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where  a.disabled = 0 and sourceType = 0 and datediff(a.createtime,b.refundtime) BETWEEN 3 and 7 and b.status in (8)\n" +
            "and datediff(CURDATE(),a.createtime) BETWEEN  0 and 4\n" +
            "group by orderUUID\n" +
            ")\n" +
            ")colorder on mcr.createUser = colorder.outsourceid  and mcr.orderNo = colorder.orderUUID\n" +
            "inner join(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid on mcr.createUser = manid.id\n" +
            "inner join(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled  = 0\n" +
            ") manid2 on manid.parentId = manid2.id;")
    List<D0ToD7CanCommitmentOrderDetails> getD0ToD7CanCommitmentOrderDetails();

    // D0当日未还款订单WA和电话没有全部标亮订单明细
    @Select("select \n" +
            "'D0' as step,                       -- 阶段\n" +
            "now() as createDay,                 -- 日期\n" +
            "manid2.realname as parentName,      -- 组长\n" +
            "manid.realname as studentName,      -- 催收员\n" +
            "orderuuid                           -- 订单编号\n" +
            "from\n" +
            "(\n" +
            "select\n" +
            "orderUUID,\n" +
            "outsourceid\n" +
            "from collectionOrderHistory\n" +
            "where id in \n" +
            "(\n" +
            "select max(a.id) from collectionOrderHistory a left join ordOrder b on a.orderUUID = b.uuid \n" +
            "where sourceType = 0 and b.status in (7) and datediff(CURDATE(),a.createtime) = 0\n" +
            "and datediff(a.createtime,b.refundtime) = 0 group by orderUUID\n" +
            ")\n" +
            ")t\n" +
            "left join\n" +
            "(\n" +
            "select \n" +
            "createUser,\n" +
            "orderNo,\n" +
            "allmode\n" +
            "from\n" +
            "(\n" +
            "select \n" +
            "a.createUser,\n" +
            "orderNo,\n" +
            "if(count(distinct contactMode)= 2,1,0) as allMode\n" +
            "from manCollectionRemark  a inner join ordOrder b on a.orderNo = b.uuid\n" +
            "where a.disabled = 0 and datediff(a.createTime,b.refundTime) = 0 \n" +
            "and b.status in (7) and a.contactMode in (1,2) and datediff(CURDATE(),a.createtime) = 0\n" +
            "GROUP BY a.createUser,orderNo\n" +
            ")mcr \n" +
            "where allMode = 1\n" +
            ")t1 on t.orderuuid = t1.orderNo\n" +
            "inner join\n" +
            "(\n" +
            "select id,realname,parentId\n" +
            "from manUser\n" +
            "WHERE disabled = 0\n" +
            ") manid on t.outsourceid = manid.id\n" +
            "inner join\n" +
            "(\n" +
            "select id, realname\n" +
            "from manUser\n" +
            "WHERE disabled = 0\n" +
            ") manid2 on manid.parentId = manid2.id\n" +
            "where t1.orderNo is NULL\n" +
            "GROUP BY step,createDay,parentName,studentname,orderUUID;\n" +
            "\n")
    List<D0NoLightUpOrderDetails> getD0NoLightUpOrderDetails();

}
