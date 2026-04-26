package MaHoaCoDien;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class MaHoaHoanVi {

    SecretKey key;
    private int[] permutation = new int[]{0, 1, 2, 3};

    public SecretKey genKey() throws NoSuchAlgorithmException {
        int keySize = 6; // key size: độ dài hoán vị cột
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < keySize; i++) order.add(i);
        Collections.shuffle(order, ClassicalCipherSupport.RANDOM);
        permutation = new int[keySize];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keySize; i++) {
            permutation[i] = order.get(i);
            if (i > 0) sb.append(',');
            sb.append(permutation[i]);
        }
        key = ClassicalCipherSupport.keyFromString(sb.toString());
        return key;
    }


    public void loadKey(SecretKey key)    {
        this.key = key;
        String raw = ClassicalCipherSupport.keyToString(key);
        String[] parts = raw.split(",");
        permutation = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            permutation[i] = Integer.parseInt(parts[i].trim());
        }
    }



    public byte[] encrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        List<Integer> positions = ClassicalCipherSupport.supportedPositions(text);
        char[] supported = ClassicalCipherSupport.supportedChars(text, positions);
        char[] encrypted = transposeEncrypt(supported, permutation);
        String merged = ClassicalCipherSupport.mergeBack(text, positions, encrypted);
        return merged.getBytes(StandardCharsets.UTF_8);

    }

    public String encryptBase64(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return Base64.getEncoder().encodeToString(encrypt(text));

    }

    public String Decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        String cipherText = new String(data, StandardCharsets.UTF_8);
        List<Integer> positions = ClassicalCipherSupport.supportedPositions(cipherText);
        char[] supported = ClassicalCipherSupport.supportedChars(cipherText, positions);
        char[] decrypted = transposeDecrypt(supported, permutation);
        return ClassicalCipherSupport.mergeBack(cipherText, positions, decrypted);
    }


    private char[] transposeEncrypt(char[] input, int[] perm) {
        int cols = perm.length;
        int n = input.length;
        int rows = (n + cols - 1) / cols;

        char[][] grid = new char[rows][cols];
        int idx = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (idx < n) grid[r][c] = input[idx++];
            }
        }

        char[] out = new char[n];
        idx = 0;
        for (int k = 0; k < cols; k++) {
            int col = perm[k];
            for (int r = 0; r < rows; r++) {
                int pos = r * cols + col;
                if (pos < n) out[idx++] = grid[r][col];
            }
        }
        return out;
    }
    private char[] transposeDecrypt(char[] input, int[] perm) {
        int cols = perm.length;
        int n = input.length;
        int rows = (n + cols - 1) / cols;

        int[] colHeights = new int[cols];
        for (int c = 0; c < cols; c++) {
            int count = 0;
            for (int r = 0; r < rows; r++) {
                if (r * cols + c < n) count++;
            }
            colHeights[c] = count;
        }

        char[][] grid = new char[rows][cols];
        int idx = 0;
        for (int k = 0; k < cols; k++) {
            int col = perm[k];
            for (int r = 0; r < colHeights[col]; r++) {
                grid[r][col] = input[idx++];
            }
        }

        char[] out = new char[n];
        idx = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r * cols + c < n) out[idx++] = grid[r][c];
            }
        }
        return out;
    }

}
