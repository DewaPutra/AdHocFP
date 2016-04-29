
package multicast;

import java.io.*;
import java.util.*;
import java.net.*;

public class orakorek {
    public static String c = "senderxxx";
    public static void main(String[] args) {
        String a ="receiver,message,ttl,lifetime,sender,sender1,sender2";
        String dat[]=a.split(",");
        String b = "sender3";
        String all ="";
        
        for(int i=0;i<dat.length;i++){
            all+=dat[i];
            all+=" ";
        }
        all+=c;
        
        System.out.println(all);
    }
}
