package com.yqg.common.utils;



public class RSATester {

//    static String publicKey;
//    static String privateKey;

//    static {
//        try {
//            Map<String, Object> keyMap = RSAUtils.genKeyPair();
//            publicKey = RSAUtils.getPublicKey(keyMap);
//            privateKey = RSAUtils.getPrivateKey(keyMap);
//            System.err.println("??: \n\r" + publicKey);
//            System.err.println("??? \n\r" + privateKey);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) throws Exception {
        test();
        //        testSign();
    }

    static void test() throws Exception {
//        OrderIdentityRequest orderIdentityModel = new OrderIdentityRequest();
//        orderIdentityModel.setCardId("360124199004226015");
//        orderIdentityModel.setIdentityPicUrl("www.sina.com");
//        orderIdentityModel.setName("???");
//        orderIdentityModel.setOrderNo("111111");
        String encryptStr = JsonUtils.serialize("");
        
        System.err.println("??????????");
//        String source = "??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????";
        System.out.println("\r??????\r\n" + encryptStr);
        byte[] data = encryptStr.getBytes();
        byte[] encodedData = RSAUtils.encryptByPrivateKey(data, RSAUtils.privateKeyStr);
        String base64encode = Base64Utils.encode(encodedData);
        System.out.println("??base64????\r\n" + base64encode);
        System.out.println("base");

        byte[] decodedData = RSAUtils.decryptByPublicKey(Base64Utils.decode(base64encode),
                RSAUtils.publicKeyStr);
        String target = new String(decodedData);
        System.out.println("?????: \r\n" + target);
    }

//    static void testSign() throws Exception {
//        System.err.println("??????????");
//        String source = "??????RSA??????????";
//        System.out.println("????\r\n" + source);
//        byte[] data = source.getBytes();
//        byte[] encodedData = RSAUtils.encryptByPrivateKey(data, privateKey);
//        System.out.println("????\r\n" + new String(encodedData));
//        byte[] decodedData = RSAUtils.decryptByPublicKey(encodedData, publicKey);
//        String target = new String(decodedData);
//        System.out.println("???: \r\n" + target);
//        System.err.println("????????????");
//        String sign = RSAUtils.sign(encodedData, privateKey);
//        System.err.println("??:\r" + sign);
//        boolean status = RSAUtils.verify(encodedData, publicKey, sign);
//        System.err.println("????:\r" + status);
//    }

}
