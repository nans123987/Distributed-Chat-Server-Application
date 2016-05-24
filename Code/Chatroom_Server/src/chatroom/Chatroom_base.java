/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom;
import java.io.*;
import java.net.*;
import java.util.*;

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
