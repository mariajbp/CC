import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ServerWorker implements Runnable{

    private InputStream clientInput, serverInput;
    private OutputStream clientOutput, serverOutput;
    private Socket client,server;
    public ServerWorker(Socket client, Socket server) throws IOException {
        clientInput = client.getInputStream();
        clientOutput = client.getOutputStream();
        serverInput=server.getInputStream();
        serverOutput =server.getOutputStream();
        this.client=client;
        this.server=server;
    }

    public void run()  {
        byte[] barrayCtoS = new byte[1024*100];
        byte[] barrayStoC = new byte[1024*100];
        boolean clientIn =true, serverIn =true, crack=true;
        int cToS=1,sToC=0;
        if(crack) {try{clientInput.transferTo(serverOutput);
                    serverInput.transferTo(clientOutput);} catch (Exception e){e.printStackTrace();}}
        else {
            while ((cToS + sToC) > -2) {
                try {
                    if (clientIn && ((cToS = clientInput.read(barrayCtoS)) > 0)) {
                        clientIn = (cToS > 0);
                        System.out.println("INCOMMING: " + new String(barrayCtoS, StandardCharsets.UTF_8) + "\n");
                        System.out.flush();
                        serverOutput.write(barrayCtoS);
                        serverOutput.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

                try {
                    if (serverIn && ((sToC = serverInput.read(barrayStoC)) > 0)) {
                        serverIn = sToC > 0;
                        System.out.println("OUTGOING: " + new String(barrayStoC, StandardCharsets.UTF_8) + "\n");
                        System.out.flush();
                        clientOutput.write(barrayStoC);
                        clientOutput.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        }
        try {
        client.close();
        server.close();
        }catch (Exception e){e.printStackTrace();}
    }
}
