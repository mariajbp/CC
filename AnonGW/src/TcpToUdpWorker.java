import Exceptions.EndOfFileException;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

public class TcpToUdpWorker implements Runnable {

    Socket inS;
    InputStream in;
    public TcpToUdpWorker(InputStream inputStream, Socket sockIn  ) {
        in=inputStream; inS=sockIn;
    }

    public void run(){
        /*
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
    */
    }


}
