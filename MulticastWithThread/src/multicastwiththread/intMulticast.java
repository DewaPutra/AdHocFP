
package multicastwiththread;

import java.util.*;
import java.io.*;
import java.net.*;
import java.text.ParseException;

public interface intMulticast {
    
    public boolean sendAgain(String inMsg, String username);
    
    public void printMsg(String inMsg);
    
    public String createMessage(String username, String posLat, String posLong);
    
    public String timeSpent(String timesent) throws ParseException;
    
    public String isAvailable(String duration, String lifetime);
    
    public String isToFar(String desPos, String srcPos, String dis);
}
