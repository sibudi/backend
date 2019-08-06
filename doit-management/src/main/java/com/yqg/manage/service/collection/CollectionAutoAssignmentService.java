package com.yqg.manage.service.collection;

//import com.sun.tools.javac.util.Convert;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.utils.JsonUtils;
import com.yqg.manage.dal.collection.CollectionOrderDetailDao;
import com.yqg.manage.dal.collection.ManCollectionDao;
import com.yqg.manage.dal.collection.ManReceiverSchedulingDao;
import com.yqg.manage.enums.DictCollectionEnum;
import com.yqg.manage.service.check.ManReceiverSchedulingService;
import com.yqg.manage.service.check.request.ReceiverSchedulingRequest;
import com.yqg.manage.service.collection.request.D0AutoAssignmentRequest;
import com.yqg.manage.service.collection.request.OverdueAssignmentStatisticsRequest;
import com.yqg.manage.service.collection.request.OverdueAutoAssignmentRequest;
import com.yqg.manage.service.collection.response.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import com.yqg.management.dao.CollectionOrderHistoryDao;
import com.yqg.management.entity.CollectionOrderHistory;
import com.yqg.management.entity.ReceiverScheduling;
import com.yqg.service.system.request.DictionaryRequest;
import com.yqg.service.system.response.SysDicItemModel;
import com.yqg.service.system.service.SysDicItemService;
import com.yqg.service.system.service.SysDicService;
import com.yqg.system.dao.SysDicDao;
import com.yqg.system.entity.SysDic;
import com.yqg.system.dao.SysDicItemDao;
import com.yqg.system.entity.SysDicItem;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.util.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 *
 * ?????????
 ****/

@Service
@Slf4j
public class CollectionAutoAssignmentService {

    private Logger logger = LoggerFactory.getLogger(CollectionAutoAssignmentService.class);

    @Autowired
    private ManCollectionDao manCollectionDao;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private SysDicService sysDicService;

    private String collectionApplys;

    @Autowired
//    @Qualifier("managementExecutorService")
    private ExecutorService executorService;

    @Autowired
    private CollectionOrderDetailDao collectionOrderDetailDao;

    @Autowired
    private CollectionOrderHistoryDao collectionOrderHistoryDao;


    private String getAmoutStr () {
        DictionaryRequest dictionaryRequest = new DictionaryRequest();
        dictionaryRequest.setDicCode("cash_amount");
        List<SysDicItemModel> sysDicItems = null;
        try {
            sysDicItems = sysDicService.dicItemListByDicCode(dictionaryRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (CollectionUtils.isEmpty(sysDicItems)) {
            return null;
        }
        return  org.apache.commons.lang3.StringUtils.join(sysDicItems.stream()
                .map(SysDicItemModel :: getDicItemValue).collect(Collectors.toList()), ",");
    }

    /**
     * 查出所有不在 amountApply数组中的金额
     *
     * @param amountApply
     * @param otherAmount
     * @return
     */
    public BigDecimal[] getOtherAmount(BigDecimal[] amountApply, Integer otherAmount) {

        if (otherAmount == null || otherAmount.equals(0)) {
            return null;
        }
        collectionApplys = getAmoutStr();
        String[] all = collectionApplys.split(",");
        BigDecimal[] result = new BigDecimal[all.length - amountApply.length];
        int index = 0;
        for (String s : all) {
            boolean flag = false;
            for (BigDecimal d : amountApply) {
                if (d.equals(BigDecimal.valueOf(Long.parseLong(s)))) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                result[index] = BigDecimal.valueOf(Long.parseLong(s));
                index ++;
            }
        }
        logger.info("getOtherAmount length is " + result.length);
        return result;
    }
    public String getOtherAmountStr(BigDecimal[] amountApply, Integer otherAmount) {

        if (otherAmount == null || otherAmount.equals(0)) {
            return null;
        }
        collectionApplys = getAmoutStr();
        String[] all = collectionApplys.split(",");
        StringBuffer result = new StringBuffer();
        for (String s : all) {
            boolean flag = false;
            for (BigDecimal d : amountApply) {
                if (d.equals(BigDecimal.valueOf(Long.parseLong(s)))) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                result.append(s).append(",");
            }
        }
        String s = result.toString();
        if (!StringUtils.isEmpty(s)) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public D0CollectionStatisticsResponse getD0CollectionStatistics(OverdueAssignmentStatisticsRequest request) {
        if (StringUtils.isEmpty(request.getSection())) {
            log.info("getD0CollectionStatistics param is null");
            return null;
        }
        D0CollectionStatisticsResponse response = new D0CollectionStatisticsResponse();
        String daysStr = request.getSection().split("#")[0];

        response.setOrderStatistics(manCollectionDao.getD0CollectionOrderStatistics(Integer.valueOf(daysStr),
                request.getSourceType(), request.getAmountApply(), getOtherAmount(request.getAmountApply(), request.getOtherAmount())));

        //查询d0,d-1或者D-2所有人员
        List<CollectionPostResponse> collectors = collectionService.getCurrentCollectors(request.getSourceType());
        if (CollectionUtils.isEmpty(collectors)) {
            log.warn("no collectors info ");
            return response;
        }
        String postStr = "D0";
        Integer daysTmp = 0;
        if ("-1".equals(daysStr)) {
            postStr = "D-1";
            daysTmp = -1;
        } else if ("-2".equals(daysStr)) {
            postStr = "D-2";
            daysTmp = -2;
        }
        final String str = postStr;
        final Integer days = daysTmp;

        Optional<CollectionPostResponse> d0Collectors = collectors.stream()
                .filter(elem -> str.equals(elem.getPostName())).findFirst();
        if (!d0Collectors.isPresent() || CollectionUtils.isEmpty(d0Collectors.get().getStaff())) {
            log.warn("no D0 collectors info ");
            return response;
        }

        List<D0CollectorOrderStatistics> collectorStatisticsList = new ArrayList<>();
        response.setCollectorOrderStatistics(collectorStatisticsList);
        d0Collectors.get().getStaff().forEach(elem -> {
            D0CollectorOrderStatistics statistics = manCollectionDao
                    .getD0CollectorOrderStatistics(Integer.valueOf(elem.getCode()), days, request.getSourceType(),
                            request.getAmountApply(), getOtherAmount(request.getAmountApply(), request.getOtherAmount()));
            statistics.setUserId(Integer.valueOf(elem.getCode()));
            statistics.setRealName(elem.getName());
            collectorStatisticsList.add(statistics);
        });
        return response;
    }


    public OverdueCollectionStatisticsResponse getOverdueAssignmentStatistics(Integer postId,
                                                                              String overduePeriod,Integer sourceType,
                                                                              BigDecimal[] amountApply,Integer otherAmount) {

        Integer minOverdueDays = Integer.valueOf(overduePeriod.split("#")[0]);
        Integer maxOverdueDays = Integer.valueOf(overduePeriod.split("#")[1]);

        OverdueCollectionStatisticsResponse response = new OverdueCollectionStatisticsResponse();
        response.setOrderStatistics(
                manCollectionDao.getOverdueOrderStatistics(minOverdueDays, maxOverdueDays, sourceType, amountApply,getOtherAmount(amountApply, otherAmount)));
        List<OverdueCollectorOrderStatistics> collectorStatisticsList = new ArrayList<>();
        response.setCollectorOrderStatistics(collectorStatisticsList);
        //处理postId对应的用户
        List<CollectorResponseInfo> collectors = collectionService.getCollectorsByPostId(postId, sourceType);
        if (CollectionUtils.isEmpty(collectors)) {
            log.warn("no collectors info with postId = {}", postId);
            return response;
        }


        collectors.forEach(elem -> {
            OverdueCollectorOrderStatistics statistics = manCollectionDao
                    .getOverdueCollectorOrderStatistics(minOverdueDays, maxOverdueDays,
                            Integer.valueOf(elem.getCode()), sourceType, amountApply, getOtherAmount(amountApply, otherAmount));
            statistics.setUserId(Integer.valueOf(elem.getCode()));
            statistics.setRealName(elem.getName());
            collectorStatisticsList.add(statistics);
        });

        return response;
    }


    /***
     * d0订单催收自动分配
     * @param request
     */

    public void autoAssignCollectionOrderForD0(D0AutoAssignmentRequest request) {
        if (!checkD0AutoAssignmentRequestParam(request)) {
            return;
        }

        //放入线程池中异步处理
        executorService
                .submit(() -> Arrays.asList(D0AssignmentType.values()).stream().forEach(elem ->
                        assignD0CollectionOrderByType(request.getCollectorAssignmentRequest(), elem,
                                request.getSection(), request.getSourceType(), request.getAmountApply(), request.getOtherAmount()))
                );

    }


    /***
     * 逾期订单催收自动分配
     * @param request
     */
    public void autoAssignCollectionOrderForOverdue(OverdueAutoAssignmentRequest request) {
        if (!checkOverdueAutoAssignmentRequestParam(request)) {
            log.warn("the input param is not valid for overdue auto assignment, param= {}",
                    JsonUtils.serialize(request));
            return;
        }
        String section = request.getSection();
        Integer minOverdueDays = Integer.valueOf(section.split("#")[0]);
        Integer maxOverdueDays = Integer.valueOf(section.split("#")[1]);

        //放入线程池中处理(处理时间较长，避免用户页面一直等待)
        executorService
                .submit(() -> Arrays.asList(OverdueAssignmentType.values()).stream().forEach(elem ->
                        assignOverdueCollectionOrderByType(request.getCollectorAssignmentRequest(), elem,
                                minOverdueDays, maxOverdueDays, request.getIsThird(),
                                request.getOutSourceId(), request.getSourceType(),
                                request.getAmountApply(),request.getOtherAmount())
                ));
    }







    private void assignD0CollectionOrderByType(
            List<D0CollectorOrderStatistics> collectorRequestParamList,
            D0AssignmentType type, String section, Integer sourceType, BigDecimal[] amountApply, Integer otherAmount) {

        if (StringUtils.isEmpty(section)
                || section.split("#").length != 2) {
            return ;
        }
        StringBuffer amounts = new StringBuffer("");
        String amoutStr = "";
        if (amountApply != null && amountApply.length > 0) {
            for (BigDecimal decimal : amountApply) {
                amounts.append(decimal).append(",");
            }
            amoutStr = amounts.substring(0, amounts.length() - 1);
        }
        Integer minOverdueDays = Integer.valueOf(section.split("#")[0]);
        Integer maxOverdueDays = Integer.valueOf(section.split("#")[1]);
        List<String> assignableList = manCollectionDao.getAssignableCollectionOrdersWithParam(
                OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode(), type.getMinBorrowingCount(),
                type.getMaxBorrowingCount(), minOverdueDays, maxOverdueDays, sourceType, amoutStr, getOtherAmountStr(amountApply, otherAmount));
        Map<String, AssignableOrderParamPair> assignableOrderMap = getD0AssignableOrderParamPairMap(
                collectorRequestParamList, type);
        if (CollectionUtils.isEmpty(assignableOrderMap)) {
            return;
        }
        //分配
        for (D0CollectorOrderStatistics elem : collectorRequestParamList) {
            AssignableOrderParamPair paramPair = assignableOrderMap
                    .get(elem.getUserId().toString());
            if (paramPair == null) {
                continue;
            }
            if (paramPair.getStartIndex() >= assignableList.size()) {
                //超过了待分配的数量
                break;
            }
            if (paramPair.getEndIndex() > assignableList.size()) {
                paramPair.setEndIndex(assignableList.size());
            }
            List<String> availableList = assignableList
                    .subList(paramPair.getStartIndex(), paramPair.getEndIndex());

            assignCollectionOrders(availableList, elem.getUserId(), false, sourceType);
        }

    }


    /***
     * d0自动分配入参检查
     * @param request
     * @return
     */
    private boolean checkD0AutoAssignmentRequestParam(D0AutoAssignmentRequest request) {
        if (CollectionUtils.isEmpty(request.getCollectorAssignmentRequest())) {
            log.warn("the input param is empty");
            return false;
        }
        int unAssignedCount0Sum = request.getCollectorAssignmentRequest().stream()
                .filter(elem -> elem.getUnAssignedReBorrowingCount0() != null)
                .mapToInt(D0CollectorOrderStatistics::getUnAssignedReBorrowingCount0).sum();
        int unAssignedCount1Sum = request.getCollectorAssignmentRequest().stream()
                .filter(elem -> elem.getUnAssignedReBorrowingCount1() != null)
                .mapToInt(D0CollectorOrderStatistics::getUnAssignedReBorrowingCount1).sum();
        int unAssignedCount2Sum = request.getCollectorAssignmentRequest().stream()
                .filter(elem -> elem.getUnAssignedReBorrowingCount2() != null)
                .mapToInt(D0CollectorOrderStatistics::getUnAssignedReBorrowingCount2).sum();

        int unAssignedCount3Sum = request.getCollectorAssignmentRequest().stream()
                .filter(elem -> elem.getUnAssignedReBorrowingCount3() != null)
                .mapToInt(D0CollectorOrderStatistics::getUnAssignedReBorrowingCount3).sum();

        int unAssignedCountNSum = request.getCollectorAssignmentRequest().stream()
                .filter(elem -> elem.getUnAssignedReBorrowingCountN() != null)
                .mapToInt(D0CollectorOrderStatistics::getUnAssignedReBorrowingCountN).sum();
        if (0 == unAssignedCount0Sum && 0 == unAssignedCount1Sum && 0 == unAssignedCount2Sum
                && 0 == unAssignedCount3Sum && 0 == unAssignedCountNSum) {
            log.warn("the input param is not correct, param = {}", JsonUtils.serialize(request));
            return false;
        }

        Optional<D0CollectorOrderStatistics> lessThanZeroRequestData = request
                .getCollectorAssignmentRequest().stream().filter(
                        elem -> !elem.isElementValid()).findFirst();

        if (lessThanZeroRequestData.isPresent()) {
            log.warn("the input number can not less then zero");
            return false;
        }
        return true;
    }



    /***
     * 逾期自动分配入参检查
     * @param request
     * @return
     */
    private boolean checkOverdueAutoAssignmentRequestParam(OverdueAutoAssignmentRequest request) {
        if (CollectionUtils.isEmpty(request.getCollectorAssignmentRequest())) {
            log.warn("the input param is empty");
            return false;
        }
        int unAssignedCount0Sum = request.getCollectorAssignmentRequest().stream()
                .filter(elem -> elem.getUnAssignedReBorrowingCount0() != null)
                .mapToInt(OverdueCollectorOrderStatistics::getUnAssignedReBorrowingCount0).sum();


        int unAssignedCountNSum = request.getCollectorAssignmentRequest().stream()
                .filter(elem -> elem.getUnAssignedReBorrowingCountN() != null)
                .mapToInt(OverdueCollectorOrderStatistics::getUnAssignedReBorrowingCountN).sum();
        if (0 == unAssignedCount0Sum && 0 == unAssignedCountNSum) {
            log.warn("the input param is not correct, param = {}", JsonUtils.serialize(request));
            return false;
        }

        Optional<OverdueCollectorOrderStatistics> lessThanZeroRequestData = request
                .getCollectorAssignmentRequest().stream().filter(
                        elem -> !elem.isElementValid()).findFirst();

        if (lessThanZeroRequestData.isPresent()) {
            log.warn("the input number can not less then zero");
            return false;
        }
        return true;
    }



    /***
     * 获取每个人的分配订单在所有的待分配订单中的顺序
     * @param request
     * @param type :0 ,1,2,3,N 对应复借次数
     * @return
     */
    private Map<String, AssignableOrderParamPair> getD0AssignableOrderParamPairMap(
            List<D0CollectorOrderStatistics> request, D0AssignmentType type) {
        Map<String, AssignableOrderParamPair> assignableOrderMap = new HashMap<>();
        int start = 0;
        List<D0CollectorOrderStatistics> filteredList = null;
        switch (type) {
            case RE_BORROWING_0:
                filteredList = request.stream().filter(
                        elem -> elem.getUnAssignedReBorrowingCount0() != null
                                && elem.getUnAssignedReBorrowingCount0() >= 1).collect(
                        Collectors.toList());
                break;
            case RE_BORROWING_1:
                filteredList = request.stream().filter(
                        elem -> elem.getUnAssignedReBorrowingCount1() != null
                                && elem.getUnAssignedReBorrowingCount1() >= 1).collect(
                        Collectors.toList());
                break;
            case RE_BORROWING_2:
                filteredList = request.stream().filter(
                        elem -> elem.getUnAssignedReBorrowingCount2() != null
                                && elem.getUnAssignedReBorrowingCount2() >= 1).collect(
                        Collectors.toList());
                break;
            case RE_BORROWING_3:
                filteredList = request.stream().filter(
                        elem -> elem.getUnAssignedReBorrowingCount3() != null
                                && elem.getUnAssignedReBorrowingCount3() >= 1).collect(
                        Collectors.toList());
                break;
            case RE_BORROWING_N:
                filteredList = request.stream().filter(
                        elem -> elem.getUnAssignedReBorrowingCountN() != null
                                && elem.getUnAssignedReBorrowingCountN() >= 1).collect(
                        Collectors.toList());
                break;
            default:
        }

        if (CollectionUtils.isEmpty(filteredList)) {
            return assignableOrderMap;
        }
        for (D0CollectorOrderStatistics elem : request) {
            AssignableOrderParamPair paramPair = new AssignableOrderParamPair();
            paramPair.setStartIndex(start);

            switch (type) {
                case RE_BORROWING_0:
                    start += elem.getUnAssignedReBorrowingCount0();
                    break;
                case RE_BORROWING_1:
                    start += elem.getUnAssignedReBorrowingCount1();
                    break;
                case RE_BORROWING_2:
                    start += elem.getUnAssignedReBorrowingCount2();
                    break;
                case RE_BORROWING_3:
                    start += elem.getUnAssignedReBorrowingCount3();
                    break;
                case RE_BORROWING_N:
                    start += elem.getUnAssignedReBorrowingCountN();
                    break;
                default:
            }


            paramPair.setEndIndex(start);
            assignableOrderMap.put(elem.getUserId().toString(), paramPair);
        }
        return assignableOrderMap;
    }

    private void assignOverdueCollectionOrderByType(
            List<OverdueCollectorOrderStatistics> collectorRequestParamList,
            OverdueAssignmentType type,Integer minOverdueDays, Integer maxOverdueDays,
            Boolean isThird, Integer outSourceId, Integer sourceType, BigDecimal[] amountApply, Integer otherAmount) {

        List<String> assignableList ;

        StringBuffer amounts = new StringBuffer("");
        String amoutStr = "";
        if (amountApply != null && amountApply.length > 0) {
            for (BigDecimal decimal : amountApply) {
                amounts.append(decimal).append(",");
            }
            amoutStr = amounts.substring(0, amounts.length() - 1);
        }
        if (isThird) {
            assignableList = manCollectionDao.getAssignableCollectionOrdersWithParam1(
                    OrdStateEnum.RESOLVING_OVERDUE.getCode(), outSourceId, type.getMinBorrowingCount(),
                    type.getMaxBorrowingCount(), sourceType, amoutStr, getOtherAmountStr(amountApply, otherAmount));

        } else {
            assignableList = manCollectionDao.getAssignableCollectionOrdersWithParam(
                    OrdStateEnum.RESOLVING_OVERDUE.getCode(), type.getMinBorrowingCount(),
                    type.getMaxBorrowingCount(), minOverdueDays, maxOverdueDays, sourceType, amoutStr,getOtherAmountStr(amountApply, otherAmount));
            //如果是分配质检，D0一下的情况
            if (sourceType.equals(1) && minOverdueDays <= 0) {
                assignableList.addAll(manCollectionDao.getAssignableCollectionOrdersWithParam(
                        OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode(), type.getMinBorrowingCount(),
                        type.getMaxBorrowingCount(), minOverdueDays, maxOverdueDays, sourceType, amoutStr, getOtherAmountStr(amountApply, otherAmount)));

            }
        }

        Map<String, AssignableOrderParamPair> assignableOrderMap = getOverdueAssignableOrderParamPairMap(
                collectorRequestParamList, type);
        if (CollectionUtils.isEmpty(assignableOrderMap)) {
            return;
        }
        List<Integer> outsourceIdList = collectorRequestParamList.stream()
                .mapToInt(OverdueCollectorOrderStatistics::getUserId).boxed().collect(Collectors.toList());

        List<String> finalAssignableList = assignableList;
        outsourceIdList.stream().forEach(elem -> {
            AssignableOrderParamPair paramPair = assignableOrderMap
                    .get(elem.toString());
            if (paramPair == null) {
                return;
            }
            if (paramPair.getStartIndex() >= finalAssignableList.size()) {
                //超过了待分配的数量
                log.warn("the input num exceed actual available orders, param= {}",JsonUtils.serialize(collectorRequestParamList));
                return;
            }
            if (paramPair.getEndIndex() > finalAssignableList.size()) {
                paramPair.setEndIndex(finalAssignableList.size());
            }
            List<String> availableList = finalAssignableList
                    .subList(paramPair.getStartIndex(), paramPair.getEndIndex());

            assignCollectionOrders(availableList, elem, isThird, sourceType);
        });


    }


    /***
     * 获取每个人的分配订单在所有的待分配订单中的顺序
     * @param request
     * @param type :0 ,1,2,3,N 对应复借次数
     * @return
     */
    private Map<String, AssignableOrderParamPair> getOverdueAssignableOrderParamPairMap(
            List<OverdueCollectorOrderStatistics> request, OverdueAssignmentType type) {
        Map<String, AssignableOrderParamPair> assignableOrderMap = new HashMap<>();

        List<OverdueCollectorOrderStatistics> filteredList = null;
        switch (type) {
            case RE_BORROWING_0:
                filteredList = request.stream().filter(
                        elem -> elem.getUnAssignedReBorrowingCount0() != null
                                && elem.getUnAssignedReBorrowingCount0() >= 1).collect(
                        Collectors.toList());
                break;
            case RE_BORROWING_N:
                filteredList = request.stream().filter(
                        elem -> elem.getUnAssignedReBorrowingCountN() != null
                                && elem.getUnAssignedReBorrowingCountN() >= 1).collect(
                        Collectors.toList());
                break;
            default:
        }

        if (CollectionUtils.isEmpty(filteredList)) {
            return assignableOrderMap;
        }
        int start = 0;
        for (OverdueCollectorOrderStatistics elem : filteredList) {
            AssignableOrderParamPair paramPair = new AssignableOrderParamPair();
            paramPair.setStartIndex(start);
            switch (type) {
                case RE_BORROWING_0:
                    start += elem.getUnAssignedReBorrowingCount0();
                    break;
                case RE_BORROWING_N:
                    start += elem.getUnAssignedReBorrowingCountN();
                    break;
                default:
            }
            paramPair.setEndIndex(start);
            assignableOrderMap.put(elem.getUserId().toString(), paramPair);
        }
        return assignableOrderMap;
    }





    private void assignCollectionOrders(List<String> orderUUIDs, Integer outsourceId,
                                        Boolean isThird, Integer sourceType) {
        if (CollectionUtils.isEmpty(orderUUIDs)) {
            return;
        }
        orderUUIDs.stream()
                .forEach(elem -> collectionService.assignCollectionOrder(outsourceId, elem, isThird, sourceType));
    }

    @Getter
    @Setter
    public static class AssignableOrderParamPair {

        private int startIndex;
        private int endIndex;
    }

    @Getter
    public enum D0AssignmentType {
        RE_BORROWING_0(1, 1),
        RE_BORROWING_1(2, 2),
        RE_BORROWING_2(3, 3),
        RE_BORROWING_3(4, 4),
        RE_BORROWING_N(5, null);

        D0AssignmentType(Integer min, Integer max) {
            this.minBorrowingCount = min;
            this.maxBorrowingCount = max;
        }

        private Integer minBorrowingCount;
        private Integer maxBorrowingCount;
    }


    @Getter
    public enum OverdueAssignmentType {
        RE_BORROWING_0(1, 1),
        RE_BORROWING_N(2, null);

        OverdueAssignmentType(Integer min, Integer max) {
            this.minBorrowingCount = min;
            this.maxBorrowingCount = max;
        }

        private Integer minBorrowingCount;
        private Integer maxBorrowingCount;
    }

    /**
     * 回收催收未还款订单
     */
    @Transactional
    public void recycleCollectionOrder () {

        String stages = getOrderStage();
        List<String> orderIds = collectionOrderDetailDao.getRecycleCollectionOrder(stages);
        if (CollectionUtils.isEmpty(orderIds)) {
            logger.info("no orders need recycle");
            return ;
        }
        //插入到collectionOrderHistory
        for (String orderNo : orderIds) {
            CollectionOrderHistory history = new CollectionOrderHistory();
            history.setOrderUUID(orderNo);
            history.setSourceType(0);
            collectionOrderHistoryDao.insert(history);
        }

        //直接更新订单为未分配状态
        collectionOrderDetailDao.recycleCollectionOrder(stages);
    }

    /**
     *
     */
    @Transactional
    public void recycleQualityOrder () {

        String stages = getOrderStage();
        List<String> orderIds = collectionOrderDetailDao.getRecycleQualityOrder(stages);
        if (CollectionUtils.isEmpty(orderIds)) {
            logger.info("no orders need recycle");
            return ;
        }
        //插入到collectionOrderHistory
        for (String orderNo : orderIds) {
            CollectionOrderHistory history = new CollectionOrderHistory();
            history.setOrderUUID(orderNo);
            history.setSourceType(1);
            collectionOrderHistoryDao.insert(history);
        }

        //直接更新订单为未分配状态
        collectionOrderDetailDao.recycleQualityOrder(stages);
    }


    /**
     * 查询委外催收自动分配情况
     */
    public OverdueCollectionStatisticsResponse getOutSourceOverdueAssignmentStatistics(
            Integer outSourceId, Integer sourceType) throws Exception {

        if (outSourceId == null) {
            return null;
        }

        OverdueCollectionStatisticsResponse response = new OverdueCollectionStatisticsResponse();
        response.setOrderStatistics(
                manCollectionDao.getOutSourceOverdueOrderStatistics(outSourceId, sourceType));
        List<OverdueCollectorOrderStatistics> collectorStatisticsList = new ArrayList<>();
        response.setCollectorOrderStatistics(collectorStatisticsList);
        //获得催收公司对应的人员
        List<SysDicItemModel> dicItemList = sysDicService.
                sysDicItemsListByDicCode(DictCollectionEnum.THIRD_COMPANY.name() +
                        "_" + String.valueOf(outSourceId));
        if (CollectionUtils.isEmpty(dicItemList)) {
            return response;
        }

        List<CollectorResponseInfo> collectors = dicItemList.stream().map(elem -> new CollectorResponseInfo(
                elem.getDicItemValue(), elem.getDicItemName())).collect(Collectors.toList());

        collectors.forEach(elem -> {
            OverdueCollectorOrderStatistics statistics = manCollectionDao
                    .getOutSourceOverdueCollectorOrderStatistics(Integer.valueOf(elem.getCode()), sourceType);
            statistics.setUserId(Integer.valueOf(elem.getCode()));
            statistics.setRealName(elem.getName());
            collectorStatisticsList.add(statistics);
        });

        return response;
    }


    @Autowired
    private ManReceiverSchedulingService manReceiverSchedulingService;

    @Autowired
    private SysDicDao sysDicDao;
    @Autowired
    private SysDicItemService sysDicItemService;

    /**
    * @Description: 系统自动分配
    * @Param: []
    * @return: void
    * @Author: 许金泉
    * @Date: 2019/4/17 16:32
    */
    public void systemAutoAssignment(){
        //来源 0 催收 1.质检
        final Integer sourceType = 0;

        collectionApplys = getAmoutStr();
        // 需要 催收的金额
        BigDecimal[] amountApply = new BigDecimal[30];
        amountApply = Arrays.asList(getAmoutStr().split(",")).stream()
                .map(elem -> new BigDecimal(elem)).collect(Collectors.toList()).toArray(amountApply);

        log.info("total amountApply is {}:", amountApply.length);
        //查询所有催收岗位
        List<CollectionPostResponse> collectors = collectionService.getCurrentCollectors(sourceType);
        // 查询的所有正在排班的催收人员
//        List<ReceiverScheduling> receiverSchedulingList = manReceiverSchedulingService.getLastReceiverSchedulingList();

        // 所有委外催收公司
        List<SysDic> thirdCompany = sysDicDao.getThirdCompany();

        collectors.forEach(c->{

            // 如果存在委外公司，则查询委外公司下面的帐号
            List<CollectorResponseInfo> collect =  c.getStaff().stream().flatMap(m->{
                String dicCode = DictCollectionEnum.THIRD_COMPANY.name() + "_" + String.valueOf(m.getCode());
                Optional<SysDic> first = thirdCompany.stream().filter(u -> u.getDicCode().equalsIgnoreCase(dicCode)).findFirst();
                if (!first.isPresent()){
                    ArrayList<CollectorResponseInfo> collectorResponseInfos = new ArrayList<>();
                    collectorResponseInfos.add(m);
                    return collectorResponseInfos.stream();
                }
               return first.map(k-> {
                    try {
                       return this.sysDicItemService.sysDicItemListByParentId(k.getId().toString()).stream().map(elem -> new CollectorResponseInfo(
                                elem.getDicItemValue(), elem.getDicItemName())).collect(Collectors.toList());
                    } catch (Exception e) {
                        log.error("Failed to get outsourcing company account",e);
                    }
                   return new ArrayList<CollectorResponseInfo>();
                }).get().stream();
            }).collect(Collectors.toList());
            // 排除不在排班人员里面的催收人员
//            collect.stream().filter(u -> receiverSchedulingList.stream().anyMatch(m -> u.getCode().equalsIgnoreCase(m.getUserId().toString()))).collect(Collectors.toList());
            // 将催收人员乱序避免每次多出的单量分配到前几个人
            Collections.shuffle(collect);
            c.setStaff(collect);
        });
        for (CollectionPostResponse item : collectors) {
            // 需要 催收的其它金额
            Integer otherAmount=0;
            // 该分类下找不到正在上班的催收人员
            if (CollectionUtils.isEmpty(item.getStaff())){
                log.warn(item.getPostName()+"There are no collection staff below！");
                continue;
            }
            //  d0,d-1或者D-2 调用同样的接口
            if ("D-1".equalsIgnoreCase(item.getPostName())||"D-2".equalsIgnoreCase(item.getPostName()) || "D0".equalsIgnoreCase(item.getPostName())){
                D0AutoAssignmentRequest request=new D0AutoAssignmentRequest();
                request.setOtherAmount(otherAmount);
                request.setSection(item.getSection());
                request.setSourceType(sourceType);
                String daysStr = item.getSection().split("#")[0];
                int index=0;
                // 多余的单子分配到第几个人了
                int collectIndex=0;
                while (index<= amountApply.length){
                    // 金额额度已完，分配其它金额
                    if (index==amountApply.length){
                        request.setAmountApply(new BigDecimal[0]);
                        otherAmount=1;
                    }else {
                        BigDecimal[] newAmountApply={amountApply[index]};
                        request.setAmountApply(newAmountApply);
                    }
                    index++;
                    D0OrderStatistics orderStatistics1 = manCollectionDao.getD0CollectionOrderStatistics(Integer.valueOf(daysStr)
                            , sourceType, request.getAmountApply(), getOtherAmount(request.getAmountApply(),otherAmount));
                    List<D0CollectorOrderStatistics> collect = item.getStaff().stream().map(u -> {
                        D0CollectorOrderStatistics userOrderStatistics = new D0CollectorOrderStatistics();
                        userOrderStatistics.setRealName(u.getName());
                        userOrderStatistics.setUserId(Integer.valueOf(u.getCode()));
                        return userOrderStatistics;
                    }).collect(Collectors.toList());
                    Integer unAssignedReBorrowingCount0 = orderStatistics1.getUnAssignedReBorrowingCount0();
                    Integer unAssignedReBorrowingCount1 = orderStatistics1.getUnAssignedReBorrowingCount1();
                    Integer unAssignedReBorrowingCount2 = orderStatistics1.getUnAssignedReBorrowingCount2();
                    Integer unAssignedReBorrowingCount3 = orderStatistics1.getUnAssignedReBorrowingCount3();
                    Integer unAssignedReBorrowingCountN = orderStatistics1.getUnAssignedReBorrowingCountN();
                    // 平均分配所有示分配订单
                    while (unAssignedReBorrowingCount0 >0 ||
                            unAssignedReBorrowingCount1 >0 ||
                            unAssignedReBorrowingCount2 >0 ||
                            unAssignedReBorrowingCount3 >0 ||
                            unAssignedReBorrowingCountN >0){
                        D0CollectorOrderStatistics userOrderStatistics = collect.get(collectIndex);
                        if (unAssignedReBorrowingCount0 >0 ){
                            Integer userCount = userOrderStatistics.getUnAssignedReBorrowingCount0();
                            userCount++;
                            userOrderStatistics.setUnAssignedReBorrowingCount0(userCount);
                            unAssignedReBorrowingCount0--;
                        }else if (unAssignedReBorrowingCount1 >0 ){
                            Integer userCount = userOrderStatistics.getUnAssignedReBorrowingCount1();
                            userCount++;
                            userOrderStatistics.setUnAssignedReBorrowingCount1(userCount);
                            unAssignedReBorrowingCount1--;
                        }else if (unAssignedReBorrowingCount2 >0 ){
                            Integer userCount = userOrderStatistics.getUnAssignedReBorrowingCount2();
                            userCount++;
                            userOrderStatistics.setUnAssignedReBorrowingCount2(userCount);
                            unAssignedReBorrowingCount2--;
                        }else if (unAssignedReBorrowingCount3 >0 ){
                            Integer userCount = userOrderStatistics.getUnAssignedReBorrowingCount3();
                            userCount++;
                            userOrderStatistics.setUnAssignedReBorrowingCount3(userCount);
                            unAssignedReBorrowingCount3--;
                        }else if (unAssignedReBorrowingCountN >0 ){
                            Integer userCount = userOrderStatistics.getUnAssignedReBorrowingCountN();
                            userCount++;
                            userOrderStatistics.setUnAssignedReBorrowingCountN(userCount);
                            unAssignedReBorrowingCountN--;
                        }
                        if (collectIndex<(collect.size()-1)){
                            collectIndex++;
                        }else {
                            collectIndex=0;
                        }
                    }
                    request.setCollectorAssignmentRequest(collect);
                    log.info("systemAutoAssignment1:"+JsonUtils.serialize(request));
                    if (checkD0AutoAssignmentRequestParam(request)) {
                        Arrays.asList(D0AssignmentType.values()).stream().forEach(elem ->
                                assignD0CollectionOrderByType(request.getCollectorAssignmentRequest(), elem,
                                        request.getSection(), request.getSourceType(), request.getAmountApply(), request.getOtherAmount()));
                    }
                }
//                this.autoAssignCollectionOrderForD0(request);
            }else {

                OverdueAutoAssignmentRequest request =new OverdueAutoAssignmentRequest();
                request.setAmountApply(amountApply);
                request.setOtherAmount(otherAmount);
                request.setSection(item.getSection());
                request.setPostId(item.getPostId());
                request.setSourceType(sourceType);
                Integer minOverdueDays = Integer.valueOf(item.getSection().split("#")[0]);
                Integer maxOverdueDays = Integer.valueOf(item.getSection().split("#")[1]);
                // 多余的单子分配到第几个人了
                int collectIndex=0;
                int index=0;
                while (index<= amountApply.length) {
                    // 金额额度已完，分配其它金额
                    if (index == amountApply.length) {
                        request.setAmountApply(new BigDecimal[0]);
                        otherAmount = 1;
                    } else {
                        BigDecimal[] newAmountApply = {amountApply[index]};
                        request.setAmountApply(newAmountApply);
                    }
                    index++;
                    OverdueOrderStatistics overdueOrderStatistics = manCollectionDao.getOverdueOrderStatistics(minOverdueDays, maxOverdueDays, sourceType, request.getAmountApply(), getOtherAmount(request.getAmountApply(), otherAmount));
                    Integer unAssignedReBorrowingCount0 = overdueOrderStatistics.getUnAssignedReBorrowingCount0();
                    Integer unAssignedReBorrowingCountN = overdueOrderStatistics.getUnAssignedReBorrowingCountN();
                    List<OverdueCollectorOrderStatistics> collect = item.getStaff().stream().map(u -> {
                        OverdueCollectorOrderStatistics userOrderStatistics = new OverdueCollectorOrderStatistics();
                        userOrderStatistics.setRealName(u.getName());
                        userOrderStatistics.setUserId(Integer.valueOf(u.getCode()));
                        return userOrderStatistics;
                    }).collect(Collectors.toList());
                    while (unAssignedReBorrowingCount0 > 0 || unAssignedReBorrowingCountN > 0) {
                        OverdueCollectorOrderStatistics userOrderStatistics = collect.get(collectIndex);
                        if (unAssignedReBorrowingCount0 > 0) {
                            Integer userCount = userOrderStatistics.getUnAssignedReBorrowingCount0();
                            userCount++;
                            userOrderStatistics.setUnAssignedReBorrowingCount0(userCount);
                            unAssignedReBorrowingCount0--;
                        } else if (unAssignedReBorrowingCountN > 0) {
                            Integer userCount = userOrderStatistics.getUnAssignedReBorrowingCountN();
                            userCount++;
                            userOrderStatistics.setUnAssignedReBorrowingCountN(userCount);
                            unAssignedReBorrowingCountN--;
                        }
                        if (collectIndex < (collect.size() - 1)) {
                            collectIndex++;
                        } else {
                            collectIndex = 0;
                        }
                    }
                    request.setCollectorAssignmentRequest(collect);
                    log.info("systemAutoAssignment2:" + JsonUtils.serialize(request));
                    if (checkOverdueAutoAssignmentRequestParam(request)) {
                        Arrays.asList(OverdueAssignmentType.values()).stream().forEach(elem -> assignOverdueCollectionOrderByType(request.getCollectorAssignmentRequest(), elem, minOverdueDays, maxOverdueDays, request.getIsThird(), request.getOutSourceId(), request.getSourceType(), request.getAmountApply(), request.getOtherAmount()));
                    }else {
                        log.warn("the input param is not valid for overdue auto assignment, param= {}", JsonUtils.serialize(request));
                    }

                }
//                this.autoAssignCollectionOrderForOverdue(request);
            }
        }



    }

    @Autowired
    private SysDicItemDao sysDicItemDao;

    /**
     * 获得需要更新处理的自然日阶段
     */
    public String getOrderStage() {
        SysDicItem sysDicItem = new SysDicItem();
        sysDicItem.setDisabled(0);
        sysDicItem.setDicId("22");
        List<String> rList = sysDicItemDao.scan(sysDicItem).stream().filter(elem -> !("D-1".equals(elem.getDicItemName())
                || "D-2".equals(elem.getDicItemName()))).map(u -> u.getDicItemValue().split("#")[0]).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(rList)) {
            return null;
        }
        return org.apache.commons.lang3.StringUtils.join(rList, ",");
    }
}
