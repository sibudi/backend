package com.yqg.ojk.entity;

import lombok.Data;

import java.util.Date;

/**
 * Author: tonggen
 * Date: 2019/6/13
 * time: 2:41 PM
 */
@Data
public class RincianLayananPendukung extends BaseOjkData{

    private String iD_Penyelenggara;

    private String nama_Layanan_Pendukung;

    private Integer jenis_Layanan_Pendukung;

    private String domisili_Layanan_Pendukung;

    private Date periode;
}
