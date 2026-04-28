package UI;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class AlgorithmCatalog {
    private AlgorithmCatalog() {
    }

    public static List<AlgorithmItem> create() {
        List<AlgorithmItem> items = new ArrayList<>();

        Color classical = new Color(191, 127, 25);
        Color jce = new Color(26, 128, 104);
        Color hybrid = new Color(87, 84, 201);

        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Dịch chuyển (Shift)", classical, CipherAdapters.dichChuyen()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Thay thế (Substitution)", classical, CipherAdapters.thayThe()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Affine", classical, CipherAdapters.affine()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Vigenère", classical, CipherAdapters.vigenere()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Hill", classical, CipherAdapters.hill()));
        items.add(new AlgorithmItem("MÃ HÓA CỔ ĐIỂN", "Hoán vị (Transposition)", classical, CipherAdapters.hoanVi()));

        items.add(new AlgorithmItem("JAVA JCE", "AES-256-GCM", jce, null));
        items.add(new AlgorithmItem("JAVA JCE", "AES-256-CBC", jce, null));
        items.add(new AlgorithmItem("JAVA JCE", "DES-CBC", jce, null));
        items.add(new AlgorithmItem("JAVA JCE", "3DES-CBC", jce, null));
        items.add(new AlgorithmItem("JAVA JCE", "RSA-2048-OAEP (Hybrid)", hybrid, null));

        items.add(new AlgorithmItem("JAVA JCE (JAVA 11+)", "ChaCha20-Poly1305", jce, null));

        items.add(new AlgorithmItem("BOUNCY CASTLE", "Twofish-256-CBC", jce, null));
        items.add(new AlgorithmItem("BOUNCY CASTLE", "Blowfish-128-CBC", jce, null));
        items.add(new AlgorithmItem("BOUNCY CASTLE", "Camellia-256-CBC", jce, null));
        items.add(new AlgorithmItem("BOUNCY CASTLE", "SEED-128-CBC", jce, null));
        items.add(new AlgorithmItem("BOUNCY CASTLE", "HC-256", jce, null));

        return items;
    }
}