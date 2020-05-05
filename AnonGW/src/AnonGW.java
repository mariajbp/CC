
import Structs.PacketUDP;
import Structs.Session;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class AnonGW {
    public static final int BODY_MAX_SIZE = 1024;
    private ConcurrentMap<Integer, Session> sessionTable;
    private List<InetAddress> peers;
    private  int udpPort;
    private DatagramSocket sendUdp;

    public AnonGW(List<InetAddress> peers,DatagramSocket udp ,int udpPort) {
        sessionTable = new ConcurrentHashMap<>();
        this.peers=peers;
        this.udpPort=udpPort;
        sendUdp = udp;
    }


    public int initSession(Socket sock) {
        InetAddress peer = peers.get((int) (Math.random()*(peers.size()-1)));
        BlockingQueue<PacketUDP> queue = new LinkedBlockingQueue<PacketUDP>();
        Session s =  new Session(sock, peer,udpPort,queue);
        int sessId = s.getId();
        sessionTable.put(sessId,s);
        return sessId;
    }
    public void initSession(Socket sock, InetAddress gateway, int tcpPort ,int sessionId) {
        BlockingQueue<PacketUDP> queue = new LinkedBlockingQueue<>();
        Session s =  new Session(sessionId,sock, gateway,tcpPort,queue);
        int sessId = sessionId;
        sessionTable.put(sessId,s);
    }
    public Session getSession(int id){
        return sessionTable.get(id);
    }
    public void sendUdp(DatagramPacket packet) throws IOException {
        sendUdp.send(packet);
    }
    public boolean hasSession(int id){
        return sessionTable.containsKey(id);
    }
}
