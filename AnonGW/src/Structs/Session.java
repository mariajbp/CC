package Structs;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class Session {
    int sessionId;
    Socket tcpSock;
    InetAddress anonGWAddress;
    int udpPort;
    BlockingQueue<PacketUDP> queue;
    SocketCircularBuffer<Integer,PacketReg> packetBuffer;


    public Session(Socket sock, InetAddress peer, int udpPort , BlockingQueue<PacketUDP> q){
        sessionId =(int) (Math.random() * Integer.MAX_VALUE);
        tcpSock=sock;
        anonGWAddress = peer;
        this.udpPort=udpPort;
        queue = q;
        packetBuffer =  new SocketCircularBuffer<>(100);
    }
    public Session(int sessionId,Socket sock, InetAddress peer,int udpPort ,int tcpPort, BlockingQueue<PacketUDP> q){
        this.sessionId =sessionId;
        tcpSock=sock;
        this.udpPort=udpPort;
        anonGWAddress = peer;
        queue = q;
        packetBuffer =  new SocketCircularBuffer<>(100);
    }

    public String toString() {
        return "Session{" +
                "sessionId=" + sessionId +
                ",|| tcpSock=" + tcpSock +
                ",|| anonGWAddress=" + anonGWAddress +
                ",|| udpPort=" + udpPort +
                ",|| queueSize=" + queue.size() +
                '}';
    }

    public int getId() {return sessionId;}
    public Socket getTcpSock() {return tcpSock;}
    public InetAddress getAnonGWAddress() {return anonGWAddress; }
    public int getUdpPort() {return udpPort;}
    public BlockingQueue<PacketUDP> getQueue() {return queue;}
    public SocketCircularBuffer<Integer, PacketReg> getPacketBuffer() {
        return packetBuffer;
    }
}
