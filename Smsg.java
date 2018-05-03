package OO;
import java.io.Serializable;
public class Smsg implements Serializable
{
	private static final long serialVersionUID = 2;
	public char msg_type;
	String sss = "111";
	public String msg;
	public Smsg(char msg_type,String msg)
	{
		this.msg_type = msg_type;
		this.msg = msg;
	}
}

