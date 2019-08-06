package com.yqg.service.system.service;

import com.yqg.base.multiDataSource.annotation.ReadDataSource;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.models.PageData;
import com.yqg.common.utils.UUIDGenerateUtil;
import com.yqg.service.system.request.DictionaryRequest;
import com.yqg.service.system.response.DictionaryTreeResponse;
import com.yqg.service.system.response.SysDicItemModel;
import com.yqg.system.dao.SysDicDao;
import com.yqg.system.entity.SysDic;
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
public class SysDicService {

    @Autowired
    private SysDicDao dicDao;

    @Autowired
    private SysDicItemService sysDicItemService;

    public void addFatherDictionary(DictionaryRequest request) throws Exception {
        String dicName = request.getDicName();
        String dicCode = request.getDicCode();
        if(StringUtils.isEmpty(dicCode) || StringUtils.isEmpty(dicName)){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_ADD_ITEM_ERROR);
        }
        SysDic addInfo = new SysDic();
        addInfo.setCreateTime(new Date());
        addInfo.setUpdateTime(new Date());
        addInfo.setUuid(UUIDGenerateUtil.uuid());
        addInfo.setDicName(request.getDicName());
        addInfo.setDicCode(request.getDicCode());
        this.dicDao.insert(addInfo);
    }

    public void editFatherDictionary(DictionaryRequest request) throws Exception {
        String dicName = request.getDicName();
        String dicCode = request.getDicCode();
        Integer id = request.getId();
        if(StringUtils.isEmpty(dicCode) || StringUtils.isEmpty(dicName) || id == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_EDIT_ITEM_ERROR);
        }

        SysDic editInfo = new SysDic();
        editInfo.setUpdateTime(new Date());
        editInfo.setDicName(request.getDicName());
        editInfo.setDicCode(request.getDicCode());
        editInfo.setId(id);
        this.dicDao.update(editInfo);
    }

    @ReadDataSource
    public PageData fatherDictionaryList(DictionaryRequest request) throws Exception {
        PageData response = new PageData();
        Integer pageSize = request.getPageSize();
        Integer pageNo = request.getPageNo();
        if(pageNo == null || pageSize == null){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        Integer pageStart = (pageNo - 1) * pageSize;
        List<SysDic> dicList = this.dicDao.sysDicByPage(pageSize,pageStart);
        List<DictionaryTreeResponse> treeList = new ArrayList<>();

        for(SysDic item:dicList){
            DictionaryTreeResponse treeItem = new DictionaryTreeResponse();
            treeItem.setCreateTime(item.getCreateTime());
            treeItem.setDicItemValue(item.getDicCode());
            treeItem.setDicItemName(item.getDicName());
            treeItem.setId(item.getId());
            List<SysDicItemModel> result = this.sysDicItemService.sysDicItemListByParentId(item.getId().toString());
            treeItem.setChildren(result);
            treeList.add(treeItem);
        }
        SysDic search = new SysDic();
        search.setDisabled(0);
        Integer totalRecord = this.dicDao.count(search);

        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setRecordsTotal(totalRecord);
        response.setData(treeList);
        return response;
    }

    /**
     * ??????DicCode?????List*/
    @ReadDataSource
    public List<SysDicItemModel> sysDicItemsListByDicCode(String dicCode) throws Exception {
        SysDic dicSearch = new SysDic();
        dicSearch.setDisabled(0);
        dicSearch.setDicCode(dicCode);
        List<SysDic> dicResult = this.dicDao.scan(dicSearch);
        if(dicResult.size() <= 0){
            return null;
        }
        Integer parentId = dicResult.get(0).getId();
        List<SysDicItemModel> dicItemResult = this.sysDicItemService.sysDicItemListByParentId(parentId.toString());

        return dicItemResult;
    }

    /**
     * ??????DicCode?????List*/
    @ReadDataSource
    public List<SysDicItemModel> dicItemListByDicCode(DictionaryRequest request)
            throws Exception {
        String dicCode = request.getDicCode();
        if(StringUtils.isEmpty(dicCode)){
            throw new ServiceExceptionSpec(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        List<SysDicItemModel> result = this.sysDicItemsListByDicCode(dicCode);
        return result;
    }


}
