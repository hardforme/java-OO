package OO;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
 	
    	try {
         Socket s = new Socket("127.0.0.1",8887);
         ObjectOutputStream oos= new ObjectOutputStream(s.getOutputStream());
         Scanner scan = new Scanner(System.in);
         String tmp;
         Cmsg clientmsg = null;
         
         Thread t1 = new Thread(new GetSmsg(s));
         t1.start();  
         
         Thread t2;
         while((tmp = scan.nextLine()).charAt(0) != '0')
			 	{	
			 		switch(tmp.charAt(0)) 
			 			{
			 			case '1':
			 				clientmsg = new Cmsg('1',tmp.split("\\s+")[1],tmp.split("\\s+")[2]); 
			 				break;
			 			case '2':
			 				clientmsg = new Cmsg('2',tmp.split("\\s+")[1],tmp.split("\\s+")[2],tmp.split("\\s+")[3],tmp.split("\\s+")[4],tmp.split("\\s+")[5],Integer.parseInt(tmp.split("\\s+")[6]));
			 				break;
			 			}
			 		t2= new Thread(new SendCmsg(s,clientmsg,oos));
			        t2.start();
			 	}
         oos.close();
         scan.close();
    	} catch(IOException e) {
    		System.out.println("Error at Client:"+e);
    	}
      }
}
    

class GetSmsg implements Runnable
{
	Smsg message;
	Socket s;
	public GetSmsg(Socket ss)
	{
		s = ss;
	}
	public void run()
	{
		try{
			ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
		 while(true)
		 {
            	 message = (Smsg)ois.readObject();
            	 System.out.println("Server response:" + message.msg);
         }
		}catch(Exception e)
		{
			System.out.println("Error get smsg："+e);
		}
	}
}

class SendCmsg implements Runnable
{
	public static Socket s;
	public Cmsg msg;
	public ObjectOutputStream oos;
	public SendCmsg(Socket ss,Cmsg clientmsg,ObjectOutputStream oos_)
	{
		s = ss;
		msg = clientmsg;
		oos = oos_;
	}
	public void run()
	{
		try {		    	
			 	oos.writeObject(msg);     
			 	oos.flush();		
	         }catch(Exception e){
	        	 System.out.println("Error at sendcmsg："+e);
	        	 }	 
	}
}
