package Test;

import Structs.PacketUDP;
import Structs.SocketCircularBuffer;

import static java.lang.System.out;

public class Run2 implements Runnable {
    SocketCircularBuffer<Integer,PacketUDP> buffer;
    int id;
    int remove;
    public Run2(SocketCircularBuffer<Integer, PacketUDP> b, int i, int r){
        buffer=b;
        id=i;
        remove = r;
    }

    public void run() {
        buffer.remove(remove);
        out.println(id + " REM DONE "+ remove);

    }
}
