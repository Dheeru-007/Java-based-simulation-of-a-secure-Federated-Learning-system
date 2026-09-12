package common;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

    // Shared AES key (16 bytes = 128 bits)
    private static final String SECRET_KEY = "1234567890abcdef";

    private static SecretKeySpec getKey() {
        return new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
    }

    public static byte[] encrypt(byte[] data) throws Exception {

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, getKey());

        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data) throws Exception {

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, getKey());

        return cipher.doFinal(data);
    }
}