package com.yqg.service.externalChannel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yqg.common.constants.SysParamContants;
import com.yqg.common.utils.CheakTeleUtils;
import com.yqg.common.utils.DESUtils;
import com.yqg.common.utils.StringUtils;
import com.yqg.order.dao.OrdDao;
import com.yqg.order.entity.OrdOrder;
import com.yqg.service.externalChannel.config.Cash2Config;
import com.yqg.service.externalChannel.request.Cash2AdditionalInfoParam;
import com.yqg.service.externalChannel.request.Cash2ApiParam;
import com.yqg.service.externalChannel.request.Cash2GetDetailProductInfoRequest;
import com.yqg.service.externalChannel.request.Cash2GetProductConfigRequest;
import com.yqg.service.externalChannel.response.Cash2Response;
import com.yqg.service.externalChannel.utils.Cash2ResponseBuiler;
import com.yqg.service.externalChannel.utils.Cash2ResponseCode;
import com.yqg.service.externalChannel.utils.SendDataBuiler;
import com.yqg.service.system.service.SysParamService;
import com.yqg.service.user.service.UsrService;
import com.yqg.system.dao.SysProductDao;
import com.yqg.system.entity.SysProduct;
import com.yqg.user.entity.UsrUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by wanghuaizhou on 2019/1/4.
 */
@Service
@Slf4j
public class Cash2ProductService {

    @Autowired
    private UsrService usrService;
    @Autowired
    private SysProductDao sysProductDao;
    @Autowired
    private SysParamService sysParamService;
    @Autowired
    private OrdDao orderDao;
    @Autowired
    private Cash2Config cash2Config;

    /***
     * 获取产品列表
     * @param request
     * @return
     */
    public Cash2Response getProductConfig(Cash2GetProductConfigRequest request) {
        if (StringUtils.isEmpty(request.getMobile())) {
            return Cash2ResponseBuiler.buildResponse(
                    Cash2ResponseCode.PARAM_EMPTY_1001);
        }

        String tempPhone = CheakTeleUtils.telephoneNumberValid2(request.getMobile());
        if (StringUtils.isEmpty(tempPhone)) {
            return Cash2ResponseBuiler.buildResponse(
                    Cash2ResponseCode.DATA_VERIFY_ERROR);
        }

        List<SysProduct> list = getUserProductList(tempPhone);
        if(CollectionUtils.isEmpty(list)){
            return Cash2ResponseBuiler.buildResponse(
                    Cash2ResponseCode.DATA_VERIFY_ERROR);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("term_unit", 1);  //借款期限单位 	1：天，2：月

        List<BigDecimal> amountList = new ArrayList<>();


        for (SysProduct product: list){

            if (!amountList.contains(product.getBorrowingAmount())){
                amountList.add(product.getBorrowingAmount());
            }
        }

        Map<String,List<Integer>> map = new HashMap<>();

        for (BigDecimal amount:amountList){
            List<Integer> termList = new ArrayList<>();

            for (SysProduct product: list) {

                if (product.getBorrowingAmount().compareTo(amount) == 0){
                    termList.add(product.getBorrowingTerm());
                }
            }

            map.put(amount+"",termList);

        }
        data.put("loan_range", map);  //借款范围

        return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.CODE_OK_1).withData(data);
    }


    /***
     * 获取产品列表
     * @param request
     * @return
     */
    public Cash2Response getDetailProductInfo(Cash2GetDetailProductInfoRequest request) {
        if (StringUtils.isEmpty(request.getMobile())) {
            return Cash2ResponseBuiler.buildResponse(
                    Cash2ResponseCode.PARAM_EMPTY_1001);
        }

        String tempPhone = CheakTeleUtils.telephoneNumberValid2(request.getMobile());
        if (StringUtils.isEmpty(tempPhone)) {
            return Cash2ResponseBuiler.buildResponse(
                    Cash2ResponseCode.DATA_VERIFY_ERROR);
        }

        List<SysProduct> list = getUserProductList(tempPhone);
        if(CollectionUtils.isEmpty(list)){
            return Cash2ResponseBuiler.buildResponse(
                    Cash2ResponseCode.DATA_VERIFY_ERROR);
        }

        Map<String, Object> data = new HashMap<>();
        for (SysProduct product: list){

           if ((request.getApplicationAmount().compareTo(product.getBorrowingAmount()) == 0 ) &&
                   request.getApplicationTerm() == product.getBorrowingTerm()){
               data.put("interest_amount",product.getInterest()); // 利息
               data.put("admin_amount",product.getDueFee()); // 管理费
               data.put("actual_amount",product.getBorrowingAmount().subtract(product.getDueFee())); // 实际到账金额
               data.put("repay_total_amount",product.getBorrowingAmount().add(product.getInterest())); // 总还款额
           }
        }

        if (data.get("admin_amount") == null){
            return Cash2ResponseBuiler.buildResponse(
                    Cash2ResponseCode.ORDER_PRODUCT_CONFIG_IS_NULL);
        }

        return Cash2ResponseBuiler.buildResponse(
                Cash2ResponseCode.CODE_OK_1).withData(data);
    }


    /**
     *    根据用户手机号 获取产品列表
     * */
    public List<SysProduct> getUserProductList(String userMobile){

/**
 *     20190514 fixbug : cashcash请求的手机号可能带0开始，这时候在doit数据库中查不到对应的手机号
 * */
        if(userMobile.substring(0,1).equals("0")){
            userMobile = userMobile.substring(1,userMobile.length());
        }
        UsrUser searchInfo = new UsrUser();
        searchInfo.setMobileNumberDES(DESUtils.encrypt(userMobile));
        List<UsrUser> userList = usrService.getUserInfo(searchInfo);

        List<SysProduct> list = null;

        if (CollectionUtils.isEmpty(userList)) {
            //新用户
            list = sysProductDao.getProductWithProductLevelAndUserLevel(2,0,"0.192");
        }else {
            // 老用户

            UsrUser user = userList.get(0);
            int level = 0;
            String sysParamValue = this.sysParamService.getSysParamValue(SysParamContants.PRODUCT_LEVEL_OFF_NO);
            if (!StringUtils.isEmpty(sysParamValue) && Integer.valueOf(sysParamValue) == 1) {
                level = user.getProductLevel();
            }

            /**
             *   cashcash订单 代还款用户也会调用这里的接口 优化
             * */
            // 判断借款次数
            String duefeeRate = "0.192";

            // 判断是否是降额用户
            List<OrdOrder> hasLowLoanList = this.orderDao.has10WOR20WOR40WOR80WLoan(user.getUuid());
            if(hasLowLoanList.size() > 0){
                //
                if (level <= -4){
                    //如果是降额用户  则只能再借降额产品
                    list = sysProductDao.getProductWithProductLevel(level,duefeeRate);
                }else {
                    //如果是降额用户  则只能再借40w产品   可能有提额用户 level = -4 则可借 40w和80w
                    list = sysProductDao.getProductWithProductLevelAndUserLevel(-3,level,duefeeRate);
                }
            }else {
                if (level < 0){
                    list = sysProductDao.getProductWithProductLevel(level,duefeeRate);
                }else {
                    // 如果不是降额  则可以借120w产品
                    list = sysProductDao.getProductWithProductLevelAndUserLevel(2,level,duefeeRate);
                }
            }
        }
        return list;
    }

    public void test() throws  Exception{

        String jasonString = "{\n" +
                "  \"add_info\": {\n" +
                "    \"device\": {\n" +
                "      \"deviceid\": \"359911060475626\",\n" +
                "      \"dns\": \"192.168.39.28\",\n" +
                "      \"android_id\": \"1efe87cc9673e02e\",\n" +
                "      \"uuid\": \"ffffffff-ddf3-e489-ba8b-64b02bdea192\",\n" +
                "      \"imei\": \"359911060475626\",\n" +
                "      \"mac\": \"02:00:00:00:00:00\",\n" +
                "      \"network_type\": \"NETWORK_4G\",\n" +
                "      \"locale_iso_3_language\": \"in\",\n" +
                "      \"imsi\": \"510102962173291\",\n" +
                "      \"longitude\": 108.22244128,\n" +
                "      \"latitude\": -7.1272216,\n" +
                "      \"order_gps_address_province\": \"Jawa Barat\",\n" +
                "      \"order_gps_address_city\": \"Kabupaten Ciamis\",\n" +
                "      \"order_gps_address_large_district\": \"Panumbangan\",\n" +
                "      \"order_gps_address_small_district\": \"\",\n" +
                "      \"order_gps_address_street\": \"Panumbangan, Sindangherang, Panumbangan, Kabupaten Ciamis, Jawa Barat 46263, Indonesia\",\n" +
                "      \"ip\": \"114.124.150.198\",\n" +
                "      \"memory_size\": \"2.73GB\",\n" +
                "      \"internal_storage_total\": \"21.93GB\",\n" +
                "      \"internal_storage_usable\": \"8.14GB\",\n" +
                "      \"memory_card_size\": \"2.73GB\",\n" +
                "      \"memory_card_size_use\": \"625.72MB\",\n" +
                "      \"ram_usable_size\": \"625.72MB\",\n" +
                "      \"root_jailbreak\": 0,\n" +
                "      \"simulator\": 0,\n" +
                "      \"image_num\": 2152,\n" +
                "      \"last_boot_time\": 1553168909,\n" +
                "      \"brand\": \"docomo\",\n" +
                "      \"cpu_type\": \"AArch64 Processor rev 2 (aarch64)\",\n" +
                "      \"device_name\": \"SO-03H\",\n" +
                "      \"device_model\": \"docomo\",\n" +
                "      \"system_version\": \"7.0\",\n" +
                "      \"battery\": 83,\n" +
                "      \"app_data\": [\n" +
                "        {\n" +
                "          \"appName\": \"ドコモ音声入力\",\n" +
                "          \"package\": \"com.nttdocomo.android.dcmvoicerecognition\",\n" +
                "          \"in_time\": 1504875196000,\n" +
                "          \"up_time\": 1504875196000\n" +
                "        },\n" +
                "        {\n" +
                "          \"appName\": \"Efek kreatif\",\n" +
                "          \"package\": \"com.sonyericsson.android.addoncamera.artfilter\",\n" +
                "          \"in_time\": 1504875196000,\n" +
                "          \"up_time\": 1504875196000\n" +
                "        }\n" +
                "      ],\n" +
                "      \"contact_data\": [\n" +
                "        {\n" +
                "          \"name\": \"Ali\",\n" +
                "          \"number\": \"0892-7109-3343\",\n" +
                "          \"up_time\": \"1552862382920\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"name\": \"0898-6937-068\",\n" +
                "          \"number\": \"08986937068\",\n" +
                "          \"up_time\": \"1553171381292\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"tel_data\": [\n" +
                "        \n" +
                "      ],\n" +
                "      \"sms_data\": [\n" +
                "        \n" +
                "      ],\n" +
                "      \"operator_data\": {\n" +
                "        \"name\": \"51010\"\n" +
                "      },\n" +
                "      \"wifi_data\": [\n" +
                "        \n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"apply_detail\": {\n" +
                "    \"user_type\": 1,\n" +
                "    \"idcard_image_front\": \"http://cdnoss.yobit.id/cashcash/loan/idcard/2019-03-22/loan-e33af8d2b742befab0b1ee5bc3a8457315532128816251553212881626-9286-1553212881625-148118886.jpg\",\n" +
                "    \"idcard_image_reverse_side\": \"http://cdnoss.yobit.id/cashcash/loan/idcard/2019-03-22/loan-e33af8d2b742befab0b1ee5bc3a8457315532129208961553212881628-6740-1553212881625-148118886.jpg\",\n" +
                "    \"idcard_image_hand\": \"http://cdnoss.yobit.id/cashcash/loan/idcard/2019-03-22/loan-e33af8d2b742befab0b1ee5bc3a8457315532131905961553212881631-5416-1553212881625-148118886.jpg\",\n" +
                "    \"marital_status\": \"1\",\n" +
                "    \"user_sex\": \"2\",\n" +
                "    \"education\": \"3\",\n" +
                "    \"loan_remark\": \"9\",\n" +
                "    \"month_income\": \"1500000\",\n" +
                "    \"religion\": \"1\",\n" +
                "    \"children\": \"2\"\n" +
                "  },\n" +
                "  \"is_reloan\": \"1\",\n" +
                "  \"order_info\": {\n" +
                "    \"order_no\": \"18071414585235605533\",\n" +
                "    \"application_amount\": 400000,\n" +
                "    \"application_term\": 30,\n" +
                "    \"term_unit\": 1,\n" +
                "    \"order_time\": 1555750336,\n" +
                "    \"status\": 80,\n" +
                "    \"product_id\": \"10000\",\n" +
                "    \"product_name\": \"Do-It\",\n" +
                "    \"user_name\": \"Emah Rohemah Hidayat\",\n" +
                "    \"user_mobile\": \"082129173291\",\n" +
                "    \"user_idcard\": \"3207076612820001\"\n" +
                "  }\n" +
                "}";

        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> map = mapper.readValue(jasonString, Map.class);


        // 加密请求
        Cash2ApiParam sendData = new SendDataBuiler().buildParam(map,cash2Config);
        log.info(sendData+"");
    }

    public static void main(String[] args) {
//        String mobile = "082194052168";
//        if(mobile.substring(0,1).equals("0")){
//            mobile = mobile.substring(1,mobile.length());
//        }
//        log.info(mobile);

        String tempPhone = CheakTeleUtils.telephoneNumberValid2("082289488376");
        log.info(tempPhone);
    }
}
