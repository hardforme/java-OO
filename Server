package server;

import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.sql.*;

public class Server implements Runnable
{
	Socket s;
	public Server(Socket socket)
	{
		s = socket;
	}
	public static void main(String[] args) 
	{
		  int MaxClientNum = 5;
		 // ConnectToDB();
	      try {
	         ServerSocket server = new ServerSocket(8887);
	         System.out.println("启动服务器....");
	         for(int i = 0;i<MaxClientNum; i++)
	         {
	        	 Socket socket = server.accept();
	        	 Thread t = new Thread(new Server(socket));
	        	 t.start();
	         }
	      } catch (IOException e) {
	         e.printStackTrace();
	      }
	}
	
	public void run()
	{		
		try {
		System.out.println("客户端:"+s.getInetAddress().getLocalHost()+"已连接到服务器");
        Thread t1 = new Thread(new GetCmsg(s));
        Thread t2 = new Thread(new SendSmsg(s));
        t1.start();
        t2.start();
		}catch (Exception e)
		{
			System.out.println("Error at server:"+e);
		}
	}
	
	public static void ConnectToDB()
	{
		String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=OO";
		String userName="sa";  
		String userPwd="1wslzqs"; 	
		try {
			Class.forName(driverName);  
		    System.out.println("加载驱动成功！");  
		}catch(Exception e) {
			e.printStackTrace();  
		    System.out.println("加载驱动失败！"); 
		}
		
		try{  
		    Connection dbConn=DriverManager.getConnection(dbURL,userName,userPwd);  
		        System.out.println("连接数据库成功！");  
		}catch(Exception e)  
		{  
		    e.printStackTrace();  
		    System.out.print("SQL Server连接失败！");  
		} 
	}
}

class Cmsg implements Serializable
{
	public char msg_type;
	public String msg;
	public String id;
	public String psw;
	public Cmsg(char msg_type, String msg)
	{
		this.msg_type = msg_type;
		this.msg = msg;
	}
	public Cmsg(char msg_type, String id,String psw)
	{
		this.msg_type = msg_type;
		this.id = id;
		this.psw = psw;
	}
}

class GetCmsg implements Runnable
{
	Cmsg message;
	Socket s;
	public GetCmsg(Socket socket)
	{
		s = socket;
	}
	public void run()
	{
		try{
				int n = 1;
				 ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
				 while((message = (Cmsg)ois.readObject()).msg_type != '0')
				 {
					 System.out.println(n);
					 if(message.msg_type == '1')
					 {
						 System.out.println("Client " +s.getInetAddress().getLocalHost()+ ":"+message.id+message.psw);
					 }
					 else 
					 {
		            	 System.out.println("Client " +s.getInetAddress().getLocalHost()+ ":"+message.msg);
					 }
					 n++;
		         }
			}catch(Exception e)
			{
				System.out.println("Error at getcmsg："+e);
			}
	}
}

class SendSmsg implements Runnable
{
	String Smsg;
	Socket s;
	public SendSmsg(Socket socket)
	{
		s = socket;
	}
	public void run()
	{
		try {
			 PrintWriter out = new PrintWriter(s.getOutputStream());
			 Scanner scan = new Scanner(System.in);
			 while(true)
			 {
				 Smsg = scan.nextLine();
				 out.println(Smsg);
	             out.flush();
	         }
		}catch(Exception e)
		{
			System.out.println("Error at sendmsg："+e);
		}	 
	}
}
