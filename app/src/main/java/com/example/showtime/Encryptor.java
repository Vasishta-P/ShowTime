package com.example.showtime;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryptor {

    private static final String key = "absentmindedness";


    //Encrypt String value in AES and Base64
    public static String encrypt(String value) {
        try {
            byte[] data = new byte[key.length()];
            IvParameterSpec iv = new IvParameterSpec(data);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    //Decrypt string value
    public static String decrypt(String encrypted) {
        try {
            byte[] data = new byte[key.length()];
            String decryptedOut;
            IvParameterSpec iv = new IvParameterSpec(data);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            decryptedOut = new String(cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP)));

            return decryptedOut;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }


}
