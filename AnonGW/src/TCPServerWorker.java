import Structs.*;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.BlockingQueue;

import static Structs.Session.WINDOW_SIZE;

public class TCPServerWorker implements Runnable {
    private int sessionId;
    private AnonGW agw;
    private Session sess;
    private BlockingQueue<PacketUDP> queue;
    SlidingWindow sendingWindow;
    private InetAddress anonGW;
    private int udpPort;

    public TCPServerWorker(int sessId,AnonGW agw) {
        sessionId=sessId;
        this.agw=agw;
        sess = agw.getSession(sessionId);
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
               // System.out.println( "B4Retry :"+sendingWindow);
                PacketUDP[] retries = sendingWindow.retry();
                System.out.println(retries.length);
                if(retries.length>0){
                for (PacketUDP retry :retries){

                    Key peerKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(agw.getPublicKeyFrom(anonGW))));
                    Key aes = sess.getAesKey();
                    byte[] enc = retry.encrypt(aes);
                    String encAes;
                    Cipher cipher = Cipher.getInstance("RSA");
                    cipher.init(Cipher.ENCRYPT_MODE,peerKey );
                    encAes= Base64.getEncoder().encodeToString(cipher.doFinal(aes.getEncoded()));
                    data = new Container(encAes,enc).serialize();

                    //Send
                    udp = new DatagramPacket(data, data.length, anonGW, udpPort);
                    agw.sendUdp(udp);
                    retry.setFlag(2);
                    queue.put(retry);
                }}
                //{Dont look}
                if(p.getFlag()==201) p.setFlag(200);else
                     if(p.getFlag()!=2&&!(1==sendingWindow.insert(p.getSeqNo(), p))){
                         queue.put(p);
                         continue;
                     };
                //Serialize
                System.out.println("PACKTOUDP :"+p.toString());
                while(agw.getPublicKeyFrom(anonGW)== null) {agw.requestPublicKeyFrom(anonGW); Thread.sleep(3000);}
                Key peerKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(agw.getPublicKeyFrom(anonGW))));
                Key aes = sess.getAesKey();
                byte[] enc = p.encrypt(aes);
                String encAes;
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE,peerKey );
                encAes= Base64.getEncoder().encodeToString(cipher.doFinal(aes.getEncoded()));
                data = new Container(encAes,enc).serialize();
                //Send
                udp = new DatagramPacket(data, data.length, anonGW, udpPort);


                agw.sendUdp(udp);
            }
        }

        }catch (Exception e){e.printStackTrace();} finally {

        }

    }
}
