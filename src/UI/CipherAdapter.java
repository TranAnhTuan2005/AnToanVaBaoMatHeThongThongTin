package UI;

public interface CipherAdapter {
    String generateKey() throws Exception;

    String encrypt(String plainText, String keyText) throws Exception;

    String decrypt(String cipherText, String keyText) throws Exception;

    String keyHint();
}