import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

public class ServerWorker implements Runnable{

    private InputStream clientInput, serverInput;
    private OutputStream clientOutput, serverOutput;
    private Socket client,server;
    
    public ServerWorker(Socket client, Socket server) throws IOException {
        clientInput = client.getInputStream();
        clientOutput = client.getOutputStream();
        serverInput = server.getInputStream();
        serverOutput =server.getOutputStream();
        this.client=client;
        this.server=server;
    }

    public void run()  {
        byte[] barrayCtoS = new byte[1024];
        byte[] barrayStoC = new byte[1024];
        boolean inputOn =true, outputOn =true;
        int num=0;

        while (inputOn||outputOn){
          try {
              if (inputOn&&(num=clientInput.read(barrayCtoS,0,1024)) >0)
              {
                  System.out.println("INCOMMING: " + Arrays.toString(barrayCtoS) + "\n");
                  System.out.flush();
                  serverOutput.write(barrayCtoS,0,num);
                  serverOutput.flush();
              }
          } catch (Exception e){inputOn =false;e.printStackTrace();

          }

           try{ if (outputOn&&(num=serverInput.read(barrayStoC,0,1024))>0){
               System.out.println("OUTGOING: "+ Arrays.toString(barrayStoC) +"\n");
               System.out.flush();
               clientOutput.write(barrayStoC,0,num);
               clientOutput.flush();
                }
           } catch (Exception e){outputOn =false;e.printStackTrace();

           }

        }
        try {
        client.close();
        server.close();
        }catch (Exception e){e.printStackTrace();}
    }
}
