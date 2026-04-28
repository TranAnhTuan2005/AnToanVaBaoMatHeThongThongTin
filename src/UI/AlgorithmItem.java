package UI;

import java.awt.*;

public class AlgorithmItem {
    private final String section;
    private final String displayName;
    private final Color bulletColor;
    private final CipherAdapter adapter;

    public AlgorithmItem(String section, String displayName, Color bulletColor, CipherAdapter adapter) {
        this.section = section;
        this.displayName = displayName;
        this.bulletColor = bulletColor;
        this.adapter = adapter;
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

    public boolean isImplemented() {
        return adapter != null;
    }
}