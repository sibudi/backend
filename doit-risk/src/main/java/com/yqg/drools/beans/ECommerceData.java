package com.yqg.drools.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.common.utils.StringUtils;
import com.yqg.drools.utils.DateUtil;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 ****/

@Setter
@Getter
@Slf4j
public class ECommerceData {

    @JsonProperty(value = "address_book")
    private List<AddressBook> addressBook;

    @JsonProperty(value = "person_basic_info")
    private List<PersonBasicInfo> personBasicInfo;

    private List<OrderInfo> order;

    @Setter
    @Getter
    public static class AddressBook {

        private String county;
        private String province;
        private String city;
        private String address;
        @JsonProperty(value = "ship_freq")
        private Integer shipFreq;//购物频次
        private String phone;//收货人手机号
        private String name;//收货人姓名
    }

    @Setter
    @Getter
    public static class  PersonBasicInfo {

        private String source;//数据来源
        private String name;//姓名
        private String phone;//手机号
        private String email;//邮箱
        @JsonProperty(value = "acct_type")
        private String accountType;//账号类型
        @JsonProperty(value = "acct_create_date")
        private String accountCreateDate;//账号注册时间

        public Date getCreateDate(){
            if(StringUtils.isEmpty(accountCreateDate)){
                return null;
            }
            return DateUtil.stringToDate(accountCreateDate,DateUtil.FMT_DDMMYYYY);
        }
    }

    @Setter
    @Getter
    public static class OrderInfo {

        @JsonProperty(value = "shipping_fee")
        private BigDecimal shippingFee;//运费
        private String source;
        private String name;
        @JsonProperty(value = "grand_total")
        private BigDecimal grandTotal;//总计金额
        @JsonProperty(value = "payment_method")
        private String paymentMethod;//付款方式
        private String currency;//货币单位
        @JsonProperty(value = "create_time")
        private String createTime;//订单时间
        private Boolean success;

        public Date getOrderCreateTime() {
            if (StringUtils.isEmpty(createTime)) {
                log.warn("tokopedia order createTime is empty");
                return null;
            }
            return DateUtil.stringToDate(createTime, DateUtil.FMT_DDMMYYYY);
        }
    }
}
