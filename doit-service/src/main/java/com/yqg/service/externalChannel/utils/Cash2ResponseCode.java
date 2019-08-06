package com.yqg.service.externalChannel.utils;

import lombok.Getter;

/*****
 * @Author zengxiangcai
 * Created at 2018/3/7
 * @Email zengxiangcai@yishufu.com
 *
 ****/

@Getter
public enum Cash2ResponseCode {

    CODE_OK_1(0,"success"),
    PARAM_EMPTY_1001(1001,"parameter kosong"), //参数为空
    PARAM_TYPE_ERROR_1002(1002,"Jenis parameter salah"), //参数类型错误
    PARAM_JSON_FORMAT_ERROR_1003(1003,"数据不是一个JSON格式"),
    DATA_SIGNATURE_ERROR_1009(1009,"数据签名错误"),
    DATA_DECRYPTED_ERROR_1009(1009,"数据解密错误"),
    DATA_VERIFY_ERROR(1002,"数据校验不通过"),
    SERVER_INTERNAL_ERROR_9001(9001,"KESALAHAN INTERNAL SERVER"),  //服务器内部错误
    SERVER_MAINTENANCE_10000(10000,"PEMELIHARAAN SISTEM"),  //系统维护中
    CAN_NOT_BORROW_FOR_EXIST_LOAN_11000(11000,"IDAK BISA DIPINJAM, SUDAH DALAM PINJAMAN"), //不可借款，已在借款中
    CAN_NOT_BORROW_FOR_OVERDUE_11001(11001,"TIDAK DAPAT MEMINJAM,MEMILIKI PESANAN TERLAMBAT"), //不可借款，有严重的逾期订单
    CAN_NOT_BORROW_FOR_REJECT_11002(11002,"TIDAK BISA MEMIINJAM, DITOLAK DALAM WAKTU DEKAT"), //不可借款，近期内拒绝过
    CAN_NOT_BORROW_FOR_USER_INFO_MISMATCH_11003(11003,"Informasi pengguna tidak cocok"),//用户信息不匹配
    CAN_NOT_BORROW_FOR_USER_IN_BLACKLIST_11004(11004,"Informasi pengguna tidak cocok"),//用户在黑名单中
    ORDER_NO_EMPTY(3000,"Nomor pesanan kosong"),  // 订单编号为空
    ORDER_NO_ERROR(3001,"Nomor pesanan salah"),  //订单编号错误
    ORDER_PRODUCT_CONFIG_IS_NULL(3002,"konfigurasi produk tidak terdaftar"),//产品配置不存在

    ;

    Cash2ResponseCode(int code,String message){
        this.code=code;
        this.message=message;
    }
    private String message;
    private int code;
}
