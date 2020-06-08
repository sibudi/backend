package com.yqg.service.risk.service.response;

import java.util.List;

import lombok.Data;

@Data
public class FDCResponse {
    
    private String noIdentitas;
    private String userId;
    private String userName;
    private String inquiryReason;
    private String memberId;
    private String memberName;
    private String refferenceId;
    private String inquiryDate;
    private String status;
    
    @Data
    public static class DataDetail {

        private List<PinjamanDetail> pinjaman;

        @Data
        public static class PinjamanDetail {
            
            private String id_penyelenggara;
            private Integer jenis_pengguna;
            private String nama_borrower;
            private String no_identitas;
            private String no_npwp;
            private String tgl_perjanjian_borrower;
            private String tgl_penyaluran_dana;
            private Double nilai_pendanaan;
            private String tgl_pelaporan_data;
            private Double sisa_pinjaman_berjalan;
            private String tgl_jatuh_tempo_pinjaman;
            private String kualitas_pinjaman;
            private Integer dpd_terakhir;
            private Integer dpd_max;
            private String status_pinjaman;
            private String jenis_pengguna_ket;
            private String kualitas_pinjaman_ket;
            private String status_pinjaman_ket;
        }
    
    }
}