package OO;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
	//String id;
    public static void main(String[] args) {
 	
    	try {
         Socket s = new Socket("127.0.0.1",8887);
         String tmp;
         Thread t1 = new Thread(new GetSmsg(s));
         t1.start();      
         Thread t2= new Thread(new SendCmsg(s));
         t2.start();
    	} catch(IOException e) {
    		System.out.println("Error at Client:"+e);
    	}
      }
}
    

class GetSmsg implements Runnable
{
	String Smsg;
	Socket s;
	public GetSmsg(Socket ss)
	{
		s = ss;
	}
	public void run()
	{
		try{
		 BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		 while(true)
		 {
            	 Smsg = br.readLine();
            	 System.out.println("Server response:" + Smsg);
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
	public SendCmsg(Socket ss)
	{
		s = ss;
	}
	public void run()
	{
		try {
			 	ObjectOutputStream oos= new ObjectOutputStream(s.getOutputStream());
		    	Scanner scan = new Scanner(System.in);
		    	Cmsg msg = null;
			 	String tmp;
			 	while((tmp = scan.nextLine()).charAt(0) != '0')
			 	{	
			 		switch(tmp.charAt(0)) 
			 			{
			 			case '1':
			 				msg = new Cmsg('1',tmp.split("\\s+")[1],tmp.split("\\s+")[2]); 
			 				break;
			 			case '2':
			 				msg = new Cmsg('2',tmp.split("\\s+")[1],tmp.split("\\s+")[2],tmp.split("\\s+")[3],tmp.split("\\s+")[4],tmp.split("\\s+")[5],Integer.parseInt(tmp.split("\\s+")[6]));
			 				break;
			 			}
			 		//msg = new Cmsg(tmp.charAt(0),tmp.substring(1)); 
			 		oos.writeObject(msg);     
			 		oos.flush();
			 	}			
				oos.close();
	         }catch(Exception e){
	        	 System.out.println("Error at sendcmsg："+e);
	        	 }	 
	}
}
