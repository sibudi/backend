package com.yqg.manage.service.collection;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.Base64Utils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.manage.constants.ConstantsForDicItem;
import com.yqg.manage.constants.ConstantsForLock;
import com.yqg.manage.dal.collection.CollectionOrderDetailDao;
import com.yqg.manage.dal.collection.ManQualityCheckConfigDao;
import com.yqg.manage.entity.collection.ManQualityCheckConfig;
import com.yqg.manage.entity.collection.ManQualityCheckRecord;
import com.yqg.manage.enums.ManCollectorOperatorEnum;
import com.yqg.manage.enums.ManOperatorEnum;
import com.yqg.manage.service.order.CommonService;
import com.yqg.manage.service.system.ManAuthManagerService;
import com.yqg.management.dao.CollectionOrderHistoryDao;
import com.yqg.manage.dal.collection.ManCollectionDao;
import com.yqg.manage.dal.collection.ManCollectionRemarkDao;
import com.yqg.manage.dal.order.ManOrderOrderDao;
import com.yqg.manage.dal.user.CollectorInfoDao;
import com.yqg.manage.dal.user.ManUserBankDao;
import com.yqg.manage.dal.user.ManUserDao;
import com.yqg.manage.entity.collection.CollectionOrderDetail;
import com.yqg.management.entity.CollectionOrderHistory;
import com.yqg.manage.entity.collection.ManCollectionRemark;
import com.yqg.manage.entity.user.CollectorInfo;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.enums.ContactTypeEnum;
import com.yqg.manage.enums.DictCollectionEnum;
import com.yqg.manage.service.collection.request.AssignableCollectionOrderReq;
import com.yqg.manage.service.collection.request.CollectionPostRequest;
import com.yqg.manage.service.collection.response.*;
import com.yqg.manage.service.order.request.OrderSearchRequest;
import com.yqg.manage.service.order.request.OverdueOrderRequest;
import com.yqg.manage.service.order.request.PaidOrderRequest;
import com.yqg.manage.service.order.response.ManOrderRemarkResponse;
import com.yqg.manage.service.system.response.SysDicItemResponse;
import com.yqg.manage.service.user.ManUserService;
import com.yqg.manage.service.user.UserUserService;
import com.yqg.manage.service.user.request.ManUserUserRequest;
import com.yqg.manage.service.user.response.ManSysUserResponse;
import com.yqg.manage.util.DateUtils;
import com.yqg.service.util.LoginSysUserInfoHolder;
import com.yqg.manage.util.PageDataUtils;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.system.response.SysDicItemModel;
import com.yqg.service.system.service.SysDicItemService;
import com.yqg.service.system.service.SysDicService;
import com.yqg.service.third.upload.UploadService;
import com.yqg.service.third.upload.response.UploadResultInfo;
import com.yqg.service.user.response.UsrAttachmentResponse;
import com.yqg.system.dao.SysDicDao;
import com.yqg.system.dao.SysDicItemDao;
import com.yqg.system.entity.SysDic;
import com.yqg.system.entity.SysDicItem;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.entity.UsrBank;
import com.yqg.user.entity.UsrUser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 ****/

@Service
@Slf4j
public class CollectionService {

    @Value("${collection.d-1}")
    private String postIdD_1;

    @Value("${collection.d-2}")
    private String postIdD_2;

    @Value("${downlaod.filePath}")
    private String filePath;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private SysDicService sysDicService;

    @Autowired
    private ManUserService manUserService;
    @Autowired

    private CollectorInfoDao collectorInfoDao;

    @Autowired
    private ManCollectionDao manCollectionDao;

    @Autowired
    private UserUserService userUserService;

    @Autowired
    private ManOrderOrderDao manOrderOrderDao;

    @Autowired
    private ManUserBankDao usrBankDao;

    @Autowired
    private CollectionOrderDetailDao collectionOrderDetailDao;

    @Autowired
    private CollectionOrderHistoryDao collectionOrderHistoryDao;

    @Autowired
    private SysDicDao dicDao;

    @Autowired
    private SysDicItemDao sysDicItemDao;

    @Autowired
    private ManUserDao manUserDao;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ManCollectionRemarkDao manCollectionRemarkDao;

    @Autowired
    private SysDicDao sysDicDao;
    @Autowired
    private SysDicItemService sysDicItemService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ManAuthManagerService manAuthManagerService;

    @Autowired
    private UsrDao usrDao;

    public List<CollectorResponseInfo> getCollectorsByThirdType(Integer isThird, Integer sourceType) {
        //查询催收岗位的角色id
        Integer[] roleIds = getCollectorRoleId(sourceType);
        if (roleIds == null) {
            return new ArrayList<>();
        }
        //查询某一角色下的所有用户
        List<ManSysUserResponse> sysUserList = manUserService.getUsersByRole(roleIds[0]);

        if (sourceType.equals(0)) {
            //公司母账号
            List<ManSysUserResponse> companyList = manUserService.getUsersByRole(roleIds[1]);
            //重新封装公司母账号名称（为了显示出来出勤人数）
            if (!CollectionUtils.isEmpty(companyList)) {
                companyList.stream().forEach(e -> {
                    try {
                        SysDic dicSearch = new SysDic();
                        dicSearch.setDisabled(0);
                        dicSearch.setDicCode(DictCollectionEnum.THIRD_COMPANY.name() +
                                "_" + String.valueOf(e.getId()));
                        List<SysDic> dicResult = this.dicDao.scan(dicSearch);
                        if (!CollectionUtils.isEmpty(dicResult)) {
                            e.setRealname(dicResult.get(0).getDicName());
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                });
                sysUserList.addAll(companyList);
            }
        }
        if (CollectionUtils.isEmpty(sysUserList)) {
            return new ArrayList<>();
        }

        //账号为207的cuishouheimingdan 不显示出来
        return sysUserList.stream().filter(elem -> isThird.equals(elem.getThird())
                && elem.getId() != 207)
                .map(elem -> new CollectorResponseInfo(elem.getId().toString(), elem.getRealname()))
                .collect(
                        Collectors.toList());
    }

    /**
    * @Description: 获取所有催收人员帐号包括委外帐号下面的人
    * @Param: [isThird, sourceType]
    * @return: java.util.List<com.yqg.manage.service.collection.response.CollectorResponseInfo>
    * @Author: 许金泉
    * @Date: 2019/4/25 15:38
    */
    public List<CollectorResponseInfo> getAllCollectors(Integer isThird, Integer sourceType) {
        //查询催收岗位的角色id
        Integer[] roleIds = getCollectorRoleId(sourceType);
        if (roleIds == null) {
            return new ArrayList<>();
        }
        List<CollectorResponseInfo> result=new ArrayList<>();
        //查询某一角色下的所有用户
        List<ManSysUserResponse> sysUserList = manUserService.getUsersByRole(roleIds[0]);
        List<CollectorResponseInfo> collect1 = sysUserList.stream().filter(elem -> isThird.equals(elem.getThird()) && elem.getId() != 207).map(elem -> new CollectorResponseInfo(elem.getId().toString(), elem.getUsername())).collect(Collectors.toList());
        result.addAll(collect1);
        // 所有委外催收公司
        List<SysDic> thirdCompany = sysDicDao.getThirdCompany();
        if (sourceType.equals(0)) {
            //公司母账号
            List<ManSysUserResponse> companyList = manUserService.getUsersByRole(roleIds[1]);
            List<CollectorResponseInfo> collect = companyList.stream().flatMap(m -> {
                String dicCode = DictCollectionEnum.THIRD_COMPANY.name() + "_" + String.valueOf(m.getId());
                Optional<SysDic> first = thirdCompany.stream().filter(u -> u.getDicCode().equalsIgnoreCase(dicCode)).findFirst();
                if (!first.isPresent()){
                    ArrayList<CollectorResponseInfo> collectorResponseInfos = new ArrayList<>();
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
            result.addAll(collect);
        }
        return result;
    }




    /****
     * 查询当前所有岗位下的催收人员
     * @return
     */
    public List<CollectionPostResponse> getCurrentCollectors(Integer sourceType) {

        //查询所有岗位信息
        List<SysDicItemResponse> postIds = getCollectionPostInfoList();
        if (CollectionUtils.isEmpty(postIds)) {
            return new ArrayList<>();
        }
        List<CollectionPostResponse> resultList = new ArrayList<>();
        postIds.stream().forEach(elem -> {
            CollectionPostResponse response = new CollectionPostResponse();
            resultList.add(response);
            response.setPostId(elem.getId());//[postId为dicItem中数据的id]
            response.setPostName(elem.getDicItemName());
            response.setSection(elem.getDicItemValue());
            response.setStaff(getCollectorsByPostId(response.getPostId(), sourceType));

        });
        return resultList;
    }

    /****
     * 查询当前所有岗位下的休息的催收人员
     * @return
     */
    public List<CollectionPostResponse> getRestCurrentCollectors(Integer sourceType) {

        //查询所有岗位信息
        List<SysDicItemResponse> postIds = getCollectionPostInfoList();
        if (CollectionUtils.isEmpty(postIds)) {
            return new ArrayList<>();
        }
        List<CollectionPostResponse> resultList = new ArrayList<>();
        postIds.stream().forEach(elem -> {
            CollectionPostResponse response = new CollectionPostResponse();
            resultList.add(response);
            response.setPostId(elem.getId());//[postId为dicItem中数据的id]
            response.setPostName(elem.getDicItemName());
            response.setSection(elem.getDicItemValue());
            response.setStaff(getCollectorsByPostId(response.getPostId(), sourceType));
            response.setRestTaff(getRestCollectorsByPostId(response.getPostId(), sourceType));

        });
        return resultList;
    }


    /***
     * 查询某一个岗位下当前所有的催收人员
     * @param postId
     * @return
     */
    public List<CollectorResponseInfo> getCollectorsByPostId(Integer postId, Integer sourceType) {
        List<CollectorInfo> collectors = collectorInfoDao
                .getCollectorsByPostId(postId, sourceType);
        if (CollectionUtils.isEmpty(collectors)) {
            return new ArrayList<>();
        }
        List<CollectorResponseInfo> rList = collectors.stream().map(
                collector -> new CollectorResponseInfo(collector.getUserId().toString(),
                        collector.getRealName())).collect(
                Collectors.toList());

        return rList;
    }
    /***
     * 查询某一个岗位下当前所有的休息的催收人员
     * @param postId
     * @return
     */
    public List<CollectorResponseInfo> getRestCollectorsByPostId(Integer postId, Integer sourceType) {
        List<CollectorInfo> collectors = collectorInfoDao
                .getRestCollectorsByPostId(postId, sourceType);
        if (CollectionUtils.isEmpty(collectors)) {
            return new ArrayList<>();
        }
        List<CollectorResponseInfo> rList = collectors.stream().map(
                collector -> new CollectorResponseInfo(collector.getUserId().toString(),
                        collector.getRealName())).collect(
                Collectors.toList());

        return rList;
    }

    /***
     * 查询催收人员所有岗位信息
     * @return
     */
    public List<SysDicItemResponse> getCollectionPostInfoList() {
        try {
            List<SysDicItemModel> dicItemList = sysDicService.
                    sysDicItemsListByDicCode(DictCollectionEnum.COLLECTION_POST.name());
            if (CollectionUtils.isEmpty(dicItemList)) {
                log.error("the COLLECTION_POST is not configure");
                return new ArrayList<>();
            }
            return dicItemList.stream().map(
                    elem -> new SysDicItemResponse(elem.getId(), elem.getDicId(), elem.getDicItemName(),
                            elem.getDicItemValue())).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("get collection role error", e);
        }
        return new ArrayList<>();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean scheduleCollector(CollectionPostRequest request) {
        if (null == request.getPostId() || CollectionUtils.isEmpty(request.getStaff())) {
            log.warn("input param is empty");
            return false;
        }
        //分配休息人员
        if (request.getRest() != null && request.getRest().equals(1)) {
            //将原来分配的数据disabled
            collectorInfoDao.updateRestCollectorStatusByPostId(request.getPostId(), request.getSourceType());
            collectorInfoDao.insertRestCollectors(request.getStaff(), request.getPostId(),
                    LoginSysUserInfoHolder.getLoginSysUserId(), request.getSourceType());
            return true;
        }
        //查询出之前分配的情况
        List<CollectorInfo> collectorInfos = collectorInfoDao.selectCollectorStatusByPostId(request.getPostId(), request.getSourceType());
        if (CollectionUtils.isEmpty(collectorInfos)) {
            //新增分配数据
            collectorInfoDao.insertCollectors(request.getStaff(), request.getPostId(),
                    LoginSysUserInfoHolder.getLoginSysUserId(), request.getSourceType());
            return true;
        }

        //排除之前就已经有的数据
        List<CollectorResponseInfo> needAdd = new ArrayList<>();
        List<String> needRemove = new ArrayList<>();
        List<String> hasBefore = new ArrayList<>();
        List<String> tempCodes = collectorInfos.stream().map(elem -> elem.getUserId().toString()).collect(Collectors.toList());
        for (CollectorResponseInfo collectorResponseInfo : request.getStaff()) {
            boolean flag = false;
            for (CollectorInfo collectorInfo : collectorInfos) {
                if (collectorInfo.getUserId().toString().equals(collectorResponseInfo.getCode())) {
                    flag = true;
                    hasBefore.add(collectorInfo.getUserId().toString());
                }
            }
            if (!flag) {
                needAdd.add(collectorResponseInfo);
            }
        }
        for (String str : tempCodes) {
            if (!hasBefore.contains(str)) {
                needRemove.add(str);
            }
        }
        //删除之前的数据
        if (!CollectionUtils.isEmpty(needRemove)) {
            collectorInfoDao.updateCollectorStatusByPostId(request.getPostId(), needRemove, request.getSourceType());
        }
        //新增分配数据
        if (!CollectionUtils.isEmpty(needAdd)) {
            collectorInfoDao.insertCollectors(needAdd, request.getPostId(),
                    LoginSysUserInfoHolder.getLoginSysUserId(), request.getSourceType());
        }
        return true;
    }


    public PageData<List<CollectionOrderResponse>> getAssignableCollectionOrderList(
            AssignableCollectionOrderReq req) {

        getManOrderOrderRequest(req);
        
        List<CollectionOrderResponse> orderList;
        if (req.getSourceType() != null && req.getSourceType().equals(0)) {
            PageHelper.startPage(req.getPageNo(), req.getPageSize());
            orderList = manCollectionDao.getAssignableCollectionOrderList(req);
        } else if (req.getSourceType() != null && req.getSourceType().equals(1)) {
            PageHelper.startPage(req.getPageNo(), req.getPageSize());
            orderList = manCollectionDao.getAssignableQualityCheckOrderList(req);
        } else {
            orderList = new ArrayList<CollectionOrderResponse>();
        }

        PageInfo pageInfo = new PageInfo(orderList);
        if (CollectionUtils.isEmpty(orderList)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }
        //set collection check data
        if (req.getSourceType() != null && req.getSourceType() == 1) {
            setCheckData(orderList);
        }

        //设置催收人员信息
        List<Integer> collectorIds = new ArrayList<>();
        for (CollectionOrderResponse response : orderList) {

            //设置复借信息
            response.setIsRepeatBorrowing(
                    response.getBorrowingCount() != null && response.getBorrowingCount() > 1 ? 1 : 0);
            response.setReBorrowingCount(
                    response.getBorrowingCount() == null ? 0 : response.getBorrowingCount() - 1);
            //设置逾期信息
            response.setOverdueDays(DateUtils.getDiffDaysIgnoreHours(response.getRefundTime(), new Date()));

            if (response.getSubOutSourceId() != null
                    && response.getSubOutSourceId() != 0) {
                response.setOutsourceId(response.getSubOutSourceId());
            }
            if (response.getOutsourceId() != null
                    && response.getOutsourceId() != 0) {
                collectorIds.add(response.getOutsourceId());
                if (req.getSourceType() == null || req.getSourceType() == 0) {
                    response.setIsAssigned(1);
                }
            }
            commonService.setCollectionOrdTerms(response);
        }

        if (CollectionUtils.isEmpty(collectorIds)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }

        List<ManUser> sysUserList = manUserService.getSysUserByIds(collectorIds);
        if (CollectionUtils.isEmpty(sysUserList)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }
        Map<String, ManUser> sysUserMap = sysUserList.stream()
                .collect(Collectors.toMap(elem -> elem.getId().toString(), elem -> elem));
        orderList.stream().filter(elem -> elem.getOutsourceId() != null).forEach(elem -> {
            if (sysUserMap != null && sysUserMap.get(elem.getOutsourceId().toString()) != null) {
                elem.setCollectorName(
                        sysUserMap.get(elem.getOutsourceId().toString()).getRealname());
            }
        });
        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }

    private void setCheckData(List<CollectionOrderResponse> result) {

        result.stream().forEach(elem -> {
            OrdOrder ordOrder = new OrdOrder();
            ordOrder.setDisabled(0);
            ordOrder.setUuid(elem.getUuid());
            List<OrdOrder> ordOrders = manOrderOrderDao.scan(ordOrder);
            if (!CollectionUtils.isEmpty(ordOrders)) {
                ordOrder = ordOrders.get(0);
                elem.setOrderType(Integer.valueOf(ordOrder.getOrderType()));
                elem.setBorrowingCount(ordOrder.getBorrowingCount());
                elem.setBorrowingTerm(ordOrder.getBorrowingTerm().toString());
                elem.setRefundTime(ordOrder.getRefundTime());
                elem.setUserUuid(ordOrder.getUserUuid());
                elem.setAmountApply(ordOrder.getAmountApply());
                //search user name;
                UsrUser usrUser = new UsrUser();
                usrUser.setDisabled(0);
                usrUser.setUuid(ordOrder.getUserUuid());
                List<UsrUser> usrUsers = usrDao.scan(usrUser);
                if (!CollectionUtils.isEmpty(usrUsers)) {
                    elem.setRealName(usrUsers.get(0).getRealName());
                }
            }

            //search voice check config.
            getConfig(elem);
            CollectionOrderDetail coll = new CollectionOrderDetail();
            coll.setDisabled(0);
            coll.setOrderUUID(elem.getUuid());
            coll.setSourceType(1);
            List<CollectionOrderDetail> details = collectionOrderDetailDao.scan(coll);
            if (!CollectionUtils.isEmpty(details)) {
                elem.setQualityCollectorName(getUserRealName((details.get(0).getSubOutSourceId() != null &&
                        !details.get(0).getSubOutSourceId().equals(0)) ? details.get(0).getSubOutSourceId() :
                        details.get(0).getOutsourceId()));
                elem.setIsAssigned(1);
            }

        });

    }
    @Autowired
    private ManQualityCheckConfigDao manQualityCheckConfigDao;

    /**
     * 查询质检记录表 并分装数据
     * @param response
     * @return
     */
    private void getConfig(CollectionOrderResponse response) {

        List<CollectionOrderResponse> lists = manQualityCheckConfigDao.listCheckReordByOrderNo(response.getUuid());
        if (CollectionUtils.isEmpty(lists)) {
            return ;
        }
        Map<Integer, List<CollectionOrderResponse>> maps = lists.stream().
                collect(Collectors.groupingBy(CollectionOrderResponse::getOrderTag));

        for (Integer key : maps.keySet()) {
            List<CollectionOrderResponse> responses = maps.get(key);
            if (CollectionUtils.isEmpty(responses)) {
                continue;
            }
            Optional<CollectionOrderResponse> optional  = responses.stream().sorted(Comparator.comparing(CollectionOrderResponse:: getId)
                    .reversed()).findFirst();
            if (optional.isPresent()) {
                CollectionOrderResponse collectionOrderResponse = optional.get();
                switch (collectionOrderResponse.getOrderTag()) {
                    case 0:
                        response.setCheckResult(collectionOrderResponse.getCheckResult());
                        response.setCheckResultInn(collectionOrderResponse.getCheckResultInn());
                        response.setCheckResultRemark(collectionOrderResponse.getCheckResultRemark());
                        break;
                    case 1:
                        response.setVoiceCheckResult(collectionOrderResponse.getCheckResult());
                        response.setVoiceCheckResultInn(collectionOrderResponse.getCheckResultInn());
                        response.setVoiceCheckResultRemark(collectionOrderResponse.getCheckResultRemark());
                        break;
                    case 2:
                        response.setWACheckResult(collectionOrderResponse.getCheckResult());
                        response.setWACheckResultInn(collectionOrderResponse.getCheckResultInn());
                        response.setWACheckResultRemark(collectionOrderResponse.getCheckResultRemark());
                        break;
                    case 3:
                        response.setCheckResultSec(collectionOrderResponse.getCheckResult());
                        response.setCheckResultSecInn(collectionOrderResponse.getCheckResultInn());
                        break;
                    case 4:
                        response.setVoiceCheckResultSec(collectionOrderResponse.getCheckResult());
                        response.setVoiceCheckResultSecInn(collectionOrderResponse.getCheckResultInn());
                        break;
                    case 5:
                        response.setWACheckResultSec(collectionOrderResponse.getCheckResult());
                        response.setWACheckResultSecInn(collectionOrderResponse.getCheckResultInn());
                        break;
                    default :
                        break;
                }


            }
        }


    }


    private String getUserRealName(Integer id) {
        ManUser manUser = new ManUser();
        manUser.setId(id);
        List<ManUser> list = manUserDao.scan(manUser);
        if (CollectionUtils.isEmpty(list)) {
            return "";
        }
        return list.get(0).getRealname();
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean assignCollectionOrder(Integer outsourceId
            , String orderUUID, Boolean isThird, Integer sourceType) {

        String lockKey = ConstantsForLock.PREFIX_COLLECTION_ASSIGNMENT_LOCK + orderUUID;
        boolean fetchLock = redisClient
                .lockRepeat(lockKey);
        if (!fetchLock) {
            log.error("fetch collection order assignment lock failed, param = {}",
                    JsonUtils.serialize(lockKey));
            return false;
        }
        CollectionOrderDetail detail = new CollectionOrderDetail();
        detail.setOrderUUID(orderUUID);
        detail.setDisabled(0);
        detail.set_orderBy(" sourceType asc ");
        List<CollectionOrderDetail> dbAssignedList = collectionOrderDetailDao.scan(detail);
        detail.setCreateTime(new Date());
        detail.setUpdateTime(new Date());
        detail.setCreateUser(LoginSysUserInfoHolder.getLoginSysUserId());
        detail.setUpdateUser(LoginSysUserInfoHolder.getLoginSysUserId());
        detail.setAssignedTime(new Date());
        detail.setIsTest(0);
        if (sourceType.equals(1)) {
            detail.setOutsourceId(outsourceId);
            //新增质检
            detail.setSourceType(1);
            if (!CollectionUtils.isEmpty(dbAssignedList) && dbAssignedList.size() == 1) {
                detail.setOrderTag(dbAssignedList.get(0).getOrderTag());
                detail.setPromiseRepaymentTime(dbAssignedList.get(0).getPromiseRepaymentTime());
                collectionOrderDetailDao.insert(detail);
            } else if (!CollectionUtils.isEmpty(dbAssignedList) && dbAssignedList.size() == 2) {
                detail.setId(dbAssignedList.get(1).getId());
                collectionOrderDetailDao.update(detail);
            }
        } else {
            //当为委外催收时，子账号进行分配
            if (!isThird) {
                detail.setOutsourceId(outsourceId);
                detail.setSubOutSourceId(0);
            } else {
                detail.setSubOutSourceId(outsourceId);
            }
            if (CollectionUtils.isEmpty(dbAssignedList)) {
                //insert
                collectionOrderDetailDao.insert(detail);
            } else {
                //update
                detail.setId(dbAssignedList.get(0).getId());
                collectionOrderDetailDao.update(detail);
            }
        }


        CollectionOrderHistory assignedHistory = new CollectionOrderHistory();
        assignedHistory.setOrderUUID(orderUUID);
        assignedHistory.setOutsourceId(outsourceId);
        assignedHistory.setCreateTime(new Date());
        assignedHistory.setUpdateTime(new Date());
        assignedHistory.setCreateUser(LoginSysUserInfoHolder.getLoginSysUserId());
        assignedHistory.setUpdateUser(LoginSysUserInfoHolder.getLoginSysUserId());
        assignedHistory.setSourceType(sourceType);

        //insert history
        collectionOrderHistoryDao.insert(assignedHistory);
        redisClient.unlockRepeat(lockKey);
        return true;
    }


    /***
     * 查询催收人员角色id
     * @Param sourceType 根据是0还是1判断是
     * @return
     */
    private Integer[] getCollectorRoleId(Integer sourceType) {


        Integer[] results = new Integer[2];
        try {
            if (sourceType.equals(1)) {
                List<SysDicItemModel> dicItemList = sysDicService.
                        sysDicItemsListByDicCode(DictCollectionEnum.QUALITY_CHECK_POST.name());
                if (CollectionUtils.isEmpty(dicItemList)) {
                    log.error("the QUALITY_CHECK_POST is not configure");
                    return null;
                }
                Optional<SysDicItemModel> dicItem = dicItemList.stream().filter(
                        elem -> ConstantsForDicItem.QUALITCHECK_ROLE_ID1.equals(elem.getDicItemName()))
                        .findFirst();
                if (dicItem.isPresent()) {
                    results[0] = Integer.valueOf(dicItem.get().getDicItemValue().trim());
                }
                return results;
            }

            List<SysDicItemModel> dicItemList = sysDicService.
                    sysDicItemsListByDicCode(DictCollectionEnum.COLLECTION_ROLE_PARAM.name());
            if (CollectionUtils.isEmpty(dicItemList)) {
                log.error("the COLLECTION_ROLE_PARAM is not configure");
                return null;
            }
            Optional<SysDicItemModel> dicItem = dicItemList.stream().filter(
                    elem -> ConstantsForDicItem.COLLECTION_ROLE_ID1.equals(elem.getDicItemName()))
                    .findFirst();
            if (dicItem.isPresent()) {
                results[0] = Integer.valueOf(dicItem.get().getDicItemValue().trim());
            }

            Optional<SysDicItemModel> dicItem2 = dicItemList.stream().filter(
                    elem -> ConstantsForDicItem.COLLECTION_ROLE_ID2.equals(elem.getDicItemName()))
                    .findFirst();
            if (dicItem2.isPresent()) {
                results[1] = Integer.valueOf(dicItem2.get().getDicItemValue().trim());
            }
        } catch (Exception e) {
            log.error("get collection role error", e);
        }
        return results;
    }


    /**
     * 获得催收管理- 逾期订单（内）
     */
    public PageData<List<OutCollectionResponse>> outCollectionsList(OverdueOrderRequest param)
            throws Exception {

        PageHelper.startPage(param.getPageNo(), param.getPageSize());
        getManOrderOrderRequest(param);
        List<OutCollectionResponse> collectionResponses = manCollectionDao
                .outCollectionsList(param);
        PageInfo pageInfo = new PageInfo(collectionResponses);
        if (CollectionUtils.isEmpty(collectionResponses)) {
            log.error("the outCollectionsList result is Empty!");
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }

        //封装返回数据
        getCollections(collectionResponses, param);
        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }

    /**
     * 获得催收管理- 逾期订单（内）
     */
    public PageData<List<OutCollectionResponse>> outCollectionsByOutSourceIdList(
            OverdueOrderRequest param)
            throws Exception {

        //判断是否为委外
        if (param.getOutsourceId() == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        ManUser manUser = new ManUser();
        manUser.setDisabled(0);
        manUser.setId(param.getOutsourceId());
        List<ManUser> manUsers = manUserDao.scan(manUser);
        if (CollectionUtils.isEmpty(manUsers)) {
            return new PageData();
        }
        getManOrderOrderRequest(param);
        List<OutCollectionResponse> collectionResponses = new ArrayList<>();
        int total = 0;
        //如果是组长，查询包括其下面所有组员的数据
        if (manUsers.get(0).getId().equals(manUsers.get(0).getParentId())) {
            ManUser user = new ManUser();
            user.setDisabled(0);
            user.setStatus(0);
            user.setParentId(param.getOutsourceId());
            List<ManUser> users = manUserDao.scan(user);
            if (CollectionUtils.isEmpty(users)) {
                log.error("the outCollectionsList result has bug!");
                return null;
            }
            for (ManUser elem : users) {
                if (!StringUtils.isEmpty(param.getCollectiorId())) {
                    if (!elem.getUsername().toLowerCase().contains(param.getCollectiorId().toLowerCase())) {
                        continue;
                    }
                }
                param.setOutsourceId(elem.getId());
                if (elem.getThird() == 0) {
                    param.setIsThird(1);
                } else {
                    param.setIsThird(0);
                }
                List<OutCollectionResponse> lists = manCollectionDao
                        .outCollectionsByOutSourceIdList(param);
                if (!CollectionUtils.isEmpty(lists)) {
                    collectionResponses.addAll(lists);
                }
            }
            total = collectionResponses.size();
            collectionResponses = collectionResponses.stream().sorted(Comparator.comparing(OutCollectionResponse::getRefundTime))
                    .skip((param.getPageNo() -1) * param.getPageSize()).limit(param.getPageSize()).collect(Collectors.toList());
        } else {
            Integer third = manUsers.get(0).getThird();
            if (third == 0) {
                param.setIsThird(1);
            } else {
                param.setIsThird(0);
            }
            PageHelper.startPage(param.getPageNo(), param.getPageSize());
            collectionResponses = manCollectionDao
                    .outCollectionsByOutSourceIdList(param);
            PageInfo pageInfo = new PageInfo(collectionResponses);
            if (CollectionUtils.isEmpty(collectionResponses)) {
                log.error("the outCollectionsList result is Empty!");
                return PageDataUtils.mapPageInfoToPageData(pageInfo);
            }
            //封装返回数据
            getCollections(collectionResponses, param);
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }

        PageData pageData = new PageData();
        if (CollectionUtils.isEmpty(collectionResponses)) {
            log.error("the outCollectionsList result is Empty!");
            return pageData;
        }
        pageData.setData(collectionResponses);
        pageData.setRecordsTotal(total);
        pageData.setPageNo(param.getPageNo());
        pageData.setPageSize(param.getPageSize());

        //封装返回数据
        getCollections(collectionResponses, param);
        return pageData;
    }


    /**
     * 查出催收列表当天为发薪日的单子
     */
    public Integer payDayOrderCount(OverdueOrderRequest param) throws ServiceExceptionSpec{

        //判断是否为委外
        if (param.getOutsourceId() == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        ManUser manUser = new ManUser();
        manUser.setDisabled(0);
        manUser.setId(param.getOutsourceId());
        List<ManUser> manUsers = manUserDao.scan(manUser);
        if (CollectionUtils.isEmpty(manUsers)) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        param.setPayDay(calendar.get(Calendar.DAY_OF_MONTH));
        int result = 0;
        //如果是组长，查询包括组员的数据
        if (manUsers.get(0).getId().equals(manUsers.get(0).getParentId())) {
            ManUser user = new ManUser();
            user.setDisabled(0);
            user.setStatus(0);
            user.setParentId(param.getOutsourceId());
            List<ManUser> users = manUserDao.scan(user);
            if (CollectionUtils.isEmpty(users)) {
                log.error("the payDayOrderCount result has bug!");
                return 0;
            }
            for (ManUser elem : users) {
                param.setOutsourceId(elem.getId());
                if (elem.getThird() == 0) {
                    param.setIsThird(1);
                } else {
                    param.setIsThird(0);
                }
                result += manCollectionDao.payDayCount(param);
            }
        } else {
            Integer third = manUsers.get(0).getThird();
            if (third == 0) {
                param.setIsThird(1);
            } else {
                param.setIsThird(0);
            }
            result = manCollectionDao.payDayCount(param);
        }


        return result;
    }

    /**
     * 通过订单号，查询订单催收备注
     */
    public List<ManOrderRemarkResponse> manOrderRemarkList(OrderSearchRequest param)
            throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(param.getUuid())) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
            return new ArrayList<>();
        }
        List<ManOrderRemarkResponse> responseList = manCollectionDao.manOrderRemarkList(param);
        List<ManOrderRemarkResponse> responseListNew = manCollectionDao.manOrderRemarkListNew(param);
        if (!CollectionUtils.isEmpty(responseListNew)) {
            responseList.addAll(responseListNew);
        }

        //催收权限号码加密
//        if (!manAuthManagerService.hasAuthorityByRoleName(ManCollectorOperatorEnum.listCollectorOperatorEnum())) {
//            if (!CollectionUtils.isEmpty(responseList)) {
//                responseList = responseList.stream().map(elem -> {
//                    if (!StringUtils.isEmpty(elem.getContactMobile())) {
//                        elem.setContactMobile(elem.getContactMobile().substring(0,3) + "*****"
//                                + elem.getContactMobile().substring(7));
//                    }
//                    return elem;
//                }).collect(Collectors.toList());
//            }
//        }
        return responseList;
    }

    /**
     * 通过用户UUID获得历史最大逾期天数
     */
    private Long getMaxOverdueDaysByUserUuid(String uuid) {

        if (StringUtils.isEmpty(uuid)) {
            return -1L;
        }
        List<OrdOrder> orders = manOrderOrderDao.getOverDayOrder(uuid);
        if (CollectionUtils.isEmpty(orders)) {
            return 0L;
        }

        //找到逾期天数最大的单
        Optional<OrdOrder> maxOverdueOrder = orders.stream().max((OrdOrder e1, OrdOrder e2) -> {
            long overdueDay1 = DateUtils.getDiffDaysIgnoreHours(e1.getRefundTime(),
                    e1.getActualRefundTime() == null ?
                            new Date() : e1.getActualRefundTime());
            long overdueDay2 = DateUtils.getDiffDaysIgnoreHours(e2.getRefundTime(),
                    e2.getActualRefundTime() == null ?
                            new Date() : e2.getActualRefundTime());
            if (overdueDay1 == overdueDay2) {
                return 0;
            }
            return overdueDay1 - overdueDay2 > 0 ? 1 : -1;
        });
        if (!maxOverdueOrder.isPresent()) {
            return 0L;
        }
        return DateUtils.getDiffDaysIgnoreHours(maxOverdueOrder.get().getRefundTime(),
                maxOverdueOrder.get().getActualRefundTime() == null ? new Date()
                        : maxOverdueOrder.get().getActualRefundTime());


    }

    /**
     * 没有权限查询催收已完成订单列表
     */
    public PageData<List<PaymentOrderResponse>> repaymentOrderList(PaidOrderRequest param)
            throws Exception {

        PageHelper.startPage(param.getPageNo(), param.getPageSize());
        getManOrderOrderRequest(param);
        List<PaymentOrderResponse> collectionResponses = manCollectionDao.repaymentOrderList(param);
        PageInfo pageInfo = new PageInfo(collectionResponses);
        if (CollectionUtils.isEmpty(collectionResponses)) {
            log.error("the repaymentOrderList result is Empty!");
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }

        //封装返回数据
        getPaymentOrderResponse(collectionResponses);
        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }

    /**
     * 包含权限查询催收已完成订单列表
     */
    public PageData<List<PaymentOrderResponse>> repaymentOrderByOutSourceIdList(
            PaidOrderRequest param) throws Exception {

        //判断是否为委外
        if (param.getOutsourceId() == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        ManUser manUser = new ManUser();
        manUser.setDisabled(0);
        manUser.setId(param.getOutsourceId());
        List<ManUser> manUsers = manUserDao.scan(manUser);
        if (CollectionUtils.isEmpty(manUsers)) {
            return new PageData();
        }
        getManOrderOrderRequest(param);
        List<PaymentOrderResponse> collectionResponses = new ArrayList<>();
        int total = 0;
        //如果是组长，查询包括其下面所有组员的数据
        if (manUsers.get(0).getId().equals(manUsers.get(0).getParentId())) {
            ManUser user = new ManUser();
            user.setDisabled(0);
            user.setStatus(0);
            user.setParentId(param.getOutsourceId());
            List<ManUser> users = manUserDao.scan(user);
            if (CollectionUtils.isEmpty(users)) {
                log.error("the outCollectionsList result has bug!");
                return null;
            }
            for (ManUser elem : users) {
                if (!StringUtils.isEmpty(param.getCollectiorId())) {
                    if (!elem.getUsername().toLowerCase().contains(param.getCollectiorId().toLowerCase())) {
                        continue;
                    }
                }
                param.setOutsourceId(elem.getId());
                if (elem.getThird() == 0) {
                    param.setIsThird(1);
                } else {
                    param.setIsThird(0);
                }
                List<PaymentOrderResponse> lists = manCollectionDao
                        .repaymentOrderByOutSourceIdList(param);
                if (!CollectionUtils.isEmpty(lists)) {
                    collectionResponses.addAll(lists);
                }
            }
            total = collectionResponses.size();
            collectionResponses = collectionResponses.stream().sorted(Comparator.comparing(PaymentOrderResponse::getRefundTime))
                    .skip((param.getPageNo() -1) * param.getPageSize()).limit(param.getPageSize()).collect(Collectors.toList());
        } else {
            PageHelper.startPage(param.getPageNo(), param.getPageSize());
            Integer third = manUsers.get(0).getThird();
            if (third == 0) {
                param.setIsThird(1);
            } else {
                param.setIsThird(0);
            }
            collectionResponses = manCollectionDao
                    .repaymentOrderByOutSourceIdList(param);
            PageInfo pageInfo = new PageInfo(collectionResponses);
            if (CollectionUtils.isEmpty(collectionResponses)) {
                log.error("the outCollectionsList result is Empty!");
                return PageDataUtils.mapPageInfoToPageData(pageInfo);
            }
            //封装返回数据
            getPaymentOrderResponse(collectionResponses);
            return PageDataUtils.mapPageInfoToPageData(pageInfo);

        }

        PageData pageData = new PageData();
        pageData.setData(collectionResponses);
        pageData.setRecordsTotal(total);
        pageData.setPageNo(param.getPageNo());
        pageData.setPageSize(param.getPageSize());

        //封装返回数据
        getPaymentOrderResponse(collectionResponses);
        return pageData;
    }

    /**
     * 封装完成订单数据
     */
    private void getPaymentOrderResponse(List<PaymentOrderResponse> collectionResponses)
            throws Exception {
        //封装返回数据
        for (PaymentOrderResponse response : collectionResponses) {
            if (response.getRefundTime() != null) {
                //逾期天数
                long overdueDay = DateUtils
                        .getDiffDaysIgnoreHours(response.getRefundTime(), response.getActualRefundTime());
                response.setOverdueDays(Long.valueOf(overdueDay));
            }
            if (!StringUtils.isEmpty(response.getUuid())) {
                //封装手机号码
                ManUserUserRequest manUserUserRequest = new ManUserUserRequest();
                manUserUserRequest.setUserUuid(response.getUuid());
                UsrUser usrUser = userUserService.userMobileByUuid(manUserUserRequest);
                String mobile = usrUser.getMobileNumberDES();
                if (StringUtils.isEmpty(mobile)) {
                    continue;
                }
                //电话号码中间四位掩码
                int length = mobile.length();
                StringBuffer sf = new StringBuffer();
                for (int index = 0; index < length - 7; index++) {
                    sf = sf.append("*");
                }
                mobile = mobile.substring(0, 3) + sf.toString() + mobile.substring(length - 4);
                response.setMobile(mobile);

            }

            if (response.getOrderType() != null) {

                if (response.getOrderType().equals(1)) {
                    response.setExtendType(1);
                } else if (response.getOrderType().equals(2)) {
                    response.setCalType(1);
                }
            }
            commonService.setCollectionOrdTerms(response);
        }
    }


    /**
     * 封装催收订单数据
     */
    private void getCollections(List<OutCollectionResponse> collectionResponses,
                                OverdueOrderRequest param) throws Exception {

        for (OutCollectionResponse response : collectionResponses) {
            if (response.getRefundTime() != null) {
                //逾期天数
                long overdueDay = DateUtils
                        .getDiffDaysIgnoreHours(response.getRefundTime(), new Date());
                response.setOverdueDays(Long.valueOf(overdueDay));
                //最大逾期天数
                response.setMaxOverdueDays(getMaxOverdueDaysByUserUuid(response.getUuid()));
            }
            if (!StringUtils.isEmpty(response.getUuid())) {
                //封装手机号码
                ManUserUserRequest manUserUserRequest = new ManUserUserRequest();
                manUserUserRequest.setUserUuid(response.getUuid());
                UsrUser usrUser = userUserService.userMobileByUuid(manUserUserRequest);
                String mobile = usrUser.getMobileNumberDES();
                if (!StringUtils.isEmpty(mobile)) {
                    //电话号码中间四位掩码
                    int length = mobile.length();
                    StringBuffer sf = new StringBuffer();
                    for (int index = 0; index < length - 7; index++) {
                        sf = sf.append("*");
                    }
                    mobile = mobile.substring(0, 3) + sf.toString() + mobile.substring(length - 4);
                    response.setMobile(mobile);

                    //封装银行卡信息
                    List<UsrBank> usrBanks = usrBankDao.getUsrBankByUsrId(response.getUuid());
                    if (!CollectionUtils.isEmpty(usrBanks)) {
                        UsrBank bank = usrBanks.get(0);
                        String bankNumberNo = bank.getBankNumberNo();
                        response.setUserBank(bank.getBankCode() + "\n (" + bankNumberNo
                                .substring(bankNumberNo.length() - 4) + ")");
                    }
                }


            }
            //封装跟进时间
            if (!StringUtils.isEmpty(response.getOrderNo())) {
                param.setUuid(response.getOrderNo());
                List<ManOrderRemarkResponse> manOrderRemarkResponses = this
                        .manOrderRemarkList(param);
                if (!CollectionUtils.isEmpty(manOrderRemarkResponses)) {
                    response.setFollowTime(manOrderRemarkResponses.get(0).getCreateTime());
                }
            }

            //封装订单催收联系情况
            Integer[] collectionContactResult = new Integer[] {0,0,0,0,0,0,0,0};
            ManCollectionRemark manCollectionRemark = new ManCollectionRemark();
            manCollectionRemark.setDisabled(0);
            manCollectionRemark.setOrderNo(response.getOrderNo());
            List<ManCollectionRemark> lists = manCollectionRemarkDao.scan(manCollectionRemark);
            if (CollectionUtils.isEmpty(lists)) {
                continue;
            }
            for (ManCollectionRemark list: lists) {
                if (list.getContactType() != null) {
                    if (list.getContactType().equals(ContactTypeEnum.HIMSELEF_PHONE.getType())) {
                        collectionContactResult[0] = 1;
                    } else if (list.getContactType().equals(ContactTypeEnum.HIMSELEF_WA.getType())) {
                        collectionContactResult[1] = 1;
                    } else if (list.getContactType().equals(ContactTypeEnum.CONTACT1.getType())) {
                        collectionContactResult[2] = 1;
                    } else if (list.getContactType().equals(ContactTypeEnum.CONTACT2.getType())) {
                        collectionContactResult[3] = 1;
                    } else if (list.getContactType().equals(ContactTypeEnum.CONTACT3.getType())) {
                        collectionContactResult[4] = 1;
                    } else if (list.getContactType().equals(ContactTypeEnum.CONTACT4.getType())) {
                        collectionContactResult[5] = 1;
                    } else if (list.getContactType().equals(ContactTypeEnum.CONTACT_RECORD.getType())) {
                        collectionContactResult[6] = 1;
                    } else if (list.getContactType().equals(ContactTypeEnum.CALL_RECORD.getType())) {
                        collectionContactResult[7] = 1;
                    }
                }

            }
            response.setCollectionContactResult(collectionContactResult);

            commonService.setCollectionOrdTerms(response);
        }
    }

    /**
     * 委外催收列表
     * @param req
     * @return
     */
    public PageData<List<CollectionOrderResponse>> getAssignableOverdueOutSourceOrderList(
            AssignableCollectionOrderReq req) {
        PageHelper.startPage(req.getPageNo(), req.getPageSize());
        getManOrderOrderRequest(req);
        List<CollectionOrderResponse> orderList = manCollectionDao
                .getAssignableOverdueOutSourceOrderList(req);

        PageInfo pageInfo = new PageInfo(orderList);
        if (CollectionUtils.isEmpty(orderList)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }

        orderList.stream().forEach(elem -> {
            //设置复借信息
            elem.setIsRepeatBorrowing(
                    elem.getBorrowingCount() != null && elem.getBorrowingCount() > 1 ? 1 : 0);
            elem.setReBorrowingCount(
                    elem.getBorrowingCount() == null ? 0 : elem.getBorrowingCount() - 1);
            //设置逾期信息
            elem.setOverdueDays(DateUtils.getDiffDaysIgnoreHours(elem.getRefundTime(), new Date()));

            commonService.setCollectionOrdTerms(elem);
        });

        //设置催收人员信息
        List<Integer> collectorIds = orderList.stream()
                .filter(elem -> elem.getOutsourceId() != null).map(elem -> elem.getOutsourceId())
                .collect(
                        Collectors.toList());
        if (CollectionUtils.isEmpty(collectorIds)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }

        List<ManUser> sysUserList = manUserService.getSysUserByIds(collectorIds);
        if (CollectionUtils.isEmpty(sysUserList)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }

        Map<String, ManUser> sysUserMap = sysUserList.stream()
                .collect(Collectors.toMap(elem -> elem.getId().toString(), elem -> elem));
        orderList.stream().filter(elem -> elem.getOutsourceId() != null).forEach(elem -> {
            if (sysUserMap != null && sysUserMap.get(elem.getOutsourceId().toString()) != null) {
                elem.setCollectorName(
                        sysUserMap.get(elem.getOutsourceId().toString()).getRealname());
            }
        });
        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }

    /**
     * 通过委外公司查询其子账号
     * @param outSourceId
     * @return
     */
    public List<CollectorResponseInfo> getOutSourceInfo(
            Integer outSourceId) throws Exception {

        if (outSourceId == null) {
            return new ArrayList<>();
        }

        List<SysDicItemModel> dicItemList = sysDicService.
                sysDicItemsListByDicCode(DictCollectionEnum.THIRD_COMPANY.name() +
                        "_" + String.valueOf(outSourceId));

        if (CollectionUtils.isEmpty(dicItemList)) {
            return new ArrayList<>();
        }

        return dicItemList.stream().map(elem -> new CollectorResponseInfo(
                elem.getDicItemValue(), elem.getDicItemName())).collect(Collectors.toList());

    }


    private void getManOrderOrderRequest(AssignableCollectionOrderReq searchResquest) {
        //封装是否展期
        if (searchResquest.getExtendType() != null && searchResquest.getExtendType().equals(1)
                && searchResquest.getCalType() != null && searchResquest.getCalType().equals(1)) {
            searchResquest.setOrderType(-1);
        } else if (searchResquest.getCalType() != null && searchResquest.getCalType().equals(1)) {
            searchResquest.setOrderType(2);
        } else if (searchResquest.getExtendType() != null && searchResquest.getExtendType().equals(0)
                && searchResquest.getCalType() != null && searchResquest.getCalType().equals(0)) {
            searchResquest.setOrderType(0);
        } else if (searchResquest.getExtendType() != null && searchResquest.getExtendType().equals(1)) {
            searchResquest.setOrderType(1);
        }
    }
    private void getManOrderOrderRequest(OrderSearchRequest searchResquest) {
        //封装是否展期
        if (searchResquest.getExtendType() != null && searchResquest.getExtendType().equals(1)
                && searchResquest.getCalType() != null && searchResquest.getCalType().equals(1)) {
            searchResquest.setOrderType(-1);
        } else if (searchResquest.getCalType() != null && searchResquest.getCalType().equals(1)) {
            searchResquest.setOrderType(2);
        } else if (searchResquest.getExtendType() != null && searchResquest.getExtendType().equals(0)
                && searchResquest.getCalType() != null && searchResquest.getCalType().equals(0)) {
            searchResquest.setOrderType(0);
        } else if (searchResquest.getExtendType() != null && searchResquest.getExtendType().equals(1)) {
            searchResquest.setOrderType(1);
        }
    }

    /**
     * 根据订单号，将其加入催收黑名单
     * @param uuid
     */
    @Transactional
    public Boolean addCollectionBlackList(String uuid, Integer type, Integer sourceType) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(uuid)
                || type == null || type.equals(0)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        String lockKey = ConstantsForLock.PREFIX_COLLECTION_ASSIGNMENT_LOCK + uuid;
        boolean fetchLock = redisClient
                .lockRepeat(lockKey);
        if (!fetchLock) {
            log.error("fetch collection order assignment lock failed, param = {}",
                    JsonUtils.serialize(lockKey));
            return false;
        }
        //type 为1时，加入到催收黑名单中
        if (type.equals(1)) {
            //先查询订单状态是否为待还款
            OrdOrder ordOrder = new OrdOrder();
            ordOrder.setUuid(uuid);
            ordOrder.setDisabled(0);
            List<OrdOrder> orders = manOrderOrderDao.scan(ordOrder);
            if (CollectionUtils.isEmpty(orders)) {
                throw new ServiceExceptionSpec(ExceptionEnum.ORDER_NOT_FOUND);
            }
            Integer status = orders.get(0).getStatus();
            if (OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode() != status &&
                    OrdStateEnum.RESOLVING_OVERDUE.getCode() != status) {
                throw new ServiceExceptionSpec(ExceptionEnum.ORDER_STATES_ERROR);
            }

            CollectionOrderDetail detail = new CollectionOrderDetail();
            detail.setOrderUUID(uuid);
            detail.setDisabled(0);
            detail.setSourceType(sourceType);
            List<CollectionOrderDetail> dbAssignedList = collectionOrderDetailDao.scan(detail);
            detail.setCreateTime(new Date());
            detail.setUpdateTime(new Date());
            detail.setCreateUser(LoginSysUserInfoHolder.getLoginSysUserId());
            detail.setUpdateUser(LoginSysUserInfoHolder.getLoginSysUserId());
            detail.setAssignedTime(new Date());
            detail.setRemark("手动加入黑名单");
            detail.setIsTest(0);
            //催收黑名单账号写死
            detail.setOutsourceId(207);
            detail.setSubOutSourceId(0);

            if (CollectionUtils.isEmpty(dbAssignedList)) {
                //insert
                collectionOrderDetailDao.insert(detail);
            } else {
                //update
                detail.setId(dbAssignedList.get(0).getId());
                collectionOrderDetailDao.update(detail);
            }

            CollectionOrderHistory assignedHistory = new CollectionOrderHistory();
            assignedHistory.setOrderUUID(uuid);
            assignedHistory.setOutsourceId(207);
            assignedHistory.setRemark("手动加入黑名单");
            assignedHistory.setCreateTime(new Date());
            assignedHistory.setUpdateTime(new Date());
            assignedHistory.setCreateUser(LoginSysUserInfoHolder.getLoginSysUserId());
            assignedHistory.setUpdateUser(LoginSysUserInfoHolder.getLoginSysUserId());
            assignedHistory.setSourceType(sourceType);

            //insert history
            collectionOrderHistoryDao.insert(assignedHistory);
        } else if (type.equals(2)) {
            CollectionOrderDetail detail = new CollectionOrderDetail();
            detail.setOrderUUID(uuid);
            detail.setDisabled(0);
            List<CollectionOrderDetail> dbAssignedList = collectionOrderDetailDao.scan(detail);
            detail.setCreateTime(new Date());
            detail.setUpdateTime(new Date());
            detail.setDisabled(0);
            detail.setCreateUser(LoginSysUserInfoHolder.getLoginSysUserId());
            detail.setUpdateUser(LoginSysUserInfoHolder.getLoginSysUserId());
            detail.setAssignedTime(new Date());
            detail.setRemark(dbAssignedList.get(0).getRemark() + ";手动移出黑名单");
            detail.setIsTest(0);
            detail.setOutsourceId(0);
            detail.setSubOutSourceId(0);
            detail.setSourceType(sourceType);
            //update
            detail.setId(dbAssignedList.get(0).getId());
            collectionOrderDetailDao.update(detail);
        }

        redisClient.unlockRepeat(lockKey);
        return true;
    }

    /**
     * 根据postId 获取每个阶段催收的评分
     * @param postId
     * @return
     */
    public String getScoreReport(Integer postId, String sessionId) throws Exception {


        if (postId == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        //获取阶段
        SysDicItem sysDicItem = new SysDicItem();
        sysDicItem.setId(postId);
        sysDicItem.setDisabled(0);
        List<SysDicItem> posts = sysDicItemDao.scan(sysDicItem);
        if (CollectionUtils.isEmpty(posts)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        String post = posts.get(0).getDicItemName();
        Float serviceMentality = null;
        Float communicationBility = null;

        //获取评分的比例
        List<SysDicItemModel> dicItemList = sysDicService.
                sysDicItemsListByDicCode("usrEvaluateScoreConfig");
        if (!CollectionUtils.isEmpty(dicItemList)) {
            for (SysDicItemModel item : dicItemList) {
                if ("serviceMentality".equals(item.getDicItemName())) {
                    serviceMentality = Float.valueOf(item.getDicItemValue());
                } else if ("communicationBility".equals(item.getDicItemName())) {
                    communicationBility = Float.valueOf(item.getDicItemValue());
                }
            }
        }
        if (serviceMentality == null || communicationBility == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.ORDER_PRODUCT_CONFIG_IS_NULL);
        }
        //创建workbook
        HSSFWorkbook wb = new HSSFWorkbook();
        //查询总共的数据
        List<CollectorScoreResponse> totalList = manCollectionDao.getTotalScore(postId);
        List<CollectorScoreResponse> rList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(totalList)) {
            //循环进行封装数据
            for (CollectorScoreResponse response : totalList) {
                if (response.getCount() != null && response.getCount() < 10) {
                    continue;
                }
                //分装催收人员
                ManUser manUser = new ManUser();
                manUser.setDisabled(0);
                manUser.setUuid(response.getUserUuid());
                List<ManUser> manUsers = manUserDao.scan(manUser);
                if (CollectionUtils.isEmpty(manUsers)) {
                    continue;
                }
                CollectorScoreResponse scoreResponse = new CollectorScoreResponse();
                //计算总评分
                Float temp = (response.getCommunicationBility() * communicationBility
                        + response.getServiceMentality() * serviceMentality) / response.getCount();
                BigDecimal total = BigDecimal.valueOf((double)temp);
                float result = total.setScale(1,  BigDecimal.ROUND_HALF_UP).floatValue();
                scoreResponse.setTotalScore(result);
                scoreResponse.setAvgCommunicationBility(
                        BigDecimal.valueOf(response.getCommunicationBility()*1.0/response.getCount()));
                scoreResponse.setAvgServiceMentality(
                        BigDecimal.valueOf(response.getServiceMentality()*1.0/response.getCount()));
                scoreResponse.setUserUuid(manUsers.get(0).getUsername());
                scoreResponse.setPostId(post);
                rList.add(scoreResponse);
            }
            if (!CollectionUtils.isEmpty(rList)) {
                setSheet1(rList, wb);
            }
        }
        //获取详细数据并封装
        List<CollectorScoreResponse> detailList = manCollectionDao.getDetailScore(postId);
        if (!CollectionUtils.isEmpty(detailList)) {
            //封装评分时间
            for (CollectorScoreResponse response : detailList) {
               if (response.getCreateTime() != null) {
                   SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                   response.setCreateTimeStr(sf.format(response.getCreateTime()));
               }
            }
            setSheet2(detailList, wb);
        }

        try {
            FileOutputStream fout = new FileOutputStream(filePath + "templates.xls");
            wb.write(fout);
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UploadResultInfo uploadResultInfo =
                uploadService.uploadFile(sessionId, filePath + "templates.xls");
        return uploadResultInfo.getData();
    }

    private void setSheet1(List<CollectorScoreResponse> countResponses, HSSFWorkbook wb) {
        HSSFSheet sheet = wb.createSheet("汇总");
        sheet.setColumnWidth(0, 5555);
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);  //居中
        HSSFCell cell = row.createCell(0); //第一个单元格
        cell.setCellValue("催收人员");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("所属阶段");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("评分");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("服务意识评分");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("沟通能力评分");
        cell.setCellStyle(style);

        for(int i = 0; i < countResponses.size(); i++) {
            row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(countResponses.get(i).getUserUuid());
            row.createCell(1).setCellValue(countResponses.get(i).getPostId());
            row.createCell(2).setCellValue(countResponses.get(i).getTotalScore());
            row.createCell(3).setCellValue(countResponses.get(i).getAvgServiceMentality()
                    .setScale(1,  BigDecimal.ROUND_HALF_UP).floatValue());
            row.createCell(4).setCellValue(countResponses.get(i).getAvgCommunicationBility()
                    .setScale(1,  BigDecimal.ROUND_HALF_UP).floatValue());
        }
    }

    private void setSheet2(List<CollectorScoreResponse> countResponses, HSSFWorkbook wb) {
        HSSFSheet sheet = wb.createSheet("详情");
        sheet.setColumnWidth(0, 5555);
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);  //居中
        HSSFCell cell = row.createCell(0); //第一个单元格
        cell.setCellValue("用户姓名");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("评分时间");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("催收人员");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("所属阶段（催收人员所属阶段）");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("服务意识评分");
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue("沟通能力评分");
        cell.setCellStyle(style);

        for(int i = 0; i < countResponses.size(); i++) {
            row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(countResponses.get(i).getUserName());
            row.createCell(1).setCellValue(countResponses.get(i).getCreateTimeStr());
            row.createCell(2).setCellValue(countResponses.get(i).getUserUuid());
            row.createCell(3).setCellValue(countResponses.get(i).getPostId());
            row.createCell(4).setCellValue(countResponses.get(i).getServiceMentality());
            row.createCell(5).setCellValue(countResponses.get(i).getCommunicationBility());
        }
    }

    /**
     * 二次质检列表
     * @param request
     */
    public PageData<List<CollectionOrderResponse>> secondQualityCheck(AssignableCollectionOrderReq request) {

        PageHelper.startPage(request.getPageNo(), request.getPageSize());

        List<CollectionOrderResponse> result = manCollectionDao.secondQualityCheck(request);
        PageInfo pageInfo = new PageInfo(result);
        if (CollectionUtils.isEmpty(result)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }
        //设置催收人员信息
        List<Integer> collectorIds = new ArrayList<>();
        result.stream().filter(e -> !StringUtils.isEmpty(e.getUuid())).forEach(elem -> {
            //查询最新质检结果
            getConfig(elem);
            if (elem.getSubOutSourceId() != null
                    && elem.getSubOutSourceId() != 0) {
                elem.setOutsourceId(elem.getSubOutSourceId());
            }
            if (elem.getOutsourceId() != null
                    && elem.getOutsourceId() != 0) {
                collectorIds.add(elem.getOutsourceId());
            }
            OrdOrder ordOrder = new OrdOrder();
            ordOrder.setDisabled(0);
            ordOrder.setUuid(elem.getUuid());
            List<OrdOrder> ordOrders = manOrderOrderDao.scan(ordOrder);
            if (!CollectionUtils.isEmpty(ordOrders)) {
                ordOrder = ordOrders.get(0);
                elem.setBorrowingCount(ordOrder.getBorrowingCount());
                elem.setBorrowingTerm(ordOrder.getBorrowingTerm().toString());
                elem.setRefundTime(ordOrder.getRefundTime());
                elem.setUserUuid(ordOrder.getUserUuid());
                elem.setAmountApply(ordOrder.getAmountApply());
                //search user name;
                UsrUser usrUser = new UsrUser();
                usrUser.setDisabled(0);
                usrUser.setUuid(ordOrder.getUserUuid());
                List<UsrUser> usrUsers = usrDao.scan(usrUser);
                if (!CollectionUtils.isEmpty(usrUsers)) {
                    elem.setRealName(usrUsers.get(0).getRealName());
                }

                //设置复借信息
                elem.setIsRepeatBorrowing(
                        elem.getBorrowingCount() != null && elem.getBorrowingCount() > 1 ? 1 : 0);
                elem.setReBorrowingCount(
                        elem.getBorrowingCount() == null ? 0 : elem.getBorrowingCount() - 1);
                //设置逾期信息
                elem.setOverdueDays(DateUtils.getDiffDaysIgnoreHours(elem.getRefundTime(), new Date()));
            }

        });
        if (CollectionUtils.isEmpty(collectorIds)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }
        List<ManUser> sysUserList = manUserService.getSysUserByIds(collectorIds);
        if (CollectionUtils.isEmpty(sysUserList)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }
        Map<String, ManUser> sysUserMap = sysUserList.stream()
                .collect(Collectors.toMap(elem -> elem.getId().toString(), elem -> elem));
        result.stream().filter(elem -> elem.getOutsourceId() != null).forEach(elem -> {
            if (sysUserMap != null && sysUserMap.get(elem.getOutsourceId().toString()) != null) {
                elem.setQualityCollectorName(
                        sysUserMap.get(elem.getOutsourceId().toString()).getRealname());
            }
        });
        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }

    public CollectionOrderResponse getQualityRecordLast(AssignableCollectionOrderReq request) {

        if (StringUtils.isEmpty(request.getUuid())) {
            log.error("getQualityRecordLast is null");
            return null;
        }
        CollectionOrderResponse response = new CollectionOrderResponse();
        response.setUuid(request.getUuid());
        getConfig(response);

        return response;
    }
}
