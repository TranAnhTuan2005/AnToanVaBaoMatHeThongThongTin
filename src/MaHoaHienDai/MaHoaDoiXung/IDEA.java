package MaHoaHienDai.MaHoaDoiXung;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class IDEA {
    static {
        if (Security.getProvider("BC") == null)
            Security.addProvider(new BouncyCastleProvider());
    }

    private SecretKey key;
    private final String transformation = "IDEA/CBC/PKCS5Padding";
    private final int ivSize = 8;

    public IDEA() {
    }

    public SecretKey genKey() throws NoSuchAlgorithmException, NoSuchProviderException {
        KeyGenerator kg = KeyGenerator.getInstance("IDEA", "BC");
        kg.init(128);
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

    public byte[] encrypt(String text) throws GeneralSecurityException {
        byte[] iv = genIV();
        Cipher cipher = Cipher.getInstance(transformation, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, this.key, new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
        return result;
    }

    public String encryptBase64(String text) throws GeneralSecurityException {
        return Base64.getEncoder().encodeToString(encrypt(text));
    }

    public String Decrypt(byte[] data) throws GeneralSecurityException {
        byte[] iv = new byte[ivSize];
        System.arraycopy(data, 0, iv, 0, ivSize);
        byte[] encrypted = new byte[data.length - ivSize];
        System.arraycopy(data, ivSize, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(transformation, "BC");
        cipher.init(Cipher.DECRYPT_MODE, this.key, new IvParameterSpec(iv));
        return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
    }

    public boolean encryptFile(String src, String dest) throws GeneralSecurityException, IOException {
        byte[] iv = genIV();
        Cipher cipher = Cipher.getInstance(transformation, "BC");
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

    public boolean decryptFile(String src, String dest) throws GeneralSecurityException, IOException {
        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
             BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dest))) {
            byte[] iv = new byte[ivSize];
            input.read(iv);

            Cipher cipher = Cipher.getInstance(transformation, "BC");
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
