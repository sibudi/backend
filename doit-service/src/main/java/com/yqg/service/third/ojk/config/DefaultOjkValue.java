package com.yqg.service.third.ojk.config;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: tonggen
 * Date: 2019/6/13
 * time: 4:19 PM
 */
public class DefaultOjkValue {

    public static Map<Object, Object> map = new HashMap<>();

    public static Map<Object, Object> setMapValue() {
        map.put("id_penyelenggara", "test");
        map.put("iD_Penyelenggara","sss");
        map.put("nama_penyelenggara", "ceshi");
        map.put("layanan_pinjaman", "SSS");
        map.put("periode", "2011-11-12");

        map.put("foto", "32");
        return map;
    }
}
