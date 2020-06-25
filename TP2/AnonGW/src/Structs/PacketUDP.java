package Structs;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Key;
import java.time.Instant;

/** Estrutura protocolar de transferencia de dados por UDP **/
public class PacketUDP implements Serializable {
    /** Id da sessao**/
    private int sessionId;
    /** Numero de sequencia do pacote**/
    private int seqNo;
    /** Flag multiusos**/
    private int flag;
    /** Slot para uma possivel troca de chaves**/
    private String idKey;
    /** Timestamp da criaçao ou atualizaçao de um pacote**/
    private Instant stamp;
    /** Corpo do pacote**/
    private byte[] body;
    /** Padding para balanço do tamanho do pacote**/
    private byte[] padding;

    /**Construtor generico de um pacote **/
    public PacketUDP(int sessionId, byte[] data,int filler, int seqN) {
        this.sessionId=sessionId;
        this.seqNo= seqN;
        this.flag=1;
        this.body=data;
        this.padding = new byte[filler];
        this.stamp= Instant.now();
    }
    /**Construtor de um pacote de controlo sem dados **/
    public PacketUDP(int sessionId, int seqN,int  flag) {
        this.sessionId=sessionId;
        this.seqNo= seqN;
        this.flag=flag;
        this.padding = new byte[0];
        this.stamp= Instant.now();
    }
    /**Construtor de um pacote de troca de chaves**/
    public PacketUDP(String publicKey ,int  flag) {
        this.flag=flag;
        idKey = publicKey;
        this.padding = new byte[0];
        this.stamp= Instant.now();
    }

    /**Metodo que serializa o pacote num array de bytes**/
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

    /**Metodo que encripta um pacote num array de bytes dado uma chave simetrica AES **/
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
