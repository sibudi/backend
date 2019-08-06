package com.yqg.manage.service.collection;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yqg.common.models.PageData;
import com.yqg.common.utils.StringUtils;
import com.yqg.manage.dal.collection.SmsTemplateDao;
import com.yqg.manage.entity.collection.SmsTemplateEntity;
import com.yqg.manage.service.collection.request.SmsTemplateRequest;
import com.yqg.manage.service.collection.response.ContactSwitchResponse;
import com.yqg.manage.util.PageDataUtils;
import com.yqg.system.dao.SysParamDao;
import com.yqg.system.entity.SysParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Author: tonggen
 * Date: 2018/3/26
 * time: 上午11:16
 */
@Service
public class SmsTemplateService {

    private Logger logger = LoggerFactory.getLogger(SmsTemplateService.class);

    @Autowired
    private SmsTemplateDao smsTemplateDao;

    @Autowired
    private SysParamDao sysParamDao;
    /**
     * 更新或者新增模板
     * @param request
     * @return
     */
    public Integer insertOrUpdateSmsTemp(SmsTemplateRequest request) {

        if (StringUtils.isEmpty(request.getSmsContent())
                || StringUtils.isEmpty(request.getSmsTitle())) {
            logger.info("insertOrUpdateSmsTemp param is null!");
            return 0;
        }
        SmsTemplateEntity smsTemplateEntity = new SmsTemplateEntity();
        BeanUtils.copyProperties(request, smsTemplateEntity);
        if (request.getId() != null && request.getId() != 0) {
            return smsTemplateDao.update(smsTemplateEntity);
        }
        Integer maxId = smsTemplateDao.getMaxId();
        maxId = maxId == null ? 0 : maxId;
        String tempId = this.getTemplateId(maxId + 1);
        smsTemplateEntity.setSmsTemplateId(tempId);
        return smsTemplateDao.insert(smsTemplateEntity);

    }
    /**
     * 删除模板
     * @param request
     * @return
     */
    public Integer deleteSmsTemp(SmsTemplateRequest request) {

        if (request.getId() == null) {
            logger.info("deleteSmsTemp param is null!");
            return 0;
        }
        SmsTemplateEntity smsTemplateEntity = new SmsTemplateEntity();
        smsTemplateEntity.setId(request.getId());
        smsTemplateEntity.setDisabled(1);
        return smsTemplateDao.update(smsTemplateEntity);
    }

    /**
     * 获得短信模板列表
     * @param request
     * @return
     */
    public PageData<List<SmsTemplateEntity>> smsTemplateList(SmsTemplateRequest request) {

        PageHelper.startPage(request.getPageNo(), request.getPageSize());
        SmsTemplateEntity smsTemplateEntity = new SmsTemplateEntity();
        smsTemplateEntity.setDisabled(0);
        List<SmsTemplateEntity> rList = smsTemplateDao.scan(smsTemplateEntity);
        PageInfo pageInfo = new PageInfo(rList);
        return PageDataUtils.mapPageInfoToPageData(pageInfo);
    }

    /**
     * 进行补全位数
     * @param id
     * @return
     */
    private String getTemplateId(Integer id) {

        if (id == null) {
            return "";
        }
        String tempId = String.valueOf(id);
        while (tempId.length() < 6) {
            tempId = "0" + tempId;
        }
        return "ID" + tempId;
    }

    /**
     * 获得一键发送短信开关
     * @param sysParam
     */
    public Integer updateCollectionSmsSwitch(SysParam sysParam) {

        if (sysParam == null || sysParam.getId() == null) {
            return 0;
        }
        sysParam.setUpdateTime(new Date());
        return sysParamDao.update(sysParam);
    }

    /**
     * 获得一键发送短信开关
     * @return
     */
    public List<SysParam> listCollectionSmsSwitch() {

        return sysParamDao.listCollectionSmsSwitch();
    }

    /**
     *
     * @return
     */
    public List<SysParam> listContactSwitch() {

        return sysParamDao.listContactSwitch();
    }
    /**
     * 修改联系人开关
     * @param sysParam
     */
    public Integer updateContactSwitch(SysParam sysParam) {

        if (sysParam == null || StringUtils.isEmpty(sysParam.getSysKey())) {
            return 0;
        }
        sysParamDao.updateSysParam(sysParam.getSysValue(), sysParam.getSysKey());
        return 1;
    }

    /**
     * 获得一键发送短信开关
     * @param sysParam
     */
    public Boolean getCollectionSmsSwitch(SmsTemplateRequest sysParam) {

        if (sysParam == null || StringUtils.isEmpty(sysParam.getSysKey())) {
            return false;
        }
        List<String> strs = sysParamDao.getCollectionSmsSwitch("sms:collections:" + sysParam.getSysKey());
        if (!CollectionUtils.isEmpty(strs) && "true".equals(strs.get(0))) {
            return true;
        }
        return false;
    }

    /**
     * 查询备用联系人是否展示
     * @param sysParam
     */
    public List<ContactSwitchResponse> getContactSwitch(SmsTemplateRequest sysParam) {

        List<ContactSwitchResponse> rLists = new ArrayList<>();

        if (sysParam == null || StringUtils.isEmpty(sysParam.getSysKey())) {
            return rLists;
        }

        for (int index = 0; index < 6 ;index++) {
            String type = "";
            if (index == 0) {
                type = "self:";
            } else if (index == 1) {
                type = "spare:";
            } else if (index == 2) {
                type = "com:";
            } else if (index == 3) {
                type = "selfSpare:";
            } else if (index == 4) {
                type = "link:";
            } else if (index == 5) {
                type = "comPhone:";
            }
            List<String> strs = sysParamDao.getCollectionSmsSwitch("users:contact:" + type + sysParam.getSysKey());
            ContactSwitchResponse response = new ContactSwitchResponse();
            response.setType(type);
            if (!CollectionUtils.isEmpty(strs) && "true".equals(strs.get(0))) {
                response.setResult(true);

            } else {
                response.setResult(false);
            }
            rLists.add(response);
        }

        return rLists;
    }
}
