package com.yqg.manage.service.check;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.utils.DateUtils;
import com.yqg.manage.dal.collection.ManReceiverSchedulingDao;
import com.yqg.manage.dal.user.CollectorInfoDao;
import com.yqg.manage.entity.user.ManUser;
import com.yqg.manage.service.check.request.ReceiverSchedulingRequest;
import com.yqg.manage.service.check.response.ManReceiverSchedulingResponse;
import com.yqg.manage.service.collection.CollectionService;
import com.yqg.manage.service.collection.request.CollectionPostRequest;
import com.yqg.manage.service.user.ManUserService;
import com.yqg.service.util.LoginSysUserInfoHolder;
import com.yqg.management.entity.ReceiverScheduling;
import com.yqg.service.third.sms.SmsServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.fest.assertions.api.IntegerAssert;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: microservice
 * @description: 收排班Service
 * @author: 许金泉
 * @create: 2019-04-16 10:22
 **/
@Service
@Slf4j
public class ManReceiverSchedulingService {

    @Autowired
    private ManUserService manUserService;

    @Autowired
    private ManReceiverSchedulingDao manReceiverSchedulingDao;

    @Autowired
    private CollectorInfoDao collectorInfoDao;

    /**
     * @Description: 查询催收排班信息
     * @Param: [request]
     * @return: java.util.List<com.yqg.manage.service.check.response.ManReceiverSchedulingResponse>
     * @Author: 许金泉
     * @Date: 2019/4/16 13:55
     */
    public PageData getReceiverSchedulingList(ReceiverSchedulingRequest request) {
        List<ManReceiverSchedulingResponse> resultData = manReceiverSchedulingDao.getReceiverSchedulingList(request).stream().map(item -> {
            ManReceiverSchedulingResponse checkingVoiceResponse = new ManReceiverSchedulingResponse();
            BeanUtils.copyProperties(item, checkingVoiceResponse);
            return checkingVoiceResponse;
        }).collect(Collectors.toList());
        PageData<List<ManReceiverSchedulingResponse>> result = new PageData<>();
        result.setPageNo(request.getPageNo());
        result.setPageSize(request.getPageSize());
        result.setData(resultData);
        result.setRecordsTotal(this.manReceiverSchedulingDao.getReceiverSchedulingCount(request));
        return result;
    }

    /**
     * @Description: 删除催收排班信息
     * @Param: [request]
     * @return: int
     * @Author: 许金泉
     * @Date: 2019/4/16 14:27
     */
    public Integer deleteReceiverScheduling(List<String> uuidList) {
        return manReceiverSchedulingDao.deleteReceiverScheduling(uuidList);
    }


    /**
     * @Description: 添加或者修改排班信息
     * @Param: [request]
     * @return: java.lang.Integer
     * @Author: 许金泉
     * @Date: 2019/4/16 15:07
     */
    @Transactional
    public Integer addOrUpdateReceiverScheduling(ReceiverSchedulingRequest request) {
        if (StringUtils.isBlank(request.getUserName()) || request.getWork() == null) {
            log.error("Incorrect parameters passed in：" + request.toString());
            throw new IllegalArgumentException("Incorrect parameters passed in");
        }
        Optional<ManUser> manUser = manUserService.getManUserByUserName(request.getUserName());
        if (!manUser.isPresent()) {
            log.error("According to the user name, the corresponding user is not queried.");
            throw new IllegalArgumentException("According to the user name, the corresponding user is not queried.");
        }
        ReceiverScheduling receiverScheduling = new ReceiverScheduling();
        BeanUtils.copyProperties(request, receiverScheduling);
        receiverScheduling.setUserId(manUser.get().getId());
        receiverScheduling.setUpdateTime(new Date());
        receiverScheduling.setUpdateUser(LoginSysUserInfoHolder.getLoginSysUserId());
        if (request.getId() == null) {
            int addCount = 0;
            receiverScheduling.setCreateTime(new Date());
            receiverScheduling.setCreateUser(LoginSysUserInfoHolder.getLoginSysUserId());
            if (request.getStartTime() != null && request.getEndTime() != null) {
                List<Date> betweenDatesAndStartEnd = DateUtils.getBetweenDatesAndStartEnd(request.getStartTime(), request.getEndTime());
                for (Date item : betweenDatesAndStartEnd) {
                    receiverScheduling.setWorkTime(item);
                    receiverScheduling.setId(null);
                    receiverScheduling.setUuid(null);
                    addCount += this.insert(receiverScheduling);
                }
            } else if (request.getWorkTime() != null) {
                return this.insert(receiverScheduling);
            }
            return addCount;
        } else {
            return manReceiverSchedulingDao.update(receiverScheduling);
        }
    }

    /**
     * @Description: 插入数据（如果当天已经存在改用户的排班信息则不插入）
     * @Param: [request]
     * @return: 插入成功条数
     * @Author: 许金泉
     * @Date: 2019/4/17 19:05
     */
    public Integer insert(ReceiverScheduling request) {
        ReceiverScheduling searchModel = new ReceiverScheduling();
        searchModel.setUserName(request.getUserName());
        searchModel.setWorkTime(request.getWorkTime());
        List<ReceiverScheduling> result = this.manReceiverSchedulingDao.scan(searchModel);
        if (CollectionUtils.isEmpty(result)) {
            return this.manReceiverSchedulingDao.insert(request);
        } else {
            return 0;
        }
    }


    /**
     * 导出排班信息导入模板Excel
     */
    public void downloadReceiverSchedulingTemplateExcel(HttpServletResponse response) throws Exception {
        OutputStream output = response.getOutputStream();
        response.reset();
        response.setHeader("Content-Disposition", "attachment;filename=ReceiverSchedulingTemplateExcel.xls");
        response.setContentType("application/vnd.ms-excel");
        /*创建excel*/
        HSSFWorkbook wk = new HSSFWorkbook();
        /*分页sheet*/
        HSSFSheet[] sheetArr = new HSSFSheet[1];
        sheetArr[0] = wk.createSheet("排班信息导入模板");
        HSSFRow rowTemp = sheetArr[0].createRow(0);
        rowTemp.createCell(0).setCellValue("催收人员登陆名");
        rowTemp.createCell(1).setCellValue("排班开始时间");
        rowTemp.createCell(2).setCellValue("排班结束时间");
        rowTemp.createCell(3).setCellValue("是否上班(0:否,1:是)");
        HSSFRow rowData = sheetArr[0].createRow(1);
        rowData.createCell(0).setCellValue("admin");
        HSSFCell cell = rowData.createCell(1);
        cell.setCellType(CellType.STRING);
        cell.setCellValue("2012-12-12");
        HSSFCell cell1 = rowData.createCell(2);
        cell1.setCellValue("2012-12-12");
        cell1.setCellValue("2012-12-13");
        rowData.createCell(3).setCellValue("0");
        wk.write(output);
        output.close();
    }

    @Transactional
    public Integer importReceiverSchedulingDataByExcel(MultipartFile file) throws Exception {
        List<ReceiverSchedulingRequest> receiverSchedulings = loadReceiverSchedulingListByExcel(file);
        int counter = 0;
        for (ReceiverSchedulingRequest item : receiverSchedulings) {
            try {
                Integer integer = this.addOrUpdateReceiverScheduling(item);
                counter += integer;
            } catch (Exception ex) {
                throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_ITEM_ERROR);
            }
        }
        return counter;
    }


    private List<ReceiverSchedulingRequest> loadReceiverSchedulingListByExcel(MultipartFile file) throws IOException {
        List<ReceiverSchedulingRequest> temp = new ArrayList<>();
        InputStream fileIn = file.getInputStream();
//根据指定的文件输入流导入Excel从而产生Workbook对象
        HSSFWorkbook wb0 = new HSSFWorkbook(fileIn);
        Sheet sht0 = wb0.getSheetAt(0);
        String format = "yyyy-MM-dd";
        for (Row r : sht0) {
            //如果当前行的行号（从0开始）未达到2（第三行）则从新循环
            if (r.getRowNum() < 1 || !r.cellIterator().hasNext()) {
                continue;
            }
            try {
                ReceiverSchedulingRequest info = new ReceiverSchedulingRequest();
                for (Cell c:
                     r) {
                    c.setCellType(Cell.CELL_TYPE_STRING);
                }
                info.setUserName(r.getCell(0).getStringCellValue());
                info.setStartTime(DateUtils.stringToDate((String) SmsServiceUtil.getCellFormatValue(r.getCell(1)), format));
                info.setEndTime(DateUtils.stringToDate((String) SmsServiceUtil.getCellFormatValue(r.getCell(2)), format));
                info.setWork(Integer.valueOf(r.getCell(3).getStringCellValue()));
                temp.add(info);
            } catch (Exception ex) {
                log.error("Reading Excel has error", ex);
            }
        }
        fileIn.close();
        return temp;
    }

    /**
     * @Description: 得到今天的排班信息，如果今天没有排班信息则获取最近一次的排班信息
     * @Param: []
     * @return: java.util.List<com.yqg.management.entity.ReceiverScheduling>
     * @Author: 许金泉
     * @Date: 2019/4/18 10:25
     */
    public List<ReceiverScheduling> getLastReceiverSchedulingList() {
        ReceiverSchedulingRequest request = new ReceiverSchedulingRequest();
        request.setWorkTime(new Date());
        request.setPageSize(1000000000);
        request.setWork(1);
        return manReceiverSchedulingDao.getReceiverSchedulingList(request);
//        List<ReceiverScheduling> receiverSchedulingList = manReceiverSchedulingDao.getReceiverSchedulingList(request);
//        if (!CollectionUtils.isEmpty(receiverSchedulingList)) {
//            return receiverSchedulingList;
//        }
//        // 最近一次排班的日期
//        Date lastWorkTime = manReceiverSchedulingDao.getLastWorkTime();
//        request.setWorkTime(lastWorkTime);
//        return manReceiverSchedulingDao.getReceiverSchedulingList(request);
    }

    /**
     * @Description: 根据排班表决定催收人员是否上班
     * @Param: []
     * @return: void
     * @Author: 许金泉
     * @Date: 2019/4/19 14:14
     */
    @Transactional(rollbackFor = Exception.class)
    public void schedulingReceiverRestAndWork() {
        // 获取排班列表
        List<ReceiverScheduling> lastReceiverSchedulingList = this.getLastReceiverSchedulingList();
        for (ReceiverScheduling item : lastReceiverSchedulingList) {
            if (item.getWork() == null) {
                continue;
            }
            int rest = item.getWork() == 0 ? 1 : 0;
            log.info("schedulingReceiverRestAndWork: update collector state:" + rest + ",userName:" + item.getUserName());
            collectorInfoDao.updateCollectorStatusByUserId(rest, item.getUserId());
        }
    }

}
