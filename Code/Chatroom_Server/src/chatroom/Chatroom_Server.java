/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom;
import java.net.*;
import java.util.*;
import java.io.*;
/**
 *  Team Members
 *  
 *  Akash Gupta             - 1001122031
 *  Harshitha Gowda         - 1001098221
 *  Meet Brahmbhatt         - 1001119131
 *  Shruthi Shanthaveerappa - 1001106474
 *          
 */
public class Chatroom_Server {
     
     public static ChatroomHandler chatroom_handler;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        try{
            final int serverPort = 9000;
            ServerSocket ser_sock = new ServerSocket(serverPort);
            chatroom_handler = new  ChatroomHandler();
            chatroom_handler.fetchChatroomList();
            for(int i=0;i<chatroom_handler.chatroomlist.size();i++){
                Chatroom_base obj = (Chatroom_base)chatroom_handler.chatroomlist.get(i);
                System.err.println(obj.chatroom_name);
            }
            while(true){
                Socket cli_sock = ser_sock.accept();
                
                System.out.println("connection accepted");
                
                Chatroom_Server_obj newconn = new Chatroom_Server_obj(cli_sock,chatroom_handler);
                
                Thread tobj = new Thread(newconn);
                
                tobj.start();
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
}
