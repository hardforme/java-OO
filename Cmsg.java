package OO;
import java.io.Serializable;
public class Cmsg implements Serializable
{
	private static final long serialVersionUID = 1;
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
