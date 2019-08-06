package com.yqg.service.third.yitu;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 
 * @author Jacob
 *
 */
public class EncryptionHelper {

    public static class PemFile {

        private PemObject pemObject;

        public PemFile(String filename) throws FileNotFoundException, IOException {
            PemReader pemReader = new PemReader(
                    new InputStreamReader(new FileInputStream(filename)));
            try {
                this.pemObject = pemReader.readPemObject();
            } finally {
                pemReader.close();
            }
        }

        public PemObject getPemObject() {
            return pemObject;
        }
    }

    /**
     * MD5???
     */
    public static class MD5Helper {

        public static class Md5EncodingException extends Exception {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public Md5EncodingException() {
            }

            public Md5EncodingException(String msg) {
                super(msg);
            }
        }

        /**
         * ??MD5, ???32???16????
         * 
         * @param filename
         *            , ??????
         * @return ???
         * @throws Md5EncodingException
         */
        public static String md5(String message) throws Md5EncodingException {
            try {
                // ?? MD5 Hash(16??)
                MessageDigest digest = MessageDigest.getInstance("MD5");
                digest.update(message.getBytes(Charset.forName("UTF-8")));
                byte messageDigest[] = digest.digest();

                // 16????(32??)
                StringBuffer hexString = new StringBuffer();
                for (int i = 0; i < messageDigest.length; i++)
                    hexString.append(
                            Integer.toHexString((messageDigest[i] & 0xFF) | 0x100).substring(1, 3));

                return hexString.toString();
            } catch (Exception e) {
                e.printStackTrace();
                throw new Md5EncodingException("????MD5???");
            }
        }
    }

    /**
     * RSA???
     * 
     */
    public static class RSAHelper {

        /**
         * ?pem??????
         * 
         * @param factory
         * @param filename
         * @return
         * @throws InvalidKeySpecException
         * @throws FileNotFoundException
         * @throws IOException
         */
        public static PrivateKey generatePrivateKey(KeyFactory factory, String filename)
                throws InvalidKeySpecException, FileNotFoundException, IOException {
            PemFile pemFile = new PemFile(filename);
            byte[] content = pemFile.getPemObject().getContent();
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
            return factory.generatePrivate(privKeySpec);
        }

        public static class PublicKeyException extends Exception {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            public PublicKeyException() {
            }

            public PublicKeyException(String msg) {
                super(msg);
            }
        }

        /**
         * ?pem??????
         * 
         * @param filename
         *            , ??????
         * @return ???
         * @throws PublicKeyException
         */
        public static PublicKey loadPublicKey(String filename) throws PublicKeyException {
            try {
                Security.addProvider(new BouncyCastleProvider());
                KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
                PemFile pemFile = new PemFile(filename);
                byte[] content = pemFile.getPemObject().getContent();
                X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
                return factory.generatePublic(pubKeySpec);
            } catch (Exception e) {
                e.printStackTrace();
                throw new PublicKeyException("??????");
            }

        }

        /**
         * ?pem??????
         * 
         * @param factory
         * @param filename
         * @return
         * @throws InvalidKeySpecException
         * @throws FileNotFoundException
         * @throws IOException
         */
        public static PublicKey generatePublicKey(KeyFactory factory, String filename)
                throws InvalidKeySpecException,
                FileNotFoundException, IOException {
            PemFile pemFile = new PemFile(filename);
            byte[] content = pemFile.getPemObject().getContent();
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
            return factory.generatePublic(pubKeySpec);
        }

        /**
         * ??????
         * 
         * @param message
         * @param publicKey
         * @return
         * @throws InvalidKeyException 
         * @throws BadPaddingException 
         * @throws IllegalBlockSizeException 
         * @throws NoSuchPaddingException 
         * @throws NoSuchAlgorithmException 
         */
        public static byte[] encrypt(byte[] message, PublicKey publicKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] enBytes = cipher.doFinal(message);

            return enBytes;
        }

        /**
         * ??????
         * 
         * @param result
         * @param privateKey
         * @return
         * @throws Exception
         */
        public static byte[] decrypt(final byte[] result, PrivateKey privateKey) throws Exception {

            Cipher cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher1.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher1.doFinal(result);
            return decryptedBytes;
        }
    }

}
