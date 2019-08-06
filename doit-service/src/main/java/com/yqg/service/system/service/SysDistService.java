package com.yqg.service.system.service;

import com.yqg.base.multiDataSource.annotation.ReadDataSource;
import com.yqg.service.system.request.SysDistRequest;
import com.yqg.service.system.response.SysDistModel;
import com.yqg.system.dao.SysDistDao;
import com.yqg.system.entity.SysDist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Service
public class SysDistService {

    @Autowired
    private SysDistDao sysDistDao;

    /**
     * 查询行政区划
     * @param distRequest
     * @return
     * @throws Exception
     */
    @ReadDataSource
    public List<SysDistModel> getDistList(SysDistRequest distRequest) throws Exception {
        SysDist sysDist = new SysDist();
        sysDist.setDisabled(0);
        sysDist.setParentCode(distRequest.getParentCode());
        sysDist.setLanguage(distRequest.getLanguage());
        sysDist.setUuid(distRequest.getUuid());
        sysDist.setDistCode(distRequest.getDistCode());
        sysDist.setDistLevel(distRequest.getDistLevel());
        sysDist.setDistName(distRequest.getDistName());
        List<SysDist> distList = this.sysDistDao.scan(sysDist);
        List<SysDistModel> list = new ArrayList<SysDistModel>();
        SysDistModel sysDistModel = null;
        for(SysDist item:distList){
            sysDistModel = new SysDistModel();
            sysDistModel.setDistName(item.getDistName());
            sysDistModel.setDistCode(item.getDistCode());
            sysDistModel.setUuid(item.getUuid());
            sysDistModel.setDistLevel(item.getDistLevel());
            sysDistModel.setLanguage(item.getLanguage());
            sysDistModel.setParentCode(item.getParentCode());
            list.add(sysDistModel);
        }
        return list;
    }
}
