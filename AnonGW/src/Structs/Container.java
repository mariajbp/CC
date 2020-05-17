package Structs;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Container implements Serializable {
    private String aesKey;
    private byte[] packet;

    public Container(String aesKey, byte[] packet) {
        this.aesKey = aesKey;
        this.packet = packet;
    }
    public Container(String aesKey) {
        this.aesKey = aesKey;
    }
    // Unencrypted
    public Container( byte[] pack) {
        this.packet = pack;
    }


    public byte[] serialize(){
        byte[] data = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            data = bos.toByteArray();
            bos.close();
        }catch (Exception e){e.printStackTrace();}

        return data;
    }
    public byte[] decrypt( Key key){
        byte[] ret = null;
       try {
           Cipher cipher = Cipher.getInstance("AES");
           cipher.init(Cipher.DECRYPT_MODE, key);
           ret = cipher.doFinal(packet);
       }catch (Exception e){e.printStackTrace();}
       return  ret;
    }

    public Key getAesKey(PrivateKey privateK) {
        byte[] encAes =Base64.getDecoder().decode(aesKey);
        System.out.println(aesKey +" || "+ encAes.length);
        Key aes = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateK);
            aes = new SecretKeySpec(cipher.doFinal(encAes), "AES");
        }catch (Exception e){e.printStackTrace();}

        return aes;
    }
    public Key getAesKey(String publicKeyFrom){
        byte[] encAes =Base64.getDecoder().decode(aesKey);
        Key aes = null;
        try {

            Key rsa = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyFrom)));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, rsa);
            aes = new SecretKeySpec(cipher.doFinal(encAes), "AES");
        }catch (Exception e){e.printStackTrace();}

        return aes;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public byte[] getPacket() {
        return packet;
    }

    public void setPacket(byte[] packet) {
        this.packet = packet;
    }

    public boolean isEncrypted() {
        return aesKey != null;
    }
}
