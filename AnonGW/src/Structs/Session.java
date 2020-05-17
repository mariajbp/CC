package Structs;

import java.net.InetAddress;
import java.net.Socket;
import java.security.Key;
import java.util.concurrent.BlockingQueue;

public class Session {
    public static final int TIMEOUT = 300;
    public static final int WINDOW_SIZE = 300;
    int sessionId;
    Socket tcpSock;
    InetAddress anonGWAddress;
    int udpPort;
    BlockingQueue<PacketUDP> tcpQueue;
    BlockingQueue<PacketUDP> udpQueue;
    SlidingWindow recieving;
    Key aes;


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

    @Override
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
