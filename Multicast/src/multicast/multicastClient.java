
package multicast;

import java.util.*;
import java.io.*;
import java.net.*;

public class multicastClient {
    
    final static String INET_ADDR = "224.0.0.3";
    public static String username = "user1";
    final static int PORT = 2526;
    private static int now =1, targetAchieved=0;
    private static String[] flag = new String[10000];
    private static int index=0;
    
    public static String sendAgain(String inMsg){
        String send="false";
        String datMsg[] = inMsg.split(",");
        if (!username.equalsIgnoreCase(datMsg[datMsg.length-1].trim())) {
            send="true";
        } else
            return "myself";
        for(int i=0;i<index;i++){
            if(flag[i].equalsIgnoreCase(datMsg[datMsg.length-1].trim())){
                return "flood";
            } else{
                send="false";
            }
        }
        return send;
    }
    
    public static void printMsg(String inMsg){
        //receiver,message,ttl,lifetime,sender
        System.out.println("Message Received!!!");
        String datMsg[] = inMsg.split(",");
        System.out.print("   message from: ");
        for(int i=4;i<datMsg.length;i++){
            System.out.print(datMsg[i].trim()+", ");
        }
        System.out.println("\n   to     : "+ datMsg[0]);
        System.out.println("   message: "+ datMsg[1]);
    }
    
    public static String newMsg(String inMsg){
        //receiver,message,ttl,lifetime,sender
        String newMsg="";
        String datMsg[] = inMsg.split(",");
        int ttl = Integer.parseInt(datMsg[2])-1; //decrease ttl
        int newLt = Integer.parseInt(datMsg[3])-300; // decrease lifetime
        String send = sendAgain(inMsg);
        
        if (send=="myself") 
            return "myself";
        if (send=="flood")
            return "flood";
        
        //check if message will be dropped or not
        if(ttl<0 || newLt<0 || username.equalsIgnoreCase(datMsg[0].trim())){
            System.out.println("      ttl        : "+ttl);
            System.out.println("      newLt      : "+newLt);
            //System.out.println("      send status: "+send);
            System.out.println("      To         : "+datMsg[0]);
            return "message dropped";
        }
        
        //if not dropped
        for(int i=0;i<datMsg.length;i++){
            if(i==2){
                newMsg += Integer.toString(ttl);
            } else
                if(i==3){
                    newMsg += Integer.toString(newLt);
                }
                if (i<2) {
                    newMsg+=datMsg[i];
                }
            if (i<=3) newMsg +=",";
        }
        newMsg+=username;
        return newMsg;
    }
    
    public static void main(String[] args) throws UnknownHostException, InterruptedException, IOException {
        System.out.println(username.toUpperCase());
        System.out.println("Create socket on address " + INET_ADDR + " and port " + PORT + ".");
        
        String msg = "";
        InetAddress addr = InetAddress.getByName(INET_ADDR);
        InetAddress group = InetAddress.getByName(INET_ADDR);
        MulticastSocket s = new MulticastSocket(PORT);
        s.joinGroup(group);
        
//        String[] flag = new String[1000];
//        int index=0;
        while(targetAchieved==0){
            System.out.println("Waiting for data to receive...");
            byte[] buff = new byte[10000];
            s.receive(new DatagramPacket(buff, 10000, group, PORT));     
            String inMsg = new String(buff, 0, buff.length); //receive message
            System.out.println("received message: "+inMsg.trim());
            String outMsg = newMsg(inMsg.trim());
            
            String datMsg[] = inMsg.split(",");
            int status = 0; //mengecek apa sender sudah pernah mengirim atau belum
            for (int i=0;i<index;i++) {
                if (flag[i].equalsIgnoreCase(datMsg[datMsg.length-1].trim())) {
                    status=1;
                }
            }
            if (status==0) {
                flag[index]=datMsg[datMsg.length-1].trim();
                index++;
            }
            
            try { // try sending
//                    System.out.println("outMsg: "+outMsg);
                    if(outMsg.equals("message dropped")){
                        System.out.println(outMsg);
                        System.out.println("\n\n List of Senders : \n");
                        for (int i=0;i<index;i++) {
                            System.out.println(flag[i]);
                        }
                        targetAchieved=1;
                        return;
                    } else {
                        if (outMsg.equalsIgnoreCase("myself")){
                            System.out.println("Target is ME\n");
                        } else if (outMsg.equalsIgnoreCase("flood")){
                            System.out.println("Flooding");
                        }
                        else {
                            System.out.println("inMsg: "+inMsg);
                            System.out.println("outMsg: "+outMsg);
                            printMsg(inMsg);
                            System.out.println("Sending message...\n\n");
                            DatagramPacket outPacket = new DatagramPacket(outMsg.getBytes(),outMsg.getBytes().length, addr, PORT);
                            s.send(outPacket);
                        }
                    }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
}
