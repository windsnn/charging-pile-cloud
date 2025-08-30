package com.trick.backend.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;

@Component
public class WxPhoneDecryptUtil {
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 解密微信手机号
     *
     * @param encryptedData 加密数据（前端传）
     * @param sessionKey    会话密钥（从 code2Session 获取）
     * @param iv            初始化向量（前端传）
     * @return 解密后的手机号
     */
    public String decryptPhoneNumber(String encryptedData, String sessionKey, String iv) {
        try {
            byte[] dataByte = Base64.getDecoder().decode(encryptedData);
            byte[] keyByte = Base64.getDecoder().decode(sessionKey);
            byte[] ivByte = Base64.getDecoder().decode(iv);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            cipher.init(Cipher.DECRYPT_MODE, spec, new IvParameterSpec(ivByte));
            byte[] resultByte = cipher.doFinal(dataByte);
            String result = new String(resultByte);

            Map<String, Object> jsonMap = objectMapper.readValue(result, new TypeReference<>() {
            });
            return (String) jsonMap.get("phoneNumber");
        } catch (Exception e) {
            throw new RuntimeException("手机号解密失败", e);
        }
    }
}

