package com.yqg.service.activity;

import com.yqg.activity.dao.ActivityAccountDao;
import com.yqg.activity.dao.ActivityAccountRecordDao;
import com.yqg.activity.dao.UsrInviteRecordDao;
import com.yqg.activity.entity.ActivityAccount;
import com.yqg.activity.entity.ActivityAccountRecord;
import com.yqg.activity.entity.UsrActivityBank;
import com.yqg.activity.entity.UsrInviteRecord;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.OrderNoCreator;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.activity.request.InviteListReq;
import com.yqg.service.activity.response.InviteListResp;
import com.yqg.service.activity.response.UsrActivityAccountResp;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.user.response.UsrGoPayResp;
import com.yqg.service.user.service.UsrGoPayService;
import com.yqg.service.user.service.UsrService;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Features: di-it邀请活动相关
 * Created by huwei on 18.8.15.
 */
@Service
@Slf4j
public class InviteService {

    @Autowired
    private OrdDao orderDao;
    @Autowired
    private UsrInviteRecordDao usrInviteRecordDao;
    @Autowired
    private ActivityAccountDao inviteAccountDao;
    @Autowired
    private ActivityAccountRecordDao inviteAccountRecordDao;
    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private UsrService userService;
    @Autowired
    private UsrActivityBankService usrActivityBankService;
    @Autowired
    private UsrGoPayService usrGopayService;
    @Autowired
    private RedisClient redisClient;


    public static final Integer channel = 50;

    /**
     * 判断是否为被邀请用户的首次下单
     * 如果是  给邀请人发放佣金  并检测是否有二级邀请人
     * 然后插入佣金记录
     *
     * @Author:huwei
     * @Date:18.8.15-14:27
     */
    @Transactional
    public void checkInvitedOrder(String orderNo) throws ServiceException, ServiceExceptionSpec {
        log.info("----------------------------------------------------------------");
        log.info("checkInvitedOrder orderNo-------------------------------" + orderNo);
        log.info("----------------------------------------------------------------");
        OrdOrder ordOrder = new OrdOrder();
        ordOrder.setUuid(orderNo);
        ordOrder.setDisabled(0);
        List<OrdOrder> scan = this.orderDao.scan(ordOrder);
        if (scan.isEmpty()) {
            throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        OrdOrder order = scan.get(0);
//        //判断订单状态
//        if (order.getStatus() != OrdStateEnum.MACHINE_CHECKING.getCode()){
//            return;
//        }
        //判断订单是否为首次借款
        if (order.getBorrowingCount() > 1 || scan.size() > 1) {
            return;
        }

        // 判断订单的用户有没有申请过
        List<OrdOrder>  commitList = this.orderDao.hasCommit(order.getUserUuid());
        if (commitList.size()>1) {
            return;
        }
        //判断订单渠道
//        UsrUser usrUser = new UsrUser();
        UsrUser user = this.userService.getUserByUuid(order.getUserUuid());

        if (order.getThirdType() != 0 || user.getUserSource() != channel) {
            return;
        }
        // 是否发放过奖励
        ActivityAccountRecord scanRecord = new ActivityAccountRecord();
        scanRecord.setDisabled(0);
        scanRecord.setAmountSource(order.getUserUuid());
        scanRecord.setType(1);
        List<ActivityAccountRecord> recordList = this.inviteAccountRecordDao.scan(scanRecord);
        if (!CollectionUtils.isEmpty(recordList)){
            return;
        }

        //判断是否为被邀请用户
        log.info("判断是否为被邀请用户-------------orderNo-------------------------------" + orderNo);
        UsrInviteRecord record = new UsrInviteRecord();
        record.setDisabled(0);
        record.setInvitedUserUuid(order.getUserUuid());
        List<UsrInviteRecord> records = this.usrInviteRecordDao.scan(record);
        if (!records.isEmpty()) {
            //如果是被邀请用户
            UsrInviteRecord usrInviteRecord = records.get(0);
            // TODO: 2018/8/24 加锁
            String lockKey = SysParamContants.ACTIVITY_ACCOUNT + usrInviteRecord.getUserUuid();
            if (redisClient.tryGetLock(lockKey, 10)) {
                //1.修改邀请关系表中订单步骤为已提交审核
                String account = this.sysParamService.getSysParamValue(SysParamContants.ACTIVITY_INVITE_LV1);
                usrInviteRecord.setStatus(2);
                usrInviteRecord.setType(2);
                usrInviteRecord.setAmount(new BigDecimal(account));
                this.usrInviteRecordDao.update(usrInviteRecord);
                //2.发放佣金
                this.Commission(usrInviteRecord.getUserUuid(), new BigDecimal(account), 1, order.getUserUuid());
                //解锁
                redisClient.unLock(lockKey);
            }else{
               log.info("没有获取到锁-------"+lockKey);
            }
            //3.查询是否有二级邀请人
            UsrInviteRecord usrInviteRecord1 = new UsrInviteRecord();
            usrInviteRecord1.setDisabled(0);
            usrInviteRecord1.setInvitedUserUuid(usrInviteRecord.getUserUuid());
            List<UsrInviteRecord> scan1 = this.usrInviteRecordDao.scan(usrInviteRecord1);
            if (!scan1.isEmpty()) {
                // TODO: 2018/8/24 加锁
                lockKey = SysParamContants.ACTIVITY_ACCOUNT + scan1.get(0).getUserUuid();
                if (redisClient.tryGetLock(lockKey, 10)) {
                    String account2 = this.sysParamService.getSysParamValue(SysParamContants.ACTIVITY_INVITE_LV2);
                    this.Commission(scan1.get(0).getUserUuid(), new BigDecimal(account2), 2, usrInviteRecord.getUserUuid());
                    //解锁
                    redisClient.unLock(lockKey);
                }else{
                    log.info("没有获取到锁-------"+lockKey);
                }
            }
        }
    }

    /**
     * 发放佣金
     *
     * @param userUuid      邀请人uuid
     * @param amount        佣金金额
     * @param type          交易类型 1-一级好友佣金 2-二级好友佣金 3-提现 4-提现锁定 5-提现失败退回
     * @param amountSource 佣金来源
     * @Author:huwei
     * @Date:18.8.15-16:59
     */
    @Transactional
    private void Commission(String userUuid, BigDecimal amount, int type, String amountSource) {
        //1.查询邀请人当前佣金账户
        ActivityAccount inviteAccount = findInviteAccountByUserUuid(userUuid);
        //2.添加邀请人佣金流水记录
        ActivityAccountRecord inviteAccountRecord = new ActivityAccountRecord();
        inviteAccountRecord.setUuid(OrderNoCreator.createOrderNo2());
        inviteAccountRecord.setCreateTime(new Date());
        inviteAccountRecord.setUpdateTime(new Date());
        inviteAccountRecord.setDisabled(0);
        inviteAccountRecord.setUserUuid(userUuid);
        inviteAccountRecord.setPerBalance(inviteAccount.getBalance());
        inviteAccountRecord.setAmount(amount);
        inviteAccountRecord.setType(type);
        inviteAccountRecord.setBalance(inviteAccount.getBalance().add(amount));
        inviteAccountRecord.setAmountSource(amountSource);
        this.inviteAccountRecordDao.insert(inviteAccountRecord);
        //3.更新邀请人佣金账户
        inviteAccount.setBalance(inviteAccount.getBalance().add(amount));
        this.inviteAccountDao.update(inviteAccount);

    }

    /**
     * 查询用户佣金账户
     *
     * @Author:huwei
     * @Date:18.8.15-17:16
     */
    public ActivityAccount findInviteAccountByUserUuid(String userUuid) {
        ActivityAccount inviteAccount = new ActivityAccount();
        inviteAccount.setUserUuid(userUuid);
        inviteAccount.setDisabled(0);
        List<ActivityAccount> accounts = this.inviteAccountDao.scan(inviteAccount);
        if (accounts.isEmpty()) {
            inviteAccount.setBalance(new BigDecimal(0));
            inviteAccount.setLockedbalance(new BigDecimal(0));
            inviteAccount.setCreateTime(new Date());
            inviteAccount.setUpdateTime(new Date());
            inviteAccount.setUuid(UUIDGenerateUtil.uuid());
            this.inviteAccountDao.insert(inviteAccount);
            return findInviteAccountByUserUuid(userUuid);
        }
        return accounts.get(0);
    }

    /**
     * 查询用户的一级被邀请人以及以及被邀请人所带来的收益
     *
     * @param baseRequest
     * @return
     */
    public List<InviteListResp> scanUserInviteList(InviteListReq baseRequest) {
        UsrUser user = this.userService.getUserByUuid(baseRequest.getUserUuid());
        //查询一级被邀请人列表
        List<UsrInviteRecord> usrInviteRecords = getInviteList(baseRequest.getUserUuid());
        List<InviteListResp> listResps = new ArrayList<>();
        for (UsrInviteRecord usr : usrInviteRecords) {
            UsrUser invited = this.userService.getUserByUuid(usr.getInvitedUserUuid());
            InviteListResp inviteListResp = new InviteListResp();
            //注册时间
            inviteListResp.setRegTime(DateUtils.DateToString2(invited.getCreateTime()));
            //用户名
            if (!StringUtils.isEmpty(invited.getRealName())) {
                inviteListResp.setUserName(nameDesensit(invited.getRealName()));
            } else {
                inviteListResp.setUserName(mobileNumberDesensit(DESUtils.decrypt(invited.getMobileNumberDES())));
            }
            //盈利
            BigDecimal decimal = new BigDecimal(0);
            //1.一级邀请人佣金
            if (usr.getStatus()!=2){
                //未下单 未发放佣金则为0
                usr.setAmount(new BigDecimal(0));
            }
            decimal = decimal.add(usr.getAmount());
            //2.查询二级被邀请人列表(当前一级被邀请人的一级被邀请人)
            List<UsrInviteRecord> inviteList = getInviteList(invited.getUuid());
            //3.查询二级被邀请人给邀请人带来的收入
            for (UsrInviteRecord record : inviteList) {
                ActivityAccountRecord accountRecord = new ActivityAccountRecord();
                accountRecord.setAmountSource(record.getUserUuid());
                accountRecord.setDisabled(0);
                accountRecord.setType(2);
                List<ActivityAccountRecord> scan = this.inviteAccountRecordDao.scan(accountRecord);
                if (!scan.isEmpty()) {
                    decimal = decimal.add(scan.get(0).getAmount());
                }
            }
            inviteListResp.setAmount(decimal.toString());
            listResps.add(inviteListResp);
        }
        if (listResps.isEmpty()) {
            return listResps;
        }
        //排序 金额 时间
        listResps.sort(new Comparator<InviteListResp>() {
            @Override
            public int compare(InviteListResp o1, InviteListResp o2) {
                int i = compareAmount(o1.getAmount(), o2.getAmount());
                if (i == 0) {
                    try {
                        return DateUtils.compare_date(DateUtils.stringToDate(o1.getRegTime()), DateUtils.stringToDate(o2.getRegTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                return i;
            }
        });
        //分页

        return pageCollection(baseRequest.getPageNo(), baseRequest.getPageSize(), listResps);
    }

    /**
     * @param pageNo   页数
     * @param pageSize 页面展示数
     * @param list     集合
     * @return
     */
    private List pageCollection(Integer pageNo, Integer pageSize, List list) {
        if (pageNo < 1) {
            pageNo = 1;
        }
        int size = list.size() / pageSize + (list.size() % pageSize == 0 ? 0 : 1);
        int from = 0;
        int to = 0;
        if (size > pageNo) {
            from = (pageNo - 1) * pageSize;
            to = pageNo * pageSize;
        } else if (size == pageNo) {
            from = (pageNo - 1) * pageSize;
            to = list.size();
        } else if (size < pageNo) {
            from = (size - 1) * pageSize;
            to = list.size();
        }
        return list.subList(from, to);
    }

    /**
     * 比较金额
     * a = -1,表示bigdemical小于bigdemical2；
     * a = 0,表示bigdemical等于bigdemical2；
     * a = 1,表示bigdemical大于bigdemical2；
     *
     * @Author:huwei
     * @Date:18.8.16-15:50
     */
    public int compareAmount(String amount1, String amount2) {
        BigDecimal decimal1 = new BigDecimal(amount1);
        BigDecimal decimal2 = new BigDecimal(amount2);
        int a = decimal1.compareTo(decimal2);
        return a;
    }

    /**
     * 查询用户一级被邀请人列表
     *
     * @Author:huwei
     * @Date:18.8.16-14:21
     */
    private List<UsrInviteRecord> getInviteList(String userUuid) {
        UsrInviteRecord usrInviteRecord = new UsrInviteRecord();
        usrInviteRecord.setDisabled(0);
        usrInviteRecord.setUserUuid(userUuid);
        List<UsrInviteRecord> usrInviteRecords = this.usrInviteRecordDao.scan(usrInviteRecord);
        return usrInviteRecords;
    }

    /**
     * 姓名脱敏  暴露前三位
     *
     * @Author:huwei
     * @Date:18.8.16-11:20
     */
    public String nameDesensit(String name) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < name.length(); i++) {
            if (i < 3) {
                stringBuffer.append(name.charAt(i));
            } else {
                stringBuffer.append("*");
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 手机号码脱敏  暴露前4后4
     *
     * @Author:huwei
     * @Date:18.8.16-11:25
     */
    public String mobileNumberDesensit(String mobileNumber) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < mobileNumber.length(); i++) {
            if (i < 4 || i > mobileNumber.length() - 5) {
                stringBuffer.append(mobileNumber.charAt(i));
            } else {
                stringBuffer.append("*");
            }
        }
        return stringBuffer.toString();
    }

    public static void main(String[] args) {
        ArrayList<Object> list = new ArrayList<>();
//        list.add(new Integer(1));
//        list.add(new Integer(2));
//        list.add(new Integer(3));
//        list.add(new Integer(4));
//        list.add(new Integer(5));
//        List<Object> objects = pageCollection(1, 30, list);
//        System.out.println(objects.toString());
    }


    /**
     * 查询用户提现账户列表
     * 1.提现银行卡
     * 2.gopay
     *
     * @Author:huwei
     * @Date:18.8.17-14:58
     */
    public List<UsrActivityAccountResp> getUsrActivityAccountList(BaseRequest baseRequest) {
        ArrayList<UsrActivityAccountResp> usrActivityAccountResps = new ArrayList<>();
        //1.查询用户活动银行卡
        UsrActivityBank usrActivityBank = this.usrActivityBankService.getUsrActivityBank(baseRequest.getUserUuid());
        if (!StringUtils.isEmpty(usrActivityBank)) {
            UsrActivityAccountResp resp = new UsrActivityAccountResp();
            resp.setType(1);
            resp.setChannel(usrActivityBank.getBankCode());
            resp.setNumber(mobileNumberDesensit(usrActivityBank.getBankNumberNo()));
            resp.setStatus(usrActivityBank.getStatus());
            resp.setCardName(nameDesensit(usrActivityBank.getBankCardName()));
            usrActivityAccountResps.add(resp);
        }
        //2.查询用户gopay账号
        UsrGoPayResp usrGoPayResp = this.usrGopayService.selectUserGoPay(baseRequest);
        if (!StringUtils.isEmpty(usrGoPayResp)) {
            UsrActivityAccountResp resp = new UsrActivityAccountResp();
            resp.setType(2);
            resp.setChannel("Go-Pay");
            resp.setNumber(mobileNumberDesensit(usrGoPayResp.getMobileNumber()));
            resp.setCardName(usrGoPayResp.getUserName());
            usrActivityAccountResps.add(resp);
        }

        return usrActivityAccountResps;
    }


}
