package com.yqg.service.system.service;

import com.yqg.common.utils.DESUtils;
import com.yqg.service.system.request.AddUserWhiteListRequest;
import com.yqg.service.system.request.PromoteUserProductLevelRequest;
import com.yqg.service.third.sms.SmsServiceUtil;
import com.yqg.system.dao.StagingProductWhiteListDao;
import com.yqg.system.dao.SysProductDao;
import com.yqg.system.entity.StagingProductWhiteList;
import com.yqg.system.entity.SysProduct;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.dao.UsrProductRecordDao;
import com.yqg.user.dao.UsrProductTempDao;
import com.yqg.user.entity.UsrProductRecord;
import com.yqg.user.entity.UsrProductTemp;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * 提升借款额度
 * Created by wanghuaizhou on 2018/9/3.
 */
@Service
@Slf4j
public class PromoteService {

    @Autowired
    private UsrProductTempDao usrProductTempDao;
    @Autowired
    private SysProductDao sysProductDao;
    @Autowired
    private UsrProductRecordDao usrProductRecordDao;
    @Autowired
    private UsrDao usrDao;
    @Autowired
    private SmsServiceUtil smsServiceUtil;
    @Autowired
    private StagingProductWhiteListDao stagingProductWhiteListDao;
    /**
     * 提升借款额度说明
     * 1.先将需要提额的数据导入userProductTemp表中，导入时需要添加批次号，批次号不要重复使用
     * 2.根据批次号查询这批需要提额的数据
     * 3.开始执行这个放款，完成提额
     * 4.提额完之后要发短信
     *
     * productLevel：  -4：80w   3：150w   4：200w
     *
     */
    public void promoteUser(PromoteUserProductLevelRequest request) {

        String beachId = request.getBatchId();

        log.info("-----__>提升额度开始");
        int productLevelTemp = 0;
        // 获取待提额的列表
        UsrProductTemp entity=new UsrProductTemp();
        entity.setDisabled(0);
        entity.setBeachId(beachId);
        List<UsrProductTemp> userList = this.usrProductTempDao.scan(entity);

        if (!CollectionUtils.isEmpty(userList)) {
            for (UsrProductTemp productTemp : userList) {
                try {

                    UsrUser user = new UsrUser();
                    user.setUuid(productTemp.getUserUuid());
                    user.setDisabled(0);
                    user.setStatus(1);
                    List<UsrUser> users = this.usrDao.scan(user);
                    if (!CollectionUtils.isEmpty(users)) {
                        user = users.get(0);
                    }else {
                        log.error("提额用户不存在，用户uuid为"+productTemp.getUserUuid());
                        continue;
                    }

                    // 用户存在 进行提额
//                    if (user.getProductLevel() == 0) {
//                        productLevelTemp = 3;
//                    }  else {
//                        continue;
//                    }

                    /**
                     *   注释掉下面部分代码  即支持降额（)
                     * */
////                    要提升的级别大于当前用户级别 进行提额
//                    if (user.getProductLevel() < request.getProductLevel()) {
                        productLevelTemp = request.getProductLevel();
//                    }  else {
//                        continue;
//                    }

                    // 1.添加用户提额记录
                    UsrProductRecord record = new UsrProductRecord();
                    record.setUserUuid(productTemp.getUserUuid());
//                    for (SysProduct product : sysProductList) {
//                        if (user.getProductLevel().equals(product.getProductLevel())) {
//                            record.setProductUuid(product.getUuid());
//                            break;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
//                        }
//                    }
                    record.setOrderNo(productTemp.getOrderNo());
                    record.setLastProductLevel(user.getProductLevel());
                    record.setCurrentProductLevel(productLevelTemp);
                    record.setRuleName(productTemp.getRuleName());
                    this.intProductUser(record);

                    // 2.更新用户额度
                    UsrUser userUser = new UsrUser();
                    userUser.setUuid(productTemp.getUserUuid());
                    userUser.setProductLevel(productLevelTemp);
                    userUser.setUpdateTime(new Date());
                    this.usrDao.update(userUser);

                    //3.给该用户发送短信告知
                    sendSmsToUser(user,productLevelTemp);
                } catch (Exception e) {

                    log.info("提升额度异常,{},订单号{}", e, productTemp.getOrderNo());
                }
            }
        }else {
            log.info("没有需要提升额度的用户");
        }
        log.info("-----__>提升额度结束");
    }


    /**
     *   添加到分期产品白名单
     * */
    public void addUserToWhiteList(AddUserWhiteListRequest addUserWhiteListRequest){

        String beachId = addUserWhiteListRequest.getBatchId();

        log.info("-----__>提升额度开始");
        int productLevelTemp = 0;
        // 获取待提额的列表
        UsrProductTemp entity=new UsrProductTemp();
        entity.setDisabled(0);
        entity.setBeachId(beachId);
        List<UsrProductTemp> userList = this.usrProductTempDao.scan(entity);

        if (!CollectionUtils.isEmpty(userList)) {
            for (UsrProductTemp productTemp : userList) {
                try {

                    UsrUser user = new UsrUser();
                    user.setUuid(productTemp.getUserUuid());
                    user.setDisabled(0);
                    user.setStatus(1);
                    List<UsrUser> users = this.usrDao.scan(user);
                    if (!CollectionUtils.isEmpty(users)) {
                        user = users.get(0);
                    }else {
                        log.error("提额用户不存在，用户uuid为"+productTemp.getUserUuid());
                        continue;
                    }

                    // 1.添加用户提额记录
                    UsrProductRecord record = new UsrProductRecord();
                    record.setUserUuid(productTemp.getUserUuid());
                    record.setOrderNo(productTemp.getOrderNo());
                    record.setRuleName(productTemp.getRuleName());
                    record.setProductUuid(addUserWhiteListRequest.getProductUuid());
                    this.intProductUser(record);

//                    2.添加白名单
                    StagingProductWhiteList white = new StagingProductWhiteList();
                    white.setUserUuid(productTemp.getUserUuid());
                    white.setProductUuid(addUserWhiteListRequest.getProductUuid());
                    white.setBeachId(beachId);
                    white.setRuleName(productTemp.getRuleName());
                    this.stagingProductWhiteListDao.insert(white);
//                    //2.给该用户发送短信告知
//                    sendSmsToUser(user,productLevelTemp);
                } catch (Exception e) {

                    log.info("提升额度异常,{},订单号{}", e, productTemp.getOrderNo());
                }
            }
        }else {
            log.info("没有需要提升额度的用户");
        }
        log.info("-----__>提升额度结束");

    }

    public void sendSmsToUser( UsrUser userUser, Integer productLevel) throws Exception{
        log.info("开始发送短信");
        String mobileNumber = "62" + DESUtils.decrypt(userUser.getMobileNumberDES());
        log.info("本次发送短信的的用户手机号："+mobileNumber);
        // 发送提醒短信
        String content = "<Do-It> Karena kredit yg bagus, limit anda naik menjadi Rp 1.500.000! Terus pertahankan kredit baik anda!";

        if (productLevel == 4){
            content = "<Do-It> Karena kredit yg bagus, limit anda naik menjadi Rp 2.000.000! Terus pertahankan kredit baik anda!";
        }

        smsServiceUtil.sendTypeSmsCodeWithType("USER_PROMOTE_LEVER",mobileNumber,content,"ZENZIVA");
        log.info("结束发送短信");
    }

    public void intProductUser(UsrProductRecord record) {
//        record.setRemark("批量提升额度到1500");
        record.setCreateUser(1);
        record.setUpdateUser(1);
        this.usrProductRecordDao.insert(record);
    }
}
