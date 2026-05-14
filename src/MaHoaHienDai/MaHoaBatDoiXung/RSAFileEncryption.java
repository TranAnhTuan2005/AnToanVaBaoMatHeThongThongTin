package MaHoaHienDai.MaHoaBatDoiXung;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// Mã hóa hybrid RSA: dùng RSA để mã hóa key đối xứng, rồi dùng key đó mã hóa dữ liệu
public class RSAFileEncryption {
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final SecureRandom srandom = new SecureRandom();

    // Danh sách thuật toán đối xứng hỗ trợ: [tên, transformation, IV size]
    public static final String[][] SUPPORTED_ALGORITHMS = {
        {"AES", "AES/CBC/PKCS5Padding", "16"},
        {"DES", "DES/CBC/PKCS5Padding", "8"},
        {"DESede", "DESede/CBC/PKCS5Padding", "8"},
        {"Blowfish", "Blowfish/CBC/PKCS5Padding", "8"},
        {"RC2", "RC2/CBC/PKCS5Padding", "8"},
    };

    // Lấy danh sách key size hỗ trợ theo tên thuật toán
    public static int[] getKeySizes(String algorithm) {
        switch (algorithm) {
            case "AES": return new int[]{128, 192, 256};
            case "DES": return new int[]{56};
            case "DESede": return new int[]{112, 168};
            case "Blowfish": return new int[]{128, 192, 256, 384, 448};
            case "RC2": return new int[]{40, 64, 128};
            default: return new int[]{128};
        }
    }

    // Lấy transformation string (vd: "AES/CBC/PKCS5Padding")
    public static String getTransformation(String algorithm) {
        for (String[] entry : SUPPORTED_ALGORITHMS) {
            if (entry[0].equals(algorithm)) return entry[1];
        }
        return algorithm + "/CBC/PKCS5Padding";
    }

    // Lấy kích thước IV (byte) theo thuật toán
    public static int getIVSize(String algorithm) {
        for (String[] entry : SUPPORTED_ALGORITHMS) {
            if (entry[0].equals(algorithm)) return Integer.parseInt(entry[2]);
        }
        return 16;
    }

    // Tạo cặp khóa RSA và lưu ra file (Base64)
    public static void generateKeyPair(int rsaKeySize, String publicKeyPath, String privateKeyPath)
            throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(rsaKeySize);
        KeyPair kp = kpg.generateKeyPair();

        // lưu public key
        File pubFile = new File(publicKeyPath);
        if (pubFile.getParentFile() != null && !pubFile.getParentFile().exists()) {
            pubFile.getParentFile().mkdirs();
        }
        try (FileOutputStream out = new FileOutputStream(publicKeyPath)) {
            out.write(encoder.encodeToString(kp.getPublic().getEncoded()).getBytes(StandardCharsets.UTF_8));
        }

        // lưu private key
        File priFile = new File(privateKeyPath);
        if (priFile.getParentFile() != null && !priFile.getParentFile().exists()) {
            priFile.getParentFile().mkdirs();
        }
        try (FileOutputStream out = new FileOutputStream(privateKeyPath)) {
            out.write(encoder.encodeToString(kp.getPrivate().getEncoded()).getBytes(StandardCharsets.UTF_8));
        }
    }

    // Đọc Public Key từ file Base64
    public static PublicKey readPublicKey(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String keyStr = new String(bytes, StandardCharsets.UTF_8).trim();
        byte[] decoded = decoder.decode(keyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    // Đọc Private Key từ file Base64
    public static PrivateKey readPrivateKey(String path) throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String keyStr = new String(bytes, StandardCharsets.UTF_8).trim();
        byte[] decoded = decoder.decode(keyStr);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    // Mã hóa file: sinh key đối xứng ngẫu nhiên -> RSA mã hóa key+IV -> ghi header + data
    public static void encryptFile(PublicKey pub, String inputFile, String outputFile,
                                   String symAlgorithm, int symKeySize) throws Exception {
        // sinh key đối xứng ngẫu nhiên
        KeyGenerator keyGen = KeyGenerator.getInstance(symAlgorithm);
        keyGen.init(symKeySize, srandom);
        SecretKey skey = keyGen.generateKey();

        // sinh IV ngẫu nhiên
        int ivSize = getIVSize(symAlgorithm);
        byte[] iv = new byte[ivSize];
        srandom.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        String transformation = getTransformation(symAlgorithm);

        // dùng RSA mã hóa key đối xứng và IV
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, pub);
        byte[] encryptedKey = rsaCipher.doFinal(skey.getEncoded());
        byte[] encryptedIV = rsaCipher.doFinal(iv);

        // ghi file: header (thuật toán, key size, transformation, encrypted key, encrypted IV) + data
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(outputFile)))) {
            dos.writeUTF(symAlgorithm);
            dos.writeInt(symKeySize);
            dos.writeUTF(transformation);

            dos.writeInt(encryptedKey.length);
            dos.write(encryptedKey);

            dos.writeInt(encryptedIV.length);
            dos.write(encryptedIV);

            // mã hóa nội dung file bằng thuật toán đối xứng
            Cipher symCipher = Cipher.getInstance(transformation);
            symCipher.init(Cipher.ENCRYPT_MODE, skey, ivSpec);

            try (FileInputStream fis = new FileInputStream(inputFile);
                 CipherOutputStream cos = new CipherOutputStream(dos, symCipher)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    cos.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    // Giải mã file: đọc header -> RSA giải mã key+IV -> giải mã data bằng key đối xứng
    public static void decryptFile(PrivateKey pri, String inputFile, String outputFile) throws Exception {
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(inputFile)))) {
            // đọc header
            String symAlgorithm = dis.readUTF();
            int symKeySize = dis.readInt();
            String transformation = dis.readUTF();

            int keyLen = dis.readInt();
            byte[] encryptedKey = new byte[keyLen];
            dis.readFully(encryptedKey);

            int ivLen = dis.readInt();
            byte[] encryptedIV = new byte[ivLen];
            dis.readFully(encryptedIV);

            // dùng RSA giải mã key đối xứng và IV
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.DECRYPT_MODE, pri);
            byte[] symKeyBytes = rsaCipher.doFinal(encryptedKey);
            byte[] iv = rsaCipher.doFinal(encryptedIV);

            SecretKey skey = new SecretKeySpec(symKeyBytes, symAlgorithm);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // giải mã nội dung file
            Cipher symCipher = Cipher.getInstance(transformation);
            symCipher.init(Cipher.DECRYPT_MODE, skey, ivSpec);

            try (CipherInputStream cis = new CipherInputStream(dis, symCipher);
                 FileOutputStream fos = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    // Mã hóa văn bản (tương tự file nhưng kết quả trả về Base64)
    public static String encryptText(PublicKey pub, String plainText,
                                     String symAlgorithm, int symKeySize) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(symAlgorithm);
        keyGen.init(symKeySize, srandom);
        SecretKey skey = keyGen.generateKey();

        int ivSize = getIVSize(symAlgorithm);
        byte[] iv = new byte[ivSize];
        srandom.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        String transformation = getTransformation(symAlgorithm);

        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, pub);
        byte[] encryptedKey = rsaCipher.doFinal(skey.getEncoded());
        byte[] encryptedIV = rsaCipher.doFinal(iv);

        Cipher symCipher = Cipher.getInstance(transformation);
        symCipher.init(Cipher.ENCRYPT_MODE, skey, ivSpec);
        byte[] encryptedContent = symCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // đóng gói tất cả vào 1 mảng byte rồi encode Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeUTF(symAlgorithm);
        dos.writeInt(symKeySize);
        dos.writeUTF(transformation);
        dos.writeInt(encryptedKey.length);
        dos.write(encryptedKey);
        dos.writeInt(encryptedIV.length);
        dos.write(encryptedIV);
        dos.write(encryptedContent);
        dos.flush();

        return encoder.encodeToString(baos.toByteArray());
    }

    // Giải mã văn bản từ chuỗi Base64
    public static String decryptText(PrivateKey pri, String cipherBase64) throws Exception {
        byte[] data = decoder.decode(cipherBase64);
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));

        String symAlgorithm = dis.readUTF();
        int symKeySize = dis.readInt();
        String transformation = dis.readUTF();

        int keyLen = dis.readInt();
        byte[] encryptedKey = new byte[keyLen];
        dis.readFully(encryptedKey);

        int ivLen = dis.readInt();
        byte[] encryptedIV = new byte[ivLen];
        dis.readFully(encryptedIV);

        byte[] encryptedContent = dis.readAllBytes();

        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, pri);
        byte[] symKeyBytes = rsaCipher.doFinal(encryptedKey);
        byte[] iv = rsaCipher.doFinal(encryptedIV);

        SecretKey skey = new SecretKeySpec(symKeyBytes, symAlgorithm);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher symCipher = Cipher.getInstance(transformation);
        symCipher.init(Cipher.DECRYPT_MODE, skey, ivSpec);
        byte[] plainBytes = symCipher.doFinal(encryptedContent);

        return new String(plainBytes, StandardCharsets.UTF_8);
    }
}
