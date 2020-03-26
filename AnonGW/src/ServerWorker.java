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

    public void run()  {
        byte[] barreiClientToServer = new byte[1024];
        byte[] barreiServerToClient = new byte[1024];
        boolean input=true, output=true;

        while (true){
            if((!input)&&(!output)) return;
          try {
              if ((is.read(barreiClientToServer) != -1))
              {
                  System.out.println("INCOMMING: " + barreiClientToServer.toString() + "\n");
                  sos.write(barreiClientToServer);
              }
          } catch (Exception e){e.printStackTrace();
          try {
              input=false;
              is.close();
              sos.close();
          } catch (Exception es){es.printStackTrace();}
          }

           try{ if ((sis.read(barreiServerToClient)!= -1)){
               System.out.println("OUTGOING: "+ barreiServerToClient+"\n");
               os.write(barreiServerToClient);
                }
           } catch (Exception e){e.printStackTrace();
           try{output=false;
               sis.close();
               os.close();}
           catch (Exception es){es.printStackTrace();}
           }

        }




    }
}
