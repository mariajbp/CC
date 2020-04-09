
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor {
    public static void main(String[] args) throws IOException {
        System.out.println("main bonita só para ti Hugo");
        ServerSocket ss = new ServerSocket(80);
        ArrayList<Socket> clientSockets= new ArrayList<>();
        ArrayList<Socket> serverSockets= new ArrayList<>();
        ArrayList<Thread> workers = new ArrayList<>();

        try {
            while (true) {
                Socket client = ss.accept();
                Socket server = new Socket(args[0], 80);
                clientSockets.add(client);
                serverSockets.add(server);
                Mario cts = new Mario(client.getInputStream(),server.getOutputStream(), client, server);
                Mario stc = new Mario(server.getInputStream(),client.getOutputStream(),server,client);
                //ServerWorker sw = new ServerWorker(client, server);
                new Thread(cts).start();
                new Thread(stc).start();
            }
        } catch (Exception e){
            e.printStackTrace();}
        ss.close();
        for(Socket s:clientSockets) s.close();
        for(Socket s:serverSockets) s.close();

        System.out.println("I DED");


    }

}
