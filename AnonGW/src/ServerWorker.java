import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerWorker implements Runnable{

    private InputStream  is, sis;
    private OutputStream os, sos;


    public ServerWorker(Socket s, Socket server) throws IOException {
        is=s.getInputStream();
        os=s.getOutputStream();
        sis = server.getInputStream();
        sos =server.getOutputStream();
    }

    public void run() throws IOException {
        byte[] barreiClientToServer = new byte[1024];
        byte[] barreiServerToClient = new byte[1024];

        while (true){
            if ((is.read(barreiClientToServer) != -1)) sos.write(barreiClientToServer);
            if ((sis.read(barreiServerToClient)!= -1)) os.write(barreiServerToClient);
        }



    }
}
