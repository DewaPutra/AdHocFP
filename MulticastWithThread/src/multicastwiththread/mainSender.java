
package multicastwiththread;

import java.util.*;
import java.io.*;
import java.net.*;

public class mainSender {
    final static String INET_ADDR = "224.0.0.3";
    final static String username = "mainsender";
    final static int PORT = 2526;
    final static String posLat = "179";
    final static String posLong = "255";
    
    public static void main(String[] args) throws UnknownHostException, InterruptedException, IOException {
        intMulticast func = new implMulticast();
        InetAddress addr = InetAddress.getByName(INET_ADDR);
        InetAddress group = InetAddress.getByName(INET_ADDR);
        
        System.out.println("Create socket on address " + INET_ADDR + " and port " + PORT + ".");
        String msg = func.createMessage(username, posLat, posLong);
        
        MulticastSocket s = new MulticastSocket(PORT);
        s.joinGroup(group);
        try{ // try sending
                if(msg.equals("message dropped")){
                    System.out.println(msg);
                    return;
                } else
                {
                    System.out.println("Sending message...");
                    DatagramPacket outPacket = new DatagramPacket(msg.getBytes(),msg.getBytes().length, addr, PORT);
                    s.send(outPacket);
                }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
