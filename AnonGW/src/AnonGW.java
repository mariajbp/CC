import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AnonGW
{
    public static void main(String[] args) throws IOException {
        System.out.println("main bonita só para ti Hugo");
        ServerSocket ss = new ServerSocket(80);
        Socket server = new Socket(args[3],80);


        while (true){
            Socket s =  ss.accept();
            ServerWorker sw= new ServerWorker(s,server);
            sw.run();





        }
    }

}
