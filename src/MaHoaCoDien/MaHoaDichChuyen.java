package MaHoaCoDien;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MaHoaDichChuyen {

    private int shift = 3;
    SecretKey key;

    public SecretKey genKey() throws NoSuchAlgorithmException {
        int m = ClassicalCipherSupport.alphabetSize();
        shift = 1 + ClassicalCipherSupport.RANDOM.nextInt(m - 1); // key size: 1 số nguyên
        key = new SecretKeySpec(String.valueOf(shift).getBytes(StandardCharsets.UTF_8), "RAW");
        return key;
    }


    public void loadKey(SecretKey key)    {
        this.key = key;
        this.shift = Integer.parseInt(new String(key.getEncoded(), StandardCharsets.UTF_8));
    }



    public byte[] encrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (ClassicalCipherSupport.isSupportedChar(chars[i])) {
                int idx = ClassicalCipherSupport.indexOf(chars[i]);
                chars[i] = ClassicalCipherSupport.at(idx + shift);
            }
        }
        return new String(chars).getBytes(StandardCharsets.UTF_8);

    }

    public String encryptBase64(String text) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return Base64.getEncoder().encodeToString(encrypt(text));

    }

    public String Decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String cipherText = new String(data, StandardCharsets.UTF_8);
        char[] chars = cipherText.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (ClassicalCipherSupport.isSupportedChar(chars[i])) {
                int idx = ClassicalCipherSupport.indexOf(chars[i]);
                chars[i] = ClassicalCipherSupport.at(idx - shift);
            }
        }
        return new String(chars);
    }




}
