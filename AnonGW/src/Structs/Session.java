package Structs;

import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;
import java.util.concurrent.BlockingQueue;

/**Classe identificadora de uma sessão**/
public class Session {
    /** Tempo (em ms) maximo entre a receçao e confirmaçao de um pacote **/
    public static final int TIMEOUT = 300;
    /**Tamanho maximo do buffer de uma Sliding Window **/
    public static final int WINDOW_SIZE = 300;
    /**Id de sessao**/
    int sessionId;
    /**Socket TCP**/
    Socket tcpSock;
    /** Endereço do peer **/
    InetAddress anonGWAddress;
    /**Port para comunicaçao UDP **/
    int udpPort;
    /**Fila de envio para TCP**/
    BlockingQueue<PacketUDP> tcpQueue;
    /**Fila de envio para UDP**/
    BlockingQueue<PacketUDP> udpQueue;
    /**Janela de ordenaçao da receçao**/
    SlidingWindow recieving;
    /** Chave AES para esta sessão **/
    Key aes;

    /**Construtor de uma sessão baseada numa ligaçao TCP **/
    public Session(Socket sock, InetAddress peer, int udpPort , BlockingQueue<PacketUDP> tcpq,BlockingQueue<PacketUDP>udpq, Key k){
        sessionId =(int) (Math.random() * Integer.MAX_VALUE);
        tcpSock=sock;
        anonGWAddress = peer;
        this.udpPort=udpPort;
        tcpQueue = tcpq;
        aes=k;
        udpQueue = udpq;
        recieving=new SlidingWindow(WINDOW_SIZE);
    }
    /**Construtor de uma sessão baseada numa ligaçao UDP **/
    public Session(int sessionId,Socket sock, InetAddress peer,int udpPort ,int tcpPort, BlockingQueue<PacketUDP> tcp,BlockingQueue<PacketUDP> udp, Key k){
        this.sessionId =sessionId;
        tcpSock=sock;
        this.udpPort=udpPort;
        anonGWAddress = peer;
        tcpQueue = tcp;
        aes=k;
        udpQueue = udp;
        recieving=new SlidingWindow(WINDOW_SIZE);
    }

    public String toString() {
        return "Session{" +
                "sessionId=" + sessionId +
                ", tcpSock=" + tcpSock +
                ", anonGWAddress=" + anonGWAddress +
                ", udpPort=" + udpPort +
                ", tcpQueue=" + tcpQueue +
                ", udpQueue=" + udpQueue +
                ", recieving=" + recieving +
                '}';
    }

    public int getId() {return sessionId;}
    public Socket getTcpSock() {return tcpSock;}
    public InetAddress getAnonGWAddress() {return anonGWAddress; }
    public int getUdpPort() {return udpPort;}
    public BlockingQueue<PacketUDP> getTCPQueue() {return tcpQueue;}
    public BlockingQueue<PacketUDP> getUDPQueue() {return udpQueue;}
    public SlidingWindow getRecievingWindow() {
        return recieving;
    }
    public Key getAesKey() {
        return aes;
    }
}
