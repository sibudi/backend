package com.yqg.ojk.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;
import lombok.Getter;

/**
 * Author: tonggen
 * Date: 2019/6/13
 * time: 3:31 PM
 */
@Data
@Table("ojkDataTotal")
public class OjkDataTotal extends BaseEntity{

    private int idCode; //具体的某种数据编号

    private String type;//种类

    private String ojkKey;

    private String ojkValue;

    @Getter
    public enum OjkTypeEnum {

        PROFIL_PENYELENGGARA("Profil_Penyelenggara", "企业资料"),
        RINCIAN_DIREKSI_KOMISARIS_PS("Rincian_Direksi_Komisaris_PS", "董事会成员信息"),
        RINCIAN_ESCROW("Rincian_Escrow", "存管银⾏行行信息"),
        RINCIAN_KERJASAMA_LJK("Rincian_Kerjasama_LJK","⾦金金融服务"),
        RINCIAN_LAYANAN_PENDUKUNG("Rincian_Layanan_Pendukung", "第三⽅方服务"),
        REG_PENGGUNA("Reg_Pengguna", "注册⽤用户信息"),
        REG_BORROWER("Reg_Borrower", "借款⼈人信息"),
        REG_LENDER("Reg_Lender", "出借⼈人信息"),
        PENGAJUAN_PINJAMAN("Pengajuan_Pinjaman", "贷款申请"),
        PENGAJUAN_PEMBERIAN_PINJAMA("Pengajuan_Pemberian_Pinjama", "贷款合同信息"),
        TRANSAKSI_PINJAM_MEMINJAM("Transaksi_Pinjam_Meminjam", "放款信息"),
        PEMBAYARAN_PINJAMAN("Pembayaran_Pinjaman", "还款信息");

        private String code;

        private String alias;

        OjkTypeEnum(String code, String alias) {
            this.code = code;
            this.alias = alias;
        }
    }

}
