package UI.MaHoaHienDai;

public interface ModernSymmetricCipher {
    String algorithmName();

    String[] supportedModes();

    String[] supportedPaddings(String mode);

    int[] supportedKeySizes();

    boolean needsIV(String mode);

    int getIVSize(String mode);

    String generateKeyBase64(int keySize) throws Exception;

    String encryptText(String plainText, String keyBase64, String mode, String padding) throws Exception;

    String decryptText(String cipherBase64, String keyBase64, String mode, String padding) throws Exception;

    void encryptFile(String src, String dest, String keyBase64, String mode, String padding) throws Exception;

    void decryptFile(String src, String dest, String keyBase64, String mode, String padding) throws Exception;
}
