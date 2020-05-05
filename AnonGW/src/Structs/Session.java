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


    public Session(Socket sock, InetAddress peer, int peerPort, BlockingQueue<PacketUDP> q){
        sessionId =(int) (Math.random() * Integer.MAX_VALUE);
        tcpSock=sock;
        anonGWAddress = peer;
        queue = q;
    }
    public Session(int sessionId,Socket sock, InetAddress peer, int peerPort, BlockingQueue<PacketUDP> q){
        this.sessionId =sessionId;
        tcpSock=sock;
        anonGWAddress = peer;
        queue = q;
    }

    public int getId() {return sessionId;}
    public Socket getTcpSock() {return tcpSock;}
    public InetAddress getAnonGWAddress() {return anonGWAddress; }
    public int getUdpPort() {return udpPort;}
    public BlockingQueue<PacketUDP> getQueue() {return queue;}


}
