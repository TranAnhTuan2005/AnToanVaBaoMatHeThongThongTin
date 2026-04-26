package MaHoaCoDien;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.*;

final class ClassicalCipherSupport {
    private ClassicalCipherSupport() {}

    // Bảng chữ cái tiếng Việt + tiếng Anh (chữ thường), sau đó cộng thêm chữ hoa.
    private static final String LOWER_ALPHABET = "abcdefghijklmnopqrstuvwxyz" +
            "àáảãạăằắẳẵặâầấẩẫậđèéẻẽẹêềếểễệìíỉĩịòóỏõọôồốổỗộơờớởỡợùúủũụưừứửữựỳýỷỹỵ";
    static final String ALPHABET = LOWER_ALPHABET + LOWER_ALPHABET.toUpperCase(Locale.forLanguageTag("vi-VN"));

    private static final Map<Character, Integer> INDEX_MAP = new HashMap<>();
    static final SecureRandom RANDOM = new SecureRandom();

    static {
        for (int i = 0; i < ALPHABET.length(); i++) {
            INDEX_MAP.put(ALPHABET.charAt(i), i);
        }
    }

    static int alphabetSize() {
        return ALPHABET.length();
    }

    static boolean isSupportedChar(char c) {
        return INDEX_MAP.containsKey(c);
    }

    static int indexOf(char c) {
        Integer idx = INDEX_MAP.get(c);
        if (idx == null) {
            throw new IllegalArgumentException("Ký tự không thuộc phạm vi mã hóa: " + c);
        }
        return idx;
    }

    static char at(int index) {
        int m = alphabetSize();
        int normalized = ((index % m) + m) % m;
        return ALPHABET.charAt(normalized);
    }

    static int normalize(int value) {
        int m = alphabetSize();
        return ((value % m) + m) % m;
    }

    static SecretKey keyFromString(String raw) {
        return new SecretKeySpec(raw.getBytes(StandardCharsets.UTF_8), "RAW");
    }

    static String keyToString(SecretKey key) {
        return new String(key.getEncoded(), StandardCharsets.UTF_8);
    }

    static List<Integer> supportedPositions(String text) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            if (isSupportedChar(text.charAt(i))) {
                positions.add(i);
            }
        }
        return positions;
    }

    static char[] supportedChars(String text, List<Integer> positions) {
        char[] result = new char[positions.size()];
        for (int i = 0; i < positions.size(); i++) {
            result[i] = text.charAt(positions.get(i));
        }
        return result;
    }

    static String mergeBack(String original, List<Integer> positions, char[] encryptedSupported) {
        char[] all = original.toCharArray();
        for (int i = 0; i < positions.size(); i++) {
            all[positions.get(i)] = encryptedSupported[i];
        }
        return new String(all);
    }

    static int gcd(int a, int b) {
        while (b != 0) {
            int t = a % b;
            a = b;
            b = t;
        }
        return Math.abs(a);
    }

    static int modInverse(int a, int m) {
        int t = 0, newT = 1;
        int r = m, newR = normalize(a);

        while (newR != 0) {
            int q = r / newR;
            int tempT = t - q * newT;
            t = newT;
            newT = tempT;

            int tempR = r - q * newR;
            r = newR;
            newR = tempR;
        }

        if (r != 1) {
            throw new IllegalArgumentException("Không tồn tại nghịch đảo modulo cho a=" + a);
        }

        return ((t % m) + m) % m;
    }
}