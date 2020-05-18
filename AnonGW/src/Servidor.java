import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Servidor {

    public static void main(String[] args) throws IOException {
        if (args.length < 7) {
            System.out.println("Argumentos Invalidos");
            return;
        }
        System.out.println("Setting Up");
        int parseArg = 0;
        InetAddress targetServer = null;
        int port = -1;
        List<InetAddress> peers = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "target-server":
                    parseArg = 1;
                    i++;
                    break;
                case "port":
                    parseArg = 2;
                    i++;
                    break;
                case "overlay-peers":
                    parseArg = 3;
                    i++;
                    break;
                default:
                    break;
            }
            try {
                switch (parseArg) {
                    case 1:
                        targetServer = InetAddress.getByName(args[i]);
                        break;
                    case 2:
                        port = Integer.parseInt(args[i]);
                        break;
                    case 3:
                        peers.add(InetAddress.getByName(args[i]));
                        break;
                    default:
                        break;
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        DatagramSocket udp;
        try {
            udp = new DatagramSocket(6666);
                } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SOCKET FAILURE");
            return;
        }
        AnonGW gw = new AnonGW(peers, udp, 6666);
        TCPServer tcpServer = new TCPServer(80, gw);
        UDPServer udpServer = new UDPServer(gw, udp, port, targetServer);

        new Thread(tcpServer).start();
        new Thread(udpServer).start();
        System.out.println("Servers Started!");


    }
}