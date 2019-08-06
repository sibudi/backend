package com.yqg.service.third.Inforbip.Enum;

/**
 * Created by wanghuaizhou on 2018/8/30.
 */
public enum CallReusltEnum {
    UNKNOWN_ERROR(1,"未知错误","Kesalahan yang tidak diketahui"),  // 未知错误
    CONNECT(2,"已接通","Tersambung"),  // 已接通
    BUSY(3,"通话中/忙","Dalam Panggilan/ sedang sibuk"),  // 通话中/忙
    NO_ANSWER(4,"无人接听","Tidak menjawab"),  // 无人接听
    ATTACHMENT_USELESS(5,"文档不可用","Dokumen tidak tersedia"),  // HTTP请求中指定的文件不可访问，无法下载
    ATTACHMENT_FORMATT_NOT_SUPPORT(6,"文件格式不支持","Format file tidak didukung"),  // 格式不支持（不支持指定文件的格式）
    REQUEST_FORMATT_NOT_SUPPORT(7,"请求格式不支持","Format permintaan tidak didukung"),  // 格式不正确（收到的请求被拒绝，因为格式不正确）
    ARREARS(8,"已欠费","Tunggakan"),  // 已欠费（服务器理解请求，但拒绝执行）
    NUMBER_NOT_EXIST(9,"号码不存在","nomor tidak ada"),  // 号码不存在
    NEED_VERIFY_USER_INFO(10,"需要验证用户信息","Perlu memverifikasi informasi pengguna"),  // 验证用户信息（该请求需要在运营商端进行用户身份验证）
    NO_SIGNAL(11,"无信号","Tidak ada sinyal"),  // 无信号（未能及时找到用户）
    NO_ARRIVE(12,"未到达","tidak tercapai"),  // 未到达（用户存在一次，但操作员不再支持目标地址）
    REQUEST_TOO_LARGE(13,"请求过大","Permintaan terlalu besar"),  // 请求过大（请求实体主体大于服务器愿意或能够处理的主体）
    REFUSE_TO_DEAL(14,"拒绝处理","Menolak untuk memproses"),  // 拒绝处理（服务器拒绝处理请求，因为Request-URI比服务器长）
    FORMATT_NOT_SUPPORT(15,"请求格式不支持","Format permintaan tidak didukung"),  // 格式不支持
    DOWN_TINE(16,"停机","Tidak Aktif"),// 停机
    INVALID_NUMBER(17,"号码无效","Nomor tersebut tidak valid"),// 号码无效
    USER_REFUSE(18,"拒接","Menolak"),// 拒接（请求已被取消按钮终止，最终用户拒绝接听语音呼叫）
    REQUEST_FORMATT_NOT_SUPPORT_IN_SERVER(19,"请求格式服务器不支持","Meminta server format tidak mendukung"),// 格式不支持（请求的格式在运营商端是不可接受的)
    REQUEST_NEED_DEAL_WITH(20,"待处理请求","Permintaan yang tertunda"),// 待处理请求（服务器具有来自同一对话框的一些待处理请求）
    REJECT_BY_OPERATOR(21,"接通-拒绝","Terangkat-Tolak"),// 接通-拒绝（同样的文本已经发送到目的地）
    SERVER_ERROR_IN(22,"服务器内部错误","Kesalahan internal server"),// 错误（服务器内部错误）
    NOT_REALIZE(23,"未实现","Tidak terwujud"),// 未实现（没有实现）
    NOT_ON_SERVER(24,"不在服务区","Tidak di area layanan"),// 不在服务区（服务不可用）
    SERVER_TIME_OUT(25,"服务器超时","melebihi batas waktu server"),// 服务器超时
    REFUSE(26,"拒绝","refuse")// 拒绝
    ;


    private int code;
    private String message;
    private String messageInn;

//    CallReusltEnum(int code, String message) {
//        this.code=code;
//        this.message=message;
//    }

    CallReusltEnum(int code, String message, String messageInn) {
        this.code=code;
        this.message=message;
        this.messageInn=messageInn;
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

    public String getMessageInn() {
        return messageInn;
    }

    public void setMessageInn(String messageInn) {
        this.messageInn = messageInn;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
