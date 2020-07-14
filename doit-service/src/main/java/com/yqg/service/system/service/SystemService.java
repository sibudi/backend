package com.yqg.service.system.service;

import static com.yqg.common.utils.ZXingCode.drawLogoQRCode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.yqg.base.multiDataSource.annotation.ReadDataSource;
import com.yqg.common.constants.RedisContants;
import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceException;
import com.yqg.common.models.BaseRequest;
import com.yqg.common.redis.RedisClient;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.service.system.request.DictionaryRequest;
import com.yqg.service.system.request.SysAppVersionRequest;
import com.yqg.service.system.response.SysAiqqonIsLiveResponse;
import com.yqg.service.system.response.SysAppH5Response;
import com.yqg.service.system.response.SysAppVersionModel;
import com.yqg.service.system.response.SysBankBasicInfoResponse;
import com.yqg.service.system.response.SysDicItemModel;
import com.yqg.service.system.response.SysSchoolListResponse;
import com.yqg.service.system.response.SysShareDataResponse;
import com.yqg.service.third.upload.UploadService;
import com.yqg.service.third.upload.response.UploadResultInfo;
import com.yqg.system.dao.SysAppH5Dao;
import com.yqg.system.dao.SysAppVersionDao;
import com.yqg.system.dao.SysBankDao;
import com.yqg.system.dao.SysDicDao;
import com.yqg.system.dao.SysDicItemDao;
import com.yqg.system.dao.SysShareDictDao;
import com.yqg.system.entity.SysAppH5;
import com.yqg.system.entity.SysAppVersion;
import com.yqg.system.entity.SysBankBasicInfo;
import com.yqg.system.entity.SysDic;
import com.yqg.system.entity.SysDicItem;
import com.yqg.system.entity.SysShareDict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Component
@Slf4j
public class SystemService {

    @Autowired
    private SysAppVersionDao sysAppVersionDao;
    @Autowired
    private SysAppH5Dao sysAppH5Dao;
    @Autowired
    private SysBankDao sysBankDao;
    @Autowired
    private SysDicDao dicDao;
    @Autowired
    private SysDicItemDao dicItemDao;
    @Autowired
    private SysShareDictDao sysShareDictDao;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private RedisClient redisClient;

    @Value("${upload.path}")
    private  String path;
//    @Value("${downlaod.writerPath}")
//    private String writerPath;

    /**
     * ??????
     *
     * @return isForce? 1??, 2???
     */

    public SysAppVersionModel checkUpdate(SysAppVersionRequest sysAppVersionRequest) {

        SysAppVersionModel modelSpec = new SysAppVersionModel();
        String version = sysAppVersionRequest.getClient_version();
        String clientType = sysAppVersionRequest.getClient_type();
        Integer sysType = 0;
        if (clientType.equals("iOS")) {
            sysType = 1;
        } else if (clientType.equals("android")) {
            if (sysAppVersionRequest.getChannel_sn().equals("10001")) {
                // 旺旺
                sysType = 2;
            } else if (sysAppVersionRequest.getChannel_sn().equals("10002")) {
                // doit
                sysType = 3;
            }
        }
        SysAppVersion serchInfo = new SysAppVersion();
        serchInfo.setDisabled(0);
        serchInfo.setStatus(1);
        serchInfo.setSysType(sysType);
        serchInfo.setAppType(sysAppVersionRequest.getAppType());
        List<SysAppVersion> sysList = sysAppVersionDao.scan(serchInfo);
        if (!CollectionUtils.isEmpty(sysList)) {
            SysAppVersion appVersion = sysList.get(0);
            modelSpec.setUpdateTitle(appVersion.getUpdateTitle());
            modelSpec.setAppUpdateTime(appVersion.getAppUpdateTime());
            modelSpec.setDownloadAddress(appVersion.getDownloadAddress());
            modelSpec.setIsForce(String.valueOf(appVersion.getIsForceUpdate()));
            modelSpec.setLeftBtnTitle(appVersion.getLeftBtnTitle());
            modelSpec.setRightBtnTitle(appVersion.getRightBtnTitle());
            modelSpec.setUpdateContent(appVersion.getUpdateContent());
            modelSpec.setStatus(String.valueOf(appVersion.getStatus()));
            modelSpec.setAppType(String.valueOf(appVersion.getAppType()));
            int temp = version.compareTo(appVersion.getAppVersion());   //将请求中数据版本与查询结果比较
            if(temp >= 0){
                modelSpec.setIsUpdate("2");                              //不需要更新
            }else{
                modelSpec.setIsUpdate("1");                               //需要更新
            }
        }
        return modelSpec;
    }

//    public static void main(String[] args) {
//        int temp = "1.1.10".compareTo("1.2.0");
//        log.info("==========>"+temp);
//    }

    /**
     * ??app????url ??
     */
    public List<SysAppH5Response> getUrlList(RedisClient redisClient) throws ServiceException {

        String cacheStr = redisClient.get(RedisContants.CACHE_H5_URL_LIST_KEY);
        if (cacheStr != null) {
            return JsonUtils.deserialize(cacheStr, List.class);
        }
        SysAppH5 entity = new SysAppH5();
        entity.setDisabled(0);
        List<SysAppH5> appH5List = this.sysAppH5Dao.scan(entity);
        if (CollectionUtils.isEmpty(appH5List)) {
            log.info("????app??url??");
            throw new ServiceException(ExceptionEnum.SYSTEM_APP_CONFIG_LIST_IS_NULL);
        }
        List<SysAppH5Response> sysH5UrlList = new ArrayList<>();
        for (SysAppH5 sysAppH5 : appH5List) {
            SysAppH5Response sysAppH5ModelSpec = new SysAppH5Response();
            sysAppH5ModelSpec.setUrlKey(sysAppH5.getUrlKey());
            sysAppH5ModelSpec.setUrlValue(sysAppH5.getUrlValue());
            sysAppH5ModelSpec.setUrlDesc(sysAppH5.getUrlDesc());
            sysH5UrlList.add(sysAppH5ModelSpec);
        }
        redisClient.set(RedisContants.CACHE_H5_URL_LIST_KEY, JsonUtils.serialize(sysH5UrlList));
        return sysH5UrlList;
    }

    /**
     * ????????
     *
     * @param baseRequest
     * @param redisClient
     */
    public List<SysBankBasicInfoResponse> getBankInfo(BaseRequest baseRequest, RedisClient redisClient) {
        // ??redis
        String cacheStr = redisClient.get(RedisContants.CACHE_BANK_CARD_LIST_KEY);
        if (cacheStr != null) {
            return JsonUtils.deserialize(cacheStr, List.class);
        }
        List<SysBankBasicInfo> result = sysBankDao.getBankInfoForCIMB();
        List<SysBankBasicInfoResponse> response = new ArrayList<>();
        SysBankBasicInfoResponse obj = null;
        for (SysBankBasicInfo item : result) {
            obj = new SysBankBasicInfoResponse();
            obj.setBankCode(item.getBankCode());
            obj.setBankName(item.getBankName());
            response.add(obj);
        }
        redisClient.set(RedisContants.CACHE_BANK_CARD_LIST_KEY, JsonUtils.serialize(response));
        return response;
    }

    /**
     * ??????DicCode?????List
     */
    @ReadDataSource
    public List<SysDicItemModel> dicItemListByDicCode(DictionaryRequest request)
            throws Exception {
        String dicCode = request.getDicCode();
        if (org.apache.commons.lang3.StringUtils.isEmpty(dicCode)) {
            log.error("??????DicCode?????List:????");
            throw new ServiceException(ExceptionEnum.MANAGE_SEARCH_ERROR);
        }

        List<SysDicItemModel> result = this.sysDicItemsListByDicCode(dicCode);
        return result;
    }

    /**
     * ??????DicCode?????List
     */
    @ReadDataSource
    public List<SysDicItemModel> sysDicItemsListByDicCode(String dicCode) throws Exception {
        SysDic dicSearch = new SysDic();
        dicSearch.setDisabled(0);
        dicSearch.setDicCode(dicCode);
        List<SysDic> dicResult = this.dicDao.scan(dicSearch);
        if (dicResult.size() <= 0) {
            return null;
        }
        Integer parentId = dicResult.get(0).getId();
        List<SysDicItemModel> dicItemResult = this.sysDicItemListByParentId(parentId.toString());

        return dicItemResult;
    }

    /**
     * ????????????
     */
    @ReadDataSource
    public List<SysDicItemModel> sysDicItemListByParentId(String parentId) throws Exception {
        SysDicItem search = new SysDicItem();
        search.setDisabled(0);
        search.setDicId(parentId);
        List<SysDicItem> list = this.dicItemDao.scan(search);
        List<SysDicItemModel> modeList = new ArrayList<SysDicItemModel>();
        SysDicItemModel dicItemModel = null;
        for (SysDicItem dicItem : list) {
            dicItemModel = new SysDicItemModel();
            dicItemModel.setId(dicItem.getId());
            dicItemModel.setDicId(dicItem.getDicId());
            dicItemModel.setDicItemName(dicItem.getDicItemName());
            dicItemModel.setDicItemValue(dicItem.getDicItemValue());
            dicItemModel.setLanguage(dicItem.getLanguage());
            modeList.add(dicItemModel);
        }
        return modeList;
    }

    /**
     * 分享二维码接口
     */
    public SysShareDataResponse getShareQRCode(BaseRequest baseRequest,File logoFile) throws Exception {
        SysShareDataResponse response =  getShareData(baseRequest,2);
        File QrCodeFile = new File(baseRequest.getUserUuid()+".png");
        try {

            String url = response.getShareUrl();
            String note = "";
            String base64Str = drawLogoQRCode(logoFile, QrCodeFile, url, note);
//        上传文件
            UploadResultInfo resultInfo =uploadService.uploadBase64Img(baseRequest.getSessionId(),base64Str,baseRequest.getUserUuid(),"png");
            if(resultInfo.getCode()==1){
                url =  path+resultInfo.getData();
            }else{
                log.info("上传图片出错。");
            }

            response.setShareImageUrl(url);
        }catch (Exception e){
            log.error("获取分享二维码图片异常",e);
        }finally {
            //删除临时文件
            if (QrCodeFile.exists()){
                QrCodeFile.delete();
            }
        }
        return response;
    }

    /**
     * 分享链接接口
     */
    public SysShareDataResponse getShareData(BaseRequest baseRequest,Integer shareId) throws Exception {
        if (StringUtils.isEmpty(baseRequest.getUserUuid())){
            log.info("获取分享配置参数错误--------参数不合法");
            throw new ServiceException(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        List<SysShareDict> shareList = this.sysShareDictDao.getShareDictWithShareId(shareId);
        if (CollectionUtils.isEmpty(shareList)) {
            log.info("没有找到app分享配置");
            throw new ServiceException(ExceptionEnum.SYSTEM_SHARE_LIST_IS_NULL);
        }

        SysShareDataResponse shareDict = new SysShareDataResponse();
        shareDict.setShareTitle(shareList.get(0).getShareTitle());
        shareDict.setShareContent(shareList.get(0).getShareContent());
        shareDict.setShareImageUrl(shareList.get(0).getShareImageUrl());
        String shareUrl = shareList.get(0).getShareUrl();
        if (shareId == 2){
            log.info("----------------userUuid="+baseRequest.getUserUuid()+"-------------");
            shareUrl += ((shareUrl.indexOf("?") > 0) ? "&" : "?") + "invite=" + DESUtils.encrypt(baseRequest.getUserUuid());
        }

        shareDict.setShareUrl(shareUrl);

        return shareDict;
    }


    /**
     * ??????
     */
    public List<SysSchoolListResponse> getSchoolNameWithKey(String name) {

        SysDicItem search = new SysDicItem();
        search.setDisabled(0);
        search.setDicId("16");
        search.set_likeField("dicItemName");
        search.set_likeKeyword(name);
        search.set_start(0);
        search.set_pageSize(5);

        List<SysSchoolListResponse> responseList = new ArrayList<>();
        List<SysDicItem> list = this.dicItemDao.scan(search);
        if (!CollectionUtils.isEmpty(list)) {
            for (SysDicItem item : list) {

                SysSchoolListResponse response = new SysSchoolListResponse();
                response.setDicItemName(item.getDicItemName());
                responseList.add(response);
            }

        }
        return responseList;
    }


    /**
     * csv ???
     * @param type  1=??  0=??  3=??
     *
     */
//    public String codeUserMobileCsv(String type,String readerPath) throws IOException {
//
//        List<String> phone = new ArrayList<>();
//        if(!type.equals("3")){
//            CsvReader csvReader = new CsvReader(readerPath);
//            csvReader.readHeaders();
//            while (csvReader.readRecord()){
//                // ???????
//                if(type.equals("1")){// 1=??
//                    phone.add(DESUtils.encrypt(csvReader.get("phone")));
//                }else if(type.equals("0")){// 0=??
//                    phone.add(DESUtils.decrypt(csvReader.get("phone")));
//                }
//            }
//        }
//        String writerPath1 = "D:\\upload\\2222.csv";
//        String[] headers = {"phone"};
//        CsvWriter csvWriter = new CsvWriter(writerPath,',', Charset.forName("GBK"));
//        csvWriter.writeRecord(headers);
//        for(String item:phone){
//            String[] itemArr = {item};
//            csvWriter.writeRecord(itemArr);
//        }
//        csvWriter.close();
//        return writerPath;
//    }
//
//    public static void main(String[] args) throws IOException {
////        codeUserMobileCsv("3","");
//
//    }




public SysAiqqonIsLiveResponse getAiqqonStatus(){
    SysAiqqonIsLiveResponse response = new SysAiqqonIsLiveResponse();
    String result = redisClient.get(RedisContants.AIQQON_SWITCH);
    if(result.equals("1")){
    response.setIsOn(result);
    } else {
    response.setIsOn("0");
    }
    return response;
    }
}
    





