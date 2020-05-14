package Structs;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class SocketCircularBuffer<K,V> {
    ConcurrentHashMap<Integer, PacketUDP> buffer;
    int base;
    int length;
   public SocketCircularBuffer(int size, int b){
       buffer= new ConcurrentHashMap<>();
       base=b;
       length=size;
   }


   public boolean inRange(int index){return (index-base)>=0 && (index-base)<length; }

   public void insert(PacketUDP e, int index){
       int i=index-base;
       boolean tmp;
       if(tmp=inRange(index)){buffer.put(i,e); }

   }
   public PacketUDP remove(int index){
       PacketUDP e = null;
       int i=index-base;
       if(inRange(index)){
           e = buffer.remove(i);

       }
       return e;
   }
   public int trim(){
       int j=0;
       for(;buffer.get(j) == null;j++){base ++;}
       return  j;
   }
    public PacketUDP get(int index){
       PacketUDP e=null;
       if(inRange(index)){e = buffer.get(index-base);}
       return e;
    }
    public void ack(int seqNo){remove(seqNo);}

    public PacketUDP first(){return get(base);}
    public  int size(){
       return buffer.size();
    }

    @Override
    public String toString() {
        return "SocketCircularBuffer{" +
                "buffer=" + buffer +
                ", base=" + base +
                ", length=" + length +
                '}';
    }
}
