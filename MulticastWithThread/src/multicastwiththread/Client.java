
package multicastwiththread;

import java.util.*;
import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Client extends Thread {
    final static String INET_ADDR = "224.0.0.3";
    final static int PORT = 2526;
    public InetAddress addr;
    public MulticastSocket s;
    private intMulticast func = new implMulticast();
    private static String username, posLat, posLong;
    public static volatile ArrayList<String> dbPacket = new ArrayList<String>();
    
    public String updateMessage(String inMsg) throws ParseException{
        intMulticast func = new implMulticast();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String newMsg = "";
        String datMsg[] = inMsg.split(",");
        String newLT = func.isAvailable(func.timeSpent(datMsg[4]), datMsg[3]); //check lifetime of message
        String now = format.format(new Date()).toString();
        if(now.length()<8) now+=":00";
        //generate newMessage if available
        for(int i=0;i<datMsg.length;i++){
            if(i==3) newMsg += newLT; else
            if(i==4) newMsg += now; else
            if(i==5) newMsg += posLat; else
            if(i==6) newMsg += posLong; else
                newMsg+= datMsg[i];
            if(i<datMsg.length-1) newMsg+=",";
        }
        return newMsg;
    }
    
    public String newMsg(String inMsg, String username) throws ParseException{
        
        intMulticast func = new implMulticast();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        
        //0        1       2     3       4        5    6   7    8    
        //receiver,message,ttl,lifetime,timesent,lat,long,dis,sender
        String newMsg = "";
        String datMsg[] = inMsg.split(",");
        
        int ttl = Integer.parseInt(datMsg[2])-1; //decrease ttl
        boolean status = func.sendAgain(inMsg, username);//check sender status
        String newLT = func.isAvailable(func.timeSpent(datMsg[4]), datMsg[3]); //check lifetime of message
        String now = format.format(new Date()).toString();
        if(now.length()<8) now+=":00";
        //get position of receiver and sender
        String clientPos = posLat+","+posLong;
        String msgPos = datMsg[5]+","+datMsg[6];
        
        //check if message will be sent again, updated or dropped or not
        if(username.equalsIgnoreCase(datMsg[datMsg.length-1].trim())) return "myself"; else
        if(ttl<=0) return "ttl over"; else
        if(username.equals(datMsg[0])) return "packet arrived!!!"; else
        if(newLT.equals("time over")) return "time over"; else
        if(func.isToFar(clientPos, msgPos, datMsg[7]).equals("to far")) return "to far"; else
        if(status==false) return "update message!!!";


        //generate newMessage if available
        for(int i=0;i<datMsg.length;i++){
            if(i==2) newMsg += Integer.toString(ttl); else
            if(i==3) newMsg += newLT; else
            if(i==4) newMsg += now; else
            if(i==5) newMsg += posLat; else
            if(i==6) newMsg += posLong; else
                newMsg+= datMsg[i];
            newMsg+=",";
        }
        newMsg+=username;
        return newMsg;
    }
    
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        System.out.print("Username: "); //input username
        username = in.nextLine();
        System.out.print("Latitude: "); //input latitude
        posLat = in.nextLine();
        System.out.print("Longitude: "); //input longitude
        posLong = in.nextLine();
        System.out.println(username.toUpperCase());
        System.out.println("Create socket on address " + INET_ADDR + " and port " + PORT + ".");
        new Thread(new Client()).start();
    }
    
    
    public void run() 
    {
        //joining group
        try{
            addr = InetAddress.getByName(INET_ADDR);
            s = new MulticastSocket(PORT);
            s.joinGroup(addr);
        }
        catch (IOException ioEx)
        {
            System.out.println("\nUnable to join group!");
            System.exit(1);
        }
        
        String outMsg = new String();
        String inMsg = new String();
        
        while(true){
            long startTime = System.currentTimeMillis();
            long endTime = startTime + 30000;
            
            receivePacket recv = new receivePacket(s,addr,PORT);
            System.out.println("Start listening...");
            recv.start();
            while (System.currentTimeMillis() < endTime) {
            // Still within time theshold, wait a little longer
                try {
                     Thread.sleep(500);  // Sleep 1/2 second
                } catch (InterruptedException e) {
                     // Someone woke us up during sleep, that's OK
                }
            }
            System.out.println("Stop listening...");
            recv.interrupt();
            try {
                recv.join(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            try {
                System.out.println("   Resending packet...");
                System.out.println("   Total packet: "+dbPacket.size());
                // try sending every 60 second
                // sending every packet in dbPacket
                
                //remove the same message
                Object[] st = dbPacket.toArray();
                for (Object s : st) {
                  if (dbPacket.indexOf(s) != dbPacket.lastIndexOf(s)) {
                      dbPacket.remove(dbPacket.lastIndexOf(s));
                   }
                }
                
                for(int i=0;i<dbPacket.size();i++)
                {
                    System.out.println("      Message["+i+"]: "+dbPacket.get(i));
                }
                
                for(int i=0;i<dbPacket.size();i++){
                    System.out.println("   Try sending packet no "+i+": "+dbPacket.get(i));
                    try{
                        outMsg = newMsg(dbPacket.get(i), username);
                    } catch(ParseException ex ){
                        ex.printStackTrace();
                    }
                    
                    if(outMsg.equals("myself")){
                        System.out.println("      Packet["+i+"]: it's from yourself!!!");
                        dbPacket.remove(i); //drop if from myself
                        i--;
                    } else
                    if(outMsg.equals("ttl over")){
                        System.out.println("      Packet["+i+"]: TTL is done!");
                        dbPacket.remove(i); //drop if ttl over
                        i--;
                    } else
                    if(outMsg.equals("packet arrived!!!")){
                        System.out.println("      Packet["+i+"]: This packet is for you "+ username);
                        String arr = dbPacket.get(i)+","+username;
                        dbPacket.set(i, arr);
                        DatagramPacket outPacket = new DatagramPacket(arr.getBytes(),arr.getBytes().length, addr, PORT);
                        s.send(outPacket);
                        i--;
                    } else
                    if(outMsg.equals("time over")){
                        System.out.println("      Packet["+i+"]: Message no more available...(timeout)");
                        dbPacket.remove(i); //drop if packet lifetime is over
                        i--;
                    } else
                    if(outMsg.equals("to far")){
                        System.out.println("      Packet["+i+"]: Message to far to send!!!");
                        dbPacket.remove(i); //drop if packet is to far
                        i--;
                    } else
                    if(outMsg.equals("update message!!!")){
                        System.out.println("      Packet["+i+"]: You have received this packet...");
                        try{
                            String update = updateMessage(dbPacket.get(i));
                            dbPacket.set(i, update);
                            System.out.println("      Sending my packet...");
                            DatagramPacket outPacket = new DatagramPacket(update.getBytes(),update.getBytes().length, addr, PORT);
                            s.send(outPacket);
                        } catch(ParseException ex ){
                            ex.printStackTrace();
                        }
                    } else {
                        dbPacket.set(i, outMsg);
                        System.out.println("      Packet["+i+"]: "+outMsg);
                        System.out.println("      Sending message...");
                        DatagramPacket outPacket = new DatagramPacket(outMsg.getBytes(),outMsg.getBytes().length, addr, PORT);
                        s.send(outPacket);
                    }
                }
                for(int i=0;i<dbPacket.size();i++)
                {
                    System.out.println("      Message["+i+"]: "+dbPacket.get(i));
                }
                System.out.println("\n\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
