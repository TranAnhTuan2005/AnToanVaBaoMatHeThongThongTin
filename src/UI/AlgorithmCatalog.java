package UI;

import UI.MaHoaHienDai.ModernSymmetricCiphers;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class AlgorithmCatalog {
    private AlgorithmCatalog() {
    }

    public static List<AlgorithmItem> create() {
        List<AlgorithmItem> items = new ArrayList<>();

        Color classical = new Color(191, 127, 25);
        Color modern = new Color(26, 128, 104);

        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Dịch chuyển (Shift)", classical, CipherAdapters.dichChuyen()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Thay thế (Substitution)", classical, CipherAdapters.thayThe()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Affine", classical, CipherAdapters.affine()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Vigenère", classical, CipherAdapters.vigenere()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Hill", classical, CipherAdapters.hill()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Hoán vị (Transposition)", classical, CipherAdapters.hoanVi()));

        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "AES", modern, ModernSymmetricCiphers.aes()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "DES", modern, ModernSymmetricCiphers.des()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "DESede (3DES)", modern, ModernSymmetricCiphers.desede()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "ChaCha20-Poly1305", modern, ModernSymmetricCiphers.chacha20()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "Blowfish", modern, ModernSymmetricCiphers.blowfish()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "RC2", modern, ModernSymmetricCiphers.rc2()));
        items.add(new AlgorithmItem("MÃ HÓA HIỆN ĐẠI ĐỐI XỨNG", "RC4 (ARCFOUR)", modern, ModernSymmetricCiphers.rc4()));

        return items;
    }
}