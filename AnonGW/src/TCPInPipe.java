import Structs.PacketUDP;
import Structs.Session;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
/**Extrai dados de um socket TCP **/
public class TCPInPipe implements Runnable {
    /**Id de sessão **/
    private int sessionId;
    /**Fila de Pacotes a enviar por UDP**/
    private BlockingQueue<PacketUDP> queue;
    /**Input Stream do socket TCP **/
    private InputStream tcpIn;
    /**Numero de Sequencia Base**/
    private int baseSeqNo;
    /**Socket TCP **/
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
            /**Lê dados do socket, empacota em Fragmentos e coloca na fila de envio por UDP **/
            tcpIn = tcpSocket.getInputStream();
            int count;
            int seqNo = baseSeqNo+1;
            byte[] buf = new byte[AnonGW.BODY_MAX_SIZE];
            while ((count = tcpIn.read(buf))>0) {
                PacketUDP packet = new PacketUDP(sessionId, Arrays.copyOf(buf, count), AnonGW.BODY_MAX_SIZE - count, seqNo++);
                queue.put(packet);
            }
            }
        catch (SocketException se){System.out.println("Fim da transferencia");}
        catch (Exception e){e.printStackTrace();}


    }
}
