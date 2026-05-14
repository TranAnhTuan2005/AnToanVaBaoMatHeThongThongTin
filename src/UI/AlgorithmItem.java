package UI;

import UI.MaHoaCoDien.CipherAdapter;
import UI.MaHoaHienDai.ModernSymmetricCipher;

import java.awt.*;

// Đại diện cho 1 thuật toán trong danh sách sidebar (cổ điển, hiện đại, bất đối xứng, hoặc hash)
public class AlgorithmItem {
    private final String section;       // tên nhóm (vd: "MÃ HÓA CỔ ĐIỂN")
    private final String displayName;   // tên hiển thị trên sidebar
    private final Color bulletColor;    // màu chấm tròn phân biệt nhóm
    private final CipherAdapter adapter;          // adapter cho mã hóa cổ điển
    private final ModernSymmetricCipher modernCipher; // cipher cho mã hóa hiện đại đối xứng
    private final boolean asymmetric;   // có phải bất đối xứng (RSA) không
    private final String hashName;      // tên giải thuật hash (null nếu không phải hash)

    // Constructor cho mã hóa cổ điển
    public AlgorithmItem(String section, String displayName, Color bulletColor, CipherAdapter adapter) {
        this(section, displayName, bulletColor, adapter, null, false, null);
    }

    // Constructor cho mã hóa hiện đại đối xứng
    public AlgorithmItem(String section, String displayName, Color bulletColor, ModernSymmetricCipher modernCipher) {
        this(section, displayName, bulletColor, null, modernCipher, false, null);
    }

    // Constructor cho mã hóa bất đối xứng (RSA)
    public AlgorithmItem(String section, String displayName, Color bulletColor, boolean asymmetric) {
        this(section, displayName, bulletColor, null, null, asymmetric, null);
    }

    // Factory method tạo item cho hàm băm
    public static AlgorithmItem hash(String section, String displayName, Color bulletColor, String hashName) {
        return new AlgorithmItem(section, displayName, bulletColor, null, null, false, hashName);
    }

    // Constructor chính (private), các constructor khác gọi về đây
    private AlgorithmItem(String section, String displayName, Color bulletColor,
                          CipherAdapter adapter, ModernSymmetricCipher modernCipher,
                          boolean asymmetric, String hashName) {
        this.section = section;
        this.displayName = displayName;
        this.bulletColor = bulletColor;
        this.adapter = adapter;
        this.modernCipher = modernCipher;
        this.asymmetric = asymmetric;
        this.hashName = hashName;
    }

    public String section() { return section; }

    public String displayName() { return displayName; }

    public Color bulletColor() { return bulletColor; }

    public CipherAdapter adapter() { return adapter; }

    public ModernSymmetricCipher modernCipher() { return modernCipher; }

    public String hashName() { return hashName; }

    // Kiểm tra thuật toán này đã được implement chưa
    public boolean isImplemented() {
        return adapter != null || modernCipher != null || asymmetric || hashName != null;
    }

    public boolean isModern() { return modernCipher != null; }

    public boolean isAsymmetric() { return asymmetric; }

    public boolean isHash() { return hashName != null; }
}
