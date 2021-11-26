package pack;
import java.net.*;

public class server{
    public static DatagramSocket createSocket(int port){
        DatagramSocket ServerSocket = null;
        try{
            ServerSocket = new DatagramSocket(port);
        }
        catch(Exception e){
            System.out.println("Problemi: ");
            e.printStackTrace();;
        }
        return ServerSocket;
    }
        
}