package com.duckduckgogogo.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA {

    public static String encode(char[] chapter) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }

        int length = chapter.length;
        byte[] chapterByte = new byte[length];
        for (int i = 0; i < length; i++) chapterByte[i] = (byte) chapter[i];
        md.update(chapterByte);
        byte[] shaByte = md.digest();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < shaByte.length; i++) {
            int val = ((int) shaByte[i]) & 0xff;
            if (val < 16) {
                hex.append("0");
            }

            hex.append(Integer.toHexString(val));
        }

        return hex.toString();
    }

}
