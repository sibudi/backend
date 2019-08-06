package com.yqg.common.utils;

/**
 * Created by Didit Dwianto on 2017/12/3.
 */
public class CheakTeleUtils {


//    private static String teleArrStr = "0814/0815/0816/0817/0818/0819/0855/0856/0857/0858/0859/0827/0828/0811/0812/0813/0821/0822/0823/0851/0852/0853/0877/0878/0831/0832/0833/0838/0881/0882/0883/0884/0885/0886/0887/0888/0889/0895/08953/0896/0897/0898/0899";

//    private static String[] teleArr = teleArrStr.split("/");

    public static boolean telephoneNumberValid(String telNumber) {
        String str1 = "628";
        String str2 = "+628";
        String str3 = "08";
        String reg = "^628";
        String reg1 = "^08";
        telNumber = telNumber.replaceAll("\\s", "").replaceAll("-", "");
        if (telNumber.startsWith(str1)) {
            telNumber = telNumber.replaceAll(reg, "8");
        } else if (telNumber.startsWith(str2)) {
            telNumber = telNumber.replaceAll("\\+628", "8");
        } else if (telNumber.startsWith(str3)) {
            telNumber = telNumber.replaceAll(reg1, "8");
        }
//        telNumber = telNumber.replaceAll("\\+62","0").replaceAll("\\s","").replaceAll("-","");
        boolean isValid = false;
//        TODO?????????  ???+62??  ?????14?? ???????????
        if (telNumber.length() >= 9 && telNumber.length() <= 13 && telNumber.startsWith("8")) {
//            for (String telePrex : teleArr){
//                if (telNumber.startsWith(telePrex)){
//                    isValid = true;
//                    break;
//                }
//            }
            return true;
        } else {
            return false;
        }
    }



    /**
     * ?????+628?08??????????????+62?0????????????9-12?
     * ?+628??????08
     * ? 628??????08
     *
     * @param phone
     * @return
     */
    public static String telephoneNumberValid2(String phone) {
        if(StringUtils.isNotEmpty(phone)&&phone.startsWith("00")){
            phone = phone.substring(2);
        }
        String str1 = "628";
        String str2 = "+628";
        String str3 = "08";
        String reg = "^628";
        String reg1 = "^08";
        phone = phone.replaceAll("\\s", "").replaceAll("-", "");
        if (phone.startsWith(str1)) {
            phone = phone.replaceAll(reg, "8");
        } else if (phone.startsWith(str2)) {
            phone = phone.replaceAll("\\+628", "8");
        } else if (phone.startsWith(str3)) {
            phone = phone.replaceAll(reg1, "8");
        }
//        phone = phone.replaceAll("\\+62","0").replaceAll("\\s","").replaceAll("-","");

        if (phone.length() >= 9 && phone.length() <= 13 && phone.startsWith("8")) {
            return phone;
        } else {
            return "";
        }
    }

    public static String fetchNumbers(String numbers){
        if(StringUtils.isEmpty(numbers)){
            return null;
        }
        try {
            //合法的数字
           long longNum =  Long.valueOf(numbers);
           return numbers;
        } catch (Exception e) {
            //非法数字
            StringBuffer bf = new StringBuffer();
            for(int i=0;i<numbers.length();i++){
                char ch = numbers.charAt(i);
                if(ch>='0' && ch<='9'){
                    bf.append(ch);
                }
            }
            return bf.toString();
        }
    }

    public static void main(String[] args) {
        System.err.println(telephoneNumberValid2("021-2197264441"));
        System.err.println(telephoneNumberValid2("+6282197264441"));
        System.err.println(fetchNumbers("6285370833996"));
        System.err.println(fetchNumbers("6282345243110"));
        System.err.println(fetchNumbers("6285240616649"));
        System.err.println(fetchNumbers("62895639159799"));
    }
}


