/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package chatroom;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author meetsbrahmbhatt
 */
public class ClientSideTempClient implements Runnable{
    public Socket socket;
    public ObjectInputStream objIn;
    public ObjectOutputStream objOut;
    public ClientSideTempClient(InetAddress serverIp){
        try{
            System.err.println("trying to connect"+serverIp.getHostAddress());
            String ipAddress = serverIp.getHostAddress();
            socket = new Socket(ipAddress, 9010);
            
            objOut = new ObjectOutputStream(socket.getOutputStream());
            objOut.flush(); 
        }catch(Exception e){
            System.err.println("Exception in ClientSideTempClient"+e);
        }
    }
    public void sendMessageToLocalServer(Message msg){
        try {
            objOut.writeObject(msg);
            objOut.flush();
            System.out.println("Outgoing message in clientSideTempClient: "+msg);
        }catch(Exception e){
            System.out.println("Exception in chatroom_client:"+e);
        }
        
    }

    @Override
    public void run() {
        
    }
}
