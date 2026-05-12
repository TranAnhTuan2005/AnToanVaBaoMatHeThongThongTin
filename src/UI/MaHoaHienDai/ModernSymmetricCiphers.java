package UI.MaHoaHienDai;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

public final class ModernSymmetricCiphers {
    static {
        if (Security.getProvider("BC") == null)
            Security.addProvider(new BouncyCastleProvider());
    }

    private ModernSymmetricCiphers() {
    }

    public static ModernSymmetricCipher aes() {
        return new BlockCipherImpl("AES",
                new String[]{"CBC", "ECB", "CTR", "CFB", "OFB", "GCM"},
                new int[]{128, 192, 256}, 16);
    }

    public static ModernSymmetricCipher des() {
        return new BlockCipherImpl("DES",
                new String[]{"CBC", "ECB", "CFB", "OFB"},
                new int[]{56}, 8);
    }

    public static ModernSymmetricCipher desede() {
        return new BlockCipherImpl("DESede",
                new String[]{"CBC", "ECB", "CFB", "OFB"},
                new int[]{112, 168}, 8);
    }

    public static ModernSymmetricCipher chacha20() {
        return new ChaCha20Impl();
    }

    public static ModernSymmetricCipher blowfish() {
        return new BlockCipherImpl("Blowfish",
                new String[]{"CBC", "ECB", "CFB", "OFB"},
                new int[]{128, 192, 256, 448}, 8);
    }

    public static ModernSymmetricCipher rc2() {
        return new BlockCipherImpl("RC2",
                new String[]{"CBC", "ECB", "CFB", "OFB"},
                new int[]{40, 64, 128}, 8);
    }

    public static ModernSymmetricCipher rc4() {
        return new RC4Impl();
    }

    public static ModernSymmetricCipher twofish() {
        return new BCBlockCipherImpl("Twofish",
                new String[]{"CBC", "ECB", "CTR", "CFB", "OFB"},
                new int[]{128, 192, 256}, 16);
    }

    public static ModernSymmetricCipher serpent() {
        return new BCBlockCipherImpl("Serpent",
                new String[]{"CBC", "ECB", "CTR", "CFB", "OFB"},
                new int[]{128, 192, 256}, 16);
    }

    public static ModernSymmetricCipher camellia() {
        return new BCBlockCipherImpl("Camellia",
                new String[]{"CBC", "ECB", "CTR", "CFB", "OFB"},
                new int[]{128, 192, 256}, 16);
    }

    public static ModernSymmetricCipher idea() {
        return new BCBlockCipherImpl("IDEA",
                new String[]{"CBC", "ECB", "CFB", "OFB"},
                new int[]{128}, 8);
    }

    public static ModernSymmetricCipher aria() {
        return new BCBlockCipherImpl("ARIA",
                new String[]{"CBC", "ECB", "CTR", "CFB", "OFB"},
                new int[]{128, 192, 256}, 16);
    }

    private static class BlockCipherImpl implements ModernSymmetricCipher {
        private final String algorithm;
        private final String[] modes;
        private final int[] keySizes;
        private final int blockSize;

        BlockCipherImpl(String algorithm, String[] modes, int[] keySizes, int blockSize) {
            this.algorithm = algorithm;
            this.modes = modes;
            this.keySizes = keySizes;
            this.blockSize = blockSize;
        }

        @Override
        public String algorithmName() {
            return algorithm;
        }

        @Override
        public String[] supportedModes() {
            return modes;
        }

        @Override
        public String[] supportedPaddings(String mode) {
            if ("GCM".equals(mode) || "CTR".equals(mode))
                return new String[]{"NoPadding"};
            return new String[]{"PKCS5Padding", "NoPadding"};
        }

        @Override
        public int[] supportedKeySizes() {
            return keySizes;
        }

        @Override
        public boolean needsIV(String mode) {
            return !"ECB".equals(mode);
        }

        @Override
        public int getIVSize(String mode) {
            if ("GCM".equals(mode)) return 12;
            return blockSize;
        }

        @Override
        public String generateKeyBase64(int keySize) throws Exception {
            KeyGenerator kg = KeyGenerator.getInstance(algorithm);
            kg.init(keySize);
            return Base64.getEncoder().encodeToString(kg.generateKey().getEncoded());
        }

        private Cipher initCipher(int opmode, byte[] keyBytes, String mode, String padding, byte[] iv) throws Exception {
            String trans = algorithm + "/" + mode + "/" + padding;
            Cipher cipher = Cipher.getInstance(trans);
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, algorithm);
            if (!needsIV(mode)) {
                cipher.init(opmode, keySpec);
            } else if ("GCM".equals(mode)) {
                cipher.init(opmode, keySpec, new GCMParameterSpec(128, iv));
            } else {
                cipher.init(opmode, keySpec, new IvParameterSpec(iv));
            }
            return cipher;
        }

        @Override
        public String encryptText(String plainText, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            byte[] iv = null;
            if (needsIV(mode)) {
                iv = new byte[getIVSize(mode)];
                new SecureRandom().nextBytes(iv);
            }
            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, keyBytes, mode, padding, iv);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            if (iv != null) {
                byte[] result = new byte[iv.length + encrypted.length];
                System.arraycopy(iv, 0, result, 0, iv.length);
                System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
                return Base64.getEncoder().encodeToString(result);
            }
            return Base64.getEncoder().encodeToString(encrypted);
        }

        @Override
        public String decryptText(String cipherBase64, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            byte[] data = Base64.getDecoder().decode(cipherBase64);
            byte[] iv = null;
            byte[] encrypted;
            if (needsIV(mode)) {
                int ivLen = getIVSize(mode);
                iv = new byte[ivLen];
                System.arraycopy(data, 0, iv, 0, ivLen);
                encrypted = new byte[data.length - ivLen];
                System.arraycopy(data, ivLen, encrypted, 0, encrypted.length);
            } else {
                encrypted = data;
            }
            Cipher cipher = initCipher(Cipher.DECRYPT_MODE, keyBytes, mode, padding, iv);
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        }

        @Override
        public void encryptFile(String src, String dest, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            byte[] iv = null;
            if (needsIV(mode)) {
                iv = new byte[getIVSize(mode)];
                new SecureRandom().nextBytes(iv);
            }
            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, keyBytes, mode, padding, iv);
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
                if (iv != null) out.write(iv);
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) != -1) {
                    byte[] updated = cipher.update(buf, 0, len);
                    if (updated != null) out.write(updated);
                }
                byte[] fin = cipher.doFinal();
                if (fin != null) out.write(fin);
                out.flush();
            }
        }

        @Override
        public void decryptFile(String src, String dest, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
                byte[] iv = null;
                if (needsIV(mode)) {
                    iv = new byte[getIVSize(mode)];
                    in.read(iv);
                }
                Cipher cipher = initCipher(Cipher.DECRYPT_MODE, keyBytes, mode, padding, iv);
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) != -1) {
                    byte[] updated = cipher.update(buf, 0, len);
                    if (updated != null) out.write(updated);
                }
                byte[] fin = cipher.doFinal();
                if (fin != null) out.write(fin);
                out.flush();
            }
        }
    }

    private static class ChaCha20Impl implements ModernSymmetricCipher {
        @Override
        public String algorithmName() {
            return "ChaCha20-Poly1305";
        }

        @Override
        public String[] supportedModes() {
            return new String[]{"None"};
        }

        @Override
        public String[] supportedPaddings(String mode) {
            return new String[]{"NoPadding"};
        }

        @Override
        public int[] supportedKeySizes() {
            return new int[]{256};
        }

        @Override
        public boolean needsIV(String mode) {
            return true;
        }

        @Override
        public int getIVSize(String mode) {
            return 12;
        }

        @Override
        public String generateKeyBase64(int keySize) throws Exception {
            KeyGenerator kg = KeyGenerator.getInstance("ChaCha20");
            kg.init(256);
            return Base64.getEncoder().encodeToString(kg.generateKey().getEncoded());
        }

        private Cipher initCipher(int opmode, byte[] keyBytes, byte[] iv) throws Exception {
            Cipher cipher = Cipher.getInstance("ChaCha20-Poly1305");
            cipher.init(opmode, new SecretKeySpec(keyBytes, "ChaCha20"), new IvParameterSpec(iv));
            return cipher;
        }

        @Override
        public String encryptText(String plainText, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, keyBytes, iv);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] result = new byte[12 + encrypted.length];
            System.arraycopy(iv, 0, result, 0, 12);
            System.arraycopy(encrypted, 0, result, 12, encrypted.length);
            return Base64.getEncoder().encodeToString(result);
        }

        @Override
        public String decryptText(String cipherBase64, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            byte[] data = Base64.getDecoder().decode(cipherBase64);
            byte[] iv = new byte[12];
            System.arraycopy(data, 0, iv, 0, 12);
            byte[] encrypted = new byte[data.length - 12];
            System.arraycopy(data, 12, encrypted, 0, encrypted.length);
            Cipher cipher = initCipher(Cipher.DECRYPT_MODE, keyBytes, iv);
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        }

        @Override
        public void encryptFile(String src, String dest, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, keyBytes, iv);
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
                out.write(iv);
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) != -1) {
                    byte[] updated = cipher.update(buf, 0, len);
                    if (updated != null) out.write(updated);
                }
                byte[] fin = cipher.doFinal();
                if (fin != null) out.write(fin);
                out.flush();
            }
        }

        @Override
        public void decryptFile(String src, String dest, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
                byte[] iv = new byte[12];
                in.read(iv);
                Cipher cipher = initCipher(Cipher.DECRYPT_MODE, keyBytes, iv);
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) != -1) {
                    byte[] updated = cipher.update(buf, 0, len);
                    if (updated != null) out.write(updated);
                }
                byte[] fin = cipher.doFinal();
                if (fin != null) out.write(fin);
                out.flush();
            }
        }
    }

    private static class RC4Impl implements ModernSymmetricCipher {
        @Override
        public String algorithmName() {
            return "RC4 (ARCFOUR)";
        }

        @Override
        public String[] supportedModes() {
            return new String[]{"None"};
        }

        @Override
        public String[] supportedPaddings(String mode) {
            return new String[]{"NoPadding"};
        }

        @Override
        public int[] supportedKeySizes() {
            return new int[]{40, 56, 64, 128, 256};
        }

        @Override
        public boolean needsIV(String mode) {
            return false;
        }

        @Override
        public int getIVSize(String mode) {
            return 0;
        }

        @Override
        public String generateKeyBase64(int keySize) throws Exception {
            KeyGenerator kg = KeyGenerator.getInstance("ARCFOUR");
            kg.init(keySize);
            return Base64.getEncoder().encodeToString(kg.generateKey().getEncoded());
        }

        @Override
        public String encryptText(String plainText, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            Cipher cipher = Cipher.getInstance("ARCFOUR");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "ARCFOUR"));
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
        }

        @Override
        public String decryptText(String cipherBase64, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            byte[] data = Base64.getDecoder().decode(cipherBase64);
            Cipher cipher = Cipher.getInstance("ARCFOUR");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "ARCFOUR"));
            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        }

        @Override
        public void encryptFile(String src, String dest, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            Cipher cipher = Cipher.getInstance("ARCFOUR");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, "ARCFOUR"));
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) != -1) {
                    byte[] updated = cipher.update(buf, 0, len);
                    if (updated != null) out.write(updated);
                }
                byte[] fin = cipher.doFinal();
                if (fin != null) out.write(fin);
                out.flush();
            }
        }

        @Override
        public void decryptFile(String src, String dest, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            Cipher cipher = Cipher.getInstance("ARCFOUR");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, "ARCFOUR"));
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) != -1) {
                    byte[] updated = cipher.update(buf, 0, len);
                    if (updated != null) out.write(updated);
                }
                byte[] fin = cipher.doFinal();
                if (fin != null) out.write(fin);
                out.flush();
            }
        }
    }

    private static class BCBlockCipherImpl implements ModernSymmetricCipher {
        private final String algorithm;
        private final String[] modes;
        private final int[] keySizes;
        private final int blockSize;

        BCBlockCipherImpl(String algorithm, String[] modes, int[] keySizes, int blockSize) {
            this.algorithm = algorithm;
            this.modes = modes;
            this.keySizes = keySizes;
            this.blockSize = blockSize;
        }

        @Override
        public String algorithmName() {
            return algorithm;
        }

        @Override
        public String[] supportedModes() {
            return modes;
        }

        @Override
        public String[] supportedPaddings(String mode) {
            if ("CTR".equals(mode))
                return new String[]{"NoPadding"};
            return new String[]{"PKCS5Padding", "NoPadding"};
        }

        @Override
        public int[] supportedKeySizes() {
            return keySizes;
        }

        @Override
        public boolean needsIV(String mode) {
            return !"ECB".equals(mode);
        }

        @Override
        public int getIVSize(String mode) {
            return blockSize;
        }

        @Override
        public String generateKeyBase64(int keySize) throws Exception {
            KeyGenerator kg = KeyGenerator.getInstance(algorithm, "BC");
            kg.init(keySize);
            return Base64.getEncoder().encodeToString(kg.generateKey().getEncoded());
        }

        private Cipher initCipher(int opmode, byte[] keyBytes, String mode, String padding, byte[] iv) throws Exception {
            String trans = algorithm + "/" + mode + "/" + padding;
            Cipher cipher = Cipher.getInstance(trans, "BC");
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, algorithm);
            if (!needsIV(mode)) {
                cipher.init(opmode, keySpec);
            } else {
                cipher.init(opmode, keySpec, new IvParameterSpec(iv));
            }
            return cipher;
        }

        @Override
        public String encryptText(String plainText, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            byte[] iv = null;
            if (needsIV(mode)) {
                iv = new byte[getIVSize(mode)];
                new SecureRandom().nextBytes(iv);
            }
            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, keyBytes, mode, padding, iv);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            if (iv != null) {
                byte[] result = new byte[iv.length + encrypted.length];
                System.arraycopy(iv, 0, result, 0, iv.length);
                System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);
                return Base64.getEncoder().encodeToString(result);
            }
            return Base64.getEncoder().encodeToString(encrypted);
        }

        @Override
        public String decryptText(String cipherBase64, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            byte[] data = Base64.getDecoder().decode(cipherBase64);
            byte[] iv = null;
            byte[] encrypted;
            if (needsIV(mode)) {
                int ivLen = getIVSize(mode);
                iv = new byte[ivLen];
                System.arraycopy(data, 0, iv, 0, ivLen);
                encrypted = new byte[data.length - ivLen];
                System.arraycopy(data, ivLen, encrypted, 0, encrypted.length);
            } else {
                encrypted = data;
            }
            Cipher cipher = initCipher(Cipher.DECRYPT_MODE, keyBytes, mode, padding, iv);
            return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
        }

        @Override
        public void encryptFile(String src, String dest, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            byte[] iv = null;
            if (needsIV(mode)) {
                iv = new byte[getIVSize(mode)];
                new SecureRandom().nextBytes(iv);
            }
            Cipher cipher = initCipher(Cipher.ENCRYPT_MODE, keyBytes, mode, padding, iv);
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
                if (iv != null) out.write(iv);
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) != -1) {
                    byte[] updated = cipher.update(buf, 0, len);
                    if (updated != null) out.write(updated);
                }
                byte[] fin = cipher.doFinal();
                if (fin != null) out.write(fin);
                out.flush();
            }
        }

        @Override
        public void decryptFile(String src, String dest, String keyBase64, String mode, String padding) throws Exception {
            byte[] keyBytes = Base64.getDecoder().decode(keyBase64);
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
                 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest))) {
                byte[] iv = null;
                if (needsIV(mode)) {
                    iv = new byte[getIVSize(mode)];
                    in.read(iv);
                }
                Cipher cipher = initCipher(Cipher.DECRYPT_MODE, keyBytes, mode, padding, iv);
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) != -1) {
                    byte[] updated = cipher.update(buf, 0, len);
                    if (updated != null) out.write(updated);
                }
                byte[] fin = cipher.doFinal();
                if (fin != null) out.write(fin);
                out.flush();
            }
        }
    }
}