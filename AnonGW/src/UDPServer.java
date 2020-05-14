import Structs.PacketUDP;
import Structs.Session;
import Structs.SlidingWindow;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

//UDP socket listening router
public class UDPServer implements Runnable {
    private int tcpPort;
    private AnonGW agw;
    private DatagramSocket udpSocket;
    private InetAddress targetServer;
    public UDPServer(AnonGW anonGW, DatagramSocket ds, int tcpPort, InetAddress targetServer){
           agw=anonGW;
           udpSocket=ds;
           this.tcpPort= tcpPort;
           this.targetServer=targetServer;
    }
    public void run() {
        try {
            byte[] buffer = new byte[64 * 1024];
            DatagramPacket rec = new DatagramPacket(buffer, buffer.length);
            while (true) {
                //Receive
                udpSocket.receive(rec);
                PacketUDP pack = (PacketUDP) (new ObjectInputStream(new ByteArrayInputStream(rec.getData())).readObject());
                //System.out.println(pack.toString());
                int sess = pack.getSessionId();
                if(!agw.hasSession(sess)){
                    Socket server =  new Socket(targetServer,tcpPort);
                    agw.initSession(server,rec.getAddress(),tcpPort,sess);
                    new Thread(new TCPOutPipe(sess,agw)).start();
                    new Thread(new TCPServerWorker(sess,agw)).start();
                }
                Session s = agw.getSession(sess);
                //Ack check
                if(pack.getFlag() == 200){s.getUDPQueue().put(pack);} else {
                    SlidingWindow ord = s.getRecievingWindow();
                    BlockingQueue<PacketUDP> queue = s.getTCPQueue();
                    boolean ok =ord.insert(pack.getSeqNo(), pack);
                    if(ok){s.getUDPQueue().put(new PacketUDP(sess,pack.getSeqNo(),201));}
                    for (PacketUDP p : ord.retrieve()) {
                        queue.put(p);
                    }
                }
                    //System.out.println(s.toString());


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

