package com.yqg.datafix;


import com.yqg.common.enums.user.CertificationEnum;
import com.yqg.common.enums.user.CertificationResultEnum;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.drools.model.RUserInfo;
import com.yqg.drools.service.RealNameVerificationService;
import com.yqg.drools.utils.DateUtil;
import com.yqg.drools.utils.JsonUtil;
import com.yqg.mongo.dao.MongoPageQueryDal;
import com.yqg.mongo.entity.AdvanceMongoData;
import com.yqg.mongo.entity.MongoPageEntity;
import com.yqg.order.entity.OrdDeviceInfo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.dao.AdvanceBlacklistDao;
import com.yqg.risk.dao.AdvanceMultiPlatformDao;
import com.yqg.risk.entity.AdvanceBlacklist;
import com.yqg.risk.entity.AdvanceMultiPlatform;
//import com.yqg.service.AutoCallErrorService;
//import com.yqg.service.AutoCallService;
import com.yqg.service.order.OrdDeviceExtendInfoService;
import com.yqg.service.order.OrdDeviceInfoService;
import com.yqg.service.order.OrdService;
import com.yqg.service.scheduling.RiskDataSynService;
import com.yqg.service.third.Inforbip.InforbipService;
import com.yqg.service.third.Inforbip.Request.InforbipRequest;
import com.yqg.service.third.advance.AdvanceService;
import com.yqg.service.third.advance.config.AdvanceConfig;
import com.yqg.service.third.advance.response.BlacklistCheckResponse;
import com.yqg.service.third.advance.response.MultiPlatformResponse;
import com.yqg.service.third.izi.Client;
import com.yqg.service.third.izi.IziService;
import com.yqg.service.third.izi.config.IziConfig;
import com.yqg.service.third.izi.response.IziResponse;
import com.yqg.service.third.jxl.JXLConfig;
import com.yqg.service.third.jxl.JXLService;
import com.yqg.service.third.jxl.response.JXLBaseResponse;
import com.yqg.service.user.service.UserVerifyResultService;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.dao.TeleCallResultDao;
import com.yqg.system.entity.TeleCallResult;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.dao.UsrVerifyResultDao;
import com.yqg.user.entity.UsrUser;
import com.yqg.user.entity.UsrVerifyResult;
import com.yqg.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class PrdDataHandler {
    //@Autowired
    //private AutoCallService autoCallService;
    @Autowired
    private InforbipService inforbipService;
    @Autowired
    private TeleCallResultDao teleCallResultDao;

    //@Autowired
    //private AutoCallErrorService autoCallErrorService;
    @Autowired
    private UsrDao usrDao;
    @Autowired
    private IziService iziService;
    @Autowired
    private UsrService usrService;


    //ahalim remark unused code
    // public void iziHistory() {
    //     List<OrdOrder> orders = teleCallResultDao.getHistoryDataToRunIzi();
    //     if (CollectionUtils.isEmpty(orders)) {
    //         return;
    //     }
    //     int i = 0;
    //     for (OrdOrder order : orders) {
    //         i++;
    //         try {
    //             if (i % 100 == 0) {
    //                 Thread.sleep(1000 * 60);
    //             }

    //             UsrUser user = usrDao.getUserInfoById(order.getUserUuid());
    //             String phone = DESUtils.decrypt(user.getMobileNumberDES());
    //             String idCard = user.getIdCardNo();
    //             IziResponse phoneAgeResponse = iziService.getPhoneAge(phone, order.getUuid(), order.getUserUuid());

    //             IziResponse phoneVerifyResponse = iziService.getPhoneVerify(phone, idCard, order.getUuid(), order.getUserUuid());


    //         } catch (Exception e) {

    //         }
    //     }

    //     log.info("finished the izi history data return");
    // }

    // ahalim: Remark unused method
    // public void reSendErrorData() {
    //     List<TeleCallResult> list = teleCallResultDao.getErrorList190219();
    //     autoCallErrorService.resendCall(list);
    // }

    // ahalim: Remark unused method
//     private void sendEmergencyCall() {
//         List<OrdOrder> orderList = teleCallResultDao.reRunOrders();
//         for (OrdOrder order : orderList) {
//             try {
//                 boolean callOwner = false;
//                 boolean callCompanyTel = false;
//                 boolean callallLinkman = false;
//                 List<InforbipRequest> sendList = new ArrayList<>();


//                 //联系人信息
//                 List<InforbipRequest> linkManRequestParam = autoCallService.getLinkmanAutoCallRequest(order);
//                 if (CollectionUtils.isEmpty(linkManRequestParam)) {
//                     log.warn("the linkman auto call request param is empty, orderNo: {}", order.getUuid());
//                 } else {
//                     sendList.addAll(linkManRequestParam);
//                     callallLinkman = true;
//                 }

//                 if (CollectionUtils.isEmpty(sendList)) {
//                     log.info("no linkman info to send auto call, orderNo: {}, userUuid: {}", order.getUuid(), order.getUserUuid());
//                     continue;
//                 }
//                 //查找调用inforbip失败的手机号
// //                   TeleCallResult search = new TeleCallResult();
// //                   search.setDisabled(0);
// //                   search.setOrderNo(order.getUuid());
// //                   search.setCallType(3);
// //                   search.setCallResultType(2,3);
//                 List<TeleCallResult> searchResultList = teleCallResultDao.getErrorResultByOrderNo(order.getUuid());
//                 if (!CollectionUtils.isEmpty(searchResultList)) {
//                     List<String> errorPhone = searchResultList.stream().map(TeleCallResult::getTellNumber).collect(Collectors.toList());
//                     sendList = sendList.stream().filter(elem -> errorPhone.contains(elem.getMobileNumber())).collect(Collectors.toList());
//                 }
//                 inforbipService.sendVoiceMessage(sendList);
//             } catch (Exception e) {
//                 log.error("send call error,orderNo: " + order.getUuid(), e);
//             }
//         }
//     }


    @Autowired
    private AdvanceConfig advanceConfig;


    @Value("${xendit.ktpValidationUrl}")
    private String xenditKtpValidationUrl;

    @Value("${xendit.apiKey}")
    private String apiKey;

    @Autowired
    private IziConfig iziConfig;

    public void iziCheckV3() throws Exception {
        Client api = iziService.setParam(60000, 60000);
        Map<String, String> data = new HashMap<>();
        data.put("name", "MFirman Capriananto");// 可选，字符串，认证人员的姓名
        data.put("id", "3275120401830003");// 字符串，认证人员的身份证号NIK，格式为16位阿拉伯数字
        log.info("request param: {}", JsonUtils.serialize(data));
        String responseStr = api.Request(this.iziConfig.getIdentityCheck3Url(), data);
        log.info("response: {}", responseStr);

//        File f = new File("C:\\Users\\zxc20\\Desktop\\IZI测试名单 _asli_2019_3.20.xlsx");
//        File returnFile = new File("C:\\Users\\zxc20\\Desktop\\IZI测试名单 _asli_2019_3.20结果.xlsx");
//        Workbook wb = getWorkBook(f);
//        Sheet sheet = wb.getSheetAt(0);
//        int maxRowCount = sheet.getLastRowNum();
//        int count = 0;
//        for (int i = 1; i <= maxRowCount; i++) {
//            count++;
//            try {
//                Row row = sheet.getRow(i);
//                if (row == null) {
//                    continue;
//                }
//                if (row.getCell(0) == null) {
//                    break;
//                }
//                // String orderNo = row.getCell(0).getStringCellValue();
//                String idCard = row.getCell(0).getStringCellValue();
//                String name = row.getCell(1).getStringCellValue();
//
//                Cell iziResponse = row.createCell(2);
//                iziResponse.setCellType(CellType.STRING);
//                Client api = iziService.setParam(60000, 60000);
//                Map<String, String> data = new HashMap<>();
//                data.put("name", name);// 可选，字符串，认证人员的姓名
//                data.put("id", idCard);// 字符串，认证人员的身份证号NIK，格式为16位阿拉伯数字
//                log.info("request param: {}", JsonUtils.serialize(data));
//                String responseStr = api.Request(this.iziConfig.getIdentityCheck3Url(), data);
//                log.info("response: {}", responseStr);
//                iziResponse.setCellValue(responseStr);
//
//            } catch (Exception e) {
//                log.error("error data: " + i, e);
//                //e.printStackTrace();
//            }
//        }
//        log.info("count: {}", count);
//        try (OutputStream fileOut = new FileOutputStream(returnFile)) {
//            wb.write(fileOut);
//            wb.close();
//        }

    }

    public void advanceXenditTest() throws Exception {
//        Header authorizationHeader = new BasicHeader("Authorization", "Basic " + Base64Utils.encode((apiKey + ":").getBytes()));
//        Map<String, String> params = new HashMap<>();
//        params.put("nik", "3374036705880001");
//        params.put("name", "Meinita Listyoningrum");
//        params.put("address", "1");//必须包含该字段
//        String responseXendit = HttpUtil.postJson(JsonUtils.serialize(params), xenditKtpValidationUrl, authorizationHeader);
//        //xenditCell.setCellValue(responseXendit);
//        File f = new File("C:\\Users\\zxc20\\Desktop\\Advance-Xendit重测名单-11Mar.xlsx");
//        File returnFile = new File("C:\\Users\\zxc20\\Desktop\\Advance-Xendit测试结果.xlsx");
//        Workbook wb = getWorkBook(f);
//        Sheet sheet = wb.getSheetAt(1);
//        int maxRowCount = sheet.getLastRowNum();
//
//        for (int i = 1; i <= maxRowCount; i++) {
//            try {
//                Row row = sheet.getRow(i);
//                if (row == null) {
//                    continue;
//                }
//                if(row.getCell(0)==null){
//                    break;
//                }
//                String orderNo = row.getCell(0).getStringCellValue();
//                String name = row.getCell(1).getStringCellValue();
//                String idCard = row.getCell(2).getStringCellValue();
//                System.err.println("orderNo: " + orderNo + ", name: " + name + ", idCard: " + idCard);
//
//                Cell advanceCell = row.createCell(3);
//                advanceCell.setCellType(CellType.STRING);
//                Cell xenditCell = row.createCell(4);
//                xenditCell.setCellType(CellType.STRING);
//
//                //调用advance
//                OpenApiClient client = new OpenApiClient(advanceConfig.getApiHost(),
//                        advanceConfig.getAccessKey(), advanceConfig.getSecretKey());
//                Map<String, String> identityCheck = new HashMap<>();
//                identityCheck.put("name", name.trim());
//                identityCheck.put("idNumber", idCard.trim());
////                // 调用接口
////                String response = null;
////                try {
////                    response = client
////                            .request(advanceConfig.getIdentityCheckApi(), JSON.toJSONString(identityCheck));
////                    advanceCell.setCellValue(response);
////                } catch (Exception e) {
////                    log.error("request to advance exception , param：" + JSON.toJSONString(identityCheck), e);
////                }
//                //调用xendit
//                Header authorizationHeader = new BasicHeader("Authorization", "Basic " + Base64Utils.encode((apiKey + ":").getBytes()));
//                Map<String, String> params = new HashMap<>();
//                params.put("nik", idCard);
//                params.put("name", name);
//                params.put("address", "1");//必须包含该字段
//                String responseXendit = HttpUtil.postJson(JsonUtils.serialize(params), xenditKtpValidationUrl, authorizationHeader);
//                xenditCell.setCellValue(responseXendit);
//                Thread.sleep(1000);
//
//            } catch (Exception e) {
//                System.err.println("error data");
//                //e.printStackTrace();
//            }
//        }
//        try(OutputStream fileOut = new FileOutputStream(returnFile)){
//            wb.write(fileOut);
//            wb.close();
//        }


    }


    private static Workbook getWorkBook(File file) throws Exception {
        Workbook wb = null;
        //Excel 2003
        if (file.getName().endsWith(".xls")) {
            wb = new HSSFWorkbook(new FileInputStream(file));
            // Excel 2007/2010
        } else if (file.getName().endsWith(".xlsx")) {
            wb = new XSSFWorkbook(new FileInputStream(file));
        }
        return wb;
    }

    @Autowired
    RiskDataSynService riskDataSynService;
    @Autowired
    AdvanceService advanceService;
    @Autowired
    private RealNameVerificationService realNameVerificationService;
    @Autowired
    private UserVerifyResultService userVerifyResultService;
    @Autowired
    private UsrVerifyResultDao usrVerifyResultDao;

    public void reRunAdvance() {
        List<OrdOrder> orders = riskDataSynService.getReRunAdvanceData();
        if (CollectionUtils.isEmpty(orders)) {
            log.info("the orders is empty");
            return;
        }
        int loopCount = 0;
        for (OrdOrder order : orders) {
            try {
                loopCount++;
                LogUtils.addMDCRequestId(order.getUuid());
                UsrUser user = usrDao.getUserInfoById(order.getUserUuid());
                if (StringUtils.isEmpty(user.getRealName()) || StringUtils.isEmpty(user.getIdCardNo())) {
                    log.error("the info is emprty");
                    continue;
                }

                RUserInfo.IdentityVerifyResult advanceResult = realNameVerificationService.advanceTest(user.getRealName(), user.getIdCardNo());

                boolean isAdvanceVerifySuccess = advanceResult.getAdvanceVerifyResultType().equals(RUserInfo.IdentityVerifyResultType.NAME_FULL_MATCH_SUCCESS)
                        || advanceResult.getAdvanceVerifyResultType().equals(RUserInfo.IdentityVerifyResultType.NAME_FUZZY_MATCH_SUCCESS);

                UsrVerifyResult saveEntity = new UsrVerifyResult();
                saveEntity.setOrderNo(order.getUuid());
                saveEntity.setUserUuid(order.getUserUuid());
                saveEntity.setResponse("risk-advance-test");
                saveEntity.setRemark(advanceResult.getDesc());
                saveEntity.setVerifyResult(isAdvanceVerifySuccess ? 1 : 2);
                saveEntity.setVerifyType(UsrVerifyResult.VerifyTypeEnum.ADVANCE.getCode());
                saveEntity.setCreateTime(new Date());
                saveEntity.setUpdateTime(new Date());
                saveEntity.setUuid(UUIDGenerateUtil.uuid());
                saveEntity.setDisabled(0);
                userVerifyResultService.insertVerifyResult(saveEntity);
                if (loopCount % 50 == 0) {
                    Thread.sleep(1000 * 20);
                }
            } catch (Exception e) {
                log.error("error for order: " + order.getUuid(), e);
            } finally {
                LogUtils.removeMDCRequestId();
            }
        }
    }


    @Autowired
    private AdvanceMultiPlatformDao advanceMultiPlatformDao;
    @Autowired
    private AdvanceBlacklistDao advanceBlacklistDao;

    private boolean existsMultiPlatformData(String userUuid){
        AdvanceMultiPlatform data  = new AdvanceMultiPlatform();
        data.setUserUuid(userUuid);
        List<AdvanceMultiPlatform> resultList = advanceMultiPlatformDao.scan(data);
        return resultList!=null && resultList.size()>0;
    }
    private boolean existsBlacklistData(String userUuid){
        AdvanceBlacklist data  = new AdvanceBlacklist();
        data.setUserUuid(userUuid);
        List<AdvanceBlacklist> resultList = advanceBlacklistDao.scan(data);
        return resultList!=null && resultList.size()>0;
    }


    public void mongoData2MysqlByPageSplit() {
        log.info("process data start...");
        Date startDate = DateUtil.stringToDate("2019-05-06", DateUtil.FMT_YYYY_MM_DD);
        Date endDate = DateUtil.stringToDate("2019-05-30 17:03:04", DateUtil.FMT_YYYY_MM_DD_HH_mm_ss);
        List<AdvanceMongoData> list = getAdvanceDataByPage(1000, startDate, endDate, null);
        if (CollectionUtils.isEmpty(list)) {
            log.info("the query result is empty");
            return;
        } else {
            log.info("total size: {}", list.size());
        }
        Integer loopCount = 0;
        while (true) {

            for (AdvanceMongoData data : list) {
                log.info("start order: {}",data.getOrderNo());
                loopCount++;
                try {
                    if (data.getRequestType().trim().equalsIgnoreCase("/openapi/anti-fraud/v5/blacklist-check")) {
                        //blacklist
                        OrdOrder order = ordService.getOrderByOrderNo(data.getOrderNo());
                        BlacklistCheckResponse response = JsonUtils.deserialize(data.getResponseData(), BlacklistCheckResponse.class);
                        if(existsBlacklistData(data.getUserUuid())){
                            continue;
                        }
                        advanceService.saveBlacklistData(order, response,data.getCreateTime());
                    } else {
                        OrdOrder order = ordService.getOrderByOrderNo(data.getOrderNo());
                        MultiPlatformResponse response = JsonUtils.deserialize(data.getResponseData(), MultiPlatformResponse.class);
                        if(existsMultiPlatformData(data.getUserUuid())){
                            continue;
                        }
                        advanceService.saveMultiPlatformData(order, response,data.getCreateTime());
                    }
                } catch (Exception e) {
                    log.error("save error for order: " + data.getOrderNo(), e);
                }

                if(loopCount%100==0){
                    log.info("now loopCount is： {}",loopCount);
                }
            }
            String lastObjectId = list.stream().max(Comparator.comparing(AdvanceMongoData::getId)).get().getId();
            list = getAdvanceDataByPage(1000, startDate, endDate, lastObjectId);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
        }

        log.info("loopCount: {}", loopCount);
    }


    @Autowired
    private MongoPageQueryDal mongoPageQueryDal;
    @Autowired
    private OrdService ordService;

    public List<AdvanceMongoData> getAdvanceDataByPage(Integer pageSize, Date beginTime, Date endTime, String lastMaxObjectId) {
        log.info("query param: {},{},{},{}", pageSize,beginTime,endTime,lastMaxObjectId);
        MongoPageEntity entity = new MongoPageEntity<AdvanceMongoData>();
        entity.setPageSize(pageSize);
        entity.setCreateStartTime(beginTime);
        entity.setCreateEndTime(endTime);
        entity.setEntityClass(AdvanceMongoData.class);
        entity.setLastPageMaxObjectId(lastMaxObjectId);

        List<AdvanceMongoData> resultList = mongoPageQueryDal.findResultByPage(entity);
        return resultList;
    }

    @Autowired
    OrdDeviceExtendInfoService ordDeviceExtendInfoService;
    @Autowired
    OrdDeviceInfoService ordDeviceInfoService;

    public void generateDeviceInfoHistory() {
//        int startId = 47799, endId = 3281077;
        int startId = 3281077, endId = 3317373;
        int period = (int) Math.ceil((endId - startId) / 10.0);
        for (int i = 0; i < 10; i++) {
            Task t1 = new Task(startId + i * period, startId + (i + 1) * period);
            t1.start();
            try {
                t1.join();
            } catch (InterruptedException e) {
                log.error("join thread interrupted...");
            }
        }

    }


    public class Task extends Thread {
        private Integer startId=1;
        private Integer endId=1;
        public Task(Integer startId,Integer endId){
            this.startId = startId;
            this.endId = endId;
            log.info("thread : {} start end: {}, {}", currentThread().getName(), startId,endId);
        }
        public void run() {
            log.info("thread run: {}", currentThread().getName());
//            int startId = 4, endId = 48078;
            int perTimeCount = 1000;
            int tmpEnd = startId + perTimeCount;
            if (tmpEnd > endId + 1) {
                tmpEnd = endId + 1;
            }
            //search [startId, tmpEnd)
            List<OrdDeviceInfo> deviceInfoList = ordDeviceInfoService.getListById(startId, tmpEnd);
            while (startId < endId) {
                Long startTime = System.currentTimeMillis();
                if (!CollectionUtils.isEmpty(deviceInfoList)) {
                    for (OrdDeviceInfo deviceInfo : deviceInfoList) {
                        try {
                            ordDeviceExtendInfoService.saveExtendInfo(deviceInfo);
                        } catch (Exception e) {
                            log.error("save extendInfo error,orderNo:" + deviceInfo.getOrderNo(), e);
                        }
                    }
                }
                startId = tmpEnd;
                tmpEnd = startId + perTimeCount;
                if (tmpEnd > endId + 1) {
                    tmpEnd = endId + 1;
                }
                Long endTime = System.currentTimeMillis();
                log.info("current startId: {} , endId: {}, cost: {} ms", startId, tmpEnd,(endTime-startTime));
                deviceInfoList = ordDeviceInfoService.getListById(startId, tmpEnd);

            }
        }
    }

    //ahalim remark unused method
    // public void getHistoryReportForInforbip() {
    //     Timer timer = new Timer();
    //     TimerTask task = new TimerTask() {
    //         @Override
    //         public void run() {
    //             try{
    //             inforbipService.getReportWithOldEndPoint();}catch (Exception e){
    //                 log.error("error for old endPoint to get data");
    //             }
    //         }
    //     };

    //     timer.schedule(task, 10 * 1000, 60 * 1000);

    // }

    @Autowired
    private JXLService jxlService;

    @Autowired
    private JXLConfig jxlConfig;
    public void newJXLCheck(){
//        System.err.println(jxlConfig.getKtpVerifyUrl());
//        if(true){
//            return;
//        }
        List<String> orderList = Arrays.asList("011904011238318030",
                "011903251525048420",
                "011903291746549230",
                "011904120913583970",
                "011904102016270640",
                "011904021314564910",
                "011810021856304480",
                "011904050841447580",
                "011904090636095820",
                "011903311615203270",
                "011901171239076040",
                "011904152359041720",
                "011903301311087880",
                "011903291042054650",
                "011904121236599350",
                "011903292018570880",
                "011904181900085730",
                "011904170924060410",
                "011904111521459540",
                "011904122245038460",
                "011904121228474320",
                "011904011300128450",
                "011903250747590790",
                "011903251031109750",
                "011903241123434690",
                "011904211715500590",
                "011904081420258270",
                "011904190822125430",
                "011904031437143670",
                "011904181755494570",
                "011904121156176260",
                "011903301059245540",
                "011903261621503710",
                "011903281759121800",
                "011904051400303200",
                "011904091125000230",
                "011903212327010970",
                "011903241703031270",
                "011903212107108870",
                "011904021818540180",
                "011904051338023070",
                "011903300641290570",
                "011904032151064480",
                "011903251510245180",
                "011904031212158510",
                "011903201534113230",
                "011903242205348290",
                "011903261142235960",
                "011904052221211200",
                "011904061339461230",
                "011903281950478250",
                "011904050701368820",
                "011904061015319470",
                "011903251153263440",
                "011903301737435930",
                "011903281343321080",
                "011903261919157890",
                "011903311452313450",
                "011903301504069560",
                "011903200729202120",
                "011903302124212420",
                "011904080058292730",
                "011903230821338220",
                "011903262206421550",
                "011903221259506590",
                "011903271221369250",
                "011904061730346580",
                "011903252128136880",
                "011904081043369430",
                "011904050125581800"
                );

        for (String orderNo : orderList) {
            try {
                OrdOrder order = ordService.getOrderByOrderNo(orderNo);
                UsrUser user = usrService.getUserByUuid(order.getUserUuid());
                JXLBaseResponse jxlBaseResponse = jxlService.identityVerify(user.getIdCardNo(), user.getRealName());


                riskDataSynService.initVerifyResult(orderNo, order.getUserUuid(), UsrVerifyResult.VerifyTypeEnum.KTP);

                boolean jxlMatch = false;
                if (jxlBaseResponse != null && jxlBaseResponse.isResponseSuccess() && jxlBaseResponse.getData() != null) {
                    JXLBaseResponse.IdentityVerifyData data = JsonUtils.deserialize(JsonUtils.serialize(jxlBaseResponse.getData()), JXLBaseResponse.IdentityVerifyData.class);
                    jxlMatch = "SUCCESS".equalsIgnoreCase(data.getVerifyResult());
                }
                riskDataSynService.updateVerifyResult(orderNo, order.getUserUuid(), jxlBaseResponse == null ? null : JsonUtils.serialize(jxlBaseResponse),
                        jxlMatch ? UsrVerifyResult.VerifyResultEnum.SUCCESS : UsrVerifyResult.VerifyResultEnum.FAILED, UsrVerifyResult.VerifyTypeEnum.KTP);
            } catch (Exception e) {
                log.error("invoke jxl ktp check error: " + orderNo, e);
            }
        }
    }

}
