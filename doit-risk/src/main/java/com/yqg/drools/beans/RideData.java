package com.yqg.drools.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yqg.drools.utils.DateUtil;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.xpath.operations.Bool;
import org.springframework.util.StringUtils;

/*****
 * @Author zengxiangcai
 * created at ${date}
 * @email zengxiangcai@yishufu.com
 * gojek骑行信息数据结构
 *
 ****/

@Getter
@Setter
public class RideData {

    @JsonProperty(value = "rides_history")
    private List<RideHistoryData> ridesHistory;

    @JsonProperty(value = "person_basic_info")
    private List<PersonBasicInfo> personBasicInfo;


    @Getter
    @Setter
    public static class RideHistoryData {

        private String time;
        private String distance;
        private BigDecimal fare;

        @JsonProperty(value = "pick_up")
        private String pickUp;//上车地点
        @JsonProperty(value = "drop_off")
        private String dropOff;//下车地点
        @JsonProperty(value = "taxi_type")
        private String taxiType;//乘车类型
        @JsonProperty(value = "payment_method")
        private String paymentMethod;//支付方式

        private Boolean success;

        public Date getRideDate() {
            if (StringUtils.isEmpty(time)) {
                return null;
            }
            return DateUtil.stringToDate(time, DateUtil.FMT_DDMMYYYY);
        }

        public BigDecimal getRideDistance() {
            if (StringUtils.isEmpty(distance)) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(distance);
        }

        public RideAddress getPickUpAddress() {
            return analyzeRideAddress(pickUp);
        }


        public RideAddress getDropOffAddress() {
            return analyzeRideAddress(dropOff);
        }

        private RideAddress analyzeRideAddress(String addr) {
            if (StringUtils.isEmpty(addr)) {
                return null;
            }
            String[] pickUpAddress = addr.split(",");
            RideAddress address = new RideAddress();
            String reg = "\\d+";
            int addressLength = pickUpAddress.length;
            if (addressLength >= 6) {

                address.setCountry(pickUpAddress[addressLength - 1]);
                address.setProvince(pickUpAddress[addressLength - 2].replace(reg, ""));
                address.setCity(pickUpAddress[addressLength - 3]);
                address.setBigDirect(pickUpAddress[addressLength - 4]);
                address.setSmallDirect(pickUpAddress[addressLength - 5]);

                //详细地址
                String[] detailAddressArray = Arrays
                    .copyOf(pickUpAddress, addressLength - 5);
                address.setDetail(String.join(" ", detailAddressArray));
            }
            return address;
        }


    }


    @Getter
    @Setter
    public static class RideAddress {

        private String country;
        private String province;
        private String city;
        private String bigDirect;
        private String smallDirect;
        private String detail;
    }

    @Getter
    public enum TaxiType {
        BLUE_BIRD("blue-bird"),
        GOLDEN("golden"),
        SILVER("silver");

        TaxiType(String typeName) {
            this.typeName = typeName;
        }

        private String typeName;
    }

    public enum PaymentMethodTypeEnum {
        Cash;
    }

    @Getter
    @Setter
    public static class PersonBasicInfo {

        private String phone;
        private String email;
    }

}
