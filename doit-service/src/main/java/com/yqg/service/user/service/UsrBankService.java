package com.yqg.service.user.service;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.common.enums.order.OrdStepTypeEnum;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.enums.system.SysThirdLogsEnum;
import com.yqg.common.enums.user.UsrBankCardBinEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.externalChannel.entity.ExternalOrderRelation;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.externalChannel.request.Cash2UserBankRequest;
import com.yqg.service.externalChannel.response.Cash2UsrBankResponse;
import com.yqg.service.externalChannel.service.Cash2OrderService;
import com.yqg.service.externalChannel.service.ExternalChannelDataService;
import com.yqg.service.order.OrdService;
import com.yqg.service.system.service.SysThirdLogsService;
import com.yqg.service.third.kaBinCheck.KaBinCheckService;
import com.yqg.service.user.request.UsrBankRequest;
import com.yqg.service.user.response.Cash2BankCardStatus;
import com.yqg.service.user.response.UsrBankResponse;
import com.yqg.system.dao.SysBankDao;
import com.yqg.system.entity.SysBankBasicInfo;
import com.yqg.user.dao.UsrBankDao;
import com.yqg.user.dao.UsrDao;
import com.yqg.user.entity.UsrBank;
import com.yqg.user.entity.UsrUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by luhong on 2017/11/25.
 */
@Service
@Slf4j
public class UsrBankService {

    @Value("${third.pay.cardBinUrl}")
    private String cardBinUrl;

    @Value("${pay.inactiveOrderUrl}")
    private String inactiveOrderUrl;

    @Value("${pay.token}")
    private String PAY_TOKEN;

    @Autowired
    private UsrBankDao usrBankDao;

    @Autowired
    private UsrDao usrDao;

    @Autowired
    private SysBankDao sysBankDao;

    @Autowired
    private UsrBaseInfoService usrBaseInfoService;

    @Autowired
    private KaBinCheckService kaBinCheckService;

    @Autowired
    private OrdDao ordDao;

    @Autowired
    private OrdService ordService;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private SysThirdLogsService sysThirdLogsService;


    @Autowired
    private ExternalChannelDataService externalChannelDataService;

    @Autowired
    private Cash2OrderService cash2OrderService;

    @Autowired
    private OkHttpClient httpClient;

    /**
     * 拉取绑卡状态
     *
     * @param cash2UserBankRequest
     * @param redisClient
     * @throws ServiceException
     */
    public Cash2BankCardStatus getCash2BankCardStatus(Cash2UserBankRequest cash2UserBankRequest, RedisClient redisClient) throws ServiceException {
        // 根据
        Cash2BankCardStatus cash2BankCardStatus = new Cash2BankCardStatus();
        if (StringUtils.isNotEmpty(cash2UserBankRequest.getOrderNo())) {
            ExternalOrderRelation externalOrderRelation = externalChannelDataService.getExternalOrderRelationByExternalOrderNo(cash2UserBankRequest.getOrderNo());
//            cash2UserBankRequest.setOrderNo(externalOrderRelation.getOrderNo());
//            cash2UserBankRequest.setUserUuid(externalOrderRelation.getUserUuid());
            UsrBank usrBank = new UsrBank();
            usrBank.setUserUuid(externalOrderRelation.getUserUuid());
            usrBank.setThirdType(cash2UserBankRequest.getThirdType());
            List<UsrBank> lists = usrBankDao.scan(usrBank);
            if (!CollectionUtils.isEmpty(lists)) {
                log.error("没有这个订单!");
            }
            UsrBank usrBankObj = lists.get(0);
            // （0:审核中 ，1:成功 ，2:失败）
            // (0=未验证，1=待验证,2=成功,3=失败)
            if (usrBankObj.getStatus() == 2) {
                cash2BankCardStatus.setStatus(1);
            } else if (usrBankObj.getStatus() == 3) {
                cash2BankCardStatus.setStatus(2);
            }
            cash2BankCardStatus.setOrderNo(cash2UserBankRequest.getOrderNo());
        } else {
            log.error("没有这个订单!");
//            throw new ServiceException(ExceptionEnum.ORDER_COMMIT_REPEAT);
        }
        return cash2BankCardStatus;
    }


    /**
     * 用户绑卡
     */
    public void bindBankCard(UsrBankRequest userBankRequest, RedisClient redisClient) throws ServiceException, ServiceExceptionSpec {

//        /**
//         *   1.删除掉特殊字符
//         *   2.如果库里存在同样的卡号，提示
//         * */
//
//        UsrBank userBankScan = new UsrBank();
//        userBankScan.setBankCode(userBankRequest.getBankCode());
//        userBankScan.setBankNumberNo(userBankRequest.getBankNumberNo().replace(" ", ""));
//        List<UsrBank> bankList = usrBankDao.scan(userBankScan);
//        if(!CollectionUtils.isEmpty(bankList)){
//            log.error("该卡已经存在,请更换其他银行卡!");
//            throw new ServiceException(ExceptionEnum.SYSTEM_USER_BANK_IS_EXIST);
//        }

        userBankRequest.setBankCardName(userBankRequest.getBankCardName().replace("'", "").replace("`", "").trim());


        // 同步锁（请勿重复提交）
        String lockKey = "bindBankCard" + userBankRequest.getUserUuid();
        if (!redisClient.lockRepeat(lockKey)) {
            log.error("请勿重复提交!");
            throw new ServiceException(ExceptionEnum.ORDER_COMMIT_REPEAT);
        }
        try {

            if (!StringUtils.isEmpty(userBankRequest.getOrderNo())) {
                //  说明是从订单流程进入的

                //  判断订单步骤是否正确
                this.usrBaseInfoService.checkOrderStep(userBankRequest.getOrderNo(), 0);

                //  检查卡号是否存在
                cheakUserBankIsExit(userBankRequest, true);
            } else {
                // 2、 说明是从个人中心进入的
                cheakUserBankIsExit(userBankRequest, false);
            }
        } finally {
            redisClient.unlockRepeat(lockKey);
        }
    }

    /**
     * 银行卡post卡bin
     *
     * @param userUuid
     * @throws ServiceException
     */
    public void sendCardBinResult(String userUuid) throws ServiceException {
        List<UsrBank> userBanks = usrBankDao.getSuccess(userUuid);
        for (UsrBank item : userBanks) {
            if (item.getStatus().equals(UsrBankCardBinEnum.SUCCESS.getType()) || item.getStatus().equals(UsrBankCardBinEnum.FAILED.getType())) {
                kaBinCheckService.kaBinCheckPost(item, "银行返回状态:成功");
            }
        }
    }

    /**
     * 卡bin校验：有两个入口，一个是下单顺序走下去，一个是个人中心的时候
     * <p>
     * 入参：useruuid，bankcode银行卡简称，banknumber银行卡号，username用户姓名
     * 通过bankcode银行卡简称和banknumber银行卡号去调用卡bin接口，拿到三种状态之一和姓名，如果姓名校验通过，就成功。
     *
     * @param userBankRequest
     * @param isUpdate        是否修改订单
     */
    public void cheakUserBankIsExit(UsrBankRequest userBankRequest, boolean isUpdate) throws ServiceException, ServiceExceptionSpec {


        UsrBank userBankScan = new UsrBank();
        userBankScan.setUserUuid(userBankRequest.getUserUuid());
        userBankScan.setBankCode(userBankRequest.getBankCode());
        userBankScan.setBankNumberNo(userBankRequest.getBankNumberNo().replace(" ", ""));
        List<UsrBank> bankList = usrBankDao.scan(userBankScan);
        if (!CollectionUtils.isEmpty(bankList)) {

            // 如果数据库有 但是被disable 怎么办

            if (isUpdate) {
                if (bankList.get(0).getDisabled() == 1) {
                    log.error("申请使用的银行卡 已经被disable = 1 ，无效的卡片");
                    throw new ServiceException(ExceptionEnum.USER_KABIN_RESPONSE_FAILED);
                }

                if (userBankRequest.getThirdType().equals(0)) {
                    // 改变订单表：状态变为待机审 2
                    usrBaseInfoService.updateOrderState(userBankRequest.getOrderNo(), userBankRequest.getUserUuid(), OrdStateEnum.MACHINE_CHECKING.getCode(), bankList.get(0));
                    // 改变订单表：步骤变为银行卡信息 7
                    usrBaseInfoService.updateOrderStep(userBankRequest.getOrderNo(), userBankRequest.getUserUuid(), OrdStepTypeEnum.BANK_INFO.getType());
                }
            } else {

                if (bankList.get(0).getStatus() == UsrBankCardBinEnum.SUCCESS.getType() || bankList.get(0).getStatus() == UsrBankCardBinEnum.PENDING.getType()) {
                    log.error("该卡已经存在,请更换其他银行卡!");
                    throw new ServiceException(ExceptionEnum.SYSTEM_USER_BANK_IS_EXIST);

                } else if (bankList.get(0).getStatus() == UsrBankCardBinEnum.FAILED.getType()) {
                    log.error("usrBank里已经有记录为 FAILED，绑卡失败!");
                    throw new ServiceException(ExceptionEnum.USER_KABIN_RESPONSE_FAILED);

                }
            }
        } else { // 如果数据库没有

            cheakBankCardBin(userBankRequest, isUpdate);
        }

    }


//    /**
//     *    反馈订单状态到cashcash
//     * */
//    public void feedBackOrdStatus(String orderNo){
//
//      if (!StringUtils.isEmpty(orderNo)){
//
//          OrdOrder order = new OrdOrder();
//          order.setUuid(orderNo);
//
//
//      }
//    }

    /**
     * 发送http请求实体类
     *
     * @param userBankRequest
     * @param needUpdate      是否需要更新标识
     * @return
     * @throws ServiceException
     */
    @Transactional
    public void cheakBankCardBin(UsrBankRequest userBankRequest, boolean needUpdate) throws ServiceException, ServiceExceptionSpec {

        JSONObject obj = kaBinCheckService.sendCardBinHttpPost(userBankRequest);

        // 落库用：根据银行code查到银行uuid
        SysBankBasicInfo bankInfo = sysBankDao.getBankInfoByBankCode(userBankRequest.getBankCode());
        String uuid = "";
        if (null != bankInfo) {
            uuid = bankInfo.getUuid();
        }
        // 找到这个用户 bankorder 最大的银行卡 再加1，落库绑卡顺序
        Integer maxBankOrder = usrBankDao.getMaxBankorder(userBankRequest.getUserUuid());//
        if (maxBankOrder == null) {
            maxBankOrder = 0;
        }

        // 卡bin认证状态
        Integer logType = UsrBankCardBinEnum.valueOf(obj.get("bankCardVerifyStatus").toString()).getType();

        if (logType == UsrBankCardBinEnum.FAILED.getType()) {// 卡bin接口返回失败（失败的时候不落库，成功和padding的时候落库）
            log.error("卡bin返回为FAILED，绑卡失败!");
            throw new ServiceException(ExceptionEnum.USER_KABIN_RESPONSE_FAILED);

        } else if (logType == UsrBankCardBinEnum.SUCCESS.getType()) {// 卡bin接口返回成功：匹配姓名
            // 姓名比较
//            String bankHolderName = dealWithName(dealWithNameStr(obj.get("bankHolderName").toString()));
//            String bankCardName = dealWithName(userBankRequest.getBankCardName());
            Map<String, String> nameMap = judgeNamesFun(obj.get("bankHolderName").toString(), userBankRequest.getBankCardName());// 比较姓名

            log.info("用户的姓名为：" + userBankRequest.getBankCardName() + "------------kabin返回的姓名为" + obj.get("bankHolderName").toString());
            if (!nameMap.get("bankHolderName").equals(nameMap.get("bankCardName"))) {// 不匹配
                log.error("卡bin返回的姓名和传过来的姓名不匹配!");
                throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_NAME);
            } else {// 匹配上就成功

                // 拿到该用户所有银行卡列表置 isRecent=0，
                setIsRecentToZeroFun(userBankRequest);

                maxBankOrder++;
                // 成功的时候：落库
                UsrBank userBankObj = addUserBank(userBankRequest, uuid, obj.get("bankHolderName").toString().replaceAll("\\pP", "").replaceAll("`", ""), logType, maxBankOrder);
                if (needUpdate) {
                    // 需要改变订单表的标识
                    udpateOrderInfo(userBankRequest, userBankObj);
                }

            }
        } else if (logType == UsrBankCardBinEnum.PENDING.getType()) {// padding，响应成功。算绑卡成功

            // 拿到该用户所有银行卡列表置 isRecent=0，
            setIsRecentToZeroFun(userBankRequest);

            maxBankOrder++;
            // 成功的时候：落库
            UsrBank userBankObj = addUserBank(userBankRequest, uuid, userBankRequest.getBankCardName(), logType, maxBankOrder);
            if (needUpdate) {
                // 需要改变订单表的标识
                udpateOrderInfo(userBankRequest, userBankObj);
            }

        } else {
            log.error("绑卡异常!");
            throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_FAILED);
        }
    }


    private void udpateOrderInfo(UsrBankRequest userBankRequest, UsrBank userBankObj) throws ServiceException, ServiceExceptionSpec {
        if (userBankRequest.getThirdType().equals(0)) {
            // 改变订单表：状态变为待机审 2
            usrBaseInfoService.updateOrderState(userBankRequest.getOrderNo(), userBankRequest.getUserUuid(), OrdStateEnum.MACHINE_CHECKING.getCode(), userBankObj);
            // 改变订单表：步骤变为银行卡信息 7
            usrBaseInfoService.updateOrderStep(userBankRequest.getOrderNo(), userBankRequest.getUserUuid(), OrdStepTypeEnum.BANK_INFO.getType());
        } else {
            //cashcash 的设置order表对应的usrBank表的主键[cashcash的订单状态在订单确认时修改]
            ordService.updateOrderBankId(userBankRequest.getOrderNo(), userBankObj.getUuid());
        }
    }


    /**
     * 根据运营同事整理的银行卡返回的姓名做响应的路由判断
     * 规则：省略、多BPK、多IBU、多SDR、多SDRI
     *
     * @param bankHolderName 银行卡返回的姓名
     * @param bankCardName   填的姓名
     * @return
     */
    public Map<String, String> judgeNamesFun(String bankHolderName, String bankCardName) {
        bankHolderName = dealWithNameStr(bankHolderName).trim();
        bankCardName = dealWithNameStr(bankCardName).trim();


        Map<String, String> map = new HashedMap();
        String[] bankHolderNameArr = bankHolderName.toUpperCase().split("\\s");// 银行卡返回的姓名
        List<String> bankHolderNameList = new ArrayList<>();
        Collections.addAll(bankHolderNameList, bankHolderNameArr);
        String[] bankCardNameArr = bankCardName.toUpperCase().split("\\s");// 填的姓名
        List<String> bankCardNameList = new ArrayList<>();
        Collections.addAll(bankCardNameList, bankCardNameArr);
        if (bankHolderNameList.size() == bankCardNameList.size() && bankCardNameList.size() >= 2) {
            Integer count = 0;
            for (Integer i = 0; i <= bankHolderNameList.size() - 2; i++) {
                if (bankHolderNameList.get(i).equals(bankCardNameList.get(i))) {
                    count++;
                }
            }
            String bankHolderNameLast = bankHolderNameList.get(bankHolderNameList.size() - 1);// 银行卡返回的姓名(省略)
            String bankCardNameLast = bankCardNameList.get(bankCardNameList.size() - 1);// 填的姓名
            if (count == bankHolderNameList.size() - 1 && bankCardNameLast.contains(bankHolderNameLast)) {// 如果最后一位是包含关系，那么两个姓名一样
                map.put("bankHolderName", dealWithName(bankCardName));
                map.put("bankCardName", dealWithName(bankCardName));
            } else {
                map.put("bankHolderName", dealWithName(bankHolderName));
                map.put("bankCardName", dealWithName(bankCardName));
            }
        } else {
            if (bankCardName.startsWith(bankHolderName)) {
                map.put("bankHolderName", dealWithName(bankCardName));
                map.put("bankCardName", dealWithName(bankCardName));
            } else {
                map.put("bankHolderName", dealWithName(bankHolderName));
                map.put("bankCardName", dealWithName(bankCardName));
            }
        }
        return map;
    }


    public static void main(String[] args) {
//        String bankCardName = "AGRIZTA DHEKA ATMAWATI";// 填的姓名
        String bankHolderName = "ADE FITRI A`SYAROH ` SRI RAHAYU ENTE DIANA NUR`AENI MOH.RUSDI RIFA`I SUHANDA`SPDI`MPD AHMAD SYAFI`I";//银行卡返回的姓名

        log.info(bankHolderName.replaceAll("\\pP", "").replaceAll("`", ""));
//        System.err.println(judgeNamesFun(bankHolderName,bankCardName));
//        System.err.println("aas".startsWith("a"));


    }

    // 处理kabin 返回的账户姓名 带有 BPK IBU SDR SDRI 的情况
    public String dealWithNameStr(String nameStr) {
        if (!StringUtils.isEmpty(nameStr)) {
            nameStr = nameStr.toUpperCase();
            if (nameStr.startsWith("SDRI")) {
                nameStr = nameStr.replaceAll("SDRI", "");
            }
            if (nameStr.startsWith("SDR")) {
                nameStr = nameStr.replaceAll("SDR", "");
            }
            if (nameStr.startsWith("IBU")) {
                nameStr = nameStr.replaceAll("IBU", "");
            }
            if (nameStr.startsWith("BPK")) {
                nameStr = nameStr.replaceAll("BPK", "");
            }
            if (nameStr.startsWith("BIU")) {
                nameStr = nameStr.replaceAll("BIU", "");
            }
            if (nameStr.startsWith("BAPAK")) {
                nameStr = nameStr.replaceAll("BAPAK", "");
            }
            return nameStr.replaceAll("-", "").toUpperCase();
        }
        return "";
    }

    private UsrBank addUserBank(UsrBankRequest userBankRequest, String uuid, String bankHolderName, Integer logType, Integer bankorder) {
        UsrBank userBank = new UsrBank();
        String userBankUuid = UUIDGenerateUtil.uuid();
        userBank.setUserUuid(userBankRequest.getUserUuid());
        userBank.setUuid(userBankUuid);
        userBank.setBankId(uuid);// 银行信息表 的 uuid
        userBank.setBankCode(userBankRequest.getBankCode());
        userBank.setBankNumberNo(userBankRequest.getBankNumberNo().replace(" ", ""));
        userBank.setBankCardName(bankHolderName);
        userBank.setStatus(logType);
        userBank.setDisabled(0);
        userBank.setBankorder(bankorder);
        userBank.setIsRecent(1);
        // 第三方类型（默认是0=wangwang，1=cashcash）
        if (userBankRequest.getThirdType() != null) {
            userBank.setThirdType(userBankRequest.getThirdType());
        } else {
            userBank.setThirdType(0);
        }
        usrBankDao.insert(userBank);
        return userBank;
    }

    /**
     * 获取用户银行卡列表
     *
     * @param cash2UserBankRequest
     */
    public Cash2UsrBankResponse getCash2UserBankList(Cash2UserBankRequest cash2UserBankRequest) throws ServiceException {
        // cash2订单号是否为空
        ExternalOrderRelation externalOrderRelation = externalChannelDataService.getExternalOrderRelationByExternalOrderNo(cash2UserBankRequest.getOrderNo());
        //  查询是否实名 ，用户表查询真实姓名是否为空
        UsrUser user = usrDao.getUserInfoById(externalOrderRelation.getUserUuid());
        // 用户不存在
        if (user == null) {
            log.error("用户不存在!");
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
        // 用户未实名认证
        if (StringUtils.isEmpty(user.getRealName())) {
            log.error("用户未实名认证!");
            throw new ServiceException(ExceptionEnum.USER_NO_VIFIFY);
        }
        // 找到状态为 1=pedding， 2=success的银行卡信息，
        List<UsrBank> userBanks = usrBankDao.getSuccess(externalOrderRelation.getUserUuid());
        Cash2UsrBankResponse cash2UsrBankResponse = new Cash2UsrBankResponse();
        cash2UsrBankResponse.setOrderNo(cash2UserBankRequest.getOrderNo());
        List<Cash2UsrBankResponse.Cash2BankResponse> bankCardList = new ArrayList<>();
        for (UsrBank userBank : userBanks) {
            Cash2UsrBankResponse.Cash2BankResponse cash2BankResponse = new Cash2UsrBankResponse.Cash2BankResponse();
            cash2BankResponse.setBankCard(userBank.getBankNumberNo());
            cash2BankResponse.setOpenBank(userBank.getBankCode());
            bankCardList.add(cash2BankResponse);
        }
        cash2UsrBankResponse.setBankCardList(bankCardList);
        return cash2UsrBankResponse;
    }


    public void setIsRecentToZeroFun(UsrBankRequest userBankRequest) {
        List<UsrBank> userBankList = usrBankDao.getUserBankList(userBankRequest.getUserUuid());
        // 遍历拿到 isRecent = 1 的时候，置位0
        for (UsrBank item : userBankList) {
            if (item.getIsRecent() == 1) {
                item.setIsRecent(0);
                usrBankDao.update(item);
            }
        }
    }

    // 将除了bankCardNo 卡号之外的其他卡的recent置为0
    public void setIsRecentToZeroWithOtherBank(UsrBankRequest userBankRequest, String bankCardNo) {
        List<UsrBank> userBankList = usrBankDao.getUserBankListWithout(userBankRequest.getUserUuid(), bankCardNo);
        // 遍历拿到 isRecent = 1 的时候，置位0
        for (UsrBank item : userBankList) {
            if (item.getIsRecent() == 1) {
                item.setIsRecent(0);
                usrBankDao.update(item);
            }
        }
    }


    /**
     * 预打款 和 打款 因为银行卡问题 失败，用户能够更改银行卡
     */
    public void changeOrderBankCard(UsrBankRequest request) throws ServiceException {

        try {
            OrdOrder orderOrder = new OrdOrder();
            orderOrder.setDisabled(0);
            orderOrder.setUuid(request.getOrderNo());
            List<OrdOrder> orderList = this.ordDao.scan(orderOrder);
            if (CollectionUtils.isEmpty(orderList)) {
                throw new ServiceException(ExceptionEnum.ORDER_NOT_FOUND);
            }
            OrdOrder order = orderList.get(0);

            if (order.getStatus() != OrdStateEnum.LOANING.getCode()
                    && order.getStatus() != OrdStateEnum.LOAN_FAILD.getCode()) {

                throw new ServiceException(ExceptionEnum.ORDER_STATES_ERROR);
            }

            if (order.getStatus() == OrdStateEnum.LOANING_DEALING.getCode()) {
                throw new ServiceException(ExceptionEnum.ORDER_STATES_ERROR);
            }

            UsrUser user = new UsrUser();
            user.setUuid(order.getUserUuid());
            user.setDisabled(0);
            user.setIsMobileValidated(1);

            //to submit, user mobile need to validate
            List<UsrUser> users = this.usrDao.scan(user);
            if(CollectionUtils.isEmpty(users)){
                throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
            }

            // 查询该订单绑定的银行卡
            UsrBank orderBank = new UsrBank();
            orderBank.setUuid(order.getUserBankUuid());
            List<UsrBank> oldBankList = usrBankDao.scan(orderBank);
            UsrBank oldBank = new UsrBank();
            if (!CollectionUtils.isEmpty(oldBankList)) {
                oldBank = oldBankList.get(0);
            }

            // 查询是否和之前的卡相同
            UsrBank userBankScan = new UsrBank();
            userBankScan.setUserUuid(request.getUserUuid());
            userBankScan.setBankCode(request.getBankCode());
            userBankScan.setBankNumberNo(request.getBankNumberNo());
            userBankScan.setDisabled(0);
            List<UsrBank> bankList = usrBankDao.scan(userBankScan);
            if (!CollectionUtils.isEmpty(bankList)) {
                if (oldBank.getUuid().equals(bankList.get(0).getUuid())) {
                    // 换绑卡和现在的卡是同一张
                    log.error("换绑的卡和现在订单绑定的卡是同一张" + request.getClient_type());
                    throw new ServiceException(ExceptionEnum.SYSTEM_USER_BANK_IS_EXIST);
                }

                if (bankList.get(0).getStatus() == UsrBankCardBinEnum.SUCCESS.getType() || bankList.get(0).getStatus() == UsrBankCardBinEnum.PENDING.getType()) {
                    // 换绑卡使用的卡号已经存在  并且是验证成功或者是pending中
                    // 直接换绑卡

                    // 更改使用的银行卡的recent  把其他的卡的recent置为0
                    setIsRecentToZeroWithOtherBank(request, bankList.get(0).getBankNumberNo());

                    UsrBank userBank = bankList.get(0);
                    userBank.setIsRecent(1);
                    usrBankDao.update(userBank);

                    // disable掉之前的卡
                    oldBank.setDisabled(1);
                    oldBank.setRemark("绑卡失败，卡信息有误，换绑卡");
                    usrBankDao.update(oldBank);

                    changeOrderBankInfo(order, bankList.get(0));

                } else if (bankList.get(0).getStatus() == UsrBankCardBinEnum.FAILED.getType()) {
                    log.error("usrBank里已经有记录为 FAILED，绑卡失败!");
                    throw new ServiceException(ExceptionEnum.USER_KABIN_RESPONSE_FAILED);

                }
            } else {
                // 如果没有
                CardVerifyResult result = checkBankCard(request);
                if (null == result) {
                    log.warn("the card bin check result is null");
                    throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_FAILED);
                }
                Integer logType = UsrBankCardBinEnum.valueOf(result.getBankCardVerifyStatus()).getType();
                if (UsrBankCardBinEnum.SUCCESS.name().equals(result.getBankCardVerifyStatus()) && result.isNameMatch()) {
                    //实名通过且姓名一致
                    updateCardSuccess(order, request, logType, oldBank);
                } else if (UsrBankCardBinEnum.PENDING.name().equals(result.getBankCardVerifyStatus())) {
                    //实名pending
                    updateCardSuccess(order, request, logType, oldBank);
                }
            }

        } catch (ServiceException e1) {
            throw e1;
        } catch (Exception e) {
            log.error("换绑卡异常!", e);
            throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_FAILED);
        }
    }


    /***
     * card bin校验
     * @param request
     * @return
     * @throws ServiceException
     */
    public CardVerifyResult checkBankCard(UsrBankRequest request) throws Exception {

        // 查询是否和之前的卡相同
        UsrBank userBankScan = new UsrBank();
        userBankScan.setUserUuid(request.getUserUuid());
        userBankScan.setBankCode(request.getBankCode());
        userBankScan.setBankNumberNo(request.getBankNumberNo());
        userBankScan.setDisabled(0);
        List<UsrBank> bankList = usrBankDao.scan(userBankScan);
        if (!CollectionUtils.isEmpty(bankList)) {
            if (bankList.get(0).getStatus() == UsrBankCardBinEnum.SUCCESS.getType() || bankList.get(0).getStatus() == UsrBankCardBinEnum.PENDING.getType()) {
                log.error("该卡已经存在,请更换其他银行卡!");
                throw new ServiceException(ExceptionEnum.SYSTEM_USER_BANK_IS_EXIST);

            } else if (bankList.get(0).getStatus() == UsrBankCardBinEnum.FAILED.getType()) {
                log.error("usrBank里已经有记录为 FAILED，绑卡失败!");
                throw new ServiceException(ExceptionEnum.USER_KABIN_RESPONSE_FAILED);

            }
        } else { // 如果数据库没有

            JSONObject obj = kaBinCheckService.sendCardBinHttpPost(request);

            // 卡bin认证状态
            Integer logType = UsrBankCardBinEnum.valueOf(obj.get("bankCardVerifyStatus").toString()).getType();
            String returnBankHolderName = obj.get("bankHolderName") == null ? null : obj.get("bankHolderName")
                    .toString();

            if (logType == UsrBankCardBinEnum.FAILED.getType()) {// 卡bin接口返回失败（失败的时候不落库，成功和padding的时候落库）
                log.error("卡bin返回为FAILED，绑卡失败!");
                throw new ServiceException(ExceptionEnum.USER_KABIN_RESPONSE_FAILED);

            } else if (logType == UsrBankCardBinEnum.SUCCESS.getType()) {// 卡bin接口返回成功：匹配姓名
                // 姓名比较
                String bankHolderName = dealWithName(dealWithNameStr(returnBankHolderName));
                String bankCardName = dealWithName(request.getBankCardName());

                if (!bankHolderName.equals(bankCardName)) {// 不匹配
                    log.error("卡bin返回的姓名和传过来的姓名不匹配!");
                    throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_NAME);
                } else {
                    // 匹配成功
                    // 绑卡 加 更换订单状态
                    return new CardVerifyResult(obj.get("bankCardVerifyStatus").toString(), returnBankHolderName, true);
                }
            } else if (logType == UsrBankCardBinEnum.PENDING.getType()) {// padding，响应成功。算绑卡成功
                // 绑卡 加 更换订单状态
                return new CardVerifyResult(obj.get("bankCardVerifyStatus").toString(), returnBankHolderName, false);
            } else {
                log.error("换绑卡异常!");
                throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_FAILED);
            }
        }

        return null;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class CardVerifyResult {
        private String bankCardVerifyStatus;
        private String bankHolderName;
        private boolean nameMatch;
    }


    /**
     * 只筛选出字母
     * 去掉逗号之后的学历
     *
     * @param realName
     * @return
     */
    public String dealWithName(String realName) {
        realName = realName.replace(".", " ");
        String reg = "[^A-Za-z]";// 只筛选出空格和字母
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(realName);
        String result = m.replaceAll("").trim();
        return result.toUpperCase();
    }


    /**
     * 换绑卡成功，修改订单状态为待打款
     */
    @Transactional
    public void updateCardSuccess(OrdOrder order, UsrBankRequest request, Integer logType, UsrBank oldBank) {

        UsrBank userBank = updateUsrBankCardAfterRebind(order.getUserUuid(), logType, request, oldBank);

        changeOrderBankInfo(order, userBank);
    }

    public void changeOrderBankInfo(OrdOrder order, UsrBank userBank) {
        // 更改订单状态
        OrdOrder entity = new OrdOrder();
        entity.setUuid(order.getUuid());
        entity.setStatus(OrdStateEnum.LOANING.getCode());
//        entity.setApplyTime(new Date());
        entity.setUserBankUuid(userBank.getUuid());
        entity.setRemark("");
        entity.setUpdateTime(new Date());
        this.ordService.updateOrder(entity);

        if (order.getStatus() != OrdStateEnum.LOANING.getCode() && order.getStatus() != OrdStateEnum.LOANING_DEALING.getCode()) {

//        TODO: 为了相同订单号能够继续打款 删除之前在redis里面的order
            this.redisClient.del(RedisContants.ORDER_LOAN_LOCK_NEW + order.getUuid());
//        TODO: 同时 使mk那里的订单号无效化
            inactiveOrder(order);

            order.setStatus(OrdStateEnum.LOANING.getCode());
            order.setApplyTime(new Date());
            order.setUpdateTime(new Date());
            order.setUserBankUuid(userBank.getUuid());
            this.ordService.addOrderHistory(order);
        }

    }

    /***
     *
     * @param userUuid
     * @param status
     * @param bankRequest
     * @param oldBank
     * @return 新的银行卡实体
     */
    private UsrBank updateUsrBankCardAfterRebind(String userUuid, Integer status, UsrBankRequest bankRequest, UsrBank oldBank) {
        // 落库用：根据银行code查到银行uuid
        SysBankBasicInfo bankInfo = sysBankDao.getBankInfoByBankCode(bankRequest.getBankCode());
        String uuid = "";
        if (null != bankInfo) {
            uuid = bankInfo.getUuid();
        }
        setIsRecentToZeroFun(bankRequest);

        // 更改银行卡
        UsrBank userBank = new UsrBank();
        userBank.setUserUuid(userUuid);
        userBank.setUuid(UUIDGenerateUtil.uuid());
        userBank.setBankId(uuid);// 银行信息表 的 uuid
        userBank.setBankCode(bankRequest.getBankCode());
        userBank.setBankNumberNo(bankRequest.getBankNumberNo());
        userBank.setBankCardName(bankRequest.getBankCardName());
        userBank.setStatus(status);
        userBank.setDisabled(0);
        userBank.setBankorder(oldBank.getBankorder());
        userBank.setIsRecent(1);
        userBank.setThirdType(bankRequest.getThirdType());
        usrBankDao.insert(userBank);
        // disable掉之前的卡
        oldBank.setDisabled(1);
        oldBank.setRemark("绑卡失败，卡信息有误，换绑卡");
        usrBankDao.update(oldBank);
        return userBank;
    }

    /**
     * 是mk那里的订单无效化（为了重复订单号打款）
     */
    public void inactiveOrder(OrdOrder order) {

        try {
            Request request = new Request.Builder()
                    .put(new FormBody.Builder().build())
                    .url(inactiveOrderUrl + order.getUuid())
                    .header("X-AUTH-TOKEN", PAY_TOKEN)
                    .build();

            // 请求数据落库，SysThirdLogs
            this.sysThirdLogsService.addSysThirdLogs(order.getUuid(), order.getUserUuid(), SysThirdLogsEnum.INACTVE_ORDER.getCode(), 0, order.getUuid(), null);

            Response response = httpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseStr = response.body().string();
                // 订单无效化响应
                log.info("订单无效化 请求后返回:{}", JsonUtils.serialize(responseStr));
                // 响应数据落库，sysThirdLogs
                sysThirdLogsService.addSysThirdLogs(order.getUuid(), order.getUserUuid(), SysThirdLogsEnum.INACTVE_ORDER.getCode(), 0, null, responseStr);
            }
        } catch (Exception e) {
            log.info("订单无效化失败,订单号:" + order.getUuid());
            e.printStackTrace();
        }
    }

    public UsrBank getBankCardInfo(String bankUuid) {
        UsrBank searchBank = new UsrBank();
        searchBank.setDisabled(0);
        searchBank.setUuid(bankUuid);
        List<UsrBank> bankList = usrBankDao.scan(searchBank);
        if (CollectionUtils.isEmpty(bankList)) {
            return null;
        }
        return bankList.get(0);
    }

    public void updateBankCardInfo(UsrBank usrBank) {
        usrBankDao.update(usrBank);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean rebindBankCardForFailed(UsrBankRequest bankRequest) throws Exception {
        //不可修改银行卡的状态
        List<Integer> canNotRebindStatus = Arrays.asList(OrdStateEnum.LOAN_FAILD.getCode(), OrdStateEnum.LOANING_DEALING.getCode(), OrdStateEnum
                        .RESOLVING_NOT_OVERDUE
                        .getCode(),
                OrdStateEnum.RESOLVING_OVERDUE.getCode(), OrdStateEnum.RESOLVED_NOT_OVERDUE.getCode(), OrdStateEnum.RESOLVED_OVERDUE.getCode());
        OrdOrder dbOrder = ordService.getOrderByOrderNo(bankRequest.getOrderNo());
        if (canNotRebindStatus.contains(dbOrder.getStatus())) {
            //状态不对
            log.warn("invalid order status");
            throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_FAILED);
        }

        //进行银行卡校验
        UsrBank orderBank = this.getBankCardInfo(dbOrder.getUserBankUuid());
        if (orderBank != null && orderBank.getStatus() == UsrBankCardBinEnum.FAILED.getType()) {
            //绑卡失败
            //card bin 检查
            CardVerifyResult result = checkBankCard(bankRequest);
            if (null == result) {
                log.warn("the card bin check result is null");
                throw new ServiceException(ExceptionEnum.USER_KABIN_CHECK_FAILED);
            }
            Integer logType = UsrBankCardBinEnum.valueOf(result.getBankCardVerifyStatus()).getType();
            if ((UsrBankCardBinEnum.SUCCESS.name().equals(result.getBankCardVerifyStatus()) && result.nameMatch) || UsrBankCardBinEnum.PENDING.name()
                    .equals(result
                            .getBankCardVerifyStatus())) {
                // success or pending
                //增加usrBank信息
                //disabled老的usrBank
                UsrBank userBank = updateUsrBankCardAfterRebind(dbOrder.getUserUuid(), logType, bankRequest, orderBank);
                //更新订单
                dbOrder.setUserBankUuid(userBank.getUuid());
                ordDao.update(dbOrder);
                return true;
            }

        }
        return false;
    }

    /**
     * 获取用户银行卡列表
     *
     * @param baseRequest
     */
    public List<UsrBankResponse> getUserBankList(BaseRequest baseRequest) throws ServiceException {
        //  查询是否实名 ，用户表查询真实姓名是否为空
        UsrUser user = usrDao.getUserInfoById(baseRequest.getUserUuid());
        // 用户不存在
        if (user == null) {
            log.error("用户不存在!");
            throw new ServiceException(ExceptionEnum.USER_NOT_FOUND);
        }
        // 用户未实名认证
        if (StringUtils.isEmpty(user.getRealName())) {
            log.error("用户未实名认证!");
            throw new ServiceException(ExceptionEnum.USER_NO_VIFIFY);
        }
        // 找到状态为 1=pedding， 2=success的银行卡信息，
        List<UsrBank> userBanks = usrBankDao.getSuccessAndPedding(baseRequest.getUserUuid());
        List<UsrBankResponse> userBankResponses = new ArrayList<>();
        UsrBankResponse userBankResponse = null;
        Boolean hasRecentBank = false;
        for (UsrBank userBank : userBanks) {
            userBankResponse = new UsrBankResponse();
            userBankResponse.setBankCode(userBank.getBankCode());
            userBankResponse.setBankNumberNo(userBank.getBankNumberNo());
            userBankResponse.setBankorder(userBank.getBankorder().toString());
            userBankResponse.setIsRecent(userBank.getIsRecent().toString());

            if (userBank.getIsRecent() == 1) {
                hasRecentBank = true;
            }
//            BeanUtils.copyProperties(userBankResponse,userBank);
            userBankResponses.add(userBankResponse);
        }
        // 如果没有最近使用的银行卡
        if (!hasRecentBank && !CollectionUtils.isEmpty(userBankResponses)) {
            UsrBankResponse response = userBankResponses.get(0);
            response.setIsRecent("1");
        }

        return userBankResponses;
    }


    public Integer countOfSameBankcardNumberWithOthers(String bankcardNumber, String bankCode, String userUuid) {
        return usrBankDao.countOfSameBankcardNumberWithOthers(bankcardNumber, bankCode, userUuid);
    }

    public boolean userInMultiBankcardUser(String userUuid) {
        Integer getCount = usrBankDao.userInMultiBankcardUser(userUuid);
        return getCount != null && getCount >= 1;
    }

    public UsrBank getUserBankById(String userUuid, String userBankId) {
        UsrBank usrBank = usrBankDao.getUserBankInfoById(userUuid, userBankId);
        return usrBank;
    }

    public List<UsrBank> getSuccessBankList(String userUuid){
       List<UsrBank> usrBanks = usrBankDao.getSuccess(userUuid);
       if(CollectionUtils.isEmpty(usrBanks)){
           return new ArrayList<>();
       }else{
           return usrBanks;
       }
    }

}
