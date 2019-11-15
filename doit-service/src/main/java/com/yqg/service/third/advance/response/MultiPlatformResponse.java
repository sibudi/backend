package com.yqg.service.third.advance.response;

import com.yqg.user.entity.UsrUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.util.List;

/***
 * {"code":"SUCCESS","message":"OK","data":{"records":[{"type":"PAYDAY","queryCount":"1","queryDates":["2019-05-05"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-05-05"]},{"type":"PAYDAY","queryCount":"2","queryDates":["2019-05-03","2019-04-26"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-05-02"]},{"type":"PAYDAY","queryCount":"2","queryDates":["2019-05-01","2018-10-03"]},{"type":"PAYDAY","queryCount":"4","queryDates":["2019-05-01","2019-02-17","2018-12-05","2018-10-05"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-04-29"]},{"type":"PAYDAY","queryCount":"10","queryDates":["2019-04-27","2019-04-13","2019-03-28","2019-03-13","2019-02-28","2019-02-12","2019-01-18","2019-01-01","2018-10-23","2018-10-22"]},{"type":"PAYDAY","queryCount":"2","queryDates":["2019-04-27","2019-02-26"]},{"type":"PRODUCT_INSTALLMENT","queryCount":"4","queryDates":["2019-04-27","2019-03-27","2018-12-30","2018-10-15"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-04-27"]},{"type":"PAYDAY","queryCount":"6","queryDates":["2019-04-25","2019-03-22","2019-02-26","2019-01-19","2018-12-31","2018-10-22"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-04-18"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-04-15"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-04-15"]},{"type":"PAYDAY","queryCount":"4","queryDates":["2019-04-13","2019-03-24","2019-03-02","2018-10-08"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-04-13"]},{"type":"PAYDAY","queryCount":"2","queryDates":["2019-04-12","2019-03-30"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-04-04"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-04-04"]},{"type":"PAYDAY","queryCount":"3","queryDates":["2019-04-04","2019-04-01","2018-12-29"]},{"type":"PAYDAY","queryCount":"2","queryDates":["2019-04-04","2019-02-28"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-04-04"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-04-02"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-04-01"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-03-28"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-03-28"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-03-23"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-03-23"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-03-21"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-03-19"]},{"type":"PRODUCT_INSTALLMENT","queryCount":"1","queryDates":["2019-03-19"]},{"type":"PAYDAY","queryCount":"5","queryDates":["2019-03-16","2019-03-01","2019-02-07","2018-12-29","2018-11-05"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-03-15"]},{"type":"PAYDAY","queryCount":"2","queryDates":["2019-03-05","2019-02-17"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-03-02"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-02-17"]},{"type":"PAYDAY","queryCount":"2","queryDates":["2019-01-23","2018-12-15"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2019-01-18"]},{"type":"PAYDAY","queryCount":"3","queryDates":["2018-12-31","2018-11-05","2018-11-04"]},{"type":"PAYDAY","queryCount":"2","queryDates":["2018-12-18","2018-10-01"]},{"type":"PAYDAY","queryCount":"1","queryDates":["2018-07-30"]}],"statistics":{"lastQueryTime":"2019-05-05 16:00-17:00","customerNumberList":[{"queryDate":"2019-05-05","customerNumber":2},{"queryDate":"2019-05-03","customerNumber":1},{"queryDate":"2019-05-02","customerNumber":1},{"queryDate":"2019-05-01","customerNumber":2},{"queryDate":"2019-04-29","customerNumber":1},{"queryDate":"2019-04-27","customerNumber":4},{"queryDate":"2019-04-26","customerNumber":1},{"queryDate":"2019-04-25","customerNumber":1},{"queryDate":"2019-04-18","customerNumber":1},{"queryDate":"2019-04-15","customerNumber":2},{"queryDate":"2019-04-13","customerNumber":3},{"queryDate":"2019-04-12","customerNumber":1},{"queryDate":"2019-04-04","customerNumber":5},{"queryDate":"2019-04-02","customerNumber":1},{"queryDate":"2019-04-01","customerNumber":2},{"queryDate":"2019-03-30","customerNumber":1},{"queryDate":"2019-03-28","customerNumber":3},{"queryDate":"2019-03-27","customerNumber":1},{"queryDate":"2019-03-24","customerNumber":1},{"queryDate":"2019-03-23","customerNumber":2},{"queryDate":"2019-03-22","customerNumber":1},{"queryDate":"2019-03-21","customerNumber":1},{"queryDate":"2019-03-19","customerNumber":2},{"queryDate":"2019-03-16","customerNumber":1},{"queryDate":"2019-03-15","customerNumber":1},{"queryDate":"2019-03-13","customerNumber":1},{"queryDate":"2019-03-05","customerNumber":1},{"queryDate":"2019-03-02","customerNumber":2},{"queryDate":"2019-03-01","customerNumber":1},{"queryDate":"2019-02-28","customerNumber":2},{"queryDate":"2019-02-26","customerNumber":2},{"queryDate":"2019-02-17","customerNumber":3},{"queryDate":"2019-02-12","customerNumber":1},{"queryDate":"2019-02-07","customerNumber":1},{"queryDate":"2019-01-23","customerNumber":1},{"queryDate":"2019-01-19","customerNumber":1},{"queryDate":"2019-01-18","customerNumber":2},{"queryDate":"2019-01-01","customerNumber":1},{"queryDate":"2018-12-31","customerNumber":2},{"queryDate":"2018-12-30","customerNumber":1},{"queryDate":"2018-12-29","customerNumber":2},{"queryDate":"2018-12-18","customerNumber":1},{"queryDate":"2018-12-15","customerNumber":1},{"queryDate":"2018-12-05","customerNumber":1},{"queryDate":"2018-11-05","customerNumber":2},{"queryDate":"2018-11-04","customerNumber":1},{"queryDate":"2018-10-23","customerNumber":1},{"queryDate":"2018-10-22","customerNumber":2},{"queryDate":"2018-10-15","customerNumber":1},{"queryDate":"2018-10-08","customerNumber":1},{"queryDate":"2018-10-05","customerNumber":1},{"queryDate":"2018-10-03","customerNumber":1},{"queryDate":"2018-10-01","customerNumber":1},{"queryDate":"2018-07-30","customerNumber":1}],"lastThreeDaysDetail":[{"queryTimeSlots":["16:00-17:00","11:00-12:00"],"queryDate":"2019-05-05","queryCount":2},{"queryTimeSlots":[],"queryDate":"2019-05-04","queryCount":0},{"queryTimeSlots":["14:00-15:00"],"queryDate":"2019-05-03","queryCount":1}],"statisticCustomerInfo":[{"timePeriod":"1-7d","queryCount":7},{"timePeriod":"1-14d","queryCount":12},{"timePeriod":"1-21d","queryCount":15},{"timePeriod":"1-30d","queryCount":18},{"timePeriod":"1-60d","queryCount":34},{"timePeriod":"1-90d","queryCount":37},{"timePeriod":"90+d","queryCount":13}],"totalCustomerNumber":42,"firstQueryTime":"2018-07-30 14:00-15:00","lastTwoWeeksQueryInfo":[{"queryTimeSlots":["2019-04-27 07:00-08:00"],"timeSlice":"1-HOUR","maxQueryCount":2},{"queryTimeSlots":["2019-04-27 06:00-09:00"],"timeSlice":"3-HOUR","maxQueryCount":2},{"queryTimeSlots":["2019-04-27 06:00-12:00"],"timeSlice":"6-HOUR","maxQueryCount":3},{"queryTimeSlots":["2019-04-27 00:00-12:00"],"timeSlice":"12-HOUR","maxQueryCount":3},{"queryTimeSlots":["2019-04-27 00:00-24:00"],"timeSlice":"24-HOUR","maxQueryCount":4}]}},"transactionId":"dad6400cfb250f51","pricingStrategy":"PAY","extra":null}
 *
 */
@Getter
@Setter
public class MultiPlatformResponse {
    private String code;
    private String message;
    private DataDetail data;
    private String transactionId;
    private String pricingStrategy;
    private Object extra;

    public boolean isHitReject(UsrUser user) {
        if (this.getData() == null) {
            return false;
        }
        if (this.getData().getStatistics() == null) {
            return false;
        }
        List<DataDetail.StatisticsDetail.StatisticCustomerInfoDetail> customerInfoList = this.getData().getStatistics().getStatisticCustomerInfo();
        if (CollectionUtils.isEmpty(customerInfoList)) {
            return false;
        }
        return customerInfoList.stream().filter(elem -> hitRejectRule(elem, user)).findFirst().isPresent();

    }

    private boolean hitRejectRule(DataDetail.StatisticsDetail.StatisticCustomerInfoDetail elem, UsrUser user) {
        if (user.getSex() == 1) {
            //男
            return "1-7d".equalsIgnoreCase(elem.getTimePeriod()) && elem.getQueryCount() != null && elem.getQueryCount() >= 11;
        } else {
            return "1-7d".equalsIgnoreCase(elem.getTimePeriod()) && elem.getQueryCount() != null && elem.getQueryCount() >= 10;
        }
    }

    @Getter
    @Setter
    public static class DataDetail {
        private List<RecordDetail> records;

        private StatisticsDetail statistics;

        @Getter
        @Setter
        public static class RecordDetail {
            private String type;
            private String queryCount;
            private List<String> queryDates;
        }

        @Getter
        @Setter
        public static class StatisticsDetail {
            private String firstQueryTime;
            private String lastQueryTime;
            private List<LastTwoWeeksQueryInfoDetail> lastTwoWeeksQueryInfo;
            private List<LastThreeDaysDetailInfo> lastThreeDaysDetail;
            private List<StatisticCustomerInfoDetail> statisticCustomerInfo;
            private List<CustomerNumberListDetail> customerNumberList;
            private Integer totalCustomerNumber;

            @Getter
            @Setter
            public static class LastTwoWeeksQueryInfoDetail {
                private String timeSlice;
                private Integer maxQueryCount;
                private List<String> queryTimeSlots;
            }

            @Getter
            @Setter
            public static class LastThreeDaysDetailInfo {
                private String queryDate;
                private Integer queryCount;
                private List<String> queryTimeSlots;
            }

            @Getter
            @Setter
            public static class StatisticCustomerInfoDetail {
                private String timePeriod;
                private Integer queryCount;
            }

            @Getter
            @Setter
            public static class CustomerNumberListDetail {
                private String queryDate;
                private Integer customerNumber;
            }
        }

    }
}
