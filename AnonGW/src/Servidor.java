
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Servidor {
    public static void main(String[] args) throws IOException {
        System.out.println("Connection Started");
        int parseArg=0;
        String targetServer = "";
        int port=-1;
        List<String> peers =  new ArrayList<>();
        for(int i=0;i<args.length;i++) {
            switch (args[i]){
                case "target-server": parseArg=1; break;
                case "port" : parseArg=2; break;
                case "overlay-peers": parseArg=3; break;
                default: break;
            }
            switch (parseArg){
                case 1: targetServer=args[i];break;
                case 2: port = Integer.parseInt(args[i+1]);i++;break;
                case 3: peers.add(args[i]); break;
                default:break;
            }
        }
        AnonGW gw = new AnonGW(peers);
        ServerSocket ss = new ServerSocket(80);
        DatagramSocket sudp = new DatagramSocket(6666);
        ArrayList<Socket> clientSockets= new ArrayList<>();
        ArrayList<Socket> serverSockets= new ArrayList<>();
        ArrayList<Thread> workers = new ArrayList<>();
        try {
            while (true) {
                Socket client = ss.accept();
                Socket server = new Socket(args[0], port);
                clientSockets.add(client);
                serverSockets.add(server);
                TcpPipeWorker cts = new TcpPipeWorker(client.getInputStream(),server.getOutputStream(), client, server);
                TcpPipeWorker stc = new TcpPipeWorker(server.getInputStream(),client.getOutputStream(),server,client);
                new Thread(cts).start();
                new Thread(stc).start();
            }
        } catch (Exception e){
            e.printStackTrace();} finally {
            ss.close();
            for(Socket s:clientSockets) s.close();
            for(Socket s:serverSockets) s.close();

            System.out.println("Connection YEETED");
            System.out.flush();

        }



    }

}
