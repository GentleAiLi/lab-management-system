package com.ailab.common.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AesUtils {

    /**
     * AES加密
     *
     * @param data
     * @param key
     * @return
     */
    public static String encrypt(String data, String key) {
        // 解码密钥
        byte[] keyBytes = Base64.getDecoder().decode(key);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        try {
            // 生成随机IV
            SecureRandom secureRandom = SecureRandom.getInstanceStrong();
            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 初始化加密器
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            // 执行加密
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // 将IV和密文拼接后Base64编码
            byte[] encryptedIVAndText = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, encryptedIVAndText, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, encryptedIVAndText, iv.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(encryptedIVAndText);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException |
                 NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("aes加密失败", e);
        }

    }

    public static String decrypt(String encryptedData, String key) {
        // 解码密钥
        byte[] keyBytes = Base64.getDecoder().decode(key);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        try {
            // Base64解码数据
            byte[] encryptedIVAndText = Base64.getDecoder().decode(encryptedData);

            // 提取IV和密文
            byte[] iv = new byte[16];
            byte[] encryptedBytes = new byte[encryptedIVAndText.length - iv.length];
            System.arraycopy(encryptedIVAndText, 0, iv, 0, iv.length);
            System.arraycopy(encryptedIVAndText, iv.length, encryptedBytes, 0, encryptedBytes.length);

            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // 初始化解密器
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

            // 执行解密
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException |
                 NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("aes解密失败", e);
        }
    }
}
