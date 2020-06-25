import Structs.PacketUDP;
import Structs.Session;

import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
/**Envia dados para um Socket TCP **/
public class TCPOutPipe implements Runnable {
    /**Id de sessão **/
    private int sessionId;
    /**Instancia do  AnonGW **/
    private AnonGW agw;
    /**Instancia da sessao **/
    private Session session;
    /**Fila de envio por TCP **/
    private BlockingQueue<PacketUDP> queue;
    /**Socket TCP **/
    private Socket tcpSocket;
    public TCPOutPipe(int sess, AnonGW agw) {
        sessionId = sess;
        this.agw=agw;
        this.session = agw.getSession(sess);
        this.queue=session.getTCPQueue();
        this.tcpSocket = session.getTcpSock();
    }

    public void run() {
      byte[] buf = new byte[AnonGW.BODY_MAX_SIZE];
      int flag=1;
     try{
         while(!tcpSocket.isClosed() && flag>0){
             /** Envia para o socket os fragementos de dados extraidos dos Pacotes da fila **/
             PacketUDP pack = queue.take();
             flag=pack.getFlag();
             buf = pack.getBody();
             tcpSocket.getOutputStream().write(buf);

         }
     }
     catch (SocketException se){System.out.println("Fim da transferencia");}
     catch (Exception e){e.printStackTrace();}


    }
}
