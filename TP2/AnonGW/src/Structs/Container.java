package Structs;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Key;
import java.security.PrivateKey;
import java.util.Base64;
/** Envelope criptografico de PacketUDP **/
public class Container implements Serializable {
    private String aesKey;
    private byte[] packet;
    /** Wrapper generico com chave e dados encriptados **/
    public Container(String aesKey, byte[] packet) {
        this.aesKey = aesKey;
        this.packet = packet;
    }

    /**Wrapper para dados não encriptados **/
    public Container( byte[] pack) {
        this.packet = pack;
    }

    /**Metodo que serializa a classe num array de bytes **/
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
    /**Metodo que extrai os dados desencriptados dada a sua chave AES **/
    public byte[] decrypt( Key key){
        byte[] ret = null;
       try {
           Cipher cipher = Cipher.getInstance("AES");
           cipher.init(Cipher.DECRYPT_MODE, key);
           ret = cipher.doFinal(packet);
       }catch (Exception e){e.printStackTrace();}
       return  ret;
    }
    /**Metodo que extrai a chave AES dos dados dada a chave privada do AnonGW **/
    public Key getAesKey(PrivateKey privateK) {
        byte[] encAes =Base64.getDecoder().decode(aesKey);
        Key aes = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateK);
            aes = new SecretKeySpec(cipher.doFinal(encAes), "AES");
        }catch (Exception e){e.printStackTrace();}

        return aes;
    }


    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }
    /**Metodo que retorna um array de bytes dos dados sem desencriptar **/
    public byte[] getPacket() {
        return packet;
    }

    public void setPacket(byte[] packet) {
        this.packet = packet;
    }
    /**Metodo que verifica se os dados vêm encriptados **/
    public boolean isEncrypted() {
        return aesKey != null;
    }
}
