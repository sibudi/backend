package com.yqg.service.system.service;

import com.yqg.base.multiDataSource.annotation.ReadDataSource;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.service.system.request.DictionaryRequest;
import com.yqg.service.system.response.SysDicItemModel;
import com.yqg.system.dao.SysDicItemDao;
import com.yqg.system.entity.SysDicItem;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Service
@Log
public class SysDicItemService {

    @Autowired
    private SysDicItemDao dicItemDao;

    /**
     * ????????????*/
    @ReadDataSource
    public List<SysDicItemModel> sysDicItemListByParentId(String parentId) throws Exception {
        SysDicItem search = new SysDicItem();
        search.setDisabled(0);
        search.setDicId(parentId);
        List<SysDicItem> list= this.dicItemDao.scan(search);
        List<SysDicItemModel> modeList = new ArrayList<SysDicItemModel>();
        SysDicItemModel dicItemModel = null;
        for (SysDicItem dicItem:list){
            dicItemModel = new SysDicItemModel();
            dicItemModel.setId(dicItem.getId());
            dicItemModel.setDicId(dicItem.getDicId());
            dicItemModel.setDicItemName(dicItem.getDicItemName());
            dicItemModel.setDicItemValue(dicItem.getDicItemValue());
            dicItemModel.setLanguage(dicItem.getLanguage());
            modeList.add(dicItemModel);
        }
        return  modeList;
    }

    /**
     * ?????*/
    public void sysDicItemAdd(DictionaryRequest request) throws Exception {
        String dicItemName = request.getDicName();
        String dicItemValue = request.getDicCode();
        String parentId  = request.getParentId();
        String language = request.getLanguage();
        if(StringUtils.isEmpty(dicItemName) || StringUtils.isEmpty(dicItemValue) || parentId == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_ITEM_ERROR);
        }
        SysDicItem addInfo = new SysDicItem();
        addInfo.setCreateTime(new Date());
        addInfo.setUpdateTime(new Date());
        addInfo.setUuid(UUIDGenerateUtil.uuid());
        addInfo.setDicItemName(request.getDicName());
        addInfo.setDicItemValue(request.getDicCode());
        addInfo.setDicId(parentId);
        addInfo.setLanguage(language);
        this.dicItemDao.insert(addInfo);
    }

    /**
     * ?????*/
    public void sysDicItemEdit(DictionaryRequest request) throws Exception {
        String dicItemName = request.getDicName();
        String dicItemValue = request.getDicCode();
        String language = request.getLanguage();
        Integer id  = request.getId();
        if(StringUtils.isEmpty(dicItemName) || StringUtils.isEmpty(dicItemValue) || id == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }

        SysDicItem editInfo = new SysDicItem();
        editInfo.setId(request.getId());
        editInfo.setDicItemName(request.getDicName());
        editInfo.setDicItemValue(request.getDicCode());
        editInfo.setLanguage(language);
        this.dicItemDao.update(editInfo);
    }

    /**
     * ?????value*/
    public void sysDicItemValueEdit(DictionaryRequest request) throws Exception {
        String dicItemValue = request.getDicCode();
        Integer id  = request.getId();
        if(StringUtils.isEmpty(dicItemValue) || id == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }

        SysDicItem editInfo = new SysDicItem();
        editInfo.setId(request.getId());
        editInfo.setDicItemValue(request.getDicCode());

        this.dicItemDao.update(editInfo);
    }
}
