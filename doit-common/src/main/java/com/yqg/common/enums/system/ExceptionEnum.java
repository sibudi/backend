package com.yqg.common.enums.system;

import java.text.MessageFormat;

/**
 *
 * @author Jacob
 *
 */
public enum ExceptionEnum {
    SESSION_UN_LOGIN(1000, "Belum masuk"),//未登录
    USER_NOT_FOUND(1001, "Pengguna tidak terdaftar"),//用户不存在
    USER_IS_EXIST(1002, "Pengguna sudah ada"),//用户已存在
    USER_LOGIN_ERROR(1003, "Login gagal"),//登录失败
    USER_LOGOUT_ERROR(1004, "Keluar gagal"),//退出失败
    USER_PASSWORD_ERROR(1005, "Kata sandi salah"),//密码不正确
    USER_CHECK_SMS_CODE_ERROR(1006, "Kode verifikasi SMS salah"),//短信验证码不正确
    USER_VALID_ERROR(1007, "Kode verifikasi gambar salah"),//图形验证码错误
    USER_SMS_CODE_COUNT_ERROR(1008, "Permintaan kode verifikasi mencapai jumlah maksimum"),//短信验证码次数已达上限
    USER_ID_CARD_NOT_MATHING(1010,"Nomor KTP tidak cocok"),//身份证号不匹配
    USER_BANK_CARD_LIST_IS_NULL(1011,"Kartu bank pengguna tidak terdaftar"),//没有用户银行卡列表
    USER_SMS_CODE_AUTO_LOGIN_ERROR(1012,"Login kode verifikasi SMS gagal secara otomatis"),//短信验证码自动登录失败
    USER_BASE_PARAMS_ILLEGAL(1015,"Pengisian tidak sah"),//参数不合法
    USER_NO_VIFIFY(1016,"Pengguna belum terotentikasi"),//用户未实名认证
    USER_INFO_ERROR(1021,"Terdapat kesalahan dalam pengisian identitas Anda, silakan isi kembali"),//您的身份信息填写有误，请重新输入
    USER_IP_ADDRESS_COUNT(1022,"Operasi Anda terlalu sering, tunggu sebentar dan coba lagi."),//您的操作太过频繁，稍等片刻，欢迎回来！
    USER_RANDOMIMAGE_CHECK_ERROR(1024,"Kode verifikasi gambar salah Silakan periksa dan coba lagi"),//图形验证码有误,请检查后重试
    USER_REGIST_DEVICENO_IDENTICAL(1025,"Coba gunakan perangkat lain untuk mendaftar"),//请换一个设备进行注册
    USER_BANK_IS_NOT_SUPPER(1026,"Bank sementara tidak mendukung, mohon ganti bank lain dan coba lagi."),//该银行暂时不支持，请更换其他银行重试！
    USER_ID_CARD_IS_EXIST(1029,"Nomor KTP sudah pernah digunakan, tidak dapat melakukan sertifikasi ulang. Jika ada pertanyaan, hubungi customer service"),//该身份证号已绑定用户，无法再次认证,如有疑问,请联系客服！
    USER_H5_USER_ORDER_IS_NULL(1030,"Saat ini h5 belum terdaftar di permohonan."),//当前用户还没有h5订单
    USER_KABIN_CHECK_FAILED(1031,"Penyesuaian kartu bank gagal"),//绑卡异常
    USER_NOT_CHOOSE_IDENTITY(1032,"Pengguna tidak memilih identitas"),//用户未选择身份
    USER_KABIN_RESPONSE_FAILED(1033,"Penyesuaian kartu bank gagal"),//绑卡失败
    USER_KABIN_CHECK_NAME(1034,"Nama pada kartu bank tidak sesuai "),//绑卡姓名不匹配
    USER_CHOOSE_IDENTITY(1035,"Nama kartu tidak sesuai"),//用户已经选择身份
    CERTIFICATION_NOT_EXIST(1036,"pengguna sudah memilih identitas"),//用户认证信息不存在
    USER_BANK_CARD_NOT_EXIT(1037,"informasi sertifikasi pengguna tidak terdaftar"),//用户银行卡不存在
    CERTIFICATION_COUNT_IS_OVER(1038,"Kartu bank pengguna tidak terdaftar"),//用户认证次数已达上限
    PAYMENT_CHANNEL_NOT_VALID(1039,"Metode pembayaran saat ini tidak tersedia"),//暂不支持此还款方式
    USER_PASSWORD_OR_ACCOUNT_ERROR(1040,"Akun dan kata sandi salah"),//账号或密码不正确
    INPUT_CAPTCHA(1041,"Masukan kode verifikasi SMS"),//请输入短信验证码
    CAPTCHA_INVALID(1042,"Kode verifikasi tidak berlaku"),//短信验证码无效
    Captcha_INVALID_AND_RESENT(1043,"terdapat kesalahan dalam kode verifikasi, mencoba kirim kode yang baru"),//短信验证码错误且会重新发送
    SYSTEM_TIMEOUT(1044,"Sistem terjadi kesalahan, silakan ulangi beberapa saat lagi"),//系统错误请稍后重试
    CAPTCHA_TIMEOUT(1045,"Telah melebihi batas waktu pengisian kode"),//提交验证码超时
    CAPTCHA_ERROR_TO_MANY_TIMES(1046,"kesalahan pengisian kode melebihi batas, akun terkunci secara otomatis"),//验证码错误次数过多,账号已被锁定
    FAILED_CAPTCHA_TOO_MANY_TIMES(1047,"kesalahan pengisian kode melebihi batas, ulangi beberapa saat lagi"),//验证码错误次数过多,请稍后重试
    USER_ALTERNET_PHONENO_IS_EXIT(1048,"Silakan isi nomor ponsel sekunder"),//请填写其他备用手机号码
    USER_IS_P2P_USER(1049,"Anda sekarang Sebagai Investor jadi tidak bisa mengajukan permohonan pinjaman ini"),//???????????
    USER_GOPAY_NOT_EXIT(1050, "Akun gopay pengguna tidak ada"),//用户gopay账户不存在
    INSUFFICIENT_ACCOUNT_BALANCE(1051, "Saldo akun tidak mencukupi"),//佣金账户余额不足
    CASE_OUT_MIN(1052, "Jumlah penarikan uang tunai anda berada di bawah batas minimum"), // 您的提现金额低于最低限额
    CAN_NOT_REBIND_CARD(1053, "Ada pengajuan penarikan belum selesai, nomor bank tidak dapat diganti"), // 有未完成的提现操作，不能换绑卡
    USER_CHECK_SMS_CODE_TIMEOUT(1054, "Kode verifikasi SMS telah kedaluwarsa"),//短信验证码已失效


    SYSTEM_TONGDUN_APPLY_ERROR(2003,"permintaan sertifikasi pengguna mencapai batas maksimum"),//请求同盾失败
    SYSTEM_CITY_LIST_IS_NULL(2006,"daftar kota tidak ditemukan"),//没有找到城市列表
    SYSTEM_DIC_ITEMS_LIST_IS_NULL(2007,"karakter tidak ditemukan"),//没有找到字典列表
    SYSTEM_DIC_LIST_IS_NULL(2008,"daftar karakter tidak ditemukan"),//没有找到字典项
    SYSTEM_APP_CONFIG_LIST_IS_NULL(2010,"daftar konfigurasi aplikasi tidak ditemukan"),//没有找到app配置url列表
    SYSTEM_USER_BANK_IS_EXIST(2017,"kartu bank sudah pernah didaftarkan, gunakan kartu lain"),//该卡已经存在,请更换其他银行卡
    SYSTEM_YITU_CALL_SCORE_LIMT_ERROR(2027,"pengenalan mahluk hidup gagal, pastikan"),//活体匹配失败,请确保您的身份证信息和刷脸本人一致
    SYSTEM_REFUND_PROCESS_ERROR(2031,"Pelunasan sedang diproses, mohon menunggu."),//还款处理中，请耐心等待！
    SYSTEM_IS_REFUND(2032,"Pinjaman sudah dilunasi, silahkan periksa kembali riwayat Anda. jangan memproses ulang."),//该订单已还款，返回订单记录查看，请勿重复操作
    SYSTEM_OVER_MANY(2034,"Melebihi batas permintaan,silahkan ulangi setelah 24 jam."),//请求过于频繁,请24小时之后再试
    SYSTEM_SHARE_LIST_IS_NULL(2036,"tidak menemukan daftar aplikasi bagikan"),//没有找到app分享配置
    SYSTEM_INVAILD_IP_ADDRESS(2037,"tidak menemukan aplikasi untuk membagikan."),//非法的IP地址
    SYSTEM_BANK_CARD_NOT_FIND_ERROR(2038,"Pendaftaran kartu bank gagal, tidak dapat menemukan kartu bank pengguna."),//解绑卡失败，未找到用户银行卡！
    SYSTEM_YITU_UPLOAD_IMAGE_FAILD(2039,"pencocokan gambar gagal, silahkan lakukan verifikasi ulang."),//活体匹配图片失败，请重新认证
    SYSTEM_APP_NEED_UPDATE(2040,"Versi yang digunakan oleh nasabah tidak kompatibel, silahkan perbaharui terlebih dahulu"),//客户端版本过低，请更新应用
    SYSTEM_UPGRADE(2041,"Maaf dengan adanya peningkatan versi. Terima kasih"),//抱歉，我们的系统正在升级，请至googleplay下载我们的APP。 谢谢


    ORDER_PRODUCT_CONFIG_IS_NULL(3014,"konfigurasi produk tidak terdaftar"),//产品配置不存在
    ORDER_NOT_FOUND(3018,"permohonan tidak terdaftara"),//订单不存在
    ORDER_UN_FINISH(3019,"terdapat permohonan yang belum selesai di proses"),//有未处理完的订单
    ORDER_IS_NOT_APPLYING(3020,"permohonan tidak sedang diasukan"),//不是申请中的订单
    ORDER_COMMIT_REPEAT(3031,"tolong jangan mengulang permintaan yang sama"),//请勿重复提交！
    ORDER_STATES_ERROR(3032,"Permohonan bermasalah!"),//订单状态异常!
    NOT_ARRVIE_DAY(3033,"Belum melebihi jumlah batas hari penolakan!"),//未到达拒绝天数
    ADDLINKMANINFO_COMMIT_REPEAT(3034,"tolong jangan mengulang permintaan yang sama"),//请勿重复提交！
    ORDER_REPAYMENT_CODE_NOT_FOUND(3035,"Kode pembayaran nasabah tidak ditemukan"),//未找到还款码
    ORDER_CAN_NOT_COMMIT(3036,"Nasabah Yth, saat ini aplikasi kami sedang dalam perbaikan. Maaf untuk ketidaknyamanannya."),//亲爱的Do-It客户 对不起，我们的系统正在维护和升级。 谢谢

    MANAGE_SESSION_UN_LOGIN(9999,"pengguna tidak sedang masuk ke akun"),//用户未登陆
    MANAGE_LOGIN_ERROR(10000,"percobaan masuk pengguna gagal"),//用户登陆失败
    MANAGE_ADD_USER_ERROR(10001,"Gagal mengubah data nasabah"),//编辑用户失败
    MANAGE_EDIT_SYS_PERMISSION_ERROR(10004,"perubahan batas otoritas sistem gagal"),//修改系统权限失败
    MANAGE_EDIT_SYS_ROLE_ERROR(10005,"perubahan fungsi sistem gagal"),//修改系统角色失败
    MANAGE_PERMISSIONTREE_ERROR(10006,"gagal mendapatkan otoritas pengguna"),//获取用户权限列表失败
    MANAGE_EDIT_PASSWORD_ERROR(10007,"gagal merubah kode sandi"),//修改密码失败
    MANAGE_SEARCH_ERROR(10008,"gagal mendapatkan informasi"),//查询失败
    MANAGE_ORDER_TAG_ERROR(10009,"gagal memberi label pada orderan/permohonan"),//订单打标签失败
    MANAGE_FREE_ORDER_ERROR(10010,"pelunasan offline gagal"),//线下还款失败
    MANAGR_DISTRIBUTE_ORDER_ERROR(10011,"gagal membagikan permohonan"),//订单分配失败
    MANAGE_ADD_ITEM_ERROR(10012,"gagal menambahkan"),//添加失败
    CALL_RECORD_IS_NONE(10014,"Call record is none"),
    MANAGE_EDIT_ITEM_ERROR(10013,"gagal merubah dikarenakan percobaan berkali-kali melebihi batas, silahkan hubungi bagian development"),//修改失败,若多次失败请联系开发人员
    SMS_COUNT_LIMIT(10015,"The number of SMS messages has reached the upper limit!"),
    LOCKED(10016,"LOCKED!"),
    NO_USER_NAME(10017,"没有查询到该用户！"),
    HAS_TEAM(10018,"该组长下还有组员！"),
    MANAGE_HAS_LOGIN(10019,"Nasabah telah login di tempat lain!"),//用户在别处已登录;
    FILE_CONTAIN_MOBILE(10020,"文件第一行第一列必须包含mobile！"),
    FREQUENT_OPERATION(10021,"请勿频繁操作！"),
    VALITYDATE_FORMAT_IS_ERROR(10022, "validity startTime or endTime format is error!"),
    ;
    private int code;
    private String message;
    private String temlate;

    private ExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
    private ExceptionEnum(int code, String message, String template) {
        this.code = code;
        this.message = message;
        this.temlate=template;
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

    public ExceptionEnum setCustomMessage(String message){
        this.setMessage(message);
        return this;
    }

    /**
     * ???????????
     * @param ags
     * @return
     */
    public ExceptionEnum setDynamicMessage(Object... ags){
        String dynamicMsg= MessageFormat.format(this.temlate,ags);
        this.setMessage(dynamicMsg);
        return this;
    }
}
