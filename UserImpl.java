
public class UserImpl implements User{
	private String userName;
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
	}

	private String type;
	private String fName;
	private String sName;
	
	public UserImpl(String userName, String type, String fName, String sName) {
		this.userName = userName;
		this.type = type;
		this.fName = fName;
		this.sName = sName;
	}
}
