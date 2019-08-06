package com.yqg.user.entity;

import com.yqg.base.data.annotations.Table;
import com.yqg.base.data.condition.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by Didit Dwianto on 2017/11/24.
 */
@Data
@Table("usrQuestionnaireAttach")
public class UsrQuestionnaireAttach extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 3730216543282817318L;

    private String userUuid;
    private String attachmentType;
    private String attachmentUrl;
    private String attachmentSaveZone;
    private String attachmentSavePath;
    private String comment;


    public enum AttachmentTypeEnum {

        DEFAULT(0,"**","**"),
        TANDA_DAFTAR_PERUSAHAAN(1,"Tanda Daftar Perusahaan","公司登记证书 "),
        IZIN_USAHA_TETAP(2, "Izin Usaha Tetap", "公司营业执照"),
        GOVERNMENT_STATEMENT_LETTER(3, "government statement letter", "政府信"),
        NPWP(4, "NPWP","税务号"),
        LETTER_OF_TRADE_LICENSE(5, "Letter of Trade License", "贸易许可证"),
        PT_CERTIFICATE(6, "PT certificate", "PT 证书"),
        COMPANYDOMICILLE_LETTER(7, "CompanyDomicille Letter", "公司地址信"),
        alur_perusahaan_ke_akun_publik (8, "Alur perusahaan ke akun publik", "公司对公账户流水");

        private int code;
        private String attachType;
        private String alias;

        AttachmentTypeEnum(int code, String attachType, String alias) {
            this.code = code;
            this.attachType = attachType;
            this.alias = alias;
        }
        public int getCode() {
            return code;
        }

        public String getAttachType() {
            return attachType;
        }

        public String getAlias() {
            return alias;
        }


    }
}
