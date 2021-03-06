package com.yqg.common.enums.order;

/**
 * ?????????
 * Created by Didit Dwianto on 2017/11/29.
 */
public enum OrdShowStateEnum {
    APPLICATION(1,"Sedang mendaftar"),//???
    CHECKING(2,"Dalam proses verifikasi"),//???
    LOANING(3,"Pinjaman sedang diproses"),//???
    TOPAY(4,"Menunggu pelunasan"),//???
    OVERDUE(5,"Terlambat melunasi"),//???
    PAYING(6,"Dalam proses pelunasan"),//???
    PAYED(7,"Pinjaman sudah lunas"),//???
    CHECKNOTPASS(8,"Pengajuan ditolak"),//?????
    CANCEL(9,"BATAL"),//??
    LOAN_FAILD(10,"Transfer gagal"),//????
    REJECT_STATUS_DESCRIPTION(99,"Silakan coba   hari lagi dari~tanggal pengajuan Anda~")
    ;

    private Integer code;
    private String message;

    private OrdShowStateEnum(Integer code, String message) {
        this.code=code;
        this.message=message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
