package com.yqg.ojk.entity;

import lombok.Data;

import java.util.Date;

/**
 * Author: tonggen
 * Date: 2019/6/13
 * time: 2:41 PM
 */
@Data
public class RincianEscrow extends BaseOjkData{

    private String iD_Penyelenggara;

    private String kode_Bank;

    private String no_Rekening_Escrow;

    private Date periode;
}
