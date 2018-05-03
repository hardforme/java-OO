package OO;

import java.util.Scanner;
import java.io.*;
import java.net.*;
import java.sql.*;

public class Server implements Runnable //让服务器变为线程体
{
	Socket s;
	public static Connection dbConn = ConnectToDB() ;
	public Server(Socket socket)
	{
		s = socket;
	}
	public static void main(String[] args) 
	{
		  int MaxClientNum = 5;
	      try {
	         ServerSocket server = new ServerSocket(8887);
	         System.out.println("启动服务器....");
	         for(int i = 0;i<MaxClientNum; i++)//每个线程监听一个客户请求
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
		//ObjectOutputStream oos= new ObjectOutputStream(s.getOutputStream());
        Thread t1 = new Thread(new GetCmsg(s,dbConn)); //接受客户端信息的线程并调用创建SendSmsg线程回复
        //Thread t2 = new Thread(new SendSmsg(s));//发送服务器信息的线程
        t1.start();
        //t2.start();
		}catch (Exception e)
		{
			System.out.println("Error at server:"+e);
		}
	}
	
	public static Connection ConnectToDB() //连接到SQLserver数据库
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
		        return dbConn;
		}catch(Exception e)  
		{  
		    e.printStackTrace();  
		    System.out.print("SQL Server连接失败！");  
		    return null;
		} 
	}
}

class GetCmsg implements Runnable //接受客户端信息
{
	Cmsg message;
	Socket s;
	Connection con;
	public GetCmsg(Socket socket,Connection dbConn)//传入已建立的socket 和数据库
	{
		s = socket;
		con = dbConn;
	}
	public void run()
	{
		try{
				 Statement stmt = con.createStatement();
				 ResultSet rs;
				 String sqlcmd;
				 ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
				 ObjectOutputStream oos= new ObjectOutputStream(s.getOutputStream());
				 Smsg servermsg;
				 Thread t;
				 while((message = (Cmsg)ois.readObject()).msg_type != '0') //0代表用户退出
				 {
					 switch(message.msg_type) 
					 {		
					 	case '1'://1代表登陆
							 sqlcmd = String.format("select OPassWord from OOUser where OID = '%s'", message.id);
							 rs = stmt.executeQuery(sqlcmd);
							 rs.next();
							 String pwd = rs.getString("OPassWord");
							 if(pwd.equals(message.pwd)) 
							 {
								 servermsg = new Smsg('1',"登陆成功");
								 System.out.println("Client " +s.getInetAddress().getLocalHost()+ ":登陆成功");
								 t = new Thread(new SendSmsg(s,servermsg,oos));
								 t.start();
							 }
							 else 
							{
								 servermsg = new Smsg('1',"账号不存在或密码错误");
								 System.out.println("Client " +s.getInetAddress().getLocalHost()+ ":账号不存在或密码错误");
								 t = new Thread(new SendSmsg(s,servermsg,oos));
								 t.start();
							}
							 break;
					 	case '2'://2代表注册
					 		sqlcmd = "select OID from OOUser where OID = "+message.id;
					 		rs = stmt.executeQuery(sqlcmd);
							if(rs.next() == true) 
								{
								 servermsg = new Smsg('2',"账号已存在");
								 System.out.println("Client " +s.getInetAddress().getLocalHost()+ ":账号已存在");
								 t = new Thread(new SendSmsg(s,servermsg,oos));
								 t.start();
								}
							else 
								{
									sqlcmd = String.format("INSERT INTO OOUser VALUES('%s','%s','%s','%s',%d,'%s')",message.id,message.pwd,message.nickname,message.truename,message.age,message.sex);
									int upresult = stmt.executeUpdate(sqlcmd);	
									if(upresult == 1)
										{
										servermsg = new Smsg('2',"注册成功");
										System.out.println("Client " +s.getInetAddress().getLocalHost()+ ":注册成功！");
										 t = new Thread(new SendSmsg(s,servermsg,oos));
										 t.start();
										}
									else 
										{
										servermsg = new Smsg('2',"注册失败");
										System.out.println("Client " +s.getInetAddress().getLocalHost()+ ":注册失败！");
										 t = new Thread(new SendSmsg(s,servermsg,oos));
										 t.start();
										}
								}
					 		break;
					 	case '3'://3表示发送消息
					 		
					 }	
		         }
				 stmt.close();
			}catch(Exception e)
			{
				System.out.println("Error at getcmsg："+e);
			}
	}
}

class SendSmsg implements Runnable //发送服务器信息
{
	Smsg msg;
	Socket s;
	ObjectOutputStream oos;
	public SendSmsg(Socket socket,Smsg servermsg,ObjectOutputStream oos_)
	{
		s = socket;
		msg = servermsg;
		oos = oos_;
	}
	public void run()
	{
		try {
			oos.writeObject(msg);     
		 	oos.flush();
		}catch(Exception e)
		{
			System.out.println("Error at sendmsg："+e);
		}	 
	}
}
