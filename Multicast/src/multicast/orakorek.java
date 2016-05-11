
package multicast;

import java.io.*;
import java.util.*;
import java.net.*;
import java.text.*;

public class orakorek extends Thread{
    
    public static void main(String[] args) throws ParseException, InterruptedException {
        ArrayList<String> as = new ArrayList<String>();
        as.add("bla");
        as.add("bla1");
        as.add("bla2");
        System.out.println(as.size());
        for(int i=0;i<as.size();i++){
            if(as.get(i).equals("bla")) as.remove(i);
            System.out.println(i+" "+as.get(i));
        }
        System.out.println(as.size());
        for(int i=0;i<as.size();i++){
            System.out.println(i+" "+as.get(i));
        }
        boolean send = false;
       
//        long start = System.currentTimeMillis();
//        long end = start + 10*1000; // 60 seconds * 1000 ms/sec
//        int i=0;
//        Scanner in = new Scanner(System.in);
//        String a = new String();
//        
//        while (System.currentTimeMillis() < end)
//        {
//            System.out.println(++i);
//            a = in.nextLine();
//            Thread.sleep(1000);
//        }
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
//        String n = format.format(new Date()).toString();
//        Date d1 = format.parse(format.format(new Date()).toString());
//        Date d2 = format.parse(format.format(new Date()).toString());
//        System.out.println(format.format(new Date()).toString());
//        long diff = d2.getTime() - d1.getTime();
//        long diffSeconds,diffMinutes,diffHours;
//
//        if (diff > 0) {
//        diffSeconds = diff / 1000 % 60;
//        diffMinutes = diff / (60 * 1000) % 60;
//        diffHours = diff / (60 * 60 * 1000);
//        }
//        else{
//        long diffpos = (24*((60 * 60 * 1000))) + diff;
//        diffSeconds = diffpos / 1000 % 60;
//        diffMinutes = diffpos / (60 * 1000) % 60;
//        diffHours = (diffpos / (60 * 60 * 1000));
//        }
//        System.out.println(diffHours+":"+diffMinutes+":"+diffSeconds);
//        test x[] = new test[10];
//        x[0] = new test();
//        x[0].message = "fuck me";
//        x[0].receiver = "User";
//        for(int i=0;i<10;i++){
//            System.out.println("Executed "+i);
//            if(x[i].message=="" && x[i].receiver==""){
//                System.out.println("message["+i+"]: null");
//                System.out.println("receiver["+i+"]: null");
//            } else{
//                System.out.println("message["+i+"]: "+x[i].message);
//                System.out.println("receiver["+i+"]: "+x[i].receiver);
//            }
//            
//        }
    }
}
