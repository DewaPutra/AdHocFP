package multicast;

import java.util.*;
import java.io.*;
import java.net.*;

public class multicastClient2 {
    
    final static String INET_ADDR = "224.0.0.3";
    public static String username = "user3";
    final static int PORT = 2526;
    private static int now =1;
    
    public static boolean sendAgain(String inMsg){
        boolean send=false;
        String datMsg[] = inMsg.split(",");
        for(int i=4;i<datMsg.length;i++){
            if(!username.equals(datMsg[i])){
                send=true;
            } else
            {
                send=false;
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
                System.out.print(datMsg[i]+", ");
            }
        System.out.println("   to     : "+ datMsg[0]);
        System.out.println("   message: "+ datMsg[1]);
    }
    
    public static String newMsg(String inMsg){
        //receiver,message,ttl,lifetime,sender
        String newMsg="";
        String datMsg[] = inMsg.split(",");
        int ttl = Integer.parseInt(datMsg[2])-1; //decrease ttl
        int newLt = Integer.parseInt(datMsg[3])-300; // decrease lifetime
        boolean send = sendAgain(inMsg);
        
        //check if message will be dropped or not
        if(ttl<0 || newLt<0 || username.equals(datMsg[0])){
            System.out.println("      ttl        : "+ttl);
            System.out.println("      newLt      : "+newLt);
            System.out.println("      send status: "+send);
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
                } else {
                    newMsg+=datMsg[i];
                }
            newMsg +=",";
        }
        newMsg+=username;
        return newMsg;
    }
    
    public static void main(String[] args) throws UnknownHostException, InterruptedException, IOException {
        System.out.println(username.toUpperCase());
        System.out.println("Create socket on address " + INET_ADDR + " and port " + PORT + ".");
        
        //Create and joining group
        InetAddress addr = InetAddress.getByName(INET_ADDR);
        InetAddress group = InetAddress.getByName(INET_ADDR);
        MulticastSocket s = new MulticastSocket(PORT);
        s.joinGroup(group);
        
        while(true){
            System.out.println("Waiting for data to receive...");
            byte[] buff = new byte[10000];
            s.receive(new DatagramPacket(buff, 10000, group, PORT));     
            String inMsg = new String(buff, 0, buff.length); //receive message
            System.out.println("received message: "+inMsg);
            String outMsg = newMsg(inMsg.trim());
            
            try { // try sending
//                    System.out.println("outMsg: "+outMsg);
                    
                    if(outMsg.equals("message dropped")){
                        System.out.println(outMsg);
                        return;
                    } else
                    {
                        System.out.println("inMsg: "+inMsg);
                        System.out.println("outMsg: "+outMsg);
                        printMsg(inMsg);
                        System.out.println("Sending message...\n\n");
                        DatagramPacket outPacket = new DatagramPacket(outMsg.getBytes(),outMsg.getBytes().length, addr, PORT);
                        s.send(outPacket);
                    }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
