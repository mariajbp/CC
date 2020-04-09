import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.logging.SocketHandler;

public class Mario implements Runnable {
    Socket inS;
    Socket outS;
    InputStream in;
    OutputStream out;
    public Mario(InputStream inputStream, OutputStream outputStream,Socket sockIn,Socket socketOut ) {
        in=inputStream; out=outputStream; inS=sockIn;outS=socketOut;
    }

    public void run(){
        byte[] barray = new byte[4*1024];
        int bread;
        try {
            while (inS.isConnected() && outS.isConnected()) {
                bread=in.read(barray);
                System.out.println("\nDATA :"+new String(barray));
                out.write(barray,0,bread);
            }

        }catch (Exception e){e.printStackTrace();}
        //try{in.transferTo(out);} catch (Exception e){e.printStackTrace();}
    }
}
