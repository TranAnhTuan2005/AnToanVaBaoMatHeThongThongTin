package UI;

import UI.MaHoaCoDien.CipherAdapters;
import UI.MaHoaHienDai.ModernSymmetricCiphers;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Danh mục tất cả các thuật toán, dùng để hiển thị trên sidebar
public final class AlgorithmCatalog {
    private AlgorithmCatalog() {}

    // Tạo danh sách toàn bộ thuật toán theo từng nhóm
    public static List<AlgorithmItem> create() {
        List<AlgorithmItem> items = new ArrayList<>();

        // --- Mã hóa cổ điển (màu nâu) ---
        Color classical = new Color(191, 127, 25);
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Dịch chuyển (Shift)", classical, CipherAdapters.dichChuyen()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Thay thế (Substitution)", classical, CipherAdapters.thayThe()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Affine", classical, CipherAdapters.affine()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Vigenère", classical, CipherAdapters.vigenere()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Hill", classical, CipherAdapters.hill()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Hoán vị (Transposition)", classical, CipherAdapters.hoanVi()));

        // --- Mã hóa hiện đại đối xứng (màu xanh lá) ---
        Color modern = new Color(26, 128, 104);
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "AES", modern, ModernSymmetricCiphers.aes()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "DES", modern, ModernSymmetricCiphers.des()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "DESede (3DES)", modern, ModernSymmetricCiphers.desede()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "ChaCha20-Poly1305", modern, ModernSymmetricCiphers.chacha20()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "Blowfish", modern, ModernSymmetricCiphers.blowfish()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "RC2", modern, ModernSymmetricCiphers.rc2()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "RC4 (ARCFOUR)", modern, ModernSymmetricCiphers.rc4()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "Twofish", modern, ModernSymmetricCiphers.twofish()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "Serpent", modern, ModernSymmetricCiphers.serpent()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "Camellia", modern, ModernSymmetricCiphers.camellia()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "IDEA", modern, ModernSymmetricCiphers.idea()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "ARIA", modern, ModernSymmetricCiphers.aria()));

        // --- Mã hóa bất đối xứng (màu hồng) ---
        Color asymmetric = new Color(180, 40, 100);
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI BẤT ĐỐI XỨNG", "RSA (File)", asymmetric, true));

        // --- Hàm băm (màu xanh dương) ---
        Color hash = new Color(40, 80, 180);
        items.add(AlgorithmItem.hash("HÀM BĂM (HASH)", "MD5", hash, "MD5"));
        items.add(AlgorithmItem.hash("HÀM BĂM (HASH)", "SHA-1", hash, "SHA-1"));
        items.add(AlgorithmItem.hash("HÀM BĂM (HASH)", "SHA-224", hash, "SHA-224"));
        items.add(AlgorithmItem.hash("HÀM BĂM (HASH)", "SHA-256", hash, "SHA-256"));
        items.add(AlgorithmItem.hash("HÀM BĂM (HASH)", "SHA-384", hash, "SHA-384"));
        items.add(AlgorithmItem.hash("HÀM BĂM (HASH)", "SHA-512", hash, "SHA-512"));
        items.add(AlgorithmItem.hash("HÀM BĂM (HASH)", "SHA-512/224", hash, "SHA-512/224"));
        items.add(AlgorithmItem.hash("HÀM BĂM (HASH)", "SHA-512/256", hash, "SHA-512/256"));
        items.add(AlgorithmItem.hash("HÀM BĂM (HASH)", "SHA3-224", hash, "SHA3-224"));
        items.add(AlgorithmItem.hash("HÀM BĂM (HASH)", "SHA3-256", hash, "SHA3-256"));
        items.add(AlgorithmItem.hash("HÀM BĂM (HASH)", "SHA3-384", hash, "SHA3-384"));
        items.add(AlgorithmItem.hash("HÀM BĂM (HASH)", "SHA3-512", hash, "SHA3-512"));
        items.add(AlgorithmItem.hash("HÀM BĂM (HASH)", "RIPEMD-160", hash, "RIPEMD160"));
        items.add(AlgorithmItem.hash("HÀM BĂM (HASH)", "Whirlpool", hash, "Whirlpool"));

        return items;
    }
}
