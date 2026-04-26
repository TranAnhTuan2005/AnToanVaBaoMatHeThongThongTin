package MaHoaCoDien;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MaHoaVigenère {

    SecretKey key;
    private String keyText = "KhoaBiMat";

    public SecretKey genKey() throws NoSuchAlgorithmException {
        int keySize = 8; // key size: số ký tự trong khóa Vigenere
        StringBuilder sb = new StringBuilder();
        int m = ClassicalCipherSupport.alphabetSize();
        for (int i = 0; i < keySize; i++) {
            sb.append(ClassicalCipherSupport.at(ClassicalCipherSupport.RANDOM.nextInt(m / 2)));
        }
        keyText = sb.toString();
        key = ClassicalCipherSupport.keyFromString(keyText);
        return key;
    }


    public void loadKey(SecretKey key)    {
        this.key = key;
        this.keyText = ClassicalCipherSupport.keyToString(key);
    }



    public byte[] encrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        char[] chars = text.toCharArray();
        int j = 0;
        for (int i = 0; i < chars.length; i++) {
            if (!ClassicalCipherSupport.isSupportedChar(chars[i])) {
                continue;
            }
            char k = keyText.charAt(j % keyText.length());
            int shift = ClassicalCipherSupport.indexOf(k);
            int idx = ClassicalCipherSupport.indexOf(chars[i]);
            chars[i] = ClassicalCipherSupport.at(idx + shift);
            j++;
        }
        return new String(chars).getBytes(StandardCharsets.UTF_8);

    }

    public String encryptBase64(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return Base64.getEncoder().encodeToString(encrypt(text));

    }

    public String Decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String cipherText = new String(data, StandardCharsets.UTF_8);
        char[] chars = cipherText.toCharArray();
        int j = 0;
        for (int i = 0; i < chars.length; i++) {
            if (!ClassicalCipherSupport.isSupportedChar(chars[i])) {
                continue;
            }
            char k = keyText.charAt(j % keyText.length());
            int shift = ClassicalCipherSupport.indexOf(k);
            int idx = ClassicalCipherSupport.indexOf(chars[i]);
            chars[i] = ClassicalCipherSupport.at(idx - shift);
            j++;
        }
        return new String(chars);
    }

}
