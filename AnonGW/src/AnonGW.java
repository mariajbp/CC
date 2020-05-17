
import Structs.Container;
import Structs.PacketUDP;
import Structs.Session;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class AnonGW {
    public static final int BODY_MAX_SIZE = 1024;
    public static final int SOCKET_BUFFER_SIZE = 100;
    private ConcurrentMap<Integer, Session> sessionTable;
    private ConcurrentMap<InetAddress, String> keyChain;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private List<InetAddress> peers;
    private  int udpPort;
    private DatagramSocket sendUdp;

    public AnonGW(List<InetAddress> peers,DatagramSocket udp ,int udpPort) {
        sessionTable = new ConcurrentHashMap<>();
        keyChain = new ConcurrentHashMap<>();
        this.peers=peers;

        this.udpPort=udpPort;
        sendUdp = udp;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair pair = keyGen.generateKeyPair();
            this.privateKey = pair.getPrivate();
            this.publicKey = pair.getPublic();
        } catch (Exception e) {
            e.printStackTrace();
    }}
    //FromTCP
    public int initSession(Socket sock) {
        InetAddress peer = peers.get((int) (Math.random()*(peers.size()-1)));
        BlockingQueue<PacketUDP> tcpqueue = new LinkedBlockingQueue<PacketUDP>();
        BlockingQueue<PacketUDP> udpqueue = new LinkedBlockingQueue<PacketUDP>();
        SecretKey key=null;
        try{KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(128);
            key = generator.generateKey();
        } catch (Exception e){e.printStackTrace();}
        Session s =  new Session(sock, peer,udpPort,tcpqueue,udpqueue,key);
        int sessId = s.getId();

        sessionTable.put(sessId,s);
        return sessId;
    }
    //From UDP
    public void initSession(Socket sock, InetAddress gateway, int tcpPort ,int sessionId, Key key) {
        BlockingQueue<PacketUDP> tcpQueue = new LinkedBlockingQueue<>();
        BlockingQueue<PacketUDP> udpQueue = new LinkedBlockingQueue<>();
        Session s =  new Session(sessionId,sock, gateway,udpPort,tcpPort,tcpQueue,udpQueue, key);
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
    public boolean isAnonGW(InetAddress i){
        return peers.contains(i);
    }
    public  boolean initKey(InetAddress peer, String key){
       if (keyChain.containsKey(peer))  return  false;
       keyChain.put(peer,key);
       return  true;
    }


    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
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

    public String getPublicKeyFrom(InetAddress anonGW) {
        return keyChain.get(anonGW);
    }

    public void requestPublicKeyFrom(InetAddress anonGW) {
        System.out.println("Requested key from "+anonGW);
        PacketUDP c;
        byte[] data= new Container( (c=new PacketUDP(Base64.getEncoder().encodeToString(publicKey.getEncoded()),328)).serialize()).serialize();
       System.out.println(c);
        DatagramPacket pack = new DatagramPacket(data,data.length,anonGW,udpPort);
        try {sendUdp(pack);} catch (Exception e){e.printStackTrace();}
    }


    public void replyPublicKeyTo(InetAddress address) {
        System.out.println("Replied key to "+address);
        byte[] peerKey = publicKey.getEncoded();
        System.out.println(publicKey.getAlgorithm() + " || " +
        publicKey.getFormat() +" || "+ peerKey.length);
        byte[] data= new Container(new PacketUDP(Base64.getEncoder().encodeToString(peerKey),329).serialize()).serialize();
        DatagramPacket pack = new DatagramPacket(data,data.length,address,udpPort);
        try {sendUdp(pack);} catch (Exception e){e.printStackTrace();}

    }
}
