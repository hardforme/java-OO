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
        Thread t1 = new Thread(new GetCmsg(s,dbConn)); //接受客户端信息的线程
        Thread t2 = new Thread(new SendSmsg(s));//发送服务器信息的线程
        t1.start();
        t2.start();
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

class Cmsg implements Serializable //序列化客户端信息，使socket可以传输对象
{
	public char msg_type;
	public String msg;
	public String id;
	public String pwd;
	public String nickname;
	public String truename;
	public String sex;
	public int age;
	public Cmsg(char msg_type, String msg)
	{
		this.msg_type = msg_type;
		this.msg = msg;
	}
	public Cmsg(char msg_type, String id,String pwd)
	{
		this.msg_type = msg_type;
		this.id = id;
		this.pwd = pwd;
	}
	public Cmsg(char msg_type, String id,String pwd,String nickname,String truename,String sex,int age)
	{
		this.msg_type = msg_type;
		this.id = id;
		this.pwd = pwd;
		this.nickname = nickname;
		this.truename = truename;
		this.sex = sex;
		this.age = age;
	}
	public void ShowCmsg()
	{
		System.out.printf("msg_type:%c msg:%s id:%s	pwd:%s nickname:%s truename:%s sex:%s age:%d\n",msg_type,msg,id,pwd,nickname,truename,sex,age);
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
				 while((message = (Cmsg)ois.readObject()).msg_type != '0') //0代表用户退出
				 {
					 switch(message.msg_type) 
					 {		
					 	case '1'://1代表登陆
							 sqlcmd = String.format("select OPassWord from OOUser where OID = '%s'", message.id);
							 rs = stmt.executeQuery(sqlcmd);
							 rs.next();
							 String pwd = rs.getString("OPassWord");
							 if(pwd.equals(message.pwd)) System.out.println("Client " +s.getInetAddress().getLocalHost()+ ":登陆成功");
							 else System.out.println("Client " +s.getInetAddress().getLocalHost()+ ":账号不存在或密码错误");
							 break;
					 	case '2'://2代表注册
					 		//2 1111 1234 meixin yanz 男 21
					 		sqlcmd = "select OID from OOUser where OID = "+message.id;
					 		rs = stmt.executeQuery(sqlcmd);
							if(rs.next() == true) System.out.println("账号已存在");
							else 
								{
									sqlcmd = String.format("INSERT INTO OOUser VALUES('%s','%s','%s','%s',%d,'%s')",message.id,message.pwd,message.nickname,message.truename,message.age,message.sex);
									int upresult = stmt.executeUpdate(sqlcmd);	
									if(upresult == 1) System.out.println("注册成功！");
									else System.out.println("注册失败！");
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
