package com.yqg.manage.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exclude collection rights and other role lists that can be found on the order details page
 * Author: tonggen
 * Date: 2019/5/14
 * time: 5:45 PM
 */
@Getter
public enum ManCollectorOperatorEnum {
    //Refer to manSysRole
    FIRST_CHECK(1),
    SECOND_CHECK(2),
    KEFU(5),                    //Layanan pelanggan (Customer Service)
    REVIEWER_LEAR(4),           //Head Verifikator
    SYS_ADMIN(6),               //Administrator sistem
    OPERATOR(7),    
    KEFU_LEAR(49),              //Layanan pelanggan (Customer Service Lead)
    CHECK_QUALITYER(13),        //Pemeriksaan Kualitas Cek Verifikasi (Audit quality inspection)
    WEIWAI_COMPANY(16),         //Akun orang tua perusahaan induk (Outsourcing company parent account)
    CHECK_OPERTATOR(20),        //managemen petugas verifikasi 
    LOOK_ALL_COLLECT_ORDERS(21),//Lihat semua permohonan kolektor
    COLLECTOR_LIST(22),         //Managemen petugas kolektor
    OPERATOR_CHECK(25),         //Kualitas Cek Operator
    OUT_DATE_COLLECTOR(30),     //Alokasi permohonan terlambat
    QUALITY_CHECKOR(36),        //Petugas pemeriksaan mutu melakukan pemeriksaan mutu
    QUALITY_CHECKER_LEAR(37),   //Personel manajemen pemeriksaan mutu, dapat ditugaskan, mengunduh laporan
    HAS_PAY_LOOKER(39),         //Reimbursed order inquiry
    HAS_PAY_ADMIN(45),          //Pesanan dilunasi - administrator
    ALL_ORDERS_LIST(32);        //Lihat semua permohonan 

    ManCollectorOperatorEnum(Integer desc){
        this.desc = desc;
    }
    private Integer  desc;

    //Currently only used to validate access to view bank information
    //API /manage/orderUserDataSql -> ManOrderUserDataService.java
    public static List<Integer> listCollectorOperatorEnum() {
        return Arrays.asList(ManCollectorOperatorEnum.values()).stream()
                .map(ManCollectorOperatorEnum:: getDesc).collect(Collectors.toList());
    }
}
