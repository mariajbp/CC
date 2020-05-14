
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
    public static final int SOCKET_BUFFER_SIZE = 100;
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
    //FromTCP
    public int initSession(Socket sock) {
        InetAddress peer = peers.get((int) (Math.random()*(peers.size()-1)));
        BlockingQueue<PacketUDP> tcpqueue = new LinkedBlockingQueue<PacketUDP>();
        BlockingQueue<PacketUDP> udpqueue = new LinkedBlockingQueue<PacketUDP>();
        Session s =  new Session(sock, peer,udpPort,tcpqueue,udpqueue);
        int sessId = s.getId();
        sessionTable.put(sessId,s);
        return sessId;
    }
    //From UDP
    public void initSession(Socket sock, InetAddress gateway, int tcpPort ,int sessionId) {
        BlockingQueue<PacketUDP> tcpQueue = new LinkedBlockingQueue<>();
        BlockingQueue<PacketUDP> udpQueue = new LinkedBlockingQueue<>();
        Session s =  new Session(sessionId,sock, gateway,udpPort,tcpPort,tcpQueue,udpQueue);
        sessionTable.put(sessionId,s);
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

    @Override
    public String toString() {
        return "AnonGW{" +
                "sessionTable=" + sessionTable +
                ", peers=" + peers +
                ", udpPort=" + udpPort +
                ", sendUdp=" + sendUdp +
                '}';
    }
}
