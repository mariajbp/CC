package Structs;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Arrays;

public class PacketUDP implements Serializable {
    private int sessionId;
    private int seqNo;
    private int flag;
    private String idKey;
    private Instant stamp;
    private byte[] body;
    private byte[] padding;

    public PacketUDP(int sessionId, byte[] data,int filler, int seqN) {
        this.sessionId=sessionId;
        this.seqNo= seqN;
        this.flag=1;
        this.body=data;
        this.padding = new byte[filler];
        this.stamp= Instant.now();
    }
    //ACK
    public PacketUDP(int sessionId, int seqN,int  flag) {
        this.sessionId=sessionId;
        this.seqNo= seqN;
        this.flag=flag;
        this.padding = new byte[0];
        this.stamp= Instant.now();
    }
    // Key
    public PacketUDP(String publicKey ,int  flag) {
        this.flag=flag;
        idKey = publicKey;
        this.padding = new byte[0];
        this.stamp= Instant.now();
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


    @Override
    public String toString() {
        return "PacketUDP{" +
                "sessionId=" + sessionId +
                "|| seqNo=" + seqNo +
                "|| flag=" + flag +
                "|| key=" + idKey +
                "|| stamp=" + stamp +
                "|| body=" + (body==null? -1 : body.length) +
                "|| padding=" + padding.length +
                '}';
    }

    public int getSessionId() {return sessionId;}
    public int getSeqNo() {return seqNo;}
    public int getFlag() {return flag;}
    public void setFlag(int f) { flag=f;}
    public byte[] getBody() {return body;}
    public void setTimeStamp(Instant i){stamp=i;}
    public Instant getTimeStamp(){return stamp;}
    public String getIdKey(){return idKey;}


    public byte[] encrypt(Key peerKey) {
        byte[] ret=null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, peerKey);
            ret= cipher.doFinal(this.serialize());
        }catch (Exception e){e.printStackTrace();}
        return ret;
    }
}
