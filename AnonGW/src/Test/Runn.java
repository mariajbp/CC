package Test;

import Structs.PacketUDP;
import Structs.SocketCircularBuffer;
import static java.lang.System.out;

import java.util.Random;

public class Runn implements Runnable {
    SocketCircularBuffer<Integer,PacketUDP> buffer;
    int id;
    int seqBase;
    public Runn(SocketCircularBuffer<Integer, PacketUDP> b, int i, int r){
        buffer=b;
        id=i;
        seqBase = r;
    }

    public void run() {

        for (int j = 0;j<4;j++){
            PacketUDP p = new PacketUDP(id, new byte[1], 0, seqBase+j);
            buffer.insert(p,p.getSeqNo());
            if(id ==1) out.println(p);
        }
        out.println(id + "ADD DONE");

    }
}
