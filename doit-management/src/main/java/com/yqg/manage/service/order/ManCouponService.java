package com.yqg.manage.service.order;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.order.OrderTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.utils.StringUtils;
import com.yqg.manage.dal.order.ManCouponRecordDao;
import com.yqg.manage.dal.order.ManOrderOrderDao;
import com.yqg.manage.dal.user.UserUserDao;
import com.yqg.manage.enums.CouponTypeEnum;
import com.yqg.manage.service.order.request.CouponBaseRequest;
import com.yqg.manage.service.order.request.CouponRequest;
import com.yqg.manage.service.order.request.CouponSendRequest;
import com.yqg.manage.service.order.response.CouponResponse;
import com.yqg.manage.service.order.response.TwilioCallCouponResponse;
import com.yqg.manage.util.DateUtils;
import com.yqg.manage.util.ExcellPoiUtils;
import com.yqg.service.util.LoginSysUserInfoHolder;
import com.yqg.manage.util.PageDataUtils;
import com.yqg.order.dao.CouponConfigDao;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.CouponConfig;
import com.yqg.order.entity.CouponRecord;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.third.sms.SmsServiceUtil;
import com.yqg.service.third.twilio.TwilioService;
import com.yqg.service.third.twilio.config.TwilioConfig;
import com.yqg.service.third.twilio.response.TwilioUserInfoResponse;
import com.yqg.system.dao.SysProductDao;
import com.yqg.system.entity.SysProduct;
import com.yqg.user.entity.UsrUser;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: tonggen
 * Date: 2019/2/26
 * time: 2:29 PM
 */
@Component
public class ManCouponService {

    private Logger logger = LoggerFactory.getLogger(ManCouponService.class);

    @Autowired
    private CouponConfigDao couponConfigDao;

    @Autowired
    private ManCouponRecordDao couponRecordDao;

    @Autowired
    private ManOrderOrderDao orderOrderDao;

    @Autowired
    private UserUserDao userUserDao;

    @Autowired
    private OrdDao ordDao;

    @Autowired
    private SysProductDao sysProductDao;

    /**
     * 添加或者更新优惠券
     * @param request
     */
    public Integer addOrUpdateCoupon(CouponBaseRequest request) {

        CouponConfig couponConfig = new CouponConfig();
        BeanUtils.copyProperties(request, couponConfig);
        //添加优惠券
        if (request.getId() == null) {
            return couponConfigDao.insert(couponConfig);
        } else {
            return couponConfigDao.update(couponConfig);
        }
    }

    /**
     * 获得配置列表
     * @param request
     */
    public PageData listCouponConfig(CouponBaseRequest request) {

        PageHelper.startPage(request.getPageNo(), request.getPageSize());

        List<CouponConfig> couponConfigs = couponConfigDao.listCouponConfig();
        PageInfo pageInfo = new PageInfo(couponConfigs);
        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }

    /**
     * 发放一种优惠券（该优惠券在couponConfig中有所配置）
     * @param request
     */
    @Transactional
    public Integer sendCoupons(CouponSendRequest request) throws ServiceExceptionSpec {

        if (CollectionUtils.isEmpty(request.getCouponRequests())) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        //计算操作成功次数
        int count = 0;
        for (CouponRequest couponRequest : request.getCouponRequests()) {
            if (StringUtils.isEmpty(couponRequest.getOrderNo()) || StringUtils.isEmpty(couponRequest.getUserName())
                    || couponRequest.getMoney() == null || StringUtils.isEmpty(couponRequest.getValidityEndTime())
                    || StringUtils.isEmpty(couponRequest.getValidityStartTime())) {
                logger.error("sendCoupons params is wrong, the param is {}.", couponRequest.toString());
                continue;
            }

            OrdOrder ordOrder = new OrdOrder();
            ordOrder.setDisabled(0);
            ordOrder.setUuid(couponRequest.getOrderNo());
            List<OrdOrder> ordOrders = orderOrderDao.scan(ordOrder);
            if (CollectionUtils.isEmpty(ordOrders)) {
                logger.info("sendCoupons no this order.");
                continue;
            }
            ordOrder = ordOrders.get(0);
            //参数认证判断（一个订单号，只能发送一张有效代金券 ；发放金额正整数格式，最高不可超过应还金额；
            // 发放代金券要多层校验，订单号和姓名都正确时，才可发放成功
            if (validCouponJudge(couponRequest, ordOrder)) {
                continue;
            }

            //根据code查询配置ID
            CouponConfig couponConfig = new CouponConfig();
            couponConfig.setStatus(0);
            couponConfig.setDisabled(0);
            couponConfig.setCouponCode(couponRequest.getCouponCode());
            List<CouponConfig> couponConfigs = couponConfigDao.scan(couponConfig);
            if (CollectionUtils.isEmpty(couponConfigs)) {
                continue;
            }
            couponRequest.setUserUuid(ordOrder.getUserUuid());
            //验证已完毕，开始发送
            insertCouponRecord(couponRequest, ordOrder, couponConfigs);
            count++;
        }
        logger.info("sendCoupons total count is {}, succeed count is {}",request.getCouponRequests().size(), count);
        return count;
    }

    private boolean validCouponJudge(CouponRequest couponRequest, OrdOrder ordOrder) {
        //所发放订单必须是普通订单
        if (!OrderTypeEnum.NORMAL.getCode().equals(ordOrder.getOrderType())) {
            logger.info("sendCoupons ordOrder orderType is not 0 {}", ordOrder.getUuid());
            return true;
        }
        //订单状态必须为待还款
        if (!ordOrder.getStatus().equals(OrdStateEnum.RESOLVING_NOT_OVERDUE.getCode()) &&
                !ordOrder.getStatus().equals(OrdStateEnum.RESOLVING_OVERDUE.getCode())) {
            logger.info("sendCoupons ordOrder status is wrong order no is {}.", ordOrder.getUuid());
            return true;
        }
        if (ordOrder.getAmountApply() == null || ordOrder.getAmountApply().compareTo(couponRequest.getMoney()) < 0) {
            logger.info("sendCoupons order amount is wrong. order no is {}", ordOrder.getUuid());
            return true;
        }
        String realName = getUserNameByUuid(ordOrder.getUserUuid());
        if (StringUtils.isEmpty(realName) || !couponRequest.getUserName().equals(realName)) {
            logger.info("sendCoupons user name is wrong.order no is {}", ordOrder.getUuid());
            return true;
        }
        //判断是否已有有效代金券
        int validCount = couponRecordDao.getValidCount(ordOrder.getUuid());
        if (validCount > 0 ) {
            logger.info("this user has validCoupon. order no is {}", ordOrder.getUuid());
            return true;
        }
        return false;
    }

    /**
     * 查询用户姓名
     * @param uuid
     * @return
     */
    private String getUserNameByUuid(String uuid) {
        //查询姓名是否正确
        UsrUser usrUser = new UsrUser();
        usrUser.setDisabled(0);
        usrUser.setUuid(uuid);
        List<UsrUser> usrUsers = userUserDao.scan(usrUser);
        if (CollectionUtils.isEmpty(usrUsers)) {
            logger.info("sendCoupons no this user.");
            return null;
        }
        return usrUsers.get(0).getRealName();
    }

    /**
     * 添加一条订单优惠券记录
     * @param couponRequest
     * @param ordOrder
     * @param couponConfigs
     */
    private void insertCouponRecord(CouponRequest couponRequest, OrdOrder ordOrder, List<CouponConfig> couponConfigs)
            throws ServiceExceptionSpec {
        CouponRecord record = new CouponRecord();
        record.setDisabled(0);
        record.setOrderNo(ordOrder.getUuid());
        record.setUserUuid(couponRequest.getUserUuid());
        record.setUserName(couponRequest.getUserName());
        record.setCouponConfigId(couponConfigs.get(0).getId());
        record.setStatus(CouponRecord.StatusEnum.NOT_USE.getCode());
        record.setMoney(couponRequest.getMoney());
        record.setValidityStartTime(couponRequest.getValidityStartTime());
        record.setValidityEndTime(couponRequest.getValidityEndTime());
        if (record.getValidityStartTime() == null || record.getValidityEndTime() == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.VALITYDATE_FORMAT_IS_ERROR);
        }
        record.setSendPersion(couponRequest.getSendPersion());
        record.setRemark(couponRequest.getRemark());
        record.setCreateUser(couponRequest.getCreateUser());
        couponRecordDao.insert(record);
    }

    /**
     * 获取优惠券统计记录列表
     * @param request
     */
    public PageData listCouponRecord(CouponRequest request) {


        PageHelper.startPage(request.getPageNo(), request.getPageSize());
        List<CouponResponse> rList = couponRecordDao.listCouponRecord(request);
        PageInfo pageInfo = new PageInfo(rList);
        if (CollectionUtils.isEmpty(rList)) {
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }
        rList.stream().forEach(elem -> {
            if (elem.getStatus() == null || !elem.getStatus().equals(CouponRecord.StatusEnum.BE_USED.getCode())) {
                elem.setOrderNo("--");
            }
        });
        SimpleDateFormat minteSf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        rList.stream().forEach(elem -> {
            if (elem.getCreateTime()!= null) {
                elem.setSendTime(minteSf.format(elem.getCreateTime()));
            }
            if (elem.getValidityStartTime() != null && elem.getValidityEndTime() != null) {
                elem.setValidateTime(sf.format(elem.getValidityStartTime()) + "--" + sf.format(elem.getValidityEndTime()));
            }
            if (elem.getUsedDate() != null) {
                elem.setUsedTime(sf.format(elem.getUsedDate()));
            } else {
                elem.setUsedTime("--");
            }
        });
        return PageDataUtils.mapPageInfoToPageData(pageInfo);

    }

    /**
     * 删除
     * @param request
     */
    public void deleteOrUpdateCoupon(CouponBaseRequest request) {

        if (request.getId() != null && !request.getId().equals(0)) {
            couponConfigDao.deleteCouponConfig(request.getId());
        }
    }

    /**
     * 根据不同的类型自动发放优惠券
     * @param request
     */
    public Integer sendCouponsAuto(CouponTypeEnum request) throws ServiceExceptionSpec {

        //获得所有需要发放的用户订单
        List<OrdOrder> allOrders = ordDao.listOrderByOverDueDays(request.getOverDueDays());
        if (CollectionUtils.isEmpty(allOrders)) {
            logger.info("overDueDays is {}, all send coupon orders is null." , request.getOverDueDays());
            return 0;
        }
        //通过优惠券名称查询优惠券
        CouponConfig couponConfig = getCouponConfigByName(request.getAlias());
        if (couponConfig == null) {
            logger.error("not found this couponConfig , alias is {}.", request.getAlias());
            return 0;
        }

        //分装发送优惠券参数
        CouponSendRequest couponSendRequest = new CouponSendRequest();
        List<CouponRequest> couponRequests = new ArrayList<>();
        allOrders.stream().forEach(elem -> {
            CouponRequest couponRequest = new CouponRequest();
            couponRequest.setMoney(getMoneyByProductId(elem, request.getInterestDownDays()));
            couponRequest.setOrderNo(elem.getUuid());
            couponRequest.setUserName(getUserNameByUuid(elem.getUserUuid()));
            couponRequest.setValidityStartTime(DateUtils.getStrCurrentDate());
            //比实际的要少一天
            couponRequest.setValidityEndTime(DateUtils.getFetureDate(request.getValidityDays() - 1));
            couponRequest.setCouponCode(couponConfig.getCouponCode());
            couponRequest.setRemark("系统自动发放优惠券");
            couponRequests.add(couponRequest);

        });
        couponSendRequest.setCouponRequests(couponRequests);
        return sendCoupons(couponSendRequest);
    }

    /**
     * 通过excel批量发送优惠券
     */
    public Integer sendCouponsExcel(MultipartFile file) throws ServiceExceptionSpec {

        if (file == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        Integer userId = LoginSysUserInfoHolder.getLoginSysUserId();
        //分装发送优惠券参数
        CouponSendRequest couponSendRequest = new CouponSendRequest();
        List<CouponRequest> couponRequests = new ArrayList<>();
        try {
            Workbook workbook = ExcellPoiUtils.getWorkbok(file.getInputStream(), file);
            Sheet firstSheet = workbook.getSheetAt(0);
            int index = 0;
            for (Row row : firstSheet) {
                if (index == 0) {
                    index ++;
                    continue;
                }
                String orderNo = (String) SmsServiceUtil.getCellFormatValue(row.getCell(0));
                String userName = (String) SmsServiceUtil.getCellFormatValue(row.getCell(1));
                String validityStartTime = (String) SmsServiceUtil.getCellFormatValue(row.getCell(2));
                String validityEndTime = (String) SmsServiceUtil.getCellFormatValue(row.getCell(3));
                String money = (String) SmsServiceUtil.getCellFormatValue(row.getCell(4));
                String couponCode = (String) SmsServiceUtil.getCellFormatValue(row.getCell(5));
                if (StringUtils.isEmpty(orderNo) || StringUtils.isEmpty(userName)
                        || StringUtils.isEmpty(money) || StringUtils.isEmpty(couponCode)) {
                    continue;
                }
                CouponRequest couponRequest = new CouponRequest();
                couponRequest.setMoney(BigDecimal.valueOf(Long.valueOf(money)));
                couponRequest.setOrderNo(orderNo);
                couponRequest.setUserName(userName);
                couponRequest.setValidityStartTime(validityStartTime);
                couponRequest.setValidityEndTime(validityEndTime);
                couponRequest.setCouponCode(couponCode);
                couponRequest.setCreateUser(userId);
                couponRequest.setRemark("手动批量发放优惠券");
                couponRequests.add(couponRequest);
            }
            couponSendRequest.setCouponRequests(couponRequests);
        } catch (IOException e) {
            logger.error("sendCouponsExcel is error.", e);
        }

        return sendCoupons(couponSendRequest);
    }

    /**
     * 下载批量发送优惠券模板
     */
    public void downloadCouponTemplateExcel(HttpServletResponse response) throws Exception {
        OutputStream output = response.getOutputStream();
        response.reset();
        response.setHeader("Content-Disposition", "attachment;filename=ReceiverSchedulingTemplateExcel.xls");
        response.setContentType("application/vnd.ms-excel");
        /*创建excel*/
        HSSFWorkbook wk = new HSSFWorkbook();
        /*分页sheet*/
        HSSFSheet[] sheetArr = new HSSFSheet[1];
        sheetArr[0] = wk.createSheet("优惠券批量发送");
        HSSFRow rowTemp = sheetArr[0].createRow(0);
        rowTemp.createCell(0).setCellValue("订单号");
        rowTemp.createCell(1).setCellValue("用户姓名");
        rowTemp.createCell(2).setCellValue("有效期开始");
        rowTemp.createCell(3).setCellValue("有效期结束");
        rowTemp.createCell(4).setCellValue("减免券金额");
        rowTemp.createCell(5).setCellValue("优惠券编码");
        HSSFRow rowData = sheetArr[0].createRow(1);
        rowData.createCell(0).setCellValue("011805111911171190");
        rowData.createCell(1).setCellValue("widya astuti");
        HSSFCell cell = rowData.createCell(2);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("2019-05-21");
        HSSFCell cell1 = rowData.createCell(3);
        cell1.setCellType(CellType.STRING);
        cell1.setCellValue("2019-05-23");
        rowData.createCell(4).setCellValue("600000");
        rowData.createCell(5).setCellValue("5877288");
        wk.write(output);
        output.close();
    }

    /**
     * 计算给该订单发放的优惠券金额
     * @param
     * @return
     */
    private BigDecimal getMoneyByProductId(OrdOrder ordOrder, Integer days) {

        SysProduct sysProduct = sysProductDao.getProductInfo(ordOrder.getProductUuid());
        if (sysProduct == null) {
            logger.info("not found product by this productUuid");
            return null;
        }
        BigDecimal result = ordOrder.getAmountApply().multiply(sysProduct.getDueFeeRate())
                .divide(BigDecimal.valueOf(sysProduct.getBorrowingTerm()),10, BigDecimal.ROUND_HALF_DOWN).multiply(BigDecimal.valueOf(days)).divide(BigDecimal.valueOf(100))
                .setScale(0,BigDecimal.ROUND_DOWN).multiply(BigDecimal.valueOf(100));
        if (result != null && result.compareTo(BigDecimal.ZERO) > 0
                && result.compareTo(ordOrder.getAmountApply()) < 0) {
            return result;
        }
        return null;
    }

    private CouponConfig getCouponConfigByName(String alias) {

        CouponConfig couponConfig = new CouponConfig();
        couponConfig.setDisabled(0);
        couponConfig.setAlias(alias);
        couponConfig.setStatus(0);
        List<CouponConfig> couponConfigs = couponConfigDao.scan(couponConfig);
        if (CollectionUtils.isEmpty(couponConfigs)) {
            return null;
        }
        return couponConfigs.get(0);
    }

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private TwilioConfig twilioConfig;
    /**
     * 查询需要外呼的数据 进行外呼
     */
    public void sendCouponAutoTask(CouponTypeEnum request) {

        //查询需要外呼的订单
        List<TwilioCallCouponResponse> responses = listOrderNeedTwilioCall(request.getAlias());
        if (CollectionUtils.isEmpty(responses)) {
            logger.error("sendCouponAutoTask is empty, the alias is {}", request.getAlias());
            return ;
        }
        responses.stream().forEach(elem -> {
            //封装外呼参数
            TwilioUserInfoResponse requestParam = new TwilioUserInfoResponse();
            requestParam.setOrderNo(elem.getOrderNo());
            requestParam.setUserUuid(elem.getUserUuid());
            requestParam.setCallNode(0);
            requestParam.setCallType(0);
            requestParam.setRemark("优惠券自动外呼提醒用户");
            requestParam.setPhoneNumber(elem.getPhoneMobile());
            requestParam.setCallPhase("D" + request.getOverDueDays());
            requestParam.setCallPhaseType(1);
            requestParam.setCallUrl(twilioConfig.getUrl() + "D" + request.getOverDueDays() + "-coupon" + "/twilioXml");
            String from = twilioService.getTwilioFromNumber();
            if (StringUtils.isNotEmpty(from)) {
                requestParam.setFrom(from);
                twilioService.initTwilio();
                twilioService.callTwilio(requestParam);
            }

        });

    }

    /**
     * 查询当天需要外呼
     * @return
     */
    private List<TwilioCallCouponResponse> listOrderNeedTwilioCall(String alias) {

        List<TwilioCallCouponResponse> result = couponRecordDao.listOrderNeedTwilioCall(alias);
        if (CollectionUtils.isEmpty(result)) {
            logger.info("alias is {}, no data to twilio call", alias);
            return null;
        }
        //查询用户手机号码
        result.stream().forEach(elem -> {
            UsrUser usrUser = userUserDao.getUserByUuid(elem.getUserUuid());
            if (usrUser != null) {
                elem.setPhoneMobile(usrUser.getMobileNumberDES());
            }
        });
        return result;
    }

    /**
     * 更新优惠券状态
     */
    public void updateCouponStatus() {

        Integer result = couponRecordDao.updateCouponStatus();
        logger.info("this updateCouponStatus count is {} ", result);
    }
}
