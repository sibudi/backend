package com.yqg.manage.service.collection;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.redis.RedisClient;
import com.yqg.manage.dal.collection.CollectionOrderDetailDao;
import com.yqg.manage.dal.collection.ManCollectionRemarkDao;
import com.yqg.manage.dal.collection.ManQualityCheckConfigDao;
import com.yqg.manage.dal.collection.ManQualityCheckRecordDao;
import com.yqg.manage.dal.order.ManOrderOrderDao;
import com.yqg.manage.dal.user.ManUserDao;
import com.yqg.manage.entity.collection.CollectionOrderDetail;
import com.yqg.manage.entity.collection.ManCollectionRemark;
import com.yqg.manage.entity.collection.ManQualityCheckConfig;
import com.yqg.manage.entity.collection.ManQualityCheckRecord;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.enums.ContactTypeEnum;
import com.yqg.manage.enums.DictCollectionEnum;
import com.yqg.manage.service.collection.request.ManQualityConfigRequest;
import com.yqg.manage.service.collection.request.ManQualityRecordRequest;
import com.yqg.manage.service.collection.response.ManQualityCountResponse;
import com.yqg.manage.service.collection.response.OutCollectionResponse;
import com.yqg.manage.service.order.request.OverdueOrderRequest;
import com.yqg.manage.util.DateUtils;
import com.yqg.service.util.LoginSysUserInfoHolder;
import com.yqg.manage.util.PageDataUtils;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.system.response.SysDicItemModel;
import com.yqg.service.system.service.SysDicService;
import com.yqg.service.third.upload.UploadService;
import com.yqg.service.third.upload.response.UploadResultInfo;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 ****/

@Service
@Slf4j
public class ManQualityCheckService {

    @Autowired
    private ManQualityCheckConfigDao manQualityCheckConfigDao;

    @Autowired
    private ManQualityCheckRecordDao manQualityCheckRecordDao;

    @Autowired
    private CollectionOrderDetailDao collectionOrderDetailDao;

    @Autowired
    private ManCollectionRemarkDao manCollectionRemarkDao;

    @Autowired
    private ManUserDao manUserDao;

    @Autowired
    private SysDicService sysDicService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private RedisClient redisClient;

    @Value("${downlaod.filePath}")
    private String filePath;

    /**
     * 分页查询质检配置
     * @param pageNo
     * @param pageSize
     */
    public PageData<List<ManQualityCheckConfig>> listQualityCheckConfigs(Integer pageNo, Integer pageSize,
                                                                         Integer type) {

        PageHelper.startPage(pageNo, pageSize);
        ManQualityCheckConfig config = new ManQualityCheckConfig();
        config.setDisabled(0);
        config.setType(type);
        config.set_orderBy("type asc, updateTime desc");
        List<ManQualityCheckConfig> lists = manQualityCheckConfigDao.scan(config);
        PageInfo pageInfo = new PageInfo(lists);
        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }

    public PageData<List<OutCollectionResponse>> listQualityChecks(OverdueOrderRequest param) throws Exception {

        if (param.getOutsourceId() == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        List<OutCollectionResponse> collectionResponses;
        PageHelper.startPage(param.getPageNo(), param.getPageSize());
        collectionResponses = manQualityCheckConfigDao
                .listQualityChecks(param);
        PageInfo pageInfo = new PageInfo(collectionResponses);
        if (CollectionUtils.isEmpty(collectionResponses)) {
            log.error("the outCollectionsList result is Empty!");
            return PageDataUtils.mapPageInfoToPageData(pageInfo);
        }
        //封装返回数据
        getCollections(collectionResponses, param);
        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer insertQualityCheckRecord(ManQualityRecordRequest param) throws ServiceExceptionSpec {


        if (StringUtils.isEmpty(param.getOrderNo()) || param.getCheckTag() == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        String lockKey = RedisContants.ORDER_COLLECTION_CHECK_QUALITY + param.getOrderNo();
        if (!redisClient.lock(lockKey)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        ManQualityCheckRecord manQualityCheckRecord = new ManQualityCheckRecord();
        manQualityCheckRecord.setCreateUser(LoginSysUserInfoHolder.getLoginSysUserId());
        manQualityCheckRecord.setOrderNo(param.getOrderNo());
        manQualityCheckRecord.setCheckTag(param.getCheckTag());
        manQualityCheckRecord.setRemark(param.getRemark());
        manQualityCheckRecord.setUserUuid(param.getUserUuid());
        manQualityCheckRecord.setType(param.getType());

        //取得当前催收人员
        CollectionOrderDetail detail = new CollectionOrderDetail();
        detail.setDisabled(0);
        detail.setSourceType(0);
        detail.setOrderUUID(param.getOrderNo());
        List<CollectionOrderDetail> lists = collectionOrderDetailDao.scan(detail);
        if (!CollectionUtils.isEmpty(lists)) {
            manQualityCheckRecord.setCollectorId(lists.get(0).getSubOutSourceId().equals(0) ? lists.get(0).getOutsourceId()
                                                : lists.get(0).getSubOutSourceId());
        }
        redisClient.unLock(lockKey);
        return manQualityCheckRecordDao.insert(manQualityCheckRecord);
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
//                response.setMaxOverdueDays(getMaxOverdueDaysByUserUuid(response.getUuid()));
            }
            if (!StringUtils.isEmpty(response.getCheckResult())
                    && !"0".equals(response.getCheckResult())) {
                ManQualityCheckConfig config = new ManQualityCheckConfig();
                config.setDisabled(0);
                config.setId(Integer.valueOf(response.getCheckResult()));
                List<ManQualityCheckConfig> lists = manQualityCheckConfigDao.scan(config);
                if (!CollectionUtils.isEmpty(lists)) {
                    response.setCheckResult(lists.get(0).getTitle());
                    response.setCheckResultInn(lists.get(0).getTitleInn());
                }
            } else {
                response.setCheckResult("");
                response.setCheckResultInn("");
            }

            //分装催收人员姓名
            if (response.getCollectiorId() != null && response.getCollectiorId() != 0) {

                ManUser manUser = new ManUser();
                manUser.setDisabled(0);
                manUser.setId(response.getCollectiorId());
                List<ManUser> manUsers = manUserDao.scan(manUser);
                if (!CollectionUtils.isEmpty(manUsers)) {
                    response.setCollectiorName(manUsers.get(0).getUsername());
                }
            }

            //语音质检结果
            if (!StringUtils.isEmpty(response.getVoiceCheckResult())
                    && !"0".equals(response.getVoiceCheckResult())) {
                ManQualityCheckConfig config = new ManQualityCheckConfig();
                config.setDisabled(0);
                config.setId(Integer.valueOf(response.getVoiceCheckResult()));
                List<ManQualityCheckConfig> lists = manQualityCheckConfigDao.scan(config);
                if (!CollectionUtils.isEmpty(lists)) {
                    response.setVoiceCheckResult(lists.get(0).getTitle());
                    response.setVoiceCheckResultInn(lists.get(0).getTitleInn());
                }
            } else {
                response.setVoiceCheckResult("");
                response.setVoiceCheckResultInn("");
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
        }
    }

    /**
     * 根据质检人员或者催收人员获得质检报表
     */
    public String getCheckReport(Integer outSourceId, Integer checkerId, String startTime,
                                 String endTime, String sessionId) throws Exception {


        long start = System.currentTimeMillis();
        if (StringUtils.isEmpty(startTime) || StringUtils.isEmpty(endTime)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        //如果是催收公司将催收人员变成子员工
        StringBuilder sf = new StringBuilder();
        if (outSourceId != null && !outSourceId.equals(0)) {
            sf.append(outSourceId);
        }

        //获得催收公司对应的人员
        List<SysDicItemModel> dicItemList = sysDicService.
                sysDicItemsListByDicCode(DictCollectionEnum.THIRD_COMPANY.name() +
                        "_" + String.valueOf(outSourceId));
        if (!CollectionUtils.isEmpty(dicItemList)) {
            for (SysDicItemModel item : dicItemList) {
                sf.append(",").append(item.getDicItemValue());
            }
        }

        //创建workbook
        HSSFWorkbook wb = new HSSFWorkbook();
        //获得sheet汇总数据
        List<ManQualityCountResponse> countResponses = manQualityCheckConfigDao.getTotalData(sf.toString(),checkerId,startTime, endTime);
        if (!CollectionUtils.isEmpty(countResponses)) {
            countResponses.stream().forEach(elem -> elem.setCountTime(startTime + "~" + endTime));
            setSheet1(countResponses, wb);
        }
        //获取sheet2数据
        Map<Integer, String> allNames = setSheet2Method(checkerId, startTime, endTime, sf, wb);


        //获取sheet3数据
        List<ManQualityCountResponse> responses = manQualityCheckConfigDao.
                getQualityDataSheet3(sf.toString(),checkerId,startTime, endTime);
        if (!CollectionUtils.isEmpty(responses)) {
            responses.stream().forEach(elem -> {
                Integer operator = Integer.valueOf(elem.getOutsourceId());
                elem.setOperator(allNames.get(operator));
            });
            setSheet3(responses, wb);
        }

        FileOutputStream fout = new FileOutputStream(filePath + "templates.xls");
        wb.write(fout);
        fout.close();
        UploadResultInfo uploadResultInfo =
                uploadService.uploadFile(sessionId, filePath + "templates.xls");
        log.info("total time is " + (System.currentTimeMillis() - start)/1000 + " second");
        return uploadResultInfo.getData();
    }

    private Map<Integer, String> setSheet2Method(Integer checkerId, String startTime, String endTime, StringBuilder sf, HSSFWorkbook wb) {
        //获得数据详情
        List<ManQualityCountResponse> rList = manQualityCheckConfigDao.getDetailData(sf.toString(),checkerId,startTime, endTime);
        //取得所有后台管理人员
        ManUser manUser = new ManUser();
        manUser.setDisabled(0);
        List<ManUser> manUsers = manUserDao.scan(manUser);
        Map<Integer, String> allNames = new HashMap<>();
        manUsers.stream().forEach(elem -> {
            allNames.put(elem.getId(), elem.getUsername());
        });
        if (CollectionUtils.isEmpty(rList)) {
            return allNames;
        }

        //获取所有质检配置表
        ManQualityCheckConfig config = new ManQualityCheckConfig();
        config.setDisabled(0);
        List<ManQualityCheckConfig> configs = manQualityCheckConfigDao.scan(config);
        Map<Integer, ManQualityCheckConfig> allConfigs = new HashMap<>();
        configs.stream().forEach(elem -> {
            allConfigs.put(elem.getId(), elem);
        });

        //分批进行封装用户名称和订单
        Integer total = rList.size();int index = 0;int tail = 1000;
        while (index < total) {
            if (tail > total) {
                tail = total;
            }
            List<String> userUuid = rList.subList(index, tail).stream().filter(e -> StringUtils.isNotBlank(e.getUserName()))
                    .map(ManQualityCountResponse :: getUserName).collect(Collectors.toList());
            List<String> orderNos = rList.subList(index, tail).stream().filter(e -> StringUtils.isNotBlank(e.getOrderNo()))
                    .map(ManQualityCountResponse :: getOrderNo).collect(Collectors.toList());
            List<UsrUser> usrUsers = manQualityCheckConfigDao.batchGetUsers(userUuid);
            List<OrdOrder> ordOrders = manQualityCheckConfigDao.batchGetOrders(orderNos);
            for (int i = index; i < tail; i++) {
                //质检人员
                rList.get(i).setOperator(allNames.get(Integer.valueOf(rList.get(i).getOperator())));
                //催收人员
                rList.get(i).setCollectionName(allNames.get(rList.get(i).getCollectorId()));
                //配置信息封装
                if (allConfigs.get(rList.get(i).getCheckTag()) != null) {
                    rList.get(i).setTitle(allConfigs.get(rList.get(i).getCheckTag()).getTitle());
                    rList.get(i).setTitleInn(allConfigs.get(rList.get(i).getCheckTag()).getTitleInn());
                    rList.get(i).setFineMoney(allConfigs.get(rList.get(i).getCheckTag()).getFineMoney());
                }

                //批量封装用户姓名
                for (UsrUser usrUser : usrUsers) {
                    if (usrUser.getUuid().equals(rList.get(i).getUserName())) {
                        rList.get(i).setUserName(usrUser.getRealName());
                        break;
                    }
                }
                //封装订单逾期天数
                for (OrdOrder ordOrder : ordOrders) {
                    if (ordOrder.getUuid().equals(rList.get(i).getOrderNo())) {
                        Date refundTime = ordOrder.getRefundTime();
                        rList.get(i).setOverDudDay(String.valueOf
                                (com.yqg.common.utils.DateUtils.getDiffDaysIgnoreHours(refundTime, rList.get(i).getCreateTime())));
                        break;
                    }
                }
            }
            index = tail;
            tail += 1000;

        }
        setSheet2(rList, wb);
        log.info("getQualityReport sheets2 detail count is :" + total);

        return allNames;
    }

    private void setSheet1(List<ManQualityCountResponse> countResponses, HSSFWorkbook wb) {
        HSSFSheet sheet = wb.createSheet("汇总");
        sheet.setColumnWidth(0, 5555);
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);  //居中
        HSSFCell cell = row.createCell(0); //第一个单元格
        cell.setCellValue("催收人员");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("统计时间");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("分类");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("存在问题(中文)");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("存在问题(印尼）");
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue("问题统计次数");
        cell.setCellStyle(style);

        cell = row.createCell(6);
        cell.setCellValue("罚款金额");
        cell.setCellStyle(style);

        for(int i = 0; i < countResponses.size(); i++) {
            row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(countResponses.get(i).getCollector());
            row.createCell(1).setCellValue(countResponses.get(i).getCountTime());
            String type;
            if (countResponses.get(i).getType() != null &&
                    countResponses.get(i).getType().equals(1)) {
                type = "语音质检";
            } else {
                type = "备注质检";
            }
            row.createCell(2).setCellValue(type);
            row.createCell(3).setCellValue(countResponses.get(i).getTitle());
            row.createCell(4).setCellValue(countResponses.get(i).getTitleInn());
            row.createCell(5).setCellValue(countResponses.get(i).getQuestionCount());
            row.createCell(6).setCellValue(countResponses.get(i).getFineMoneys());
        }
    }

    private void setSheet2(List<ManQualityCountResponse> countResponses, HSSFWorkbook wb) {
        HSSFSheet sheet = wb.createSheet("详情");
        sheet.setColumnWidth(0, 5555);
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);  //居中
        HSSFCell cell = row.createCell(0); //第一个单元格
        cell.setCellValue("订单编号 Nomor Permohonan");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("姓名Nama Nasabah");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("逾期天数Hari Keterlambatan");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("催收人员 Kolektor");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("分类");
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue("存在问题 Permasalahan");
        cell.setCellStyle(style);

        cell = row.createCell(6);
        cell.setCellValue("存在问题 Permasalahan");
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellValue("日期 Tanggal");
        cell.setCellStyle(style);

        cell = row.createCell(8);
        cell.setCellValue("罚款 Denda");
        cell.setCellStyle(style);

        cell = row.createCell(9);
        cell.setCellValue("质检人员");
        cell.setCellStyle(style);

        cell = row.createCell(10);
        cell.setCellValue("备注");
        cell.setCellStyle(style);

        for(int i = 0; i < countResponses.size(); i++) {
            row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(countResponses.get(i).getOrderNo());
            row.createCell(1).setCellValue(countResponses.get(i).getUserName());
            row.createCell(2).setCellValue(countResponses.get(i).getOverDudDay());
            row.createCell(3).setCellValue(countResponses.get(i).getCollectionName());
            String type;
            if (countResponses.get(i).getType() != null &&
                    countResponses.get(i).getType().equals(1)) {
                type = "语音质检";
            } else {
                type = "备注质检";
            }
            row.createCell(4).setCellValue(type);
            row.createCell(5).setCellValue(countResponses.get(i).getTitle());
            row.createCell(6).setCellValue(countResponses.get(i).getTitleInn());
            row.createCell(7).setCellValue(countResponses.get(i).getDays());
            row.createCell(8).setCellValue(countResponses.get(i).getFineMoney());
            row.createCell(9).setCellValue(countResponses.get(i).getOperator());
            row.createCell(10).setCellValue(countResponses.get(i).getRemark());
        }
    }

    private void setSheet3(List<ManQualityCountResponse> countResponses, HSSFWorkbook wb) {
        HSSFSheet sheet = wb.createSheet("比率统计");
        sheet.setColumnWidth(0, 5555);
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);  //居中
        HSSFCell cell = row.createCell(0); //第一个单元格
        cell.setCellValue("质检人员 Staff QC");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("分配订单数 Jumlah Data yang Dibagikan ");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("备注质检订单数 Jumlah Data yang Dicek ");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("添加备注质检备注数 Tambahkan catatan QC ");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("备注质检率 Persentase QC ");
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue("平均每单备注质检次数 Rata-rata QC per Data ");
        cell.setCellStyle(style);

        cell = row.createCell(6);
        cell.setCellValue("语音质检订单数  ");
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellValue("添加语音质检备注数 ");
        cell.setCellStyle(style);

        cell = row.createCell(8);
        cell.setCellValue("语音质检率");
        cell.setCellStyle(style);

        cell = row.createCell(9);
        cell.setCellValue("平均每单语音质检次数");
        cell.setCellStyle(style);

        for(int i = 0; i < countResponses.size(); i++) {
            row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(countResponses.get(i).getOperator());
            row.createCell(1).setCellValue(countResponses.get(i).getTotalOrderCount());
            row.createCell(2).setCellValue(countResponses.get(i).getQulityOrderCount());
            row.createCell(3).setCellValue(countResponses.get(i).getRemarkOrderCount());
            row.createCell(4).setCellValue(countResponses.get(i).getQualityCheckRate());
            row.createCell(5).setCellValue(countResponses.get(i).getAvgQualityCheckRate());
            row.createCell(6).setCellValue(countResponses.get(i).getVoiceQulityOrderCount());
            row.createCell(7).setCellValue(countResponses.get(i).getVoiceRemarkOrderCount());
            row.createCell(8).setCellValue(countResponses.get(i).getVoiceQualityCheckRate());
            row.createCell(9).setCellValue(countResponses.get(i).getVoiceAvgQualityCheckRate());
        }
    }
    /**
     * 新增或者更新质检配置
     * @param param
     */
    public void insertOrUpdateCheckConfig(ManQualityConfigRequest param) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(param.getFineMoney())) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        ManQualityCheckConfig config = new ManQualityCheckConfig();
        config.setDisabled(0);
        config.setCreateUser(LoginSysUserInfoHolder.getLoginSysUserId());
        config.setUpdateUser(LoginSysUserInfoHolder.getLoginSysUserId());
        config.setTitle(param.getTitle());
        config.setTitleInn(param.getTitleInn());
        config.setFineMoney(param.getFineMoney());
        config.setType(param.getType());

        //新增
        if (param.getId() != null && !param.getId().equals(0)) {
            config.setId(param.getId());
            manQualityCheckConfigDao.update(config);

        } else {
            manQualityCheckConfigDao.insert(config);
        }
    }

    /**
     * 删除一个质检配置
     * @param id
     */
    public Integer deleteQualityCheckConfig(Integer id) throws ServiceExceptionSpec {

        if (id == null || id.equals(0)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        ManQualityCheckConfig config = new ManQualityCheckConfig();
        config.setId(id);
        config.setDisabled(1);
        return manQualityCheckConfigDao.update(config);
    }
}
