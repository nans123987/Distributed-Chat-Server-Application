/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 *  Team Members
 *  
 *  Akash Gupta             - 1001122031
 *  Harshitha Gowda         - 1001098221
 *  Meet Brahmbhatt         - 1001119131
 *  Shruthi Shanthaveerappa - 1001106474
 *          
 */
public class Chatroom_base{
    public ArrayList<Socket> chatSocketArray;
    public ArrayList<String> userInfo;
    public String chatroom_name;
    
    Chatroom_base(String tname){
        userInfo = new ArrayList<String>();
        chatSocketArray = new ArrayList<Socket>();
        chatroom_name=tname;
    }
    
}
