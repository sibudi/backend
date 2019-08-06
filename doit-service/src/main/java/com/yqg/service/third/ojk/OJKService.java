package com.yqg.service.third.ojk;

import com.yqg.common.enums.system.ExceptionEnum;
import com.yqg.common.exceptions.ServiceExceptionSpec;
import com.yqg.common.utils.DateUtils;
import com.yqg.common.utils.JsonUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.ojk.dao.OjkDataTotalDao;
import com.yqg.ojk.entity.BaseOjkData;
import com.yqg.ojk.entity.OjkDataTotal;
import com.yqg.service.third.ojk.aes.AESUtils;
import com.yqg.service.third.ojk.config.DefaultOjkValue;
import com.yqg.service.third.ojk.config.OJKConfig;
import com.yqg.service.util.LoginSysUserInfoHolder;
import com.yqg.user.dao.UsrDao;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 定时上传数据给OJK
 * Created by wanghuaizhou on 2019/5/31.
 */
@Component
@Slf4j
public class OJKService {

    @Autowired
    private OJKConfig ojkConfig;

    @Autowired
    private OkHttpClient httpClient;

    @Autowired
    private OjkDataTotalDao ojkDataTotalDao;

    @Autowired
    private UsrDao usrDao;

    /**
     *  上传数据到OJK
     * */
    public void uploadDataToOjk(){

      try {
          final String credential = Credentials.basic(this.ojkConfig.getUserName(),this.ojkConfig.getPassword());

          RequestBody requestBody = new FormBody.Builder()
                  .add("language","id-ID")
                  .add("audioFileUrl","http://h5.do-it.id/Test.mp3")
                  .build();

          Request request = new Request.Builder()
                  .url(this.ojkConfig.getUploadUrl())
                  .post(requestBody)
                  .header("Authorization", credential)
                  .addHeader("Content-Type","application/json")
                  .build();

//         请求数据落库 存在mongo

          Response response = httpClient.newCall(request).execute();
          if(response.isSuccessful())
          {
              String  responseStr = response.body().string();
              log.info("上传OJK数据 请求后返回:{}", JsonUtils.serialize(responseStr));
//            inforbipResponse = JsonUtils.deserialize(responseStr,InforbipResponse.class);
              // 响应数据落库 存在mongo

          }else {
              log.error("上传OJK数据 失败"+response);
          }
      }catch (Exception e){
          log.error("上传OJK数据异常",e);
      }
    }

    /**
     * 通过反射更新或者添加Ojk数据
     * @param obj
     * @throws IllegalAccessException
     */
    public void insertOrUpdateOjkData(Object obj) throws IllegalAccessException {

        Map<Object , Object> configValue = DefaultOjkValue.setMapValue();

        Class clazz = obj.getClass();
        String className = clazz.getName();
        //新增
        Integer code = ((BaseOjkData)obj).getIdCode();
        if (code == null || code == 0) {
            code = getMaxCodeValue();
            for (Field field : clazz.getDeclaredFields()) {

                field.setAccessible(true);
                Object value = getRequestValue(field, obj);
                String fieldName = field.getName();
                if ("null".equals(value) || "0".equals(value)) {
                    value = configValue.get(fieldName);
                }
                log.info("insert key is {}, value is {}, className is {}", fieldName, value, className);
                if (value == null) {
                    continue;
                }
                insertOjkData(fieldName, value.toString(),className, code);
            }
        } else {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = getRequestValue(field, obj);
                if ("null".equals(value) || "0".equals(value)) {
                    continue;
                }
                log.info("insert key is {}, value is {}, className is {}", fieldName, value, className);
                if (value == null) {
                    continue;
                }
                updateOjkData(fieldName, value.toString(), className, code);
            }
        }
    }
    private Object getRequestValue(Field field, Object obj) throws IllegalAccessException {

        Object value = field.get(obj);
        if (field.getType().getName().contains("Date")) {
            SimpleDateFormat sf = new SimpleDateFormat("YYYY-MM-dd");
            try {
                return sf.format(value);
            } catch (Exception e) {
                log.error("getRequestValue is error.");
                return null;
            }

        }
        return value;
    }

    private void insertOjkData(String key, String value, String type, int code) {

        OjkDataTotal ojkDataTotal = new OjkDataTotal();
        ojkDataTotal.setOjkKey(key);
        ojkDataTotal.setIdCode(code);
        ojkDataTotal.setOjkValue(value);
        ojkDataTotal.setType(type);
        ojkDataTotal.setCreateUser(LoginSysUserInfoHolder.getLoginSysUserId());
        ojkDataTotalDao.insert(ojkDataTotal);

    }

    private void updateOjkData(String key, String value, String type, int code) {

        ojkDataTotalDao.updateOjkData(key, value, code, type);
    }

    private int getMaxCodeValue() {

        Integer result = ojkDataTotalDao.getMaxCodeValue();

        return result == null ? 1 : result + 1;
    }

    /**
     * 根据类型查询ojk一个list数据
     * @param type
     * @return
     */
    public List<Object> listOjkData(String type) throws ServiceExceptionSpec {

        if (StringUtils.isEmpty(type)) {
            throw new ServiceExceptionSpec(ExceptionEnum.USER_BASE_PARAMS_ILLEGAL);
        }
        //查询所有的数据
        List<OjkDataTotal> allData = ojkDataTotalDao.listData(type);

        if (CollectionUtils.isEmpty(allData)) {
            log.info(" not found this type data ,type is {}", type);
            return null;
        }
        //将数据进行分类整合
        Map<Integer, List<OjkDataTotal>> ojkDataMap = allData.stream().collect(Collectors.groupingBy(OjkDataTotal::getIdCode));

        return setResultOjkData(ojkDataMap, type);

    }

    private List<Object> setResultOjkData (Map<Integer, List<OjkDataTotal>> ojkDataMap, String type) {

        //所有其他库中没有字段集合
        Map<Object , Object> configValue = DefaultOjkValue.setMapValue();
        List<Object> result = new ArrayList<>();
        //通过类型进行反射获得class
        try {
            Class clazz = Class.forName(type);
            for (Integer idCode : ojkDataMap.keySet()) {
                Object obj = clazz.newInstance();
                List<OjkDataTotal> lists = ojkDataMap.get(idCode);
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Optional<OjkDataTotal> optional = lists.stream()
                            .filter(elem -> elem.getOjkKey().equals(fieldName)).findFirst();
                    //有值则存，无值则从map中取
                    if (optional.isPresent()) {
                        field.set(obj, objTransforType(field, optional.get().getOjkValue()));
                    } else {
                        field.set(obj, objTransforType(field, configValue.get(fieldName).toString()));
                    }
                }
                Optional<Field> superObj = Arrays.asList(clazz.getSuperclass().getDeclaredFields()).stream().findFirst();
                if (superObj.isPresent()) {
                    Field superField = superObj.get();
                    superField.setAccessible(true);

                    superField.set(obj, idCode);
                }
                result.add(obj) ;

            }
        } catch (Exception e) {
            log.error("className is error!{}", e);
        }
        return result;
    }

    private Object objTransforType(Field field, String value) {

        if (field.getType().getName().contains("Integer")) {
            return Integer.valueOf(value);
        } else if (field.getType().getName().contains("BigDecimal")) {
            return BigDecimal.valueOf(Double.valueOf(value));
        } else if (field.getType().getName().contains("Date")) {
            try {

                SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd");
                return df.parse(value);
            } catch (ParseException e) {
                log.error("objTransForType is error", e);
            }
        }
        return value;
    }




    /**
     *  第三方服务 数据
     * */
    public String getRegistUserData(){

        Map<String,Object> dataMap = new HashMap<>();
//        TODO: 字段名待确认
        dataMap.put("id_penyelenggara",""); // 企业id（ojk提供)
        // 机构名称  类型 所在地 由后台录入
        dataMap.put("nama_layanan_pendukung",""); // 机构名称
        dataMap.put("jenis_layanan_pendukung",""); // 机构类型
        dataMap.put("domisili_layanan_pendukung",""); // 机构所在地
        dataMap.put("periode", DateUtils.DateToString(new Date())); // 提交报告的时间 每个市为YYYY-MM-DD

        String str =  JsonUtils.serialize(dataMap);
        return AESUtils.encryptJsonData(str,this.ojkConfig);
    }

    /**
     * 获得数据库中有数据的类字符串
     * @param type
     * @return
     * @throws ServiceExceptionSpec
     */
    public String setOjkServerData(String type) throws Exception {

       List<Object> lists  = this.listOjkData(type);


       String str = JsonUtils.serialize(lists);
       return AESUtils.encryptJsonData(str,this.ojkConfig);
    }

    public int deleteOjkData(Integer idCode) {

        if (idCode != null && !idCode.equals(0)) {
           return ojkDataTotalDao.deleteOjkData(idCode);
        }
        return 0;
    }


//    /**
//     *  注册用户信息 数据
//     * */
//    public String getThirdServerData(){
//
//        ArrayList<Map<String,Object>> dataArray = new ArrayList<>();
////        TODO: 字段名待确认
//
//        Date nowDate = new Date();
//        List<UsrUser> userList = this.usrDao.getAllRegistUser();
//        for (UsrUser user : userList){
//            Map<String,Object> dataMap = new HashMap<>();
//            dataMap.put("id_penyelenggara",""); // 企业id（ojk提供)
//            // 用户 id，身份，类型，注册时间，姓名，身份证号，税卡号（NPWP)，上传时间
//            dataMap.put("kode_pengguna",2); // 身份（借款人/出借人)  1. Lender, 2. Borrower
//            dataMap.put("jenis_pengguna",1); // 用户类型（个人/企业） 1. People, 2. Legal Entity
//            dataMap.put("id_pengguna",user.getUuid()); // 用户id
//            dataMap.put("tgl_registrasi", user.getCreateTime()); // 注册时间 YYYY-MM-DD
//            dataMap.put("nama_pengguna",user.getRealName()); // 姓名
//            dataMap.put("no_ktp", user.getIdCardNo()); // 身份证号
//
//            dataMap.put("no_npwp", DateUtils.DateToString(new Date())); // 税卡号（NPWP) 不能有。或者-
//
//            dataMap.put("periode", DateUtils.DateToString(nowDate)）; // 上传时间 YYYY-MM-DD
//            dataArray.add(dataMap);
//        }
//        String str =  JsonUtils.serialize(dataArray);
//        return AESUtils.encryptJsonData(str,this.ojkConfig);
//    }




}
