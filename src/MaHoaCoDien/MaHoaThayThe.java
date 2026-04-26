package MaHoaCoDien;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class MaHoaThayThe {

    private String mapping = ClassicalCipherSupport.ALPHABET;
    SecretKey key;

    public SecretKey genKey() throws NoSuchAlgorithmException {
        List<Character> chars = new ArrayList<>();
        for (int i = 0; i < ClassicalCipherSupport.ALPHABET.length(); i++) {
            chars.add(ClassicalCipherSupport.ALPHABET.charAt(i));
        }
        Collections.shuffle(chars, ClassicalCipherSupport.RANDOM);
        StringBuilder sb = new StringBuilder(chars.size()); // key size: bảng thay thế có độ dài bằng bảng chữ cái
        for (char c : chars) sb.append(c);
        mapping = sb.toString();
        key = ClassicalCipherSupport.keyFromString(mapping);
        return key;
    }


    public void loadKey(SecretKey key)    {
        this.key = key;
        this.mapping = ClassicalCipherSupport.keyToString(key);
        if (mapping.length() != ClassicalCipherSupport.ALPHABET.length()) {
            throw new IllegalArgumentException("Khóa thay thế không hợp lệ");
        }
    }



    public byte[] encrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (ClassicalCipherSupport.isSupportedChar(chars[i])) {
                int idx = ClassicalCipherSupport.indexOf(chars[i]);
                chars[i] = mapping.charAt(idx);
            }
        }
        return new String(chars).getBytes(StandardCharsets.UTF_8);

    }

    public String encryptBase64(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return Base64.getEncoder().encodeToString(encrypt(text));

    }

    public String Decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String cipherText = new String(data, StandardCharsets.UTF_8);
        char[] chars = cipherText.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int idx = mapping.indexOf(chars[i]);
            if (idx >= 0) {
                chars[i] = ClassicalCipherSupport.ALPHABET.charAt(idx);
            }
        }
        return new String(chars);
    }


}
