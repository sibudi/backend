package com.yqg.service.third.izi;

import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.third.izi.response.IziResponse;
import com.yqg.service.third.izi.response.IziResponse.IziWhatsAppDetailResponse;
import com.yqg.service.util.CustomEmojiUtils;
import com.yqg.third.dao.IziWhatsAppDetailDao;
import com.yqg.third.entity.IziWhatsAppDetailEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class IziWhatsAppService {

    @Autowired
    private IziWhatsAppDetailDao iziWhatsAppDetailDao;

    public void saveWhatsAppDetail(IziResponse response, String userUuid, String orderNo, String mobile, int type) {
        if (response == null) {
            return;
        }
        IziWhatsAppDetailEntity saveEntity = new IziWhatsAppDetailEntity();
        saveEntity.setCreateTime(new Date());

        //get from db
        IziWhatsAppDetailEntity dbEntity = getLatestIziWhatsDetail(userUuid, type, mobile);
        if (dbEntity != null) {
            saveEntity = dbEntity;
        }
        saveEntity.setUpdateTime(new Date());
        saveEntity.setDisabled(0);
        saveEntity.setOrderNo(orderNo);
        saveEntity.setUserUuid(userUuid);
        saveEntity.setStatus(response.getStatus());
        saveEntity.setMobileNumber(mobile);
        if (!"OK".equalsIgnoreCase(response.getStatus())) {
            //errorInfo
            if (response.getMessage() != null) {
                saveEntity.setMessage(JsonUtils.serialize(response.getMessage()));
            }
        } else {
            IziWhatsAppDetailResponse detail = JsonUtils.deserialize(JsonUtils.serialize(response.getMessage()),
                    IziWhatsAppDetailResponse.class);

            saveEntity.setAvatar(detail.getAvatar());
            saveEntity.setBusinessUser(detail.getBusiness_user());
            saveEntity.setType(type);
            String signature = detail.getSignature();
            if(StringUtils.isNotEmpty(signature)){
                signature = CustomEmojiUtils.removeEmoji(signature);
            }
            saveEntity.setSignature(signature);
            saveEntity.setWhatsapp(detail.getWhatsapp());
            saveEntity.setStatusUpdate(detail.getStatus_update());
        }
        if (dbEntity != null) {
            iziWhatsAppDetailDao.update(saveEntity);
        } else {
            iziWhatsAppDetailDao.insert(saveEntity);
        }

    }

    public IziWhatsAppDetailEntity getLatestIziWhatsDetail(String userUuid, Integer type, String mobile) {
        return iziWhatsAppDetailDao.getLatestResultByUserIdAndType(userUuid, type, mobile);
    }
}
