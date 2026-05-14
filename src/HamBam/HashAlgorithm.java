package HamBam;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

// Lớp xử lý các giải thuật băm (hash), hỗ trợ cả JDK lẫn BouncyCastle
public class HashAlgorithm {

    // Đăng ký BouncyCastle để dùng thêm RIPEMD160, Whirlpool
    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    // Danh sách tên các thuật toán được hỗ trợ
    public static final String[] SUPPORTED = {
        "MD5", "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512",
        "SHA-512/224", "SHA-512/256",
        "SHA3-224", "SHA3-256", "SHA3-384", "SHA3-512",
        "RIPEMD160", "Whirlpool"
    };

    // Thông tin chi tiết: [tên, output bits, block size bits]
    public static final String[][] ALGORITHM_INFO = {
        {"MD5",        "128", "512"},
        {"SHA-1",      "160", "512"},
        {"SHA-224",    "224", "512"},
        {"SHA-256",    "256", "512"},
        {"SHA-384",    "384", "1024"},
        {"SHA-512",    "512", "1024"},
        {"SHA-512/224","224", "1024"},
        {"SHA-512/256","256", "1024"},
        {"SHA3-224",   "224", "1152"},
        {"SHA3-256",   "256", "1088"},
        {"SHA3-384",   "384", "832"},
        {"SHA3-512",   "512", "576"},
        {"RIPEMD160",  "160", "512"},
        {"Whirlpool",  "512", "512"},
    };

    private final String algorithmName;
    private final MessageDigest digest;

    // Khởi tạo với tên giải thuật, lấy instance từ BouncyCastle provider
    public HashAlgorithm(String algorithmName) throws NoSuchAlgorithmException {
        this.algorithmName = algorithmName;
        this.digest = MessageDigest.getInstance(algorithmName, Security.getProvider("BC"));
    }

    public String getName() {
        return algorithmName;
    }

    // Trả về số bit đầu ra của giải thuật (vd: SHA-256 -> 256)
    public int getOutputBits() {
        for (String[] info : ALGORITHM_INFO) {
            if (info[0].equals(algorithmName)) return Integer.parseInt(info[1]);
        }
        return digest.getDigestLength() * 8;
    }

    // Trả về kích thước block (bit) của giải thuật
    public int getBlockBits() {
        for (String[] info : ALGORITHM_INFO) {
            if (info[0].equals(algorithmName)) return Integer.parseInt(info[2]);
        }
        return 512;
    }

    // Băm chuỗi văn bản, trả về hex string
    public String hashText(String text) {
        digest.reset();
        byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashBytes);
    }

    // Băm file theo từng khối 8KB để tiết kiệm bộ nhớ
    public String hashFile(String filePath) throws IOException {
        digest.reset();
        try (InputStream fis = new BufferedInputStream(new FileInputStream(filePath))) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        return bytesToHex(digest.digest());
    }

    // So sánh hash của văn bản với hash mong đợi
    public boolean verifyText(String text, String expectedHash) {
        return hashText(text).equalsIgnoreCase(expectedHash.trim());
    }

    // So sánh hash của file với hash mong đợi
    public boolean verifyFile(String filePath, String expectedHash) throws IOException {
        return hashFile(filePath).equalsIgnoreCase(expectedHash.trim());
    }

    // Chuyển mảng byte sang chuỗi hex
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
