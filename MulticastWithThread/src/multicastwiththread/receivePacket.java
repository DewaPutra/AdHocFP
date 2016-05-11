package multicastwiththread;

import java.util.*;
import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class receivePacket extends Thread{
    Client c = new Client();
    private MulticastSocket sender;
    private String inMsg;
    private InetAddress addr;
    private int PORT;
    
    public receivePacket(MulticastSocket s, InetAddress addr, int PORT){
        this.sender = s;
        this.addr = addr;
        this.PORT = PORT;
    }
    
    public void run(){
        try{//listening packet
                    System.out.println("Waiting for data to receive...");
                    byte[] buff = new byte[10000];
                    sender.receive(new DatagramPacket(buff, 10000, addr, PORT));     
                    inMsg = new String(buff, 0, buff.length); //receive message
                    c.dbPacket.add(inMsg.trim());
                    System.out.println("   received message: "+inMsg.trim());
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
    }
}
