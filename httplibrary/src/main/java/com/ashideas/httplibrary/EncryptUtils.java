package com.ashideas.httplibrary;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtils {

    private static final String HEX_CHARS = "0123456789ABCDEF";

    public static String sha1(String text) {
        return sha1(text.getBytes());
    }

    public static String sha1(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(input);
            byte[] encrypted = md.digest();

            final StringBuilder hex = new StringBuilder(2 * encrypted.length);
            for (final byte b : encrypted) {
                hex.append(HEX_CHARS.charAt((b & 0xF0) >> 4)).append(HEX_CHARS.charAt((b & 0x0F)));
            }
            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
