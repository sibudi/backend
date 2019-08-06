package com.yqg.service.system.service;

import com.alibaba.fastjson.JSONObject;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.utils.DateUtils;
import com.yqg.order.dao.CouponConfigDao;
import com.yqg.order.entity.CouponConfig;
import com.yqg.order.entity.CouponRecord;
import com.yqg.service.system.response.CouponResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yqg.order.dao.CouponRecordDao;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghuaizhou on 2019/5/15.
 */
@Service
@Slf4j
public class CouponService {

    @Autowired
    private CouponRecordDao couponRecordDao;
    @Autowired
    private CouponConfigDao couponConfigDao;

    // 获取用户优惠券列表
    public Object getCouponList(BaseRequest baseRequest){

        JSONObject result = new JSONObject();

        CouponRecord scan = new CouponRecord();
        scan.setDisabled(0);
        scan.setUserUuid(baseRequest.getUserUuid());
        List<CouponRecord> couponRecordList = this.couponRecordDao.scan(scan);
        if (!CollectionUtils.isEmpty(couponRecordList)){

            // 未使用
            ArrayList<CouponResponse>  notUseList = new ArrayList<>();
            // 已使用
            ArrayList<CouponResponse>  useList = new ArrayList<>();
            // 已过期
            ArrayList<CouponResponse>  overdueList = new ArrayList<>();

            for (CouponRecord record:couponRecordList){
                CouponResponse response = new CouponResponse();
                response.setCouponStartDate(DateUtils.DateToString6(record.getValidityStartTime()));
                response.setCouponEndDate(DateUtils.DateToString6(record.getValidityEndTime()));
                //通过优惠券名称查询优惠券
                CouponConfig couponConfig = getCouponConfig(record.getCouponConfigId());
                if (couponConfig != null) {
                     response.setCouponName(couponConfig.getIndonisaName());
                }
                response.setCouponNum(record.getMoney()+"");
                response.setStatus(record.getStatus()+"");
                switch (record.getStatus()) {
                    case 1: // 已使用
                        useList.add(response);
                        break;
                    case 2: // 已过期
                        overdueList.add(response);
                        break;
                    case 3: // 未使用
                        notUseList.add(response);
                        break;
                }
            }
            result.put("notUseList",notUseList); // 未使用
            result.put("useList",useList); // 已使用
            result.put("overdueList",overdueList); //已过期
        }
        return result;
    }

    // 获取优惠券对应的配置
    public CouponConfig getCouponConfig(int couponConfId){

        CouponConfig couponConfig = new CouponConfig();
        couponConfig.setId(couponConfId);
        List<CouponConfig> couponConfigs = this.couponConfigDao.scan(couponConfig);
        if (CollectionUtils.isEmpty(couponConfigs)) {
            return null;
        }
        return couponConfigs.get(0);
    }



    // 获取用户未使用的优惠券列表(根据订单号)
    public CouponRecord getCouponInfoWithOrderNo(String orderNo){

        CouponRecord scan = new CouponRecord();
        scan.setDisabled(0);
        scan.setOrderNo(orderNo);
        scan.setStatus(CouponRecord.StatusEnum.NOT_USE.getCode());
        List<CouponRecord> couponRecordList = this.couponRecordDao.scan(scan);
        if (!CollectionUtils.isEmpty(couponRecordList)){
            return couponRecordList.get(0);
        }
        return null;
    }


    // 获取用户优惠券列表(根据优惠券id)
    public CouponRecord getCouponInfoWithUuid(String couponUuid){

        CouponRecord scan = new CouponRecord();
        scan.setDisabled(0);
        scan.setUuid(couponUuid);
        List<CouponRecord> couponRecordList = this.couponRecordDao.scan(scan);
        if (!CollectionUtils.isEmpty(couponRecordList)){
            return couponRecordList.get(0);
        }
        return null;
    }


    // 更新
    public void updateCoupon(CouponRecord record){
        this.couponRecordDao.update(record);
    }
}
