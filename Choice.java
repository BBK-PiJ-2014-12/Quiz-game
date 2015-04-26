
public class Choice {
	private int id;
	private String choice;
	private int qId;
	
	public Choice(int id, String choice, int qId) {
		super();
		this.id = id;
		this.choice = choice;
		this.qId = qId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getChoice() {
		return choice;
	}

	public void setChoise(String choice) {
		this.choice = choice;
	}

	public int getqId() {
		return qId;
	}

	public void setqId(int qId) {
		this.qId = qId;
	}
	
}
