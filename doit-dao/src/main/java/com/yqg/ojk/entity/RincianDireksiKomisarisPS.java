package com.yqg.ojk.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author: tonggen
 * Date: 2019/6/13
 * time: 2:36 PM
 */
@Data
public class RincianDireksiKomisarisPS extends BaseOjkData{

    private String id_penyelenggara;

    private String nama;

    private String no_KTP;

    private String jabatan;

    private Date tanggal_mulai_menjabat;

    private Date tanggal_akhir_menjabat;

    @JsonProperty("npwp_tin")
    private String nPWP_TIN;

    private BigDecimal jumlah_Nilai_Saham;

    private Integer jumlah_Lembar_Saham;

    private BigDecimal presentase_Nilai_Saham;

    private Date periode;

    private String foto;

}
