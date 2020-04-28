import Exceptions.EndOfFileException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.logging.SocketHandler;

public class TcpPipeWorker implements Runnable {
    Socket inS;
    Socket outS;
    InputStream in;
    OutputStream out;
    public TcpPipeWorker(InputStream inputStream, OutputStream outputStream, Socket sockIn, Socket socketOut ) {
        in=inputStream; out=outputStream; inS=sockIn;outS=socketOut;
    }

    public void run(){
        byte[] barray = new byte[64*1024];
        int bread;
        try {
            while (true) {
                bread = in.read(barray);
                if(bread<0) throw new EndOfFileException("END OF FILE");
                out.write(barray, 0, bread);
            }

        }
        catch (EndOfFileException eof ){ System.out.println(eof.getMessage()); }
        catch (SocketException se){System.out.println("CONNECTION SHUTDOWN");}
        catch (Exception e){e.printStackTrace();}
    }
}
