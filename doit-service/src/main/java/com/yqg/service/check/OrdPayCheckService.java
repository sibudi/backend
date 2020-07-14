package com.yqg.service.check;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yqg.common.enums.order.OrdLoanChannelEnum;
import com.yqg.common.enums.order.PayStatusEnum;
import com.yqg.common.enums.system.SysThirdLogsEnum;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.dao.OrdPayCheckDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.order.entity.OrdPayCheck;
import com.yqg.service.check.response.OrderPayCheckResponse;
import com.yqg.service.system.service.SysThirdLogsService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: caomiaoke
 * Date: 07/02/2018
 * Time: 3:07 PM
 */

@Service
@Slf4j
public class OrdPayCheckService {

    @Autowired
    private OrdDao ordDao;

    @Autowired
    private OrdPayCheckDao ordPayCheckDao;

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private SysThirdLogsService sysThirdLogsService;

    @Value("${pay.checkPayOrderUrl}")
    private String checkPayOrderUrl;
    @Value("${pay.token}")
    private String PAY_TOKEN;


    @Value("${excel.file.dir}")
    private String payExcelDir;

    private ObjectMapper objectMapper;

    @PostConstruct
    private void init(){
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }



    public void saveToExcel() {
        log.info("开始存储到Excel...");
        String startTime = getNDate(0);
        String endTime = getNDate(1);

        List<OrdPayCheck> ordPayCheckList = ordPayCheckDao.getpayCheckedOrderListByTimeStamp(startTime, endTime);
        HSSFWorkbook workbook =  ExcelOperator.insertDataToExcel(ordPayCheckList);
        OutputStream outputStream = null;

        try {
            File excelDir = new File(payExcelDir);
            excelDir.mkdirs();  //ahalim no need to use getParentFile() because payExcelDir is only the directory
            String excelFilePath = excelDir+"/orderPayChecked_"+getNDate(-1)+".xls";
            outputStream = new FileOutputStream(excelFilePath);
            workbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            log.error("创建File 文件失败", e);
        } catch (IOException e) {
            log.error("Workbook 写入文件失败");
        }finally {
            if (outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
        }
    }


    //    开始查询订单
    public void checkPayOrder(PayStatusEnum payStatusEnum) {
        log.info("开始检查已完成付款操作订单");

        String startTime = getNDate(-1);
        String endTime = getNDate(0);

        if (payStatusEnum.getCode() == 1) {
            List<OrdOrder> paySucceedOrderList = getSucceedPayOrderList(startTime, endTime);
            if(CollectionUtils.isEmpty(paySucceedOrderList)){
                log.info("没有${}放款成功的订单.", startTime);
            }else {
                for (OrdOrder ordOrder: paySucceedOrderList) {
                    OrdPayCheck ordPayCheck = getSucceedOrdPayCheck(ordOrder);
                    ordPayCheckDao.insert(ordPayCheck);
                    OrderPayCheckResponse orderPayCheckResponse = null;

                    try {
                        orderPayCheckResponse = sendOrdCheckRequest(ordPayCheck.getOrderNo(), ordPayCheck );
                    } catch (IOException e) {
                        log.error("IOException: Order pay check 失败，orderNo: {}",ordPayCheck.getOrderNo());
                    }

                    if(orderPayCheckResponse != null){
                        if("0".equals(orderPayCheckResponse.getCode())){
                            if("COMPLETED".equals(orderPayCheckResponse.getDisburseStatus())){
                                ordPayCheck.setStatusThird(PayStatusEnum.SUCCEED.getCode());
                            }
                            ordPayCheck.setAmountApplyThird(orderPayCheckResponse.getAmount());
                            ordPayCheck.setLendingTimeThird(orderPayCheckResponse.getLendingTime());
                            ordPayCheck.setDisbursementId(orderPayCheckResponse.getDisbursementId());

                        }else {
                            if("FAILED".equals(orderPayCheckResponse.getDisburseStatus())){
                                ordPayCheck.setStatusThird(PayStatusEnum.FAILED.getCode());
                            }
                            ordPayCheck.setLendingTimeThird(orderPayCheckResponse.getLendingTime());
                            ordPayCheck.setDisbursementId(orderPayCheckResponse.getDisbursementId());
                            ordPayCheck.setErrorCode(orderPayCheckResponse.getErrorCode());
                            ordPayCheck.setErrorMsg(orderPayCheckResponse.getErrorMessage());

                        }
                        ordPayCheckDao.update(ordPayCheck);
                    }
                }
            }
        }

        if (payStatusEnum.getCode() == 2) {
            List<OrdOrder> payFailedOrderList = getFailedPayOrderList(startTime, endTime);
            if(CollectionUtils.isEmpty(payFailedOrderList)){
                log.info("没有${}放款失败的订单.", startTime);
            }else {
                for (OrdOrder ordOrder: payFailedOrderList) {
                    OrdPayCheck ordPayCheck = getFaildOrdPayCheck(ordOrder);
                    ordPayCheckDao.insert(ordPayCheck);
                    OrderPayCheckResponse orderPayCheckResponse = null;

                    try {
                        orderPayCheckResponse = sendOrdCheckRequest(ordPayCheck.getOrderNo(), ordPayCheck );
                    } catch (IOException e) {
                        log.error("IOException: Order pay check 失败，orderNo: {}",ordPayCheck.getOrderNo());
                    }

                    if(orderPayCheckResponse != null){
                        if("0".equals(orderPayCheckResponse.getCode())){
                            if("FAILED".equals(orderPayCheckResponse.getDisburseStatus())){
                                ordPayCheck.setStatusThird(PayStatusEnum.FAILED.getCode());
                            }
                            ordPayCheck.setAmountApplyThird(orderPayCheckResponse.getAmount());
                            ordPayCheck.setLendingTimeThird(orderPayCheckResponse.getLendingTime());
                            ordPayCheck.setDisbursementId(orderPayCheckResponse.getDisbursementId());

                            ordPayCheck.setErrorCode(orderPayCheckResponse.getErrorCode());
                            ordPayCheck.setErrorMsg(orderPayCheckResponse.getErrorMessage());

                        }else {
                            if("FAILED".equals(orderPayCheckResponse.getDisburseStatus())){
                                ordPayCheck.setStatusThird(PayStatusEnum.FAILED.getCode());
                            }
                            ordPayCheck.setLendingTimeThird(orderPayCheckResponse.getLendingTime());
                            ordPayCheck.setDisbursementId(orderPayCheckResponse.getDisbursementId());
                            ordPayCheck.setErrorCode(orderPayCheckResponse.getErrorCode());
                            ordPayCheck.setErrorMsg(orderPayCheckResponse.getErrorMessage());

                        }
                        ordPayCheckDao.update(ordPayCheck);
                    }

                }

            }
        }

    }


    private List<OrdOrder> getSucceedPayOrderList(String startTime, String endTime) {
        return ordDao.getInRepayOrderListByTimeStamp(startTime, endTime);
    }

    private List<OrdOrder> getFailedPayOrderList(String startTime, String endTime) {
        return ordDao.getPayFailedOrderListByTimeStamp(startTime, endTime);
    }

    private static String getNDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }


    private OrdPayCheck getSucceedOrdPayCheck(OrdOrder ordOrder){

        OrdPayCheck ordPayCheck = new OrdPayCheck();

        ordPayCheck.setUuid(UUIDGenerateUtil.uuid());
        ordPayCheck.setDisabled(0);
        ordPayCheck.setCreateTime(new Date());
        ordPayCheck.setUpdateTime(new Date());
        ordPayCheck.setCreateUser(0);
        ordPayCheck.setUpdateUser(0);
        ordPayCheck.setRemark("");

        ordPayCheck.setOrderNo(ordOrder.getUuid());
        ordPayCheck.setUserUuid(ordOrder.getUserUuid());
        ordPayCheck.setStatus(PayStatusEnum.SUCCEED.getCode());
        if(ordOrder.getAmountApply() != null && ordOrder.getServiceFee() != null){
            ordPayCheck.setAmountApply(ordOrder.getAmountApply().subtract(ordOrder.getServiceFee()));
        }
        ordPayCheck.setLendingTime(ordOrder.getLendingTime());
        ordPayCheck.setChannel(OrdLoanChannelEnum.XENDIT.getCode());

        return ordPayCheck;
    }

    private OrdPayCheck getFaildOrdPayCheck(OrdOrder ordOrder){
        OrdPayCheck ordPayCheck = getSucceedOrdPayCheck(ordOrder);
        ordPayCheck.setStatus(PayStatusEnum.FAILED.getCode());
        return ordPayCheck;
    }


    private OrderPayCheckResponse sendOrdCheckRequest(String orderUuid, OrdPayCheck ordPayCheck ) throws IOException {
        Request request = checkPayOrderRequest(orderUuid);
        Response response = okHttpClient.newCall(request).execute();
        String responseJsonStr = response.body().string();
        String responseStr = response.toString()+"body: "+ responseJsonStr;
        log.info("Order pay check response： {}",responseStr);
        sysThirdLogsService.addSysThirdLogs(ordPayCheck.getOrderNo(), ordPayCheck.getUserUuid(), SysThirdLogsEnum.PAY_CHECK.getCode(),0, request.toString(), responseStr);
        if(response.code() == 200){
            return handleOrdPayCheckResponse(responseJsonStr);
        }else {
            log.info("Pay order check 请求异常：orderUuid： {}", ordPayCheck.getOrderNo());
        }
        return null;
    }

    private Request checkPayOrderRequest(String orderUuid){
        String requestUrl = checkPayOrderUrl+"/"+orderUuid;
        log.info("Order pay check 查询请求: {}", requestUrl);
        Request request = new Request.Builder()
                .url(requestUrl)
                .addHeader("X-AUTH-TOKEN",PAY_TOKEN)
                .get()
                .build();
        return request;

    }

    private OrderPayCheckResponse handleOrdPayCheckResponse(String responseStr) throws IOException {
        return objectMapper.readValue(responseStr, OrderPayCheckResponse.class);
    }

}
