import Structs.Container;
import Structs.PacketUDP;
import Structs.Session;
import Structs.SlidingWindow;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;
import java.util.Arrays;
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
                System.out.println("RECEIVED SOMETHINGGG :"+ rec.getAddress());
                if(agw.isAnonGW(rec.getAddress())){
                    byte[] decrypted;
                Container c = (Container) (new ObjectInputStream(new ByteArrayInputStream(rec.getData())).readObject());

                if(!c.isEncrypted()) {decrypted = c.getPacket();}
                else {
                    Key decAes =c.getAesKey(agw.getPrivateKey());
                    decrypted=c.decrypt(decAes); }
                PacketUDP pack =  (PacketUDP) (new ObjectInputStream(new ByteArrayInputStream(decrypted)).readObject());
                System.out.println("RECEIVED SOMETHING :"+pack);
                if(pack.getFlag()==329 || pack.getFlag()==328){

                    agw.initKey(rec.getAddress(),pack.getIdKey());
                    if(pack.getFlag()==328) {
                        agw.replyPublicKeyTo(rec.getAddress());
                    }
                }else{

                //System.out.println(pack.toString());
                int sess = pack.getSessionId();
                if(!agw.hasSession(sess)){
                    Socket server =  new Socket(targetServer,tcpPort);
                    agw.initSession(server,rec.getAddress(),tcpPort,sess, c.getAesKey(agw.getPrivateKey()));
                    new Thread(new TCPInPipe(sess,agw)).start();
                    new Thread(new TCPOutPipe(sess,agw)).start();
                    new Thread(new TCPServerWorker(sess,agw)).start();
                }
                Session s = agw.getSession(sess);
                //Ack check
                if(pack.getFlag() == 200){s.getUDPQueue().put(pack);} else {

                    SlidingWindow ord = s.getRecievingWindow();
                    BlockingQueue<PacketUDP> queue = s.getTCPQueue();

                    System.out.println(queue.remainingCapacity()+ "|||" + queue.size());
                    int ok =ord.insert(pack.getSeqNo(), pack);
                    if(ok!=0)System.out.println("ACK"); else{System.out.println(ord);}
                    if(ok!=0){s.getUDPQueue().put(new PacketUDP(sess,pack.getSeqNo(),201));}
                    PacketUDP[] tmp =ord.retrieve();
                    System.out.println("PACK ARRAY :" + Arrays.toString(tmp) );
                    for (PacketUDP p : tmp) {

                        System.out.println("Q :"+queue);
                        queue.put(p);
                    }
                }


                }
                }}
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

