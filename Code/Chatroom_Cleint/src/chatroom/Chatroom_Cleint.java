/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package chatroom;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *  Team Members
 *
 *  Akash Gupta             - 1001122031
 *  Harshitha Gowda         - 1001098221
 *  Meet Brahmbhatt         - 1001119131
 *  Shruthi Shanthaveerappa - 1001106474
 *
 */
public class Chatroom_Cleint implements Runnable{
    
    /**
     * @param args the command line arguments
     */
    
    
    public Socket socket;
    public LoginScreen ui;
    public ObjectInputStream objIn;
    public ObjectOutputStream objOut;
    public Signup signUpUI;
    public Signin signInUI;
    public String username;
    public chatrooms chtrm;
    public ArrayList<ChatWindow> openedChatroom;
    public InetAddress clientSideServerIp;
    public InetAddress clientIp;
    ClientSideTempClient clientTemp;
    ArrayList<String> chatroomnames;
    ArrayList<Integer> numberOfClientInGroup;
    boolean userSignedIn;
    
    public Chatroom_Cleint(LoginScreen frame) throws Exception{
        ui = frame;
        socket = new Socket("192.168.43.251", 9000);
        objOut = new ObjectOutputStream(socket.getOutputStream());
        objOut.flush();
        objIn = new ObjectInputStream(socket.getInputStream());
        openedChatroom = new ArrayList<ChatWindow>();
        userSignedIn=false;
    }
    
    @Override
    public void run() {
        while(true){
            try{
                Message message = (Message)objIn.readObject();
                System.out.println("Received msg:"+message);
                if(message.type.equals("connection")){
                    if(message.content.equals("true")){
                        System.out.println("connection successful");
                        InetAddress senderIp = message.senderIp;
                        clientIp = senderIp;
                        InetAddress receiverIp = message.receiverIp;
                        System.err.println(senderIp+"------"+receiverIp);
                        if(senderIp.getHostAddress().equals(receiverIp.getHostAddress())){
                            //first member of ring topology
                            System.err.println("first member");
                        }else{
                            System.err.println("not the first member");
                            clientSideServerIp=receiverIp;
                            clientTemp = new ClientSideTempClient(receiverIp);
                            Thread obj1 = new Thread(clientTemp);
                            obj1.start();
                            Message m1 = new Message("tempJoin", username, "new temp client side connection creadted ", "clientsideServer", senderIp, receiverIp);
                            clientTemp.sendMessageToLocalServer(m1);
                        }
                    }else{
                        System.out.println("something went wrong");
                    }
                }else if(message.type.equals("signup")){
                    System.out.println("Signup message"+message);
                    if(message.content.equals("true")){
                        username = message.recipient;
                        signUpUI.showChatroomList(username);
                        userSignedIn=true;
                    }else{
                        signUpUI.showUsernameExist();
                    }
                    signUpUI=null;
                }else if(message.type.equals("login")){
                    System.out.println("Login message"+message);
                    if(message.content.equals("true")){
                        username = message.recipient;
                        signInUI.showChatroomList(username);
                        userSignedIn=true;
                    }else{
                        signInUI.invalidCredentials();
                    }
                    signInUI=null;
                }else if(message.type.equals("create")){
                    if(message.content.equals("true")){
                        String creatorname = message.sender;
                        for(int i=0;i<chatroomnames.size();i++){
                            String tempString = chatroomnames.get(i);
                            if(tempString.compareToIgnoreCase(message.recipient)>0){
                                chatroomnames.add(i,message.recipient);
                                numberOfClientInGroup.add(i,0);
                                break;
                            }
                        }
                        joinGroup(message.recipient);
                        
//                        getChatLists(chtrm, new Message("getChatroomList", username, "getlist", "server"));
//                        if(username.equals(creatorname)){
//                            joinChatRoom(chtrm, new Message("join", username, message.recipient, "server"));
//                        }else{
//                            for(int i=0;i<openedChatroom.size();i++){
//                                ChatWindow obj = openedChatroom.get(i);
//                                obj.addmessage("Chatroom "+message.recipient+" created");
//                            }
//                        }
                    }else{
                        JOptionPane.showMessageDialog(null, "Something went wrong. Try again later.");
                    }
                    
                    
                }else if(message.type.equals("message")){
                    String sender = message.sender;
                    String text = message.content;
                    String chatroomname = message.recipient;
                    for(int i=0;i<openedChatroom.size();i++){
                        ChatWindow obj = openedChatroom.get(i);
                        if(obj.chat_window_name.equals(chatroomname)){
                            if(username.equals(sender)){
                                obj.addmessage("You::"+text);
                            }else{
                                obj.addmessage(sender+":::"+text);
                                
                            }
                            break;
                        }
                        
                    }
                    
                    
                }else if(message.type.equals("leave")){
                    String nameOfChatroom = message.recipient;
                    for(int i=0;i<openedChatroom.size();i++){
                        ChatWindow tobj = (ChatWindow)openedChatroom.get(i);
                        if(tobj.chat_window_name.equals(nameOfChatroom)){
                            if(message.content.equals(username)){
                                tobj.dispose();
                                openedChatroom.remove(tobj);
                            }else{
                                tobj.addmessage(message.content+" left chatroom");
                                tobj.updateUserListOnLeave(message.content);
                            }
                            
                            break;
                        }
                    }
                    getChatLists(chtrm, new Message("getChatroomList", username, "getlist", "server"));
                }else if(message.type.equals("logout")){
                    
                    if(message.content.equals("true")){
                        userSignedIn=false;
                        chtrm.dispose();
                        chtrm=null;
                        String[] args = {};
                        ClientMain.main(args);
                        
                    }
                }else if(message.type.equals("getChatroomList")){
                    System.out.println("got chatroomlist from server");
                    chatroomnames = message.dataList;
                    numberOfClientInGroup =  new ArrayList<>();
                    for(int m=0;m<chatroomnames.size();m++){
                        numberOfClientInGroup.add(0);
                    }
                    chtrm.chatroomNameList(chatroomnames,numberOfClientInGroup);
                }else if(message.type.equals("join")){
                    
                    if(message.content.equals(username)){
                        //open the chatwindow
                        System.out.println("Add chat window for this user because he is joining this chatroom for the first time.");
                        ChatWindow chtWindow = new ChatWindow();
                        chtWindow.chat_window_name = message.recipient;
                        chtWindow.customSetup(this,username);
                        chtWindow.setVisible(true);
                        openedChatroom.add(chtWindow);
                        chtWindow.addmessage("You joined");
                        send(new Message("chat_user_list", username, message.recipient, "server"));
                        
                    }else{
                        //notify all users.
                        for(int i=0;i<openedChatroom.size();i++){
                            ChatWindow obj = openedChatroom.get(i);
                            if(obj.chat_window_name.equals(message.recipient)){
                                obj.addmessage(message.content+" joined");
                                obj.updateUserListOnJoin(message.content);
                                break;
                            }
                            
                        }
                    }
                    getChatLists(chtrm, new Message("getChatroomList", username, "getlist", "server"));
                }else if(message.type.equals("chat_user_list")){
                    System.out.println("List of users "+message.dataList);
                    for(int i=0;i<openedChatroom.size();i++){
                        ChatWindow obj = openedChatroom.get(i);
                        if(obj.chat_window_name.equals(message.recipient)){
                            obj.update(message.dataList);
                            break;
                        }
                        
                    }
                }
                
            }catch(Exception e){
                System.err.println("Exception in chatroom_client:run method:"+e);
            }
        }
    }
    public void sendMessageToLocalServer(String messageText,String chatRoomName) throws UnknownHostException{
        InetAddress addr = InetAddress.getLocalHost();
        clientTemp.sendMessageToLocalServer(new Message("tempMessage", username, messageText, chatRoomName, clientIp, clientSideServerIp));
    }
    public void joinGroup(String groupName) throws UnknownHostException{
        boolean x=true;
        for(int i=0;i<openedChatroom.size();i++){
            ChatWindow tobj = openedChatroom.get(i);
            System.out.println(tobj.chat_window_name);
            if(tobj.chat_window_name.equals(groupName)){
                System.err.println("it's already opened:");
                x=false;
                break;
            }
        }
        if(x){
            int indexOfChatroom = chatroomnames.indexOf(groupName);
            System.err.println("index of chatroom:"+indexOfChatroom+"Number of users in that group"+numberOfClientInGroup.get(indexOfChatroom));
            int numberOfClientInChatroom = numberOfClientInGroup.get(indexOfChatroom)+1;
            numberOfClientInGroup.set(indexOfChatroom, numberOfClientInChatroom);
            
            chtrm.chatroomNameList(chatroomnames, numberOfClientInGroup);
            ChatWindow chtWindow = new ChatWindow();
            chtWindow.chat_window_name = groupName;
            chtWindow.customSetup(this,username);
            chtWindow.setVisible(true);
            openedChatroom.add(chtWindow);
            chtWindow.addmessage("You joined");
            if(clientSideServerIp==null){
                ArrayList<String> nameOfUser = new ArrayList<String>();
                nameOfUser.add("You");
                chtWindow.update(nameOfUser);
            }else{
                ArrayList<String> chatroomListNameWithNum = new ArrayList<String>();
                for(int i=0;i<chatroomnames.size();i++){
                    String tempObj = chatroomnames.get(i)+"("+numberOfClientInGroup.get(i)+")";
                    chatroomListNameWithNum.add(tempObj);
                }
                
                clientTemp.sendMessageToLocalServer(new Message("joinGroup", username, "groupNameWithNum", "localServer", chatroomListNameWithNum, clientIp, clientSideServerIp));
            }
            ArrayList<String> userName = new ArrayList<String>();
            userName.add(username);
            if(clientTemp!=null){
                clientTemp.sendMessageToLocalServer(new Message("userListInGroup", username, "nameList", groupName, userName, clientIp, clientSideServerIp));
            }
            
        }else{
            JOptionPane.showMessageDialog(null, "Chatroom is already opened.");
        }
        
        
    }
    public void signup(Signup frame, Message msg){
        signUpUI=frame;
        send(msg);
    }
    public void signIn(Signin frame, Message msg){
        signInUI=frame;
        send(msg);
    }
    public void logout(){
//        for(int i=0;i<openedChatroom.size();i++){
//            ChatWindow tobj = (ChatWindow)openedChatroom.get(i);
//            send(new Message("leave", username, tobj.chat_window_name, "server"));
//            client.leaveGroup(chat_window_name);
//        }
        send(new Message("logout", username, "turnoff", "server"));
        
    }
    public void getChatLists(chatrooms tchtrm, Message msg){
        chtrm = tchtrm;
        send(msg);
    }
    public void joinChatRoom(chatrooms tchtrm, Message msg){
        boolean x=true;
        for(int i=0;i<openedChatroom.size();i++){
            ChatWindow tobj = openedChatroom.get(i);
            System.out.println(tobj.chat_window_name);
            if(tobj.chat_window_name.equals(msg.content)){
                System.err.println("it's already opened:");
                x=false;
                break;
            }
        }
        if(x){
            chtrm = tchtrm;
            send(msg);
        }else{
            JOptionPane.showMessageDialog(null, "Chatroom is already opened.");
        }
        
    }
    public void leaveGroup(String groupName){
        for(int i=0;i<openedChatroom.size();i++){
            ChatWindow tobj = openedChatroom.get(i);
            System.out.println(tobj.chat_window_name);
            if(tobj.chat_window_name.equals(groupName)){
                
                openedChatroom.remove(tobj);
                tobj.dispose();
                break;
            }
        }
        int indexOfChatroom = chatroomnames.indexOf(groupName);
        System.err.println("index of chatroom:"+indexOfChatroom+"Number of users in that group"+numberOfClientInGroup.get(indexOfChatroom));
        int numberOfClientInChatroom = numberOfClientInGroup.get(indexOfChatroom)-1;
        numberOfClientInGroup.set(indexOfChatroom, numberOfClientInChatroom);
        chtrm.chatroomNameList(chatroomnames, numberOfClientInGroup);
        
        if(clientSideServerIp==null){
//            ArrayList<String> nameOfUser = new ArrayList<String>();
//            nameOfUser.add("You");
//            chtWindow.update(nameOfUser);
        }else{
            ArrayList<String> chatroomListNameWithNum = new ArrayList<String>();
            for(int i=0;i<chatroomnames.size();i++){
                String tempObj = chatroomnames.get(i)+"("+numberOfClientInGroup.get(i)+")";
                chatroomListNameWithNum.add(tempObj);
            }
            clientTemp.sendMessageToLocalServer(new Message("leaveGroup", username, "user left group", groupName, chatroomListNameWithNum, clientIp, clientSideServerIp));
        }
    }
    public void clientSignOut(){
        for(int i=0;i<openedChatroom.size();i++){
            ChatWindow tobj = (ChatWindow)openedChatroom.get(i);
            leaveGroup(tobj.chat_window_name);
        }
        if(clientTemp!=null){
            clientTemp.sendMessageToLocalServer(new Message("tempsignOut", username, "user sign out", "local server", clientIp, clientSideServerIp));
        }else{
            logout();
        }
        
    }
    public void send(Message msg){
        try {
            objOut.writeObject(msg);
            objOut.flush();
            System.out.println("Outgoing : "+msg);
        }catch(Exception e){
            System.out.println("Exception in chatroom_client:send method:"+e);
        }
        
    }
    
}