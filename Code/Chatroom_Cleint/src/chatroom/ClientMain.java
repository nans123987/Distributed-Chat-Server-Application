/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package chatroom;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *  Team Members
 *
 *  Akash Gupta             - 1001122031
 *  Harshitha Gowda         - 1001098221
 *  Meet Brahmbhatt         - 1001119131
 *  Shruthi Shanthaveerappa - 1001106474
 *
 */
public class ClientMain {
    public static void main(String args[]) {
        try{
            final int serverPort = 9010;
            ServerSocket ser_sock = new ServerSocket(serverPort);
            
            LoginScreen loginScreenObj = new LoginScreen();
            loginScreenObj.setVisible(true);
            
            while(true){
                Socket cli_sock = ser_sock.accept();
                System.out.println("connection accepted");
                ClientSideTempServer newconn = new ClientSideTempServer(cli_sock,loginScreenObj.client);
                
                Thread tobj = new Thread(newconn);
                
                tobj.start();
                System.err.println("after creating temp server");
                
            }
        }catch(Exception e){
            System.out.println("Kya avyu aa:"+e);
        }
        
        
    }
    
}
