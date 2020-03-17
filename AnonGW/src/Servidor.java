
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor {
    public static void main(String[] args) throws IOException {
        System.out.println("main bonita só para ti Hugo");
        ServerSocket ss = new ServerSocket(80);
        ArrayList<Socket> sockets= new ArrayList<>();
        ArrayList<Thread> workers = new ArrayList<>();


        try {
            while (true) {
                Socket server = new Socket(args[3], 80);
                Socket s = ss.accept();
                sockets.add(s);
                ServerWorker sw = new ServerWorker(s, server);
                new Thread(sw).start();


            }
        } catch (Exception e){

        for(Socket s:sockets){
            s.shutdownInput();
            s.shutdownOutput();
            s.close();
        }
        }

    }

}
