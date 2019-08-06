package com.yqg.ojk.entity;

import lombok.Data;

import java.util.Date;

/**
 * Author: tonggen
 * Date: 2019/6/13
 * time: 2:43 PM
 */
@Data
public class RincianKerjasamaLJK extends BaseOjkData{

    private String iD_Penyelenggara;

    private String nama_LJK;

    private String jenis_LJK;

    private String domisili_LJK;

    private Date periode;
}
