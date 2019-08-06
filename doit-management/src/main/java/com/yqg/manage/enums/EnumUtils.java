package com.yqg.manage.enums;

import com.yqg.common.enums.order.OrdStateEnum;
import com.yqg.service.third.Inforbip.Enum.CallReusltEnum;
import com.yqg.user.entity.UsrFeedBack;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/*****
 * @Author Jeremy Lawrence
 * created at ${date}
 *
 ****/
public class EnumUtils {

    public static OrdStateEnum valueOfOrderStatus(int code){
        List<OrdStateEnum> allValues = Arrays.asList(OrdStateEnum.values());
        Optional<OrdStateEnum> enumOptional = allValues.stream()
            .filter(elem -> elem.getCode() == code).findFirst();
        if (enumOptional.isPresent()) {
            return enumOptional.get();
        }
        return null;
    }

    public static ManOrderCheckRemarkEnum valueOfManOrderCheckRemarkEnum(int code){
        List<ManOrderCheckRemarkEnum> allValues = Arrays.asList(ManOrderCheckRemarkEnum.values());
        Optional<ManOrderCheckRemarkEnum> enumOptional = allValues.stream()
                .filter(elem -> elem.getType() == code).findFirst();
        if (enumOptional.isPresent()) {
            return enumOptional.get();
        }
        return null;
    }

    public static OperationTypeEnum valueOfOperationTypeEnum(int code){
        List<OperationTypeEnum> allValues = Arrays.asList(OperationTypeEnum.values());
        Optional<OperationTypeEnum> enumOptional = allValues.stream()
                .filter(elem -> elem.getType() == code).findFirst();
        if (enumOptional.isPresent()) {
            return enumOptional.get();
        }
        return null;
    }
    public static ManWorkYearEnum valueOfManWorkYearEnum(int code){
        List<ManWorkYearEnum> allValues = Arrays.asList(ManWorkYearEnum.values());
        Optional<ManWorkYearEnum> enumOptional = allValues.stream()
                .filter(elem -> elem.getType() == code).findFirst();
        if (enumOptional.isPresent()) {
            return enumOptional.get();
        }
        return null;
    }

    public static TeleReviewOperationTypeEnum valueOfTeleReviewOperationTypeEnum(int code){
        List<TeleReviewOperationTypeEnum> allValues = Arrays.asList(TeleReviewOperationTypeEnum.values());
        Optional<TeleReviewOperationTypeEnum> enumOptional = allValues.stream()
                .filter(elem -> elem.getType() == code).findFirst();
        if (enumOptional.isPresent()) {
            return enumOptional.get();
        }
        return null;
    }

    public static CallReusltEnum valueOfCallResultEnum(int code){
        List<CallReusltEnum> allValues = Arrays.asList(CallReusltEnum.values());
        Optional<CallReusltEnum> enumOptional = allValues.stream()
                .filter(elem -> elem.getCode() == code).findFirst();
        if (enumOptional.isPresent()) {
            return enumOptional.get();
        }
        return null;
    }

    public static UsrFeedBack.ResolutionEnum valueOfResolutionEnum(int code){
        List<UsrFeedBack.ResolutionEnum> allValues = Arrays.asList(UsrFeedBack.ResolutionEnum.values());
        Optional<UsrFeedBack.ResolutionEnum> enumOptional = allValues.stream()
                .filter(elem -> elem.getCode() == code).findFirst();
        if (enumOptional.isPresent()) {
            return enumOptional.get();
        }
        return null;
    }
}
