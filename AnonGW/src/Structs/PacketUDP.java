package Structs;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;

public class PacketUDP implements Serializable {
    private int sessionId;
    private int seqNo;
    private int flag;
    private Instant stamp;
    private byte[] body;
    private byte[] padding;

    public PacketUDP(int sessionId, byte[] data,int filler, int seqN) {
        this.sessionId=sessionId;
        this.seqNo= seqN;
        this.flag=1;
        this.body=data;
        this.padding = new byte[filler];
    }
    //ACK
    public PacketUDP(int sessionId, int seqN,int  flag) {
        this.sessionId=sessionId;
        this.seqNo= seqN;
        this.flag=flag;
        this.padding = new byte[0];
    }

    public String toString() {
        return "PacketUDP{" +
                "sessionId=" + sessionId +
                ", seqNo=" + seqNo +
                ", flag=" + flag +
                ", body=" + Arrays.toString(body) +
                ", padding=" + Arrays.toString(padding) +
                '}';
    }

    public int getSessionId() {return sessionId;}
    public int getSeqNo() {return seqNo;}
    public int getFlag() {return flag;}
    public void setFlag(int f) { flag=f;}
    public byte[] getBody() {return body;}
    public void setTimeStamp(Instant i){stamp=i;}
    public Instant getTimeStamp(){return stamp;}




}
