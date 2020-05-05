import Structs.PacketUDP;
import Structs.Session;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class TCPServerWorker implements Runnable {
    private int sessionId;
    private AnonGW agw;
    private Session sess;
    private Socket tcpSocket;
    private InputStream tcpIn;
    private int baseSeqNo;
    private InetAddress anonGW;
    private int udpPort;

    public TCPServerWorker(int sessId,AnonGW agw) {
        sessionId=sessId;
        this.agw=agw;
        sess = agw.getSession(sessionId);
        tcpSocket = sess.getTcpSock();
        baseSeqNo = (int)(Math.random()*Integer.MAX_VALUE);
        anonGW =  sess.getAnonGWAddress();
        udpPort =  sess.getUdpPort();


    }
    public void run() {
        try{tcpIn = tcpSocket.getInputStream();
        int count;
        int seqNo = baseSeqNo+1;
        byte[] buf = new byte[AnonGW.BODY_MAX_SIZE];
        while ((count = tcpIn.read(buf))>0){
            //Build
            PacketUDP packet = new PacketUDP(sessionId,Arrays.copyOf(buf,count),AnonGW.BODY_MAX_SIZE-count,seqNo++);
            //SERIALIZE
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(packet); out.flush();
            byte[] data = bos.toByteArray();
            bos.close();
            //Send
            DatagramPacket udp =  new DatagramPacket(data,data.length,anonGW,udpPort);
            agw.sendUdp(udp);
        }

        }catch (Exception e){e.printStackTrace();} finally {

        }

    }
}
