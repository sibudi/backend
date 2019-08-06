package com.yqg.service.externalChannel.service;

import com.yqg.common.utils.StringUtils;
import com.yqg.externalChannel.dao.ExternalOrderRelationDao;
import com.yqg.externalChannel.entity.ExternalOrderRelation;
import com.yqg.mongo.dao.ExternalChannelDataDal;
import com.yqg.mongo.entity.ExternalChannelDataMongo;
import java.util.Date;

import com.yqg.service.externalChannel.enums.ExternalChannelEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/8
 * @Email zengxiangcai@yishufu.com
 *
 ****/
@Slf4j
@Service
public class ExternalChannelDataService {

    @Autowired
    private ExternalChannelDataDal externalChannelDataDal;

    @Autowired
    private ExternalOrderRelationDao externalOrderRelationDao;

    /***
     * 保存externalChannel的请求数据到mongo
     * @param url
     * @param decryptedData
     * @param channel
     */
    public void saveExternalChannelRequestDataToMongo(String url, String decryptedData,
        String channel) {
        ExternalChannelDataMongo data = new ExternalChannelDataMongo();
        data.setChannel(channel);
        data.setDecryptedText(decryptedData);
        data.setRequestUri(url);
        data.setCreateTime(new Date());
        data.setUpdateTime(new Date());
        String orderNo = "";
        try {
            if (ExternalChannelEnum.CASHCASH.name().equals(channel)) {
                if(StringUtils.isNotEmpty(decryptedData)){
                    String orderNoPattern = "\"order_no\":\"";
                    int indexStart = decryptedData.indexOf(orderNoPattern) + orderNoPattern.length();
                    int indexEnd = decryptedData.indexOf("\"", indexStart);
                    if(indexStart>=0 && indexEnd>=0){
                        orderNo = decryptedData.substring(indexStart, indexEnd);
                        data.setExternalOrderNo(orderNo);
                    }
                }
            }
            //新增cheetah兼容
            else if (ExternalChannelEnum.CHEETAH.name().equals(channel)) {
                if(StringUtils.isNotEmpty(decryptedData)){
                    String orderNoPattern = "\"orderId\":\"";
                    int indexStart = decryptedData.indexOf(orderNoPattern) + orderNoPattern.length();
                    int indexEnd = decryptedData.indexOf("\"", indexStart);
                    if(indexStart>=0 && indexEnd>=0 && decryptedData.indexOf(orderNoPattern) != -1){
                        orderNo = decryptedData.substring(indexStart, indexEnd);
                        data.setExternalOrderNo(orderNo);
                    } else {
                        String orderNoPattern1 = "\"orderInfo\":{\"id\":\"";
                        int indexStart1 = decryptedData.indexOf(orderNoPattern1) + orderNoPattern1.length();
                        int indexEnd1 = decryptedData.indexOf("\"", indexStart1);
                        if(indexStart1>=0 && indexEnd1>=0 && decryptedData.indexOf(orderNoPattern1) != -1){
                            orderNo = decryptedData.substring(indexStart1, indexEnd1);
                            data.setExternalOrderNo(orderNo);
                        } else {
                            String orderNoPattern2 = "\"mobile\":\"";
                            int indexStart2 = decryptedData.indexOf(orderNoPattern2) + orderNoPattern2.length();
                            int indexEnd2 = decryptedData.indexOf("\"", indexStart2);
                            if(indexStart2>=0 && indexEnd2>=0 && decryptedData.indexOf(orderNoPattern2) != -1){
                                orderNo = decryptedData.substring(indexStart2, indexEnd2);
                                data.setExternalOrderNo(orderNo);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("fetch order_no error", e);
        }
        String id = externalChannelDataDal.insert(data);
        log.info("request save with id: {}, externalOrderNo: {}", id, orderNo);

    }


    /****
     * 根据外部订单号查询内部订单，userUuid
     * @param externalOrderNo
     * @return
     */
    public ExternalOrderRelation getExternalOrderRelationByExternalOrderNo(String externalOrderNo) {
        return externalOrderRelationDao.selectByExternalOrderNo(externalOrderNo);
    }

    /**
     * 根据我们内部订单号去查询外部订单号
     */
    public ExternalOrderRelation getExternalOrderNoByRealOrderNoRelation(String orderNo) {
        return externalOrderRelationDao.selectByOrderNo(orderNo);
    }

    /***
     * 保存关联关系
     * @param externalOrderNo
     * @param orderNo
     * @param userUuid
     * @param channel
     * @return
     */
    public boolean addExternalOrderRelation(String externalOrderNo, String orderNo,
        String userUuid, String channel) {
        ExternalOrderRelation relation = new ExternalOrderRelation();
        relation.setChannel(channel);
        relation.setOrderNo(orderNo);
        relation.setExternalOrderNo(externalOrderNo);
        relation.setUserUuid(userUuid);
        relation.setUpdateTime(new Date());
        relation.setCreateTime(new Date());

        ExternalOrderRelation dbExist = externalOrderRelationDao.selectByOrderNo(orderNo);
        if (dbExist == null) {
            dbExist = externalOrderRelationDao.selectByExternalOrderNo(externalOrderNo);
        }

        if (dbExist != null) {
            //disabled
            log.info("the old order is disabled , orderNo: {}", orderNo);
            externalOrderRelationDao.disableRelation(dbExist.getId());
        }
        Integer affectRow = externalOrderRelationDao.insert(relation);

        return affectRow != null && affectRow == 1;
    }

}
