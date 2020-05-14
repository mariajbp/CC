package Structs;

import java.time.Duration;
import java.time.Instant;

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
   public boolean insert(int index, PacketUDP p){
        if(base ==-1) base =p.getSeqNo();
        int i=index-base;
        if(i<0|| i>=lenght) return false;
        buffer[i]=p;
        return true;
    }
    //ACK [DANGEROUS]
    public void setNull(int index){
        int i=index-base;
        buffer[i]=null;
    }
   public PacketUDP get(int index){
        int i=index-base;
        if(i<0|| i>=lenght) return null;
        return buffer[i];
    }
    //Retrieve and remove Recieving
   public PacketUDP[] retrieve(){
        int i=0;
        while (i<lenght && buffer[i]!=null){i++;}
        PacketUDP[] r = new PacketUDP[i+1];
        PacketUDP[] newbuf = new PacketUDP[lenght];
        System.arraycopy(buffer,0,r,0,i+1);
        System.arraycopy(buffer,i,newbuf,0,lenght-i);
        buffer=newbuf;
        return r;
    }
    //Sending
    //----------nullInd----------timeOutInd-----------------------------
    //|null|null|timedOut|timedOut|timedOut|packet|packet|...
    //-----------^-----------------^------------------------
    public PacketUDP[] retry(){
        int timeOutInd=0,nullInd=0;
        while (nullInd<lenght && buffer[nullInd]==null){nullInd++;}
        while (timeOutInd+nullInd<lenght && buffer[timeOutInd+nullInd]!=null
                && Duration.between(buffer[timeOutInd+nullInd].getTimeStamp(), Instant.now()).toMillis()>TIMEOUT){
            buffer[timeOutInd+nullInd].setTimeStamp(Instant.now());timeOutInd++;}
        PacketUDP[] r = new PacketUDP[timeOutInd];
        PacketUDP[] newbuf = new PacketUDP[lenght];
        System.arraycopy(buffer,nullInd,r,0,timeOutInd);
        System.arraycopy(buffer,nullInd,newbuf,0,lenght-nullInd);
        buffer=newbuf;
        return r;
    }
}
