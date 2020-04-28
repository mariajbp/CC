package Structs;

import java.net.InetAddress;
import java.net.Socket;

public class PacketLog {


    private int id;
    private Socket s;
    private InetAddress ipAnonGW;

    public PacketLog(int id, Socket s, InetAddress ipAnonGW) {
        this.id = id;
        this.s = s;
        this.ipAnonGW = ipAnonGW;
    }

    public int getId() {
        return id;
    }

    public Socket getS() {
        return s;
    }

    public InetAddress getIpAnonGW() {
        return ipAnonGW;
    }





}
