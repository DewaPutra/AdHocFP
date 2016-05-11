package multicastwiththread;

import java.io.*;
import static java.lang.Math.abs;
import java.net.*;
import java.util.*;
import java.text.*;

public class implMulticast implements intMulticast{
    
    public boolean sendAgain(String inMsg, String username){
        boolean send=false;
        String datMsg[] = inMsg.split(",");
        for(int i=8;i<datMsg.length;i++){
            if(username.equals(datMsg[i])){
                return false;
            } else
            {
                send=true;
            }
        }
        return send;
    }
    
    public void printMsg(String inMsg){
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
    
        
    public String createMessage(String username, String posLat, String posLong){
        String outMsg = new String();
        Scanner in = new Scanner(System.in);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        System.out.print("Message: ");
        String p = in.nextLine();
        System.out.print("Receiver: ");
        String to = in.nextLine();
        System.out.print("TTL: ");
        String ttl = in.nextLine();
        System.out.print("Message lifetime(HH:mm:ss): ");
        String lt = in.nextLine();
        String now = format.format(new Date()).toString();
        System.out.print("Radius: ");
        String r = in.nextLine();
        //receiver,message,ttl,lifetime,timesent,lat,long,dis,sender
        outMsg = to+","+p+","+ttl+","+lt+","+now+","+posLat+","+posLong+","+r+","+username;
        return outMsg;
    }
    
    public String timeSpent(String timesent) throws ParseException{ // calculate time passed
        String duration = new String();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date d1 = format.parse(timesent); 
        Date d2 = format.parse(format.format(new Date()).toString());
        
        if(timesent.equalsIgnoreCase(format.format(new Date()).toString())){
            return "00:00:00";
        }
        
        long diff = d2.getTime() - d1.getTime();
        long diffSeconds,diffMinutes,diffHours;

        if (diff > 0) {
        diffSeconds = diff / 1000 % 60;
        diffMinutes = diff / (60 * 1000) % 60;
        diffHours = diff / (60 * 60 * 1000);
        }
        else{
        long diffpos = (24*((60 * 60 * 1000))) + diff;
        diffSeconds = diffpos / 1000 % 60;
        diffMinutes = diffpos / (60 * 1000) % 60;
        diffHours = (diffpos / (60 * 60 * 1000));
        }
        duration = diffHours+":"+diffMinutes+":"+diffSeconds;
        return duration;
    }
    
    public String isAvailable(String duration, String lifetime){ // calculate is message available
        String newLT="";
        long h,m,s;
        int i=0;
        String dur[] = duration.split(":");
        String lif[] = lifetime.split(":");
        int dur1 = Integer.parseInt(dur[0])*3600+Integer.parseInt(dur[1])*60+Integer.parseInt(dur[2]);
        int lif1 = Integer.parseInt(lif[0])*3600+Integer.parseInt(lif[1])*60+Integer.parseInt(lif[2]);
        if(lif1<=dur1) return "time over"; 
        else {
            int rest = lif1-dur1;;
            s = rest %60;
            m = (rest%3600) / 60;
            h = rest/3600;
        }
        newLT = h+":"+m+":"+s;
        return newLT;
    }
    
    public String isToFar(String desPos, String srcPos, String dis){
        String des[] = desPos.split(",");
        String src[] = srcPos.split(",");
        int lat = abs(Integer.parseInt(des[0])-Integer.parseInt(src[0]));
        int lon = abs(Integer.parseInt(des[1])-Integer.parseInt(src[1]));
        if(lat>Integer.parseInt(dis) || lon>Integer.parseInt(dis)) return "to far"; else
            return "still in distance";
    }
}
