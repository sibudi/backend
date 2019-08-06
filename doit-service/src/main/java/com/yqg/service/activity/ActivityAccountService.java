package com.yqg.service.activity;

import com.alibaba.fastjson.JSONObject;
import com.yqg.activity.dao.ActivityAccountDao;
import com.yqg.activity.dao.ActivityAccountRecordDao;
import com.yqg.activity.dao.UsrActivityBankDao;
import com.yqg.activity.dao.UsrGoPayDao;
import com.yqg.activity.entity.ActivityAccount;
import com.yqg.activity.entity.ActivityAccountRecord;
import com.yqg.activity.entity.UsrActivityBank;
import com.yqg.activity.entity.UsrGoPay;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.system.service.SysParamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ActivityAccountService {

    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private ActivityAccountDao activityAccountDao;
    @Autowired
    private InviteService inviteService;
    @Autowired
    private UsrGoPayDao usrGoPayDao;
    @Autowired
    private UsrActivityBankDao usrActivityBankDao;
    @Autowired
    private ActivityAccountRecordDao activityAccountRecordDao;

    public JSONObject getMyAccount(BaseRequest request) throws ServiceExceptionSpec {
        JSONObject jsonObject = new JSONObject();
        ActivityAccount activityAccount = new ActivityAccount();
        activityAccount.setUserUuid(request.getUserUuid());
        List<ActivityAccount> activityAccounts= this.activityAccountDao.scan(activityAccount);
        if (!activityAccounts.isEmpty()){
            activityAccount = activityAccounts.get(0);
            jsonObject.put("available",activityAccount.getBalance());
            jsonObject.put("lockBalance",activityAccount.getLockedbalance());
        }else{
            jsonObject.put("available",0);
            jsonObject.put("lockBalance",0);
        }

        //最后一次的提现账号
        List<ActivityAccountRecord> list = activityAccountRecordDao.getLastCard(request.getUserUuid());
        if (list.isEmpty()){
            jsonObject.put("channel",-1);
            jsonObject.put("caseoutAccount","");
            jsonObject.put("goPayUserName","");
            jsonObject.put("bankCode","");
        }else{
            jsonObject.put("channel",list.get(0).getChannel());
            if (list.get(0).getChannel().equals(1)){
                UsrActivityBank usrActivityBank = new UsrActivityBank();
                usrActivityBank.setBankNumberNo(list.get(0).getCaseoutAccount());
                usrActivityBank.setDisabled(0);
                List<UsrActivityBank> usrActivityBanks = usrActivityBankDao.scan(usrActivityBank);
                if (usrActivityBanks.isEmpty()){
                    jsonObject.put("bankCode","");
                    jsonObject.put("caseoutAccount","");
                    jsonObject.put("goPayUserName","");
                }else{
                    jsonObject.put("caseoutAccount",this.inviteService.mobileNumberDesensit(list.get(0).getCaseoutAccount()) );
                    jsonObject.put("goPayUserName",inviteService.nameDesensit(list.get(0).getGoPayUserName()) );
                    jsonObject.put("bankCode",usrActivityBanks.get(0).getBankCode());
                }
            }else{
                jsonObject.put("bankCode","Go-Pay");
                UsrGoPay usrGoPay = new UsrGoPay();
                usrGoPay.setUserUuid(request.getUserUuid());
                usrGoPay.setDisabled(0);
                List<UsrGoPay> scan = this.usrGoPayDao.scan(usrGoPay);
                if (CollectionUtils.isEmpty(scan)){
                    jsonObject.put("caseoutAccount","");
                    jsonObject.put("goPayUserName","");
                }else {
                    UsrGoPay goPay = scan.get(0);
                    jsonObject.put("caseoutAccount",this.inviteService.mobileNumberDesensit(goPay.getMobileNumber()) );
                    jsonObject.put("goPayUserName",inviteService.nameDesensit(goPay.getUserName()) );
                }
            }
        }

        String caseOutMin = this.sysParamService.getSysParamValue(SysParamContants.ACTIVITY_CASEOUT_MIN);
        jsonObject.put("caseOutMin",new BigDecimal(caseOutMin));


        return jsonObject;

    }
    public List<JSONObject> getAccountTop10(BaseRequest request) throws ServiceExceptionSpec {
        List<JSONObject> list = new ArrayList<>();
        List<Map> top10= this.activityAccountDao.getAccountTop10();
        for (Map map:top10){
            JSONObject jsonObject = new JSONObject();
            if (null != map.get("username") && StringUtils.isNotEmpty(map.get("username").toString())){
                jsonObject.put("username",inviteService.nameDesensit(map.get("username").toString()));
            }else{
//               map.get("mobileNumber").toString();

                jsonObject.put("username",inviteService.mobileNumberDesensit(map.get("mobileNumber").toString()));
            }
            jsonObject.put("amount","RP "+ StringUtils.convMoney(new BigDecimal(map.get("amount").toString())));
            jsonObject.put("friendNum",map.get("friendNum").toString());
            list.add(jsonObject);
        }
        return list;

    }

}
