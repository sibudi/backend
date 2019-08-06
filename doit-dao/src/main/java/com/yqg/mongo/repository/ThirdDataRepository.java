package com.yqg.mongo.repository;

import com.yqg.common.enums.system.ThirdDataTypeEnum;
import com.yqg.common.utils.StringUtils;
import com.yqg.mongo.dao.OrderThirdDataDal;
import com.yqg.mongo.entity.OrderThirdDataMongo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class ThirdDataRepository {
    @Autowired
    private OrderThirdDataDal orderThirdDataDal;

    public Optional<OrderThirdDataMongo> getLatestDataByType(String userUuid, String orderNo,ThirdDataTypeEnum typeEnum) {
        if (StringUtils.isEmpty(orderNo) && StringUtils.isEmpty(userUuid)) {
            return Optional.empty();
        }
        OrderThirdDataMongo searchParam = new OrderThirdDataMongo();

        if (StringUtils.isNotEmpty(orderNo)) {
            searchParam.setOrderNo(orderNo);
        }
        if (StringUtils.isNotEmpty(userUuid)) {
            searchParam.setUserUuid(userUuid);
        }

        searchParam.setThirdType(typeEnum.getType());
        List<OrderThirdDataMongo> thirdDataList = orderThirdDataDal.find(searchParam);
        if (CollectionUtils.isEmpty(thirdDataList)) {
            return Optional.empty();
        }
        Optional<OrderThirdDataMongo> maxData =
                thirdDataList.stream().filter(elem -> elem.getCreateTime() != null).max(Comparator.comparing(OrderThirdDataMongo::getUpdateTime));

        if (maxData.isPresent()) {
            return maxData;
        }
        return Optional.of(thirdDataList.get(0));
    }

    @Getter
    @Setter
    public static class EKYCReturn{
        private Long timestamp;
        private Integer status;
        private String error;
        private String message;
        private DataDetail data;

        @Getter
        @Setter
        public static class DataDetail{
            private Boolean name;
            private Boolean birthplace;
            private Boolean birthdate;
            private String address;
        }
    }
}
