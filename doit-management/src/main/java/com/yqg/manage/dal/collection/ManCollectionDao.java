package com.yqg.manage.dal.collection;

import com.yqg.base.data.mapper.BaseMapper;
import com.yqg.manage.dal.provider.CollectionSqlProvider;
import com.yqg.manage.service.collection.request.AssignableCollectionOrderReq;
import com.yqg.manage.service.collection.response.*;
import com.yqg.manage.service.order.request.OrderSearchRequest;
import com.yqg.manage.service.order.request.OverdueOrderRequest;
import com.yqg.manage.service.order.request.PaidOrderRequest;
import com.yqg.manage.service.order.response.ManOrderRemarkResponse;
import com.yqg.order.entity.OrdOrder;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * @author Jacob
 * ??????
 */
@Mapper
public interface ManCollectionDao extends BaseMapper<OrdOrder> {

    /**
     * 获得逾期订单
     * @param searchResquest
     * @return
     */
    @SelectProvider(type = CollectionSqlProvider.class, method = "outCollectionsList")
    @Options(useGeneratedKeys = true)
    List<OutCollectionResponse> outCollectionsList(@Param("OverdueOrderRequest") OverdueOrderRequest searchResquest);

    /**
     * 获得逾期订单（只查催收人员的订单）
     * @param searchResquest
     * @return
     */
    @SelectProvider(type = CollectionSqlProvider.class, method = "outCollectionsByOutSourceIdList")
    @Options(useGeneratedKeys = true)
    List<OutCollectionResponse> outCollectionsByOutSourceIdList(@Param("OverdueOrderRequest") OverdueOrderRequest searchResquest);


    /**
     * 查发薪日个数
     * @param searchResquest
     * @return
     */
    @SelectProvider(type = CollectionSqlProvider.class, method = "payDayCount")
    @Options(useGeneratedKeys = true)
    Integer payDayCount(@Param("OverdueOrderRequest") OverdueOrderRequest searchResquest);
    /**
     * 通过订单号，获得备注信息
     * @param searchResquest
     * @return
     */
    @SelectProvider(type = CollectionSqlProvider.class, method = "manOrderRemarkList")
    @Options(useGeneratedKeys = true)
    List<ManOrderRemarkResponse> manOrderRemarkList(@Param("OrderSearchRequest") OrderSearchRequest searchResquest);

    /**
     * 通过订单号，获得备注信息(新改了存储逻辑）
     * @param searchResquest
     * @return
     */
    @SelectProvider(type = CollectionSqlProvider.class, method = "manOrderRemarkListNew")
    @Options(useGeneratedKeys = true)
    List<ManOrderRemarkResponse> manOrderRemarkListNew(@Param("OrderSearchRequest") OrderSearchRequest searchResquest);

    /**
     * 获得已还款订单
     * @param searchResquest
     * @return
     */
    @SelectProvider(type = CollectionSqlProvider.class, method = "repaymentOrderList")
    @Options(useGeneratedKeys = true)
    List<PaymentOrderResponse> repaymentOrderList(@Param("PaidOrderRequest") PaidOrderRequest searchResquest);

    /**
     * 获得已还款订单通过催收人员ID
     * @param searchResquest
     * @return
     */
    @SelectProvider(type = CollectionSqlProvider.class, method = "repaymentOrderByOutSourceIdList")
    @Options(useGeneratedKeys = true)
    List<PaymentOrderResponse> repaymentOrderByOutSourceIdList(@Param("PaidOrderRequest") PaidOrderRequest searchResquest);

    @SelectProvider(type = CollectionSqlProvider.class, method = "getAssignableCollectionOrderList")
    List<CollectionOrderResponse> getAssignableCollectionOrderList(
            @Param("AssignableCollectionOrderReq") AssignableCollectionOrderReq request);

    @SelectProvider(type = CollectionSqlProvider.class, method = "getAssignableQualityCheckOrderList")
    List<CollectionOrderResponse> getAssignableQualityCheckOrderList(
            @Param("AssignableCollectionOrderReq") AssignableCollectionOrderReq request);

    /**
     * 委外催收列表
     * @param request
     * @return
     */
    @SelectProvider(type = CollectionSqlProvider.class, method = "getAssignableOverdueOutSourceOrderList")
    List<CollectionOrderResponse> getAssignableOverdueOutSourceOrderList(
            @Param("AssignableCollectionOrderReq") AssignableCollectionOrderReq request);
    @Select("<script>" +
            "SELECT "
            + "    COUNT(CASE WHEN (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "            THEN o.id END) AS totalAssigned,"
            + "    COUNT(CASE  WHEN o.borrowingCount = 1 AND (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "            THEN o.id END) AS assignedReBorrowingCount0,"
            + "    COUNT(CASE WHEN o.borrowingCount = 2 AND (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "            THEN o.id END) AS assignedReBorrowingCount1,"
            + "    COUNT(CASE WHEN o.borrowingCount = 3  AND (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "            THEN  o.id END) AS assignedReBorrowingCount2,"
            + "    COUNT(CASE  WHEN  o.borrowingCount = 4  AND (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "            THEN o.id END) AS assignedReBorrowingCount3,"
            + "    COUNT(CASE  WHEN  o.borrowingCount > 4 AND (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "            THEN o.id END) AS assignedReBorrowingCountN,"
            + "    COUNT(CASE WHEN (c.id IS NULL OR c.outsourceId = '0' ) "
            + "            THEN o.id END) AS totalUnAssigned,"
            + "    COUNT(CASE WHEN o.borrowingCount = 1 AND (c.id IS NULL OR c.outsourceId = '0' ) "
            + "            THEN o.id END) AS unAssignedReBorrowingCount0,"
            + "    COUNT(CASE WHEN o.borrowingCount = 2 AND (c.id IS NULL OR c.outsourceId = '0' ) "
            + "            THEN o.id END) AS unAssignedReBorrowingCount1,"
            + "    COUNT(CASE WHEN o.borrowingCount = 3 AND (c.id IS NULL OR c.outsourceId = '0' ) "
            + "            THEN o.id END) AS unAssignedReBorrowingCount2,"
            + "    COUNT(CASE WHEN o.borrowingCount = 4 AND (c.id IS NULL OR c.outsourceId = '0' ) "
            + "            THEN o.id END) AS unAssignedReBorrowingCount3,"
            + "    COUNT(CASE WHEN o.borrowingCount > 4 AND (c.id IS NULL OR c.outsourceId = '0' ) "
            + "            THEN o.id END) AS unAssignedReBorrowingCountN"
            + " FROM  ordOrder o LEFT JOIN collectionOrderDetail c ON c.orderUUID = o.uuid and c.disabled =0 and c.sourceType = #{sourceType} "
            + " WHERE o.status = 7 AND DATEDIFF(NOW(), o.refundTime) = #{days} and o.disabled =0 "
            + "<choose><when test='otherArray != null and otherArray.length > 0'> and o.amountApply not in <foreach collection='otherArray' index='index' " +
            "item='item' open='(' separator=',' close=')'>#{item}</foreach></when> "
            + "<otherwise><if test='array != null and array.length > 0'> and o.amountApply in <foreach collection='array' index='index' " +
            "item='item1' open='(' separator=',' close=')'>#{item1}</foreach></if></otherwise></choose>"+
            "</script>")
    D0OrderStatistics getD0CollectionOrderStatistics(@Param("days") Integer days,
                                                     @Param("sourceType") Integer sourceType,
                                                     @Param("array") BigDecimal[] amountApply,
                                                     @Param("otherArray") BigDecimal[] otherArray);

    @Select("<script> SELECT "
            + "    COUNT(CASE "
            + "        WHEN "
            + "            o.borrowingCount = 1 "
            + "                AND (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "        THEN "
            + "            o.id "
            + "    END) AS assignedReBorrowingCount0, "
            + "    COUNT(CASE "
            + "        WHEN "
            + "            o.borrowingCount = 2 "
            + "                AND (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "        THEN "
            + "            o.id "
            + "    END) AS assignedReBorrowingCount1, "
            + "    COUNT(CASE "
            + "        WHEN "
            + "            o.borrowingCount = 3 "
            + "                AND (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "        THEN "
            + "            o.id "
            + "    END) AS assignedReBorrowingCount2, "
            + "    COUNT(CASE "
            + "        WHEN "
            + "            o.borrowingCount = 4 "
            + "                AND (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "        THEN "
            + "            o.id "
            + "    END) AS assignedReBorrowingCount3, "
            + "    COUNT(CASE "
            + "        WHEN "
            + "            o.borrowingCount > 4 "
            + "                AND (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "        THEN "
            + "            o.id "
            + "    END) AS assignedReBorrowingCountN "
            + "     "
            + " FROM "
            + "    ordOrder o "
            + "        LEFT JOIN "
            + "    collectionOrderDetail c ON c.orderUUID = o.uuid and c.disabled =0 and c.sourceType = #{sourceType} "
            + " WHERE "
            + "    o.status = 7  and o.disabled =0 "
            + "        AND DATEDIFF(NOW(), o.refundTime) = #{days} "
            + "        and c.outsourceId = #{sysUserId} "
            + "<choose><when test='otherArray != null and otherArray.length > 0'> and o.amountApply not in <foreach collection='otherArray' index='index' " +
            "item='item' open='(' separator=',' close=')'>#{item}</foreach></when> "
            + "<otherwise><if test='array != null and array.length > 0'> and o.amountApply in <foreach collection='array' index='index' " +
            "item='item1' open='(' separator=',' close=')'>#{item1}</foreach></if></otherwise></choose>"+
            "</script>")
    D0CollectorOrderStatistics getD0CollectorOrderStatistics(@Param("sysUserId") Integer sysUserId,
                                                             @Param("days") Integer days,
                                                             @Param("sourceType") Integer sourceType,
                                                             @Param("array") BigDecimal[] amountApply,
                                                             @Param("otherArray") BigDecimal[] otherArray);


    @Select("<script> SELECT \n"
            + "    COUNT(CASE WHEN (c.id IS NOT NULL AND c.outsourceId != '0' )\n"
            + "        THEN o.id\n"
            + "        END) AS totalAssigned,\n"
            + "    COUNT(CASE WHEN  o.borrowingCount = 1 AND (c.id IS NOT NULL AND c.outsourceId != '0' )\n"
            + "          THEN o.id\n"
            + "        END) AS assignedReBorrowingCount0,\n"
            + "    COUNT(CASE WHEN o.borrowingCount <![CDATA[>]]> 1 AND (c.id IS NOT NULL AND c.outsourceId != '0' )\n"
            + "          THEN o.id\n"
            + "          END) AS assignedReBorrowingCountN,\n"
            + "    COUNT(CASE WHEN (c.id IS NULL OR c.outsourceId = '0' )\n"
            + "        THEN o.id\n"
            + "        END) AS totalUnAssigned,\n"
            + "    COUNT(CASE WHEN  o.borrowingCount = 1 AND (c.id IS NULL OR c.outsourceId = '0' )\n"
            + "          THEN o.id\n"
            + "        END) AS unAssignedReBorrowingCount0,\n"
            + "    COUNT(CASE WHEN o.borrowingCount <![CDATA[>]]> 1 AND (c.id IS NULL OR c.outsourceId = '0' )\n"
            + "          THEN o.id\n"
            + "          END) AS unAssignedReBorrowingCountN\n"
            + "FROM\n"
            + "    ordOrder o\n"
            + "        LEFT JOIN\n"
            + "    collectionOrderDetail c ON c.orderUUID = o.uuid and c.disabled =0 and c.sourceType = #{sourceType} \n"
            + "WHERE\n"
            + "    o.status in (7,8) and o.disabled =0 \n"
            + "        AND DATEDIFF(NOW(), o.refundTime) <![CDATA[>=]]>#{minOverdueDays}"
            + "        AND DATEDIFF(NOW(), o.refundTime) <![CDATA[<=]]>#{maxOverdueDays} "
            + "<choose><when test='otherArray != null and otherArray.length > 0'> and o.amountApply not in <foreach collection='otherArray' index='index' " +
            "item='item' open='(' separator=',' close=')'>#{item}</foreach></when> "
            + "<otherwise><if test='array != null and array.length > 0'> and o.amountApply in <foreach collection='array' index='index' " +
            "item='item1' open='(' separator=',' close=')'>#{item1}</foreach></if></otherwise></choose>"+
            "</script>")
    OverdueOrderStatistics getOverdueOrderStatistics(
            @Param("minOverdueDays") Integer minOverdueDays,
            @Param("maxOverdueDays") Integer maxOverdueDays,
            @Param("sourceType") Integer sourceType,
            @Param("array") BigDecimal[] amountApply,
            @Param("otherArray") BigDecimal[] otherArray);

    @Select("<script> SELECT \n"
            + "    COUNT(CASE WHEN (c.id IS NOT NULL AND c.outsourceId != '0' )\n"
            + "        THEN o.id\n"
            + "        END) AS totalAssigned,\n"
            + "    COUNT(CASE WHEN  o.borrowingCount = 1 AND (c.id IS NOT NULL AND c.outsourceId != '0' )\n"
            + "          THEN o.id\n"
            + "        END) AS assignedReBorrowingCount0,\n"
            + "    COUNT(CASE WHEN o.borrowingCount <![CDATA[>]]> 1 AND (c.id IS NOT NULL AND c.outsourceId != '0' )\n"
            + "          THEN o.id\n"
            + "          END) AS assignedReBorrowingCountN,\n"
            + "    COUNT(CASE WHEN (c.id IS NULL OR c.outsourceId = '0' )\n"
            + "        THEN o.id\n"
            + "        END) AS totalUnAssigned,\n"
            + "    COUNT(CASE WHEN  o.borrowingCount = 1 AND (c.id IS NULL OR c.outsourceId = '0' )\n"
            + "          THEN o.id\n"
            + "        END) AS unAssignedReBorrowingCount0,\n"
            + "    COUNT(CASE WHEN o.borrowingCount <![CDATA[>]]> 1 AND (c.id IS NULL OR c.outsourceId = '0' )\n"
            + "          THEN o.id\n"
            + "          END) AS unAssignedReBorrowingCountN\n"
            + "FROM\n"
            + "    ordOrder o\n"
            + "        LEFT JOIN\n"
            + "    collectionOrderDetail c ON c.orderUUID = o.uuid and c.disabled =0 and c.sourceType = #{sourceType} \n"
            + "WHERE\n"
            + "     o.disabled =0 \n"
            + "        AND DATEDIFF(NOW(), o.refundTime) <![CDATA[>=]]>#{minOverdueDays}"
            + "        AND DATEDIFF(NOW(), o.refundTime) <![CDATA[<=]]>#{maxOverdueDays} "
            + "<choose><when test='otherArray != null and otherArray.length > 0'> and o.amountApply not in <foreach collection='otherArray' index='index' " +
            "item='item' open='(' separator=',' close=')'>#{item}</foreach></when> "
            + "<otherwise><if test='array != null and array.length > 0'> and o.amountApply in <foreach collection='array' index='index' " +
            "item='item1' open='(' separator=',' close=')'>#{item1}</foreach></if></otherwise></choose>"+
            "</script>")
    OverdueOrderStatistics getOverdueOrderStatisticsCheck(
            @Param("minOverdueDays") Integer minOverdueDays,
            @Param("maxOverdueDays") Integer maxOverdueDays,
            @Param("sourceType") Integer sourceType,
            @Param("array") BigDecimal[] amountApply,
            @Param("otherArray") BigDecimal[] otherArray);


    @Select("<script> SELECT "
            + "    COUNT(CASE WHEN  o.borrowingCount = 1 AND (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "          THEN o.id"
            + "        END) AS assignedReBorrowingCount0,"
            + "    COUNT(CASE WHEN o.borrowingCount > 1 AND (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "          THEN o.id"
            + "          END) AS assignedReBorrowingCountN "
            + "FROM"
            + "    ordOrder o "
            + "        JOIN "
            + "    collectionOrderDetail c ON c.orderUUID = o.uuid and c.disabled =0 and c.outsourceId = #{outSourceId} and c.sourceType = #{sourceType} "
            + "WHERE "
            + "    o.status in (7,8) and o.disabled =0 "
            + "    <![CDATA[ AND DATEDIFF(NOW(), o.refundTime) >=]]>#{minOverdueDays}"
            + "    <![CDATA[ AND DATEDIFF(NOW(), o.refundTime) <=]]>#{maxOverdueDays} "
            + "<choose><when test='otherArray != null and otherArray.length > 0'> and o.amountApply not in <foreach collection='otherArray' index='index' " +
            "item='item' open='(' separator=',' close=')'>#{item}</foreach></when> "
            + "<otherwise><if test='array != null and array.length > 0'> and o.amountApply in <foreach collection='array' index='index' " +
            "item='item1' open='(' separator=',' close=')'>#{item1}</foreach></if></otherwise></choose>"+
            "</script>")
    OverdueCollectorOrderStatistics getOverdueCollectorOrderStatistics(
            @Param("minOverdueDays") Integer minOverdueDays,
            @Param("maxOverdueDays") Integer maxOverdueDays,
            @Param("outSourceId") Integer outSourceId,
            @Param("sourceType") Integer sourceType,
            @Param("array") BigDecimal[] amountApply,
            @Param("otherArray") BigDecimal[] otherArray);

    @Select("<script> SELECT "
            + "    COUNT(CASE WHEN  o.borrowingCount = 1 AND (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "          THEN o.id"
            + "        END) AS assignedReBorrowingCount0,"
            + "    COUNT(CASE WHEN o.borrowingCount > 1 AND (c.id IS NOT NULL AND c.outsourceId != '0' ) "
            + "          THEN o.id"
            + "          END) AS assignedReBorrowingCountN "
            + "FROM"
            + "    ordOrder o "
            + "        JOIN "
            + "    collectionOrderDetail c ON c.orderUUID = o.uuid and c.disabled =0 and c.outsourceId = #{outSourceId} and c.sourceType = #{sourceType} "
            + "WHERE "
            + "     o.disabled =0 "
            + "    <![CDATA[ AND DATEDIFF(NOW(), o.refundTime) >=]]>#{minOverdueDays}"
            + "    <![CDATA[ AND DATEDIFF(NOW(), o.refundTime) <=]]>#{maxOverdueDays} "
            + "<choose><when test='otherArray != null and otherArray.length > 0'> and o.amountApply not in <foreach collection='otherArray' index='index' " +
            "item='item' open='(' separator=',' close=')'>#{item}</foreach></when> "
            + "<otherwise><if test='array != null and array.length > 0'> and o.amountApply in <foreach collection='array' index='index' " +
            "item='item1' open='(' separator=',' close=')'>#{item1}</foreach></if></otherwise></choose>"+
            "</script>")
    OverdueCollectorOrderStatistics getOverdueCollectorOrderStatisticsCheck(
            @Param("minOverdueDays") Integer minOverdueDays,
            @Param("maxOverdueDays") Integer maxOverdueDays,
            @Param("outSourceId") Integer outSourceId,
            @Param("sourceType") Integer sourceType,
            @Param("array") BigDecimal[] amountApply,
            @Param("otherArray") BigDecimal[] otherArray);


    @SelectProvider(type = CollectionSqlProvider.class, method = "getAssignableCollectionOrdersWithParam")
    List<String> getAssignableCollectionOrdersWithParam(
            @Param("status") Integer status,
            @Param("borrowingCountMin") Integer borrowingCountMin,
            @Param("borrowingCountMax") Integer borrowingCountMax,
            @Param("minOverdueDays") Integer minOverdueDays,
            @Param("maxOverdueDays") Integer maxOverdueDays,
            @Param("sourceType") Integer sourceType,
            @Param("amountApply") String amountApply,
            @Param("otherAmount") String otherAmount);

    @SelectProvider(type = CollectionSqlProvider.class, method = "getAssignableCollectionOrdersWithParam1")
    List<String> getAssignableCollectionOrdersWithParam1(
            @Param("status") Integer status,
            @Param("outSourceId") Integer outSourceId,
            @Param("borrowingCountMin") Integer borrowingCountMin,
            @Param("borrowingCountMax") Integer borrowingCountMax,
            @Param("sourceType") Integer sourceType,
            @Param("amountApply") String amountApply,
            @Param("otherAmount") String otherAmount);

    @Select("SELECT \n"
            + "    COUNT(CASE WHEN (c.id IS NOT NULL AND c.subOutSourceId != '0' )\n"
            + "        THEN o.id\n"
            + "        END) AS totalAssigned,\n"
            + "    COUNT(CASE WHEN  o.borrowingCount = 1 AND (c.id IS NOT NULL AND c.subOutSourceId != '0' )\n"
            + "          THEN o.id\n"
            + "        END) AS assignedReBorrowingCount0,\n"
            + "    COUNT(CASE WHEN o.borrowingCount > 1 AND (c.id IS NOT NULL AND c.subOutSourceId != '0' )\n"
            + "          THEN o.id\n"
            + "          END) AS assignedReBorrowingCountN,\n"
            + "    COUNT(CASE WHEN (c.id IS NULL OR c.subOutSourceId = '0' )\n"
            + "        THEN o.id\n"
            + "        END) AS totalUnAssigned,\n"
            + "    COUNT(CASE WHEN  o.borrowingCount = 1 AND (c.id IS NULL OR c.subOutSourceId = '0' )\n"
            + "          THEN o.id\n"
            + "        END) AS unAssignedReBorrowingCount0,\n"
            + "    COUNT(CASE WHEN o.borrowingCount > 1 AND (c.id IS NULL OR c.subOutSourceId = '0' )\n"
            + "          THEN o.id\n"
            + "          END) AS unAssignedReBorrowingCountN\n"
            + "FROM\n"
            + "    ordOrder o\n"
            + "        LEFT JOIN\n"
            + "    collectionOrderDetail c ON c.orderUUID = o.uuid and c.disabled =0 and c.sourceType = #{sourceType} \n"
            + "WHERE\n"
            + "    o.status in (7,8) and o.disabled =0 and c.outSourceId = #{outSourceId}")
    OverdueOrderStatistics getOutSourceOverdueOrderStatistics(
            @Param("outSourceId") Integer outSourceId, @Param("sourceType") Integer sourceType);


    @Select("SELECT \n"
            + "    COUNT(CASE WHEN  o.borrowingCount = 1 AND (c.id IS NOT NULL AND c.subOutSourceId != '0' )\n"
            + "          THEN o.id\n"
            + "        END) AS assignedReBorrowingCount0,\n"
            + "    COUNT(CASE WHEN o.borrowingCount > 1 AND (c.id IS NOT NULL AND c.subOutSourceId != '0' )\n"
            + "          THEN o.id\n"
            + "          END) AS assignedReBorrowingCountN\n"
            + "FROM\n"
            + "    ordOrder o\n"
            + "        LEFT JOIN\n"
            + "    collectionOrderDetail c ON c.orderUUID = o.uuid and c.disabled =0 and c.subOutSourceId = #{outSourceId} and c.sourceType = #{sourceType} \n"
            + "WHERE\n"
            + "    o.status in (7,8) and o.disabled = 0 ")
    OverdueCollectorOrderStatistics getOutSourceOverdueCollectorOrderStatistics(
            @Param("outSourceId") Integer outSourceId, @Param("sourceType") Integer sourceType);

    @Select("select score.userUuid as userUuid ,count(1) as count, sum(score.serviceMentality) as serviceMentality , sum(score.communicationBility) as communicationBility " +
            "from doit.usrEvaluateScore score  where score.disabled=0 and score.type = 1 and score.postId =#{postId} group by 1")
    List<CollectorScoreResponse> getTotalScore(@Param("postId") Integer postId);

    @Select("select usr.realName as userName, score.createTime as createTime, manUser.userName as userUuid , item.dicItemName as postId, score.serviceMentality as serviceMentality, score.communicationBility as communicationBility\n" +
            "from doit.usrEvaluateScore score join doit.usrUser usr on usr.id = score.createUser and score.disabled = 0 join doit.manUser manUser on manUser.disabled = 0 and manUser.uuid = score.userUuid join sysDicItem item on item.disabled =0 and item.id = score.postId \n" +
            "where score.type = 1 and score.postId = #{postId} order by manUser.username, createTime desc ")
    List<CollectorScoreResponse> getDetailScore(@Param("postId") Integer postId);

    @SelectProvider(type = CollectionSqlProvider.class, method = "secondQualityCheck")
    @Options(useGeneratedKeys = true)
    List<CollectionOrderResponse> secondQualityCheck(@Param("AssignableCollectionOrderReq") AssignableCollectionOrderReq request);
}
