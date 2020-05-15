import Structs.PacketUDP;
import Structs.Session;

import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
//Just a tap on a tcp InputStream
public class TCPInPipe implements Runnable {
    private int sessionId;
    private BlockingQueue<PacketUDP> queue;
    private InputStream tcpIn;
    private int baseSeqNo;
    private Socket tcpSocket;
    public TCPInPipe(int sess, AnonGW agw) {
        sessionId = sess;
        Session session = agw.getSession(sess);
        this.queue=session.getUDPQueue();
        this.tcpSocket = session.getTcpSock();
        this.baseSeqNo = (int)(Math.random()*Integer.MAX_VALUE);
    }

    public void run() {
        try{
            tcpIn = tcpSocket.getInputStream();
            int count;
            int seqNo = baseSeqNo+1;
            byte[] buf = new byte[AnonGW.BODY_MAX_SIZE];
            while ((count = tcpIn.read(buf))>0) {
                PacketUDP packet = new PacketUDP(sessionId, Arrays.copyOf(buf, count), AnonGW.BODY_MAX_SIZE - count, seqNo++);
                queue.put(packet);
            }
            }catch (Exception e){e.printStackTrace();}


    }
}
