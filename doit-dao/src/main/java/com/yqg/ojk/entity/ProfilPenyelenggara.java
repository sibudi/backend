package com.yqg.ojk.entity;

import lombok.Data;

import java.util.Date;

/**
 * Author: tonggen
 * Date: 2019/6/13
 * time: 2:32 PM
 */
@Data
public class ProfilPenyelenggara extends BaseOjkData{

    private String id_penyelenggara;

    private String nama_penyelenggara;

    private String layanan_pinjaman;

    private Integer jumlah_tenaga_kerja_pria;

    private Integer jumlah_tenaga_kerja_wanita;

    private Integer jumlah_kantor_cabang;

    private Date periode;
}
