package MaHoaCoDien;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class MaHoaAffine {
    SecretKey key;

    private int a = 5;
    private int b = 8;

    public SecretKey genKey() throws NoSuchAlgorithmException {
        int m = ClassicalCipherSupport.alphabetSize();
        do {
            a = 1 + ClassicalCipherSupport.RANDOM.nextInt(m - 1);
        } while (ClassicalCipherSupport.gcd(a, m) != 1);
        b = ClassicalCipherSupport.RANDOM.nextInt(m);
        String raw = a + ":" + b; // key size: 2 số nguyên (a,b)
        key = ClassicalCipherSupport.keyFromString(raw);
        return key;
    }

    public void loadKey(SecretKey key) {
        this.key = key;
        String[] parts = ClassicalCipherSupport.keyToString(key).split(":");
        if (parts.length != 2) throw new IllegalArgumentException("Khóa Affine không hợp lệ");
        this.a = Integer.parseInt(parts[0]);
        this.b = Integer.parseInt(parts[1]);
    }

    public byte[] encrypt(String text) {
        int m = ClassicalCipherSupport.alphabetSize();
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (ClassicalCipherSupport.isSupportedChar(chars[i])) {
                int x = ClassicalCipherSupport.indexOf(chars[i]);
                int y = (a * x + b) % m;
                chars[i] = ClassicalCipherSupport.at(y);
            }
        }
        return new String(chars).getBytes(StandardCharsets.UTF_8);
    }

    public String encryptBase64(String text) {
        return Base64.getEncoder().encodeToString(encrypt(text));
    }

    public String Decrypt(byte[] data) {
        int m = ClassicalCipherSupport.alphabetSize();
        int invA = ClassicalCipherSupport.modInverse(a, m);
        String cipherText = new String(data, StandardCharsets.UTF_8);
        char[] chars = cipherText.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (ClassicalCipherSupport.isSupportedChar(chars[i])) {
                int y = ClassicalCipherSupport.indexOf(chars[i]);
                int x = ClassicalCipherSupport.normalize(invA * (y - b));
                chars[i] = ClassicalCipherSupport.at(x);
            }
        }
        return new String(chars);
    }



}
