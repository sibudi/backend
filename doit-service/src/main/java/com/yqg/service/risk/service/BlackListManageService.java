package com.yqg.service.risk.service;

import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.risk.dao.FraudUserOrderInfoDao;
import com.yqg.risk.entity.FraudUserInfo;
import com.yqg.service.user.service.UserRiskService;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.dao.UsrBlackListDao;
import com.yqg.user.entity.UsrBlackList;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlackListManageService {

    @Autowired
    private UsrBlackListDao usrBlackListDao;

    @Autowired
    private OrdDao ordDao;

    @Autowired
    private Overdue15UserService overdue15UserService;
    @Autowired
    private FraudUserOrderInfoDao fraudUserOrderInfoDao;

    @Autowired
    private UsrService usrService;

    @Autowired
    private UserRiskService userRiskService;

    /**
     * 读取文件解析excel，然后将相关和名单内容设置到db中
     * @param file
     */
    public void addFraudAndSensitityUser(MultipartFile file) throws Exception{
        try {
            Workbook workbook = getWorkBook(file);
            Sheet sheet = workbook.getSheetAt(0);
            int maxRowCount = sheet.getLastRowNum();
            List<String> orderNos = new ArrayList<>();
            List<String> mobileNumbers = new ArrayList<>();
            //第一行行行标题过滤掉
            for (int i = 1; i <= maxRowCount; i++) {
                Row row = sheet.getRow(i);
                if(row==null){
                    continue;
                }
                String orderNo = getCellValue(row.getCell(0));
                String mobileNumber = getCellValue(row.getCell(1));
                if(StringUtils.isNotEmpty(orderNo)){
                    orderNo = orderNo.trim().replaceAll("'","");
                    orderNos.add(orderNo);
                }
                if(StringUtils.isEmpty(orderNo)&&StringUtils.isNotEmpty(mobileNumber)){
                    mobileNumber = mobileNumber.trim().replaceAll("'","");
                    mobileNumbers.add(mobileNumber);
                }
            }
            log.info("the orderNos: "+orderNos);
            log.info("the mobiles: "+mobileNumbers);
            //根据订单号增加欺诈用户黑名单
            addFraudUserByOrders(orderNos);  //有订单的带给您做欺诈用户
            addSensitityUser(mobileNumbers); //没订单的当做催收黑名单用户
            log.info("finished...");
            //根据手机号
        } catch (Exception e) {
            log.info("analyze file error",e);
            throw new Exception("解析文件异常");
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellTypeEnum()) {
            case STRING:

            case NUMERIC:
                return cell.getStringCellValue();
            default:
                break;
        }
        return null;
    }

    public void addFraudUserByOrders(List<String> orders){
        if(CollectionUtils.isEmpty(orders)){
            log.info("the orderNo list is empty");
            return;
        }
        List<String> userList = ordDao.getUserIdsByOrderNos(orders);
        List<String> needCacheList = new ArrayList<>();
        for (String userUuid: userList){

            if(usrBlackListDao.existsFraudUser(userUuid)>0){
                //用户是否已经存在
                continue;
            }
            //欺诈用户放到usrBlackList表(type5)
            usrBlackListDao.addFraudUser(userUuid, "");
            needCacheList.add(userUuid);
        }
        //查询刚刚插入的订单
        if (CollectionUtils.isEmpty(needCacheList)) {
            return ;
        }
        List<UsrBlackList> blackLists = usrBlackListDao.getFixUsrBlackList(needCacheList);
        //欺诈用户数据也放到逾期15天的缓存中
        overdue15UserService.addCallRecordToRedis(blackLists,true, UsrBlackList.BlackUserCategory.OVERDUE15);
        overdue15UserService.addContactToRedis(blackLists,true, UsrBlackList.BlackUserCategory.OVERDUE15);
        overdue15UserService.addSmsToRedis(blackLists,true, UsrBlackList.BlackUserCategory.OVERDUE15);
        overdue15UserService.addCallRecordToRedis(blackLists,true, UsrBlackList.BlackUserCategory.FRAUD);
        overdue15UserService.addContactToRedis(blackLists,true, UsrBlackList.BlackUserCategory.FRAUD);
        overdue15UserService.addSmsToRedis(blackLists,true, UsrBlackList.BlackUserCategory.FRAUD);
        //欺诈用户的订单放到fraudUserOrderInfo表
        addOrdersToFraudUserOrderInfo(userList);
    }

    private void addSensitityUser(List<String> mobiles){
        if(CollectionUtils.isEmpty(mobiles)){
            log.info("the mobile list is empty");
            return;
        }
        //插入数据
        List<String> mobileDesList = mobiles.stream().map(elem-> DESUtils.encrypt(elem)).collect(Collectors.toList());
        List<String> dbExists = usrBlackListDao.existsUserByMobiles(mobileDesList,UsrBlackList.UsrBlackTypeEnum.COLLECTOR_BLACK_LIST_USER.getCode());
        if(!CollectionUtils.isEmpty(dbExists)){
            mobileDesList =  mobileDesList.stream().filter(elem->!dbExists.contains(elem)).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(mobileDesList)) {
            return ;
        }
        usrBlackListDao.addSensitiveUser(mobileDesList,UsrBlackList.UsrBlackTypeEnum.COLLECTOR_BLACK_LIST_USER.getCode());
    }

    private Workbook getWorkBook(MultipartFile file) throws Exception{
        Workbook wb = null;
        //Excel 2003
        if (file.getOriginalFilename().endsWith(".xls")) {
            wb = new HSSFWorkbook(file.getInputStream());
            // Excel 2007/2010
        } else if (file.getOriginalFilename().endsWith(".xlsx")){
            wb = new XSSFWorkbook(file.getInputStream());
        }
        return wb;
    }


    private void addOrdersToFraudUserOrderInfo(List<String> userIds){
        if(CollectionUtils.isEmpty(userIds)){
            return;
        }
        List<OrdOrder> orders = usrBlackListDao.getFraudUserOrdersByUserId(userIds);

        if (CollectionUtils.isEmpty(orders)) {
            return;
        }
        log.warn("total count of fraud userList... {} ", orders.size());
        for (OrdOrder order : orders) {
            try {
                //查看信息是否已经在欺诈信息表
                Integer existsCount = fraudUserOrderInfoDao.existFraudUserOrder(order.getUuid());
                if(existsCount!=null&&existsCount>0){
                    continue;
                }
                UsrUser user = usrService.getUserByUuid(order.getUserUuid());
                FraudUserInfo info = userRiskService.getOrderFraudCompareData(user, order);
                info.setOrderNo(order.getUuid());
                info.setUserUuid(order.getUserUuid());
                fraudUserOrderInfoDao.insert(info);
            } catch (Exception e) {
                log.error("add fraudBaseInfo error", e);
            }

        }
        log.warn("finished to add FraudBaseInfo");
    }
}
