import Structs.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;

import static Structs.Session.WINDOW_SIZE;

public class TCPServerWorker implements Runnable {
    private int sessionId;
    private AnonGW agw;
    private BlockingQueue<PacketUDP> queue;
    SlidingWindow sendingWindow;
    private InetAddress anonGW;
    private int udpPort;

    public TCPServerWorker(int sessId,AnonGW agw) {
        sessionId=sessId;
        this.agw=agw;
        System.out.println(agw);
        Session sess = agw.getSession(sessionId);
        queue= sess.getUDPQueue();
        anonGW =  sess.getAnonGWAddress();
        udpPort =  sess.getUdpPort();
        sendingWindow= new SlidingWindow(WINDOW_SIZE);
    }

    public void run() {
        try{
            PacketUDP p;
        while ((p=queue.take()).getFlag()!=9){
            //Ack Check
            if(p.getFlag()==200){

                sendingWindow.setNull(p.getSeqNo());
                System.out.println(sendingWindow.toString());
            }else {
                //Serialization artifice
                ByteArrayOutputStream bos;
                byte[] data;
                DatagramPacket udp;
                //Retry - higher priority
                System.out.println( "B4Retry :"+sendingWindow);
                PacketUDP[] retries = sendingWindow.retry();
                if(retries.length>0){
                for (PacketUDP retry :retries){
                    //Serialize
                    bos = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bos);
                    out.writeObject(retry);
                    out.flush();
                    data = bos.toByteArray();
                    bos.close();
                    //Send
                    udp = new DatagramPacket(data, data.length, anonGW, udpPort);
                    agw.sendUdp(udp);
                }}
                //{Dont look}
                if(p.getFlag()==201) p.setFlag(200);else
                    sendingWindow.insert(p.getSeqNo(), p);
                //Serialize
                System.out.println("PACKTOUDP :"+p.toString());
                bos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bos);
                out.writeObject(p);
                out.flush();
                data = bos.toByteArray();
                bos.close();
                //Send
                udp = new DatagramPacket(data, data.length, anonGW, udpPort);


                agw.sendUdp(udp);
            }
        }

        }catch (Exception e){e.printStackTrace();} finally {

        }

    }
}
