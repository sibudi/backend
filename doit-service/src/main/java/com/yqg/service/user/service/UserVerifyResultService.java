package com.yqg.service.user.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.utils.StringUtils;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.user.dao.UsrVerifyResultDao;
import com.yqg.user.entity.UsrVerifyResult;
import com.yqg.user.entity.UsrVerifyResult.VerifyTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/***
 * 用户调用第三方验证结果处理
 */
@Service
@Slf4j
public class UserVerifyResultService {

    @Autowired
    private UsrVerifyResultDao usrVerifyResultDao;

    @Autowired
    private OrdDao ordDao;

    //初始化验证信息
    public void initVerifyResult(String orderNo, String userUuid,
                                UsrVerifyResult.VerifyTypeEnum verifyType) {
        UsrVerifyResult result = new UsrVerifyResult();
        result.setCreateTime(new Date());
        result.setUpdateTime(new Date());
        result.setUuid(UUIDGenerateUtil.uuid());
        if(StringUtils.isEmpty(orderNo)||"null".equals(orderNo)){
            //取用户最近的一个订单号
            List<OrdOrder> ordList = ordDao.getLatestOrder(userUuid);
            if(CollectionUtils.isEmpty(ordList)){
                result.setOrderNo("");
            }else{
                result.setOrderNo(ordList.get(0).getUuid());
            }

        }else{
            result.setOrderNo(orderNo);
        }

        result.setUserUuid(userUuid);
        result.setVerifyType(verifyType.getCode());
        result.setVerifyResult(UsrVerifyResult.VerifyResultEnum.INIT.getCode());
        usrVerifyResultDao.insert(result);
    }

    //更新最近的一次验证记录
    public void updateVerifyResult(String orderNo,String userUuid,String response,
                                  UsrVerifyResult.VerifyResultEnum verifyResult,
                                  UsrVerifyResult.VerifyTypeEnum verifyType){
        UsrVerifyResult searchEntity = new UsrVerifyResult();
        searchEntity.setDisabled(0);
        searchEntity.setUserUuid(userUuid);
        searchEntity.setOrderNo(orderNo);
        searchEntity.setVerifyType(verifyType.getCode());
        List<UsrVerifyResult> searchResultList = usrVerifyResultDao.scan(searchEntity);
        if(CollectionUtils.isEmpty(searchResultList)){
            log.warn("cannot find the verify result with userId: {}, orderNo: {}",userUuid,orderNo);
            return;
        }

        UsrVerifyResult searchResult = searchResultList.stream().max(Comparator.comparing(UsrVerifyResult::getId)).get();
        searchResult.setVerifyResult(verifyResult.getCode());
        searchResult.setRemark(response);
        usrVerifyResultDao.update(searchResult);
    }

    public UsrVerifyResult getVerifyResult(String orderNo,String userUuid,UsrVerifyResult.VerifyTypeEnum verifyTypeEnum){
        UsrVerifyResult searchEntity = new UsrVerifyResult();
        searchEntity.setDisabled(0);
        searchEntity.setUserUuid(userUuid);
        searchEntity.setOrderNo(orderNo);
        searchEntity.setVerifyType(verifyTypeEnum.getCode());
        List<UsrVerifyResult> searchResultList = usrVerifyResultDao.scan(searchEntity);
        if(CollectionUtils.isEmpty(searchResultList)){
            return null;
        }
        Optional<UsrVerifyResult> verifyResult = searchResultList.stream().max(Comparator.comparing(UsrVerifyResult::getCreateTime));
        if(verifyResult.isPresent()){
            return verifyResult.get();
        }
        return null;
    }

    public UsrVerifyResult getLatestVerifyResultByUserUuid(String userUuid,VerifyTypeEnum verifyTypeEnum){
        return usrVerifyResultDao.getLatestVerifyResultByUserUuid(userUuid,verifyTypeEnum.getCode());
    }

    public void insertVerifyResult(UsrVerifyResult saveEntity){
        usrVerifyResultDao.insert(saveEntity);
    }

    @Getter
    @Setter
    public static class JXLRealNameVerifyResult{
        private String birthday;
        private String division;
        @JsonProperty(value = "place_of_birth")
        private String placeOfBirth;
        private String verify_result;
        @JsonProperty(value = "id_card_no")
        private String idCard;

        private String gender;
        private String province;
        private String city;
        private String district;
        private String name;
    }


}
