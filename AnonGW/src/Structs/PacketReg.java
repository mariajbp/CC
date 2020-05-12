package Structs;

public class PacketReg {
    int seqNo;



    PacketUDP pack;

    public PacketReg(PacketUDP pack){
        this.seqNo = pack.getSeqNo();
        this.pack = pack;
    }
    public PacketUDP getPack() {
        return pack;
    }
    public int getSeqNo(){return seqNo;}

}
