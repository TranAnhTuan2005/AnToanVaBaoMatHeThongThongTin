package MaHoaHienDai.MaHoaDoiXung;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class DESede {
    private SecretKey key;
    private final String transformation = "DESede/CBC/PKCS5Padding";
    private final int ivSize = 8;

    public SecretKey genKey() throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance("DESede");
        kg.init(168);
        key = kg.generateKey();
        return key;
    }

    public void loadKey(SecretKey key) {
        this.key = key;
    }

    public byte[] genIV() {
        byte[] iv = new byte[ivSize];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public byte[] encrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] iv = genIV();
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, this.key, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
        return result;
    }

    public String encryptBase64(String text) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        return Base64.getEncoder().encodeToString(encrypt(text));
    }

    public String Decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] iv = new byte[ivSize];
        System.arraycopy(data, 0, iv, 0, ivSize);
        byte[] encrypted = new byte[data.length - ivSize];
        System.arraycopy(data, ivSize, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, this.key, new IvParameterSpec(iv));
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public boolean encryptFile(String src, String dest) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] iv = genIV();
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, this.key, new IvParameterSpec(iv));

        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dest))) {
            output.write(iv);
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
            InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dest))) {
            byte[] iv = new byte[ivSize];
            input.read(iv);

            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, this.key, new IvParameterSpec(iv));

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
