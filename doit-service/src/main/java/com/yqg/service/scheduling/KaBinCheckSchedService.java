package com.yqg.service.scheduling;

import com.alibaba.fastjson.JSONObject;
import com.yqg.activity.dao.UsrActivityBankDao;
import com.yqg.activity.entity.UsrActivityBank;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.user.UsrBankCardBinEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.utils.StringUtils;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.third.kaBinCheck.KaBinCheckService;
import com.yqg.service.user.request.UsrBankRequest;
import com.yqg.service.user.service.UsrBankService;
import com.yqg.user.dao.UsrBankDao;
import com.yqg.user.entity.UsrBank;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by luhong on 2017/11/27.
 */
@Component
@Slf4j
public class KaBinCheckSchedService {

    @Autowired
    private UsrBankDao usrBankDao;

    @Autowired
    private UsrBankService usrBankService;

    @Autowired
    private KaBinCheckService kaBinCheckService;

    @Autowired
    private OrdDao ordDao;

    @Autowired
    private UsrActivityBankDao usrActivityBankDao;


    /**
     *  大体逻辑：不断扫描 usrBank 的 status 状态为 1=待验证 的发送 bankCode 和 bankNumberNo
     *  拿到名字 和 返回的名字比较，将拿到的状态更改到 usrBank 的状态。
     */
    @Transactional
    public void kaBinCheck() throws ServiceException {
        UsrBank userBank = new UsrBank();
        userBank.setDisabled(0);
        userBank.setStatus(UsrBankCardBinEnum.PENDING.getType());
        userBank.setThirdType(null);
        List<UsrBank> userBanks =  usrBankDao.scan(userBank);
        // 遍历PENDING状态的订单
        if(userBanks.size()>0){
            for(UsrBank item:userBanks){
                UsrBankRequest userBankRequest = new UsrBankRequest();
                userBankRequest.setBankCode(item.getBankCode());
                userBankRequest.setBankNumberNo(item.getBankNumberNo());
                userBankRequest.setUserUuid(item.getUserUuid());
                JSONObject obj = kaBinCheckService.sendCardBinHttpPost(userBankRequest);
                Integer logType = UsrBankCardBinEnum.valueOf(obj.get("bankCardVerifyStatus").toString()).getType();// 卡bin状态(0=未验证，1=待验证,2=成功,3=失败)
                if(logType== UsrBankCardBinEnum.SUCCESS.getType()){
                    String bankHolderName = StringUtils.replaceBlank(obj.get("bankHolderName").toString()).toUpperCase();// 调用的名字
                    String bankCardName = StringUtils.replaceBlank(item.getBankCardName()).toUpperCase();// 数据库中的名字（PENDING状态时候，将订单实名的名字存进去了）
                    Map<String,String> nameMap = usrBankService.judgeNamesFun(obj.get("bankHolderName").toString(),item.getBankCardName());// 比较姓名
                    if(!nameMap.get("bankHolderName").equals(nameMap.get("bankCardName"))) {// 失败
                        item.setStatus(UsrBankCardBinEnum.FAILED.getType());
                        usrBankDao.update(item);
                        kaBinCheckService.kaBinCheckPost(item,"Nama tidak cocok"); // 姓名不匹配
                    }else {// 成功
                        item.setStatus(UsrBankCardBinEnum.SUCCESS.getType());
                        if (obj.get("bankHolderName") != null ){
                            item.setBankCardName(obj.get("bankHolderName").toString());
                        }
                        usrBankDao.update(item);
                        kaBinCheckService.kaBinCheckPost(item,"Kecocokan nama"); //姓名匹配
                    }
                }else if(logType== UsrBankCardBinEnum.PENDING.getType()){//
//                    item.setStatus(UsrBankCardBinEnum.PENDING.getType());
//                    usrBankDao.update(item);
                }else if(logType== UsrBankCardBinEnum.FAILED.getType()){
                    item.setStatus(UsrBankCardBinEnum.FAILED.getType());
                    usrBankDao.update(item);
                    kaBinCheckService.kaBinCheckPost(item,"Mengecek kesalahan"); // 校验失败
                }
            }
        }
    }


    // 邀请好友活动绑卡校验
    @Transactional
    public void activityKaBinCheck() throws ServiceException {
        UsrActivityBank userBank = new UsrActivityBank();
        userBank.setDisabled(0);
        userBank.setStatus(UsrBankCardBinEnum.PENDING.getType());
        List<UsrActivityBank> userBanks =  this.usrActivityBankDao.scan(userBank);
        // 遍历PENDING状态的订单
        if(userBanks.size()>0){
            for(UsrActivityBank item : userBanks){

                UsrBankRequest userBankRequest = new UsrBankRequest();
                userBankRequest.setBankCode(item.getBankCode());
                userBankRequest.setBankNumberNo(item.getBankNumberNo());
                userBankRequest.setUserUuid(item.getUserUuid());
                JSONObject obj = kaBinCheckService.sendCardBinHttpPost(userBankRequest);
                Integer logType = UsrBankCardBinEnum.valueOf(obj.get("bankCardVerifyStatus").toString()).getType();// 卡bin状态(0=未验证，1=待验证,2=成功,3=失败)
                if(logType== UsrBankCardBinEnum.SUCCESS.getType()){
                    Map<String,String> nameMap = usrBankService.judgeNamesFun(obj.get("bankHolderName").toString(),item.getBankCardName());// 比较姓名
                    if(!nameMap.get("bankHolderName").equals(nameMap.get("bankCardName"))) {// 失败
                        item.setStatus(UsrBankCardBinEnum.FAILED.getType());
                        usrActivityBankDao.update(item);
                        log.info("卡片校验失败");
                    }else {// 成功
                        item.setStatus(UsrBankCardBinEnum.SUCCESS.getType());
                        usrActivityBankDao.update(item);
                        log.info("卡片校验成功");
                    }
                }else if(logType== UsrBankCardBinEnum.FAILED.getType()){
                    item.setStatus(UsrBankCardBinEnum.FAILED.getType());
                    usrActivityBankDao.update(item);
                    log.info("卡片校验失败");
                }
            }
        }
    }


    /**
     * 将状态为 机审的 改变成初审
     */
    @Transactional
    public void updateOrderStateToFirstCheck() {
        OrdOrder order = new OrdOrder();
        order.setStatus(OrdStateEnum.MACHINE_CHECKING.getCode());
        List<OrdOrder> list = ordDao.scan(order);
        for(OrdOrder item:list){
            OrdOrder orderUpdate = new OrdOrder();
            orderUpdate = item;
            orderUpdate.setStatus(OrdStateEnum.FIRST_CHECK.getCode());
            ordDao.update(orderUpdate);
        }
    }
}
