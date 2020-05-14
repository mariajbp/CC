package Structs;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class Session {
    public static final int TIMEOUT = 2000;
    public static final int WINDOW_SIZE = 100;
    int sessionId;
    Socket tcpSock;
    InetAddress anonGWAddress;
    int udpPort;
    BlockingQueue<PacketUDP> tcpQueue;
    BlockingQueue<PacketUDP> udpQueue;
    SlidingWindow recieving;


    public Session(Socket sock, InetAddress peer, int udpPort , BlockingQueue<PacketUDP> tcpq,BlockingQueue<PacketUDP>udpq){
        sessionId =(int) (Math.random() * Integer.MAX_VALUE);
        tcpSock=sock;
        anonGWAddress = peer;
        this.udpPort=udpPort;
        tcpQueue = tcpq;
        udpQueue = udpq;
        recieving=new SlidingWindow(WINDOW_SIZE);
    }
    public Session(int sessionId,Socket sock, InetAddress peer,int udpPort ,int tcpPort, BlockingQueue<PacketUDP> tcp,BlockingQueue<PacketUDP>udp){
        this.sessionId =sessionId;
        tcpSock=sock;
        this.udpPort=udpPort;
        anonGWAddress = peer;
        tcpQueue = tcp;
        udpQueue = udp;
        recieving=new SlidingWindow(WINDOW_SIZE);
    }

    public String toString() {
        return "Session{" +
                "sessionId=" + sessionId +
                ",|| tcpSock=" + tcpSock +
                ",|| anonGWAddress=" + anonGWAddress +
                ",|| udpPort=" + udpPort +
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
}
