import Structs.*;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.BlockingQueue;

import static Structs.Session.WINDOW_SIZE;
/**Thread de envio de dados por UDP**/
public class TCPServerWorker implements Runnable {
    /**Id de sessao**/
    private int sessionId;
    /**Instancia do AnonGW**/
    private AnonGW agw;
    /**Sessão**/
    private Session sess;
    /**Fila de pacotes para serem enviados por UDP**/
    private BlockingQueue<PacketUDP> queue;
    /**Estrutura de controlo de reenvios**/
    SlidingWindow sendingWindow;
    /**Endereço do peer**/
    private InetAddress anonGW;
    /** Port UDP **/
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
            /** Receber os pacotes da fila **/
        while ((p=queue.take()).getFlag()!=-9){
            /** Processar os ACKs **/
            if(p.getFlag()==200){
                sendingWindow.setNull(p.getSeqNo());
            }else {
                ByteArrayOutputStream bos;
                byte[] data;
                DatagramPacket udp;
                /** Obter pacotes para reenvio **/
                PacketUDP[] retries = sendingWindow.retry();
                if(retries.length>0){
                for (PacketUDP retry :retries) queue.put(retry); }
                /** Encaminhamento de ACKs a enviar**/
                if(p.getFlag()==201) p.setFlag(200);else
                     if(!(1==sendingWindow.insert(p.getSeqNo(), p))){
                         queue.put(p);
                         continue;
                     };

                 while(agw.getPublicKeyFrom(anonGW)== null) {agw.requestPublicKeyFrom(anonGW); Thread.sleep(3000);}
                /** Cifragem em AES e wrapping com RSA**/
                Key peerKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(agw.getPublicKeyFrom(anonGW))));
                Key aes = sess.getAesKey();
                byte[] enc = p.encrypt(aes);
                String encAes;
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE,peerKey );
                encAes= Base64.getEncoder().encodeToString(cipher.doFinal(aes.getEncoded()));
                data = new Container(encAes,enc).serialize();
                udp = new DatagramPacket(data, data.length, anonGW, udpPort);


                agw.sendUdp(udp);
            }
        }

        }catch (Exception e){e.printStackTrace();} finally {

        }

    }
}
