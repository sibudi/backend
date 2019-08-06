package com.yqg.manage.service.collection;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.redis.RedisClient;
import com.yqg.manage.dal.collection.CollectionOrderDetailDao;
import com.yqg.manage.dal.collection.ManCollectionRemarkDao;
import com.yqg.manage.dal.collection.ManUsrEvaluateScoreDao;
import com.yqg.manage.dal.user.UserUserDao;
import com.yqg.manage.entity.collection.ManCollectionRemark;
import com.yqg.manage.entity.collection.ManUsrEvaluateScore;
import com.yqg.manage.service.collection.request.ManCollectionRemarkRequest;
import com.yqg.manage.service.collection.response.ManCollectionRemarkResponse;
import com.yqg.manage.service.order.request.ManAlertMessageRequest;
import com.yqg.manage.service.system.ManAlertMessageService;
import com.yqg.manage.service.user.UserUserService;
import com.yqg.manage.service.user.request.ManUserUserRequest;
import com.yqg.service.system.service.SysDicService;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Author: tonggen
 * Date: 2018/8/27
 * time: 下午4:39
 */
@Component
@Slf4j
public class CollectionRemarkService {

    @Autowired
    private ManAlertMessageService manAlertMessageService;

    @Autowired
    private UserUserService userUserService;

    @Autowired
    private CollectionOrderDetailDao collectionOrderDetailDao;

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ManCollectionRemarkDao manCollectionRemarkDao;

    @Autowired
    private UserUserDao userUserDao;

    @Autowired
    private SysDicService sysDicService;

    @Autowired
    private ManUsrEvaluateScoreDao manUsrEvaluateScoreDao;

    @Transactional(rollbackFor = Exception.class)
    public void insertCollectionRemark(ManCollectionRemarkRequest request) throws Exception {

        if (request.getCreateUser() == null ||
                request.getUpdateUser() == null ||
                StringUtils.isEmpty(request.getOrderNo()) ||
                StringUtils.isEmpty(request.getUserUuid())) {
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_ITEM_ERROR);
        }
        //判断如果不是催收打标签 contactMode不能为空
        if (request.getOrderTag() == null || request.getOrderTag().equals(0)) {
            if (request.getContactMode() == null || request.getContactResult() == null) {
                throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_ITEM_ERROR);
            }
        }

        if( !this.redisClient.lockRepeat("insertCollectionRemark：" + request.getOrderNo())){
            throw new ServiceExceptionSpec(ExceptionEnum.ORDER_COMMIT_REPEAT);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //如果提醒时间不为空，添加到提醒
        if (StringUtils.isNotBlank(request.getAlertTime())) {
            ManAlertMessageRequest manAlertMessageRequest =
                    new ManAlertMessageRequest();
            //获得手机号码和用户名称
            ManUserUserRequest manUserUserRequest = new ManUserUserRequest();
            manUserUserRequest.setUserUuid(request.getUserUuid());
            UsrUser mobile = userUserService.userMobileByUuid(manUserUserRequest);
            manAlertMessageRequest.setMobileNumber(mobile.getMobileNumberDES());
            manAlertMessageRequest.setRealName(mobile.getRealName());
            try {
                if (StringUtils.isNotBlank(request.getAlertTime())) {
                    manAlertMessageRequest.setAlertTime(simpleDateFormat.parse(request.getAlertTime()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            manAlertMessageRequest.setOperateId(request.getCreateUser());
            manAlertMessageRequest.setUserUuid(request.getUserUuid());
            manAlertMessageRequest.setOrderNo(request.getOrderNo());
            manAlertMessageRequest.setOrderTag(request.getOrderTag());
            manAlertMessageRequest.setLangue(request.getLangue());
            manAlertMessageService.addCollectionAlertMessage(manAlertMessageRequest);
        }
        //将订单标签和承诺时间更新到collectionOrderDetail表
        if ((request.getOrderTag() != null && request.getOrderTag() != 0)
                || StringUtils.isNotBlank(request.getPromiseRepaymentTime())) {

            Date tempTime = null;
            try {
                if (StringUtils.isNotBlank(request.getPromiseRepaymentTime())) {
//                        coll.setPromiseRepaymentTime(simpleDateFormat.parse(request.getPromiseRepaymentTime()));
                    tempTime = simpleDateFormat.parse(request.getPromiseRepaymentTime());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            collectionOrderDetailDao.updateOrderTagAndPromiseTime(request.getOrderNo(), request.getOrderTag(), tempTime);
        }
        //保存催收日志数据
        this.insertManCollRemark(request);

        //保存发薪日
        if (request.getPayDay() != null &&
                request.getPayDay() != 0) {
            UsrUser usrUser = new UsrUser();
            usrUser.setUuid(request.getUserUuid());
            usrUser.setDisabled(0);
            usrUser.setPayDay(request.getPayDay());
            userUserDao.update(usrUser);
        }

        this.redisClient.unlockRepeat("insertCollectionRemark：" + request.getOrderNo());
    }

    public void insertManCollRemark(ManCollectionRemarkRequest request) throws Exception{

        ManCollectionRemark manCollectionRemark = new ManCollectionRemark();
        BeanUtils.copyProperties(request, manCollectionRemark);

        //记录催收对用户的评分
        if (request.getRepayDesire() != null && !request.getRepayDesire().equals(0)
                && request.getType() != null && (request.getType().equals(2) || request.getType().equals(3))) {
//            //从字典表中获得分数的配置
//            DictionaryRequest dictionaryRequest = new DictionaryRequest();
//            dictionaryRequest.setDicCode("usrEvaluateScoreConfig");
//            List<SysDicItemModel> sysDicItemModelList = sysDicService.dicItemListByDicCode(dictionaryRequest);
//            if (!CollectionUtils.isEmpty(sysDicItemModelList)) {
//                sysDicItemModelList.stream().forEach(elem -> {
//
//                });
//            }
            ManUsrEvaluateScore score = new ManUsrEvaluateScore();
            score.setDisabled(0);
            score.setType(request.getType());
            score.setCreateUser(request.getCreateUser());
            score.setUserUuid(request.getUserUuid());
            score.setOrderNo(request.getOrderNo());
            score.setRepayDesire(request.getRepayDesire());
            score.setRepayBility(request.getRepayBility());
            score.setUserDiathesis(request.getUserDiathesis());
            manUsrEvaluateScoreDao.insert(score);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            if (StringUtils.isNotBlank(request.getAlertTime())) {
                manCollectionRemark.setAlertTime(simpleDateFormat.parse(request.getAlertTime()));
            }
            if (StringUtils.isNotBlank(request.getPromiseRepaymentTime())) {
                manCollectionRemark.setPromiseRepaymentTime(simpleDateFormat.parse(request.getPromiseRepaymentTime()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        manCollectionRemarkDao.insert(manCollectionRemark);
    }

    /**
     * 根据订单号查询催收备注
     * @param request
     * @return
     */
    public List<ManCollectionRemarkResponse> listManCollectionRemark(ManCollectionRemarkRequest request)
            throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(request.getOrderNo())) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        ManCollectionRemark manCollectionRemark = new ManCollectionRemark();
        manCollectionRemark.setOrderNo(request.getOrderNo());
        manCollectionRemark.setDisabled(0);
        List<ManCollectionRemark> lists = manCollectionRemarkDao.scan(manCollectionRemark);

        if (CollectionUtils.isEmpty(lists)) {
            return null;
        }
        List<ManCollectionRemarkResponse> results = new ArrayList<>();
        lists.stream().forEach(elem -> {
            ManCollectionRemarkResponse response = new ManCollectionRemarkResponse();
            BeanUtils.copyProperties(elem, response);
            results.add(response);
        });
        return results;
    }
}
