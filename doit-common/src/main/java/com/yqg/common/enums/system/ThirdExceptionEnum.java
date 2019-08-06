package com.yqg.common.enums.system;

import java.text.MessageFormat;

/**
 * @author Jacob
 */
public enum ThirdExceptionEnum {

    /**
     * 调用App.Sip服务，POST请求,返回状态码说明
     */
    //POST请求成功
    INFINITY_SIP_200(200, "Permintaan POST berhasil！"),
    //非法请求（一般为参数不合法）
    INFINITY_SIP_400(400, "Permohonan ilegal（一般为参数不合法）"),
    //服务器错误
    INFINITY_SIP_500(500, "Kesalahan server"),
    //token无效，请重新登陆验证
    INFINITY_SIP_600(600, "Token tidak valid, silakan masuk kembali"),
    //appid未授权，请检查appid或联系云呼科技是否授权
    INFINITY_SIP_601(601, "Appid belum mendapat izin, silakan periksa ulang atau hubungi system cloud untuk pengesahan"),
    //appid授权已到期
    INFINITY_SIP_602(602, "Izin appid telah kadaluarsa"),
    //模块未授权
    INFINITY_SIP_603(603, "Modul tidak resmi"),
    //非法IP访问
    INFINITY_SIP_604(604, "Mengunjungi IP ilegal"),

    /**
     * 调用App.Sip服务，POST请求成功,但返回数据失败说明
     */
    // POST请求成功，获取数据成功
    INFINITY_SIP_RESULT_0(0, "Permintaan POST berhasil,berhasil mendapatkan data"),
    // POST请求成功，获取数据失败
    INFINITY_SIP_RESULT_1(1, "Permintaan POST berhasil,gagal mendapatkan data"),
    // 服务器连接失败
    INFINITY_SIP_RESULT_1001(1001, "Gagal menyambung ke server"),
    // 操作异常（一般为校验异常等）
    INFINITY_SIP_RESULT_1002(1002, "Operasi tidak normal（一般为校验异常等）"),
    INFINITY_SIP_RESULT_1003(1003, "操作失败(一般是授权失败、注销失败、命令发送失败、服务器连接异常等)"),
    // 分机异常(可能是新加的分机，需要重新登陆获取新token)
    INFINITY_SIP_RESULT_1010(1010, "Extension tidak normal(可能是新加的分机，需要重新登陆获取新token)"),
    // 非法分机(非本公司所有)
    INFINITY_SIP_RESULT_1011(1011, "= Extension ilegal(本公司没有此分机号)"),
    // 分机不存在
    INFINITY_SIP_RESULT_1012(1012, "Extension tidak ditemukan"),
    // 分机已停用
    INFINITY_SIP_RESULT_1013(1013, "Extension sudah berhenti digunakan"),
    // 分机未注册
    INFINITY_SIP_RESULT_1014(1014, "Extension belum didaftarkan"),
    // 分机不在通话中
    INFINITY_SIP_RESULT_1015(1015, "Extension sedang tidak dalam panggilan"),
    // 分机已启用
    INFINITY_SIP_RESULT_1016(1016, "Extension sudah mulai digunakan"),
    // 分机已注册
    INFINITY_SIP_RESULT_1017(1017, "Extension sudah didaftarkan"),
    // 号码已启用
    INFINITY_SIP_RESULT_1018(1018, "Nomor sudah digunakan"),
    // 号码已禁用
    INFINITY_SIP_RESULT_1019(1019, "Nomor sudah dilarang"),
    // 进行中
    INFINITY_SIP_RESULT_1020(1020, "Sedang berlangsung"),
    // 暂停
    INFINITY_SIP_RESULT_1021(1021, "Pause"),
    // 已结束
    INFINITY_SIP_RESULT_1022(1022, "Selesai"),
    // 文件不存在
    INFINITY_SIP_RESULT_1023(1023, "Dokumen tidak ditemukan"),

    INFINITY_SIP_RESULT_1202(1202,"振铃"),
    INFINITY_SIP_RESULT_1203(1203,"摘机"),
    INFINITY_SIP_RESULT_1204(1204,"通话中"),
    INFINITY_SIP_RESULT_1210(1210,"队列异常"),
    INFINITY_SIP_RESULT_1211(1211,"非法队列\t非本公司所有"),
    INFINITY_SIP_RESULT_1212(1212,"已分配坐席未接听"),
    INFINITY_SIP_RESULT_1213(1213,"已接听"),
    INFINITY_SIP_RESULT_1214(1213,"等待分配给坐席"),
    INFINITY_SIP_RESULT_1215(1215,"坐席拒接"),
    INFINITY_SIP_RESULT_1216(1216,"其它"),

    /**
     * 异常处理提示
     */
    // 缺少必要参数
    NO_PARAMETERS(10022, "kekurangan parameter yang diperlukan  "),
    // 分机号不存在
    INFINITY_EXTNUMBER_NO(10031, "Extension tidak ditemukan"),

    ;
    private int code;
    private String message;
    private String temlate;

    private ThirdExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private ThirdExceptionEnum(int code, String message, String template) {
        this.code = code;
        this.message = message;
        this.temlate = template;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ThirdExceptionEnum setCustomMessage(String message) {
        this.setMessage(message);
        return this;
    }

    /**
     * ???????????
     *
     * @param ags
     * @return
     */
    public ThirdExceptionEnum setDynamicMessage(Object... ags) {
        String dynamicMsg = MessageFormat.format(this.temlate, ags);
        this.setMessage(dynamicMsg);
        return this;
    }
}
