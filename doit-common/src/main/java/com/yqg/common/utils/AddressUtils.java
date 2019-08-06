package com.yqg.common.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Didit Dwianto on 2018/2/28.
 */
public class AddressUtils {


    // ??
    public static String WEST = "Kabupaten Kepulauan Seribu#Kota Jakarta Barat#Kota Jakarta Pusat#Kota Jakarta Selatan#Kota Jakarta Timur#Kota Jakarta Utara#Kabupaten Tangerang#Kota Tangerang Selatan#Kota Tangerang#Kota Depok#Kota Bandung#Kabupaten Bandung#Kabupaten Bandung Barat#Kota Bogor#Kabupaten Bogor#Kota Bekasi#Kabupaten Bekasi#Kabupaten Karawang#Kota Cimahi#Kota Cirebon#Kabupaten Cirebon#Kabupaten Sumedang#Kota Tasikmalaya#Kabupaten Tasikmalaya#Kota Sukabumi#Kabupaten Sukabumi#Kabupaten Subang#Kabupaten Purwakarta#Kabupaten Indramayu";

    // ??
    public static String MIDDLE = "Kota Banjar#Kabupaten Badung#Kabupaten Bangli#Kabupaten Buleleng#Kabupaten Gianyar#Kabupaten Jembrana#Kabupaten Karangasem#Kabupaten Klungkung#Kabupaten Tabanan#Kota Denpasar";

    // ??
    public static String EAST = "";


    /**
     * ???????
     *
     * @param city
     * @return
     */
    public static String getRegionByCity(String city) {
        if(StringUtils.isEmpty(city)){
            return "";
        }

        List<String> westlist = Arrays.asList(WEST.split("#"));
        List<String> middlelist = Arrays.asList(MIDDLE.split("#"));
        List<String> eastlist = Arrays.asList(EAST.split("#"));
        String region = "";
        for (String elem : westlist) {
            if (elem.equals(city)) {
                return "West";
            }
        }

        for (String elem : middlelist) {
            if (elem.equals(city)) {
                return "Middle";
            }
        }

        for (String elem : eastlist) {
            if (elem.equals(city)) {
                return "East";
            }
        }
        return region;
    }

}
