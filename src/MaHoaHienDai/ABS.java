package MaHoaHienDai;

import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ABS {
    SecretKey key;

    public SecretKey genKey() throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance("MaHoaVigenère");
        //kg.init(...); //key size
        key = kg.generateKey();
        return key;
    }


    public void loadKey(SecretKey key)    {
        this.key = key;
    }



    public byte[] encrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher=Cipher.getInstance("MaHoaVigenère");
        cipher.init(Cipher.ENCRYPT_MODE, this.key);
        byte[] data= text.getBytes(StandardCharsets.UTF_8);
        return cipher.doFinal(data);

    }

    public String encryptBase64(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return Base64.getEncoder().encodeToString(encrypt(text));

    }

    public String Decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher=Cipher.getInstance("MaHoaVigenère");
        cipher.init(Cipher.DECRYPT_MODE, this.key);
        byte[] bytes= cipher.doFinal(data);
        return new String(bytes, StandardCharsets.UTF_8);
    }


}
