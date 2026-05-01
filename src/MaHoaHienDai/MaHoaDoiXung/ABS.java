package MaHoaHienDai.MaHoaDoiXung;

import javax.crypto.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ABS {
    SecretKey key;

    public SecretKey genKey() throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance("ABS");
        //kg.init(...); //key size
        key = kg.generateKey();
        return key;
    }


    public void loadKey(SecretKey key)    {
        this.key = key;
    }



    public byte[] encrypt(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher=Cipher.getInstance("");//mode and padding
        cipher.init(Cipher.ENCRYPT_MODE, this.key);
        byte[] data= text.getBytes(StandardCharsets.UTF_8);
        return cipher.doFinal(data);

    }

    public String encryptBase64(String text) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return Base64.getEncoder().encodeToString(encrypt(text));

    }

    public String Decrypt(byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher=Cipher.getInstance("");//mode and padding
        cipher.init(Cipher.DECRYPT_MODE, this.key);
        byte[] bytes= cipher.doFinal(data);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public boolean encryptFile(String src, String dec) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("");//mode and padding
        cipher.init(Cipher.ENCRYPT_MODE, this.key);
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dec));
        CipherInputStream in = new CipherInputStream(input, cipher);
        int i;
        byte[] read = new byte[1024];
        byte[] re = null;
        while ((i = in.read(read)) != -1) {
            output.write(read, 0, i);

        }
        read = cipher.doFinal();
        if (read != null) {
            output.write(read);
        }
        in.close();
        output.flush();
        output.close();
        return true;
    }

    public boolean decryptFile(String src, String dec) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("");//mode and padding
        cipher.init(Cipher.DECRYPT_MODE, this.key);
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(src));
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(dec));
        CipherInputStream in = new CipherInputStream(input, cipher);
        int i;
        byte[] read = new byte[1024];
        byte[] re = null;
        while ((i=input.read())!=-1){
            output.write(read,0,i);
        }
        read= cipher.doFinal();
        if (read != null) {
            output.write(read);
        }
        in.close();
        output.flush();
        output.close();
        return true;
    }




}
