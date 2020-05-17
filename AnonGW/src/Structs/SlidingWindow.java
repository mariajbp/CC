package Structs;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import static Structs.Session.TIMEOUT;

public class SlidingWindow {
    int base;
    int lenght;
    PacketUDP[] buffer;

    public SlidingWindow(int l){
        base =-1;
        lenght=l;
        buffer= new PacketUDP[lenght];
    }
   public int insert(int index, PacketUDP p){
        if(base ==-1) base = index;
        int i=index-base;
        if(i<0) return -1;
        if(i>=lenght) return 0;
        buffer[i]=p;
        return 1;
    }
    //ACK [DANGEROUS]
    public void setNull(int index){
        int i=index-base;
        if(i<0|| i>=lenght) return;
        buffer[i]=null;
        int nullInd=0;
        while (nullInd<lenght && buffer[nullInd]==null){nullInd++;}
        if(nullInd==lenght) base =-1;else{
            PacketUDP[] r = new PacketUDP[lenght];
            System.arraycopy(buffer,nullInd,r,0,lenght-nullInd);
            buffer=r;
            base +=nullInd;
        }
    }
   public PacketUDP get(int index){
        int i=index-base;
        if(i<0|| i>=lenght) return null;
        return buffer[i];
    }
    //Retrieve and remove Recieving
   public PacketUDP[] retrieve(){
        int i=0,j=0;
        while (i<lenght && buffer[i]!= null){i++;}
        while(j<lenght && buffer[j]==null ){j++;}
        if(j==lenght) base =-1;
        PacketUDP[] r = new PacketUDP[i];
        PacketUDP[] newbuf = new PacketUDP[lenght];
        System.arraycopy(buffer,0,r,0,i);
        System.arraycopy(buffer,i,newbuf,0,lenght-i);
        base +=i;
        buffer=newbuf;
        return r;
    }
    //Sending
    //----------nullInd----------timeOutInd-----------------------------
    //|null|null|timedOut|timedOut|timedOut|packet|packet|...
    //-----------^-----------------^------------------------
    public PacketUDP[] retry(){
        int timeOutInd=0,nullInd=0;
        long print =0L;
        while (nullInd<lenght && buffer[nullInd]==null){nullInd++;}
        while (timeOutInd+nullInd<lenght && buffer[timeOutInd+nullInd]!=null
                && (print = Duration.between(buffer[timeOutInd+nullInd].getTimeStamp(), Instant.now()).toMillis())>TIMEOUT){
            buffer[timeOutInd+nullInd].setTimeStamp(Instant.now());timeOutInd++; System.out.println("TIMEOUT :"+print);}
        PacketUDP[] r = new PacketUDP[timeOutInd];
        PacketUDP[] newbuf = new PacketUDP[lenght];
        System.arraycopy(buffer,nullInd,r,0,timeOutInd);
        System.arraycopy(buffer,nullInd,newbuf,0,lenght-nullInd);
        base+= base==-1? 0: nullInd;
        buffer=newbuf;
        return r;
    }


    @Override
    public String toString() {
        return "SlidingWindow{" +
                "base=" + base +
                ", lenght=" + lenght +
                ", buffer=" + Arrays.toString(buffer) +
                '}';
    }
}
