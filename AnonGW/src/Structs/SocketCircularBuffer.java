package Structs;

import java.util.concurrent.ConcurrentHashMap;


public class SocketCircularBuffer<K,V> {
    ConcurrentHashMap<Integer, PacketReg> buffer;
    int base;
    int length;
   public SocketCircularBuffer(int size){
       buffer= new ConcurrentHashMap<>(size);
       base=0;
       length=size;
   }
   public boolean inRange(int index){return (base-index)>=base && (base-index)<base+length; }

   public void insert(PacketReg e, int index){
       int i=index-base;
       if(inRange(index)){buffer.put(i,e);}
   }
   public PacketReg remove(int index){
       PacketReg e = null;
       if(inRange(index)){
           e = buffer.remove(index-base);
           for(int j=0;buffer.get(j) == null;j++){base ++;}
       }
       return e;
   }
    public PacketReg get(int index){
       PacketReg e=null;
       if(inRange(index)){e = buffer.get(index-base);}
       return e;
    }
    public void ack(int seqNo){remove(seqNo);}

    public PacketUDP first(){return get(base).getPack();}


}
