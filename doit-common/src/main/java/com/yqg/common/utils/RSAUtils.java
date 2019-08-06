package com.yqg.common.utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * RSA??/??/?????
 * </p>
 * <p>
 * ???·?????Ron [R]ivest????·????Adi [S]hamir?????·????Leonard [A]dleman?
 * </p>
 * <p>
 * ????????????????????BASE64????<br/>
 * ??????????????????????????????????<br/>
 * ?????????????????????????????????????????
 * </p>
 * 
 * @author Jacob
 */
public class RSAUtils {

    public static final String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCOg2on4bQBkjZRWXcJPiBcpoXh8A" +
            "+AGU2RbKvW0kLHuffkk3meZDscwbRb6jPJX9m69E9lI0SJFXNk6D4DkyIEMUuVrQ8t3zJgNT3/esC/bh+Os9zFL/XI/H1vFbleqMWIdhy50uCubiybgD3OMHjADptA4O6/Ce7kw/5xz0tUEwIDAQAB";
    public static final String privateKeyStr ="MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAI6DaifhtAGSNlFZdwk+IFymheHwD4AZTZFsq9bSQse59" +
        "+STeZ5kOxzBtFvqM8lf2br0T2UjRIkVc2ToPgOTIgQxS5WtDy3fMmA1Pf96wL9uH46z3MUv9cj8fW8VuV6oxYh2HLnS4K5uLJuAPc4weMAOm0Dg7r8J7uTD/nHPS1QTAgMBAAECgYAzmQDmT6GMsBNagLnIbuXopaHzm6cBCig7bs0IijgDeCF4cKyn3EJKtxdF8BWv4zRBrBLS22IE/+83qZ5KTQsTTs20QZG3Ylj2ZHqcF1EJo4PZLoIT0nJF6Zni0hmjFr9kwRob5TZl6L8EMa+U+ZIHpkCm0by101xMmyn8bHBagQJBAM48fumKGVrKMX9AiQqPod6dTAIXWWgLQtenuFvFljyTkfeVPleudMw67XWtZdYbQHc5YFUgKcc7/CrjWklDzEcCQQCw5qmAThbj3SBpKXmygpVP1olFlnFBjLMSBWV21xJNGKLER/1NFMJb+oRCR9WGTvwixa9SoXJj0c74Jmc0wjvVAkEAze5aS5g5+Q4snoQyUvlZCJDIdM4b2GhmNjCjRf/mJFa2SiIcPjFVLxkoQ3kBVumfVW1VY6raDLc49RpP7RhlwQJBAIIlALjTOoXjTg/wIodRCLZW+GDIwIPgFnIApFgc/GrZ4A2VhOYrip4OnTUCEjYgb+DJHSYmfrivR5xK7IzlBQ0CQQCak3az53vhR5ZcdGL1DcjqcmidjDxJrBYR+7Tehlh5J6tav+suP5YEkUJmDqBmKTCys+H5HYU6iCNlyUJ5w2om";


    //老版本的key
    public static final String oldPrivateKeyStr = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKI+dB+vt1twt2+z58qK4S4HYBqgjP" +
            "+cR87QToEBFq3KDwwQ0E9jhItJfj6eMGg6Gl86gmVwOBxlIzWsHYmvQ3Mt6UM99q35JV5NQyIEU85+jrrxeKTeNY4/OrvXK55LJUJiUq2PclVyCX3CdZDnNPpqtAUvloc5q0E2FAgRV5lpAgMBAAECgYABPYL7vwciztDILNLnzHn+NRY5/eI2DgbKgPMor05yE6pbyEEfaj5YJj6t0d3C3jbXZYYbqmjzZp6HSYKMS+ezFY9QjDXaJDYddcPieRvKgJd/H/JfUgiH83HIh2lCT8Pkly09smX8jdhUgv82K74hrZnOAfCRFyyZ3VV053qhAQJBAOxVaZYjEgUKi4UDTVRIEBuqjFgmNQBZJmLill46W6KyJf1nNwf6Ck1iFoAlitPnUI4BjJOg2xaB2fDc6zWKJ8kCQQCvvrepqo0aTAuP3ZntvUB9dndxNjJXjLN/e2rdtqO7QFehz+9N5CJZ2tRw8/ZCKKh3RtrY+8noC78xH3xxwfShAkEAzIJ9BDcE+OfPJCNfX3ecuNQ07nLhmpOK7Dc3+AJ4GaofxdDzhiuRjrU601mpcQXSZ5BfncCs1iU3+36w63vKQQJAHmjVxtnoCIDYD1C87dItcogpKsHB/DwwYwKr/yk3M5NylmEwez3aY8nlsJXGKrR2Ug1EmP8YdLpznsBf/B8fgQJBAI+2urTtm5aLd523kUlsX47932FT07yUIVLjNjJxdmHP8tL0XhqIraijamkCsBvrYfYrUBNbn3eLD2fIvTql1xs=";
    public static final String oldPublicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCiPnQfr7dbcLdvs+fKiuEuB2AaoIz/nEfO0E6BARatyg8MENBPY4SLSX4" +
            "+njBoOhpfOoJlcDgcZSM1rB2Jr0NzLelDPfat+SVeTUMiBFPOfo668Xik3jWOPzq71yueSyVCYlKtj3JVcgl9wnWQ5zT6arQFL5aHOatBNhQIEVeZaQIDAQAB";



    /**
     * ????RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * ????
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * ?????key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * ?????key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA????????
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA????????
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * <p>
     * ?????(?????)
     * </p>
     * 
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * <p>
     * ????????????
     * </p>
     * 
     * @param data ?????
     * @param privateKey ??(BASE64??)
     * 
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64Utils.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64Utils.encode(signature.sign());
    }

    /**
     * <p>
     * ??????
     * </p>
     * 
     * @param data ?????
     * @param publicKey ??(BASE64??)
     * @param sign ????
     * 
     * @return
     * @throws Exception
     * 
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64Utils.decode(sign));
    }

    /**
     * <P>
     * ????
     * </p>
     * 
     * @param encryptedData ?????
     * @param privateKey ??(BASE64??)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // ???????
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * <p>
     * ????
     * </p>
     * 
     * @param encryptedData ?????
     * @param publicKey ??(BASE64??)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // ???????
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * <p>
     * ????
     * </p>
     * 
     * @param data ???
     * @param publicKey ??(BASE64??)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // ?????
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // ???????
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * <p>
     * ????
     * </p>
     * 
     * @param data ???
     * @param privateKey ??(BASE64??)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // ???????
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * <p>
     * ????
     * </p>
     * 
     * @param keyMap ???
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64Utils.encode(key.getEncoded());
    }

    /**
     * <p>
     * ????
     * </p>
     * 
     * @param keyMap ???
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64Utils.encode(key.getEncoded());
    }

    /**
     *
     *  单元测试，给字符串加密使用
     * */
    public static String encryptString(String str) throws Exception {
        byte[] data = str.getBytes("utf-8");
        byte[] decodedData = RSAUtils.encryptByPublicKey(data, RSAUtils.publicKeyStr);
        String str1 = Base64Utils.encode(decodedData);
        return str1;
    }



}
