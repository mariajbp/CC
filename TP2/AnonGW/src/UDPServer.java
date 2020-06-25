import Structs.Container;
import Structs.PacketUDP;
import Structs.Session;
import Structs.SlidingWindow;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;

import java.util.concurrent.BlockingQueue;

/** Thread de leitura, processamento generico e desmultiplexagem de tramas UDP recebidas**/
public class UDPServer implements Runnable {
    /**Port de receçao TCP **/
    private int tcpPort;
    /**Instancia do AnonGW **/
    private AnonGW agw;
    /**Socket de receçao UDP **/
    private DatagramSocket udpSocket;
    /** Endereço do Servidor **/
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
                /**Receçao e validaçao de datagramas UDP receibdos**/
                udpSocket.receive(rec);
                if(agw.isAnonGW(rec.getAddress())){
                    byte[] decrypted;
                Container c = (Container) (new ObjectInputStream(new ByteArrayInputStream(rec.getData())).readObject());
                /**Desencriptaçao**/
                if(!c.isEncrypted()) {decrypted = c.getPacket();}
                else {
                    Key decAes =c.getAesKey(agw.getPrivateKey());
                    decrypted=c.decrypt(decAes); }
                PacketUDP pack =  (PacketUDP) (new ObjectInputStream(new ByteArrayInputStream(decrypted)).readObject());
                /**Processamento da troca de chaves**/
                if(pack.getFlag()==329 || pack.getFlag()==328){

                    agw.initKey(rec.getAddress(),pack.getIdKey());
                    if(pack.getFlag()==328) {
                        agw.replyPublicKeyTo(rec.getAddress());
                    }
                }else{

                int sess = pack.getSessionId();
                if(!agw.hasSession(sess)){
                    /**Inicio de sessão **/

                    Socket server =  new Socket(targetServer,tcpPort);
                    agw.initSession(server,rec.getAddress(),tcpPort,sess, c.getAesKey(agw.getPrivateKey()));
                    new Thread(new TCPInPipe(sess,agw)).start();
                    new Thread(new TCPOutPipe(sess,agw)).start();
                    new Thread(new TCPServerWorker(sess,agw)).start();
                }
                    /**Encaminhamento dos pacotes para os threads de cada sessão (Desmultiplexagem) **/
                Session s = agw.getSession(sess);
                    /**Desvio de ACKs **/
                if(pack.getFlag() == 200){s.getUDPQueue().put(pack);} else {

                    /**Ordenaçao e processamento de perdas**/
                    SlidingWindow ord = s.getRecievingWindow();
                    BlockingQueue<PacketUDP> queue = s.getTCPQueue();

                    int ok =ord.insert(pack.getSeqNo(), pack);
                    if(ok!=0){s.getUDPQueue().put(new PacketUDP(sess,pack.getSeqNo(),201));}
                    PacketUDP[] tmp =ord.retrieve();
                    for (PacketUDP p : tmp) {
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

