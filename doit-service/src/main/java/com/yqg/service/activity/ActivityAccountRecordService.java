package com.yqg.service.activity;

import com.yqg.activity.dao.ActivityAccountDao;
import com.yqg.activity.dao.ActivityAccountRecordDao;
import com.yqg.activity.dao.UsrActivityBankDao;
import com.yqg.activity.dao.UsrGoPayDao;
import com.yqg.activity.entity.ActivityAccount;
import com.yqg.activity.entity.ActivityAccountRecord;
import com.yqg.activity.entity.UsrActivityBank;
import com.yqg.activity.entity.UsrGoPay;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.OrderNoCreator;
import com.yqg.common.utils.StringUtils;
import com.yqg.service.activity.enums.RecodType;
import com.yqg.service.activity.request.ActivityAccountRecordReq;
import com.yqg.service.activity.request.CaseoutReq;
import com.yqg.service.activity.response.ActivityAccountRecordResp;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.dao.UsrBankDao;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ActivityAccountRecordService {

    @Autowired
    private ActivityAccountRecordDao inviteAccountRecordDao;
    @Autowired
    private UsrService userService;
    @Autowired
    private UsrBankDao usrBankDao;
    @Autowired
    private UsrGoPayDao usrGoPayDao;
    @Autowired
    private InviteService inviteService;
    @Autowired
    private ActivityAccountDao activityAccountDao;
    @Autowired
    private UsrActivityBankDao usrActivityBankDao;
    @Autowired
    private RedisClient redisClient;


    @Transactional
    public void withdrawFail(ActivityAccountRecordReq request) throws ServiceExceptionSpec {
        ActivityAccountRecord activityAccountRecord = new ActivityAccountRecord();
        activityAccountRecord.setUuid(request.getUuid());
        activityAccountRecord = inviteAccountRecordDao.scan(activityAccountRecord).get(0);
        String lockKey = SysParamContants.ACTIVITY_ACCOUNT + activityAccountRecord.getUserUuid();
        // TODO: 2018/8/24 加锁
        if (redisClient.tryGetLock(lockKey, 10)) {
            activityAccountRecord.setType(RecodType.Type5.getId());//失败
            activityAccountRecord.setUpdateTime(new Date());

            ;
            activityAccountRecord.setUpdateUser(request.getUid());
            //更新锁定中状态流水为失败
            this.inviteAccountRecordDao.update(activityAccountRecord);

            String userUuid = activityAccountRecord.getUserUuid();
            ActivityAccount activityAccount = new ActivityAccount();
            activityAccount.setUserUuid(userUuid);
            activityAccount = activityAccountDao.scan(activityAccount).get(0);

            activityAccountRecord = inviteAccountRecordDao.scan(activityAccountRecord).get(0);
            activityAccountRecord.setType(RecodType.Type6.getId());//退回
            //退回前 账户余额
            activityAccountRecord.setPerBalance(activityAccount.getBalance());
//       退回前 账户余额+本次失败退回金额= 退回后账户余额
            activityAccountRecord.setBalance(activityAccount.getBalance().add(activityAccountRecord.getAmount()));
            activityAccountRecord.setId(null);
            activityAccountRecord.setUuid(OrderNoCreator.createOrderNo2());
            activityAccountRecord.setCreateTime(new Date());
            activityAccountRecord.setCreateUser(request.getUid());
            activityAccountRecord.setUpdateUser(request.getUid());
            inviteAccountRecordDao.insert(activityAccountRecord);

            activityAccount = activityAccountDao.scan(activityAccount).get(0);
            if (activityAccount.getLockedbalance().compareTo(activityAccountRecord.getAmount()) != -1) {
                activityAccount.setBalance(activityAccount.getBalance().add(activityAccountRecord.getAmount()));//账户余额+提现金额
                activityAccount.setLockedbalance(activityAccount.getLockedbalance().subtract(activityAccountRecord.getAmount()));//冻结金额-提现金额
                activityAccount.setUpdateUser(request.getUid());
                activityAccountDao.update(activityAccount);//更新账户
            } else {
                //解锁
                redisClient.unLock(lockKey);
                throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
            }
            //解锁
            redisClient.unLock(lockKey);
        }else{
            throw  new ServiceExceptionSpec(ExceptionEnum.LOCKED);
        }


    }

    @Transactional
    public void withdrawSuccess(ActivityAccountRecordReq request) throws ServiceExceptionSpec, ServiceException {

        ActivityAccountRecord activityAccountRecord = new ActivityAccountRecord();
        activityAccountRecord.setUuid(request.getUuid());
        activityAccountRecord = inviteAccountRecordDao.scan(activityAccountRecord).get(0);
        // TODO: 2018/8/24 加锁
        String lockKey = SysParamContants.ACTIVITY_ACCOUNT + activityAccountRecord.getUserUuid();
        if (redisClient.tryGetLock(lockKey, 10)) {
            activityAccountRecord.setType(RecodType.Type3.getId());
            activityAccountRecord.setUpdateTime(new Date());
            activityAccountRecord.setUpdateUser(request.getUid());
            String userUuid = activityAccountRecord.getUserUuid();
            ActivityAccount activityAccount = new ActivityAccount();
            activityAccount.setUserUuid(userUuid);
            activityAccount = activityAccountDao.scan(activityAccount).get(0);
            if (activityAccount.getLockedbalance().compareTo(activityAccountRecord.getAmount()) != -1) {
                activityAccount.setLockedbalance(activityAccount.getLockedbalance().subtract(activityAccountRecord.getAmount()));//冻结金额-提现金额
                activityAccount.setUpdateUser(request.getUid());
                activityAccountDao.update(activityAccount);//更新冻结金额
                this.inviteAccountRecordDao.update(activityAccountRecord);//更新流水状态
            } else {
                //解锁
                redisClient.unLock(lockKey);
                throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
            }
            redisClient.unLock(lockKey);
        }else{
            throw  new ServiceExceptionSpec(ExceptionEnum.LOCKED);
        }
    }


    public PageData getAccountRecords(ActivityAccountRecordReq request) throws ServiceExceptionSpec {
        PageData response = new PageData();
        Integer pageSize = request.getPageSize();
        Integer pageNo = request.getPageNo();
        if (pageNo == null || pageSize == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        Integer pageStart = (pageNo - 1) * pageSize;

        if (StringUtils.isNotEmpty(request.getMobile())) {
            UsrUser usrUser = new UsrUser();
            usrUser.setMobileNumberDES(DESUtils.encrypt(request.getMobile()));
            List<UsrUser> list = userService.getUserInfo(usrUser);
            if (!list.isEmpty()) {
//                request.setActUserUuid(list.get(0).getUuid());
                request.setUserUuid(list.get(0).getUuid());
            } else {
                throw new ServiceExceptionSpec(ExceptionEnum.USER_NOT_FOUND);
            }
        }

        List<ActivityAccountRecordResp> recordResps = new ArrayList<>();
        List<ActivityAccountRecord> list = this.inviteAccountRecordDao.scanAccountRecords(request.getUuid(), request.getType(), request.getCaseoutAccount(), request.getCaseoutAccountName(), request.getBeginTime(), request.getEndTime(), request.getUserUuid(), pageSize, pageStart, request.getChannel());
//        List<ActivityAccountRecord> list = this.inviteAccountRecordDao.scanAccountRecords(request.getUuid(), request.getType(), request.getCaseoutAccount(), request.getCaseoutAccountName(), request.getBeginTime(), request.getEndTime(), request.getActUserUuid(), pageSize, pageStart);
        for (ActivityAccountRecord a : list) {
            ActivityAccountRecordResp recordResp = new ActivityAccountRecordResp();
            recordResp.setUuid(a.getUuid());
            UsrUser usrUser = userService.getAllUserByUuid(a.getUserUuid());
            recordResp.setMobile(DESUtils.decrypt(usrUser.getMobileNumberDES()));
            recordResp.setCaseoutAccount(a.getCaseoutAccount());
            recordResp.setCaseoutAccountName(a.getGoPayUserName());
            String preStr = "+";
            switch (a.getType()) {
                case 3:
                    preStr = "-RP ";
                    break;
                case 4:
                    preStr = "-RP ";
                    break;
                case 5:
                    preStr = "-RP ";
                    break;
                case 7:
                    preStr = "-RP ";
                    break;
                default:
                    preStr = "+RP ";

            }
            recordResp.setAmount(preStr + StringUtils.convMoney(a.getAmount()));
            recordResp.setBalance("RP " + StringUtils.convMoney(a.getBalance()));
            recordResp.setCreateTime(a.getCreateTime());
            recordResp.setType(RecodType.getNameById(a.getType()));
//            recordResp.setType(a.getType()+"");
            recordResps.add(recordResp);
        }

        Integer totalRecord = 0;
//       ActivityAccountRecord record = new ActivityAccountRecord();
//        if (StringUtils.isNotEmpty(request.getUserUuid())) {
//            record.setUserUuid(request.getUserUuid());
//            totalRecord = this.inviteAccountRecordDao.count(record);
//        }else{
        totalRecord = this.inviteAccountRecordDao.scanAccountRecordCount(request.getUuid(), request.getType(), request.getCaseoutAccount(), request.getCaseoutAccountName(), request.getBeginTime(), request.getEndTime(), request.getUserUuid(), request.getChannel());
//        totalRecord = this.inviteAccountRecordDao.scanAccountRecordCount(request.getUuid(), request.getType(), request.getCaseoutAccount(), request.getCaseoutAccountName(), request.getBeginTime(), request.getEndTime(), request.getActUserUuid());
//        }

        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setRecordsTotal(totalRecord);
        response.setData(recordResps);
        return response;

    }

    public PageData withdrawRecord(ActivityAccountRecordReq request) throws ServiceExceptionSpec {
        PageData response = new PageData();
        Integer pageSize = request.getPageSize();
        Integer pageNo = request.getPageNo();
        if (pageNo == null || pageSize == null) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        Integer pageStart = (pageNo - 1) * pageSize;

        if (StringUtils.isNotEmpty(request.getMobile())) {
            UsrUser usrUser = new UsrUser();
            usrUser.setMobileNumberDES(DESUtils.encrypt(request.getMobile()));
            List<UsrUser> list = userService.getUserInfo(usrUser);
            if (!list.isEmpty()) {
//                request.setActUserUuid(list.get(0).getUuid());
                request.setUserUuid(list.get(0).getUuid());
            } else {
                throw new ServiceExceptionSpec(ExceptionEnum.USER_NOT_FOUND);
            }
        }

        List<ActivityAccountRecordResp> recordResps = new ArrayList<>();
        List<ActivityAccountRecord> list = this.inviteAccountRecordDao.withdrawRecord(request.getType(), request.getCaseoutAccount(), request.getCaseoutAccountName(), request.getBeginTime(), request.getEndTime(), request.getUserUuid(), request.getUuid(), pageSize, pageStart);
//        List<ActivityAccountRecord> list = this.inviteAccountRecordDao.withdrawRecord(request.getType(), request.getCaseoutAccount(), request.getCaseoutAccountName(), request.getBeginTime(), request.getEndTime(), request.getActUserUuid(), request.getUuid(), pageSize, pageStart);
        for (ActivityAccountRecord a : list) {
            ActivityAccountRecordResp recordResp = new ActivityAccountRecordResp();
            recordResp.setUuid(a.getUuid());
            UsrUser usrUser = userService.getAllUserByUuid(a.getUserUuid());
            recordResp.setMobile(DESUtils.decrypt(usrUser.getMobileNumberDES()));
            recordResp.setCaseoutAccount(a.getCaseoutAccount());
            recordResp.setCaseoutAccountName(a.getGoPayUserName());
            String preStr = "+";
            switch (a.getType()) {
                case 3:
                    preStr = "-RP ";
                    break;
                case 4:
                    preStr = "-RP ";
                    break;
                case 5:
                    preStr = "-RP ";
                    break;
                default:
                    preStr = "+RP ";

            }
            recordResp.setAmount(preStr + StringUtils.convMoney(a.getAmount()));
            recordResp.setBalance("RP " + StringUtils.convMoney(a.getBalance()));
            recordResp.setCreateTime(a.getCreateTime());
//            recordResp.setType(RecodType.getNameById(a.getType()));
            recordResp.setType(a.getType() + "");
            recordResps.add(recordResp);
        }
        Integer totalRecord = this.inviteAccountRecordDao.withdrawRecordCount(request.getType(), request.getCaseoutAccount(), request.getCaseoutAccountName(), request.getBeginTime(), request.getEndTime(), request.getUserUuid(), request.getUuid());
//        Integer totalRecord = this.inviteAccountRecordDao.withdrawRecordCount(request.getType(), request.getCaseoutAccount(), request.getCaseoutAccountName(), request.getBeginTime(), request.getEndTime(), request.getActUserUuid(), request.getUuid());

        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setRecordsTotal(totalRecord);
        response.setData(recordResps);
        return response;

    }

    /**
     * 用户提现
     *
     * @Author:huwei
     * @Date:18.8.16-19:40
     */
    @Transactional
    public void caseout(CaseoutReq caseoutRequest) throws ServiceException {
        // TODO: 2018/8/24 加锁
        //1.确认提现账户账号
        ActivityAccountRecord record = new ActivityAccountRecord();
        switch (caseoutRequest.getChannel()) {
            case "1":
                UsrActivityBank usrBank = new UsrActivityBank();
                usrBank.setUserUuid(caseoutRequest.getUserUuid());
                usrBank.setDisabled(0);
                usrBank.setStatus(2);
                List<UsrActivityBank> banks = this.usrActivityBankDao.scan(usrBank);
                if (banks.isEmpty()) {
                    log.info("用户银行卡信息不存在 {}", caseoutRequest.getUserUuid());
                    throw new ServiceException(ExceptionEnum.USER_BANK_CARD_NOT_EXIT);
                }
                record.setCaseoutAccount(banks.get(0).getBankNumberNo());
                record.setGoPayUserName(banks.get(0).getBankCardName());
                break;
            case "2":
                UsrGoPay usrGoPay = new UsrGoPay();
                usrGoPay.setUserUuid(caseoutRequest.getUserUuid());
                usrGoPay.setDisabled(0);
                List<UsrGoPay> pays = this.usrGoPayDao.scan(usrGoPay);
                if (pays.isEmpty()) {
                    log.info("用户gopay信息不存在 {}", caseoutRequest.getUserUuid());
                    throw new ServiceException(ExceptionEnum.USER_GOPAY_NOT_EXIT);
                }
                record.setCaseoutAccount(pays.get(0).getMobileNumber());
                record.setGoPayUserName(pays.get(0).getUserName());
        }
        // TODO: 2018/8/24 加锁
        String lockKey = SysParamContants.ACTIVITY_ACCOUNT + caseoutRequest.getUserUuid();
        if (redisClient.tryGetLock(lockKey, 10)) {
            //2.添加流水
            //2.1 查询之前账户信息
            ActivityAccount account = this.inviteService.findInviteAccountByUserUuid(caseoutRequest.getUserUuid());


            if (account.getBalance().compareTo(new BigDecimal(caseoutRequest.getAmount())) < 0) {
                log.info("用户账户余额小于提现金额 {}", caseoutRequest.getUserUuid());
                //解锁
                redisClient.unLock(lockKey);
                throw new ServiceException(ExceptionEnum.INSUFFICIENT_ACCOUNT_BALANCE);
            }
            //2.2 添加流水记录
            record.setUuid(OrderNoCreator.createOrderNo2());
            record.setDisabled(0);
            record.setChannel(Integer.parseInt(caseoutRequest.getChannel()));
            record.setAmount(new BigDecimal(caseoutRequest.getAmount()));
            record.setUserUuid(caseoutRequest.getUserUuid());
            record.setType(RecodType.Type4.getId());
            record.setCreateTime(new Date());
            record.setUpdateTime(new Date());
            record.setPerBalance(account.getBalance());
            record.setBalance(account.getBalance().subtract(new BigDecimal(caseoutRequest.getAmount())));
            record.setUuid(OrderNoCreator.createOrderNo2());
            this.inviteAccountRecordDao.insert(record);
            //3.修改用户佣金账户
            account.setBalance(account.getBalance().subtract(new BigDecimal(caseoutRequest.getAmount())));
            account.setLockedbalance(account.getLockedbalance().add(new BigDecimal(caseoutRequest.getAmount())));
            this.activityAccountDao.update(account);
            //解锁
            redisClient.unLock(lockKey);
        }else{
            throw  new ServiceException(ExceptionEnum.LOCKED);
        }
    }
}
