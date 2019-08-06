package com.yqg.service.third.mobox;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.enums.system.MoboxDataEnum;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.mongo.dao.MoboxDataMongoDal;
import com.yqg.mongo.entity.MoboxDataMongo;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.order.OrdService;
import com.yqg.service.third.mobox.config.MoboxConfig;
import com.yqg.service.third.mobox.config.TongDunConfig;
import com.yqg.service.third.mobox.request.MoboxNotiCallbackRequest;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.dao.MoboxNotiCallbackDao;
import com.yqg.system.entity.MoboxNotiCallback;
import com.yqg.user.dao.UsrCertificationDao;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 同盾相关
 * Created by wanghuaizhou on 2019/2/25.
 * updated by lingbo on 2019/3/25
 */
@Service
@Slf4j
public class MoboxService {


    @Autowired
    private MoboxConfig moboxConfig;

    @Autowired
    private TongDunConfig tongDunConfig;

    // 查询任务结果url
    @Value("${mobox.checkTaskResultUrl}")
    private String CHECK_TASK_RESULT_URL;

    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    private MoboxNotiCallbackDao moboxNotiCallbackDao;

    @Autowired
    private OrdService ordService;

    @Autowired
    private UsrService usrService;

    @Autowired
    private MoboxDataMongoDal moboxDataMongoDal;

    @Autowired
    private UsrCertificationDao usrCertificationInfoDao;


    /**
     * 拼接信贷保镖访问路径，加上key和code及appName
     */
    private String jointCheckBeforeCreditUrl() {
        return new StringBuilder().append(tongDunConfig.getCheckBeforeCreditUrl())
                .append("?partner_code=").append(tongDunConfig.getPartnerCode())
                .append("&partner_key=").append(tongDunConfig.getPartnerKey())
                .append("&app_name=").append(tongDunConfig.getAppName()).toString();
    }


    // 获取同盾信贷保镖数据
    public void getTongDunCreditBodyguardsData(OrdOrder order){

        MoboxDataMongo scan = new MoboxDataMongo();
        scan.setType(MoboxDataEnum.BLACK_BOX_DATA.getType());
        scan.setUserUuid(order.getUserUuid());
        scan.setOrderNo(order.getUuid());
//        scan.setDisabled(0);
        List<MoboxDataMongo> moboxDataMongoList = this.moboxDataMongoDal.find(scan);
        log.info("moboxDataMongoList size:"+moboxDataMongoList.size());
        if (!CollectionUtils.isEmpty(moboxDataMongoList)){

            /**
             *  6月13号优化  如果获取过保镖数据 就不重复获取了（有些机审通过订单 外呼未通过  又重新走了机审)
             * */
            MoboxDataMongo scan2 = new MoboxDataMongo();
            scan2.setType(MoboxDataEnum.XDBB_SECD_DATA.getType());
            scan2.setUserUuid(order.getUserUuid());
            scan2.setOrderNo(order.getUuid());
//        scan.setDisabled(0);
            List<MoboxDataMongo> moboxDataMongoList2 = this.moboxDataMongoDal.find(scan2);
            if (CollectionUtils.isEmpty(moboxDataMongoList2)){
                MoboxDataMongo mobox = moboxDataMongoList.get(moboxDataMongoList.size() - 1);
                checkFirstBeforeCreditResult(order.getUserUuid(),order.getUuid(),mobox.getData());
            }
        }
    }


    /**
     * 同盾信贷保镖，第一次调用同盾接口
     */
    public void checkFirstBeforeCreditResult(String userUuid,String orderNo, String blackBox) {
        try {
            UsrUser user = this.usrService.getUserByUuid(userUuid);
            String jointUrl = this.jointCheckBeforeCreditUrl();
            // 解密手机号
            RequestBody requestBody = new FormBody.Builder()
                    .add("full_nm", user.getRealName())
                    .add("id_num", user.getIdCardNo())
                    .add("act_mbl", DESUtils.decrypt(user.getMobileNumberDES()))
                    .add("biz_code", "dqand")
                    .add("black_box", blackBox).build();

            Request request = new Request.Builder()
                    .url(jointUrl)
                    .post(requestBody)
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .build();

            Response response = httpClient.newCall(request).execute();
            log.info("订单号为："+orderNo+"  用户id为："+userUuid);
            if(response.isSuccessful())
            {
                String  responseStr = response.body().string();
                log.info("同盾信贷保镖 第一次请求后返回:{}", JsonUtils.serialize(responseStr));
                JSONObject object = JSONObject.parseObject(responseStr);;
                if (object != null){
                    this.saveToMongoDB(orderNo, user.getUuid(), object, MoboxDataEnum.XDBB_FIR_DATA);

                    // 延时几秒后第二次调用
                    TimeUnit.SECONDS.sleep(4);
                    this.checkSecondBeforeCreditResult(userUuid,orderNo,object);
                }

            }else {
                log.error("同盾信贷保镖 第一次请求失败，返回的response为:{}", response);
            }

        } catch (Exception e) {
            log.error("同盾信贷保镖 第一次请求异常", e);
        }
    }

    /**
     * 第二次调用同盾接口
     */
    private void checkSecondBeforeCreditResult(String userUuid,String orderNo,JSONObject object) {

        // 解析
        String id = object.get("id").toString();
        String invokeType = object.get("nextService").toString();
        try {
            String jointUrl = this.jointCheckBeforeCreditUrl();
            // 解密手机号
            RequestBody requestBody = new FormBody.Builder()
                    .add("id", id)
                    .add("invoke_type", invokeType).build();

            Request request = new Request.Builder()
                    .url(jointUrl)
                    .post(requestBody)
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .build();

            Response response = httpClient.newCall(request).execute();
            log.info("订单号为："+orderNo+"  用户id为："+userUuid);
            if(response.isSuccessful())
            {
                String  responseStr = response.body().string();
                log.info("同盾信贷保镖 第二次请求后返回:{}", JsonUtils.serialize(responseStr));

                JSONObject responseObj =  JSONObject.parseObject(responseStr);
                this.saveToMongoDB(orderNo,userUuid,responseObj, MoboxDataEnum.XDBB_SECD_DATA);

            }else {
                log.error("同盾信贷保镖 第二次请求失败，返回的response为:{}", response);
            }

        } catch (Exception e) {
            log.error("同盾信贷保镖 第二次请求异常", e);
        }
    }

    private void saveToMongoDB(String orderNo, String userUuid, JSONObject object, MoboxDataEnum moboxDataEnum) {

        MoboxDataMongo mongo = new MoboxDataMongo();
        mongo.setCreateTime(new Date());
        mongo.setUpdateTime(new Date());
        mongo.setTaskId(object.get("id").toString());
        mongo.setType(moboxDataEnum.getType());
        mongo.setData(object.get("result_desc").toString());
        mongo.setOrderNo(orderNo);
        mongo.setUserUuid(userUuid);
        if (object.get("nextService") != null){
            mongo.setResultState(object.get("nextService").toString());
        }
        this.moboxDataMongoDal.insert(mongo);
    }


    /**
     *   异步回调通知  该回调是同盾服务端回调给客户服务端
     * */
    public void getNotiCallback (Map<String, String> requestParams) throws Exception{


        try {
            MoboxNotiCallback moboxNotiCallback = new MoboxNotiCallback();
            String orderNo =  requestParams.get("passback_params");
            if (!StringUtils.isEmpty(orderNo)){
                OrdOrder order = ordService.getOrderByOrderNo(orderNo);
                if (order != null){
                    moboxNotiCallback.setOrderNo(order.getUuid());
                    moboxNotiCallback.setUserUuid(order.getUserUuid());
                }
            }

            //  data
            moboxNotiCallback.setNotifyEvent(requestParams.get("notify_event"));
            moboxNotiCallback.setNotifyType(requestParams.get("notify_type"));
            moboxNotiCallback.setNotifyTime(requestParams.get("notify_time"));
            //  notifyData
            MoboxNotiCallbackRequest.NotifyData data = JsonUtils.deserialize(requestParams.get("notify_data"),MoboxNotiCallbackRequest.NotifyData.class);
            moboxNotiCallback.setCode(data.getCode());
            moboxNotiCallback.setMessage(data.getMessage());
            moboxNotiCallback.setTaskId(data.getTaskId());

            //  notifyData-data
            if (!StringUtils.isEmpty(data.getData().getChannelCode())){
                moboxNotiCallback.setChannelCode(data.getData().getChannelCode());
            }
            if (!StringUtils.isEmpty(data.getData().getChannelType())){
                moboxNotiCallback.setChannelType(data.getData().getChannelType());
            }
            if (!StringUtils.isEmpty(data.getData().getRealName())){
                moboxNotiCallback.setRealName(data.getData().getRealName());
            }
            if (!StringUtils.isEmpty(data.getData().getIdentityCode())){
                moboxNotiCallback.setIdentityCode(data.getData().getIdentityCode());
            }
            if (!StringUtils.isEmpty(data.getData().getUserMobile())){
                moboxNotiCallback.setUserMobile(data.getData().getUserMobile());
            }
            if (!StringUtils.isEmpty(data.getData().getCreatedTime())){
                moboxNotiCallback.setCreatedTime(data.getData().getCreatedTime());
            }
            if (!StringUtils.isEmpty(data.getData().getUserName())){
                moboxNotiCallback.setUserName(data.getData().getUserName());
            }
//            moboxNotiCallback.setType("1");
            this.moboxNotiCallbackDao.insert(moboxNotiCallback);
            /**
             *  0     事件: SUCCESS  采集任务成功
             137   事件: CREATE   任务提交成功
             211   事件: FAILURE  创建任务阶段:渠道维护中，任务失败;
             1     事件: FAILURE  用户授权/采集阶段:未知错误，反馈错误给技术⽀支持
             132   事件: FAILURE  用户授权阶段:⽤用户超时等，授权失败
             170   事件: FAILURE  用户授权阶段:加载登录链接中⽤用户取消认证(如加载时⻓长太⻓长)
             171   事件: FAILURE  用户授权阶段:账号密码输⼊入阶段，⽤用户取消认证(如未输⼊入账密)
             172   事件: FAILURE  用户授权阶段:官⽹网认证中⽤用户取消认证(如⽤用户点击完login后，未能顺利利退出)
             173   事件: FAILURE  用户授权阶段:加载登录成功⻚页中⽤用户取消认证(如加载时⻓长太⻓长)
             200~207 事件: FAILURE  ⽤用户授权阶段:⽬目标服务不不可⽤用
             * */
            if (data.getCode().equals("0")){
              // 采集任务成功  去查询任务结果
                String taskId = data.getTaskId();
                checkTaskResult(moboxNotiCallback,taskId);
            }

        }catch (Exception e){
            log.error("接受mobox异步回调异常", e);
        }
    }

    /**
     *   查询任务结果
     * */
    public void checkTaskResult (MoboxNotiCallback moboxNotiCallback,String taskId) throws Exception{

        try {
            RequestBody requestBody = new FormBody.Builder()
                    .add("partner_code",moboxConfig.getPartnerCode())  // 合作⽅方标识
                    .add("partner_key",moboxConfig.getPartnerKey())  // 合作⽅密钥
                    .add("task_id",taskId)  // 任务编码
                    .build();

            Request request = new Request.Builder()
                    .url(CHECK_TASK_RESULT_URL)
                    .post(requestBody)
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .build();

            Response response = httpClient.newCall(request).execute();
            if(response.isSuccessful())
            {
                String  responseStr = response.body().string();
                log.info("mobox查询任务结果 请求后返回:{}", JsonUtils.serialize(responseStr));
                // 保存数据到数据库（taskData是否要保存在mongo？)


                JSONObject res = JSONObject.parseObject(responseStr);
                JSONObject data = (JSONObject)res.get("data");

                // 插入mysql

                // 插入mongo
                MoboxDataMongo moboxDataMongo = new MoboxDataMongo();
                moboxDataMongo.setCreateTime(new Date());
                moboxDataMongo.setUpdateTime(new Date());
                moboxDataMongo.setOrderNo(moboxNotiCallback.getOrderNo());
                moboxDataMongo.setUserUuid(moboxNotiCallback.getUserUuid());
                if(moboxNotiCallback.getChannelType().equals("PR")&&moboxNotiCallback.getChannelCode().equals("105001")){
                    // NPWP
                    moboxDataMongo.setType(MoboxDataEnum.NPWP_DATA.getType());
                }else if(moboxNotiCallback.getChannelType().equals("PR")&&moboxNotiCallback.getChannelCode().equals("105002")){
                    // BPJS
                    moboxDataMongo.setType(MoboxDataEnum.BPJS_DATA.getType());
                }else if(moboxNotiCallback.getChannelType().equals("YYS")){
                    // 运营商
                    moboxDataMongo.setType(MoboxDataEnum.OPERATOR_DATA.getType());
                }else if(moboxNotiCallback.getChannelType().equals("SOCIAL")&&moboxNotiCallback.getChannelCode().equals("903004")){
                    // Linkedin
                    moboxDataMongo.setType(MoboxDataEnum.LINKEDIN_DATA.getType());
                }
                moboxDataMongo.setTaskId(taskId);
                if (data != null){
                    moboxDataMongo.setData(data.get("task_data").toString());
                }
                this.moboxDataMongoDal.insert(moboxDataMongo);
            }else {
                log.error("mobox查询任务结果 请求失败，返回的response为:{}", response);
            }
            log.info("订单号为："+moboxNotiCallback.getOrderNo()+"  用户id为："+moboxNotiCallback.getUserUuid());
        }catch (Exception e){
            log.error("mobox查询任务结果异常", e);
        }

    }

}
