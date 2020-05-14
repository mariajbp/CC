import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {
    ServerSocket ss;
    AnonGW agw;


    public TCPServer(int port, AnonGW ag) {
        try{ss= new ServerSocket(port);}catch (Exception e){e.printStackTrace();}
        agw =ag;


    }

    public void run() {
        try{
            while(true) {
                Socket sock = ss.accept();
                int sessId = agw.initSession(sock);
                new Thread(new TCPInPipe(sessId,agw)).start();
                new Thread(new TCPServerWorker(sessId,agw)).start();
                new Thread(new TCPOutPipe(sessId,agw)).start();


            }

        }catch (Exception e){e.printStackTrace();}


    }
}
