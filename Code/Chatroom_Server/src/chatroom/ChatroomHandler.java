/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom;

import java.io.Serializable;
import java.util.ArrayList;
import java.net.*;

/**
 *  Team Members
 *  
 *  Akash Gupta             - 1001122031
 *  Harshitha Gowda         - 1001098221
 *  Meet Brahmbhatt         - 1001119131
 *  Shruthi Shanthaveerappa - 1001106474
 *          
 */
public class ChatroomHandler{
    public ArrayList<Chatroom_base> chatroomlist;
    DbConnection dbcon;
    ChatroomHandler(){
        //load all chatrooms from the database and add those into arrayList
        dbcon = new DbConnection();
        chatroomlist = new ArrayList<Chatroom_base>();
        
        
    }
    public void fetchChatroomList(){
        ArrayList<String> chtrmlist = dbcon.getChatroomList();
        
        for(int i = 0;i<chtrmlist.size();i++){
            Chatroom_base obj = new Chatroom_base(chtrmlist.get(i));
            chatroomlist.add(obj);
            obj=null;
            
        }
    }
    public boolean addChatroom(String chatroom_name){
        boolean success = dbcon.createChatRooms(chatroom_name);
        if(success){
            Chatroom_base obj = new Chatroom_base(chatroom_name);
            chatroomlist.add(obj);
            obj=null;
            return true;
        }else{
            return false;
        }        
    }
    public void addClientToChatroom(String chatroom_name,Socket tsock,String tusername){
        
    }
    public void leaveChatroom(String chatroom_name, Socket tsock){
        
    }
}
