/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package chatroom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author meetsbrahmbhatt
 */
public class ClientSideTempServer implements Runnable{
    Socket clientSocket;
    ObjectInputStream objIn;
    ObjectOutputStream objOut;
    Chatroom_Cleint client;
    public ClientSideTempServer(Socket cliSocket,Chatroom_Cleint tclient) throws IOException{
        clientSocket = cliSocket;
        client = tclient;
        objIn= new ObjectInputStream(clientSocket.getInputStream());
        objOut= new ObjectOutputStream(clientSocket.getOutputStream());
    }
    
    @Override
    public void run(){
        try{
            try{
                while(true){
                    if(objIn.available()<0&&objIn!=null){
                        return;
                    }
                    Message message = (Message)objIn.readObject();
                    System.out.println(message);
                    if(message.type.equals("tempJoin")){
                        InetAddress senderIp = message.senderIp;
                        InetAddress receiverIp=message.receiverIp;
                        InetAddress storedServerIp = client.clientSideServerIp;
                        System.err.println("**"+senderIp+"**"+receiverIp+"**"+storedServerIp+"** stop here....");
                        if(storedServerIp==null){
                            // for the second member of the ringtopology
                            client.clientTemp = new ClientSideTempClient(senderIp);
                            Thread obj1 = new Thread(client.clientTemp);
                            obj1.start();
                            client.clientSideServerIp=senderIp;
                            System.err.println("************New ClientSideServerIp"+senderIp);
                            Message msg = new Message("ringCompletion", client.username,"first member joined last member", "lastMember", receiverIp, senderIp);
                            client.clientTemp.sendMessageToLocalServer(msg);
                        }else if(!storedServerIp.getHostAddress().equals(receiverIp.getHostAddress())){
                            client.clientTemp.sendMessageToLocalServer(message);
                        }else if(storedServerIp.getHostAddress().equals(receiverIp.getHostAddress())){
                            
                            client.clientSideServerIp = senderIp;
//                            client.clientTemp.socket.close();
                            client.clientTemp = new ClientSideTempClient(senderIp);
                            Thread obj1 = new Thread(client.clientTemp);
                            obj1.start();
                            Message msg = new Message("ringCompletion", client.username,"first member joined last member", "lastMember", receiverIp, senderIp);
                            client.clientTemp.sendMessageToLocalServer(msg);
                        }
                    }else if(message.type.equals("ringCompletion")){
                        //do nothing. It's just an acknoledge
                    }else if(message.type.equals("tempGetChatroomList")){
                        InetAddress senderIp = message.senderIp;
                        InetAddress receiverIp=message.receiverIp;
                        InetAddress storedServerIp = client.clientSideServerIp;
                        System.err.println("**"+senderIp+"**"+receiverIp+"**"+storedServerIp+"**");
                        if(!storedServerIp.getHostAddress().equals(senderIp.getHostAddress())){
                            System.err.println("I am not super peer. I will pass message to my local server");
                            client.clientTemp.sendMessageToLocalServer(message);
                        }else if(storedServerIp.getHostAddress().equals(senderIp.getHostAddress())){
                            System.err.println("I am the super peer and i have to give chatroom list");
                            ArrayList<String> chatroomNameWithNum = new ArrayList<String>();
                            for(int i=0;i<client.chatroomnames.size();i++){
                                String tempObjct = client.chatroomnames.get(i)+"("+client.numberOfClientInGroup.get(i)+")";
                                chatroomNameWithNum.add(tempObjct);
                            }
                            System.err.println(chatroomNameWithNum);
                            client.clientTemp.sendMessageToLocalServer(new Message("tempGetChatroomListAns", "localMainServer", "listOfchatroomList", "localNewClient", chatroomNameWithNum, client.clientSideServerIp, senderIp));
                        }
                    }else if(message.type.equals("tempGetChatroomListAns")){
                        InetAddress senderIp = message.senderIp;
                        InetAddress receiverIp=message.receiverIp;
                        InetAddress storedServerIp = client.clientSideServerIp;
                        ArrayList<String> chatroomListNameWithNum = new ArrayList<String>();
                        chatroomListNameWithNum=message.dataList;
                        System.err.println(chatroomListNameWithNum+"----------------");
                        client.chatroomnames=new ArrayList<String>();
                        client.numberOfClientInGroup=new ArrayList<Integer>();
                        for(int i=0;i<chatroomListNameWithNum.size();i++){
                            int indexOfOpenBreket = chatroomListNameWithNum.get(i).indexOf('(');
                            int indexOfCloseBreket = chatroomListNameWithNum.get(i).indexOf(')');
                            String trueName = chatroomListNameWithNum.get(i);
                            
                            trueName=trueName.substring(0, indexOfOpenBreket);
                            System.err.println("truename:"+trueName);
                            client.chatroomnames.add(trueName);
                            Integer numOfClient = Integer.parseInt(chatroomListNameWithNum.get(i).substring(indexOfOpenBreket+1, indexOfCloseBreket));
                            client.numberOfClientInGroup.add(numOfClient);
                        }
                        System.err.println("chatroomname:"+client.chatroomnames);
                        System.err.println("chatroommember:"+client.numberOfClientInGroup);
                        if(client.chtrm!=null){
                            client.chtrm.chatroomNameList(client.chatroomnames,client.numberOfClientInGroup);
                        }
                        
                    }else if(message.type.equals("joinGroup")){
                        InetAddress senderIp = message.senderIp;
                        InetAddress receiverIp=message.receiverIp;
                        InetAddress storedServerIp = client.clientSideServerIp;
                        
                        if(!storedServerIp.getHostAddress().equals(senderIp.getHostAddress())){
                            client.clientTemp.sendMessageToLocalServer(message);
                        }
                        if(client.chtrm!=null){
                            ArrayList<String> chatroomListNameWithNum = new ArrayList<String>();
                            chatroomListNameWithNum=message.dataList;
                            System.err.println(chatroomListNameWithNum+"----------------");
                            client.chatroomnames=new ArrayList<String>();
                            client.numberOfClientInGroup=new ArrayList<Integer>();
                            for(int i=0;i<chatroomListNameWithNum.size();i++){
                                int indexOfOpenBreket = chatroomListNameWithNum.get(i).indexOf('(');
                                int indexOfCloseBreket = chatroomListNameWithNum.get(i).indexOf(')');
                                String trueName = chatroomListNameWithNum.get(i);
                                
                                trueName=trueName.substring(0, indexOfOpenBreket);
                                System.err.println("truename:"+trueName);
                                client.chatroomnames.add(trueName);
                                Integer numOfClient = Integer.parseInt(chatroomListNameWithNum.get(i).substring(indexOfOpenBreket+1, indexOfCloseBreket));
                                client.numberOfClientInGroup.add(numOfClient);
                                
                            }
                            client.chtrm.chatroomNameList(client.chatroomnames,client.numberOfClientInGroup);
                        }
                    }else if(message.type.equals("userListInGroup")){
                        InetAddress senderIp = message.senderIp;
                        InetAddress receiverIp=message.receiverIp;
                        InetAddress storedServerIp = client.clientSideServerIp;
                        InetAddress addr = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
                        if(!client.clientIp.getHostAddress().equals(senderIp.getHostAddress())){
                            String groupName = message.recipient;
                            for(int i=0;i<client.openedChatroom.size();i++){
                                ChatWindow tobj = client.openedChatroom.get(i);
                                System.out.println(tobj.chat_window_name);
                                if(tobj.chat_window_name.equals(groupName)){
                                    ArrayList<String> userInChatList = message.dataList;
                                    userInChatList.add(client.username);
                                    message.dataList=userInChatList;
                                    ArrayList<String> currentUserInChatroom = tobj.connectedUsers;
                                    currentUserInChatroom.add(message.sender);
                                    tobj.update(currentUserInChatroom);
                                    tobj.addmessage(message.sender+" has joined");
                                    break;
                                }
                            }
                            client.clientTemp.sendMessageToLocalServer(message);
                        }else{
                            String groupName = message.recipient;
                            
                            ArrayList<String> userInChatList = message.dataList;
                            int indexOf = userInChatList.indexOf(client.username);
                            userInChatList.set(indexOf, "You");
                            for(int i=0;i<client.openedChatroom.size();i++){
                                ChatWindow tobj = client.openedChatroom.get(i);
                                System.out.println(tobj.chat_window_name);
                                if(tobj.chat_window_name.equals(groupName)){
                                    tobj.update(userInChatList);
                                    break;
                                }
                            }
                        }
                    }else if(message.type.equals("tempMessage")){
                        InetAddress senderIp = message.senderIp;
                        InetAddress receiverIp=message.receiverIp;
                        InetAddress storedServerIp = client.clientSideServerIp;
                        System.err.println("**"+senderIp+"**"+receiverIp+"**"+storedServerIp+"**");
                        String groupName = message.recipient;
                        for(int i=0;i<client.openedChatroom.size();i++){
                            ChatWindow tobj = client.openedChatroom.get(i);
                            System.out.println(tobj.chat_window_name);
                            if(tobj.chat_window_name.equals(groupName)){
                                tobj.addmessage(message.sender+"::"+message.content);
                                break;
                            }
                        }
                        if(!storedServerIp.getHostAddress().equals(senderIp.getHostAddress())){
                            client.clientTemp.sendMessageToLocalServer(message);
                        }
                    }else if(message.type.equals("leaveGroup")){
                        InetAddress senderIp = message.senderIp;
                        InetAddress receiverIp=message.receiverIp;
                        InetAddress storedServerIp = client.clientSideServerIp;
                        System.err.println("**"+senderIp+"**"+receiverIp+"**"+storedServerIp+"**");
                        String groupName = message.recipient;
                        for(int i=0;i<client.openedChatroom.size();i++){
                            ChatWindow tobj = client.openedChatroom.get(i);
                            System.out.println(tobj.chat_window_name);
                            if(tobj.chat_window_name.equals(groupName)){
                                tobj.addmessage(message.sender+" left. :-(");
                                break;
                            }
                        }
                        int indexOfChatroom = client.chatroomnames.indexOf(groupName);
                        System.err.println("index of chatroom:"+indexOfChatroom+"Number of users in that group"+client.numberOfClientInGroup.get(indexOfChatroom));
                        int numberOfClientInChatroom = client.numberOfClientInGroup.get(indexOfChatroom)-1;
                        client.numberOfClientInGroup.set(indexOfChatroom, numberOfClientInChatroom);
                        client.chtrm.chatroomNameList(client.chatroomnames, client.numberOfClientInGroup);
                        if(!storedServerIp.getHostAddress().equals(senderIp.getHostAddress())){
                            client.clientTemp.sendMessageToLocalServer(message);
                        }
                    }else if(message.type.equals("tempsignOut")){
                        InetAddress senderIp = message.senderIp;
                        InetAddress receiverIp=message.receiverIp;
                        InetAddress storedServerIp = client.clientSideServerIp;
                        System.err.println("**"+senderIp+"**"+receiverIp+"**"+storedServerIp+"**");
                        if(!senderIp.getHostAddress().equals(client.clientSideServerIp.getHostAddress())){
                            client.clientTemp.sendMessageToLocalServer(message);
                        }else{
                            client.clientSideServerIp = receiverIp;
//                            client.clientTemp.socket.close();
                            client.clientTemp = new ClientSideTempClient(receiverIp);
                            Thread obj1 = new Thread(client.clientTemp);
                            obj1.start();
                            Message msg = new Message("ringCompletionOnSignOut", client.username,"first member joined last member", "lastMember", receiverIp, senderIp);
                            client.clientTemp.sendMessageToLocalServer(msg);
                        }
                    }else if(message.type.equals("ringCompletionOnSignOut")){
                        client.logout();
                    }
                    
                }
            }finally{
                clientSocket.close();
            }
            
        }catch(Exception e){
            System.out.println("ClientSideTempServer  exception`11:"+e);
        }
        
    }
}
