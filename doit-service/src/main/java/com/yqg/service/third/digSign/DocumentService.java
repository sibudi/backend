package com.yqg.service.third.digSign;

import com.yqg.signcontract.dao.OrderContractDao;
import com.yqg.signcontract.entity.OrderContract;
import com.yqg.signcontract.entity.OrderContract.DocumentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class DocumentService {

    @Autowired
    private OrderContractDao orderContractDao;

    public void saveDocument(String orderNo, String userUuid, DocumentStatus documentStatus,String downloadedPath,String remark){
        OrderContract searchParam = new OrderContract();
        searchParam.setUserUuid(userUuid);
        searchParam.setOrderNo(orderNo);
        searchParam.setDocumentId(orderNo);
        searchParam.setDisabled(0);
        List<OrderContract> dbDocumentList = orderContractDao.scan(searchParam);
        if(CollectionUtils.isEmpty(dbDocumentList)){
            //insert
            OrderContract newDocument = new OrderContract();
            newDocument.setUserUuid(userUuid);
            newDocument.setOrderNo(orderNo);
            newDocument.setDocumentId(orderNo);
            newDocument.setDownloadedPath(downloadedPath);
            newDocument.setStatus(documentStatus.getCode());
            if(remark!=null&&remark.length()>200){
                remark = remark.substring(0,200);
            }
            newDocument.setRemark(remark);
            orderContractDao.insert(newDocument);
        }else{
            //update
            OrderContract dbDocument = dbDocumentList.get(0);
            dbDocument.setStatus(documentStatus.getCode());
            if(!StringUtils.isEmpty(downloadedPath)){
                dbDocument.setDownloadedPath(downloadedPath);
            }
            orderContractDao.update(dbDocument);
        }
    }

    public Optional<OrderContract> getOrderContract(String orderNo){
        OrderContract searchParam = new OrderContract();
        searchParam.setOrderNo(orderNo);
        searchParam.setDocumentId(orderNo);
        searchParam.setDisabled(0);
        List<OrderContract> dbDocumentList = orderContractDao.scan(searchParam);
        if(CollectionUtils.isEmpty(dbDocumentList)){
            return Optional.empty();
        }else{
            return dbDocumentList.stream().max(Comparator.comparing(OrderContract::getUpdateTime));
        }
    }

    public List<OrderContract> getNeedToDownloadOrderContracts(){
        return orderContractDao.getNeedToDownLoadListForContract();
    }
    public List<OrderContract> getNeedToCheckSignStatusContracts(){
        return orderContractDao.getNeedToCheckSignStatusContracts();
    }

}