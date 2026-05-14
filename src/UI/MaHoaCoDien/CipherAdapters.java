package UI.MaHoaCoDien;

import MaHoaCoDien.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

// Factory tạo CipherAdapter cho từng thuật toán mã hóa cổ điển
public final class CipherAdapters {
    private CipherAdapters() {}

    // Tạo SecretKey từ chuỗi text (dùng chung cho các adapter)
    private static SecretKey rawKey(String keyText) {
        return new SecretKeySpec(keyText.getBytes(StandardCharsets.UTF_8), "RAW");
    }

    // Adapter cho mã hóa dịch chuyển (Caesar/Shift cipher)
    public static CipherAdapter dichChuyen() {
        return new CipherAdapter() {
            @Override
            public String generateKey() throws Exception {
                MaHoaDichChuyen c = new MaHoaDichChuyen();
                return new String(c.genKey().getEncoded(), StandardCharsets.UTF_8);
            }

            @Override
            public String encrypt(String plainText, String keyText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
                MaHoaDichChuyen c = new MaHoaDichChuyen();
                c.loadKey(rawKey(keyText));
                return new String(c.encrypt(plainText), StandardCharsets.UTF_8);
            }

            @Override
            public String decrypt(String cipherText, String keyText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
                MaHoaDichChuyen c = new MaHoaDichChuyen();
                c.loadKey(rawKey(keyText));
                return c.Decrypt(cipherText.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public String keyHint() { return "Số nguyên dịch chuyển, ví dụ: 5"; }
        };
    }

    // Adapter cho mã hóa thay thế (Substitution cipher)
    public static CipherAdapter thayThe() {
        return new CipherAdapter() {
            @Override
            public String generateKey() throws Exception {
                MaHoaThayThe c = new MaHoaThayThe();
                SecretKey k = c.genKey();
                return new String(k.getEncoded(), StandardCharsets.UTF_8);
            }

            @Override
            public String encrypt(String plainText, String keyText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
                MaHoaThayThe c = new MaHoaThayThe();
                c.loadKey(rawKey(keyText));
                return new String(c.encrypt(plainText), StandardCharsets.UTF_8);
            }

            @Override
            public String decrypt(String cipherText, String keyText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
                MaHoaThayThe c = new MaHoaThayThe();
                c.loadKey(rawKey(keyText));
                return c.Decrypt(cipherText.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public String keyHint() { return "Chuỗi mapping cùng độ dài bảng chữ cái (khuyên dùng: Sinh key)"; }
        };
    }

    // Adapter cho mã hóa Affine
    public static CipherAdapter affine() {
        return new CipherAdapter() {
            @Override
            public String generateKey() throws Exception {
                MaHoaAffine c = new MaHoaAffine();
                return new String(c.genKey().getEncoded(), StandardCharsets.UTF_8);
            }

            @Override
            public String encrypt(String plainText, String keyText) {
                MaHoaAffine c = new MaHoaAffine();
                c.loadKey(rawKey(keyText));
                return new String(c.encrypt(plainText), StandardCharsets.UTF_8);
            }

            @Override
            public String decrypt(String cipherText, String keyText) {
                MaHoaAffine c = new MaHoaAffine();
                c.loadKey(rawKey(keyText));
                return c.Decrypt(cipherText.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public String keyHint() { return "Dạng a:b, ví dụ 5:8"; }
        };
    }

    // Adapter cho mã hóa Vigenère
    public static CipherAdapter vigenere() {
        return new CipherAdapter() {
            @Override
            public String generateKey() throws Exception {
                MaHoaVigenère c = new MaHoaVigenère();
                return new String(c.genKey().getEncoded(), StandardCharsets.UTF_8);
            }

            @Override
            public String encrypt(String plainText, String keyText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
                MaHoaVigenère c = new MaHoaVigenère();
                c.loadKey(rawKey(keyText));
                return new String(c.encrypt(plainText), StandardCharsets.UTF_8);
            }

            @Override
            public String decrypt(String cipherText, String keyText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
                MaHoaVigenère c = new MaHoaVigenère();
                c.loadKey(rawKey(keyText));
                return c.Decrypt(cipherText.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public String keyHint() { return "Chuỗi key, ví dụ: KhoaBiMat"; }
        };
    }

    // Adapter cho mã hóa Hill (dùng ma trận 2x2)
    public static CipherAdapter hill() {
        return new CipherAdapter() {
            @Override
            public String generateKey() throws Exception {
                MaHoaHill c = new MaHoaHill();
                return new String(c.genKey().getEncoded(), StandardCharsets.UTF_8);
            }

            @Override
            public String encrypt(String plainText, String keyText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
                MaHoaHill c = new MaHoaHill();
                c.loadKey(rawKey(keyText));
                return new String(c.encrypt(plainText), StandardCharsets.UTF_8);
            }

            @Override
            public String decrypt(String cipherText, String keyText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
                MaHoaHill c = new MaHoaHill();
                c.loadKey(rawKey(keyText));
                return c.Decrypt(cipherText.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public String keyHint() { return "Ma trận 2x2 dạng a,b,c,d, ví dụ 3,3,2,5"; }
        };
    }

    // Adapter cho mã hóa hoán vị (Transposition cipher)
    public static CipherAdapter hoanVi() {
        return new CipherAdapter() {
            @Override
            public String generateKey() throws Exception {
                MaHoaHoanVi c = new MaHoaHoanVi();
                return new String(c.genKey().getEncoded(), StandardCharsets.UTF_8);
            }

            @Override
            public String encrypt(String plainText, String keyText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
                MaHoaHoanVi c = new MaHoaHoanVi();
                c.loadKey(rawKey(keyText));
                return new String(c.encrypt(plainText), StandardCharsets.UTF_8);
            }

            @Override
            public String decrypt(String cipherText, String keyText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
                MaHoaHoanVi c = new MaHoaHoanVi();
                c.loadKey(rawKey(keyText));
                return c.Decrypt(cipherText.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public String keyHint() { return "Dãy hoán vị cách nhau bởi dấu phẩy, ví dụ: 2,0,1,3,5,4"; }
        };
    }
}
