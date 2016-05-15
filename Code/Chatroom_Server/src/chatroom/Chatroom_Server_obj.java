/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

/**
 *  Team Members
 *  
 *  Akash Gupta             - 1001122031
 *  Harshitha Gowda         - 1001098221
 *  Meet Brahmbhatt         - 1001119131
 *  Shruthi Shanthaveerappa - 1001106474
 *          
 */
public class Chatroom_Server_obj implements Runnable{
    
    public static ArrayList<Socket> clientSockets = new ArrayList<Socket>();
    public static ArrayList<String> clientInfo = new ArrayList<String>();
    public static ArrayList<ObjectInputStream> inputStreams = new ArrayList<ObjectInputStream>();
    public static ArrayList<ObjectOutputStream> outputStreams = new ArrayList<ObjectOutputStream>();
    public Socket sock;
    public ObjectInputStream objIn;
    public ObjectOutputStream objOut;
    public Message message;
    public DbConnection dbcon;
    public String uname;
    public ChatroomHandler chatroomhandler;
    public Chatroom_Server_obj(Socket tsock,ChatroomHandler chatroom){
        this.sock=tsock;
        chatroomhandler = chatroom;
        try {
            objIn = new ObjectInputStream(sock.getInputStream());
            objOut= new ObjectOutputStream(sock.getOutputStream());
            objOut.flush();
            clientSockets.add(sock);
            inputStreams.add(objIn);
            outputStreams.add(objOut);
            
            dbcon = new DbConnection();
        } catch (IOException ex) {
            Logger.getLogger(Chatroom_Server_obj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void run(){
        try{
           try{
               while(true){
                   if(objIn.available()<0){
                       return;
                   }
                    message = (Message)objIn.readObject();
                   
                   System.out.println(message);
                   
                   if(message.type.equals("connection")){
//                       Message m1 = new Message("connection", "server", "true", "test");
                       Message m1;
                       System.err.println("number of objects in socket array:"+clientSockets.size());
                       if(clientSockets.size()==1){
                           m1 = new Message("connection", "server", "true", "test", sock.getInetAddress(), sock.getInetAddress());
                       }else{
                           
                           int numberOfSocket = clientSockets.size();
                           Socket lastObj = clientSockets.get(numberOfSocket-2);
                           m1 = new Message("connection", "server", "true", "test", sock.getInetAddress(), lastObj.getInetAddress());
                       }
                       
                       objOut.writeObject(m1);
                       objOut.flush();
                       
                   }else if(message.type.equals("signup")){
                       //creating user
                       //fail
                       String username = message.sender;
                       String password = message.content;
                       boolean check = dbcon.signup(username, password);
                       if(check){
                           System.out.println("Signup Successful");
                           uname = username;
                           Message m1 = new Message("signup", "server", "true", username);
                           objOut.writeObject(m1);
                           objOut.flush();
                       }else{
                           Message m1 = new Message("signup", "server", "false", username);
                           objOut.writeObject(m1);
                           objOut.flush();
                       }
                       
                   }else if(message.type.equals("login")){
                       String username = message.sender;
                       String password = message.content;
                       boolean check = dbcon.Signin(username, password);
                       if(check){
                           System.out.println("Login Successful");
                           uname=username;
                           Message m1 = new Message("login", "server", "true", username);
                           objOut.writeObject(m1);
                           objOut.flush();
                       }else{
                           Message m1 = new Message("login", "server", "false", username);
                           objOut.writeObject(m1);
                           objOut.flush();
                       }
                   }else if(message.type.equals("create")){
                       String nameOfChatroom = message.content;
                       boolean success = chatroomhandler.addChatroom(nameOfChatroom);
                       Message m1=null;
                       if(success){
                           m1 = new Message("create", message.sender, "true", nameOfChatroom);
                       }else{
                           m1 = new Message("create", message.sender, "false", nameOfChatroom);
                       }
                       objOut.writeObject(m1);
                       objOut.flush();
//                       for(int i = 0;i<clientSockets.size();i++){
//                               
//                               Socket sock1 = (Socket)clientSockets.get(i);
//                               ObjectOutputStream tobj = outputStreams.get(i);
//                               tobj.writeObject(m1);
//                               tobj.flush();
//                       }
                   }else if(message.type.equals("message")){
                       String sender = message.sender;
                       String textMsg = message.content;
                       String recipient = message.recipient;
                       Chatroom_base askedChatroom = null;
                       for(int i=0;i<chatroomhandler.chatroomlist.size();i++){
                           Chatroom_base obj = chatroomhandler.chatroomlist.get(i);
                           if(obj.chatroom_name.equals(recipient)){
                               
                               askedChatroom=(Chatroom_base)obj;
                               break;
                           }
                       }
                       Message m1 = new Message("message", sender, textMsg, recipient);
                       for(int i = 0;i<askedChatroom.chatSocketArray.size();i++){

                           Socket sock1 = (Socket)askedChatroom.chatSocketArray.get(i);
                           int indexOfSocket = clientSockets.indexOf(sock1);
                           ObjectOutputStream tobj = outputStreams.get(indexOfSocket);
                           tobj.writeObject(m1);
                           tobj.flush();
                       }
                       
                       
                   }else if(message.type.equals("leave")){
                       String from_which_chatroom = message.content;
                       String who_wants_to_leave = message.sender;
                       
                       for(int i=0;i<chatroomhandler.chatroomlist.size();i++){
                           Chatroom_base tcht_rm_obj = (Chatroom_base)chatroomhandler.chatroomlist.get(i);
                           if(tcht_rm_obj.chatroom_name.equals(from_which_chatroom)){
                               System.out.println("Chatroom found");
                               int index = tcht_rm_obj.chatSocketArray.indexOf(sock);
                               
                               for(int j=0;j<clientSockets.size();j++){
                                   Socket tsock = (Socket)clientSockets.get(j);
                                   ObjectOutputStream objOp = (ObjectOutputStream)outputStreams.get(j);
                                   objOp.writeObject(new Message("leave", "server", who_wants_to_leave, from_which_chatroom));
                               }
                               tcht_rm_obj.chatSocketArray.remove(index);
                               tcht_rm_obj.userInfo.remove(index);
                               break;
                           }
                       }
                   }else if(message.type.equals("logout")){
                       
                       objOut.writeObject(new Message("logout", "server", "true", "user"));
                       objOut.flush();
                       int index = clientSockets.indexOf(sock);
                       clientSockets.remove(index);
//                       clientInfo.remove(index);
                       
                   }else if(message.type.equals("getChatroomList")){
                       ArrayList<String> chatroomNameList = new ArrayList<String>();
                       for(int i=0;i<chatroomhandler.chatroomlist.size();i++){
                           Chatroom_base baseObj = chatroomhandler.chatroomlist.get(i);
//                           String tempString = baseObj.chatroom_name+"("+baseObj.chatSocketArray.size()+")";
                           String tempString = baseObj.chatroom_name;
                           chatroomNameList.add(tempString);
                       }
//                       Message m1 = new Message("server", "getChatroomList", "chatroomlist", uname, chatroomNameList);
                       Message m1 = new Message("getChatroomList", "server", "chatroomlist", uname, chatroomNameList);
                       objOut.writeObject(m1);
                       objOut.flush();
                       
                   }else if(message.type.equals("join")){
                       Chatroom_base askedChatroom = null;
                       String askedChatroomName=message.content;
                       for(int i=0;i<chatroomhandler.chatroomlist.size();i++){
                           Chatroom_base obj = chatroomhandler.chatroomlist.get(i);
                           if(obj.chatroom_name.equals(askedChatroomName)){
                               askedChatroom=(Chatroom_base)obj;
                               break;
                           }
                       }    
                       askedChatroom.chatSocketArray.add(sock);
                       askedChatroom.userInfo.add(uname);
//                       try{
//                           Message m1 = new Message("join", "server", uname, message.content);
//                           for(int i = 0;i<askedChatroom.chatSocketArray.size();i++){
//                               
//                               Socket sock1 = (Socket)askedChatroom.chatSocketArray.get(i);
//                               int indexOfSocket = clientSockets.indexOf(sock1);
//                               ObjectOutputStream tobj = outputStreams.get(indexOfSocket);
//                               tobj.writeObject(m1);
//                               tobj.flush();
//                           }
//                       }catch(Exception e){
//                           System.out.println("Exception in join message:"+e);
//                       }
                       try{
                           Message m1 = new Message("join", "server", uname, message.content);
                           for(int i = 0;i<clientSockets.size();i++){
                               
                               Socket sock1 = (Socket)clientSockets.get(i);
                               ObjectOutputStream tobj = outputStreams.get(i);
                               tobj.writeObject(m1);
                               tobj.flush();
                           }
                       }catch(Exception e){
                           System.out.println("Exception in join message:"+e);
                       }
                           
                   }else if(message.type.equals("chat_user_list")){
                       Chatroom_base askedChatroom = null;
                       String askedChatroomName=message.content;
                       System.out.println(askedChatroomName);
                       for(int i=0;i<chatroomhandler.chatroomlist.size();i++){
                           Chatroom_base obj = chatroomhandler.chatroomlist.get(i);
                           if(obj.chatroom_name.equals(askedChatroomName)){
                               System.out.println("chat room found");
                               askedChatroom=(Chatroom_base)obj;
                               break;
                           }
                       }
                       objOut.writeObject(new Message("chat_user_list", "server", uname, message.content, askedChatroom.userInfo));
                       objOut.flush();
                   }
                   
               }
           }finally{
               sock.close();
           }
        }catch(Exception e){
            System.out.println("Server side exception:"+e);
        }
        
    }
    
}
