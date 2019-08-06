package com.yqg.manage.service.review;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.models.PageData;
import com.yqg.manage.dal.order.ManOrderOrderDao;
import com.yqg.manage.dal.user.ManSecondCheckRecordDao;
import com.yqg.manage.dal.user.ManUserDao;
import com.yqg.manage.dal.user.ReviewerOrderTaskDAO;
import com.yqg.manage.entity.check.ManSecondCheckRecord;
import com.yqg.manage.entity.order.ReviewOrderAssignParam;
import com.yqg.manage.entity.user.ManSysUserRole;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.entity.user.ReviewerOrderTask;
import com.yqg.manage.enums.DictCollectionEnum;
import com.yqg.manage.enums.ReviewerPostEnum;
import com.yqg.manage.service.order.CommonService;
import com.yqg.manage.service.order.ManOrderOrderService;
import com.yqg.manage.service.order.OrderAssignmentService;
import com.yqg.manage.service.order.request.AssignableOrderRequest;
import com.yqg.manage.service.order.request.CompletedOrderRequest;
import com.yqg.manage.service.order.request.OrderSearchRequest.OrderChannelEnum;
import com.yqg.manage.service.order.request.ReviewerOrderApplicationParam;
import com.yqg.manage.service.order.response.AssignableOrderResponse;
import com.yqg.manage.service.order.response.CompletedOrderResponse;
import com.yqg.manage.service.order.response.ManOrderListResponse;
import com.yqg.manage.service.user.ManUserRoleService;
import com.yqg.manage.service.user.ManUserService;
import com.yqg.manage.service.user.UserUserService;
import com.yqg.manage.util.DateUtils;
import com.yqg.manage.util.PageDataUtils;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.system.response.SysDicItemModel;
import com.yqg.service.system.service.SysDicService;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/

@Service
@Slf4j
public class ReviewService {


    @Autowired
    private ManUserRoleService manUserRoleService;

    @Autowired
    private ManUserService manUserService;

    @Autowired
    private ManOrderOrderService manOrderOrderService;

    @Autowired
    private OrderAssignmentService orderAssignmentService;

    @Autowired
    private UserUserService userUserService;

    @Autowired
    private SysDicService sysDicService;


    @Autowired
    private ReviewerOrderTaskDAO reviewerOrderTaskDao;

    @Autowired
    private ManOrderOrderDao manOrderOrderDao;

    @Autowired
    private ManUserDao manUserDao;

    @Autowired
    private ManSecondCheckRecordDao manSecondCheckRecordDao;

    @Autowired
    private CommonService commonService;

    /***
     * 将订单分配给审批人
     * @param operatorId
     * @param reviewerId
     * @param orderUUIDs
     */
    public void assignOrder(Integer operatorId, Integer reviewerId, List<String> orderUUIDs) {
        List<String> postNames = getReviewerPostNames(reviewerId);
        for (String orderUUID : orderUUIDs) {
            //判定是初审分配还是复审分配
            String postName = checkCurrentAssigmentType(orderUUID);
            //判定传入的审核人是否有该岗位
            if (!postNames.contains(postName)) {
                log.warn(
                        "current user is not a reviewer: operatorId = {}, reviewerId= {}, orderUUID= {}",
                        operatorId, reviewerId, orderUUID);
                continue;
            }
            ReviewOrderAssignParam assignParam = new ReviewOrderAssignParam(reviewerId, operatorId,
                    orderUUID, postName);
            orderAssignmentService.assignReviewOrder(assignParam);
        }
    }


    /***
     * 根据审核人和订单状态查询订单
     * @param reviewerId
     * @param orderStatus
     * @return
     */
    public PageData<List<ManOrderListResponse>> getReviewableOrderList(Integer reviewerId,
                                                                       Integer orderStatus, int pageNum,
                                                                       int pageSize) {

        String reviewerRole = "";
        if (orderStatus == OrdStateEnum.FIRST_CHECK.getCode()) {
            reviewerRole = ReviewerPostEnum.JUNIOR_REVIEWER.name();
        } else if (orderStatus == OrdStateEnum.SECOND_CHECK.getCode()) {
            reviewerRole = ReviewerPostEnum.SENIOR_REVIEWER.name();
        }

        PageHelper.startPage(pageNum, pageSize);
        List<ManOrderListResponse> orderList = reviewerOrderTaskDao
                .getAssignedReviewOrders(reviewerId, orderStatus, reviewerRole);
        PageInfo page = new PageInfo(orderList);
        if (CollectionUtils.isEmpty(orderList)) {
            return PageDataUtils.mapPageInfoToPageData(page);
        }

        //设置订单用户信息
        List<String> userUUIDs = orderList.stream().map(elem -> elem.getUserUuid())
                .collect(Collectors.toList());
        List<UsrUser> userList = userUserService
                .getUserInfoByUuids("'" + StringUtils.join(userUUIDs, "','") + "'");
        Map<String, UsrUser> userMap = userList.stream().collect(Collectors.toMap(UsrUser::getUuid,
                Function.identity()));

        //设置用戶名和复审状态
        orderList.stream().forEach(elem -> {
            if (userMap != null && userMap.get(elem.getUserUuid()) != null) {
                elem.setRealName(userMap.get(elem.getUserUuid()).getRealName());
            }
            elem.setOrderChannelEnum(OrderChannelEnum.valueOf(elem.getChannel()));
            //查询manOrderRemark表，若有操作日志，则表示复审状态为处理中，但订单可能重新分配，需要关联查询订单分配表
            if (StringUtils.isNotBlank(elem.getUuid())) {
                String maxRemarkTime = reviewerOrderTaskDao.getManRemarkCount(elem.getUuid());
                String maxAssignment = reviewerOrderTaskDao.getMaxCreateTime(elem.getUuid());
                Integer restult = 0;
                if (StringUtils.isEmpty(maxRemarkTime) ||
                        StringUtils.isEmpty(maxAssignment)) {
                    restult = 0;
                } else if (maxRemarkTime.compareTo(maxAssignment) > 0 ) {
                    restult = 1;
                }
                elem.setSecondCheckStatus(restult);
            }
            //封装初审人员姓名
            ManUser manUser = new ManUser();
            manUser.setId(Integer.valueOf(elem.getFirstChecker()));
            manUser.setDisabled(0);
            List<ManUser> manUsers = manUserDao.scan(manUser);
            elem.setFirstChecker(
                    CollectionUtils.isEmpty(manUsers) ? "" : manUsers.get(0).getRealname());

            //封装订单复审标签，1表示稍后再审
            ManSecondCheckRecord record = new ManSecondCheckRecord();
            record.setDisabled(0);
            record.setOrderNo(elem.getUuid());
            List<ManSecondCheckRecord> records = manSecondCheckRecordDao.scan(record);
            if (CollectionUtils.isEmpty(records)) {
                elem.setOperatorType(0);
            } else {
                elem.setOperatorType(1);
            }

        });
        return PageDataUtils.mapPageInfoToPageData(page);
    }


    /***
     * 待分配订单
     * @param request
     * @return
     */
    public PageData<List<AssignableOrderResponse>> getAssignableOrderList(
            AssignableOrderRequest request) {
        PageHelper.startPage(request.getPageNo(), request.getPageSize());
        List<AssignableOrderResponse> orderList = manOrderOrderDao.getAssignableOrderList(request);
        PageInfo page = new PageInfo(orderList);
        if (CollectionUtils.isEmpty(orderList)) {
            return PageDataUtils.mapPageInfoToPageData(page);
        }
        //设置订单审核人信息
        List<Integer> reviewerIds = orderList.stream().filter(elem -> elem.getReviewerId() != null)
                .map(elem -> elem.getReviewerId())
                .collect(Collectors.toList());
        Map<String, ManUser> sysUserMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(reviewerIds)) {
            List<ManUser> sysUsers = manUserService.getSysUserByIds(reviewerIds);
            if (!CollectionUtils.isEmpty(sysUsers)) {
                sysUserMap = sysUsers.stream()
                        .collect(Collectors.toMap(elem -> elem.getId().toString(),
                                elem -> elem));
            }
        }

        for (AssignableOrderResponse elem : orderList) {
            if (sysUserMap != null && elem.getReviewerId() != null) {
                ManUser reviewer = sysUserMap.get(elem.getReviewerId().toString());
                elem.setReviewerName(reviewer == null ? null : reviewer.getRealname());
            }
            commonService.setTerms(elem);
        }
        return PageDataUtils.mapPageInfoToPageData(page);
    }


    /***
     * 查询已经审核完成的订单
     * @param sysUserId
     * @param request
     * @return
     */
    public PageData<List<CompletedOrderResponse>> getCompletedOrderList(Integer sysUserId,
                                                                        CompletedOrderRequest request,boolean isAdmin) {

        //管理员页面进行条件筛选审核人员
        if (request.getReviewerId() != null) {
            request.setIsAdmin(false);
            request.setOperatorId(request.getReviewerId());
        } else {
            request.setIsAdmin(isAdmin);
            //控制初审复审人员只能查看到分配给自己的单[管理员可以看所有的]
            request.setOperatorId(sysUserId);
        }

        PageHelper.startPage(request.getPageNo(), request.getPageSize());
        List<CompletedOrderResponse> orderList = manOrderOrderDao.getCompletedOrderList(request);
        PageInfo page = new PageInfo(orderList);
        if (CollectionUtils.isEmpty(orderList)) {
            return PageDataUtils.mapPageInfoToPageData(page);
        }

        //设置逾期信息
        orderList.stream().forEach(elem -> {
            elem.setOverdueDays(0L);
            if (elem.getRefundTime() != null && new Date().compareTo(elem.getRefundTime()) >= 0) {
                elem.setOverdueDays(
                        DateUtils.getDiffDaysIgnoreHours(elem.getRefundTime(), new Date()));
            }
            if (elem.getActualRefundTime() != null) {
                elem.setOverdueDays(DateUtils
                        .getDiffDaysIgnoreHours(elem.getRefundTime(), elem.getActualRefundTime()));
            }
            commonService.setTerms(elem);
        });

        //设置订单审核人信息
        List<Integer> juniorReviewIds = orderList.stream()
                .filter(elem -> elem.getJuniorReviewerId() != null)
                .map(elem -> elem.getJuniorReviewerId()).collect(Collectors.toList());

        List<Integer> seniorReviewerIds = orderList.stream()
                .filter(elem -> elem.getSeniorReviewerId() != null)
                .map(elem -> elem.getSeniorReviewerId()).collect(Collectors.toList());

        List<Integer> reviewerIds = new ArrayList<>();
        reviewerIds
                .addAll(CollectionUtils.isEmpty(juniorReviewIds) ? new ArrayList<>() : juniorReviewIds);
        reviewerIds.addAll(
                CollectionUtils.isEmpty(seniorReviewerIds) ? new ArrayList<>() : seniorReviewerIds);

        //查询审核人信息
        List<ManUser> sysUsers = manUserService.getSysUserByIds(reviewerIds);

        if (CollectionUtils.isEmpty(sysUsers)) {
            log.warn("cannot find reviewers info");
            return PageDataUtils.mapPageInfoToPageData(page);
        }

        Map<String, ManUser> sysUserMap = sysUsers.stream()
                .collect(Collectors.toMap(elem -> elem.getId().toString(),
                        elem -> elem));

        //查询审核时间
        List<String> orderUUIDs = orderList.stream().map(elem -> elem.getUuid())
                .collect(Collectors.toList());
        List<ReviewerOrderTask> assignedList = reviewerOrderTaskDao
                .getFinishedAssignedTaskByOrderUUIDs(orderUUIDs);

        //复审、初审分配完成时间
        if (!CollectionUtils.isEmpty(assignedList)) {
            for (CompletedOrderResponse order : orderList) {
                for (ReviewerOrderTask reviewerOrderTask : assignedList) {
                    if (order.getUuid().equals(reviewerOrderTask.getOrderUUID())) {
                        if ("JUNIOR_REVIEWER".equals(reviewerOrderTask.getReviewerRole())) {
                            order.setJuniorAssignTime(reviewerOrderTask.getCreateTime());
                            order.setJuniorReviewTime(reviewerOrderTask.getUpdateTime());
                        } else {
                            order.setSeniorAssignTime(reviewerOrderTask.getCreateTime());
                            order.setSeniorReviewTime(reviewerOrderTask.getUpdateTime());
                        }
                    }

                }
            }

        }

        if (CollectionUtils.isEmpty(sysUsers)) {
            log.warn("cannot find review order assignment info");
            return PageDataUtils.mapPageInfoToPageData(page);
        }

        //设置初审人员信息
        orderList.stream().filter(elem -> elem.getJuniorReviewerId() != null).forEach(elem -> {
            ManUser juniorReviewer = sysUserMap.get(elem.getJuniorReviewerId().toString());
            elem.setJuniorReviewerName(
                    juniorReviewer == null ? null : juniorReviewer.getRealname());
        });

        //设置复审人员信息
        orderList.stream().filter(elem -> elem.getSeniorReviewerId() != null).forEach(elem -> {
            ManUser seniorReviewer = sysUserMap.get(elem.getSeniorReviewerId().toString());
            elem.setSeniorReviewerName(
                    seniorReviewer == null ? null : seniorReviewer.getRealname());
        });

        return PageDataUtils.mapPageInfoToPageData(page);
    }


    /***
     * 申请新的审核订单
     * @param sysUserId
     * @param param
     */
    public void applyOrderReview(Integer sysUserId, ReviewerOrderApplicationParam param) {
        //检查未完成订单最大数量
        try {
            List<SysDicItemModel> list = sysDicService
                    .sysDicItemsListByDicCode(DictCollectionEnum.REVIEWER_APPLY_PARAM.name());
            if (!CollectionUtils.isEmpty(list)) {
                Optional<SysDicItemModel> itemModel = list.stream().filter(
                        elem -> elem.getDicItemName()
                                .equals(DictCollectionEnum.REVIEWER_APPLY_PARAM_MAX_COUNT.name()))
                        .findFirst();
                if (itemModel.isPresent()) {
                    Integer maxCount = Integer.valueOf(itemModel.get().getDicItemValue());
                    PageData pageData = this
                            .getReviewableOrderList(sysUserId, param.getStatus().getCode(), 0, 10);
                    if (pageData.getRecordsTotal() >= maxCount) {
                        log.error("the review order assign apply > maxCount: {}", maxCount);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            log.error("fetch apply param error, userId= {}", sysUserId);
            return;
        }

        //查询可分配订单，最近20条可分配订单
        AssignableOrderRequest request = new AssignableOrderRequest();
        request.setStatus(param.getStatus());

        request.setPageNo(1);
        request.setPageSize(20);
        PageData pageData = this.getAssignableOrderList(request);

        if (pageData == null) {
            log.error("no available orders to assign, sysUserId= {}", sysUserId);
            return;
        }
        List<AssignableOrderResponse> orderList = (List<AssignableOrderResponse>) pageData
                .getData();
        if (CollectionUtils.isEmpty(orderList)) {
            log.error("no available orders to assign, orderListSize =0, sysUserId= {}", sysUserId);
            return;
        }

        for (AssignableOrderResponse orderInfo : orderList) {
            ReviewOrderAssignParam assignParam = new ReviewOrderAssignParam(sysUserId, sysUserId,
                    orderInfo.getUuid(), ReviewerPostEnum.JUNIOR_REVIEWER.name());
            boolean flag = orderAssignmentService.assignReviewOrder(assignParam);
            if (flag) {
                log.info("assign success, userId= {}", sysUserId);
                return;
            }
        }
        log.error("assign failed, userId= {}", sysUserId);
    }



    /****
     * 查询审核人员岗位
     * @param sysUserId
     * @return
     */
    private List<String> getReviewerPostNames(Integer sysUserId) {
        List<ManSysUserRole> rolesList = manUserRoleService.userRoleListByUserId(sysUserId);
        if (!CollectionUtils.isEmpty(rolesList)) {
            List<String> roleIdList = rolesList.stream().map(elem -> elem.getRoleId().toString())
                    .collect(Collectors.toList());
            //审核人员的角色岗位关系
            List<SysDicItemModel> allRoleInfoList = manUserService.getAllReviewerRoleDicItems();
            if (CollectionUtils.isEmpty(allRoleInfoList)) {
                log.error("the reviewer role post relation is not config...");
                return new ArrayList<>();
            }
            //比对用户的角色信息，看是否有审核角色JUNIOR_REVIEWER#1,SENIOR_REVIEWER#2,管理员可能有两个角色
            List<SysDicItemModel> postInfoList = allRoleInfoList.stream().filter(elem -> {
                String roleId = elem.getDicItemValue().split("#")[1];
                return roleIdList.contains(roleId);
            }).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(postInfoList)) {
                return postInfoList.stream().map(elem -> elem.getDicItemValue().split("#")[0])
                        .collect(
                                Collectors.toList());
            }
        }
        return new ArrayList<>();
    }


    /***
     * 检查订单当前可分配类型，初审分配or复审分配
     * @param orderUUID
     * @return
     */
    private String checkCurrentAssigmentType(String orderUUID) {
        OrdOrder order = manOrderOrderService.getOrderInfoByUuid(orderUUID);
        if (order.getStatus().equals(OrdStateEnum.FIRST_CHECK.getCode())) {
            //初审
            return ReviewerPostEnum.JUNIOR_REVIEWER.name();
        } else if (order.getStatus().equals(OrdStateEnum.SECOND_CHECK.getCode())) {
            //复审
            return ReviewerPostEnum.SENIOR_REVIEWER.name();
        }
        return "";
    }
}
