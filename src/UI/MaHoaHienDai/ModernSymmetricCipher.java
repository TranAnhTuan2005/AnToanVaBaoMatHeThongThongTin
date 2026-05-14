package UI.MaHoaHienDai;

// Interface chung cho các thuật toán mã hóa hiện đại đối xứng (AES, DES, Blowfish,...)
public interface ModernSymmetricCipher {
    // Tên thuật toán
    String algorithmName();

    // Danh sách mode hỗ trợ (CBC, ECB, GCM,...)
    String[] supportedModes();

    // Danh sách padding hỗ trợ theo mode
    String[] supportedPaddings(String mode);

    // Danh sách key size hỗ trợ (bit)
    int[] supportedKeySizes();

    // Mode này có cần IV không
    boolean needsIV(String mode);

    // Kích thước IV (byte) theo mode
    int getIVSize(String mode);

    // Sinh key ngẫu nhiên, trả về dạng Base64
    String generateKeyBase64(int keySize) throws Exception;

    // Mã hóa văn bản, trả về ciphertext dạng Base64
    String encryptText(String plainText, String keyBase64, String mode, String padding) throws Exception;

    // Giải mã ciphertext Base64, trả về plaintext
    String decryptText(String cipherBase64, String keyBase64, String mode, String padding) throws Exception;

    // Mã hóa file từ src sang dest
    void encryptFile(String src, String dest, String keyBase64, String mode, String padding) throws Exception;

    // Giải mã file từ src sang dest
    void decryptFile(String src, String dest, String keyBase64, String mode, String padding) throws Exception;
}
