package com.duckduckgogogo.test;

import com.duckduckgogogo.utils.PasswordEncodeAssistant;

import org.junit.Assert;
import org.junit.Test;

public class PasswordEncodeAssistantTest {
    private char[] chapters = "123456".toCharArray();

    // "123456" 密文
    public final static String SHA = "7c4a8d09ca3762af61e59520943dc26494f8941b";
    public final static String SHA256 = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92";
    public final static String MD5 = "e10adc3949ba59abbe56e057f20f883e";

    // 使用SHA默认加密算法
    @Test
    public void testEncode() {
        Assert.assertTrue(SHA.equals(
                PasswordEncodeAssistant.encode(chapters)
        ));
    }

    // 使用SHA256加密算法
    @Test
    public void testEncodeSHA256() {
        Assert.assertTrue(SHA256.equals(
                PasswordEncodeAssistant.encode(chapters, PasswordEncodeAssistant.SHA256)
        ));
    }

    // 使用MD5加密算法
    @Test
    public void testEncodeMD5() {
        Assert.assertTrue(MD5.equals(
                PasswordEncodeAssistant.encode(chapters, PasswordEncodeAssistant.MD5)
        ));
    }
}
