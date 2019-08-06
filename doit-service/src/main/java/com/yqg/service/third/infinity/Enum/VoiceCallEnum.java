package com.yqg.service.third.infinity.Enum;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by tonggen on 2018/8/30.
 */
public enum VoiceCallEnum {

    NORMAL_DOWN (10001,	"正常挂断"),
    CALL_CANCLE(10002	,"呼叫取消"),
    NOT_RESPONSE(10003	,"被叫拒接"),
    CALL_ERROR(10004	,"外呼通道线路失败"),
    USER_NOT_RESPONSE(10005	,"用户超时未接听"),
    USER_BUSY(10006	,"用户忙"),
    SERVER_ERROR(10007	,"服务器端挂断"),
    SIP_NOT_LOGIN(10008	,"SIP分机未注册"),
    NOT_AVIBLEIYT(10009	,"目标不可达"),
    SIP_DOWN(10010	,"SIP分机拒接"),
    TIME_OUT(10011	,"定时器超时"),
    CALL_SERVICE_ERROR(10012	,"呼入时回调接口错误"),
    SIP_NOT_EXITS(10013	,"SIP分机不存在"),
    CALL_TURN_OFF(10014	,"主叫正常挂机"),
    BECALLED_TURN_OFF(10015	,"被叫正常挂机"),
    SERVICE_NOT_RESPONSE(10016	,"服务器拒绝"),
    CALL_PASS_DOWN(10017	,"外呼通道线路失败被挂断"),
    CANCLE_CALL(10018	,"主叫取消呼叫"),
    CANCLE_DIS_CALL(10019	,"线路取消呼叫"),
    OTHERS(10020	,"其它原因");


    private int code;
    private String message;
//    private String messageInn;

//    CallReusltEnum(int code, String message) {
//        this.code=code;
//        this.message=message;
//    }

    VoiceCallEnum(int code, String message) {
        this.code=code;
        this.message=message;
//        this.messageInn=messageInn;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

//    public static VoiceCallEnum valueOfVoiceCallEnum(int code){
//        List<VoiceCallEnum> allValues = Arrays.asList(VoiceCallEnum.values());
//        Optional<VoiceCallEnum> enumOptional = allValues.stream()
//                .filter(elem -> elem.getCode() == code).findFirst();
//        if (enumOptional.isPresent()) {
//            return enumOptional.get();
//        }
//        return null;
//    }
}
