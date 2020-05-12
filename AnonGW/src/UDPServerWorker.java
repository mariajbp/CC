import Structs.PacketUDP;
import Structs.Session;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class UDPServerWorker implements Runnable {
    private int sessionId;
    private AnonGW agw;
    private Session session;
    private BlockingQueue<PacketUDP> queue;
    private Socket tcpSocket;
    public UDPServerWorker(int sess, AnonGW agw) {
        sessionId = sess;
        this.agw=agw;
        this.session = agw.getSession(sess);
        this.queue=session.getQueue();
        this.tcpSocket = session.getTcpSock();
        System.out.println("EU PRINTO, LOGO EXISTO");
    }

    public void run() {
      byte[] buf = new byte[AnonGW.BODY_MAX_SIZE];
      int flag=1;
     try{
         while(flag>0){
             PacketUDP pack = queue.take();
             //System.out.println("Flag :"+flag );
             flag=pack.getFlag();
             buf = pack.getBody();
             //System.out.println(new String(buf));
             tcpSocket.getOutputStream().write(buf);

         }
     }catch (Exception e){e.printStackTrace();}


    }
}
