package MaHoaHienDai.MaHoaDoiXung;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RC4 {
    private SecretKey key;
    private final String transformation = "ARCFOUR";
    private final int keySize;

    public RC4(int keySize) {
        this.keySize = keySize;
    }

    public RC4() {
        this(128);
    }

    public SecretKey genKey() throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance("ARCFOUR");
        kg.init(keySize);
        key = kg.generateKey();
        return key;
    }

    public void loadKey(SecretKey key) {
        this.key = key;
    }

    public byte[] encrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, this.key);
        return cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
    }

    public String encryptBase64(String text) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return Base64.getEncoder().encodeToString(encrypt(text));
    }

    public String Decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, this.key);
        byte[] decrypted = cipher.doFinal(data);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public boolean encryptFile(String src, String dest) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, this.key);

        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dest))) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) != -1) {
                byte[] updated = cipher.update(buffer, 0, len);
                if (updated != null) output.write(updated);
            }
            byte[] finalBytes = cipher.doFinal();
            if (finalBytes != null) output.write(finalBytes);
            output.flush();
        }
        return true;
    }

    public boolean decryptFile(String src, String dest) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, this.key);

        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dest))) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = input.read(buffer)) != -1) {
                byte[] updated = cipher.update(buffer, 0, len);
                if (updated != null) output.write(updated);
            }
            byte[] finalBytes = cipher.doFinal();
            if (finalBytes != null) output.write(finalBytes);
            output.flush();
        }
        return true;
    }
}
