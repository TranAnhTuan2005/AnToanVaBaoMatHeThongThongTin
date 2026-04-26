package MaHoaCoDien;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

public class MaHoaHill {
    SecretKey key;
    // Ma trận 2x2
    private int a = 3, b = 3, c = 2, d = 5;

    public SecretKey genKey() throws NoSuchAlgorithmException {
        int m = ClassicalCipherSupport.alphabetSize();
        while (true) {
            a = ClassicalCipherSupport.RANDOM.nextInt(m);
            b = ClassicalCipherSupport.RANDOM.nextInt(m);
            c = ClassicalCipherSupport.RANDOM.nextInt(m);
            d = ClassicalCipherSupport.RANDOM.nextInt(m);
            int det = ClassicalCipherSupport.normalize(a * d - b * c);
            if (ClassicalCipherSupport.gcd(det, m) == 1) break;
        }
        String raw = a + "," + b + "," + c + "," + d; // key size: 4 phần tử ma trận 2x2
        key = ClassicalCipherSupport.keyFromString(raw);
        return key;
    }


    public void loadKey(SecretKey key)    {
        this.key = key;
        String[] p = ClassicalCipherSupport.keyToString(key).split(",");
        if (p.length != 4) throw new IllegalArgumentException("Khóa Hill không hợp lệ");
        a = Integer.parseInt(p[0]);
        b = Integer.parseInt(p[1]);
        c = Integer.parseInt(p[2]);
        d = Integer.parseInt(p[3]);
    }



    public byte[] encrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        List<Integer> positions = ClassicalCipherSupport.supportedPositions(text);
        char[] src = ClassicalCipherSupport.supportedChars(text, positions);
        char[] out = processHillEncrypt(src);
        String merged = ClassicalCipherSupport.mergeBack(text, positions, out);
        return merged.getBytes(StandardCharsets.UTF_8);

    }

    public String encryptBase64(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return Base64.getEncoder().encodeToString(encrypt(text));

    }

    public String Decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String cipherText = new String(data, StandardCharsets.UTF_8);
        List<Integer> positions = ClassicalCipherSupport.supportedPositions(cipherText);
        char[] src = ClassicalCipherSupport.supportedChars(cipherText, positions);
        char[] out = processHillDecrypt(src);
        return ClassicalCipherSupport.mergeBack(cipherText, positions, out);
    }


    private char[] processHillEncrypt(char[] src) {
        int m = ClassicalCipherSupport.alphabetSize();
        char[] out = src.clone();
        for (int i = 0; i + 1 < src.length; i += 2) {
            int x1 = ClassicalCipherSupport.indexOf(src[i]);
            int x2 = ClassicalCipherSupport.indexOf(src[i + 1]);
            int y1 = ClassicalCipherSupport.normalize(a * x1 + b * x2) % m;
            int y2 = ClassicalCipherSupport.normalize(c * x1 + d * x2) % m;
            out[i] = ClassicalCipherSupport.at(y1);
            out[i + 1] = ClassicalCipherSupport.at(y2);
        }
        return out;
    }


    private char[] processHillDecrypt(char[] src) {
        int m = ClassicalCipherSupport.alphabetSize();
        int det = ClassicalCipherSupport.normalize(a * d - b * c);
        int detInv = ClassicalCipherSupport.modInverse(det, m);
        int ia = ClassicalCipherSupport.normalize(detInv * d);
        int ib = ClassicalCipherSupport.normalize(detInv * (-b));
        int ic = ClassicalCipherSupport.normalize(detInv * (-c));
        int id = ClassicalCipherSupport.normalize(detInv * a);

        char[] out = src.clone();
        for (int i = 0; i + 1 < src.length; i += 2) {
            int y1 = ClassicalCipherSupport.indexOf(src[i]);
            int y2 = ClassicalCipherSupport.indexOf(src[i + 1]);
            int x1 = ClassicalCipherSupport.normalize(ia * y1 + ib * y2) % m;
            int x2 = ClassicalCipherSupport.normalize(ic * y1 + id * y2) % m;
            out[i] = ClassicalCipherSupport.at(x1);
            out[i + 1] = ClassicalCipherSupport.at(x2);
        }
        return out;
    }

}
