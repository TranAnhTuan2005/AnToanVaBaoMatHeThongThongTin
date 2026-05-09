package UI;

import UI.MaHoaHienDai.ModernSymmetricCipher;

import java.awt.*;

public class AlgorithmItem {
    private final String section;
    private final String displayName;
    private final Color bulletColor;
    private final CipherAdapter adapter;
    private final ModernSymmetricCipher modernCipher;

    public AlgorithmItem(String section, String displayName, Color bulletColor, CipherAdapter adapter) {
        this(section, displayName, bulletColor, adapter, null);
    }

    public AlgorithmItem(String section, String displayName, Color bulletColor, ModernSymmetricCipher modernCipher) {
        this(section, displayName, bulletColor, null, modernCipher);
    }

    private AlgorithmItem(String section, String displayName, Color bulletColor,
                          CipherAdapter adapter, ModernSymmetricCipher modernCipher) {
        this.section = section;
        this.displayName = displayName;
        this.bulletColor = bulletColor;
        this.adapter = adapter;
        this.modernCipher = modernCipher;
    }

    public String section() {
        return section;
    }

    public String displayName() {
        return displayName;
    }

    public Color bulletColor() {
        return bulletColor;
    }

    public CipherAdapter adapter() {
        return adapter;
    }

    public ModernSymmetricCipher modernCipher() {
        return modernCipher;
    }

    public boolean isImplemented() {
        return adapter != null || modernCipher != null;
    }

    public boolean isModern() {
        return modernCipher != null;
    }
}