package com.device;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.math.BigInteger;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Base64;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
/**
 *
 * @author Administrator
 *
 */
public class AES {
    private final static Base64.Decoder decoder = Base64.getDecoder();
    private final static Base64.Encoder encoder = Base64.getEncoder();
    private static MessageDigest mMessageDigest = null;
    static{
    try {
        mMessageDigest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    }
    public static String Encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            System.out.print("Key=null");
            return null;
        }
        if (sKey.length() != 16) {
            System.out.print("Key length is not 16");
            return null;
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

        return encoder.encodeToString(encrypted);
    }

    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            if (sKey == null) {
                System.out.print("Key=null");
                return null;
            }
            if (sKey.length() != 16) {
                System.out.print("Key length is not 16");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = decoder.decode(sSrc);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }
    /**
     * obtain the file's MD5
     */
    public static String getFileMD5String(File file) {
        try {
            InputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) > 0) {
                mMessageDigest.update(buffer, 0, length);
            }
            fis.close();
            return new BigInteger(1, mMessageDigest.digest()).toString(16);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String CCBase64Encoder(String params)
    {
        String str=null;
        try{
            str=encoder.encodeToString(params.getBytes("utf-8"));
        }catch(Exception e){e.printStackTrace();}        
        return str;
    }
    public static String CCBase64Decoder(String params)
    {   
        String str=null;
        try{
            byte[] bytes=Base64.getDecoder().decode(params);
            str = new String(bytes,"utf-8");
        }catch(Exception e){e.printStackTrace();}
        //return new String(bytes,"utf-8");
        return str;
    }
    /*
    public static void main(String[] args) throws Exception {
        String cKey = "1234567890123456";
        String cSrc = "www.gowhere.so";
        System.out.println(cSrc);
        String enString = AES.Encrypt(cSrc, cKey);
        System.out.println("encorder:" + enString);

        String DeString = AES.Decrypt(enString, cKey);
        System.out.println("decorder:" + DeString);
    }*/
}
