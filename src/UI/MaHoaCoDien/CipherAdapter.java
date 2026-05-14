package UI.MaHoaCoDien;

// Interface chung cho các thuật toán mã hóa cổ điển
public interface CipherAdapter {
    // Tự sinh key ngẫu nhiên
    String generateKey() throws Exception;

    // Mã hóa plainText với key cho trước
    String encrypt(String plainText, String keyText) throws Exception;

    // Giải mã cipherText với key cho trước
    String decrypt(String cipherText, String keyText) throws Exception;

    // Gợi ý định dạng key cho người dùng
    String keyHint();
}
