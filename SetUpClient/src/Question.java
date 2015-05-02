import java.util.List;

public class Question {
	private String question;
	private int quizId;
	private int answer;
	private List<String> choices;

	public Question(String question, int answer, List<String> choises) {
		super();
		this.question = question;
		this.answer = answer;
		this.choices = choises;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public int getGameId() {
		return quizId;
	}

	public void setGameId(int gameId) {
		this.quizId = gameId;
	}

	public int getAnswer() {
		return answer;
	}

	public void setAnswer(int answer) {
		this.answer = answer;
	}

	public List<String> getChoises() {
		return choices;
	}

	public void setChoises(List<String> choises) {
		this.choices = choises;
	}
	
	
}
