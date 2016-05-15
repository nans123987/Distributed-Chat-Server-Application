/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatroom;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.lang.*;
import java.sql.ResultSet;
import java.util.Scanner;
import java.util.ArrayList;


/**
 *  Team Members
 *  
 *  Akash Gupta             - 1001122031
 *  Harshitha Gowda         - 1001098221
 *  Meet Brahmbhatt         - 1001119131
 *  Shruthi Shanthaveerappa - 1001106474
 *          
 */
public class DbConnection {
    public Connection con;
    DbConnection(){
        try{
            con = DriverManager.getConnection("jdbc:mysql://localhost/chatroom","root","");
        }catch(Exception e){
            System.out.println("Exception in Dbconnection file");
        }
        
    }
    public boolean Signin (String uname, String pwd){
       try{
            
            Statement stm = con.createStatement();
            pwd = byteArrayToHexString(computeHash(pwd));
            ResultSet rs = stm.executeQuery("select * from Userinfo where user_name ='" + uname + "' and passwor='"+pwd+"'");
            if(rs.last()){
                System.out.println(rs.getString("user_name")+rs.getString("passwor"));
                return true;
            }else{
                return false;
            }
            
        }catch (Exception e){ 
            System.out.println("your got exception"+e);
        }
       return false;
   }

public boolean signup(String uname, String pwd)
{
    try{
            boolean s;
            
            s = check(uname);
            pwd = byteArrayToHexString(computeHash(pwd));
            if (s == false)
            {
                Statement stmt = (Statement) con.createStatement(); 
                String insert = "INSERT INTO UserInfo(user_name,passwor) VALUES('" + uname + "','" + pwd + "')";
                stmt.executeUpdate(insert); 
                return true;
            }
            else { return false; }
            
            
        }catch (Exception e){ System.out.println("your got exception"+e);
                   return false;}
   
}

public boolean check(String uname1)
{        
        try{
                
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("select * from UserInfo where user_name='"+uname1+"'");
            System.out.println("result set:"+rs);
            if(rs.last()){
                System.out.println("data found");
                    return true;
            }else{
                System.out.print("data not found");
                return false;
            }
        }catch (Exception e){ 
            System.out.println("you have got exception"+e+"at dbconnection file");
        }
        return false;
}

public ArrayList<String> getChatroomList(){
    ArrayList<String> arrlist = new ArrayList<String>();
    try{
        Statement stmt = (Statement)con.createStatement();
        String selectString = "Select * from chatroom";
        ResultSet rs = stmt.executeQuery(selectString);
        if(rs !=null){
            while(rs.next()){
                arrlist.add(rs.getString("chtrm_name"));
            }
        }
    }catch(Exception e){
        
    }
    return arrlist;
}
 
 public boolean createChatRooms(String chatroom){
     try{
         
         Statement stmt = (Statement) con.createStatement();
         String insert = "INSERT INTO chatroom (chtrm_name)VALUES('"+chatroom+"')";
         stmt.executeUpdate(insert);
         return true;
        }catch(Exception e){
            System.out.println("create chat rooms exception" +e);
            return false;
        }
 }
 public static byte[] computeHash(String x)   
  throws Exception  
  {
     java.security.MessageDigest d =null;
     d = java.security.MessageDigest.getInstance("SHA-1");
     d.reset();
     d.update(x.getBytes());
     return  d.digest();
  }
  
  public static String byteArrayToHexString(byte[] b){
     StringBuffer sb = new StringBuffer(b.length * 2);
     for (int i = 0; i < b.length; i++){
       int v = b[i] & 0xff;
       if (v < 16) {
         sb.append('0');
       }
       sb.append(Integer.toHexString(v));
     }
     return sb.toString().toUpperCase();
  }
} 

