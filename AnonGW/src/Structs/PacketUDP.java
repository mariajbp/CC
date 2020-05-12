package Structs;

import java.io.Serializable;
import java.util.Arrays;

public class PacketUDP implements Serializable {
    private int sessionId;
    private int seqNo;
    private int flag;
    private byte[] body;
    private byte[] padding;

    public PacketUDP(int sessionId, byte[] data,int filler, int seqN) {
        this.sessionId=sessionId;
        this.seqNo= seqN;
        this.flag=1;
        this.body=data;
        this.padding = new byte[filler];
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
    public byte[] getBody() {return body;}



}
