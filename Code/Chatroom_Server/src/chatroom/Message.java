/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom;

import java.io.Serializable;
import java.net.InetAddress;
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

public class Message implements Serializable{
    
    private static final long serialVersionUID = 1L;
    public String type, sender, content, recipient;
    public ArrayList<String> dataList;
    InetAddress senderIp,receiverIp;
    
    
    public Message(String type, String sender, String content, String recipient){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
    }
    public Message(String type, String sender, String content, String recipient,InetAddress tsenderIp, InetAddress treceiverIp){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
        this.senderIp=tsenderIp;
        this.receiverIp=treceiverIp;
    }
    public Message(String type, String sender, String content, String recipient,ArrayList<String> tdataStirng){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
        this.dataList=tdataStirng;
    }
    public Message(String type, String sender, String content, String recipient,ArrayList<String> tdataStirng,InetAddress tsenderIp, InetAddress treceiverIp){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
        this.dataList=tdataStirng;
        this.senderIp=tsenderIp;
        this.receiverIp=treceiverIp;
    }
    @Override
    public String toString(){
        return "{type='"+type+"', sender='"+sender+"', content='"+content+"', recipient='"+recipient+"'}";
    }
}
