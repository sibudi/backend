package com.yqg.manage.service.order;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yqg.base.multiDataSource.annotation.ReadDataSource;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.order.OrdBillStatusEnum;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.order.OrderTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.system.SysThirdLogsEnum;
import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.manage.dal.collection.CollectionOrderDetailDao;
import com.yqg.manage.dal.collection.ManCollectionRemarkDao;
import com.yqg.manage.dal.order.ManOrderOrderDao;
import com.yqg.manage.dal.order.ManOrderRemarkDao;
import com.yqg.manage.dal.order.ManTeleReviewRecordDao;
import com.yqg.manage.dal.user.ManUserDao;
import com.yqg.manage.entity.check.ManOrderCheckRemark;
import com.yqg.manage.entity.check.ManTeleReviewRecord;
import com.yqg.manage.entity.collection.CollectionOrderDetail;
import com.yqg.manage.entity.collection.ManCollectionRemark;
import com.yqg.manage.entity.system.ManOrderRemark;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.enums.*;
import com.yqg.manage.service.check.ManOrderCheckRemarkService;
import com.yqg.manage.service.collection.CollectionRemarkService;
import com.yqg.manage.service.collection.request.ManCollectionRemarkRequest;
import com.yqg.manage.service.order.request.*;
import com.yqg.manage.service.order.response.*;
import com.yqg.manage.service.system.ManAuthManagerService;
import com.yqg.manage.service.user.ManUserService;
import com.yqg.manage.service.user.UserUserService;
import com.yqg.manage.util.DateUtils;
import com.yqg.service.util.LoginSysUserInfoHolder;
import com.yqg.manage.util.PageDataUtils;
import com.yqg.mongo.dao.OrderUserDataDal;
import com.yqg.mongo.entity.OrderUserDataMongo;
import com.yqg.order.dao.OrdBillDao;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdRepayAmoutRecordDao;
import com.yqg.order.entity.OrdBill;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdRepayAmoutRecord;
import com.yqg.service.loan.response.LoanResponse;
import com.yqg.service.order.OrdService;
import com.yqg.service.order.request.GetOrdRepayAmoutRequest;
import com.yqg.service.pay.RepayService;
import com.yqg.service.system.request.DictionaryRequest;
import com.yqg.service.system.response.SysDicItemModel;
import com.yqg.service.system.service.SysDicService;
import com.yqg.service.system.service.SysThirdLogsService;
import com.yqg.service.user.response.UsrAttachmentResponse;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.entity.SysThirdLogs;
import com.yqg.user.dao.UsrCertificationInfoDao;
import com.yqg.user.dao.UsrStudentDetailDao;
import com.yqg.user.dao.UsrWorkDetailDao;
import com.yqg.user.entity.UsrCertificationInfo;
import com.yqg.user.entity.UsrStudentDetail;
import com.yqg.user.entity.UsrUser;
import com.yqg.user.entity.UsrWorkDetail;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author alan
 */
@Component
@Slf4j
public class ManOrderOrderService {


    private Logger logger = LoggerFactory.getLogger(ManOrderOrderService.class);

    @Autowired
    private ManOrderOrderDao manOrderOrderDao;

    @Autowired
    private UserUserService userUserService;

    @Autowired
    private ManUserService manUserService;

    @Autowired
    private ManOrderCheckRemarkService manOrderCheckRemarkService;

    @Autowired
    private ManOrderRemarkDao manOrderRemarkDao;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private CollectionOrderDetailDao collectionOrderDetailDao;

    @Autowired
    private ManUserDao manUserDao;

    @Autowired
    private SysDicService sysDicService;

    @Autowired
    private ManCollectionRemarkDao manCollectionRemarkDao;

    @Autowired
    private UsrStudentDetailDao usrStudentDetailDao;

    @Autowired
    private UsrWorkDetailDao usrWorkDetailDao;

    @Autowired
    private ManTeleReviewRecordDao manTeleReviewRecordDao;

    @Autowired
    private OrdDao ordDao;

    @Autowired
    private OrderUserDataDal orderUserDataDal;

    @Autowired
    private RepayService repayService;

    @Autowired
    private OrdRepayAmoutRecordDao ordRepayAmoutRecordDao;

    @Autowired
    private UsrCertificationInfoDao usrCertificationInfoDao;

    @Value("${pay.inactiveOrderUrl}")
    private String inactiveOrderUrl;
    @Value("${pay.token}")
    private String PAY_TOKEN;
    // 查询放款 url
    @Value("${pay.cheakLoanUrl}")
    private String QUERY_LOAN_URL;

    @Autowired
    private SysThirdLogsService sysThirdLogsService;

    @Autowired
    private CollectionRemarkService collectionRemarkService;

    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    private CommonService commonService;

    @Autowired
    private ManAuthManagerService manAuthManagerService;

    @Autowired
    private UsrService usrService;

    @Autowired
    private OrdService ordService;

    /**
     * 全部订单列表查询接口
     */
    @ReadDataSource
    public PageData getOrderList(ManAllOrdListSearchResquest searchResquest)
            throws ServiceExceptionSpec, ParseException {

        PageData result = new PageData();
        //临时加上一个判断
        if (StringUtils.isEmpty(searchResquest.getUuid()) && StringUtils.isEmpty(searchResquest.getRealName())
                && StringUtils.isEmpty(searchResquest.getMobile())) {
            return result;
        }


        //如果手机号不为空，使用掩码查询
        if (!StringUtils.isEmpty(searchResquest.getMobile())) {
            searchResquest.setMobile(DESUtils.encrypt(searchResquest.getMobile()));
        }
        //封装是否展期
        getManOrderOrderRequest(searchResquest);

        List<ManAllOrdListResponse> orderResult = this.manOrderOrderDao.allOrdList(searchResquest);

        if (CollectionUtils.isEmpty(orderResult)) {
            result.setData(null);
            result.setRecordsTotal(0);
            result.setPageNo(searchResquest.getPageNo());
            result.setPageSize(searchResquest.getPageSize());
            return result;
        }

        for (ManAllOrdListResponse orderObj : orderResult) {
            //封装是否复借
            if (orderObj.getBorrowingCount() != null && orderObj.getBorrowingCount() < 2) {
                orderObj.setIsRepeatBorrowing(0);
            } else if (orderObj.getBorrowingCount() != null && orderObj.getBorrowingCount() > 1) {
                orderObj.setIsRepeatBorrowing(1);
            }
            if (orderObj.getOrderType() != null) {
                if (orderObj.getOrderType().equals(1)) {
                    orderObj.setExtendType(1);
                } else if (orderObj.getOrderType().equals(2)) {
                    orderObj.setCalType(1);
                }
            }
            //如果是分期产品 分装数据
            commonService.setTerms(orderObj);
        }

        Integer orderCount = this.manOrderOrderDao.orderListCount(searchResquest);

        result.setData(orderResult);
        result.setRecordsTotal(orderCount);
        result.setPageNo(searchResquest.getPageNo());
        result.setPageSize(searchResquest.getPageSize());

        return result;
    }

    /**
     * 回显订单信息
     */
    public ManOrderDetailResponse orderInfoByUuid(ManAllOrdListSearchResquest request)
            throws ServiceExceptionSpec {
        String orderNo = request.getUuid();
        if (StringUtils.isEmpty(orderNo)) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }
        OrdOrder order = this.getOrderInfoByUuid(orderNo);
        if (order == null) {
            return null;
        }

        ManOrderDetailResponse response = new ManOrderDetailResponse();
        BeanUtils.copyProperties(order, response);
        //订单是第几次申请的
        Integer applyCount = this.manOrderOrderDao
                .orderApplyCountByUser(order.getUserUuid(), order.getId());
        response.setOrderStep(applyCount);

        //判断是否复借
        if (order.getBorrowingCount() != null && order.getBorrowingCount() < 2) {
            response.setIsRepeatBorrowing(0);
        } else if (order.getBorrowingCount() != null && order.getBorrowingCount() > 1) {
            response.setIsRepeatBorrowing(1);
        }

        //封装进件标识
        if (order.getThirdType() != null && order.getThirdType() == 1) {
            response.setIntoOrdFlag(1);
        }
        //封装用户角色
        UsrUser user = this.userUserService.userInfoByUuid(order.getUserUuid());
        if (user != null) {
            response.setUserRole(user.getUserRole());
        }

        //封装催收标识
        CollectionOrderDetail coll = new CollectionOrderDetail();
        coll.setOrderUUID(order.getUuid());
        coll.setDisabled(0);
        List<CollectionOrderDetail> colls = collectionOrderDetailDao.scan(coll);
        if (!CollectionUtils.isEmpty(colls)) {
            response.setCollectionFlag(colls.get(0).getOrderTag());
        }

        //封装初审复审人员
        ManUser manUser = new ManUser();
        manUser.setDisabled(0);
        if (order.getFirstChecker() != 0) {
            manUser.setId(order.getFirstChecker());
            List<ManUser> firstList = manUserDao.scan(manUser);
            response.setFirstChecker(CollectionUtils.isEmpty(firstList) ? "" : firstList.get(0).getRealname());
        } else {
            response.setFirstChecker("");
        }
        if (order.getSecondChecker() != 0) {
            manUser.setId(order.getSecondChecker());
            List<ManUser> secondList = manUserDao.scan(manUser);
            response.setSecondChecker(CollectionUtils.isEmpty(secondList) ? "" : secondList.get(0).getRealname());
        } else {
            response.setSecondChecker("");
        }
        //分装是否展期和结清
        String orderType = order.getOrderType();
        if (StringUtils.isNotBlank(orderType)) {
            if ("1".equals(orderType)) {
                response.setExtendType(1);
                response.setCalType(0);
            } else if ("2".equals(orderType)) {
                response.setExtendType(0);
                response.setCalType(1);
            } else {
                response.setExtendType(0);
                response.setCalType(0);
            }
        }

        //封装逾期金额数据
        if (order.getStatus() > OrdStateEnum.LOANING_DEALING.getCode()) {
            getOverdueMoney(response, order);
        }
        response.setAmountApply(com.yqg.common.utils.StringUtils.formatMoney(order.getAmountApply().doubleValue())
                .replaceAll(",",".").toString());


        //封装订单逾期级别
        if (order.getStatus() == OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode() ||
                order.getStatus() == OrdStateEnum.RESOLVING_OVERDUE.getCode()) {
            String nowDate = com.yqg.common.utils.DateUtils.formDate
                    (new Date(), "yyyy-MM-dd");
            try {
                int dayNum = (int) com.yqg.common.utils.DateUtils.daysBetween(com.yqg.common.utils.DateUtils.formDate
                        (order.getRefundTime(), "yyyy-MM-dd"), nowDate);
                if (dayNum == 0) {
                    response.setCollectionLevel("D0");
                } else if (dayNum > 0 && dayNum <= 7) {
                    response.setCollectionLevel("D1-D7");
                } else if (dayNum > 7 && dayNum <= 30) {
                    response.setCollectionLevel("D8-D30");
                } else if (dayNum > 30) {
                    response.setCollectionLevel("D30+");
                } else if (dayNum == -1) {
                    response.setCollectionLevel("D-1");
                } else if (dayNum == -2) {
                    response.setCollectionLevel("D-2");
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //查询是否实名认证通过
        List<UsrCertificationInfo> usrCertificationInfos = usrCertificationInfoDao.listCertificationInfo(user.getUuid());
        if (!CollectionUtils.isEmpty(usrCertificationInfos)) {
            if (usrCertificationInfos.get(0).getCertificationResult() == 1
                    || (usrCertificationInfos.size() > 1 && usrCertificationInfos.get(1).getCertificationResult() == 1)) {
                response.setRealNameAuthFlag(true);
            }
        }

        //只具有客服权限的不展示
        if (manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
            response.setIsKefu(false);
            //封装借款用途字段
            response.setBorrowUse(getBorrowUse(user.getUserRole(), user.getUuid()));
            //查询whatsApp
            if (manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnumNoCheck())) {
                UsrCertificationInfo usrCertificationInfo = new UsrCertificationInfo();
                usrCertificationInfo.setUserUuid(user.getUuid());
                usrCertificationInfo.setDisabled(0);
                usrCertificationInfo.setCertificationType(CertificationEnum.WHATS_APP.getType());
                usrCertificationInfos = usrCertificationInfoDao.scan(usrCertificationInfo);
                if (!CollectionUtils.isEmpty(usrCertificationInfos)) {
                    response.setWhatsApp(usrCertificationInfos.get(0).getCertificationData());
                }
            }
        } else {
            response.setIsKefu(true);
        }

        if (!manAuthManagerService.hasAuthorityByRoleName(ManCheckOperatorEnum.listCheckOperatorEnum())) {
            response.setBorrowingTerm(0);
        }

        return response;
    }

    /**
     * 封装订单逾期账户管理费、逾期账户滞纳金、应还款总额
     * @param response
     * @param order
     */
    private void getOverdueMoney (ManOrderDetailResponse response, OrdOrder order) {
//        if (StringUtils.isNotBlank(order.getProductUuid())) {
        int dayNum = 0;
        try {
            if (order.getRefundTime() == null) {
                return ;
            }
            //判断不同订单状态，其时间不同
            String nowDate = com.yqg.common.utils.DateUtils.formDate
                    (new Date(), "yyyy-MM-dd");
            if (order.getStatus() == OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode()) {
                nowDate = com.yqg.common.utils.DateUtils.formDate
                        (order.getRefundTime(), "yyyy-MM-dd");
            } else if (order.getStatus() == OrdStateEnum.RESOLVED_OVERDUE.getCode()) {
                nowDate = com.yqg.common.utils.DateUtils.formDate
                        (order.getActualRefundTime(),"yyyy-MM-dd");
            }
            dayNum = (int) com.yqg.common.utils.DateUtils.daysBetween(com.yqg.common.utils.DateUtils.formDate
                    (order.getRefundTime(), "yyyy-MM-dd"), nowDate);
            if (dayNum <= 0) {
                response.setOverdueFee("0");
                response.setOverdueMoney("0");
                BigDecimal tempMoney = order.getAmountApply().add(order.getInterest());
                response.setShouldPayAmount(com.yqg.common.utils.StringUtils.
                        formatMoney(tempMoney
                                .doubleValue()).replaceAll(",",".").toString());
                BigDecimal limit = order.getAmountApply().multiply(BigDecimal.valueOf(1.2)).setScale(2);
                if (tempMoney.compareTo(limit) > 0 ){
                    tempMoney = limit;
                }
                //实际应还款金额
                response.setActualPayAmount(com.yqg.common.utils.StringUtils.formatMoney(tempMoney.doubleValue()).replaceAll(",",".").toString());
                return ;
            }

            //逾期账户管理费
            response.setOverdueFee(this.repayService.calculateOverDueFee(order));

            BigDecimal overdueMoney = new BigDecimal(this.repayService.calculatePenaltyFee(order));
            // （逾期）应还款金额：借款金额+利息 + 逾期费
            BigDecimal shouldPayAmount = BigDecimal.ZERO;

            if (!order.getOrderType().equals("0")){
                shouldPayAmount  = new BigDecimal(this.repayService.calculateRepayAmount(order,"2"));
                response.setExtendServiceFee(com.yqg.common.utils.StringUtils.formatMoney(Double.valueOf(this.repayService.calculateDelayFee(order))).replaceAll(",",".").toString());
            }else {
                if (order.getStatus() == OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode()  || order.getStatus() == OrdStateEnum.RESOLVED_OVERDUE.getCode()){

                    OrdRepayAmoutRecord ordRepayAmoutRecord = new OrdRepayAmoutRecord();
                    ordRepayAmoutRecord.setDisabled(0);
                    ordRepayAmoutRecord.setOrderNo(order.getUuid());
                    List<OrdRepayAmoutRecord> recordList = this.ordRepayAmoutRecordDao.scan(ordRepayAmoutRecord);
                    if (!CollectionUtils.isEmpty(recordList)) {
                        shouldPayAmount  = new BigDecimal(recordList.get(0).getActualRepayAmout());
                    }
                }else {
                    shouldPayAmount = new BigDecimal(this.repayService.calculateRepayAmount(order, "1"));
                }
            }
            // 应还款金额
            response.setShouldPayAmount(com.yqg.common.utils.StringUtils.formatMoney(shouldPayAmount.doubleValue()).replaceAll(",",".").toString());

            BigDecimal limit = order.getAmountApply().multiply(BigDecimal.valueOf(1.2)).setScale(2);
            if (shouldPayAmount.compareTo(limit) > 0 ){
                shouldPayAmount = limit;
            }
            //实际应还款金额
            response.setActualPayAmount(com.yqg.common.utils.StringUtils.formatMoney(shouldPayAmount.doubleValue()).replaceAll(",",".").toString());

            //逾期账户滞纳金
            response.setOverdueMoney(com.yqg.common.utils.StringUtils.formatMoney(overdueMoney.doubleValue()).replaceAll(",",".").toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        }
    }
    /**
     * 修改订单状态
     */
    public void editOrder(OrdOrder order) throws ServiceExceptionSpec {
        this.manOrderOrderDao.update(order);
    }

    /**
     * */
    public OrdOrder getOrderInfoByUuid(String orderNo) {
        OrdOrder search = new OrdOrder();
        search.setUuid(orderNo);
        search.setDisabled(0);
        List<OrdOrder> result = this.manOrderOrderDao.scan(search);
        if (!CollectionUtils.isEmpty(result)) {
            return result.get(0);
        }
        return null;
    }

    /**
     * 通过用户uuid查询历史订单
     */
    @ReadDataSource
    public List<OrdOrder> orderHistoryListByUserUuid(ManOrderListSearchResquest resquest)
            throws ServiceExceptionSpec {
        String userUuid = resquest.getUuid();
        if (StringUtils.isEmpty(userUuid)) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
            return new ArrayList<>();
        }
        OrdOrder search = new OrdOrder();
        search.setUserUuid(userUuid);
        search.setDisabled(0);
        search.set_orderBy(" id desc ");
        List<OrdOrder> result = this.manOrderOrderDao.scan(search);
        //查询所有订单的审核备注
        for (OrdOrder item : result) {
            String orderNo = item.getUuid();
            List<ManOrderCheckRemark> remarkList = this.manOrderCheckRemarkService
                    .orderCheckRemarkListByOrderNo(orderNo);
            String orderRemark = "";
            for (ManOrderCheckRemark cell : remarkList) {
                orderRemark += cell.getRemark() + " || ";
            }
            item.setRemark(orderRemark);
        }

        return result;
    }

    /**
     * 获取用户uuid集合String
     */
    public String getUserUuidString(List<OrdOrder> result) throws ServiceExceptionSpec {
        StringBuilder userUuids = new StringBuilder();
        for (OrdOrder orderObj : result) {
            if (StringUtils.isNotBlank(orderObj.getUserUuid())) {
                userUuids.append("'" + orderObj.getUserUuid() + "',");
            }
        }
        String resultStr = StringUtils.removeEnd(userUuids.toString(), ",");
        return resultStr;
    }


    public PageData<List<D0OrderResponse>> getD0OrderList(D0OrderRequest param) {
        PageHelper.startPage(param.getPageNo(), param.getPageSize());
        //封装是否展期
        getManOrderOrderRequest(param);
        List<D0OrderResponse> orderList = manOrderOrderDao.getD0OrderList(param);
        PageInfo pageInfo = new PageInfo(orderList);
        if (CollectionUtils.isEmpty(orderList)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }

        //设置订单是否复借
        orderList.stream().forEach(elem -> elem.setIsRepeatBorrowing(
                elem.getBorrowingCount() != null && elem.getBorrowingCount() > 1 ? 1 : 0));

        List<Integer> outsourceIdList = orderList.stream()
                .filter(elem -> elem.getOutsourceId() != null).map(elem -> elem.getOutsourceId())
                .collect(
                        Collectors.toList());
        if (CollectionUtils.isEmpty(outsourceIdList)) {
            //没有分配催收人员
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }
        //设置是否展期
        orderList.stream().forEach(elem -> {
            if (elem.getOrderType() != null) {
                if (elem.getOrderType().equals(1)) {
                    elem.setExtendType(1);
                } else if (elem.getOrderType().equals(2)) {
                    elem.setCalType(1);
                }
            }
        });

        //查询催收人员信息
        List<ManUser> sysUsers = manUserService.getSysUserByIds(outsourceIdList);
        Map<String, ManUser> sysUserMap = sysUsers.stream()
                .collect(Collectors.toMap(elem -> elem.getId().toString(),
                        elem -> elem));
        //设置订单催收人员
        if (sysUserMap != null) {
            orderList.stream().filter(elem -> elem.getOutsourceId() != null).forEach(elem -> {
                ManUser sysUser = sysUserMap.get(elem.getOutsourceId().toString());
                elem.setOutsourceUserName(sysUser != null ? sysUser.getRealname() : null);
            });
        }
        //封装订单催收联系情况
        for (D0OrderResponse elem : orderList) {

            Integer[] collectionContactResult = new Integer[] {0,0,0,0,0,0,0,0};
            ManCollectionRemark manCollectionRemark = new ManCollectionRemark();
            manCollectionRemark.setDisabled(0);
            manCollectionRemark.setOrderNo(elem.getUuid());
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
            elem.setCollectionContactResult(collectionContactResult);

            //如果是分期产品 分装数据
            commonService.setTerms(elem);
        }

        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }



    public PageData<List<OverdueOrderResponse>> getOverdueOrderList(OverdueOrderRequest param) {
        PageHelper.startPage(param.getPageNo(), param.getPageSize());
        //封装是否展期
        getManOrderOrderRequest(param);
        List<OverdueOrderResponse> orderList = manOrderOrderDao.getOverdueOrderList(param);
        PageInfo pageInfo = new PageInfo(orderList);
        if (CollectionUtils.isEmpty(orderList)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }


        //设置订单是否复借
        orderList.stream().forEach(elem -> elem.setIsRepeatBorrowing(
                elem.getBorrowingCount() != null && elem.getBorrowingCount() > 1 ? 1 : 0));
        //设置逾期天数
        orderList.stream().forEach(elem -> elem
                .setOverdueDays(DateUtils.getDiffDaysIgnoreHours(elem.getRefundTime(), new Date())));
        //设置是否展期
        orderList.stream().forEach(elem -> {
            if (elem.getOrderType() != null) {
                if (elem.getOrderType().equals(1)) {
                    elem.setExtendType(1);
                } else if (elem.getOrderType().equals(2)) {
                    elem.setCalType(1);
                }
            }
        });


        List<Integer> outsourceIdList = orderList.stream()
                .filter(elem -> elem.getOutsourceId() != null).map(elem -> elem.getOutsourceId())
                .collect(
                        Collectors.toList());
        if (CollectionUtils.isEmpty(outsourceIdList)) {
            //查询的订单没有催收人员
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }

        //查询催收人员信息
        List<ManUser> sysUsers = manUserService.getSysUserByIds(outsourceIdList);
        Map<String, ManUser> sysUserMap = sysUsers.stream()
                .collect(Collectors.toMap(elem -> elem.getId().toString(),
                        elem -> elem));
        //设置订单催收人员
        if (sysUserMap != null) {
            orderList.stream().filter(elem -> elem.getOutsourceId() != null).forEach(elem -> {
                ManUser sysUser = sysUserMap.get(elem.getOutsourceId().toString());
                elem.setOutsourceUserName(sysUser != null ? sysUser.getRealname() : null);
            });
        }
        //封装订单催收联系情况
        for (OverdueOrderResponse elem : orderList) {

            Integer[] collectionContactResult = new Integer[] {0,0,0,0,0,0,0,0};
            ManCollectionRemark manCollectionRemark = new ManCollectionRemark();
            manCollectionRemark.setDisabled(0);
            manCollectionRemark.setOrderNo(elem.getUuid());
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
            elem.setCollectionContactResult(collectionContactResult);
            //如果是分期产品 分装数据
            commonService.setTerms(elem);
        }
        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }

    public PageData<List<PaidOrderResponse>> getPaidOrderList(PaidOrderRequest param) {
        PageHelper.startPage(param.getPageNo(), param.getPageSize());
        //封装是否展期
        getManOrderOrderRequest(param);
        List<PaidOrderResponse> orderList = manOrderOrderDao.getPaidOrderList(param);
        PageInfo pageInfo = new PageInfo(orderList);
        if (CollectionUtils.isEmpty(orderList)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }

        //设置订单是否复借
        orderList.stream().forEach(elem -> elem.setIsRepeatBorrowing(
                elem.getBorrowingCount() != null && elem.getBorrowingCount() > 1 ? 1 : 0));
        //设置是否展期
        orderList.stream().forEach(elem -> {
            if (elem.getOrderType() != null) {
                if (elem.getOrderType().equals(1)) {
                    elem.setExtendType(1);
                } else if (elem.getOrderType().equals(2)) {
                    elem.setCalType(1);
                }
            }
            //如果是分期产品 分装数据
            commonService.setTerms(elem);
        });
        //设置逾期天数
        orderList.stream().forEach(elem -> elem
                .setOverdueDays(DateUtils
                        .getDiffDaysIgnoreHours(elem.getRefundTime(), elem.getActualRefundTime())));

        List<Integer> outsourceIdList = orderList.stream()
                .filter(elem -> elem.getOutsourceId() != null).map(elem -> elem.getOutsourceId())
                .collect(
                        Collectors.toList());
        if (CollectionUtils.isEmpty(outsourceIdList)) {
            //查询的订单没有催收人员
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }

        //查询催收人员信息
        List<ManUser> sysUsers = manUserService.getSysUserByIds(outsourceIdList);
        Map<String, ManUser> sysUserMap = sysUsers.stream()
                .collect(Collectors.toMap(elem -> elem.getId().toString(),
                        elem -> elem));
        //设置订单催收人员
        if (sysUserMap != null) {
            orderList.stream().filter(elem -> elem.getOutsourceId() != null).forEach(elem -> {
                ManUser sysUser = sysUserMap.get(elem.getOutsourceId().toString());
                elem.setOutsourceUserName(sysUser != null ? sysUser.getRealname() : null);
            });
        }
        return PageDataUtils.mapPageInfoToPageData(pageInfo);

    }

    /***
     * 放款失败订单
     * @param param
     * @return
     */
    public PageData<List<IssueFailedOrderResponse>> getIssueFailedOrderList(
            OrderSearchRequest param) {
        PageHelper.startPage(param.getPageNo(), param.getPageSize());
        List<IssueFailedOrderResponse> orderList = manOrderOrderDao.getIssueFailedOrderList();
        PageInfo pageInfo = new PageInfo(orderList);
        if (CollectionUtils.isEmpty(orderList)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }
        //设置放款银行卡展示信息
        orderList.stream().filter(elem -> StringUtils.isNoneEmpty(elem.getBankNumberNo()))
                .forEach(elem -> {
                    elem.setShortBankNumber(elem.getBankNumberNo()
                            .substring(elem.getBankNumberNo().length() - 4,
                                    elem.getBankNumberNo().length()));
                    //elem.setBankCardInfoDesc(elem.getBankCardName()+"("+elem.getShortBankNumber()+")");
                });
        //查询订单放款的日志数据,mock
        List<SysThirdLogs> issueLogList = new ArrayList<>();
        //查询response最新的response数据
        if (CollectionUtils.isEmpty(issueLogList)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }
        //response日志按照orderNo分组==== 先按照orderNo分组，然后在分组的基础上找到最最大id的记录即为最新的返回记录
        Map<String, Optional<SysThirdLogs>> logMap = issueLogList.stream()
                .filter(elem -> StringUtils.isEmpty(elem.getRequest())).collect(
                        Collectors.groupingBy(SysThirdLogs::getOrderNo,
                                Collectors.maxBy(Comparator.comparing(SysThirdLogs::getId))));

        orderList.stream().forEach(elem -> {
            Optional<SysThirdLogs> log = logMap.get(elem.getUuid());
            if (log != null && log.isPresent()) {
                elem.setErrorMessage(log.get().getResponse());
            }
        });


        return PageDataUtils.mapPageInfoToPageData(pageInfo);

    }
    /**
     * 新增或更新电核备注
     * @param request
     * @return
     */
    @Transactional
    public Integer inserOrUpdateTeleReviewRemark(ManOrderRemarkRequest request) throws Exception {

        if (request.getCreateUser() == null ||
                request.getUpdateUser() == null ||
                StringUtils.isEmpty(request.getOrderNo()) ||
                StringUtils.isEmpty(request.getUserUuid()) ||
                request.getType() == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_ITEM_ERROR);
        }
        if( !this.redisClient.lockRepeat(request.getOrderNo())){
            throw new ServiceExceptionSpec(ExceptionEnum.ORDER_COMMIT_REPEAT);
        }

        if (request.getType() == ManOrderRemarkTypeEnum.COLLECTION_ORDERTAG.getType()) {

            ManCollectionRemarkRequest search = new ManCollectionRemarkRequest();
            BeanUtils.copyProperties(request, search);
            collectionRemarkService.insertCollectionRemark(search);
            return 1;
        }
        ManOrderRemark manOrderRemark = transferManOrderRemark(request);

        //新增备注
        if (request.getId() == null) {
            manOrderRemarkDao.insert(manOrderRemark);

            //催收打标签
//            if (request.getType() == ManOrderRemarkTypeEnum.COLLECTION_ORDERTAG.getType()) {
//                //如果提醒时间不为空，添加到提醒
//                if (StringUtils.isNotBlank(request.getAlertTime())) {
//                    ManAlertMessageRequest manAlertMessageRequest =
//                            new ManAlertMessageRequest();
//                    //获得手机号码和用户名称
//                    ManUserUserRequest manUserUserRequest = new ManUserUserRequest();
//                    manUserUserRequest.setUserUuid(request.getUserUuid());
//                    UsrUser mobile = userUserService.userMobileByUuid(manUserUserRequest);
//                    manAlertMessageRequest.setMobileNumber(mobile.getMobileNumberDES());
//                    manAlertMessageRequest.setRealName(mobile.getRealName());
//                    manAlertMessageRequest.setAlertTime(manOrderRemark.getAlertTime());
//                    manAlertMessageRequest.setOperateId(request.getCreateUser());
//                    manAlertMessageRequest.setUserUuid(request.getUserUuid());
//                    manAlertMessageRequest.setOrderNo(request.getOrderNo());
//                    manAlertMessageRequest.setOrderTag(request.getOrderTag());
//                    manAlertMessageRequest.setLangue(request.getLangue());
//                    manAlertMessageService.addCollectionAlertMessage(manAlertMessageRequest);
//                }
//                //将订单标签和承诺时间更新到collectionOrderDetail表
//                CollectionOrderDetail coll = new CollectionOrderDetail();
//                coll.setOrderUUID(request.getOrderNo());
//                coll.setDisabled(0);
//                List<CollectionOrderDetail> colls = collectionOrderDetailDao.scan(coll);
//                if (!CollectionUtils.isEmpty(colls)) {
//                    coll = colls.get(0);
//                    coll.setOrderTag(request.getOrderTag());
//                    coll.setPromiseRepaymentTime(manOrderRemark.getPromiseRepaymentTime());
//                    collectionOrderDetailDao.update(coll);
//                }
//                this.redisClient.unlockRepeat(request.getOrderNo());
//                return manOrderRemark.getId();
//            }
        } else {
            //修改电核备注
            manOrderRemark.setUuid(null);
            manOrderRemarkDao.update(manOrderRemark);
        }
        //如果提醒时间不为空，添加到提醒
//        if (StringUtils.isNotBlank(request.getAlertTime())) {
//
//            ManAlertMessageRequest manAlertMessageRequest =
//                    new ManAlertMessageRequest();
//            //获得手机号码
//            ManUserUserRequest manUserUserRequest = new ManUserUserRequest();
//            manUserUserRequest.setUserUuid(request.getUserUuid());
//            UsrUser mobile = userUserService.userMobileByUuid(manUserUserRequest);
//            manAlertMessageRequest.setMobileNumber(mobile.getMobileNumberDES());
//            manAlertMessageRequest.setRealName(mobile.getRealName());
//            manAlertMessageRequest.setAlertTime(manOrderRemark.getAlertTime());
//            manAlertMessageRequest.setOperateId(request.getCreateUser());
//            manAlertMessageRequest.setUserUuid(request.getUserUuid());
//            manAlertMessageRequest.setOrderNo(request.getOrderNo());
//            manAlertMessageRequest.setLangue(request.getLangue());
//            manAlertMessageService.addTeleReviewAlertMessage(manAlertMessageRequest);
//        }

        //保存公司电核问题及答案
        if (!CollectionUtils.isEmpty(request.getCompanyTeleQARequest())) {
            //先删除可能存在的问题
            manTeleReviewRecordDao.deleteCompanyTele(manOrderRemark.getId());
            for (CompanyTeleQARequest companyTeleQARequest : request.getCompanyTeleQARequest()) {
                ManTeleReviewRecord reviewRecord = new ManTeleReviewRecord();
                reviewRecord.setLangue(request.getLangue());
                reviewRecord.setDisabled(0);
                reviewRecord.setManOrderRemarkId(manOrderRemark.getId());
                reviewRecord.setQuestion(companyTeleQARequest.getQuestion());
                reviewRecord.setAnswer(String.valueOf(companyTeleQARequest.getAnswer()));
                reviewRecord.setRemark(companyTeleQARequest.getRemark());
                manTeleReviewRecordDao.insert(reviewRecord);
            }
        }
        this.redisClient.unlockRepeat(request.getOrderNo());
        return manOrderRemark.getId();

    }

    /**
     * 获得实体类
     * @param request
     * @return
     */
    private ManOrderRemark transferManOrderRemark (ManOrderRemarkRequest request) throws Exception {

        ManOrderRemark remark = new ManOrderRemark();
        remark.setUuid(UUIDGenerateUtil.uuid());
        remark.setType(request.getType());
        remark.setCreateUser(request.getCreateUser());
        remark.setOrderNo(request.getOrderNo());
        remark.setUpdateUser(request.getUpdateUser());
        remark.setRemark(request.getRemark());
        remark.setOrderTag(request.getOrderTag());
        remark.setBurningTime(request.getBurningTime());
        remark.setId(request.getId());
        remark.setTeleReviewOperationType(request.getTeleReviewOperationType());
        remark.setTeleReviewResultType(request.getTeleReviewResult());
        remark.setMobile(request.getMobile());
        remark.setRealName(request.getRealName());
        //保存手动点击拒绝的原因
        if (StringUtils.isNotBlank(request.getDescription())) {
            remark.setRemark(remark.getRemark() + ",[" + request.getDescription() + "]");
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            if (StringUtils.isNotBlank(request.getStartTime())) {
                remark.setStartTime(sf.parse(request.getStartTime()));
            }
            if (StringUtils.isNotBlank(request.getEndTime())) {
                remark.setEndTime(sf.parse(request.getEndTime()));
            }
            if (StringUtils.isNotBlank(request.getAlertTime())) {
                remark.setAlertTime(simpleDateFormat.parse(request.getAlertTime()));
            }
            if (StringUtils.isNotBlank(request.getPromiseRepaymentTime())) {
                remark.setPromiseRepaymentTime(simpleDateFormat.parse(request.getPromiseRepaymentTime()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //封装电话审核的电核结果(包括印尼版）
        DictionaryRequest dictionaryRequest = new DictionaryRequest();
        if (request.getType() == 1 && request.getLangue() == 1) {
            dictionaryRequest.setDicCode("PERSON_TELEREVIEW_ENUM");
        } else if (request.getType() == 3 && request.getLangue() == 1) {
            dictionaryRequest.setDicCode("COMPANY_TELEREVIEW_ENUM");
        } else if (request.getType() == 1 && request.getLangue() == 2) {
            dictionaryRequest.setDicCode("PERSON_TELEREVIEW_ENUM_Inn");
        } else if (request.getType() == 3 && request.getLangue() == 2) {
            dictionaryRequest.setDicCode("COMPANY_TELEREVIEW_ENUM_Inn");
        }else {
            return remark;
        }

        if (request.getOperationType() == null
                && request.getTeleReviewOperationType() == null) {
            return remark;
        }
        List<SysDicItemModel> sysDicItemModelList = this.sysDicService.dicItemListByDicCode(dictionaryRequest);
        if (CollectionUtils.isEmpty(sysDicItemModelList)) {
            return remark;
        }
        //取得字典枚举数据
        for (SysDicItemModel sysDicItemModel : sysDicItemModelList) {
            String[] values = sysDicItemModel.getDicItemValue().split("#");
            if (values == null || values.length != 2) {
                continue;
            }
            Integer operationType = Integer.valueOf(values[0]);
            Integer teleReviewResult = Integer.valueOf(values[1]);
            //兼容新版公司审核
            if (request.getTeleReviewOperationType() != null) {
                //只分接通和未接通
                Integer teleReviewType = request.getTeleReviewOperationType() % 2 == 0 ? 2 : 1;
                if (request.getTeleReviewOperationType().equals(operationType)
                        && request.getTeleReviewResult().equals(teleReviewResult)) {
                    String tempStr = (request.getLangue() == 1 ? EnumUtils.valueOfTeleReviewOperationTypeEnum(teleReviewType)
                            .getMessage() : EnumUtils.valueOfTeleReviewOperationTypeEnum(teleReviewType)
                            .getMessageInn()) + "，" + sysDicItemModel.getDicItemName();
                    remark.setTeleReviewResult(tempStr);
                }
            } else {
                if (request.getOperationType().equals(operationType)
                        && request.getTeleReviewResult().equals(teleReviewResult)) {
                    remark.setTeleReviewResult((request.getLangue() == 1 ? EnumUtils.valueOfOperationTypeEnum(operationType)
                            .getMessage() : EnumUtils.valueOfOperationTypeEnum(operationType)
                            .getMessageInn()) + "，" + sysDicItemModel.getDicItemName());
                }
            }
        }
        remark.setOperationType(request.getOperationType());
//        if (request.getOperationType() != null &&
//                request.getOperationType().equals(3)
//                && request.getType().equals(1)) {
//            if (request.getTeleReviewResult() != null) {
//                if (request.getTeleReviewResult().equals(1)) {
//                    remark.setDescription("本人审核拒绝，用户主动请求拒绝");
//                } else if (request.getTeleReviewResult().equals(2)) {
//                    remark.setDescription("本人审核拒绝，用户不配合审核");
//                }else if (request.getTeleReviewResult().equals(3)) {
//                    remark.setDescription("本人审核拒绝，空号/不存在/无效/错误");
//                }else if (request.getTeleReviewResult().equals(4)) {
//                    remark.setDescription("本人审核拒绝，停机/已欠费");
//                }else if (request.getTeleReviewResult().equals(5)) {
//                    remark.setDescription("本人审核拒绝，用户资质不符");
//                }else if (request.getTeleReviewResult().equals(6)) {
//                    remark.setDescription("本人审核拒绝，非本人申请");
//                }else if (request.getTeleReviewResult().equals(7)) {
//                    remark.setDescription("本人审核拒绝，资料不真实/有欺诈嫌疑");
//                }else if (request.getTeleReviewResult().equals(8)) {
//                    remark.setDescription("本人审核拒绝，已经3次未接通");
//                } else if (request.getTeleReviewResult().equals(0)) {
//                    remark.setDescription("本人审核拒绝，但未选择结果，只填写了备注");
//                }
//            }
//        }
        remark.setWorkYear(request.getWorkYear());
        return remark;
    }

    /**
     * 通过订单的uuid查询所有订单详情*/
    public List<OrdOrder> getAllOrderInfobyUuid(String uuid) throws Exception {
        OrdOrder search = new OrdOrder();
        search.setUserUuid(uuid);
        search.setDisabled(0);
        List<OrdOrder> result = this.manOrderOrderDao.scan(search);
        if(result.size() <= 0){
            return null;
        }
        return result;
    }

    /**
     * 查询借款用途
     * @param type
     * @param userUuid
     * @return
     */
    private String getBorrowUse(Integer type , String userUuid) {
        //学生
        if (type == 1) {
            UsrStudentDetail usrStudentDetail = new UsrStudentDetail();
            usrStudentDetail.setUserUuid(userUuid);
            usrStudentDetail.setDisabled(0);
            List<UsrStudentDetail> rList = usrStudentDetailDao.scan(usrStudentDetail);
            return CollectionUtils.isEmpty(rList) ? "" : rList.get(0).getBorrowUse();
        } else if (type == 2) {
            UsrWorkDetail usrWorkDetail = new UsrWorkDetail();
            usrWorkDetail.setUserUuid(userUuid);
            usrWorkDetail.setDisabled(0);
            List<UsrWorkDetail> rList = usrWorkDetailDao.scan(usrWorkDetail);
            return CollectionUtils.isEmpty(rList) ? "" : rList.get(0).getBorrowUse();
        }
        return "";
    }


    /**
     * 通过订单号查询历史订单
     */
    @ReadDataSource
    public List<OrdOrder> orderListByOrderNo(String orderNo)
            throws ServiceExceptionSpec {
        List<OrdOrder> result = this.manOrderOrderDao.getOrderNoByUserUuid(orderNo);
        return result;
    }


    // 展期订单数据处理
    public void copyDataToDelayOrder(RepairDelayOrderRequest delayOrderRequest){

        OrdOrder order = new OrdOrder();
        order.setUuid(delayOrderRequest.getOrderNo());
        List<OrdOrder> ordOrders =  this.ordDao.scan(order);

        // 身份信息
        OrderUserDataMongo mongo = new OrderUserDataMongo();
        mongo.setOrderNo(ordOrders.get(0).getUuid());
        mongo.setUserUuid(ordOrders.get(0).getUserUuid());
        List<OrderUserDataMongo> mongoList = this.orderUserDataDal.find(mongo);
        if (!CollectionUtils.isEmpty(mongoList)){
            for (OrderUserDataMongo entity:mongoList){
                OrderUserDataMongo newMongo = new OrderUserDataMongo();
                newMongo.setUserUuid(ordOrders.get(0).getUserUuid());
                newMongo.setOrderNo(delayOrderRequest.getDelayOrderNo());
                newMongo.setInfoType(entity.getInfoType());
                newMongo.setData(entity.getData());
                newMongo.setStatus(entity.getStatus());
                this.orderUserDataDal.insert(newMongo);
            }
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

    //  是淼科那边的订单号失效  以便重新打款
    public void makeMkOrderDisable(MakeMKOrderRequest makeMKOrderRequest) throws Exception{

        OrdOrder scan = new OrdOrder();
        scan.setDisabled(0);
        scan.setUuid(makeMKOrderRequest.getOrderNo());
        List<OrdOrder> orderList = this.ordDao.scan(scan);
        if(!CollectionUtils.isEmpty(orderList)){
           OrdOrder order = orderList.get(0);
           if (order.getStatus() == OrdStateEnum.LOAN_FAILD.getCode()){
            //  订单状态正确 去淼科那边修改订单
               changeOrderWithMK(order);
           }else {

               throw new ServiceException(ExceptionEnum.ORDER_STATES_ERROR);
           }

        }else {
            throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
        }

    }

    public void changeOrderWithMK(OrdOrder order){
        try {
            Request request = new Request.Builder()
                    .put(new FormBody.Builder().build())
                    .url(inactiveOrderUrl +order.getUuid())
                    .header("X-AUTH-TOKEN", PAY_TOKEN)
                    .build();

            // 请求数据落库，SysThirdLogs
            this.sysThirdLogsService.addSysThirdLogs(order.getUuid(),order.getUserUuid(), SysThirdLogsEnum.INACTVE_ORDER.getCode(),0, order.getUuid(),null);

            Response response = httpClient.newCall(request).execute();
            if(response.isSuccessful())
            {
                String  responseStr = response.body().string();

                // 订单无效化响应
                log.info("订单无效化 请求后返回:{}", JsonUtils.serialize(responseStr));

                //清除redis里面的放款记录(为了相同订单号能够继续打款 删除之前在redis里面的order)
                this.redisClient.del(RedisContants.ORDER_LOAN_LOCK_NEW + order.getUuid());

                // 响应数据落库，sysThirdLogs
                sysThirdLogsService.addSysThirdLogs(order.getUuid(),order.getUserUuid(), SysThirdLogsEnum.INACTVE_ORDER.getCode(),0,null,responseStr);
            }
        }catch (Exception e){
            log.info("订单无效化失败,订单号:"+order.getUuid());
            e.printStackTrace();
        }
    }

    /**
     *   查询放款
     * */
    public LoanResponse queryLoanResult(String orderNo, String userUuid) throws Exception{

        LoanResponse loanResponse = new LoanResponse();

        Request request = new Request.Builder()
                .url(QUERY_LOAN_URL+orderNo)
                .header("X-AUTH-TOKEN", PAY_TOKEN)
                .build();

        // 请求数据落库，SysThirdLogs
        this.sysThirdLogsService.addSysThirdLogs(orderNo,userUuid, SysThirdLogsEnum.CHEAK_LOAN.getCode(),0, orderNo,null);

        Response response = httpClient.newCall(request).execute();
        if(response.isSuccessful())
        {
            String  responseStr = response.body().string();
            // 查询放款响应
            log.info("查询放款 请求后返回:{}", JsonUtils.serialize(responseStr));
            loanResponse = JsonUtils.deserialize(responseStr,LoanResponse.class);
            // 响应数据落库，sysThirdLogs
            sysThirdLogsService.addSysThirdLogs(orderNo,userUuid, SysThirdLogsEnum.CHEAK_LOAN.getCode(),0,null,responseStr);
        }

        return loanResponse;
    }


    public void repairFaildOrderWithNoBalance() {

        List<OrdOrder> loanFalidList = this.ordDao.getUserOrderLoanFaildOrder();
        if (!CollectionUtils.isEmpty(loanFalidList)) {
            log.info("CIMB打款异常的订单总个数为：" + loanFalidList.size());
            for (OrdOrder order : loanFalidList) {
                log.info("本次处理的打款订单号" + order.getUuid());

                // 去查询结果
                UsrUser user = this.usrService.getUserByUuid(order.getUserUuid());
                try {
                    LoanResponse response = queryLoanResult(order.getUuid(), user.getUuid());

                    if (response != null) {
                        if (response.getCode().equals("-1")){

                            String errorMsg = "The disbursement, externalId: " + order.getUuid() + ", is not exist.";
                            if (response.getErrorMessage().equals(errorMsg)) {
                                //1.使淼科那边的订单号无效化
                                changeOrderWithMK(order);
                                //2.清除redis里面的放款记录(为了相同订单号能够继续打款 删除之前在redis里面的order)
                                this.redisClient.del(RedisContants.ORDER_LOAN_LOCK_NEW + order.getUuid());
                                //3.更改订单状态为待放款

                                OrdOrder entity = new OrdOrder();
                                entity.setUuid(order.getUuid());
                                entity.setStatus(OrdStateEnum.LOANING.getCode());
                                entity.setApplyTime(new Date());
                                entity.setRemark("");
                                entity.setUpdateTime(new Date());
                                this.ordService.updateOrder(entity);

                                if (order.getStatus() != OrdStateEnum.LOANING.getCode()) {

                                    order.setStatus(OrdStateEnum.LOANING.getCode());
                                    order.setApplyTime(new Date());
                                    order.setUpdateTime(new Date());
                                    this.ordService.addOrderHistory(order);
                                }

                                log.info("重新回到打款队列，订单号：{}", order.getUuid());
                            }
                        }

                    }
                } catch (Exception e) {
                    log.error("查询订单异常,单号:" + order.getUuid(), e);
                }

            }
        }
    }


    /**
     * 通过订单号取消订单
     * @param request
     */
    @Transactional
    public Integer cancleOrder(GetOrdRepayAmoutRequest request) throws ServiceExceptionSpec {

        if (com.yqg.common.utils.StringUtils.isEmpty(request.getOrderNo())) {
            throw new ServiceExceptionSpec(ExceptionEnum.ORDER_NOT_FOUND);
        }
        OrdOrder ordOrder = new OrdOrder();
        ordOrder.setDisabled(0);
        ordOrder.setUuid(request.getOrderNo());
        List<OrdOrder> ordOrders = manOrderOrderDao.scan(ordOrder);
        if (CollectionUtils.isEmpty(ordOrders)) {
            throw new ServiceExceptionSpec(ExceptionEnum.ORDER_NOT_FOUND);
        }
        //订单状态不能为6，7，8，16
        Integer status = ordOrders.get(0).getStatus();
        if (status.equals(OrdStateEnum.LOANING_DEALING.getCode()) ||
                status.equals(OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode()) ||
                status.equals(OrdStateEnum.RESOLVING_OVERDUE.getCode())) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }
        if (status.equals(OrdStateEnum.LOAN_FAILD.getCode()) && !ordOrders.get(0).getRemark().equals("BANK_CARD_ERROR")) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }
        //取消订单
        ordOrder.setDisabled(1);
        ordOrder.setRemark("用户取消借款");
        ordOrder.setUpdateUser(LoginSysUserInfoHolder.getLoginSysUserId());
        ordOrder.setUpdateTime(new Date());
        return manOrderOrderDao.update(ordOrder);
    }


    @Autowired
    private OrdBillDao ordBillDao;
    /**
     * 查询分期账单信息
     * @param request
     * @return
     */
    public List<OrderTermBillResponse> byStagesBillInfo(OrderSearchRequest request) throws ServiceExceptionSpec, ParseException {

        if (StringUtils.isEmpty(request.getUuid())) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        if (!manAuthManagerService.hasAuthorityByRoleName(ManOperatorEnum.listManOperatorEnum())) {
            return new ArrayList<>();
        }
        OrdOrder order = new OrdOrder();
        order.setDisabled(0);
        order.setUuid(request.getUuid());
        List<OrdOrder> ordOrders = ordDao.scan(order);
        if (CollectionUtils.isEmpty(ordOrders)) {
            log.error("byStagesBillInfo order no is null");
            throw new ServiceExceptionSpec(ExceptionEnum.ORDER_NOT_FOUND);
        }
        order = ordOrders.get(0);
        if (!order.getOrderType().equals(OrderTypeEnum.STAGING.getCode())) {
            log.info("byStagesBillInfo orderType is {}, orderNo is {} ", order.getOrderType(), order.getUuid());
            return null;
        }

        List<OrdBill> bills = ordBillDao.billsWithUserUuidAndOrderNo(order.getUserUuid(),order.getUuid());
        if (CollectionUtils.isEmpty(bills)) {
            log.info("byStagesBillInfo bills orderType is {}, orderNo is {} ", order.getOrderType(), order.getUuid());
            return null;
        }
        List<OrderTermBillResponse> list = new ArrayList<>();
        for (OrdBill bill: bills) {
            OrderTermBillResponse response = new OrderTermBillResponse();
            response.setBillNo(bill.getUuid());
            response.setBillAmount(bill.getBillAmout().toString().replace(".00",""));
            response.setBillTerm(bill.getBillTerm());
            response.setRefundTime(com.yqg.common.utils.DateUtils.DateToString(bill.getRefundTime()));
            response.setStatus(bill.getStatus());
            response.setInterest(bill.getInterest().toString().replace(".00",""));

            BigDecimal  shouldPayAmount = BigDecimal.valueOf(0);
            if (bill.getStatus().equals(OrdBillStatusEnum.RESOLVING.getCode())){
                shouldPayAmount = bill.getBillAmout().add(bill.getInterest());
                // 每期应还款总金额 或者 已还款总金额
                response.setTotalAmount(shouldPayAmount.toString().replace(".00",""));

            }else if (bill.getStatus().equals(OrdBillStatusEnum.RESOLVING_OVERDUE.getCode())){
                int dayNum = (int) com.yqg.common.utils.DateUtils.daysBetween(com.yqg.common.utils.DateUtils.formDate(bill.getRefundTime(),"yyyy-MM-dd"), com.yqg.common.utils.DateUtils.formDate(new Date(),"yyyy-MM-dd"));
                shouldPayAmount = bill.getBillAmout().add(bill.getInterest()).add(bill.getOverdueFee()).add(bill.getBillAmout().multiply(bill.getOverdueRate()).multiply(BigDecimal.valueOf(dayNum))).setScale(2);
                // 每期应还款总金额 或者 已还款总金额
                response.setTotalAmount(shouldPayAmount.toString().replace(".00",""));
            }else {
                // 查询还款记录
                OrdRepayAmoutRecord record = new OrdRepayAmoutRecord();
                record.setDisabled(0);
                record.setOrderNo(bill.getUuid());
                record.setUserUuid(bill.getUserUuid());
                List<OrdRepayAmoutRecord> recordList = this.ordRepayAmoutRecordDao.scan(record);
                if (!CollectionUtils.isEmpty(recordList)){
                    OrdRepayAmoutRecord repayRecord = recordList.get(0);
                    response.setTotalAmount(repayRecord.getActualRepayAmout().replace(".00",""));
                    response.setActualRepayAmout(repayRecord.getActualRepayAmout().replace(".00",""));
                }else {
                    log.error("not find pay record order no is {}：",bill.getUuid());
                }
            }
            if (bill.getStatus().equals(OrdBillStatusEnum.RESOLVING_OVERDUE.getCode())){
                // 账单逾期 计算服务费和滞纳金
                response.setOverdueFee(bill.getOverdueFee().toString().replace(".00",""));
                int overdueDay = com.yqg.common.utils.DateUtils.daysBetween(com.yqg.common.utils.DateUtils.formDate(bill.getRefundTime(),"yyyy-MM-dd"), com.yqg.common.utils.DateUtils.formDate(new Date(),"yyyy-MM-dd"));
                response.setPenalty(this.repayService.calculatePenaltyFeeByRepayDaysForBills(bill,overdueDay).toString().replace(".00",""));
            }
            list.add(response);
        }
        return list;


    }
}
